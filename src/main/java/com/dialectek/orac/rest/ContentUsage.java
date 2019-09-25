package com.dialectek.orac.rest;

// Content usage.
public class ContentUsage
{
   // User ID.
   public int userID;

   // Content ID.
   public int contentID;

   // Rating.
   public float rating;

   // Constructors.
   public ContentUsage()
   {
      userID    = -1;
      contentID = -1;
      rating    = 0.0f;
   }


   public ContentUsage(int userID, int contentID, float rating)
   {
      this.userID    = userID;
      this.contentID = contentID;
      this.rating    = rating;
   }
}
