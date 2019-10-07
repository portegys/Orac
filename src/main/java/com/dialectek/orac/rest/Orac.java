// Orac REST interface.

package com.dialectek.orac.rest;

import com.dialectek.orac.User;
import java.util.TreeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/service")
public class Orac
{
   // Users.
   public static TreeMap<String, User> users;

   // Orac recommender.
   public static com.dialectek.orac.Orac orac;

   // File store.
   public static final String ORAC_FILE = "orac.dat";

   // Synchronization.
   private static Object lock;

   // Initialize.
   static
   {
      users = new TreeMap<String, User>();
      orac  = new com.dialectek.orac.Orac(users);
      orac.load(ORAC_FILE);
      lock = new Object();
   }

   // Get user.
   @GET
   @Path("/get_user/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response get_user(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            return(Response.status(200).entity(user.toString()).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Get all users.
   @GET
   @Path("/get_users")
   @Produces(MediaType.TEXT_PLAIN)
   public Response get_users()
   {
      synchronized (lock)
      {
         String output = users.keySet().toString();
         return(Response.status(200).entity(output).build());
      }
   }


   // Add user.
   @GET
   @Path("/add_user/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_user(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         orac.add_user(user_name);
         return(Response.status(200).build());
      }
   }


   // Add user with description.
   @GET
   @Path("/add_described_user/{user_name}/{description}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_user(@PathParam ("user_name") String   user_name,
                            @PathParam ("description") String description)
   {
      synchronized (lock)
      {
         orac.add_user(user_name);
         User user = users.get(user_name);
         user.description = description;
         users.put(user_name, user);
         return(Response.status(200).build());
      }
   }


   // Delete user.
   @GET
   @Path("/delete_user/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_user(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         orac.delete_user(user_name);
         return(Response.status(200).build());
      }
   }


   // Recommend new friends for user.
   @GET
   @Path("/recommend_friends/{user_name}/{max_friends}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response recommend_friends(@PathParam ("user_name") String   user_name,
                                     @PathParam ("max_friends") String max_friends)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            int maxFriends = 0;
            try
            {
               maxFriends = Integer.parseInt(max_friends);
            }
            catch (NumberFormatException e)
            {
               return(Response.status(400).entity("invalid max_friends parameter").build());
            }
            if (maxFriends < 0)
            {
               return(Response.status(400).entity("invalid max_friends parameter").build());
            }
            TreeMap<String, Float> friends = orac.recommend_friends(user_name, maxFriends);
            return(Response.status(200).entity(friends.toString()).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Recommend new resources for user.
   @GET
   @Path("/recommend_resources/{user_name}/{max_resources}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response recommend_resources(@PathParam ("user_name") String     user_name,
                                       @PathParam ("max_resources") String max_resources)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            int maxResources = 0;
            try
            {
               maxResources = Integer.parseInt(max_resources);
            }
            catch (NumberFormatException e)
            {
               return(Response.status(400).entity("invalid max_resources parameter").build());
            }
            if (maxResources < 0)
            {
               return(Response.status(400).entity("invalid max_resources parameter").build());
            }
            TreeMap<String, Float> resourceList = orac.recommend_resources(user_name, maxResources);
            return(Response.status(200).entity(resourceList.toString()).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Get all user friends.
   @GET
   @Path("/get_friends/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response get_friends(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            return(Response.status(200).entity(user.friends.toString()).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Add friend.
   @GET
   @Path("/add_friend/{user_name}/{friend_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_friend(@PathParam ("user_name") String   user_name,
                              @PathParam ("friend_name") String friend_name)
   {
      synchronized (lock)
      {
         User user   = users.get(user_name);
         User friend = users.get(friend_name);
         if ((user != null) && (friend != null))
         {
            if (orac.add_friend(user_name, friend_name, 0.0f))
            {
               return(Response.status(200).build());
            }
            else
            {
               return(Response.status(400).build());
            }
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Delete friend.
   @GET
   @Path("/delete_friend/{user_name}/{friend_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_friend(@PathParam ("user_name") String   user_name,
                                 @PathParam ("friend_name") String friend_name)
   {
      synchronized (lock)
      {
         User user   = users.get(user_name);
         User friend = users.get(friend_name);
         if ((user != null) && (friend != null))
         {
            if (orac.delete_friend(user_name, friend_name))
            {
               return(Response.status(200).build());
            }
            else
            {
               return(Response.status(400).build());
            }
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Clear friends.
   @GET
   @Path("/clear_friends/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response clear_friends(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);

         if (user != null)
         {
            if (orac.clear_friends(user_name))
            {
               return(Response.status(200).build());
            }
            else
            {
               return(Response.status(400).build());
            }
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Add resource rating.
   @GET
   @Path("/add_rating/{user_name}/{resource_name}/{resource_rating}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_rating(@PathParam ("user_name") String       user_name,
                              @PathParam ("resource_name") String   resource_name,
                              @PathParam ("resource_rating") String resource_rating)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);

         if (user != null)
         {
            float rating = 0.0f;
            try
            {
               rating = Float.parseFloat(resource_rating);
            }
            catch (NumberFormatException e)
            {
               return(Response.status(400).entity("invalid resource_rating parameter").build());
            }
            if (orac.add_rating(user_name, resource_name, rating))
            {
               return(Response.status(200).build());
            }
            else
            {
               return(Response.status(400).build());
            }
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Clear.
   @GET
   @Path("/clear")
   @Produces(MediaType.TEXT_PLAIN)
   public Response clear()
   {
      synchronized (lock)
      {
         orac.clear();
         return(Response.status(200).build());
      }
   }


   // Save.
   @GET
   @Path("/save")
   @Produces(MediaType.TEXT_PLAIN)
   public Response save()
   {
      synchronized (lock)
      {
         if (orac.save(ORAC_FILE))
         {
            return(Response.status(200).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }
}
