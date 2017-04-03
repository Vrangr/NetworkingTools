package com.himanshu.navigationdrawer.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.himanshu.navigationdrawer.R;
import com.himanshu.navigationdrawer.activity.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IfconfigFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IfconfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IfconfigFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View v;
    Activity mActivity;
    int state;
    private OnFragmentInteractionListener mListener;

    public IfconfigFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IfconfigFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IfconfigFragment newInstance(String param1, String param2) {
        IfconfigFragment fragment = new IfconfigFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_ifconfig, container, false);
        Button b = (Button)v.findViewById(R.id.ifconfig_button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vpi) {
                ipFind(v);
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
        mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    /***************************************************************************************/

    void ipFind(View v)
    {
        ConnectivityManager cm = (ConnectivityManager)
                mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected

        if (networkInfo != null && networkInfo.isConnected()) {
            state = 1;
            ConnectivityManager cm2 = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = cm2.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(mWifi.isConnected())
            {
                state = 2;
            }
        }
        else
            state = 0;

        TextView op = (TextView)v.findViewById(R.id.ifconfig_op);
        Log.v("IfConfig o/p ", String.valueOf(op));
        threadt t = new threadt(op,0,0,mActivity,state);
        Thread tt = new Thread(t);
        tt.start();
    }

}

class threadt extends MainActivity implements Runnable
{
    BufferedReader br;
    TextView tv;
    String s,ss;
    StringBuilder sb;
    ProgressBar spinnerr;
    StringBuilder tracker;
    Activity mActivity;
    int value,state;
    int flag =0,cancel=0;

    @Override
    public View findViewById(@IdRes int id) {
        return super.findViewById(id);
    }
    public threadt(TextView tvv,int v,int ct,Activity ac,int stat)
    {
        tv = tvv;
        value = v;
        cancel = ct;
        mActivity = ac;
        state = stat;
    }

    public void run()
    {
        try
        {
            sb = new StringBuilder();
            tracker = new StringBuilder();
            String command = getCommand(state);

            System.out.println(command);

            if(command!=null && command.length()>0)
            {
                java.lang.Process pp = Runtime.getRuntime().exec(command);

                br = new BufferedReader(new InputStreamReader(pp.getInputStream()));
                while ((s = br.readLine()) != null) {
                    if (s.length() > 0 && s.contains("RX bytes")) {
                        tracker.append("\n\n"+s);
                        break;
                    }
                    else
                    {
                        tracker.append("\n\n" +s);
                        if(s.contains("inet addr"))
                        {
                            ss = new String(s);
                        }
                    }

                }

                if(tracker.capacity()>0)
                {
                    value =1;
                }

            }
            System.out.println("end");

        } catch (IOException e)
        {
            e.printStackTrace();
        }


        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (value == 1)
                    tv.setText(sb.toString()+tracker.toString()+"\n");
                else
                {
                    if(flag == 1) {
                        tv.setText(sb.toString()/*+"\n"+"Ip :"+ss*/+tracker.toString()+"\n" );
                    }
                    else
                    {
                        tv.setText("WIFI Disconnected");
                    }
                }
                try {
                    spinnerr.setVisibility(View.INVISIBLE);
                    tv.setVisibility(View.VISIBLE);
                }
                catch(Exception e)
                {
                    System.out.println(spinnerr+" Here thread "+tv);
                }
            }
        });

    }


    String getCommand(int state)
    {
        String command = "/system/bin/ifconfig ";

        if(state==1)
        {
            command =command + "rmnet_data0";
        }
        else if(state==2)
        {
            command =command + "wlan0";
        }
        else
            command = null;

        return command;
    }


}
