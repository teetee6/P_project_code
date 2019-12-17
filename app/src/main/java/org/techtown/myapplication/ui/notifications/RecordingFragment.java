package org.techtown.myapplication.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.techtown.myapplication.MainActivity;
import org.techtown.myapplication.R;

import java.util.Random;


public class RecordingFragment extends Fragment {

    ViewGroup rootView;
    Handler mhandler;
    String[] name = new String[]{"토끼가 어느날 나타났어요","호랑이가 기어가요","우르르쾅쾅"};




//    final  TextView tv = (TextView) rootView.findViewById(R.id.voice_txt);

    public static RecordingFragment newInstance(){
        return new RecordingFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_recording, container, false);
        //final  TextView tv = (TextView) rootView.findViewById(R.id.voice_txt);

        Button btn =(Button)rootView.findViewById(R.id.record_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView record_gif = (ImageView) rootView.findViewById(R.id.record_image);
                Glide.with(getContext()).load(R.drawable.voice_loading).into(record_gif);
                setHandler(name, 0);

            }
        });

        Button btn2 =(Button)rootView.findViewById(R.id.record_complt);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView record_gif = (ImageView) rootView.findViewById(R.id.record_image);
                Glide.with(getContext()).load(R.drawable.smile).into(record_gif);
                setHandler(name, 1);

            }
        });

        Button return_btn =(Button)rootView.findViewById(R.id.return_button);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).replaceFragment(NotificationsFragment.newInstance());
            }
        });




        return rootView;
    }

    public void setHandler(String[] name,int key){
        final  String[] n = name;
        final int stop_key = key;

        mhandler = new Handler(){
            public void handleMessage(Message msg){
                if(stop_key == 1) {
                    return;
                } else if( stop_key == 0) {
                    final TextView tv = (TextView) rootView.findViewById(R.id.voice_txt);

                    Random rand = new Random();
                    int r = rand.nextInt(n.length);
                    tv.setText(n[r]);

                    mhandler.sendEmptyMessageDelayed(0, 1000);

                }
            }

        };
        boolean bSend = mhandler.sendEmptyMessage(0);


    }



}
