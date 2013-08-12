package com.liketivist.androidcommon;

import java.util.HashMap;

public class MyHttpPostResult {
   public boolean _clientError = false;
   public String _clientErrorMessage = "";
   public boolean _serverError = false;
   public String _serverErrorMessage = "";
   public String _serverResponseText = "";
   public HashMap<String, String> _serverResponseHashMap = new HashMap<String, String>();
   
   public MyHttpPostResult(){};
   public MyHttpPostResult setServerResponseText(String text) {
      _serverResponseText = text;
      String[] lines = text.split("\n");
      for (int i = 0; i<lines.length; i++) {
         String[] fields = lines[i].split("\\|");
         if(fields.length != 2) {
            _serverError = true;
            _serverErrorMessage = String.format("Invalid response from server, fields = %d: string = %s",fields.length,lines[i]);
            return this;
         }
         _serverResponseHashMap.put(fields[0], fields[1]);
      }
      return this;
   }
   
   public int getInt(String key, int defaultInt) {
      String value = _serverResponseHashMap.get(key);
      if (value == null) return defaultInt;
      int result;
      try {
         result = Integer.parseInt(value);
      } catch (NumberFormatException e) {
         return defaultInt;
      }
      return result;
   }

   public float getFloat(String key, float defaultFloat) {
      String value = _serverResponseHashMap.get(key);
      if (value == null) return defaultFloat;
      float result;
      try {
         result = Float.parseFloat(value);
      } catch (NumberFormatException e) {
         return defaultFloat;
      }
      return result;
   }
   
   public boolean getBoolean(String key, boolean defaultBoolean) {
      String value = _serverResponseHashMap.get(key);
      if (value == null) return defaultBoolean;
      Boolean result;
      try {
         result = Boolean.parseBoolean(value);
      } catch (NumberFormatException e) {
         return defaultBoolean;
      }
      return result;
   }

   public String getString(String key, String defaultString) {
      String value = _serverResponseHashMap.get(key);
      if (value == null) return defaultString;
      return value;
   }
}
