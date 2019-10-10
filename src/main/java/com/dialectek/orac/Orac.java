/**
 * Orac recommendation system.
 *
 * @author     Tom Portegys (portegys@gmail.com)
 */

package com.dialectek.orac;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class Orac
{
   // Maximum users.
   public static final int MAX_USERS = 1000000;

   // Maximum string size.
   public static final int MAX_STRING_LENGTH = 100;

   // File store.
   public static final String ORAC_FILE = "orac.dat";

   // Users.
   public TreeMap<String, User> users;

   // Constructor.
   public Orac(TreeMap<String, User> users)
   {
      this.users = users;
   }


   // Add user.
   public synchronized boolean add_user(String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name) && (users.size() >= MAX_USERS)) { return(false); }
      users.put(user_name, new User(user_name));
      return(true);
   }


   // Delete user.
   public synchronized boolean delete_user(String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      for (Map.Entry<String, User> entry : users.entrySet())
      {
         User u = entry.getValue();
         u.delete_friend_all(user_name);
      }
      users.remove(user_name);
      return(true);
   }


   // Update user friends.
   public synchronized boolean update_friends(String user_name, int maxFriends)
   {
      return(update_friends(User.DEFAULT_CATEGORY_NAME, user_name, maxFriends));
   }


   // Update user categorized friends.
   public synchronized boolean update_friends(String category, String user_name, int maxFriends)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      u.delete_friends(category);
      u.friends.put(category, recommend_friends(category, user_name, maxFriends));
      return(true);
   }


   // Update user friends in all current categories.
   public synchronized boolean update_friends_all(String user_name, int maxFriends)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User user = users.get(user_name);
      for (Map.Entry < String, TreeMap < String, Float >> entry : user.friends.entrySet())
      {
         String category = entry.getKey();
         update_friends(category, user_name, maxFriends);
      }
      return(true);
   }


   // Recommend new friends for user.
   public synchronized TreeMap<String, Float> recommend_friends(String user_name, int maxFriends)
   {
      return(recommend_friends(User.DEFAULT_CATEGORY_NAME, user_name, maxFriends));
   }


   // Recommend new categorized friends for user.
   public synchronized TreeMap<String, Float> recommend_friends(String category, String user_name, int maxFriends)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(new TreeMap<String, Float>()); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(new TreeMap<String, Float>()); }
      if (!users.containsKey(user_name)) { return(new TreeMap<String, Float>()); }
      Recommender recommender = new Recommender(users);
      if (maxFriends > User.MAX_FRIENDS_PER_CATEGORY) { maxFriends = User.MAX_FRIENDS_PER_CATEGORY; }
      return(recommender.recommend_friends(category, user_name, maxFriends));
   }


   // Add friend.
   public synchronized boolean add_friend(String user_name, String friend_name, float distance)
   {
      return(add_friend(User.DEFAULT_CATEGORY_NAME, user_name, friend_name, distance));
   }


   // Add categorized friend.
   public synchronized boolean add_friend(String category, String user_name, String friend_name, float distance)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((friend_name == null) || (friend_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      if (!users.containsKey(friend_name)) { return(false); }
      if (user_name.equals(friend_name)) { return(false); }
      User u = users.get(user_name);
      return(u.add_friend(category, friend_name, distance));
   }


   // Add friend category.
   public synchronized boolean add_friend_category(String category, String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      return(u.add_friend_category(category));
   }


   // Delete friend.
   public synchronized boolean delete_friend(String user_name, String friend_name)
   {
      return(delete_friend(User.DEFAULT_CATEGORY_NAME, user_name, friend_name));
   }


   // Delete categorized friend.
   public synchronized boolean delete_friend(String category, String user_name, String friend_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((friend_name == null) || (friend_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      if (!users.containsKey(friend_name)) { return(false); }
      if (user_name.equals(friend_name)) { return(false); }
      User u = users.get(user_name);
      u.delete_friend(category, friend_name);
      return(true);
   }


   // Delete friends.
   public synchronized boolean delete_friends(String user_name)
   {
      return(delete_friends(User.DEFAULT_CATEGORY_NAME, user_name));
   }


   // Delete all friends in category.
   public synchronized boolean delete_friends(String category, String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      u.delete_friends(category);
      return(true);
   }


   // Delete friend category.
   public synchronized boolean delete_friend_category(String category, String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      u.delete_friend_category(category);
      return(true);
   }


   // Clear all friends.
   public synchronized boolean clear_friends(String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      u.clear_friends();
      return(true);
   }


   // Add resource rating.
   public synchronized boolean add_rating(String user_name, String resource_name, float rating)
   {
      return(add_rating(User.DEFAULT_CATEGORY_NAME, user_name, resource_name, rating));
   }


   // Add categorized resource rating.
   public synchronized boolean add_rating(String category, String user_name, String resource_name, float rating)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((resource_name == null) || (resource_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      return(u.add_rating(category, resource_name, rating));
   }


   // Add rating category.
   public synchronized boolean add_rating_category(String category, String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      return(u.add_rating_category(category));
   }


   // Delete resource rating.
   public synchronized boolean delete_rating(String user_name, String resource_name)
   {
      return(delete_rating(User.DEFAULT_CATEGORY_NAME, user_name, resource_name));
   }


   // Delete categorized resource rating.
   public synchronized boolean delete_rating(String category, String user_name, String resource_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((resource_name == null) || (resource_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      u.delete_rating(category, resource_name);
      return(true);
   }


   // Delete resource ratings.
   public synchronized boolean delete_ratings(String user_name)
   {
      return(delete_ratings(User.DEFAULT_CATEGORY_NAME, user_name));
   }


   // Delete categorized resource ratings.
   public synchronized boolean delete_ratings(String category, String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      u.delete_ratings(category);
      return(true);
   }


   // Delete resource rating category.
   public synchronized boolean delete_rating_category(String category, String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      u.delete_rating_category(category);
      return(true);
   }


   // Clear all resource ratings.
   public synchronized boolean clear_ratings(String user_name)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(false); }
      if (!users.containsKey(user_name)) { return(false); }
      User u = users.get(user_name);
      u.clear_ratings();
      return(true);
   }


   // Recommend new resources for user using friends' ratings.
   public synchronized TreeMap<String, Float> recommend_resources(String user_name, int maxResources)
   {
      return(recommend_resources(User.DEFAULT_CATEGORY_NAME, user_name, maxResources));
   }


   // Recommend new resources in category for user using friends' ratings.
   public synchronized TreeMap<String, Float> recommend_resources(String category, String user_name, int maxResources)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(new TreeMap<String, Float>()); }
      if ((category == null) || (category.length() > MAX_STRING_LENGTH)) { return(new TreeMap<String, Float>()); }
      if (!users.containsKey(user_name)) { return(new TreeMap<String, Float>()); }
      if (!users.get(user_name).friends.containsKey(category)) { return(new TreeMap<String, Float>()); }

      // Build list of resources that are not yet rated by user.
      TreeMap<Float, String> ratings = new TreeMap<Float, String>(Collections.reverseOrder());
      User user = users.get(user_name);
      TreeMap<String, Float> user_ratings = user.ratings.get(category);
      for (Map.Entry<String, Float> entry : user.friends.get(category).entrySet())
      {
         User friend = users.get(entry.getKey());
         TreeMap<String, Float> friend_ratings = friend.ratings.get(category);
         if (friend_ratings != null)
         {
            for (Map.Entry<String, Float> elem : friend_ratings.entrySet())
            {
               String resource = elem.getKey();
               if ((user_ratings == null) || !user_ratings.containsKey(resource))
               {
                  Float value = elem.getValue();
                  ratings.put(value, resource);
               }
            }
         }
      }

      // Return highest rated resources.
      TreeMap<String, Float> recommendations = new TreeMap<String, Float>();
      if (maxResources > User.MAX_RATINGS_PER_CATEGORY) { maxResources = User.MAX_RATINGS_PER_CATEGORY; }
      int count = 0;
      for (Map.Entry<Float, String> entry : ratings.entrySet())
      {
         if (count == maxResources) { break; }
         String resource = entry.getValue();
         if (!recommendations.containsKey(resource))
         {
            recommendations.put(resource, entry.getKey());
            count++;
         }
      }
      return(recommendations);
   }


   // Recommend new resource rating categories based on friends' ratings.
   // Returns TreeMap<order, Vector<category name, category count> in descending order of category count.
   public synchronized TreeMap < Integer, Vector < Object >> recommend_categories(String user_name, int maxCategories)
   {
      if ((user_name == null) || (user_name.length() > MAX_STRING_LENGTH)) { return(new TreeMap < Integer, Vector < Object >> ()); }
      if (!users.containsKey(user_name)) { return(new TreeMap < Integer, Vector < Object >> ()); }
      User user = users.get(user_name);
      TreeMap<String, Integer> category_counts = new TreeMap<String, Integer>();
      for (Map.Entry < String, TreeMap < String, Float >> entry : user.friends.entrySet())
      {
         String category = entry.getKey();
         for (Map.Entry<String, Float> elem : user.friends.get(category).entrySet())
         {
            String friend_name = elem.getKey();
            User   friend      = users.get(friend_name);
            for (Map.Entry < String, TreeMap < String, Float >> friend_entry : friend.ratings.entrySet())
            {
               String friend_category = friend_entry.getKey();
               if (!user.ratings.containsKey(friend_category))
               {
                  if (category_counts.containsKey(friend_category))
                  {
                     category_counts.put(friend_category, category_counts.get(friend_category) + 1);
                  }
                  else
                  {
                     category_counts.put(friend_category, 1);
                  }
               }
            }
         }
      }

      // Order by category frequency.
      TreeMap < Integer, Vector < Object >> ordered_categories = new TreeMap < Integer, Vector < Object >> ();
      int order_num = 1;
      for (int i = 0; i < maxCategories && category_counts.size() > 0; i++)
      {
         String max_resource_name = "";
         int    max_count         = -1;
         for (Map.Entry<String, Integer> entry : category_counts.entrySet())
         {
            String resource_name = entry.getKey();
            int    count         = entry.getValue();
            if ((max_count == -1) || (count > max_count))
            {
               max_resource_name = resource_name;
               max_count         = count;
            }
         }
         if (max_count != -1)
         {
            category_counts.remove(max_resource_name);
            Vector<Object> category_count = new Vector<Object>();
            category_count.add((Object)max_resource_name);
            category_count.add((Object) new Integer(max_count));
            ordered_categories.put(order_num, category_count);
            order_num++;
         }
      }
      return(ordered_categories);
   }

   // Clear.
   public synchronized void clear()
   {
      users.clear();
   }


   // Save.
   public synchronized boolean save()
   {
      DataOutputStream writer = null;

      try
      {
         writer = new DataOutputStream(new FileOutputStream(ORAC_FILE));
         writer.writeInt(users.size());
         for (Map.Entry<String, User> entry : users.entrySet())
         {
            User u = entry.getValue();
            u.save(writer);
         }
      }
      catch (Exception e)
      {
         return(false);
      }
      finally
      {
         if (writer != null)
         {
            try
            {
               writer.close();
            }
            catch (Exception e) {}
         }
      }
      return(true);
   }


   // Load.
   public synchronized boolean load()
   {
      DataInputStream reader = null;

      try
      {
         reader = new DataInputStream(new FileInputStream(ORAC_FILE));
         users.clear();
         int n = reader.readInt();
         for (int i = 0; i < n; i++)
         {
            User u = User.load(reader);
            users.put(u.name, u);
         }
      }
      catch (Exception e)
      {
         return(false);
      }
      finally
      {
         if (reader != null)
         {
            try
            {
               reader.close();
            }
            catch (Exception e) {}
         }
      }
      return(true);
   }


   // Main.
   public static void main(String[] args) throws Exception
   {
      // Test categories.
      System.out.println("Testing default category");

      // Create users.
      TreeMap<String, User> users = new TreeMap<String, User>();
      String                user1 = "User 1";
      users.put(user1, new User(user1));
      String user2 = "User 2";
      users.put(user2, new User(user2));
      String user3 = "User 3";
      users.put(user3, new User(user3));

      // Create resources.
      String resource1 = "Resource 1";
      String resource2 = "Resource 2";

      // Create Orac.
      Orac orac = new Orac(users);

      // Rate resources.
      orac.add_rating(user1, resource1, 1.0f);
      orac.add_rating(user2, resource1, 2.0f);
      orac.add_rating(user2, resource2, 2.0f);
      orac.add_rating(user3, resource2, 3.0f);

      // Add recommended friend.
      orac.update_friends(user1, 1);

      // Validate.
      System.out.println(orac.users.get(user1).toString());
      System.out.println(orac.users.get(user2).toString());
      System.out.println(orac.users.get(user3).toString());

      // Recommend resources.
      System.out.println("Resource recommendations for " + user1 + ":");
      TreeMap<String, Float> recommendations = orac.recommend_resources(user1, 3);
      for (Map.Entry<String, Float> entry : recommendations.entrySet())
      {
         String resource = entry.getKey();
         Float  value    = entry.getValue();
         System.out.println("Resource name=" + resource + ", rating=" + value);
      }

      // Test categories.
      System.out.println("Testing categories");

      // Create categories.
      String category1 = "people";
      String category2 = "places";

      orac.clear();

      users.put(user1, new User(user1));
      users.put(user2, new User(user2));
      users.put(user3, new User(user3));

      // Rate resources.
      orac.add_rating(category1, user1, resource1, 1.0f);
      orac.add_rating(user2, resource2, 3.0f);
      orac.add_rating(category2, user2, resource2, 5.0f);
      orac.add_rating(category1, user3, resource1, 2.0f);
      orac.add_rating(category1, user3, resource2, 2.0f);

      // Validate before.
      System.out.println("Users before:");
      System.out.println(orac.users.get(user1).toString());
      System.out.println(orac.users.get(user2).toString());
      System.out.println(orac.users.get(user3).toString());

      // Add recommended friends.
      orac.update_friends(category1, user1, 3);

      // Validate.
      System.out.println("Users after:");
      System.out.println(orac.users.get(user1).toString());
      System.out.println(orac.users.get(user2).toString());
      System.out.println(orac.users.get(user3).toString());

      // Recommend resources.
      System.out.println("Resource recommendations for " + user1 + ":");
      recommendations = orac.recommend_resources(category1, user1, 3);
      for (Map.Entry<String, Float> entry : recommendations.entrySet())
      {
         String resource = entry.getKey();
         Float  value    = entry.getValue();
         System.out.println("Resource name=" + resource + ", rating=" + value);
      }

      TreeMap < Integer, Vector < Object >> categories = orac.recommend_categories(user1, 2);
      System.out.println(categories.toString());
   }
}
