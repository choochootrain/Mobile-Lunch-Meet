package com.cellphones.mobilelunchmeet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import org.json.JSONException;
import org.json.JSONObject;

public class Overlays extends ItemizedOverlay<OverlayItem> {

    private static int maxNum = 10;
    private OverlayItem overlays[] = new OverlayItem[maxNum];
    private int index = 0;
    private boolean full = false;
    private Context context;
    private OverlayItem previousoverlay;
    private int id;

    public Overlays(Context context, Drawable defaultMarker, int id) {
        super(boundCenterBottom(defaultMarker));
        this.context = context;
        this.id = id;
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

    protected boolean onTap(int index) {
        OverlayItem overlayItem = overlays[index];
        if (overlayItem.getTitle().equals("You") && overlayItem.getSnippet().equals("Your Location")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Would you like to be matched to someone?");
            builder.setCancelable(true);
            builder.setPositiveButton("Match me", new OkOnClickListener());
            builder.setNegativeButton("Lolwut no", new CancelOnClickListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(context, "" + overlayItem.getTitle() + " " + overlayItem.getSnippet(), Toast.LENGTH_LONG).show();
        }
        return true;
    };

    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(context, "You will not be matched automatically", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private final class OkOnClickListener implements
            DialogInterface.OnClickListener {

        public void onClick(DialogInterface dialog, int which) {
            JSONObject match = Server.match(id);
            try {
                JSONObject location = (JSONObject)match.get("location");
                int loc_id = location.getInt("user_id");
                Toast.makeText(context, "You are matched to " + loc_id, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Brb something broke.", Toast.LENGTH_LONG).show();
            }
        }
    }
}