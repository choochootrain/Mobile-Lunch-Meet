package com.cellphones.mobilelunchmeet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Toast;

public class CreateAccountActivity extends Activity{
	private EditText c_loginText;
	private EditText c_nameText;
    private EditText c_passwordText;
    private EditText c_repeatPasswordText;
    private Spinner c_yearSpinner;
    private Button c_accountButton;
    private TextView c_errorText;
    
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private View accountView;
    private Activity this_reference;
    
    private int selected_year;
    
    public static final String PREFS_NAME = "PrefsFile"; 
	
	@Override
	 public void onCreate(Bundle savedInstanceState)
	 {
	     super.onCreate(savedInstanceState);
	     
	     LayoutInflater inflater = getLayoutInflater();
	     accountView = inflater.inflate(R.layout.create_account, null);
	     addContentView(accountView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
	       
	     c_loginText = (EditText) findViewById(R.id.c_login_input);
	     c_nameText = (EditText) findViewById(R.id.c_name_input);
	     c_passwordText = (EditText) findViewById(R.id.c_password_input);
	     c_repeatPasswordText = (EditText) findViewById(R.id.c_repeat_password_input);
	     c_accountButton = (Button) findViewById(R.id.c_create_account_button);
	     c_errorText = (TextView) findViewById(R.id.c_error_text);
	     
	     c_yearSpinner = (Spinner) findViewById(R.id.c_spinner);
	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     c_yearSpinner.setAdapter(adapter);
	     c_yearSpinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener());
	     
	     selected_year = 0;
	     
	     settings = getSharedPreferences(PREFS_NAME, 0);
	     editor = settings.edit();
	     
	     this_reference = this;
	     
	     createButtonListeners();
	 }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    }
	
	private void createButtonListeners(){
		try{
    		c_accountButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				// create account and switch back to login screen
    				
    				if(handleInput()){
    					((ViewGroup)accountView.getParent()).removeView(accountView);
    					setResult(1);
    					this_reference.finish();
    				}
    			}
    			
    			private boolean handleInput(){
    				String loginText = c_loginText.getText().toString();
    				String nameText = c_nameText.getText().toString();
    				String passwordText = c_passwordText.getText().toString();
    				String repeatPasswordText = c_repeatPasswordText.getText().toString();
    				
    				if(loginText.equals("")
    						|| nameText.equals("")
    						|| passwordText.equals("")
    						|| repeatPasswordText.equals("")){
    					//Toast.makeText(this_reference, "Not all required fields completed", Toast.LENGTH_LONG).show();
    					c_errorText.setText("Not all fields complete");
    					return false;
    				}
    				
    				if(!passwordText.equals(repeatPasswordText)){
    					//Toast.makeText(this_reference, "Passwords did not match", Toast.LENGTH_LONG).show();
    					c_passwordText.setText("");
    					c_repeatPasswordText.setText("");
    					c_errorText.setText("Passwords do not match");
    					return false;
    				}
    				
    				if(settings.getInt(loginText, -1) != -1){
    					//Toast.makeText(this_reference, "Account already exists", Toast.LENGTH_LONG).show();
    					c_errorText.setText("Account already exists");
    					return false;
    				}	
    				
    				int user_id = Server.register(loginText.toLowerCase(), passwordText, nameText, selected_year);
    				
                    if(user_id == -1) {
                        //Toast.makeText(this_reference, "Account already exists", Toast.LENGTH_LONG).show();
                    	c_errorText.setText("Account already exists");
                        return false;
                    }

                    //Toast.makeText(this_reference, "id: " + user_id, Toast.LENGTH_LONG).show();
    				
    				editor.putString("login", loginText);
    				editor.putString("password", passwordText);
					editor.putInt("id", user_id);
					
					editor.putInt(loginText,user_id);
					editor.putInt(passwordText, user_id);
					editor.putInt(nameText, user_id);
					
    				editor.commit();

    				return true;
    			}
    		});
    	}catch(Exception e){
    		System.out.println("Error while creating create account button listener");
    		Log.e("ERROR", "Error in CreateAccountActivity.createButtonListeners: " + e.toString());
    		e.printStackTrace();
    	}
	}
	
	private class SpinnerOnItemSelectedListener implements OnItemSelectedListener{
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
			selected_year = pos;
		}
		
		public void onNothingSelected(AdapterView<?> parent){
			// do nothing
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
	
}
