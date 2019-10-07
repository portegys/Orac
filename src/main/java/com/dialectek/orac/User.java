// User.

package com.dialectek.orac;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class User
{
   // Default category name.
   public static final String DEFAULT_CATEGORY_NAME = "default";

   // Rating parameters.
   public static final int   MAX_RATING_CATEGORIES    = 1000;
   public static final int   MAX_RATINGS_PER_CATEGORY = 10000;
   public static final float MIN_RATING = 0.0f;
   public static final float MAX_RATING = 100.0f;

   // Friend parameters.
   public static final int MAX_FRIEND_CATEGORIES    = 1000;
   public static final int MAX_FRIENDS_PER_CATEGORY = 10000;

   // Name.
   public String name;

   // Description.
   public String description;

   // Friends.
   // TreeMap<category_name, TreeMap<friend_name, distance>>
   public TreeMap < String, TreeMap < String, Float >> friends;

   // Resource ratings:
   // TreeMap<category_name, TreeMap<resource_name, rating>>
   public TreeMap < String, TreeMap < String, Float >> ratings;

   // Constructors.
   public User()
   {
      name        = "none";
      description = "none";
      friends     = new TreeMap < String, TreeMap < String, Float >> ();
      friends.put(DEFAULT_CATEGORY_NAME, new TreeMap<String, Float>());
      ratings = new TreeMap < String, TreeMap < String, Float >> ();
      ratings.put(DEFAULT_CATEGORY_NAME, new TreeMap<String, Float>());
   }


   public User(String name)
   {
      this.name   = name;
      description = "none";
      friends     = new TreeMap < String, TreeMap < String, Float >> ();
      friends.put(DEFAULT_CATEGORY_NAME, new TreeMap<String, Float>());
      ratings = new TreeMap < String, TreeMap < String, Float >> ();
      ratings.put(DEFAULT_CATEGORY_NAME, new TreeMap<String, Float>());
   }


   // Add friend.
   public boolean add_friend(String friend, float distance)
   {
      return(add_friend(DEFAULT_CATEGORY_NAME, friend, distance));
   }


   // Add categorized friend.
   public boolean add_friend(String category, String friend, float distance)
   {
      // Can add to category?
      TreeMap<String, Float> categorizedFriends = friends.get(category);
      if ((categorizedFriends == null) && (friends.size() >= MAX_FRIEND_CATEGORIES))
      {
         return(false);
      }
      if (categorizedFriends == null)
      {
         friends.put(category, new TreeMap<String, Float>());
         categorizedFriends = friends.get(category);
      }
      if (!categorizedFriends.containsKey(friend) && (categorizedFriends.size() >= MAX_FRIENDS_PER_CATEGORY))
      {
         return(false);
      }

      // Add friend.
      categorizedFriends.put(friend, distance);
      return(true);
   }


   // Add friend category.
   public boolean add_friend_category(String category)
   {
      if (friends.containsKey(category))
      {
         return(true);
      }
      if (friends.size() < MAX_FRIEND_CATEGORIES)
      {
         friends.put(category, new TreeMap<String, Float>());
         return(true);
      }
      else
      {
         return(false);
      }
   }


   // Delete friend.
   public void delete_friend(String friend)
   {
      delete_friend(DEFAULT_CATEGORY_NAME, friend);
   }


   // Delete categorized friend.
   public void delete_friend(String category, String friend)
   {
      TreeMap<String, Float> categorizedFriends = friends.get(category);
      if (categorizedFriends != null)
      {
         categorizedFriends.remove(friend);
      }
   }


   // Delete friend from all categories.
   public void delete_friend_all(String friend)
   {
      for (Map.Entry < String, TreeMap < String, Float >> category : friends.entrySet())
      {
         TreeMap<String, Float> elem = category.getValue();
         elem.remove(friend);
      }
   }


   // Delete friends.
   public void delete_friends()
   {
      delete_friends(DEFAULT_CATEGORY_NAME);
   }


   // Delete friends in category.
   public void delete_friends(String category)
   {
      TreeMap<String, Float> categorizedFriends = friends.get(category);
      if (categorizedFriends != null)
      {
         categorizedFriends.clear();
      }
   }


   // Delete friend category.
   public void delete_friend_category(String category)
   {
      if (!category.equals(DEFAULT_CATEGORY_NAME))
      {
         friends.remove(category);
      }
   }


   // Clear all friends.
   public void clear_friends()
   {
      friends.clear();
      friends.put(DEFAULT_CATEGORY_NAME, new TreeMap<String, Float>());
   }


   // Add resource rating.
   public boolean add_rating(String resource, float rating)
   {
      return(add_rating(DEFAULT_CATEGORY_NAME, resource, rating));
   }


   // Add categorized resource rating.
   public boolean add_rating(String category, String resource, float rating)
   {
      // Can add to category?
      TreeMap<String, Float> categorizedRatings = ratings.get(category);
      if ((categorizedRatings == null) && (ratings.size() >= MAX_RATING_CATEGORIES))
      {
         return(false);
      }
      if (categorizedRatings == null)
      {
         ratings.put(category, new TreeMap<String, Float>());
         categorizedRatings = ratings.get(category);
      }
      if (!categorizedRatings.containsKey(resource) && (categorizedRatings.size() >= MAX_RATINGS_PER_CATEGORY))
      {
         return(false);
      }

      // Add rating.
      categorizedRatings.put(resource, rating);
      return(true);
   }


   // Add rating category.
   public boolean add_rating_category(String category)
   {
      if (ratings.containsKey(category))
      {
         return(true);
      }
      if (ratings.size() < MAX_RATING_CATEGORIES)
      {
         ratings.put(category, new TreeMap<String, Float>());
         return(true);
      }
      else
      {
         return(false);
      }
   }


   // Delete rating.
   public void delete_rating(String resource)
   {
      delete_rating(DEFAULT_CATEGORY_NAME, resource);
   }


   // Delete categorized rating.
   public void delete_rating(String category, String resource)
   {
      TreeMap<String, Float> categorizedRatings = ratings.get(category);
      if (categorizedRatings != null)
      {
         categorizedRatings.remove(resource);
      }
   }


   // Delete rating from all categories.
   public void delete_rating_all(String resource)
   {
      for (Map.Entry < String, TreeMap < String, Float >> category : ratings.entrySet())
      {
         TreeMap<String, Float> elem = category.getValue();
         elem.remove(resource);
      }
   }


   // Delete categorized ratings.
   public void delete_ratings(String category)
   {
      TreeMap<String, Float> categorizedRatings = ratings.get(category);
      if (categorizedRatings != null)
      {
         categorizedRatings.clear();
      }
   }


   // Delete rating category.
   public void delete_rating_category(String category)
   {
      if (!category.equals(DEFAULT_CATEGORY_NAME))
      {
         ratings.remove(category);
      }
   }


   // Clear all ratings.
   public void clear_ratings()
   {
      ratings.clear();
      ratings.put(DEFAULT_CATEGORY_NAME, new TreeMap<String, Float>());
   }


   // Save.
   public void save(DataOutputStream writer) throws IOException
   {
      writer.writeUTF(name);
      writer.writeUTF(description);
      writer.writeInt(ratings.size());
      for (Map.Entry < String, TreeMap < String, Float >> entry : ratings.entrySet())
      {
         String category = entry.getKey();
         writer.writeUTF(category);
         TreeMap<String, Float> categorizedRatings = ratings.get(category);
         writer.writeInt(categorizedRatings.size());
         for (Map.Entry<String, Float> categorizedEntry : categorizedRatings.entrySet())
         {
            String resource = categorizedEntry.getKey();
            writer.writeUTF(resource);
            float rating = categorizedEntry.getValue();
            writer.writeFloat(rating);
         }
      }
      writer.writeInt(friends.size());
      for (Map.Entry < String, TreeMap < String, Float >> entry : friends.entrySet())
      {
         String category = entry.getKey();
         writer.writeUTF(category);
         TreeMap<String, Float> categorizedFriends = friends.get(category);
         writer.writeInt(categorizedFriends.size());
         for (Map.Entry<String, Float> categorizedEntry : categorizedFriends.entrySet())
         {
            String friend = categorizedEntry.getKey();
            writer.writeUTF(friend);
            float distance = categorizedEntry.getValue();
            writer.writeFloat(distance);
         }
      }
      writer.flush();
   }


   // Load.
   public static User load(DataInputStream reader) throws IOException
   {
      try
      {
         String name = reader.readUTF();
         User   user = new User(name);
         user.description = reader.readUTF();
         int n = reader.readInt();
         for (int i = 0; i < n; i++)
         {
            String category = reader.readUTF();
            user.ratings.put(category, new TreeMap<String, Float>());
            TreeMap<String, Float> categorizedRatings = user.ratings.get(category);
            int n2 = reader.readInt();
            for (int j = 0; j < n2; j++)
            {
               String resource = reader.readUTF();
               float  rating   = reader.readFloat();
               categorizedRatings.put(resource, rating);
            }
         }
         n = reader.readInt();
         for (int i = 0; i < n; i++)
         {
            String category = reader.readUTF();
            user.friends.put(category, new TreeMap<String, Float>());
            TreeMap<String, Float> categorizedFriends = user.friends.get(category);
            int n2 = reader.readInt();
            for (int j = 0; j < n2; j++)
            {
               String friend   = reader.readUTF();
               float  distance = reader.readFloat();
               categorizedFriends.put(friend, distance);
            }
         }
         return(user);
      }
      catch (EOFException e) {
         return(null);
      }
   }


   @Override
   public String toString()
   {
      //return ReflectionToStringBuilder.toString(this);
      //return new ReflectionToStringBuilder(this, new RecursiveToStringStyle()).toString();
      String output = "name : " + name + " description : " + description;

      output += " friends : " + friends.entrySet().toString();
      output += " ratings : " + ratings.entrySet().toString();
      return(output);
   }
}
