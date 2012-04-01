package com.cellphones.mobilelunchmeet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

public class Server {

    public static int register(String name, int year) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String address = "http://vivid-ocean-9711.heroku.com/register/" + name + "/" + year + ".json";
            request.setURI(new URI(address.replace(" ", "%20")));
            String content = client.execute(request, new BasicResponseHandler());
            JSONObject response = new JSONObject(content);
            JSONObject user = (JSONObject) response.get("user");
            return user.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
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