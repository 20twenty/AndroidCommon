package com.liketivist.androidcommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public abstract class MyHttpPost extends AsyncTask<Integer, Integer, MyHttpPostResult> {
   String _url;
   List<NameValuePair> _nameValuePairs = new ArrayList<NameValuePair>(2);
   
   public MyHttpPost(String url){
      _url = url;
   };

   public MyHttpPost addKeyValuePair(String key, String value) {
      _nameValuePairs.add(new BasicNameValuePair(key, value));
      return this;
   }
   
   public MyHttpPost addKeyValuePairList(String keyPrefix, List<String> valueList) {
      for(int i = 0; i < valueList.size(); i++) {
         _nameValuePairs.add(new BasicNameValuePair(String.format("%s%d",keyPrefix,i), valueList.get(i)));
      }
      return this;
   }

   @TargetApi(Build.VERSION_CODES.HONEYCOMB)
   public MyHttpPost exec() {
      if (Build.VERSION.SDK_INT < 11) {
         this.execute();
      } else {
         this.executeOnExecutor(THREAD_POOL_EXECUTOR);
      }
      return this;
   }
   
   @Override
   protected MyHttpPostResult doInBackground(Integer... params) {
      MyHttpPostResult rspr = new MyHttpPostResult();
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httppost = new HttpPost(_url);
      int attempts = 0;
      boolean sent = false;
      while (!sent && attempts < 3) {
         try {
            httppost.setEntity(new UrlEncodedFormEntity(_nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
               InputStream instream = entity.getContent();
               rspr._clientError = false;
               rspr._clientErrorMessage = "";
               rspr.setServerResponseText(convertStreamToString(instream));
               instream.close();
            }
            sent = true;
         } catch (Exception e) {
            // All exceptions come here
            rspr._clientError = true;
            rspr._clientErrorMessage = "no connection";
            try {
               Thread.sleep(10000);
            } catch (InterruptedException e1) {
            }
            attempts++;
         }
      }
      return rspr;
   }

   private String convertStreamToString(InputStream is) {
      /*
       * To convert the InputStream to String we use the
       * BufferedReader.readLine() method. We iterate until the BufferedReader
       * return null which means there's no more data to read. Each line will
       * appended to a StringBuilder and returned as String.
       */
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();

      String line = null;
      try {
         boolean first = true;
         while ((line = reader.readLine()) != null) {
            if (!first)
               sb.append("\n");
            else
               first = false;
            sb.append(line);
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            is.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return sb.toString();
   }

   @Override
   protected void onPostExecute(MyHttpPostResult result) {
      onResponseReceived(result);
   }

   public abstract void onResponseReceived(MyHttpPostResult result);
   
}
