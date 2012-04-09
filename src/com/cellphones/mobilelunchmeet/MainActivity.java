package com.cellphones.mobilelunchmeet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import android.util.Log;
import android.widget.EditText;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class MainActivity extends Activity
{
    private Button jsonButton;
    private TextView jsonResult;
    
    private EditText loginText;
    private EditText passwordText;
    private Button loginButton;
    private Button accountButton;
    
    private ViewGroup views;
    private LayoutInflater inflater;
    
    private static final int splash_window = 5000;
    
    public static final String PREFS_NAME = "PrefsFile";
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
setContentView(R.layout.splash);
        
/*
        Thread splash_thread = new Thread() {
        	@Override
        	public void run(){
        		try{
        			int wait_time = 0;
        			while(wait_time <= splash_window){
        				sleep(100);
        				wait_time += 100;
        				System.out.println("splash screen waited");
        			}
        		}catch(Exception e){
        			System.out.println("Error in splash screen:");
        			e.printStackTrace();
        		}finally{
        			startup();
        		}
        	}
        };
        splash_thread.start();
        */
        //inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		startup();
    }

    private void getData() {
        Server.sendLocation(24, 123.456, 789.123);
        JSONArray a = Server.showLocations();
        jsonResult.setText((CharSequence) a.toString());
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    protected void startup(){
        try{
        	startLoginView();
        }catch(Exception e){
        	Log.e("ERROR", "Error during login screen creation: " + e.toString());
        	e.printStackTrace();
        }	
    }
    
    protected void startLoginView(){
    	setContentView(R.layout.login);
    	
    	loginText = (EditText) findViewById(R.id.login_input);
    	passwordText = (EditText) findViewById(R.id.password_input);
    	loginButton = (Button) findViewById(R.id.login_button);
        accountButton = (Button) findViewById(R.id.create_account_button);
         
    	populateLogin();
        
        createLoginListeners();
    }
    
    protected void populateLogin(){
    	String login = "wugs";
    	String password = "********";
    	
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	
    	loginText.setText(login);
    	passwordText.setText(password);
    }
    
    protected void createLoginListeners(){
    	try{
    		loginButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				// switch focus to GPSActivity
    				Intent i = new Intent(MainActivity.this, GPSActivity.class);
    			    try{
    			       	startActivity(i);
    			    }catch(Exception e){
    			      	Log.e("ERROR", "Error during GPSActivity creation: " + e.toString());
    			       	e.printStackTrace();
    			    }
    			}
    		});
    		accountButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				// go to account creation screen (not created)
    			}
    		});
    	}catch(Exception e){
    		System.out.println("Error while creating login listeners");
    		Log.e("ERROR", "Error in createLoginListeners: " + e.toString());
    		e.printStackTrace();
    	}
    }
}
