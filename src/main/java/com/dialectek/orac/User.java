// User.

package com.dialectek.orac;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class User
{
   // Name.
   public String name;

   // Description.
   public String description;

   // Resource ratings.
   public TreeMap<String, Float> ratings;

   // Friends.
   public TreeSet<String> friends;

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


   // Save.
   public void save(DataOutputStream writer) throws IOException
   {
      writer.writeUTF(name);
      writer.writeUTF(description);
      writer.writeInt(ratings.size());
      for (Map.Entry<String, Float> entry : ratings.entrySet())
      {
         String resource = entry.getKey();
         writer.writeUTF(resource);
         float rating = entry.getValue();
         writer.writeFloat(rating);
      }
      writer.writeInt(friends.size());
      for (String friend : friends)
      {
         writer.writeUTF(friend);
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
            String resource = reader.readUTF();
            float  rating   = reader.readFloat();
            user.ratings.put(resource, rating);
         }
         n = reader.readInt();
         for (int i = 0; i < n; i++)
         {
            String friend = reader.readUTF();
            user.friends.add(friend);
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
      return(" name : " + name + " description : " + description);
   }
}
