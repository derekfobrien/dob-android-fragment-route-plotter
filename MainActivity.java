package com.example.fragmentsrouteplotter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.os.Build;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.Buffer;

import java.nio.file.attribute.GroupPrincipal;
import java.util.ArrayList;

// the start of the app
public class MainActivity extends FragmentActivity implements SpeedoFragment.SpeedoListener {

    private LocationManager locationManager;
    private LocationListener listener;
    boolean isRecording = true;
    int numberStamps = 0;

    private StampAdapter sa;

    private String fnJson="MyPlottedPath.json";
    private String fnText="MyPlottedPath.txt";
    private String filepath="MyFileStorage";
    File myExternalFile;
    File myJsonFile;

    private SpeedoFragment sf;
    private ListFragment lf;
    private ArrayList<GpsStamp> gpsStamps = new ArrayList<>();

    // the procedure when the app / Activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(MainActivity.this, "onCreate() method is triggered", Toast.LENGTH_SHORT).show();

        sf = (SpeedoFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentSpeedo);
        lf = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentList);

        myExternalFile = new File(getExternalFilesDir(filepath), fnText);
        myJsonFile = new File(getExternalFilesDir(filepath), fnJson);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        // the procedure when a new location is read
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    // create a new GpsStamp object
                    GpsStamp theStamp = new GpsStamp(location.getTime(), location.getSpeed(), location.getLatitude(), location.getLongitude(), numberStamps);
                    // display the values of the GpsStamp's properties in the SpeedoFragment
                    sf.displayStampValues(theStamp);

                    // if the Switch in the SpeedoFragment is turned on, log the values of the GpsStamps
                    if (isRecording) {
                        numberStamps++;
                        gpsStamps.add(theStamp);

                        // add the latest GpsStamp values to the ListFragment
                        sa = new StampAdapter(MainActivity.this, gpsStamps);
                        lf.setListAdapter(sa);

                        // once there are 12 GpsStamps in the Array, append to the file, and clear the Array
                        if(gpsStamps.size() == 12) {
                            saveStamps();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(getApplicationContext(), "Provider Enabled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configureButton();
    }

    @Override
    public void onStop() {
        // the procedure when the Home button on the tablet is pressed
        saveStamps(); //
        Toast.makeText(MainActivity.this,"onStop method is triggered", Toast.LENGTH_SHORT).show();
        writeJSONFile();
        super.onStop();

    }

    // write whatever GpsStamps are in the Array, into the file before the Activity is shut down
    @Override
    public void onDestroy() {
        //Toast.makeText(MainActivity.this, "onDestroy() method is triggered", Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, "onDestroy() method is triggered", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    // executed following flicking the Switch - called by a line in the switchFlicked() method in the SpeedoFragment class
    public void onSwitchChanged(boolean swVal) {
        isRecording = sf.switchStatus; // 'sf' is the SpeedFragment object declared in one of this class's declarations
        if (isRecording){
            Toast.makeText(MainActivity.this, "Recording is ON", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Recording is OFF", Toast.LENGTH_SHORT).show();
        }

    }

    void configureButton() {
        // check for permissions to access GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
            }
            return;
        }

        // here we set the minimum interval between location updates
        // the second argument in the requestLocationUpdates() method is the minimum interval, in milliseconds
        try {
            locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Problem updating GPS", Toast.LENGTH_LONG).show();
        }
    }

    // writing one or more GpsStamps into the .TXT file - appending - adding to what's already there
    public void saveStamps(){
        if (!myExternalFile.exists()) { // if the file doesn't already exist, create it
            try {
                myExternalFile.createNewFile();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        Toast.makeText(MainActivity.this, "Appending into text file", Toast.LENGTH_SHORT).show();

        // it is here, that we open the file, write in the values of the GpsStamp(s), close the file, then clear the Array
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(myExternalFile, true));
            BufferedWriter bw = new BufferedWriter(osw);

            // the next few lines will write the properties of the GPS stamp into the file, in JSON format
            for (int i = 0; i < gpsStamps.size(); i++) {
                GpsStamp currentStamp = gpsStamps.get(i);
                bw.write(currentStamp.getStampNumber() + "\n");
                bw.write(currentStamp.getHumanReadableTimeDate() + "\n");
                bw.write(currentStamp.getLatitudeTo6DP() + "\n");
                bw.write(currentStamp.getLongitudeTo6DP() + "\n");
                bw.write(currentStamp.getSpeedKmHOneDP() + "\n");
            }
            bw.close();
            gpsStamps.clear();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // this method writes the .JSON file, from the data collated in the .TXT file
    public void writeJSONFile(){
        myExternalFile = new File(getExternalFilesDir(filepath), fnText);
        myJsonFile = new File(getExternalFilesDir(filepath), fnJson);
        BufferedReader br;
        BufferedWriter bwj;
        String strNum, strDate, strLat, strLong, strSpeed;

        // open the Json file
        try{
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(myJsonFile, false));
            bwj = new BufferedWriter(ow);
            bwj.write("[\n");

            // open the text file
            try{
                br = new BufferedReader(new FileReader(myExternalFile));

                strNum = br.readLine();

                while(strNum != null) {
                    strDate = br.readLine();
                    strLat = br.readLine();
                    strLong = br.readLine();
                    strSpeed = br.readLine();

                    // write the data into the Json file, all on one line
                    bwj.write("{");
                    bwj.write("\"id\": " + strNum + ", ");
                    bwj.write("\"datetime\": \"" + strDate + "\", ");
                    bwj.write("\"latitude\": " + strLat + ", ");
                    bwj.write("\"longitude\": " + strLong + ", ");
                    bwj.write("\"speed\": " + strSpeed + "}");
                    strNum = br.readLine(); // read in the next line

                    if (strNum != null) // if we are not at the end of the .TXT file
                    {
                        bwj.write(",\n"); // write a carriage return (new line) in the .JSON file
                    }
                }
                bwj.write("\n]"); // at the end of the .TXT file, put in a carriage return, and then a ']' at the end of the .JSON file
                bwj.close();
                Toast.makeText(MainActivity.this, "Json file is created", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
