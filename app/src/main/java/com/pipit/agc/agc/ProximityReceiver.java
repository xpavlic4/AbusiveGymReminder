package com.pipit.agc.agc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager;
import android.util.Log;

import com.pipit.agc.agc.data.DBRecordsSource;

import java.text.DateFormat;
import java.util.Date;

public class ProximityReceiver extends BroadcastReceiver {
    public static final String EVENT_ID_INTENT_EXTRA = "EventIDIntentExtraKey";

    @Override
    public void onReceive(Context context, Intent arg1) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Log.d("Alarm", "onReceive of PROXIMITY RECEIVER + intent action" + arg1.getAction());

        String k = LocationManager.KEY_PROXIMITY_ENTERING;
        boolean state = arg1.getBooleanExtra(k, false);

        String enterOrLeave;
        if (state) {
            //Todo: Enter state
            enterOrLeave="entered";
            updateLastDayRecord("Went to gym");
        } else {
            //Todo: Exit state
            enterOrLeave="exited";
        }

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = prefs.edit();
        double lat = Util.getDouble(prefs, "lat", 0);
        double lng = Util.getDouble(prefs, "lng", 0);
        Location gymLocation = new Location("");
        gymLocation.setLatitude(lat);
        gymLocation.setLongitude(lng);
        Location currLocation =  getCurrentLocation(context);
        double distance = gymLocation.distanceTo(currLocation);
        String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        String lastLocation = prefs.getString("locationlist", "none");
        String body = lastLocation+"\nProximity Alert " + enterOrLeave + " at " +
                mLastUpdateTime + " distance " + distance;
        editor.putString("locationlist", body);
        Log.d("Alarm", "Proximity Alert just entered last location into sharedprefs");
        Util.sendNotification(context, "Location Update", "Entered proximity at " + mLastUpdateTime);
        editor.commit();
        wl.release();
    }

    private Location getCurrentLocation(Context context){
        LocationManager locationManager;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        String gpsMsg = "";
        Location lc = new Location("");

        if (null != provider)
        {
            Location location = locationManager.getLastKnownLocation(provider);
            if (null != location) {
                double currentLat = location.getLatitude();
                double currentLon = location.getLongitude();
                gpsMsg = "curr location is " + currentLat + " " + currentLon;
                lc.setLongitude(currentLon);
                lc.setLatitude(currentLat);
            }
            else
            {
                gpsMsg="Current Location can not be resolved!";
            }

        }
        else
        {
            gpsMsg="Provider is not available!";
        }
        return lc;
    }

    private void updateLastDayRecord(String comment){
        DBRecordsSource datasource = DBRecordsSource.getInstance();
        datasource.openDatabase();
        datasource.updateLatestDayRecord(comment);
        DBRecordsSource.getInstance().closeDatabase();




    }
}