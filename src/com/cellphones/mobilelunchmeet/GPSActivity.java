package com.cellphones.mobilelunchmeet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.maps.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.EditText;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Activity;
import android.content.res.Configuration;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

public class GPSActivity extends MapActivity {
    private MapController mapController;
    private MapView mapView;
    private LocationManager locationManager;
    private Overlays itemizedoverlay;
    protected MyLocationOverlay myLocationOverlay;
    private Location previous;
    protected int id;
    protected int match;
    private boolean locationCentered;

    private boolean logged_in;
    
    //private LayoutInflater inflater;

    /*
    private EditText loginText;
    private EditText passwordText;
    private Button loginButton;
    private Button accountButton;
    */
    
    private Activity this_reference;
    
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private Handler handler;
    
    private NotificationManager nManager;
    private Notification notification;
    
    public static final String PREFS_NAME = "PrefsFile";
    public static final String TAG = "GPSActivity";
    private static final int NOTIFICATION_ID = 1;
    private static final int LOGIN_REQUEST_CODE = 1;
    private static final int SPLASH_REQUEST_CODE = 3;
    
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this_reference = this;
        
        //inflater = getLayoutInflater();
        
        setContentView(R.layout.map);
        
        Intent i = new Intent(GPSActivity.this, SplashActivity.class);
        startActivityForResult(i, SPLASH_REQUEST_CODE);
        
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
            
        mapView = (MapView) findViewById(R.id.map);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        mapController.setZoom(17);
       
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                10, (LocationListener) new GeoUpdateHandler());

        previous = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            //@Override
            public void run() {
                Location me = myLocationOverlay.getLastFix();
                mapController.animateTo(myLocationOverlay.getMyLocation());
                Server.sendLocation(id, me.getLatitude(), me.getLongitude());
            }
        });

        //AsyncTaskify this
        /*
        id = settings.getInt("current id", -1);

        if (id < 0) {
            id = Server.register("Test User", "password", 2);
            Toast.makeText(this, "New user registered: " + id, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Old user logged in: " + id, Toast.LENGTH_SHORT).show();
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("id", id);
        editor.commit();
        */

        Thread partnerUpdate = new Thread() {
            @Override
            public void run() {
                while (true) {
                    GPSActivity.this.match = Server.partner(GPSActivity.this.id);
                    if (GPSActivity.this.match > 0) {//you were matched
                        GPSActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(GPSActivity.this, "You are matched to " + match, Toast.LENGTH_LONG).show();

                                JSONObject match = Server.match(id);
                                try {
                                    Object o = match.get("location");
                                    if (o == null)
                                        throw new JSONException("No match was found");
                                    JSONObject location = (JSONObject)o;
                                    int loc_id = location.getInt("user_id");
                                    double end_lat = location.getDouble("lat");
                                    double end_long = location.getDouble("long");
                                    Toast.makeText(GPSActivity.this, "You are matched to " + loc_id, Toast.LENGTH_LONG).show();
                                    if (GPSActivity.this.myLocationOverlay.getLastFix() != null) {
                                        double lat = GPSActivity.this.myLocationOverlay.getLastFix().getLatitude();
                                        double lon = GPSActivity.this.myLocationOverlay.getLastFix().getLongitude();
                                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?saddr=" +
                                                        lat + "," + lon + "&daddr=" +
                                                        end_lat + "," + end_long));
                                        GPSActivity.this.startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(GPSActivity.this, "Brb something broke.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        break;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        partnerUpdate.start();

        /*
        Drawable drawable = this.getResources().getDrawable(R.drawable.point);
        itemizedoverlay = new Overlays(this, drawable, id, myLocationOverlay);

        getLocations(Server.showLocations());
		*/

        handler = new Handler();

        Runnable refreshTask = new Runnable()
        {
            public void run()
            {
                handler.removeCallbacks(this);

                mapView.postInvalidate();

                handler.postDelayed(this, 1000);

            }
        };
        refreshTask.run();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public class GeoUpdateHandler implements LocationListener {

        //@Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Server.sendLocation(id, lat, lng);
            if(!location.equals(previous)) {
                previous = location;
                getLocations(Server.showLocations());
            }
        }

        //@Override
        public void onProviderDisabled(String provider) {
        }

        //@Override
        public void onProviderEnabled(String provider) {
        }

        //@Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private void getLocations(JSONArray locations) {
        mapView.getOverlays().clear();
        try {
            for (int i = 0; i < locations.length(); i++) {
                JSONObject item = (JSONObject) locations.get(i);
                JSONObject location = (JSONObject) item.get("location");
                int loc_id = location.getInt("user_id");
                double lat = location.getDouble("lat");
                double lon = location.getDouble("long");
                Log.d(TAG, "##########" + lat + " " + (int)(1E6 * lat));
                Log.d(TAG, "##########" + lon + " " + (int) (1E6 * lon));
                GeoPoint p = new GeoPoint((int)(1E6 * lat), (int)(1E6 * lon));
                OverlayItem overlayItem;
                if (loc_id != id)
                    overlayItem = new OverlayItem(p, "User " + loc_id, "(" + lat +", " + lon + ")");
                else {//your location
                    overlayItem = new OverlayItem(p, "You", "Your Location");
                    if (!locationCentered) {
                        mapController.animateTo(p);
                        locationCentered = true;
                    }
                }
                itemizedoverlay.addOverlay(overlayItem);

                if (itemizedoverlay.size() > 0) {
                    mapView.getOverlays().add(itemizedoverlay);
                }
            }
        } catch (Exception e) {
        	System.out.println("Error in getLocations");
            e.printStackTrace();
        }
        mapView.postInvalidate();
    }

    private void createMarker() {
        GeoPoint p = mapView.getMapCenter();
        OverlayItem overlayitem = new OverlayItem(p, "", "");
        itemizedoverlay.addOverlay(overlayitem);
        if (itemizedoverlay.size() > 0) {
            mapView.getOverlays().add(itemizedoverlay);
        }
    }

    private void createMarker(Location location, String title, String snippet) {
        GeoPoint p = new GeoPoint((int)(1E6 * location.getLatitude()),(int)(1E6 * location.getLongitude()));
        OverlayItem overlayitem = new OverlayItem(p, title, snippet);
        itemizedoverlay.addOverlay(overlayitem);
        if (itemizedoverlay.size() > 0) {
            mapView.getOverlays().add(itemizedoverlay);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableCompass();
    }

    @Override
    protected void onPause() {
        super.onResume();
        myLocationOverlay.disableMyLocation();
        myLocationOverlay.disableCompass();
    }
    
    @Override
	protected void onDestroy(){
		super.onDestroy();
		
		Server.logout(settings.getString("login", "").toLowerCase());
		
		try{
			nManager.cancelAll();
		}catch(Exception e){
			// if it doesn't work, program exited before nManager instanced, so who cares
		}
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode,  resultCode, data);
    	
    	if(requestCode == LOGIN_REQUEST_CODE){
    		switch(resultCode){
	    	case 1: // user successfully logged in
	    		logged_in = true;
	    		mapView.setVisibility(View.VISIBLE);
	    		createNotification();
	    		
	    		settings = getSharedPreferences(PREFS_NAME, 0);
	            editor = settings.edit();
	            id = settings.getInt("id", -1);
	            
	    		break;
	    	case 5: // quit button pressed
	    		super.finish();
	    		break;
	    	default:
	    		break;
	    	}	
    	}else if(requestCode == SPLASH_REQUEST_CODE){
    		//Log.d("GPSActivity.onActivityResult", "splash activity finished");
    		switch(resultCode){
    		case 1:
    			login();
    			break;
    		default:
    			break;
    		}
    	}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
			case R.id.quit_button:
				super.finish();
				nManager.cancelAll();
				return true;
			case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
			case R.id.logout_button:
				// switch to login screen
				//super.finish();
				logged_in = false;
				
				Server.logout(settings.getString("login", "").toLowerCase());
				nManager.cancelAll();
				Toast.makeText(this, settings.getString("login", "") + " logged out", Toast.LENGTH_SHORT).show();
				
				editor.putInt("id", -1);
				editor.commit();
				
				mapView.setVisibility(View.INVISIBLE);
				Intent i = new Intent(GPSActivity.this, LoginActivity.class);
				startActivityForResult(i, LOGIN_REQUEST_CODE);
				
				return true;
			case R.id.info_button:
				
				
				return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    protected void initLoginView(){
    	loginText = (EditText) findViewById(R.id.login_input);
    	passwordText = (EditText) findViewById(R.id.password_input);
    	loginButton = (Button) findViewById(R.id.login_button);
        accountButton = (Button) findViewById(R.id.create_account_button);

    	populateLogin();

        createLoginListeners();
    }

    protected void populateLogin(){
    	String login = settings.getString("login", "");
        String password = settings.getString("password", "");
    	loginText.setText(login);
    	passwordText.setText(password);
    }

    protected void createLoginListeners(){
    	try{
    		loginButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				// switch focus to GPSActivity if login checks out
    				String login_text = loginText.getText().toString();
    				int id = settings.getInt("id", -1);
    				if(id == -1){
    					Toast.makeText(this_reference, "login \"" + login_text + "\" does not exist", Toast.LENGTH_LONG).show();
    					return;
    				}
    				
                    String login = settings.getString("login", "");
    				String password_text = passwordText.getText().toString();
    				String password = settings.getString("password", "");
    				if(!password.equals(password_text) || password.equals("") || !login.equals(login_text) || login.equals("")) {
    					Toast.makeText(this_reference, "Invalid credentials", Toast.LENGTH_LONG).show();
    					return;
    				}

                    boolean loggedin = Server.login(login_text.toLowerCase(), password_text);
                    
                    if(!loggedin)
                        Toast.makeText(this_reference, "Login failed", Toast.LENGTH_LONG).show();
    				
    				loginView.setVisibility(View.INVISIBLE);
    				mapView.setVisibility(View.VISIBLE);
    				mapView.requestFocus();
    				
    				login();
    			}
    		});
    		accountButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				// go to account creation screen (not created)
    		    	loginView.setVisibility(View.INVISIBLE);
    		    	Intent i = new Intent(GPSActivity.this, CreateAccountActivity.class);
    		    	startActivity(i);
    		    	startActivityForResult(i, LOGIN_REQUEST_CODE);

    		    	loginView.setVisibility(View.VISIBLE);
    			}
    		});
    	}catch(Exception e){
    		System.out.println("Error while creating login listeners");
    		Log.e("ERROR", "Error in createLoginListeners: " + e.toString());
    		e.printStackTrace();
    	}
    }
    */
    
    protected void login(){
    	id = settings.getInt("id", -1);
        
        if(id < 0){
        	mapView.setVisibility(View.INVISIBLE);
        	logged_in = false;
        	
        	Intent i = new Intent(GPSActivity.this, LoginActivity.class);
	    	startActivityForResult(i, LOGIN_REQUEST_CODE);
	    	
        }else{
        	logged_in = true;

            boolean loggedin = Server.login(settings.getString("login", "").toLowerCase(), settings.getString("password", ""));

            if(!loggedin){
                Toast.makeText(this_reference, "Login to server failed", Toast.LENGTH_LONG).show();
                
                mapView.setVisibility(View.INVISIBLE);
            	logged_in = false;
            	
            	Intent i = new Intent(GPSActivity.this, LoginActivity.class);
    	    	startActivityForResult(i, LOGIN_REQUEST_CODE);
    	    	return;
            }
        	
        	Drawable drawable = this.getResources().getDrawable(R.drawable.point);
            itemizedoverlay = new Overlays(this, drawable, id, myLocationOverlay);

            getLocations(Server.showLocations());
            
            createNotification();
        }
    }
    
    protected void createNotification(){
    	nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	int icon = R.drawable.ic_launcher;
    	CharSequence tickerText = "Logged onto Mobile Lunch Meet";
    	long when = System.currentTimeMillis();  	
    	notification = new Notification(icon, tickerText, when);
    
    	Context context = getApplicationContext();
    	CharSequence contentTitle = "Mobile Lunch Meet";
    	CharSequence contentText = "Looking for a lunch partner";
    	Intent notificationIntent = new Intent(this, GPSActivity.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	
    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    	notification.flags |= Notification.FLAG_ONGOING_EVENT;
        
    	try{
    		nManager.notify(NOTIFICATION_ID, notification);
    	}catch(Exception e){
    		Log.e("GPSActivity.createNotification", "Error while creating notification: " + e.toString());
    		e.printStackTrace();
    	}
    	
    	notification.defaults |= Notification.DEFAULT_VIBRATE;
    }
    
    protected void matchNotification(){
    	Context context = getApplicationContext();
    	CharSequence contentTitle = "Mobile Lunch Meet";
    	CharSequence contentText = "Someone wants to have lunch with you!";
    	Intent notificationIntent = new Intent(this, GPSActivity.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	
    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    	try{
    		nManager.notify(NOTIFICATION_ID, notification);
    	}catch(Exception e){
    		Log.e("GPSActivity.matchNotification", "Error while attempting match notification: " + e.toString());
    		e.printStackTrace();
    	}
    }
    
    protected void rejectMatchNotification(){
    	Context context = getApplicationContext();
    	CharSequence contentTitle = "Mobile Lunch Meet";
    	CharSequence contentText = "Looking for a lunch partner!";
    	Intent notificationIntent = new Intent(this, GPSActivity.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	
    	notification.defaults |= Notification.DEFAULT_VIBRATE;
    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    	try{
    		nManager.notify(NOTIFICATION_ID, notification);
    	}catch(Exception e){
    		Log.e("GPSActivity.rejectMatchNotification", "Error while attempting reject match notification: " + e.toString());
    		e.printStackTrace();
    	}
    	notification.defaults |= Notification.DEFAULT_VIBRATE;
    }
}
