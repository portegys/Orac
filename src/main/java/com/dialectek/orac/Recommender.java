package com.dialectek.orac;

import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;

/** Recommender. */
public class Recommender
{
   public static final int K_USERS = 3;

   /** Users. */
   public Vector<User> users;

   /** User attributes. */
   public Instances attributes;

   /** Contents. */
   public Vector<Content> contents;

   /** kNN search. */
   private LinearNNSearch kNNsearch;

   /**
    * Constructor.
    */
   public Recommender(Vector<User> users, Instances attributes, Vector<Content> contents)
   {
      this.users      = users;
      this.attributes = attributes;
      this.contents   = contents;
      kNNsearch       = new LinearNNSearch(attributes);
      kNNsearch.setSkipIdentical(true);
   }


   /**
    * Recommend contents for user.
    */
   public Vector<UserContent> recommend(int userID, int maxContents)
   {
      int         i, j, k, r, s;
      float       rating;
      int         index = 0;
      User        user;
      UserContent usage;

      Vector<UserContent> recommendations = new Vector<UserContent>();
      user = null;
      for (i = users.size(); index < i; index++)
      {
         user = users.get(index);
         if (user.id == userID)
         {
            break;
         }
      }
      if (index == users.size())
      {
         return(null);
      }

      // First add pre-recommended content to output.
      for (i = 0, j = user.recommendations.size(); i < j && i < maxContents; i++)
      {
         recommendations.add(user.recommendations.get(i));
      }
      maxContents -= i;
      if (maxContents == 0)
      {
         return(recommendations);
      }

      // Find neighbors.
      try
      {
         Instances neighbors = kNNsearch.kNearestNeighbours(attributes.instance(index), K_USERS);
         System.out.println("Nearest neighbors:");
         System.out.println(neighbors);
         double[] distances = kNNsearch.getDistances();
         System.out.println("Distances:");
         for (i = 0; i < distances.length; i++)
         {
            System.out.println(distances[i] + "");
         }

         // Tally content usage ratings.
         int[] contentIDs = new int[contents.size()];
         int[] counts     = new int[contents.size()];
         float[] ratings  = new float[contents.size()];
         for (i = 0; i < contentIDs.length; i++)
         {
            contentIDs[i] = contents.get(i).id;
            counts[i]     = 0;
            ratings[i]    = 0.0f;
         }
         Vector<User> neighborUsers = getNeighborUsers(neighbors);
         for (i = 0, j = neighborUsers.size(); i < j; i++)
         {
            user = neighborUsers.get(i);
            for (r = 0, s = user.usages.size(); r < s; r++)
            {
               usage = user.usages.get(r);
               if (usage.value != -1.0f)
               {
                  for (k = 0; k < contentIDs.length; k++)
                  {
                     if (contentIDs[k] == usage.id)
                     {
                        counts[k]++;
                        ratings[k] += usage.value;
                        break;
                     }
                  }
               }
            }
         }
         user = users.get(index);
         for (r = 0, s = user.usages.size(); r < s; r++)
         {
            usage = user.usages.get(r);
            if (usage.value != -1.0f)
            {
               for (k = 0; k < contentIDs.length; k++)
               {
                  if (contentIDs[k] == usage.id)
                  {
                     counts[k]  = 0;
                     ratings[k] = 0.0f;
                     break;
                  }
               }
            }
         }

         // Assemble content recommendations.
         for (i = 0; i < counts.length; i++)
         {
            if (counts[i] > 0)
            {
               ratings[i] /= (float)counts[i];
            }
         }
         for (i = 0; i < maxContents; i++)
         {
            for (j = 0, k = -1, rating = 0.0f; j < counts.length; j++)
            {
               if (counts[j] > 0)
               {
                  if ((k == -1) || (ratings[j] > rating))
                  {
                     rating = ratings[j];
                     k      = j;
                  }
               }
            }
            if (k == -1) { break; }
            recommendations.add(new UserContent(contentIDs[k], rating));
            counts[k]  = 0;
            ratings[k] = 0.0f;
         }
      }
      catch (Exception e) {
         System.err.println("Recommend error: " + e.toString());
      }
      return(recommendations);
   }


   private Vector<User> getNeighborUsers(Instances neighbors)
   {
      int  i, j, p, q, r, s;
      User user;

      Vector<User> neighborUsers = new Vector<User>();
      for (i = 0, j = neighbors.numInstances(); i < j; i++)
      {
         for (p = 0, q = attributes.numInstances(); p < q; p++)
         {
            if (InstancesEqual(neighbors.instance(i), attributes.instance(p)))
            {
               user = users.get(p);
               for (r = 0, s = neighborUsers.size(); r < s; r++)
               {
                  if (neighborUsers.get(r) == user) { break; }
               }
               if (r == s)
               {
                  neighborUsers.add(user);
               }
            }
         }
      }
      return(neighborUsers);
   }


   private boolean InstancesEqual(Instance instance1, Instance instance2)
   {
      if (instance1.numAttributes() != instance2.numAttributes())
      {
         return(false);
      }
      for (int i = 0, j = instance1.numAttributes(); i < j; i++)
      {
         if (instance1.value(i) != instance2.value(i))
         {
            return(false);
         }
      }
      return(true);
   }
}
