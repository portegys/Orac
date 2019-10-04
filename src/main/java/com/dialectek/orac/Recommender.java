package com.dialectek.orac;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;

/** Recommender. */
public class Recommender
{
   /** Users. */
   public TreeMap<String, User> users;

   /** Resources. */
   public TreeMap<String, Resource> resources;

   /**
    * Constructor.
    */
   public Recommender(TreeMap<String, User> users, TreeMap<String, Resource> resources)
   {
      this.users     = users;
      this.resources = resources;
   }


   /**
    * Recommend new friends for user.
    */
   public Vector<String> recommendFriends(String user, int maxFriends)
   {
      if (resources.size() == 0) { return(null); }

      // Default ratings are the average rating over all users.
      TreeMap<String, Float>   defaultRatings      = new TreeMap<String, Float>();
      TreeMap<String, Integer> defaultRatingCounts = new TreeMap<String, Integer>();
      for (Map.Entry<String, Resource> resource : resources.entrySet())
      {
         String name = resource.getKey();
         defaultRatings.put(name, 0.0f);
         defaultRatingCounts.put(name, 0);
      }
      for (Map.Entry<String, User> elem : users.entrySet())
      {
         User u = elem.getValue();
         for (Map.Entry<String, Float> rating : u.ratings.entrySet())
         {
            String resource = rating.getKey();
            int    i        = defaultRatingCounts.get(resource);
            i++;
            defaultRatingCounts.put(resource, i);
         }
      }
      for (Map.Entry<String, Float> defaultRating : defaultRatings.entrySet())
      {
         String resource = defaultRating.getKey();
         float  f        = defaultRating.getValue();
         int    i        = defaultRatingCounts.get(resource);
         if (i > 0)
         {
            f /= (float)i;
            defaultRatings.put(resource, f);
         }
      }

      // Create attributes: resource rating and user names.
      User       userObj    = users.get(user);
      FastVector attributes = new FastVector();
      for (Map.Entry<String, Resource> resource : resources.entrySet())
      {
         attributes.addElement(new Attribute(resource.getKey()));
      }
      FastVector userNames = new FastVector();
      for (Map.Entry<String, User> elem : users.entrySet())
      {
         User u = elem.getValue();
         if (!user.equals(u.name) && !userObj.friends.contains(u.name))
         {
            userNames.addElement(u.name);
         }
      }
      attributes.addElement(new Attribute("user_name", userNames));

      // Create instances of user resource ratings.
      Instances dataset = new Instances("dataset", attributes, 0);
      dataset.setClassIndex(dataset.numAttributes() - 1);
      Instance userInstance = null;
      for (Map.Entry<String, User> elem : users.entrySet())
      {
         User u = elem.getValue();
         double[] attrValues = new double[dataset.numAttributes()];
         int a = 0;
         for (Map.Entry<String, Resource> resource : resources.entrySet())
         {
            String name = resource.getKey();
            if (u.ratings.containsKey(name))
            {
               attrValues[a] = u.ratings.get(name);
            }
            else
            {
               attrValues[a] = defaultRatings.get(name);
            }
            a++;
         }
         attrValues[a] = dataset.attribute(a).indexOfValue(u.name);
         if (user.equals(u.name))
         {
            userInstance = new Instance(1.0, attrValues);
         }
         else
         {
            dataset.add(new Instance(1.0, attrValues));
         }
      }

      // Find nearest neighbors as recommended friends.
      Vector<String> recommendations = null;
      try
      {
         LinearNNSearch knn       = new LinearNNSearch(dataset);
         Instances      neighbors = knn.kNearestNeighbours(userInstance, maxFriends);
         recommendations = getNeighborNames(neighbors);
      }
      catch (Exception e) {
         System.err.println("Recommend error: " + e.toString());
      }
      return(recommendations);
   }


   private Vector<String> getNeighborNames(Instances neighbors)
   {
      Vector<String> neighborNames = new Vector<String>();
      int            nameIndex     = neighbors.numAttributes() - 1;
      for (int i = 0, j = neighbors.numInstances(); i < j; i++)
      {
         Instance instance = neighbors.instance(i);
         String   name     = instance.stringValue(nameIndex);
         neighborNames.add(name);
      }
      return(neighborNames);
   }
}
