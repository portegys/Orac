// Recommender.

package com.dialectek.orac;

import java.util.Map;
import java.util.TreeMap;
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

   /**
    * Constructor.
    */
   public Recommender(TreeMap<String, User> users)
   {
      this.users = users;
   }


   /**
    * Recommend nearest neighbors as new friends based on similar resource ratings.
    */
   public TreeMap<String, Float> recommend_friends(String user_name, int maxFriends)
   {
      return(recommend_friends(User.DEFAULT_CATEGORY_NAME, user_name, maxFriends));
   }


   /**
    * Recommend new categorized friends for user.
    */
   public TreeMap<String, Float> recommend_friends(String category, String user_name, int maxFriends)
   {
      // Default ratings are the average rating over all users.
      TreeMap<String, Float>   defaultRatings      = new TreeMap<String, Float>();
      TreeMap<String, Integer> defaultRatingCounts = new TreeMap<String, Integer>();
      for (Map.Entry<String, User> elem : users.entrySet())
      {
         User               u = elem.getValue();
         Map<String, Float> categorizedRatings = u.ratings.get(category);
         if (categorizedRatings != null)
         {
            for (Map.Entry<String, Float> rating : categorizedRatings.entrySet())
            {
               String resource = rating.getKey();
               float  value    = rating.getValue();
               if (defaultRatings.containsKey(resource))
               {
                  defaultRatings.put(resource, defaultRatings.get(resource) + value);
                  defaultRatingCounts.put(resource, defaultRatingCounts.get(resource) + 1);
               }
               else
               {
                  defaultRatings.put(resource, value);
                  defaultRatingCounts.put(resource, 1);
               }
            }
         }
      }
      for (Map.Entry<String, Float> defaultRating : defaultRatings.entrySet())
      {
         String resource = defaultRating.getKey();
         float  value    = defaultRating.getValue();
         float  n        = (float)defaultRatingCounts.get(resource);
         defaultRatings.put(resource, value / n);
      }

      // Create attributes: resource names and column of user names.
      FastVector attributes = new FastVector();
      for (Map.Entry<String, Float> resource : defaultRatings.entrySet())
      {
         attributes.addElement(new Attribute(resource.getKey()));
      }
      FastVector user_names = new FastVector();
      for (Map.Entry<String, User> elem : users.entrySet())
      {
         String name = elem.getKey();
         if (!user_name.equals(name))
         {
            user_names.addElement(name);
         }
      }
      attributes.addElement(new Attribute("user_name", user_names));

      // Create instances of user resource ratings.
      Instances dataset = new Instances("dataset", attributes, 0);
      dataset.setClassIndex(dataset.numAttributes() - 1);
      Instance userInstance = null;
      for (Map.Entry<String, User> elem : users.entrySet())
      {
         User u = elem.getValue();
         double[] attrValues = new double[dataset.numAttributes()];
         int a = 0;
         for (Map.Entry<String, Float> resource : defaultRatings.entrySet())
         {
            String                 name    = resource.getKey();
            TreeMap<String, Float> ratings = u.ratings.get(category);
            if ((ratings != null) && ratings.containsKey(name))
            {
               attrValues[a] = ratings.get(name);
            }
            else
            {
               attrValues[a] = resource.getValue();
            }
            a++;
         }
         attrValues[a] = dataset.attribute(a).indexOfValue(u.name);
         if (user_name.equals(u.name))
         {
            userInstance = new Instance(1.0, attrValues);
         }
         else
         {
            dataset.add(new Instance(1.0, attrValues));
         }
      }

      // Find nearest neighbors as recommended friends.
      TreeMap<String, Float> recommendations = null;
      try
      {
         LinearNNSearch knn       = new LinearNNSearch(dataset);
         Instances      neighbors = knn.kNearestNeighbours(userInstance, maxFriends);
         double[] distances = knn.getDistances();
         recommendations    = getNeighbors(neighbors, distances);
      }
      catch (Exception e)
      {
         System.err.println("recommend_friends error: " + e.toString());
      }
      return(recommendations);
   }


   // Get neighbors by name and distance.
   private TreeMap<String, Float> getNeighbors(Instances neighbors, double[] distances)
   {
      TreeMap<String, Float> namedNeighbors = new TreeMap<String, Float>();
      int nameIndex = neighbors.numAttributes() - 1;
      for (int i = 0, j = neighbors.numInstances(); i < j; i++)
      {
         Instance instance = neighbors.instance(i);
         String   name     = instance.stringValue(nameIndex);
         namedNeighbors.put(name, (float)distances[i]);
      }
      return(namedNeighbors);
   }
}
