package com.cellphones.mobilelunchmeet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{
	
	private EditText loginText;
	private EditText passwordText;
	private Button loginButton;
	private Button accountButton;
	    
	private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    
	private View loginView;
	private Activity this_reference;
	
	public static final String PREFS_NAME = "PrefsFile";
    private static final int CREATE_ACCOUNT_REQUEST_CODE = 2;
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		LayoutInflater inflater = getLayoutInflater();
		loginView = inflater.inflate(R.layout.login, null);
		addContentView(loginView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		
		this_reference = this;
		
		settings = getSharedPreferences(PREFS_NAME, 0);
	    editor = settings.edit();
	        
		initLoginView();
		populateLogin();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode,  resultCode, data);
	    	
	    if(requestCode == CREATE_ACCOUNT_REQUEST_CODE){
	    	switch(resultCode){
	    	case 1: // create account finished successfully
	    		settings = getSharedPreferences(PREFS_NAME, 0);
	            editor = settings.edit();
	    		
	    		populateLogin();
	    		loginView.setVisibility(View.VISIBLE);
	    	
	    		break;
	    	case 5: // quit button pressed
	    		setResult(5);
	    		super.finish();
	    		break;
	    	default:
	    		break;
	    	}
	    }
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alternate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
			case R.id.quit_button:
				setResult(5);
				super.finish();
				return true;
			case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
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
    				// finish LoginActivity and switch focus to GPSActivity if login checks out
    				String login_text = loginText.getText().toString();
    				int id = settings.getInt(login_text, -1);
    				if(id == -1){
    					Toast.makeText(this_reference, "login \"" + login_text + "\" does not exist", Toast.LENGTH_LONG).show();
    					return;
    				}
    				
    				String password_text = passwordText.getText().toString();
    				int pass_id = settings.getInt(password_text, -1);
    				if(id != pass_id){
    					Toast.makeText(this_reference, "Password incorrect", Toast.LENGTH_LONG).show();
    					return;
    				}

                    boolean loggedin = Server.login(login_text.toLowerCase(), password_text);
                    
                    if(!loggedin){
                        Toast.makeText(this_reference, "Login to server failed", Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    editor.putString("login", login_text);
                    editor.putString("password", password_text);
                    editor.putInt("id", id);
                    editor.commit();
    				
    				((ViewGroup)loginView.getParent()).removeView(loginView);
    				
    				setResult(1);
    				this_reference.finish();
    			}
    		});
    		accountButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				// go to account creation screen
    		    	loginView.setVisibility(View.INVISIBLE);
    		    	Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
    		    	//startActivity(i);
    		    	startActivityForResult(i, CREATE_ACCOUNT_REQUEST_CODE);
    			}
    		});
    	}catch(Exception e){
    		System.out.println("Error while creating login listeners");
    		Log.e("ERROR", "Error in createLoginListeners: " + e.toString());
    		e.printStackTrace();
    	}
    }
}
