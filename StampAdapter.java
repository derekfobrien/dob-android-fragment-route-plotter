package com.example.fragmentsrouteplotter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

// this class manages the data in the GpsStamp objects, and adapts it to the individual items in the List Fragment
public class StampAdapter extends BaseAdapter {
    private ArrayList<GpsStamp> gpsStamps;
    private LayoutInflater layoutInflater;

    public StampAdapter(Context context, ArrayList<GpsStamp> list) {
        this.gpsStamps = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return gpsStamps.size();
    }

    @Override
    public Object getItem(int position) {
        return gpsStamps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // here is where we get the data in the GpsStamp class and put it into the TextView items in the ListItem widget
    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder vh;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.listitem, null);
            vh = new ViewHolder();
            vh.theTime = (TextView) v.findViewById(R.id.textTime);
            vh.theCoords = (TextView) v.findViewById(R.id.textLocation);
            vh.theSpeed = (TextView) v.findViewById(R.id.textSpeed);
            vh.theIndex = (TextView) v.findViewById(R.id.textIndex);
        } else {
            vh = (ViewHolder) v.getTag();
        }

        GpsStamp currentStamp = gpsStamps.get(position);

        vh.theTime.setText(currentStamp.getHumanReadableTimeDate());
        vh.theCoords.setText(currentStamp.getFullCoords());
        vh.theSpeed.setText(currentStamp.getSpeedKmHString());
        vh.theIndex.setText(Integer.toString(currentStamp.getStampNumber()));
        return v;
    }

    static class ViewHolder {
        TextView theTime;
        TextView theCoords;
        TextView theSpeed;
        TextView theIndex;
    }

}
