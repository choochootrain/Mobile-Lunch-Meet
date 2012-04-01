package com.cellphones.mobilelunchmeet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.maps.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GPSActivity extends MapActivity {
    private MapController mapController;
    private MapView mapView;
    private LocationManager locationManager;
    private Overlays itemizedoverlay;
    private MyLocationOverlay myLocationOverlay;
    private Location previous;
    private int id;
    private boolean locationCentered;
    
    public static final String PREFS_NAME = "PrefsFile";
    public static final String TAG = "GPSActivity";
    
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.map);

        mapView = (MapView) findViewById(R.id.map);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(17);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000,
                10, (LocationListener) new GeoUpdateHandler());

        previous = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                Location me = myLocationOverlay.getLastFix();
                mapController.animateTo(myLocationOverlay.getMyLocation());
                Server.sendLocation(id, me.getLatitude(), me.getLongitude());
            }
        });

        //AsyncTaskify this
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id = settings.getInt("id", -1);
        if (id < 0) {
            id = Server.register("Test User", 2);
            Toast.makeText(this, "New user registered: " + id, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Old user logged in: " + id, Toast.LENGTH_SHORT).show();
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("id", id);
        editor.commit();


        Drawable drawable = this.getResources().getDrawable(R.drawable.point);
        itemizedoverlay = new Overlays(this, drawable, id, myLocationOverlay);

        getLocations(Server.showLocations());
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public class GeoUpdateHandler implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Server.sendLocation(id, lat, lng);
            if(!location.equals(previous)) {
                previous = location;
                getLocations(Server.showLocations());
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
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
}
