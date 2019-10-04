// Resource.

package com.dialectek.orac;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class Resource
{
   // Name.
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


   // Save.
   public void save(DataOutputStream writer) throws IOException
   {
      writer.writeUTF(name);
      writer.writeUTF(description);
      writer.flush();
   }


   // Load.
   public static Resource load(DataInputStream reader) throws IOException
   {
      try
      {
         String   name     = reader.readUTF();
         Resource resource = new Resource(name);
         resource.description = reader.readUTF();
         return(resource);
      }
      catch (EOFException e) {
         return(null);
      }
   }


   @Override
   public String toString()
   {
      return("name : " + name + " description : " + description);
   }
}
