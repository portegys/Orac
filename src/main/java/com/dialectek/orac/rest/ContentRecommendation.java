package com.dialectek.orac.rest;

// Content recommendation.
public class ContentRecommendation
{
   // User ID.
   public int userID;

   // Content ID.
   public int contentID;

   // Data.
   public int[] data;

   // Constructors.
   public ContentRecommendation()
   {
      userID    = -1;
      contentID = -1;
      data      = new int[0];
   }


   public ContentRecommendation(int userID, int contentID, int[] data)
   {
      this.userID    = userID;
      this.contentID = contentID;
      this.data      = data;
   }
}
