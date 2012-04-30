package com.cellphones.mobilelunchmeet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class Overlays extends BalloonItemizedOverlay<OverlayItem> {

    private static int maxNum = 20;
    private OverlayItem overlays[] = new OverlayItem[maxNum];
    private int index = 0;
    private boolean full = false;
    private Context context;
    private OverlayItem previousoverlay;
    private int id;
    private MyLocationOverlay locationOverlay;
    private GPSActivity gps;

    public Overlays(Context context, Drawable defaultMarker, int id, MyLocationOverlay myLocationOverlay, GPSActivity gps, MapView mapView) {
        super(boundCenterBottom(defaultMarker), mapView);
        this.context = context;
        this.id = id;
        locationOverlay = myLocationOverlay;
        this.gps = gps;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return overlays[i];
    }

    @Override
    public int size() {
        if (full) {
            return overlays.length;
        } else {
            return index;
        }

    }

    public void addOverlay(OverlayItem overlay) {
        if (previousoverlay != null) {
            if (index < maxNum) {
                overlays[index] = previousoverlay;
            } else {
                index = 0;
                full = true;
                overlays[index] = previousoverlay;
            }
            index++;
            populate();
        }
        this.previousoverlay = overlay;
    }

    public boolean onBalloonTap(int index, OverlayItem overlayItem) {
        Toast.makeText(context, "Tapped", Toast.LENGTH_LONG).show();
        if (overlayItem.getTitle().equals("You")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Would you like to be randomly matched with someone near you?");
            builder.setCancelable(true);
            builder.setPositiveButton("Match me!", new OkOnClickListener());
            builder.setNegativeButton("Cancel", new CancelOnClickListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Would you like to eat lunch with " + overlayItem.getTitle() + "?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes please", new OkOnClickListener2(overlayItem.getTitle()));
            builder.setNegativeButton("No thanks", new CancelOnClickListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    };

    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
        }
    }

    private final class OkOnClickListener implements
            DialogInterface.OnClickListener {

        public void onClick(DialogInterface dialog, int which) {
            int otherid = gps.match();
            Toast.makeText(context, "You have been matched to " + Server.getName(otherid) + ". Waiting for " + Server.getName(otherid) + " to respond...start polling here", Toast.LENGTH_LONG).show();
        }
    }

    private final class OkOnClickListener2 implements DialogInterface.OnClickListener {

        private String name;

        public OkOnClickListener2(String s) {
            super();
            name = s;
        }
        public void onClick(DialogInterface dialog, int which) {
            gps.matchTo(Server.getId(name));
            Toast.makeText(context, "Waiting for " + name + " to respond...start polling here", Toast.LENGTH_LONG).show();
        }
    }
}