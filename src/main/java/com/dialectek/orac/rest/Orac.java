// Orac REST interface.

package com.dialectek.orac.rest;

import com.dialectek.orac.User;
import com.dialectek.orac.UserDAO;
import com.dialectek.orac.Content;
import com.dialectek.orac.ContentDAO;
import com.dialectek.orac.UserContent;
import com.dialectek.orac.rest.ContentUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/service")
public class Orac
{
   public int MAX_RECOMMENDED_CONTENTS = 3;

   // Get user by id.
   @GET
   @Path("/get_user/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public User getUser(@PathParam ("id") String id)
   {
      User user = UserDAO.instance.content.get(Integer.parseInt(id));

      return(user);
   }


   // Get a user's complete profile (Rc4).
   @GET
   @Path("/user/profile/get/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public User getUserProfile(@PathParam ("id") String id)
   {
      User user = UserDAO.instance.content.get(Integer.parseInt(id));

      return(user);
   }


   // Get a user's active problem list (Rc5).
   @GET
   @Path("/user/problems/get/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Map<Integer, String> getUserProblems(@PathParam ("id") String id)
   {
      User user = UserDAO.instance.content.get(Integer.parseInt(id));

      if (user != null)
      {
         return(user.problems);
      }
      else
      {
         return(null);
      }
   }


   // Get a user's inactive problem list (Rc6).
   @GET
   @Path("/user/problems/getOld/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Map<Integer, String> getUserProblemsOld(@PathParam ("id") String id)
   {
      User user = UserDAO.instance.content.get(Integer.parseInt(id));

      if (user != null)
      {
         return(user.oldProblems);
      }
      else
      {
         return(null);
      }
   }


   // Get all users.
   @GET
   @Path("/get_users")
   @Produces(MediaType.APPLICATION_JSON)
   public List<User> getUsers()
   {
      List<User> users = new ArrayList<User>();
      users.addAll(UserDAO.instance.content.values());
      return(users);
   }


   // Add a user.
   @POST
   @Path("/add_user")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response addUser(User user)
   {
      UserDAO.instance.content.remove(user.id);
      UserDAO.instance.content.put(user.id, user);
      String output = user.toString();
      return(Response.status(200).entity(output).build());
   }


   // Delete user by id.
   @DELETE
   @Path("/delete_user/{id}")
   public void deleteUser(@PathParam ("id") String id)
   {
      UserDAO.instance.content.remove(Integer.parseInt(id));
   }


   // Get content by id.
   @GET
   @Path("/get_content/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Content getContent(@PathParam ("id") String id)
   {
      Content content = ContentDAO.instance.content.get(Integer.parseInt(id));

      return(content);
   }


   // Get metadata associated with a piece of content (Rc3).
   @GET
   @Path("/content/getMetadata/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Content getContentMetadata(@PathParam ("id") String id)
   {
      Content content = ContentDAO.instance.content.get(Integer.parseInt(id));

      return(content);
   }


   // Get all contents.
   @GET
   @Path("/get_contents")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Content> getContents()
   {
      List<Content> contents = new ArrayList<Content>();
      contents.addAll(ContentDAO.instance.content.values());
      return(contents);
   }


   // Get the catalog of content from the server (Rc2).
   @GET
   @Path("/content/getCatalog")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Content> getContentCatalog()
   {
      List<Content> contents = new ArrayList<Content>();
      contents.addAll(ContentDAO.instance.content.values());
      return(contents);
   }


   // Add content.
   @POST
   @Path("/add_content")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response addContent(Content content)
   {
      ContentDAO.instance.content.remove(content.id);
      ContentDAO.instance.content.put(content.id, content);
      String output = content.toString();
      return(Response.status(200).entity(output).build());
   }


   // Delete content by id.
   @DELETE
   @Path("/delete_content/{id}")
   public void deleteContent(@PathParam ("id") String id)
   {
      ContentDAO.instance.content.remove(Integer.parseInt(id));
   }


   // Use content.
   @POST
   @Path("/use_content")
   @Consumes(MediaType.APPLICATION_JSON)
   public void useContent(ContentUsage usage)
   {
      User user = UserDAO.instance.content.get(usage.userID);

      if (user != null)
      {
         user.useContent(usage.contentID, usage.rating);
      }
   }


   // Add a recommendation for viewing of content for a user (Rc1).
   @POST
   @Path("/user/recommend/create")
   @Consumes(MediaType.APPLICATION_JSON)
   public void createRecommendation(ContentRecommendation recommendation)
   {
      User user = UserDAO.instance.content.get(recommendation.userID);

      if (user != null)
      {
         user.recommendContent(recommendation.contentID, recommendation.data);
      }
   }


   // Recommend content for user.
   @GET
   @Path("/recommend_content/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Vector<UserContent> recommmendContent(@PathParam ("id") String id)
   {
      Vector<User> users = new Vector<User>();
      users.addAll(UserDAO.instance.content.values());
      Vector<Content> contents = new Vector<Content>();
      contents.addAll(ContentDAO.instance.content.values());
      if ((users.size() == 0) || (contents.size() == 0))
      {
         return(null);
      }
      com.dialectek.orac.Orac orac = new com.dialectek.orac.Orac(users, contents);
      return(orac.recommendContent(Integer.parseInt(id), MAX_RECOMMENDED_CONTENTS));
   }
}
