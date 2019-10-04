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
   // Users.
   public TreeMap<String, User> users;

   // Resources.
   public TreeMap<String, Resource> resources;

   // Constructor.
   public Orac(TreeMap<String, User> users, TreeMap<String, Resource> resources)
   {
      this.users     = users;
      this.resources = resources;
   }


   // Add user.
   public synchronized boolean addUser(String user)
   {
      if (users.containsKey(user)) { return(false); }
      users.put(user, new User(user));
      return(true);
   }


   // Delete user.
   public synchronized boolean deleteUser(String user)
   {
      if (!users.containsKey(user)) { return(false); }
      users.remove(user);
      for (Map.Entry<String, User> entry : users.entrySet())
      {
         User u = entry.getValue();
         u.friends.remove(user);
      }
      return(true);
   }


   // Add resource.
   public synchronized boolean addResource(String resource)
   {
      if (resources.containsKey(resource)) { return(false); }
      resources.put(resource, new Resource(resource));
      return(true);
   }


   // Delete resource.
   public synchronized boolean deleteResource(String resource)
   {
      if (!resources.containsKey(resource)) { return(false); }
      resources.remove(resource);
      for (Map.Entry<String, User> entry : users.entrySet())
      {
         User u = entry.getValue();
         u.ratings.remove(resource);
      }
      return(true);
   }


   // Recommend new friends for user.
   public synchronized Vector<String> recommendFriends(String user, int maxFriends)
   {
      Recommender recommender = new Recommender(users, resources);

      return(recommender.recommendFriends(user, maxFriends));
   }


   // Recommend new resources for user using friends' ratings.
   public synchronized Vector<String> recommendResources(String user, int maxResources)
   {
      // Build list of resources that are not yet rated by user.
      TreeMap<Float, String> ratings = new TreeMap<Float, String>(Collections.reverseOrder());
      User u = users.get(user);
      for (String friend : u.friends)
      {
         User f = users.get(friend);
         for (Map.Entry<String, Float> entry : f.ratings.entrySet())
         {
            String resource = entry.getKey();
            if (!u.ratings.containsKey(resource))
            {
               Float r = entry.getValue();
               ratings.put(r, resource);
            }
         }
      }

      // Return highest rated resources.
      TreeMap<String, Float> work = new TreeMap<String, Float>();
      int count = 0;
      for (Map.Entry<Float, String> entry : ratings.entrySet())
      {
         if (count == maxResources) { break; }
         String resource = entry.getValue();
         if (!work.containsKey(resource))
         {
            work.put(resource, entry.getKey());
            count++;
         }
      }
      Vector<String> recommendations = new Vector<String>();
      for (Map.Entry<String, Float> entry : work.entrySet())
      {
         recommendations.add(entry.getKey());
      }
      return(recommendations);
   }


   // Befriend user.
   public synchronized boolean befriendUser(String user, String friend)
   {
      User u = users.get(user);

      if (u == null) { return(false); }
      User f = users.get(friend);
      if (f == null) { return(false); }
      if (user.equals(friend)) { return(false); }
      u.befriend(friend);
      return(true);
   }


   // Unfriend user.
   public synchronized boolean unfriendUser(String user, String friend)
   {
      User u = users.get(user);

      if (u == null) { return(false); }
      User f = users.get(friend);
      if (f == null) { return(false); }
      u.unfriend(friend);
      return(true);
   }


   // Unfriend all.
   public synchronized boolean unfriendAll(String user)
   {
      User u = users.get(user);

      if (u == null) { return(false); }
      u.unfriendAll();
      return(true);
   }


   // Rate resource.
   public synchronized boolean rateResource(String user, String resource, float rating)
   {
      User u = users.get(user);

      if (u == null) { return(false); }
      Resource r = resources.get(resource);
      if (r == null) { return(false); }
      u.rateResource(resource, rating);
      return(true);
   }


   // Clear.
   public synchronized void clear()
   {
      users.clear();
      resources.clear();
   }


   // Save.
   public synchronized boolean save(String filename)
   {
      DataOutputStream writer = null;

      try
      {
         writer = new DataOutputStream(new FileOutputStream(filename));
         writer.writeInt(users.size());
         for (Map.Entry<String, User> entry : users.entrySet())
         {
            User u = entry.getValue();
            u.save(writer);
         }
         writer.writeInt(resources.size());
         for (Map.Entry<String, Resource> entry : resources.entrySet())
         {
            Resource r = entry.getValue();
            r.save(writer);
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
   public synchronized boolean load(String filename)
   {
      DataInputStream reader = null;

      try
      {
         reader = new DataInputStream(new FileInputStream(filename));
         users.clear();
         int n = reader.readInt();
         for (int i = 0; i < n; i++)
         {
            User u = User.load(reader);
            users.put(u.name, u);
         }
         resources.clear();
         n = reader.readInt();
         for (int i = 0; i < n; i++)
         {
            Resource r = Resource.load(reader);
            resources.put(r.name, r);
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
      // Create users.
      TreeMap<String, User> users = new TreeMap<String, User>();
      String                user1 = "User 1";
      users.put(user1, new User(user1));
      String user2 = "User 2";
      users.put(user2, new User(user2));
      String user3 = "User 3";
      users.put(user3, new User(user3));

      // Create resources.
      TreeMap<String, Resource> resources = new TreeMap<String, Resource>();
      String resource1 = "Resource 1";
      resources.put(resource1, new Resource(resource1));
      String resource2 = "Resource 2";
      resources.put(resource2, new Resource(resource2));
      Orac orac = new Orac(users, resources);

      // Rate resources.
      orac.rateResource(user1, resource1, 1.0f);
      orac.rateResource(user2, resource1, 2.0f);
      orac.rateResource(user2, resource2, 2.0f);
      orac.rateResource(user3, resource2, 3.0f);

      // Recommend and add friend.
      Vector<String> recommendations = orac.recommendFriends(user1, 1);
      String         friend          = recommendations.get(0);
      System.out.println("Friend recommendation for " + user1 + ": " + friend);
      orac.befriendUser(user1, friend);

      // Recommend resources.
      recommendations = orac.recommendResources(user1, 3);
      System.out.println("Resource recommendations for " + user1 + ":");
      for (int i = 0, j = recommendations.size(); i < j; i++)
      {
         String resource = recommendations.get(i);
         System.out.println("Resource name=" + resource);
      }

      // Validate.
      System.out.println(orac.users.get(user1).toString());
      System.out.println(orac.users.get(user2).toString());
      System.out.println(orac.users.get(user3).toString());
      System.out.println(orac.resources.get(resource1).toString());
      System.out.println(orac.resources.get(resource2).toString());
   }
}
