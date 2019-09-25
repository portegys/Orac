package com.dialectek.orac;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class User
{
   public int                  id;
   public String               description;
   public Vector<Float>        attributes;
   public Map<Integer, String> problems;
   public Map<Integer, String> oldProblems;
   public Vector<UserContent>  recommendations;
   public Vector<UserContent>  usages;

   // Constructors.
   public User()
   {
      id              = -1;
      description     = "None";
      attributes      = new Vector<Float>();
      problems        = new HashMap<Integer, String>();
      oldProblems     = new HashMap<Integer, String>();
      recommendations = new Vector<UserContent>();
      usages          = new Vector<UserContent>();
   }


   public User(int id, Vector<Float> attributes)
   {
      this.id         = id;
      description     = "None";
      this.attributes = attributes;
      problems        = new HashMap<Integer, String>();
      oldProblems     = new HashMap<Integer, String>();
      recommendations = new Vector<UserContent>();
      usages          = new Vector<UserContent>();
   }


   // Use and rate content.
   // Remove from recommendations.
   public void useContent(int contentID, float rating)
   {
      int i, j;

      for (i = 0, j = usages.size(); i < j; i++)
      {
         UserContent usage = usages.get(i);
         if (usage.id == contentID)
         {
            usage.value = rating;
            break;
         }
      }
      if (i == j)
      {
         usages.add(new UserContent(contentID, rating));
      }

      // Remove from recommmendations.
      for (i = 0, j = recommendations.size(); i < j; i++)
      {
         UserContent recommendation = recommendations.get(i);
         if (recommendation.id == contentID)
         {
            recommendations.remove(recommendation);
            return;
         }
      }
   }


   // Recommend content.
   public void recommendContent(int contentID, int[] data)
   {
      for (int i = 0, j = recommendations.size(); i < j; i++)
      {
         UserContent recommendation = recommendations.get(i);
         if (recommendation.id == contentID)
         {
            recommendation.data = data;
            return;
         }
      }
      recommendations.add(new UserContent(contentID, data));
   }


   @Override
   public String toString()
   {
      return(new StringBuffer(" ID : ").append(this.id)
                .append(" Description : ").append(this.description)
                .toString());
   }
}
