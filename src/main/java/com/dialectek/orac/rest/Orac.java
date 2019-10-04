// Orac REST interface.

package com.dialectek.orac.rest;

import com.dialectek.orac.User;
import com.dialectek.orac.Resource;

import java.util.TreeMap;
import java.util.Vector;

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

   // Resources.
   public static TreeMap<String, Resource> resources;

   // Orac recommender.
   public static com.dialectek.orac.Orac orac;

   // File store.
   public static final String ORAC_FILE = "orac.dat";

   // Synchronization.
   private static Object lock;

   // Initialize.
   static
   {
      users     = new TreeMap<String, User>();
      resources = new TreeMap<String, Resource>();
      orac      = new com.dialectek.orac.Orac(users, resources);
      orac.load(ORAC_FILE);
      lock = new Object();
   }

   // Get user.
   @GET
   @Path("/get_user/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getUser(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);

         if (user != null)
         {
            String output = user.toString();
            output += " ratings : " + user.ratings.entrySet().toString();
            output += " friends : " + user.friends.toString();
            return(Response.status(200).entity(output).build());
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
   public Response getUsers()
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
   public Response addUser(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         orac.addUser(user_name);
         return(Response.status(200).build());
      }
   }


   // Add user with description.
   @GET
   @Path("/add_described_user/{user_name}/{description}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response addUser(@PathParam ("user_name") String   user_name,
                           @PathParam ("description") String description)
   {
      synchronized (lock)
      {
         orac.addUser(user_name);
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
   public Response deleteUser(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         orac.removeUser(user_name);
         return(Response.status(200).build());
      }
   }


   // Get resource.
   @GET
   @Path("/get_resource/{resource_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getResource(@PathParam ("resource_name") String resource_name)
   {
      synchronized (lock)
      {
         Resource resource = resources.get(resource_name);
         if (resource != null)
         {
            String output = resource.toString();
            return(Response.status(200).entity(output).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Get all resources.
   @GET
   @Path("/get_resources")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getResources()
   {
      synchronized (lock)
      {
         String output = resources.keySet().toString();
         return(Response.status(200).entity(output).build());
      }
   }


   // Add resource.
   @GET
   @Path("/add_resource/{resource_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response addResource(@PathParam ("resource_name") String resource_name)
   {
      synchronized (lock)
      {
         resources.put(resource_name, new Resource(resource_name));
         return(Response.status(200).build());
      }
   }


   // Add resource with description.
   @GET
   @Path("/add_described_resource/{resource_name}/{description}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response addResource(@PathParam ("resource_name") String resource_name,
                               @PathParam ("description") String   description)
   {
      synchronized (lock)
      {
         orac.addResource(resource_name);
         Resource resource = resources.get(resource_name);
         resource.description = description;
         resources.put(resource_name, resource);
         return(Response.status(200).build());
      }
   }


   // Delete resource.
   @GET
   @Path("/delete_resource/{resource_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response deleteResource(@PathParam ("resource_name") String resource_name)
   {
      synchronized (lock)
      {
         orac.removeResource(resource_name);
         return(Response.status(200).build());
      }
   }


   // Recommend new friends for user.
   @GET
   @Path("/recommend_friends/{user_name}/{max_friends}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response recommendFriends(@PathParam ("user_name") String   user_name,
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
            Vector<String> friends = orac.recommendFriends(user_name, maxFriends);
            String         output  = friends.toString();
            return(Response.status(200).entity(output).build());
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
   public Response recommendResources(@PathParam ("user_name") String     user_name,
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
            Vector<String> resourceList = orac.recommendResources(user_name, maxResources);
            String         output       = resourceList.toString();
            return(Response.status(200).entity(output).build());
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
   public Response getFriends(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            String output = user.friends.toString();
            return(Response.status(200).entity(output).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Befriend user.
   @GET
   @Path("/befriend_user/{user_name}/{friend_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response befriendUser(@PathParam ("user_name") String   user_name,
                                @PathParam ("friend_name") String friend_name)
   {
      synchronized (lock)
      {
         User user   = users.get(user_name);
         User friend = users.get(friend_name);
         if ((user != null) && (friend != null))
         {
            if (orac.befriendUser(user_name, friend_name))
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


   // Unfriend user.
   @GET
   @Path("/unfriend_user/{user_name}/{friend_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response unfriendUser(@PathParam ("user_name") String   user_name,
                                @PathParam ("friend_name") String friend_name)
   {
      synchronized (lock)
      {
         User user   = users.get(user_name);
         User friend = users.get(friend_name);
         if ((user != null) && (friend != null))
         {
            if (orac.unfriendUser(user_name, friend_name))
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


   // Unfriend all.
   @GET
   @Path("/unfriend_all/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response unfriendAll(@PathParam ("name") String name)
   {
      synchronized (lock)
      {
         User user = users.get(name);

         if (user != null)
         {
            if (orac.unfriendAll(name))
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


   // Rate resource.
   @GET
   @Path("/rate_resource/{user_name}/{resource_name}/{resource_rating}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response rateResource(@PathParam ("user_name") String       user_name,
                                @PathParam ("resource_name") String   resource_name,
                                @PathParam ("resource_rating") String resource_rating)
   {
      synchronized (lock)
      {
         User     user     = users.get(user_name);
         Resource resource = resources.get(resource_name);

         if ((user != null) && (resource != null))
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
            if (orac.rateResource(user_name, resource_name, rating))
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
