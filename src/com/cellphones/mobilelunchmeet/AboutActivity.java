package com.cellphones.mobilelunchmeet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView t3 = (TextView) findViewById(R.id.github);
        t3.setText(
                Html.fromHtml(
                        "Source code available at <a href='http://github.com/choochootrain/Mobile-Lunch-Meet'>http://github.com/choochootrain/Mobile-Lunch-Meet</a>"));
        t3.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
