package com.dialectek.orac;

import java.util.HashMap;
import java.util.Map;

// Content.
public class Content
{
   // Identity.
   public int id;

   // Properties.
   public Map<String, String> properties;

   // Constructors.
   public Content()
   {
      id         = -1;
      properties = new HashMap<String, String>();
   }


   public Content(int id, Map<String, String> properties)
   {
      this.id         = id;
      this.properties = properties;
   }


   @Override
   public String toString()
   {
      return(new StringBuffer(" ID : ").append(this.id).toString());
   }
}
