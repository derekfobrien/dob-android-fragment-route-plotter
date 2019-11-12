package com.example.fragmentsrouteplotter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ListFragment extends Fragment {

    private static ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listsfragment, container, false);

        lv = (ListView) view.findViewById(R.id.listStamps);
        //tv = (TextView) view.findViewById(R.id.textView2);

        return view;
    }

    public void setListAdapter(StampAdapter a) {
        lv.setAdapter(a);

    }

}
