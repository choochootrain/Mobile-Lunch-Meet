package com.cellphones.mobilelunchmeet;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class Server {
    
    private static HashMap<Integer, String> names;
    private static HashMap<String, Integer> ids;
    private static HashMap<Integer, GeoPoint> points;

    public static int register(String username, String password, String name, int year) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String address = "http://vivid-ocean-9711.heroku.com/register/" + username + "/" + password + "/" + name + "/" + year + ".json";
            request.setURI(new URI(address.replace(" ", "%20")));
            String content = client.execute(request, new BasicResponseHandler());
            JSONObject response = new JSONObject(content);
            JSONObject user = (JSONObject) response.get("user");
            return user.getInt("id");
        } catch (Exception e) {
        	Log.e("register", "Problem in register");
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean login(String username, String password) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String address = "http://vivid-ocean-9711.heroku.com/login/" + username + "/" + password + ".json";
            request.setURI(new URI(address.replace(" ", "%20")));
            client.execute(request, new BasicResponseHandler());
            populateNames();
            populateLocations();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void populateNames() {
        names = new HashMap<Integer, String>();
        ids = new HashMap<String, Integer>();
        JSONArray users = showUsers();
        try {
            for(int i = 0; i < users.length(); i++) {
                JSONObject usercontainer = (JSONObject)users.get(i);
                JSONObject user = (JSONObject)usercontainer.get("user");
                int id = user.getInt("id");
                String name = user.getString("name");
                names.put(id, name);
                ids.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getName(int id) {
        if (!names.containsKey(id))
            populateNames();
        if (!names.containsKey(id))
            return "Id: " + id;
        else
            return names.get(id);
    }

    public static boolean logout(String username) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String address = "http://vivid-ocean-9711.heroku.com/logout/" + username + ".json";
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

    public static GeoPoint getLocation(int id) {
        if (!points.containsKey(id))
            populateLocations();
        if (!points.containsKey(id))
            return null;
        else
            return points.get(id);
    }

    private static void populateLocations() {
        points = new HashMap<Integer, GeoPoint>();
        JSONArray locations = showLocations();
        try {
            for (int i = 0; i < locations.length(); i++) {
                JSONObject item = (JSONObject) locations.get(i);
                JSONObject location = (JSONObject) item.get("location");
                int loc_id = location.getInt("user_id");
                double lat = location.getDouble("lat");
                double lon = location.getDouble("long");
                GeoPoint p = new GeoPoint((int)(1E6 * lat), (int)(1E6 * lon));
                points.put(loc_id, p);
            }
        } catch (Exception e) {
            System.out.println("Error in getLocations");
            e.printStackTrace();
        }
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

    public static int partner(int id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/partner/" + id + ".json"));
            String content = client.execute(request, new BasicResponseHandler());
            return Integer.parseInt(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public static void changeInfo(int id, String name, int year) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/changeInfo/" + id + "/" + name + "/" + year + ".json"));
            String content = client.execute(request, new BasicResponseHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getId(String name) {
        if (!ids.containsKey(name))
            populateNames();
        if (!ids.containsKey(name))
            return -1;
        else
            return ids.get(name);
    }

    public static JSONObject match(int id, int otherid) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://vivid-ocean-9711.heroku.com/match/" + id + "/" + otherid + ".json"));
            String content = client.execute(request, new BasicResponseHandler());
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void reject(int id, int match) {

    }
}