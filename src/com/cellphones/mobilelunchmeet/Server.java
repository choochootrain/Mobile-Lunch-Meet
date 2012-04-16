package com.cellphones.mobilelunchmeet;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import android.util.Log;

public class Server {

    public static int register(String name, String password, int year) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String address = "http://vivid-ocean-9711.heroku.com/register/" + name + "/" + password + "/" + year + ".json";
            request.setURI(new URI(address.replace(" ", "%20")));
            String content = client.execute(request, new BasicResponseHandler());
          //  Log.d("register", "content: " + content);
            
            JSONObject response = new JSONObject(content);
            JSONObject user = (JSONObject) response.get("user");
           // Log.d("register", "user: " + user.toString());
            //Log.d("register", "user id: " + user.getInt("id"));
            return user.getInt("id");
        } catch (Exception e) {
        	Log.e("register", "Problem in register");
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean login(String name, String password) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String address = "http://vivid-ocean-9711.heroku.com/login/" + name + "/" + password + ".json";
            request.setURI(new URI(address.replace(" ", "%20")));
            client.execute(request, new BasicResponseHandler());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean logout(String name) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String address = "http://vivid-ocean-9711.heroku.com/logout/" + name + ".json";
            request.setURI(new URI(address.replace(" ", "%20")));
            client.execute(request, new BasicResponseHandler());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONArray showUsers() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/showusers.json"));
            String content = client.execute(request, new BasicResponseHandler());
            return new JSONArray(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray showLocations() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/showlocations.json"));
            String content = client.execute(request, new BasicResponseHandler());
            return new JSONArray(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeUser(int id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/removeuser/" + id + ".json"));
            String content = client.execute(request, new BasicResponseHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeLocation(int id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/removelocation/" + id + ".json"));
            String content = client.execute(request, new BasicResponseHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendLocation(int id, double latitude, double longitude) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/sendlocation/" + id + "/" + latitude + "/" + longitude));
            client.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getUser(int id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/getuser/" + id + ".json"));
            String content = client.execute(request, new BasicResponseHandler());
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getLocation(int id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/getlocation/" + id + ".json"));
            String content = client.execute(request, new BasicResponseHandler());
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject match(int id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/match/" + id + ".json"));
            String content = client.execute(request, new BasicResponseHandler());
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}