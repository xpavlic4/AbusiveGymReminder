package com.pipit.agc.agc.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pipit.agc.agc.fragment.DayPickerFragment;
import com.pipit.agc.agc.fragment.LogFragment;
import com.pipit.agc.agc.R;
import com.pipit.agc.agc.fragment.PreferencesFragment;
import com.pipit.agc.agc.fragment.TestDBFragmentDays;
import com.pipit.agc.agc.fragment.TestDBFragmentMessages;

/**
 * This class wraps the fragment that is shown for each indivudal setting
 * So
 *    SettingsActivity
 */
public class IndividualSettingActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individualsettinglayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String fragname = getIntent().getStringExtra("fragment");
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Fragment frag = new Fragment();
            switch (fragname) {
                case "LogFragment":
                    frag = LogFragment.newInstance(0);
                    break;
                case "TestDBFragmentMessages":
                    frag = TestDBFragmentMessages.newInstance(1);
                    break;
                case "PreferencesFragment":
                    frag = PreferencesFragment.newInstance();
                    break;
                case "DayPickerFragment":
                    frag = DayPickerFragment.newInstance(3);
                    break;
                default:
                    finish();
            }
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, frag).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
