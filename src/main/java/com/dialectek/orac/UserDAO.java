package com.dialectek.orac;

import java.util.HashMap;
import java.util.Map;

import com.dialectek.orac.User;

public enum UserDAO
{
   instance;

   public Map<Integer, User> content = new HashMap<Integer, User>();

   private UserDAO()
   {
   }
}
