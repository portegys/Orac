package com.dialectek.orac;

// User content.
public class UserContent
{
   // Content ID.
   public int id;

   // Value.
   public float value;

   // Data.
   public int[] data;

   // Constructors.
   public UserContent()
   {
      id    = -1;
      value = 0.0f;
      data  = new int[0];
   }


   public UserContent(int id, float value)
   {
      this.id    = id;
      this.value = value;
      data       = new int[0];
   }


   public UserContent(int id, int[] data)
   {
      this.id   = id;
      value     = 0.0f;
      this.data = data;
   }
}
