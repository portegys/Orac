package com.dialectek.orac;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Orac: the Second Life content recommendation engine.
 *
 * @author     Tom Portegys (portegys@gmail.com)
 */

public class Orac
{
   // Users.
   public Vector<User> users;

   // Contents.
   public Vector<Content> contents;

   // Recommender.
   public Recommender recommender;

   // Constructor.
   public Orac(Vector<User> users, Vector<Content> contents)
   {
      this.users    = users;
      this.contents = contents;
      FastVector attributes = new FastVector();
      int        l          = users.get(0).attributes.size();
      for (int i = 0, j = l; i < j; i++)
      {
         attributes.addElement(new Attribute(i + ""));
      }
      Instances userAttributes = new Instances("User instances", attributes, users.size());
      for (int i = 0, j = users.size(); i < j; i++)
      {
         User     user         = users.get(i);
         Instance userInstance = new Instance(l);
         for (int k = 0; k < l; k++)
         {
            userInstance.setValue(k, user.attributes.get(k));
         }
         userAttributes.add(userInstance);
      }
      recommender = new Recommender(users, userAttributes, contents);
   }


   // Recommend content for user.
   public Vector<UserContent> recommendContent(int userID, int maxContent)
   {
      return(recommender.recommend(userID, maxContent));
   }


   // Use content.
   public boolean useContent(int userID, int contentID, float rating)
   {
      for (int i = 0, j = users.size(); i < j; i++)
      {
         User user = users.get(i);
         if (user.id == userID)
         {
            user.useContent(contentID, rating);
            return(true);
         }
      }
      return(false);
   }


   // Main.
   public static void main(String[] args) throws Exception
   {
      Vector<User>  users      = new Vector<User>();
      Vector<Float> attributes = new Vector<Float>();
      attributes.add(1.0f);
      User user = new User(1, attributes);
      users.add(user);
      attributes = new Vector<Float>();
      attributes.add(3.0f);
      user = new User(2, attributes);
      users.add(user);
      attributes = new Vector<Float>();
      attributes.add(1.1f);
      user = new User(3, attributes);
      users.add(user);

      Vector<Content>     contents   = new Vector<Content>();
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("1", "One");
      Content content = new Content(1, properties);
      contents.add(content);
      properties = new HashMap<String, String>();
      properties.put("3", "Three");
      properties.put("4", "Four");
      content = new Content(2, properties);
      contents.add(content);

      Orac orac = new Orac(users, contents);

      orac.useContent(1, 1, 1.0f);
      orac.useContent(2, 1, 2.0f);
      orac.useContent(2, 2, 2.0f);
      orac.useContent(3, 2, 3.0f);
      Vector<UserContent> recommendations = orac.recommendContent(1, 3);
      System.out.println("Content recommendations for user 1:");
      for (int i = 0, j = recommendations.size(); i < j; i++)
      {
         UserContent userContent = recommendations.get(i);
         System.out.println("Content id=" + userContent.id + " rating=" + userContent.value);
      }
   }
}
