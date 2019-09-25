package com.dialectek.orac;

import java.util.HashMap;
import java.util.Map;

import com.dialectek.orac.Content;

public enum ContentDAO
{
   instance;

   public Map<Integer, Content> content = new HashMap<Integer, Content>();

   private ContentDAO()
   {
   }
}
