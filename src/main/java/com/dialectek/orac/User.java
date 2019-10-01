// User.

package com.dialectek.orac;

import java.util.TreeMap;
import java.util.TreeSet;

public class User
{
   public String                 name;
   public String                 description;
   public TreeMap<String, Float> ratings;
   public TreeSet<String>        friends;

   // Constructors.
   public User()
   {
      name        = null;
      description = "None";
      ratings     = new TreeMap<String, Float>();
      friends     = new TreeSet<String>();
   }


   public User(String name)
   {
      this.name   = name;
      description = "None";
      ratings     = new TreeMap<String, Float>();
      friends     = new TreeSet<String>();
   }


   // Befriend user.
   public void befriend(String user)
   {
      friends.add(user);
   }


   // Unfriend user.
   public void unfriend(String user)
   {
      friends.remove(user);
   }


   // Unfriend all.
   public void unfriendAll()
   {
      friends.clear();
   }


   // Rate resource.
   public void rateResource(String resource, float rating)
   {
      ratings.put(resource, rating);
   }


   @Override
   public String toString()
   {
      return(" name : " + name + " description : " + description);
   }
}
