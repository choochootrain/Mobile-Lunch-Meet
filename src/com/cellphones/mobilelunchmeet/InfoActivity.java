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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import org.json.JSONObject;
import android.widget.AdapterView.OnItemSelectedListener;

public class InfoActivity extends Activity{
	
	private EditText nameText;
	private Spinner yearSpinner;
	private Button saveButton;
	private Button returnButton;
	    
	private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    
	private View infoView;
	private Activity this_reference;
	
	private int selected_year;
	
	public static final String PREFS_NAME = "PrefsFile";
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		LayoutInflater inflater = getLayoutInflater();
		infoView = inflater.inflate(R.layout.info, null);
		addContentView(infoView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		
		this_reference = this;
		
		settings = getSharedPreferences(PREFS_NAME, 0);
	    editor = settings.edit();
	    
		nameText = (EditText) findViewById(R.id.i_name_input);
    	saveButton = (Button) findViewById(R.id.i_save_info_button);
    	returnButton = (Button) findViewById(R.id.i_return_button);
    	
    	 yearSpinner = (Spinner) findViewById(R.id.i_spinner);
	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     yearSpinner.setAdapter(adapter);
	     yearSpinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener());
    
    	int id = settings.getInt("id", -1);
    	
    	try{
        	JSONObject user_info = Server.getUser(id);
        	JSONObject user = (JSONObject) user_info.get("user");
        	
        	String name = user.getString("name");
        	nameText.setText(name);
        	selected_year = user.getInt("year");
        	yearSpinner.setSelection(selected_year);
        }catch(Exception e){
        	Log.e("InfoActivity.onCreate()", e.toString());
        	e.printStackTrace();
        }
    	
    	createButtonListeners();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
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

    protected void createButtonListeners(){
    	try{
    		saveButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				// save changes locally and push them to the server
    				int id = settings.getInt("id", -1);
    				String name = nameText.getText().toString();
    				
    				if(name.isEmpty()){
    					Toast.makeText(this_reference,  "Must provide a name", Toast.LENGTH_LONG).show();
    					return;
    				}
    				
    				editor.putInt(name, id);
    				editor.commit();
    				
    				Server.changeInfo(id, name, selected_year);
    				Toast.makeText(this_reference, "Information saved", Toast.LENGTH_LONG).show();
    			}
    		});
    		
    		returnButton.setOnClickListener(new View.OnClickListener(){
    			//@Override
    			public void onClick(View view){
    				this_reference.setResult(1);
    				this_reference.finish();
    			}
    		});
    	}catch(Exception e){
    		Log.e("InfoActivity.createButtonListener()", e.toString());
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
    
  //  04-27 20:13:32.057: D/JSONObject(15423): {"user":{"id":19,"username":"buns","updated_at":"2012-04-26T04:44:15Z","name":"Fred","created_at":"2012-04-26T04:44:12Z","active":1,"year":1,"partner":0,"password":"boy"}}

}

