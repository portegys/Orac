// Orac REST client.

package com.dialectek.orac.rest.client;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.util.URIUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class OracClient
{
   public static String REST_ADDRESS = "http://localhost:8080/Orac/rest/service/";
   //public static String REST_ADDRESS = "http://ec2-52-11-225-180.us-west-2.compute.amazonaws.com:8080/Orac/rest/service/";

   public static void main(String[] args)
   {
      if (args.length == 1)
      {
         REST_ADDRESS = args[0];
      }

      try
      {
         ClientConfig clientConfig = new DefaultClientConfig();
         Client       client       = Client.create(clientConfig);

         // Add some users.
         String user1 = URIUtil.encodeQuery("User 1");
         addUser(user1, client);
         String user2 = "User2";
         addUser(user2, client);
         String user3 = "User3";
         addUser(user3, client);
         getUsers(client);

         // Add some resources.
         String resource1 = "Resource1";
         addResource(resource1, client);
         String resource2 = "Resource2";
         addResource(resource2, client);
         getResources(client);

         // Rate resources.
         rateResource(user1, resource1, 1.0f, client);
         rateResource(user2, resource1, 2.0f, client);
         rateResource(user2, resource2, 2.0f, client);
         rateResource(user3, resource2, 3.0f, client);

         // Recommend new friends.
         recommendFriends(user1, 1, client);

         // Befriend user.
         befriendUser(user1, user2, client);

         // Recommend new resources.
         recommendResources(user1, 3, client);

         // Validate.
         getUser(user1, client);
         getUser(user2, client);
         getUser(user3, client);
         getResource(resource1, client);
         getResource(resource2, client);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void addUser(String userName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "add_user/" + userName);
         webResource.accept(MediaType.TEXT_PLAIN).get(String.class );
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void deleteUser(String userName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "delete_user/" + userName);
         webResource.delete();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void getUser(String userName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "get_user/" + userName);
         System.out.println(webResource.accept(MediaType.TEXT_PLAIN).get(String.class ));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void getUsers(Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "get_users");
         System.out.println(webResource.accept(MediaType.TEXT_PLAIN).get(String.class ));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void addResource(String resourceName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "add_resource/" + resourceName);
         webResource.accept(MediaType.TEXT_PLAIN).get(String.class );
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void deleteResource(String resourceName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "delete_resource/" + resourceName);
         webResource.delete();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void getResource(String resourceName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "get_resource/" + resourceName);
         System.out.println(webResource.accept(MediaType.TEXT_PLAIN).get(String.class ));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void getResources(Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "get_resources");
         System.out.println(webResource.accept(MediaType.TEXT_PLAIN).get(String.class ));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   // Recommend new friends.
   public static void recommendFriends(String userName, int maxFriends, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "recommend_friends/" + userName + "/" + maxFriends);
         System.out.println(webResource.accept(MediaType.TEXT_PLAIN).get(String.class ));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void recommendResources(String userName, int maxResources, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "recommend_resources/" + userName + "/" + maxResources);
         System.out.println(webResource.accept(MediaType.TEXT_PLAIN).get(String.class ));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void getFriends(String userName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "get_friends/" + userName);
         System.out.println(webResource.accept(MediaType.TEXT_PLAIN).get(String.class ));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void befriendUser(String userName, String friendName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "befriend_user/" + userName + "/" + friendName);
         webResource.accept(MediaType.TEXT_PLAIN).get(String.class );
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void unfriendUser(String userName, String friendName, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "unfriend_user/" + userName + "/" + friendName);
         webResource.accept(MediaType.TEXT_PLAIN).get(String.class );
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void unfriendAll(Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "unfriend_all");
         webResource.accept(MediaType.TEXT_PLAIN).get(String.class );
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public static void rateResource(String userName, String resourceName, float rating, Client client)
   {
      try
      {
         WebResource webResource =
            client.resource(REST_ADDRESS + "rate_resource/" + userName + "/" + resourceName + "/" + rating);
         webResource.accept(MediaType.TEXT_PLAIN).get(String.class );
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}
