// Resource.

package com.dialectek.orac;

public class Resource
{
   // Identity.
   public String name;

   // Description.
   public String description;

   // Constructors.
   public Resource()
   {
      name        = null;
      description = "None";
   }


   public Resource(String name)
   {
      this.name   = name;
      description = "None";
   }


   @Override
   public String toString()
   {
      return(" name : " + name + " description : " + description);
   }
}
