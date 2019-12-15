package org.techtown.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.Buffer;
import java.security.spec.ECField;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.techtown.myapplication.ui.home.HomeFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.core.graphics.drawable.IconCompat.*;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    static private List<BookItem> mBookTempArray;
    static Context context;
    static int cur_num, cur_page;
    static boolean play = false;
    static boolean down = false;
    static MediaPlayer audioPlayer = new MediaPlayer();
    static MediaPlayer player;
    static HashMap<String, String> serverData;
    static String mood;
    static String[] bookData;
    static boolean complete = false;
    static String tempName;
    //패턴 기분 : 긍적적 : 2 부정적 : 1 걍 그럼 :3
    static Bitmap bitmap = null;
    static final String rabbit = "https://post-phinf.pstatic.net/MjAxNzA0MjNfMjAw/MDAxNDkyOTU3MjU4ODg1.gLkHHCf0BIWd2MtdwK324RVEdgLgCLuijrz1kq9nNIcg.BJnf1whFOJWSrhw4oWo8Sqs5XgZVq4rH4OJuLuFVm0wg.JPEG/01.JPG?type=w1200";
    static final String cat = "https://c8.alamy.com/comp/P9WXB1/nystrm-jenny-a-boy-and-a-cat-on-the-bench-on-a-sunny-day-P9WXB1.jpg";


    public BookAdapter(List<BookItem> BookList, Context c) {

        mBookTempArray = BookList;
        this.context = c;

    }

    OnItemClickListener listener;

    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    static public BookItem getItem(int position) {
        return mBookTempArray.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//img src setting
        holder.tBookTitle.setText(mBookTempArray.get(position).getName());
        cur_num = position;
        holder.setOnItemClickListener(listener);
    }


    public int getItemCount() {
        return mBookTempArray.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tBookTitle;
        public CardView cView1;
        public ImageView bookImg;
        public ImageButton btnPlay, btnDown;
        private ttsTask mttsTask;
        String[] mTextString;
        View curView;

        OnItemClickListener listener;

        public ViewHolder(final View itemView) {
            super(itemView);
            tBookTitle = (TextView) itemView.findViewById(R.id.tBookTitle);
            bookImg = (ImageView) itemView.findViewById(R.id.bookImg);
            cView1 = (CardView) itemView.findViewById(R.id.cView1);
            btnPlay = (ImageButton) itemView.findViewById(R.id.btnPlay);
            btnDown = (ImageButton) itemView.findViewById(R.id.btnDown);

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //btnPlay click event
                    if (down) {
                        if (!play) {
                            btnPlay.setBackgroundResource(R.mipmap.ic_action_pause_circle_filled);
                            play = true;
                            setPlayList(serverData.get("title_server"), 0);//TODO: set title
                            //if(complete){};
                            for (int i = 0; i < bookData.length; i++) {
                                try {
                                    CustomTask2 customtask2 = new CustomTask2();
                                    customtask2.execute(bookData[i]).get();
                                    System.out.println(bookData[i]);
                                    System.out.println(mood);
                                    BackgoundSound backgoundSound = new BackgoundSound();
                                    backgoundSound.execute(mood).get();
                                    audioPlayer.start();

                                    int duration = audioPlayer.getDuration();
                                    try {
                                        Thread.sleep(duration + 100);
                                        player.pause();
                                    } catch (InterruptedException e) {
                                    }


                                } catch (Exception e) {
                                }


                            }

                        } else {
                            audioPlayer.pause();
                            btnPlay.setBackgroundResource(R.mipmap.ic_action_play_circle_filled);
                            play = false;
                        }
                        Toast.makeText(v.getContext(), "position" + cur_num, Toast.LENGTH_LONG).show();

                    } else {//TODO: check whether file is downloaded or not
                        Toast.makeText(v.getContext(), "책을 먼저 다운로드 해주세요.", Toast.LENGTH_SHORT).show();
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

                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            try {
                                String tempName = serverData.get("title_server");

                                File dir = new File(Environment.getExternalStorageDirectory() + "/TTS/" + tempName);
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                    Log.d("Tag", "mkdirs");
                                }


                                //여기 나중에 로직으로 변경해야함 어떤 책인지에 따라서
                                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "TTS/" + tempName + "/" + tempName + ".txt");
                                FileWriter fw = new FileWriter(f, false);
                                fw.write(bookData.toString());
                                fw.close();
                                f.createNewFile();
                                Log.d("Tag", "rabbit/rabbit.txt 생성");


                            } catch (Exception e) {


                            }


                        }
                        //external storage에 bookData 저장하는 코드

                        imageBack task = new imageBack();
                        task.execute().get();
                        //image가져오는 코드!
                    } catch (Exception e) {

                    }

                    if (!down) {//TODO: check book data. if it exists
                        btnDown.setBackgroundResource(R.mipmap.ic_action_delete);
                        cView1.setCardBackgroundColor(Color.GRAY);
                        cView1.setBackgroundResource(0);
                        down = true;
                        //Log.d("tag","hey1");
                        String text =bookData[0];
                        if(text.length()>0){
                            mTextString = new String[]{text};
                            mttsTask = new ttsTask();
                            mttsTask.execute(mTextString);
                            curView = v;
                        } else {
                            //empty text
                        }
                    } else {
                        btnDown.setBackgroundResource(R.mipmap.ic_action_file_download);
                        down = false;
                        //TODO: delete book data

                    }
                }
            });
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        private class ttsTask extends AsyncTask<String[], Void, String> {
            @Override
            protected String doInBackground(String[]... strings) {//6 pages
                for (int i = 0; i < bookData.length; i++) {
                    mTextString = new String[]{bookData[i]};//set next page
                    TTSAPI.main(mTextString, serverData.get("title_server"), i, curView);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(curView.getContext(), "전부 다운로드 하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        class CustomTask extends AsyncTask<String, Void, HashMap> {
            String sendMsg, receiveMsg;
            HashMap<String, String> hashmap;

            @Override
            protected HashMap doInBackground(String... strings) {
                try {
                    String str;
                    URL url = new URL("http://10.0.2.2:8080/gjavaweb/addrbook/server.jsp");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                    sendMsg = "type=" + strings[0];
                    osw.write(sendMsg);
                    osw.flush();
                    if (conn.getResponseCode() == conn.HTTP_OK) {
                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuffer buffer = new StringBuffer();
                        while ((str = reader.readLine()) != null) {

                            buffer.append(str);
                        }
                        receiveMsg = buffer.toString();
                        Document doc = Jsoup.parse(receiveMsg);
                        System.out.println(doc.text());
                        receiveMsg = doc.text();
                        try {
                            JSONObject jsonObject = new JSONObject(receiveMsg);
                            String char1 = jsonObject.getString("char1");
                            String char2 = jsonObject.getString("char2");
                            String data = jsonObject.getString("data");
                            String title = jsonObject.getString("title");
                            String title_server = jsonObject.getString("title_server");


                            hashmap = new HashMap<>();
                            hashmap.put("char1", char1);
                            hashmap.put("char2", char2);
                            hashmap.put("data", data);
                            hashmap.put("title", title);
                            hashmap.put("title_server",title_server);

                            System.out.println(hashmap);

                        } catch (Exception e) {
                            Log.d("errort1", "못읽어옴");


                        }
                    } else {
                        Log.i("통신 결과", conn.getResponseCode() + "에러");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return hashmap;
            }
        }

        private class imageBack extends AsyncTask<String, Integer, Bitmap> {


            @Override
            protected Bitmap doInBackground(String... urls) {
                // TODO Auto-generated method stub
                try{
                    URL myFileUrl = new URL(rabbit);


                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();

                    bitmap = BitmapFactory.decodeStream(is);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            protected void onPostExecute(Bitmap img) {
                bookImg.setImageBitmap(bitmap);
            }

        }


        private class BackgoundSound extends AsyncTask<String, Integer,Void>{
            @Override
            protected Void doInBackground(String... strings) {
                // TODO Auto-generated method stub
                Random random = new Random();
                int randomValue = random.nextInt(3);
                mood = strings[0];
                if(mood.equals("1")){
                    switch(randomValue){
                        case 0:
                            player = MediaPlayer.create(context, R.raw.n0);
                            break;
                        case 1:
                            player = MediaPlayer.create(context, R.raw.n1);
                            break;
                        case 2:
                            player = MediaPlayer.create(context, R.raw.n2);
                            break;
                        default:
                            break;


                    }
                }else if(mood.equals("2")){
                    switch(randomValue){
                        case 0:
                            player = MediaPlayer.create(context, R.raw.p0);
                            break;
                        case 1:
                            player = MediaPlayer.create(context, R.raw.p1);
                            break;
                        case 2:
                            player = MediaPlayer.create(context, R.raw.p2);
                            break;
                        default:
                            break;


                    }
                }else if(mood.equals("3")){
                    switch(randomValue){
                        case 0:
                            player = MediaPlayer.create(context, R.raw.m0);
                            break;
                        case 1:
                            player = MediaPlayer.create(context, R.raw.m1);
                            break;
                        case 2:
                            player = MediaPlayer.create(context, R.raw.m2);
                            break;
                        default:
                            break;


                    }
                }
                player.setVolume((float) 0.48,(float) 0.48);
                player.start();
                play = true;
                return null;
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
                        } else {
                            mood = "3";
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

    public static void setPlayList(String t, int page) {
        try {
            complete = false;
            cur_page = page;
            String tempName = t + page;
            String path = Environment.getExternalStorageDirectory() + File.separator + "TTS/" + t + "/" + tempName + ".mp4";//TODO: set title
            Log.d("tag", path);
            audioPlayer = new MediaPlayer();
            audioPlayer.setDataSource(path);
            audioPlayer.prepare();
            audioPlayer.start();
            audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    audioPlayer.reset();
                    cur_page++;
                    if (cur_page < bookData.length) {
                        setPlayList(serverData.get("title_server"), cur_page);
                    } else {
                        complete = true;
                        Log.d("status", Boolean.toString(complete));
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
