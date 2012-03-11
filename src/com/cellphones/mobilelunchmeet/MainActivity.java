package com.cellphones.mobilelunchmeet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class MainActivity extends Activity
{
    private Button jsonButton;
    private TextView jsonResult;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        jsonButton = (Button) findViewById(R.id.json_button);
        jsonResult = (TextView) findViewById(R.id.json_result);
        
        jsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
    }

    private void getData() {
        //PUT CALL TO SERVER HERE
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://search.twitter.com/search.json?q=lolwut"));
            String content = client.execute(request, new BasicResponseHandler());
            JSONObject response = new JSONObject(content);
            JSONArray data = response.getJSONArray("results");
            String result = "";
            for(int i = 0 ; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                result += "<b>" + item.get("from_user") + ":</b> " + item.get("text") + "<br />";
            }
            jsonResult.setText(Html.fromHtml(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
