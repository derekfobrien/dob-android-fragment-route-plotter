package com.example.fragmentsrouteplotter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.AssetManager;

/*
* This class outlines the fragment containing the text boxes where
* the speed, in kilometres per hour, will be shown (there will be two,
* one showing the whole number, and the other one showing tenths of a km/h,
* latter being in smaller text.
* Two other text boxes will show the time/date, and GPS coordinates.
* There will also be a switch to start/stop logging of GPS stamps.
* */

public class SpeedoFragment extends Fragment {

    private TextView txtSpeedWhole;
    private TextView txtTenthKmh;
    private TextView txtCoords;
    private TextView txtTimeDate;
    private Switch sw;

    public boolean switchStatus;

    SpeedoListener activityCallback;

    // this interface is for communicating with the Main Activity when
    // the Record switch is turned on/off.
    // This method is called in the Main Activity.
    public interface SpeedoListener {
        void onSwitchChanged(boolean checked);
    }

    // this method sets up the fragment when the app is started up
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speedofragment, container, false);

        // Loading in the fonts we are going to use in the fragment
        Typeface dinFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/DiCnBd.ttf");
        Typeface dinReg = Typeface.createFromAsset(getContext().getAssets(), "fonts/DINRg.ttf");

        // inflate the layout for the fragment
        // speed in km/h - left of the decimal point
        txtSpeedWhole = (TextView) view.findViewById(R.id.textSpeedWhole);
        txtSpeedWhole.setTypeface(dinFont);

        // speed in km/h - right of the decimal point, to one place of decimals
        txtTenthKmh = (TextView) view.findViewById(R.id.textSpeedTenths);
        txtTenthKmh.setTypeface(dinFont);

        // latitude and longitude
        txtCoords = (TextView) view.findViewById(R.id.textCoordinates);
        txtCoords.setTypeface(dinReg);

        // time and date
        txtTimeDate = (TextView) view.findViewById(R.id.textTimeDate);
        txtTimeDate.setTypeface(dinReg);

        // switch to turn logging of GPS stamps on or off
        sw = (Switch) view.findViewById(R.id.switchStartStop);
        sw.setTypeface(dinReg);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            // when Switch is turned on or off, the program goes here first
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // call the switchFlicked method
                switchFlicked(isChecked);
            }

        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            activityCallback = (SpeedoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SpeedoListener");
        }
    }

    // this method is called by the onCheckedChanged method, when the switch is turned on or off
    public void switchFlicked(boolean chk) {
        switchStatus = sw.isChecked();
        activityCallback.onSwitchChanged(chk); // this line calls the onSwitchChanged method in the Main Activity
    }

    // this method displays the stamp values in the fragment - called by the onLocationChanged method
    public void displayStampValues(GpsStamp stamp) {
        int speedKmhWhole;
        int speedKmhTenths;
        int speedKmhToOneDecPlace;
        double theSpeed = stamp.getSpeedKmH();
        String strSpeedKmhWhole;
        String strSpeedKmhTenths;

        /*
        * The next three lines create two whole numbers from the number representing the speed
        * One representing the whole number, and the other representing the tenths of a km/h
        * We have to do this separation to put them into separate text boxes, to give them different
        * font sizes and colours
         */
        speedKmhToOneDecPlace = (int)Math.round(theSpeed * 10);
        speedKmhTenths = speedKmhToOneDecPlace % 10;
        speedKmhWhole = (speedKmhToOneDecPlace - speedKmhTenths) / 10;

        /* The next two lines set up the two parts that make up the speed, ready for display into
        * the text boxes */
        strSpeedKmhWhole = Integer.toString(speedKmhWhole) + ".";
        strSpeedKmhTenths = Integer.toString(speedKmhTenths) + " km/h";

        // The next four lines call the methods in the GpsStamp class
        txtSpeedWhole.setText(strSpeedKmhWhole);
        txtTenthKmh.setText(strSpeedKmhTenths);
        txtCoords.setText(stamp.getFullCoords());
        txtTimeDate.setText(stamp.getHumanReadableTimeDate());
    }
}
