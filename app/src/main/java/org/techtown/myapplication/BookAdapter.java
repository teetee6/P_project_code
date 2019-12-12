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

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.techtown.myapplication.ui.home.HomeFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    static private List<BookItem> mBookTempArray;
    static int cur_num;
    static boolean play = false;
    static boolean down = false;
    static MediaPlayer audioPlayer;
    static HashMap<String,String> serverData;
    static String mood;
    static  String[] bookData;
    //패턴 기분 : 긍적적 : 2 부정적 : 1 걍 그럼 :3


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
                    if(down) {
                        if (!play) {
                            btnPlay.setBackgroundResource(R.mipmap.ic_action_pause_circle_filled);
                            audioPlayer.start();
                            play = true;
                        } else {
                            audioPlayer.pause();
                            btnPlay.setBackgroundResource(R.mipmap.ic_action_play_circle_filled);
                            play = false;
                        }
                        Toast.makeText(v.getContext(), "position" + cur_num, Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(v.getContext(), "책을 먼저 다운로드 해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    try {
                        for(int i =0; i<bookData.length; i++) {
                        CustomTask2 customtask2 = new CustomTask2();

                            customtask2.execute(bookData[i]).get();
                            System.out.println(bookData[i]);

                             System.out.println(mood);
                        }
                        //                        //sd카드에서 데이터 가져오기
                        //                        //인자로 넣어주기
                        //                        //for문
                        //                        //for문 안에서 무드 별

                    }catch (Exception e){

                    }


                    Toast.makeText(v.getContext(), "position" + cur_num, Toast.LENGTH_LONG).show();
                }
            });

            btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        CustomTask customtask = new CustomTask();
                        serverData = customtask.execute("rabbit").get();
                        //인자 넣어주기
                        //sd카드에 넣기
                        // String text ="안녕하세요" -> 파일에서 읽어와서 저장하는 거
                        //이때 서버 열어야함
                        bookData = (serverData.get("data")).split("\\$");
                        System.out.println(bookData[0]);
                        //캐릭터 같이 있는건 serverData이고 책 내용 쪼개논 배열은 bookData

                    }catch (Exception e){

                    }

                    if(!down){
                        btnDown.setBackgroundResource(R.mipmap.ic_action_delete);
                        cView1.setCardBackgroundColor(Color.GRAY);
                        cView1.setBackgroundResource(0);
                        down = true;
                        Log.d("tag","hey1");
                        String text ="안녕하세요";//for test
                        if(text.length()>0){
                            mTextString = new String[]{text};
                            mttsTask = new ttsTask();
                            mttsTask.execute(mTextString);//set page array
                            curView = v;
                        }else{
                            //empty text
                        }
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

        private class ttsTask extends AsyncTask<String[], Void, HashMap> {
            HashMap hashMap;
                @Override
                protected HashMap doInBackground(String[]... strings) {
                audioPlayer = TTSAPI.main(mTextString, curView);
                audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnPlay.setBackgroundResource(R.mipmap.ic_action_play_circle_filled);
                        play = false;
                    }
                });
                return hashMap;
            }

            @Override
            protected void onPostExecute(HashMap s) {
                super.onPostExecute(s);
            }
        }

        class CustomTask extends AsyncTask<String, Void, HashMap> {
            String sendMsg, receiveMsg;
            HashMap<String,String> hashmap;
            @Override
            protected HashMap doInBackground(String... strings) {
                try {
                    String str;
                    URL url = new URL("http://192.168.219.104:8080/gjavaweb4/bookdata2.jsp");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                    sendMsg = "type="+strings[0];
                    osw.write(sendMsg);
                    osw.flush();
                    if(conn.getResponseCode() == conn.HTTP_OK) {
                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuffer buffer = new StringBuffer();
                        while ((str = reader.readLine()) != null) {
                            buffer.append(str);
                        }
                        receiveMsg = buffer.toString();
                        Document doc = Jsoup.parse(receiveMsg);
                        System.out.println(doc.text());
                        receiveMsg= doc.text();
                        try{
                            JSONObject jsonObject = new JSONObject(receiveMsg);
                            String char1 = jsonObject.getString("char1");
                            String char2 = jsonObject.getString("char2");
                            String data = jsonObject.getString("data");
                            hashmap = new HashMap<>();
                            hashmap.put("char1",char1);
                            hashmap.put("char2",char2);
                            hashmap.put("data",data);
                            System.out.println(hashmap);

                        }catch (Exception e){
                            Log.d("error","못읽어옴");


                        }


                    } else {
                        Log.i("통신 결과", conn.getResponseCode()+"에러");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return hashmap;

            }
        }


        class CustomTask2 extends AsyncTask<String, Void, Void> {
            String sendMsg, receiveMsg;
            @Override
            protected Void doInBackground(String... strings) {
                try {
                    String str;
                    URL url = new URL("https://dev-api.maum.ai/api/hmd/");


                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    Log.d("error", " conn");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                    JSONObject requestdata = new JSONObject();
                    Log.d("error", "json object");
                    sendMsg = strings[0];
                    try {
                        requestdata.put("apiId", "gachon.pproject.6728d71a1dc3c");
                        requestdata.put("apiKey", "ed13742e94834e339b70dc8a29a2142f");
                        requestdata.put("lang", "kor");
                        requestdata.put("reqText", sendMsg);
                        Log.d("error", "json");

                    } catch (JSONException e) {
                        Log.d("error", "변환 문제");
                    }
                    osw.write(String.valueOf(requestdata));

                    osw.flush();

                    if (conn.getResponseCode() == conn.HTTP_OK) {
                        Log.d("error", "http ok");
                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuffer buffer = new StringBuffer();
                        while ((str = reader.readLine()) != null) {
                            Log.d("error", "buffer");
                            buffer.append(str);
                        }
                        System.out.println(buffer);
                        String pattern = "0";
                        try {
                            JSONObject jsonObject = new JSONObject(buffer.toString());


                                pattern = jsonObject.getJSONArray("cls").getJSONObject(0).getString("category");


                        } catch (JSONException e) {

                        }
                        Log.d("category", pattern);
                        if (pattern.indexOf("Positive") != -1) {
                            System.out.println("긍정적인 페이지 입니다.");
                            mood = "2";

                        } else if (pattern.indexOf("Negative") != -1) {
                            System.out.println("부정적인 페이지 입니다.");
                            mood = "1";
                        }else {
                            mood="3";
                        }


                    } else {
                        Log.i("통신 결과", conn.getResponseCode() + "에러");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;


          }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }


    }
}
