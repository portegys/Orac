// Orac REST interface.

package com.dialectek.orac.rest;

import com.dialectek.orac.User;
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


   // Update friends for user.
   @GET
   @Path("/update_friends/{user_name}/{max_friends}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response update_friends(@PathParam ("user_name") String   user_name,
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
            if (orac.update_friends(user_name, maxFriends))
            {
               return(Response.status(200).build());
            }
            else
            {
               return(Response.status(400).entity("error updating friends").build());
            }
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Update categorized friends for user.
   @GET
   @Path("/update_categorized_friends/{category}/{user_name}/{max_friends}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response update_categorized_friends(@PathParam ("category") String    category,
                                              @PathParam ("user_name") String   user_name,
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
            if (orac.update_friends(category, user_name, maxFriends))
            {
               return(Response.status(200).build());
            }
            else
            {
               return(Response.status(400).entity("error updating friends").build());
            }
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Update friends in all current categories.
   @GET
   @Path("/update_friends_all/{user_name}/{max_friends}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response update_friends_all(@PathParam ("user_name") String   user_name,
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
            if (orac.update_friends_all(user_name, maxFriends))
            {
               return(Response.status(200).build());
            }
            else
            {
               return(Response.status(400).entity("error updating friends").build());
            }
         }
         else
         {
            return(Response.status(404).build());
         }
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


   // Recommend new categorized friends for user.
   @GET
   @Path("/recommend_categorized_friends/{category}/{user_name}/{max_friends}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response recommend_categorized_friends(@PathParam ("category") String    category,
                                                 @PathParam ("user_name") String   user_name,
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
            TreeMap<String, Float> friends = orac.recommend_friends(category, user_name, maxFriends);
            return(Response.status(200).entity(friends.toString()).build());
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
            return(Response.status(200).entity(user.friends.entrySet().toString()).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Add friend.
   @GET
   @Path("/add_friend/{user_name}/{friend_name}/{distance}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_friend(@PathParam ("user_name") String   user_name,
                              @PathParam ("friend_name") String friend_name,
                              @PathParam ("distance") String    distance)
   {
      synchronized (lock)
      {
         User user   = users.get(user_name);
         User friend = users.get(friend_name);
         if ((user != null) && (friend != null))
         {
            float d = 0.0f;
            try
            {
               d = Float.parseFloat(distance);
            }
            catch (NumberFormatException e)
            {
               return(Response.status(400).entity("invalid distance parameter").build());
            }
            if (d < 0.0f)
            {
               return(Response.status(400).entity("invalid distance parameter").build());
            }
            if (orac.add_friend(user_name, friend_name, d))
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


   // Add categorized friend.
   @GET
   @Path("/add_categorized_friend/{category}/{user_name}/{friend_name}/{distance}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_categorized_friend(@PathParam ("category") String    category,
                                          @PathParam ("user_name") String   user_name,
                                          @PathParam ("friend_name") String friend_name,
                                          @PathParam ("distance") String    distance)
   {
      synchronized (lock)
      {
         User user   = users.get(user_name);
         User friend = users.get(friend_name);
         if ((user != null) && (friend != null))
         {
            float d = 0.0f;
            try
            {
               d = Float.parseFloat(distance);
            }
            catch (NumberFormatException e)
            {
               return(Response.status(400).entity("invalid distance parameter").build());
            }
            if (d < 0.0f)
            {
               return(Response.status(400).entity("invalid distance parameter").build());
            }
            if (orac.add_friend(category, user_name, friend_name, d))
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


   // Add friend category.
   @GET
   @Path("/add_friend_category/{category}/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_friend_category(@PathParam ("category") String  category,
                                       @PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.add_friend_category(category, user_name))
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


   // Delete categorized friend.
   @GET
   @Path("/delete_categorized_friend/{category}/{user_name}/{friend_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_categorized_friend(@PathParam ("category") String    category,
                                             @PathParam ("user_name") String   user_name,
                                             @PathParam ("friend_name") String friend_name)
   {
      synchronized (lock)
      {
         User user   = users.get(user_name);
         User friend = users.get(friend_name);
         if ((user != null) && (friend != null))
         {
            if (orac.delete_friend(category, user_name, friend_name))
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


   // Delete friends.
   @GET
   @Path("/delete_friends/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_friends(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.delete_friends(user_name))
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


   // Delete all friends in category.
   @GET
   @Path("/delete_categorized_friends/{category}/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_categorized_friends(@PathParam ("category") String  category,
                                              @PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.delete_friends(category, user_name))
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


   // Delete friend category.
   @GET
   @Path("/delete_friend_category/{category}/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_friend_category(@PathParam ("category") String  category,
                                          @PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.delete_friend_category(category, user_name))
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
   @Path("/add_rating/{user_name}/{resource_name}/{rating}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_rating(@PathParam ("user_name") String     user_name,
                              @PathParam ("resource_name") String resource_name,
                              @PathParam ("rating") String        rating)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            float r = 0.0f;
            try
            {
               r = Float.parseFloat(rating);
            }
            catch (NumberFormatException e)
            {
               return(Response.status(400).entity("invalid rating parameter").build());
            }
            if ((r < User.MIN_RATING) || (r > User.MAX_RATING))
            {
               return(Response.status(400).entity("invalid rating parameter").build());
            }
            if (orac.add_rating(user_name, resource_name, r))
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


   // Add categorized resource rating.
   @GET
   @Path("/add_categorized_rating/{category}/{user_name}/{resource_name}/{rating}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_categorized_rating(@PathParam ("category") String      category,
                                          @PathParam ("user_name") String     user_name,
                                          @PathParam ("resource_name") String resource_name,
                                          @PathParam ("rating") String        rating)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            float r = 0.0f;
            try
            {
               r = Float.parseFloat(rating);
            }
            catch (NumberFormatException e)
            {
               return(Response.status(400).entity("invalid rating parameter").build());
            }
            if ((r < User.MIN_RATING) || (r > User.MAX_RATING))
            {
               return(Response.status(400).entity("invalid rating parameter").build());
            }
            if (orac.add_rating(category, user_name, resource_name, r))
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


   // Add rating category.
   @GET
   @Path("/add_rating_category/{category}/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response add_rating_category(@PathParam ("category") String  category,
                                       @PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.add_rating_category(category, user_name))
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


   // Delete rating.
   @GET
   @Path("/delete_rating/{user_name}/{resource_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_rating(@PathParam ("user_name") String     user_name,
                                 @PathParam ("resource_name") String resource_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.delete_rating(user_name, resource_name))
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


   // Delete categorized rating.
   @GET
   @Path("/delete_categorized_rating/{category}/{user_name}/{resource_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_categorized_rating(@PathParam ("category") String      category,
                                             @PathParam ("user_name") String     user_name,
                                             @PathParam ("resource_name") String resource_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.delete_rating(category, user_name, resource_name))
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


   // Delete ratings.
   @GET
   @Path("/delete_ratings/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_ratings(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.delete_ratings(user_name))
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


   // Delete all ratings in category.
   @GET
   @Path("/delete_categorized_ratings/{category}/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_categorized_ratings(@PathParam ("category") String  category,
                                              @PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.delete_ratings(category, user_name))
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


   // Delete rating category.
   @GET
   @Path("/delete_rating_category/{category}/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response delete_rating_category(@PathParam ("category") String  category,
                                          @PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            if (orac.delete_rating_category(category, user_name))
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


   // Clear ratings.
   @GET
   @Path("/clear_ratings/{user_name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response clear_ratings(@PathParam ("user_name") String user_name)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);

         if (user != null)
         {
            if (orac.clear_ratings(user_name))
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


   // Recommend new resources for user using friends' ratings.
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


   // Recommend new resources in category for user using friends' ratings.
   @GET
   @Path("/recommend_categorized_resources/{category}/{user_name}/{max_resources}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response recommend_categorized_resources(@PathParam ("category") String      category,
                                                   @PathParam ("user_name") String     user_name,
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
            TreeMap<String, Float> resourceList = orac.recommend_resources(category, user_name, maxResources);
            return(Response.status(200).entity(resourceList.toString()).build());
         }
         else
         {
            return(Response.status(404).build());
         }
      }
   }


   // Recommend new resource categories for user.
   @GET
   @Path("/recommend_categories/{user_name}/{max_categories}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response recommend_categories(@PathParam ("user_name") String      user_name,
                                        @PathParam ("max_categories") String max_categories)
   {
      synchronized (lock)
      {
         User user = users.get(user_name);
         if (user != null)
         {
            int maxCategories = 0;
            try
            {
               maxCategories = Integer.parseInt(max_categories);
            }
            catch (NumberFormatException e)
            {
               return(Response.status(400).entity("invalid max_categories parameter").build());
            }
            if (maxCategories < 0)
            {
               return(Response.status(400).entity("invalid max_categories parameter").build());
            }
            TreeMap < Integer, Vector < Object >> categoryList = orac.recommend_categories(user_name, maxCategories);
            return(Response.status(200).entity(categoryList.entrySet().toString()).build());
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
