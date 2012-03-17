package com.cellphones.mobilelunchmeet;

import android.location.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONObject;

public class Locater{
	private Location loc;
	private LocationManager loc_man;
	private LocationListener loc_listener;
	private String current_provider;
	
	public Locater(Activity a){
		loc_man = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
		
		if(loc_man.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			current_provider = LocationManager.GPS_PROVIDER;
		}else{
			current_provider = LocationManager.NETWORK_PROVIDER;
		}
		
		loc = loc_man.getLastKnownLocation(current_provider);
		
		loc_listener = new LocationListener(){
			public void onLocationChanged(Location location) {
				loc = location;
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				if(provider.equals(current_provider) 
						&& (status == LocationProvider.OUT_OF_SERVICE
							|| status == LocationProvider.TEMPORARILY_UNAVAILABLE) ){
					if(loc_man.isProviderEnabled(LocationManager.GPS_PROVIDER)){
						current_provider = LocationManager.GPS_PROVIDER;
					}else{
						current_provider = LocationManager.NETWORK_PROVIDER;
					}
				}
				
				loc_man.requestLocationUpdates(current_provider, 120000, 200, loc_listener);
			}
			
			public void onProviderEnabled(String provider) {
				if(!provider.equals(current_provider)
						&& provider.equals(LocationManager.GPS_PROVIDER)){
					current_provider = LocationManager.GPS_PROVIDER;
					loc_man.requestLocationUpdates(current_provider, 120000, 200, loc_listener);
				}
			}
			
			public void onProviderDisabled(String provider) {
				if(provider.equals(current_provider)){
					if(loc_man.isProviderEnabled(LocationManager.GPS_PROVIDER)){
						current_provider = LocationManager.GPS_PROVIDER;
					}else{
						current_provider = LocationManager.NETWORK_PROVIDER;
					}
				}
				
				loc_man.requestLocationUpdates(current_provider, 120000, 200, loc_listener);
			}
		};
		
		loc_man.requestLocationUpdates(current_provider, 120000, 200, loc_listener);
	}
	
	public Location getLocation(){
		return loc;
	}
	
	public JSONObject packageLocation(){
		JSONObject coords = new JSONObject();
		try{
			coords.put("latitude", loc.getLatitude());
			coords.put("longitude", loc.getLongitude());
		}catch(Exception e){
			System.out.println("Exception when packaging location");
			e.printStackTrace();
		}
		
		return coords;
	}
	
	public void printLocation(JSONObject loc){
		double latitude = 0, longitude = 0;
		try{
			latitude = loc.getDouble("latitude");
			longitude = loc.getDouble("longitude");
		}catch(Exception e){
			System.out.println("Exception when trying to unpackage JSONObject in Location_Activity.printLocation()");
			e.printStackTrace();
		}
		
		System.out.println("Latitude: " + latitude + "; Longitude: " + longitude);
	}
}
