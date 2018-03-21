package com.pipit.agc.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.pipit.agc.R;
import com.pipit.agc.data.InsultRecordsConstants;
import com.pipit.agc.data.MsgAndDayRecords;
import com.pipit.agc.model.DayRecord;
import com.pipit.agc.util.Constants;
import com.pipit.agc.util.NotificationUtil;
import com.pipit.agc.util.ReminderOracle;
import com.pipit.agc.util.SharedPrefUtil;
import com.pipit.agc.util.StatsContent;

import java.util.ArrayList;

/**
 * This fragment displays an option for manually starting and ending visits
 * If user is not at a gym, he will see
 */
public class ManualVisitFragment extends Fragment {

    private static final String TAG = "ManualVisitFragment";
    private Button _startGymVisit;

    public ManualVisitFragment() {
    }


    public static ManualVisitFragment newInstance() {
        ManualVisitFragment fragment = new ManualVisitFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.start_end_visit_layout, container, false);
        TextView openingheader = (TextView) rootview.findViewById(R.id.manualstarttext);
        TextView allvisitsheader = (TextView) rootview.findViewById(R.id.visitstodayexplanation);
        final TextView allvisits = (TextView) rootview.findViewById(R.id.visitstoday);
        TextView countupheader = (TextView) rootview.findViewById(R.id.countuptimerexplanation);

        openingheader.setText("This page allows you to manually start and end visits");
        allvisitsheader.setText("Visits today");
        countupheader.setText("Total gym time");

        final String gymVisitStart = "Start a new visit";
        final String gymVisitEnd = "End current visit";
        _startGymVisit = (Button) rootview.findViewById(R.id.startVisit);

        /*Retrieve data*/
        MsgAndDayRecords datasource;
        datasource = MsgAndDayRecords.getInstance();
        datasource.openDatabase();
        DayRecord today = datasource.getLastDayRecord();
        datasource.closeDatabase();

        //Set countuptimer
        final Chronometer simpleChronometer = (Chronometer) rootview.findViewById(R.id.simpleChronometer); // initiate a chronometer
       /* TODO: These following two lines disable the chronometer. I'm still not sure if chronometer is a good design decision so I'm
       temporarily disabling it
        */
        simpleChronometer.setVisibility(View.GONE);
        countupheader.setVisibility(View.GONE);

        //List of visits
        allvisits.setText(today.printVisits());

        //Set buttons
        if (today.isCurrentlyVisiting()){
            _startGymVisit.setText(gymVisitEnd);
        }
        else{
            _startGymVisit.setText(gymVisitStart);
        }
        _startGymVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgAndDayRecords datasource;
                datasource = MsgAndDayRecords.getInstance();
                datasource.openDatabase();
                DayRecord today = datasource.getLastDayRecord();
                datasource.closeDatabase();

                //Click to end
                if (today.isCurrentlyVisiting()) {
                    if (today.endCurrentVisit()) { //Ends the visit
                        _startGymVisit.setText(gymVisitStart);
                        allvisits.setText(today.printVisits());
                        NotificationUtil.endNotifications(getContext());
                        //String m = ReminderOracle.findANewMessageId(getContext(), InsultRecordsConstants.))
                        //NotificationUtil.showGymVisitingNotification(getContext(), );
                    }else{
                        Toast.makeText(getContext(),
                                getContext().getString(R.string.nogymvisittoend),
                                Toast.LENGTH_SHORT).show();                    }
                }
                //Click to start
                else {
                    today.startCurrentVisit();
                    _startGymVisit.setText(gymVisitEnd);
                    SharedPrefUtil.updateLastVisitTime(getContext(), System.currentTimeMillis());
                    allvisits.setText(today.printVisits());
                    today.setHasBeenToGym(true);
                }
                datasource.openDatabase();
                datasource.updateLatestDayRecordVisits(today.getSerializedVisitsList());
                datasource.updateDayRecordGymStats(today);
                datasource.closeDatabase();

                //We do this outside of the first loop because we need to do database updates
                //before we can show a notification (checks for visiting before showing).
                if (today.isCurrentlyVisiting()){
                    ReminderOracle.conditionalLeaveVisitingMessage(getContext());
                }
            }
        });
        return rootview;
    }
}
