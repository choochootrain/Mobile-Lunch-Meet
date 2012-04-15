package com.cellphones.mobilelunchmeet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.maps.*;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;
import android.widget.EditText;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.app.Activity;

public class GPSActivity extends MapActivity {
    private MapController mapController;
    private MapView mapView;
    private LocationManager locationManager;
    private Overlays itemizedoverlay;
    private MyLocationOverlay myLocationOverlay;
    private Location previous;
    private int id;
    private boolean locationCentered;
    
    private View loginView;
    private View splashView;
    private View accountView;
    private LayoutInflater inflater;
    
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    
    private EditText loginText;
    private EditText passwordText;
    private Button loginButton;
    private Button accountButton;
    
    private EditText c_loginText;
    private EditText c_passwordText;
    private EditText c_repeatPasswordText;
    private Spinner c_yearSpinner;
    private Button c_accountButton;
    
    public static final String PREFS_NAME = "PrefsFile";
    public static final String TAG = "GPSActivity";
    
    private static final int splash_window = 5000;
    
    
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        inflater = getLayoutInflater();
        
        setContentView(R.layout.map);
        
        splashView = inflater.inflate(R.layout.splash, null);
        addContentView(splashView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        
        loginView = inflater.inflate(R.layout.login, null);
        addContentView(loginView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        initLoginView();
        loginView.setVisibility(View.INVISIBLE);
        
        //runSplashThread();
        
        mapView = (MapView) findViewById(R.id.map);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(17);

        mapView.setVisibility(View.INVISIBLE);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000,
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
        id = settings.getInt("id", -1);
        if (id < 0) {
            id = Server.register("Test User", 2);
            Toast.makeText(this, "New user registered: " + id, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Old user logged in: " + id, Toast.LENGTH_SHORT).show();
        }
        editor.putInt("id", id);
        editor.commit();


        Drawable drawable = this.getResources().getDrawable(R.drawable.point);
        itemizedoverlay = new Overlays(this, drawable, id, myLocationOverlay);

        getLocations(Server.showLocations());
        
        loginView.setVisibility(View.VISIBLE);
        ((ViewGroup)splashView.getParent()).removeView(splashView);
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
                Log.e(TAG, "##########" + lat + " " + (int)(1E6 * lat));
                Log.e(TAG, "##########" + lon + " " + (int)(1E6 * lon));
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
				return true;
			case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
			case R.id.logout_button:
				// switch to login screen
				//super.finish();
				mapView.setVisibility(View.INVISIBLE);
				loginView.setVisibility(View.VISIBLE);
				loginView.requestFocus();
				return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    protected void initLoginView(){
    	loginText = (EditText) findViewById(R.id.login_input);
    	passwordText = (EditText) findViewById(R.id.password_input);
    	loginButton = (Button) findViewById(R.id.login_button);
        accountButton = (Button) findViewById(R.id.create_account_button);
         
    	populateLogin();
        
        createLoginListeners();
    }
    
    protected void populateLogin(){
    	String login = settings.getString("current_login", "");
        String password = settings.getString("current_password", "");
    	
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
    				int desired_id = settings.getInt(login_text, -1);
    				if(id == -1) return;
    				String password_text = passwordText.getText().toString();
    				int password_id = settings.getInt(password_text, -1);
    				if(desired_id != password_id) return;
    				
    				loginView.setVisibility(View.INVISIBLE);
    				mapView.setVisibility(View.VISIBLE);
    				mapView.requestFocus();
    			}
    		});
    		accountButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				// go to account creation screen (not created)
    				editor.putString("login", "wugs");
    				editor.putString("password", "*******");
    				editor.commit();
    				loginText.setText("wugs");
    		    	passwordText.setText("*******");
    		    	
    		    	loginView.setVisibility(View.INVISIBLE);
    		    	Intent i = new Intent(GPSActivity.this, CreateAccountActivity.class);
    		    	startActivity(i);
    		    	loginView.setVisibility(View.VISIBLE);
    			}
    		});
    	}catch(Exception e){
    		System.out.println("Error while creating login listeners");
    		Log.e("ERROR", "Error in createLoginListeners: " + e.toString());
    		e.printStackTrace();
    	}
    }
    
    private void runSplashThread(){
    	 splashView = inflater.inflate(R.layout.splash, null);
         addContentView(splashView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
         
    	  Thread splash_thread = new Thread() {
          	@Override
          	public void run(){
          		addContentView(splashView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                
          		try{
          			int wait_time = 0;
          			while(wait_time <= splash_window){
          				sleep(100);
          				wait_time += 100;
          				//System.out.println("splash screen waited");
          			}
          		}catch(Exception e){
          			System.out.println("Error in splash screen:");
          			e.printStackTrace();
          		}finally{
          			//((ViewGroup)splashView.getParent()).removeView(splashView);
          			try{
          				splashView.setVisibility(View.INVISIBLE);
          			//loginView.setVisibility(View.VISIBLE);
          			//loginView.requestFocus();
          			}catch(Exception e){
          				System.out.println("Error in splash screen:");
          				Log.e("Error in splash screen", "error: " + e.toString());
              			e.printStackTrace();
          			}
          		}
          	}
          };
          splash_thread.start();
    }
}
