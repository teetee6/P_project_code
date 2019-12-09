package org.techtown.myapplication;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.myapplication.ui.home.HomeFragment;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    static private List<BookItem> mBookTempArray;
    static int cur_num;
    static boolean play = false;
    static boolean down = false;
    static MediaPlayer audioPlayer;

    public BookAdapter(List<BookItem> BookList){
        mBookTempArray = BookList;
    }

    OnItemClickListener listener;
    public static interface OnItemClickListener{
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    static public BookItem getItem(int position){
        return mBookTempArray.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bookImg.setImageResource(R.mipmap.ic_action_crop_original); //img src setting
        holder.tBookTitle.setText(mBookTempArray.get(position).getName());
        cur_num=position;
        holder.setOnItemClickListener(listener);
    }



    public int getItemCount(){
        return mBookTempArray.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tBookTitle;
        public CardView cView1;
        public ImageView bookImg;
        public ImageButton btnPlay, btnDown;
        private ttsTask mttsTask;
        String[] mTextString;
        View curView;

        OnItemClickListener listener;

        public ViewHolder(final View itemView){
            super(itemView);
            tBookTitle = (TextView) itemView.findViewById(R.id.tBookTitle);
            bookImg = (ImageView) itemView.findViewById(R.id.bookImg);
            cView1 = (CardView) itemView.findViewById(R.id.cView1);
            btnPlay = (ImageButton) itemView.findViewById(R.id.btnPlay);
            btnDown = (ImageButton) itemView.findViewById(R.id.btnDown);

            btnPlay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //btnPlay click event
                    if(!play){
                        btnPlay.setBackgroundResource(R.mipmap.ic_action_pause_circle_filled);
                        play = true;
                        Log.d("tag","hey1");
                        String text ="안녕하세요오오오오오오";
                        if(text.length()>0){
                            mTextString = new String[]{text};
                            mttsTask = new ttsTask();
                            mttsTask.execute(mTextString);
                            curView = v;
                        }else{
                            //empty text
                        }
                    }
                    else {
                        audioPlayer.pause();
                        btnPlay.setBackgroundResource(R.mipmap.ic_action_play_circle_filled);
                        play = false;
                    }
                    Toast.makeText(v.getContext(), "position" + cur_num, Toast.LENGTH_LONG).show();
                }
            });

            btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!down){
                        btnDown.setBackgroundResource(R.mipmap.ic_action_delete);
                        cView1.setCardBackgroundColor(Color.GRAY);
                        cView1.setBackgroundResource(0);
                        down = true;
                    }
                    else{
                        btnDown.setBackgroundResource(R.mipmap.ic_action_file_download);
                        down = false;
                    }
                }
            });
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
        private class ttsTask extends AsyncTask<String[], Void, String> {
            @Override
            protected String doInBackground(String[]... strings) {
                audioPlayer = TTSAPI.main(mTextString, curView);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }
    }

}