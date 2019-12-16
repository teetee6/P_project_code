package org.techtown.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
    static String[] bookData, moodData;
    static boolean complete = false;
    static String tempName;
    static Activity activity;
    static ProgressBar progress;
    //패턴 기분 : 긍적적 : 2 부정적 : 1 걍 그럼 :3
    static Bitmap bitmap = null;
    static SQLiteDatabase sqLiteDatabase;
    static DatabaseHelper dbHelper;

    public BookAdapter(List<BookItem> BookList, Context c) {
        mBookTempArray = BookList;
        this.context = c;
        activity = (Activity)c;
    }

    OnItemClickListener listener;
    public static interface OnItemClickListener{
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
        ArrayList<Nickname> items = new ArrayList<Nickname>();
        public Button clear_button, save_button;

        OnItemClickListener listener;

        public ViewHolder(final View itemView) {
            super(itemView);
            tBookTitle = (TextView) itemView.findViewById(R.id.tBookTitle);
            bookImg = (ImageView) itemView.findViewById(R.id.bookImg);
            cView1 = (CardView) itemView.findViewById(R.id.cView1);
            btnPlay = (ImageButton) itemView.findViewById(R.id.btnPlay);
            btnDown = (ImageButton) itemView.findViewById(R.id.btnDown);
            dbHelper = new DatabaseHelper(itemView.getContext());
            sqLiteDatabase = dbHelper.getWritableDatabase();
            File dir = new File(Environment.getExternalStorageDirectory() + "/TTS/" + "rabbit");//TODO: get title_server from bookItem
            if(dir.exists()) {
                down = true;
                btnDown.setBackgroundResource(R.mipmap.ic_action_delete);
            }
            else
                down = false;

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //btnPlay click event
                    if (down) {
                        if (!play) {
                            btnPlay.setBackgroundResource(R.mipmap.ic_action_pause_circle_filled);
                            play = true;
                            //if(complete){};
                            for (int i = 0; i < bookData.length; i++) {
                                try {
                                    CustomTask2 customtask2 = new CustomTask2();
                                    customtask2.execute(bookData[i]).get();
                                    System.out.println(bookData[i]);
                                    System.out.println(mood);
                                    moodData[i] = mood;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            setPlayList(serverData.get("title_server"), 0, moodData[0]);

                        } else {
                            audioPlayer.pause();
                            player.pause();
                            btnPlay.setBackgroundResource(R.mipmap.ic_action_play_circle_filled);
                            play = false;
                        }
                    } else {
                        Toast.makeText(v.getContext(), "책을 먼저 다운로드 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, itemView, position);
                        Toast.makeText(v.getContext(),"position: "+position,Toast.LENGTH_SHORT).show();
                        cur_num = position;
                        Log.d("tag","cur_num: "+cur_num);
                    }
                }

            });

            btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get book file from server and put it in external storage
                    if (!down) {
                        btnDown.setBackgroundResource(R.mipmap.ic_action_delete);
                        down = true;
                        progress = new ProgressBar(activity);
                        progress.showDialog();

                        try {
                            CustomTask customtask = new CustomTask();
                            serverData = customtask.execute("rabbit").get();//TODO: set server_title from bookItem
                            bookData = (serverData.get("data")).split("\\$");
                            moodData = new String[bookData.length];

                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                try {
                                    String tempName = "rabbit";//TODO: get this programmatically

                                    File dir = new File(Environment.getExternalStorageDirectory() + "/TTS/" + tempName);
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                        Log.d("Tag", "mkdirs");
                                    }

                                    //여기 나중에 로직으로 변경해야함 어떤 책인지에 따라서
                                    Log.d("Tag", "rabbit/rabbit.txt 생성");
                                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "TTS/" + tempName + "/" + tempName + ".txt");
                                    FileWriter fw = new FileWriter(f, false);
                                    fw.write(bookData.toString());
                                    fw.close();
                                    f.createNewFile();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            imageBack task = new imageBack();
                            task.execute().get();
                            //image가져오는 코드!
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
                        File dir = new File(Environment.getExternalStorageDirectory() + "/TTS/" + "rabbit");//TODO: get this from bookItem
                        if (dir.isDirectory())
                        {
                            String[] children = dir.list();
                            for (int i = 0; i < children.length; i++)
                            {
                                new File(dir, children[i]).delete();
                            }
                            dir.delete();
                            Log.d("tag","Successfully deleted whole file of "+"rabbit");
                        }
                    }
                }
            });

            cView1.setOnLongClickListener(new Button.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    Cursor cursor = sqLiteDatabase.rawQuery(
                            "select * from book", null);
                    int recordCount = cursor.getCount();
                    Log.d("Real_Real_db반환갯수", String.valueOf(recordCount));
                    for (int i = 0; i < recordCount; i++) {
                        cursor.moveToNext();
                        int id = cursor.getInt(0);
                        String book_nation = cursor.getString(1);
                        String book_name = cursor.getString(2);
                        String book_char = cursor.getString(3);
                        String book_nick = cursor.getString(4);
                        Log.d("Real_Real_db반환값", id + "  " + book_nation + "  " +book_name + "  " +book_char + "  " +book_nick);
                    }
                    // serverData 해쉬함수의, "char1" "char2" "data" 에 맞춰,
                    // "nick1" "nick2" (갯수만큼 동적생성됨) "changed_data" 가 생성됩니다.
                    char_change_to_nick();
                    return false;
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
                    TTSAPI.main(mTextString, serverData.get("title_server"), i, serverData.get("type"),curView);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(curView.getContext(), "전부 다운로드 하였습니다.", Toast.LENGTH_SHORT).show();
                progress.hideDialog();
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
                            String type = jsonObject.getString("type");
                            String imgRsc = jsonObject.getString("img");

                            hashmap = new HashMap<>();
                            hashmap.put("char1", char1);
                            hashmap.put("char2", char2);
                            hashmap.put("data", data);
                            hashmap.put("title", title);
                            hashmap.put("title_server",title_server);
                            hashmap.put("type",type);
                            hashmap.put("imgRsc",imgRsc);

                            if (sqLiteDatabase == null) {
                                Log.d("Real_Real", "db생성안됨");
                            } else {
                                Log.d("Real_Real", "db생성됨");
                            }

                            for(int i=1; hashmap.containsKey("char"+i); i++ ) {
                                sqLiteDatabase.execSQL(
                                        "insert into book(book_nation, book_name, book_char, book_nick)"
                                                + "values ('kor','rabbit_story', '"+ hashmap.get("char"+i)+"' , '"+hashmap.get("char"+i)+"' )"
                                );
                            }

                            serverData=hashmap;

                            System.out.println(hashmap);

                        } catch (Exception e) {
                            Log.d("tag", "못읽어옴");


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
                try{
                    URL myFileUrl = new URL(serverData.get("imgRsc").replace("\'",""));


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
                        requestdata.put("apiId", R.string.apiId);
                        requestdata.put("apiKey", R.string.apiKey);
                        requestdata.put("lang", "kor");//TODO: set type programmatically
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

        private void char_change_to_nick(){
            final String data = serverData.get( "data" );
            for(int i=1; serverData.containsKey("char"+i); i++ ) {
                items.add(new Nickname( serverData.get("char"+i) ) );
            }
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            final View cccardview= inflater.inflate(R.layout.card_of_change_name, null);
            LinearLayout addcard = cccardview.findViewById(R.id.addCard);
            int counting = items.size();
            final TextView[] row_character = new TextView[counting];  // 동적 생성될 캐릭터 칸
            final EditText[] row_nickname = new EditText[counting]; // 동적 생성될 닉네임 칸
            for(int i=0; i<counting; i++ ) {
                LinearLayout row_row_row = new LinearLayout(itemView.getContext());
                row_row_row.setOrientation(LinearLayout.HORIZONTAL);
                row_character[i] = new TextView(itemView.getContext());
                row_character[i].setText( items.get(i).character );
                row_row_row.addView(row_character[i]);
                row_nickname[i] = new EditText(itemView.getContext());
//                row_nickname[i].setHint("이곳에 입력하세요");
                row_nickname[i].setHint("[ "+items.get(i).character+" ]");

                Cursor cursor1;
                int j = i+1;
                cursor1 = sqLiteDatabase.rawQuery(" SELECT book_nick FROM book " +
                                "WHERE " +
                                "book_char='" + serverData.get("char" + j) + "' " +
                                "AND " +
                                "book_nick!='" + serverData.get("char" + j) + "' "
                        , null);
                int record_Count = cursor1.getCount();

                if( record_Count == 1 ) {
                    cursor1.moveToNext();

                    Log.d("tag","djfudj");
                    String book_nick = cursor1.getString(0);
                    row_nickname[i].setText(book_nick);
                    Log.d("Real_Real_최종최종//db반환값", book_nick);
                }
                row_row_row.addView(row_nickname[i]);
                addcard.addView(row_row_row);
            }

            AlertDialog.Builder builder= new AlertDialog.Builder(itemView.getContext()); //AlertDialog.Builder 객체 생성
            builder.setTitle("동화속 캐릭터의 이름을 바꾸어봐요");
            builder.setIcon(android.R.drawable.ic_menu_add);
            builder.setView(cccardview);
            Log.d("Real_Real_error--story", "tototo_1-5");
            builder.setNeutralButton("돌아가기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if( serverData.containsKey("changed_data") ) {
                        bookData = serverData.get("changed_data").split("\\$");
                        for(String page : bookData) {
                            Log.d("Real_Real 변환된 배열값:", page);
                        }
                    } else {
                        serverData.put("changed_data", serverData.get("data"));
                        bookData = serverData.get("changed_data").split("\\$");
                        for(String page : bookData) {
                            Log.d("Real_Real 변환된 배열값:", page);
                        }
                    }
                    items.clear();
                }
            });

            builder.setPositiveButton("저장 후 돌아가기", new DialogInterface.OnClickListener() {       // 돌아갈 땐, " serverData에 changed_data(변환된 문장)만 반환합니다 "
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if( serverData.containsKey("changed_data") ) {
                        serverData.remove("changed_data");  ////
                    }
                    for (int i = 0; i < items.size(); i++) {
                        if (row_nickname[i].getText().toString().equals("")) { // 닉네임 입력칸을 비워뒀으면
                            items.get(i).setNickname(row_character[i].getText().toString());  // 데이터객체의 닉네임 값에, 캐릭터 입력칸의 값 저장 // 캐릭터:tiger -> 닉네임:tiger
                        } else {
                            items.get(i).setNickname(row_nickname[i].getText().toString()); // 데이터객체의 닉네임 값에, 닉네임 입력칸의 값 저장  // 캐릭터:tiger -> 닉네임:trump
                        }
                        serverData.put("nick" + (i + 1), items.get(i).getNickname());
                    }
                    String s1 = "";
                    String changed = data;
                    for (int i = 0; i < items.size(); i++) {
                        s1 = items.get(i).getCharacter();  // 데이터객체의 캐릭터 이름 한번씩 훑음
                        if (data.contains(s1)) {        // 원문에서 캐릭터 이름이 있다면
                            changed = changed.replace(s1, items.get(i).getNickname()); // 캐릭터 이름을 닉네임 이름으로 변환함.
                        }
                    }
                    // 최종 변환된 문장은 changed 변수입니다.
                    serverData.put("changed_data", changed);
                    Log.d("Real_Real_error-bef_str", serverData.get("data"));
                    Log.d("Real_Real_error-aft_str", serverData.get("changed_data"));
                    for (int i = 1; serverData.containsKey("char"+i) ; i++) {
                        sqLiteDatabase.execSQL("UPDATE book SET book_nick='"+ serverData.get("nick"+i) +"' WHERE book_char='"+ serverData.get("char"+i) +"' ");
                    }

                    bookData = serverData.get("changed_data").split("\\$");

                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        try {
                            String tempName = "rabbit";

                            File dir = new File(Environment.getExternalStorageDirectory() + "/TTS/" + tempName);
                            if (!dir.exists()) {
                                dir.mkdirs();
                                Log.d("Tag", "mkdirs");
                            }

                            //여기 나중에 로직으로 변경해야함 어떤 책인지에 따라서

                            Log.d("Tag", "rabbit/rabbit.txt 생성");  File f = new File(Environment.getExternalStorageDirectory() + File.separator + "TTS/" + tempName + "/" + tempName + ".txt");
                            FileWriter fw = new FileWriter(f, false);
                            fw.write(bookData.toString());
                            fw.close();
                            f.createNewFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String text =bookData[0];
                        if(text.length()>0){
                            mTextString = new String[]{text};
                            mttsTask = new ttsTask();
                            mttsTask.execute(mTextString);

                        } else {
                            //empty text
                        }
                    }

                    for (String page : bookData) {
                        Log.d("Real_Real 변환된 배열값:", page);
                    }
                    items.clear();
                    progress = new ProgressBar(activity);
                    progress.showDialog();
                }
            });

            save_button = cccardview.findViewById(R.id.save_button);
            save_button.setOnClickListener(new View.OnClickListener() {         // 저장할 땐, " serverData의 char1, char2에 대응하는 nick1, nick2를 저장합니다."
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < items.size(); i++) {
                        if ( row_nickname[i].getText().toString().equals("") ) { // 닉네임 입력칸을 비워뒀으면
                            items.get(i).setNickname( row_character[i].getText().toString() );  // 데이터객체에, 캐릭터 입력칸의 값 저장 // tiger -> tiger
                        } else {
                            items.get(i).setNickname( row_nickname[i].getText().toString() ); // 데이터객체에,  닉네임 입력칸의 값 저장  // tiger -> trump
                        }
                        serverData.put("nick"+(i+1), items.get(i).getNickname() );
                    }
                    String s1 = "";
                    String changed = data;
                    for (int i = 0; i < items.size(); i++) {
                        s1 = items.get(i).getCharacter();  // 주인공이름 한번씩 훑음
                        if (data.contains(s1)) {
                            changed = changed.replace(s1, items.get(i).getNickname()); // i차 바뀐문장
                        }
                    }
                    // 최종 변환된 문장은 changed 변수입니다.
                    serverData.put("changed_data", changed);
                    Log.d("Real_Real_error--story", "tototo_saved_button!");
                    Log.d("Real_Real_error--char1:", serverData.get("char1"));
                    Log.d("Real_Real_error--char2:", serverData.get("char2"));
                    Log.d("Real_Real_error--nick1:", serverData.get("nick1"));
                    Log.d("Real_Real_error--nick2:", serverData.get("nick2"));
                    for (int i = 1; serverData.containsKey("char"+i) ; i++) {
                        sqLiteDatabase.execSQL("UPDATE book SET book_nick='"+ serverData.get("nick"+i) +"' WHERE book_char='"+ serverData.get("char"+i) +"' ");
                    }
                }
            });
            clear_button = cccardview.findViewById(R.id.clear_button);
            clear_button.setOnClickListener(new View.OnClickListener() {         // 초기화 땐, " serverData의 nick1, nick2에  기존 단어를 그대로 저장합니다."
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < items.size(); i++) {
                        items.get(i).setNickname( row_character[i].getText().toString() );  // 데이터객체에, 캐릭터 입력칸의 값 저장 // tiger -> tiger
                        serverData.put("nick"+(i+1), items.get(i).getNickname() );
                        row_nickname[i].setText("");
                    }
                    for (int i = 1; serverData.containsKey("char"+i) ; i++) {
                        sqLiteDatabase.execSQL("UPDATE book SET book_nick='"+ serverData.get("nick"+i) +"' WHERE book_char='"+ serverData.get("char"+i) +"' ");
                    }
                    if( serverData.containsKey("changed_data") ) {
                        serverData.remove("changed_data");  //다 초기화
                    }
                    Log.d("Real_Real_error--story", "tototo_clear_button!");
                    Log.d("Real_Real_error--char1:", serverData.get("char1"));
                    Log.d("Real_Real_error--char2:", serverData.get("char2"));
                    Log.d("Real_Real_error--nick1:", serverData.get("nick1"));
                    Log.d("Real_Real_error--nick2:", serverData.get("nick2"));
                }
            });
            Log.d("Real_Real_error--story", "tototo_3");
            AlertDialog dialog=builder.create();
            dialog.setCanceledOnTouchOutside(false);    //없어지지 않도록 설정
            dialog.show();
            Log.d("Real_Real_error--story", "tototo_4");
            return;
        }

    }

    public static void setPlayList(String t, int page, String mood) {
        try {
            int sound = background(mood);
            complete = false;
            cur_page = page;
            String tempName = t + page;
            String path = Environment.getExternalStorageDirectory() + File.separator + "TTS/" + t + "/" + tempName + ".mp4";
            Log.d("tag", path);
            Resources resources = context.getResources();
            Uri uri = new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(resources.getResourcePackageName(sound))
                    .appendPath(resources.getResourceTypeName(sound))
                    .appendPath(resources.getResourceEntryName(sound))
                    .build();
            audioPlayer = new MediaPlayer();
            player = new MediaPlayer();
            audioPlayer.setDataSource(path);
            player.setDataSource(context, uri);
            audioPlayer.setVolume((float)0.7,(float)0.7);
            player.setVolume((float) 0.3, (float) 0.3);
            audioPlayer.prepare();
            player.prepare();
            audioPlayer.start();
            player.start();
            audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    audioPlayer.reset();
                    player.reset();
                    cur_page++;
                    if (cur_page < bookData.length) {
                        setPlayList(serverData.get("title_server"), cur_page, moodData[cur_page]);
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

    private static int background(String mood) {
        Random random = new Random();
        int sound = -1;
        player = new MediaPlayer();
        int randomValue = random.nextInt(3);
        if (mood.equals("1")) {
            switch (randomValue) {
                case 0:
                    sound = R.raw.n0;
                    break;
                case 1:
                    sound = R.raw.n1;
                    break;
                case 2:
                    sound = R.raw.n2;
                    break;
                default:
                    break;
            }
        } else if (mood.equals("2")) {
            switch (randomValue) {
                case 0:
                    sound = R.raw.n0;
                    break;
                case 1:
                    sound = R.raw.n1;
                    break;
                case 2:
                    sound = R.raw.n2;
                    break;
                default:
                    break;
            }
        } else if (mood.equals("3")) {
            switch (randomValue) {
                case 0:
                    sound = R.raw.n0;
                    break;
                case 1:
                    sound = R.raw.n1;
                    break;
                case 2:
                    sound = R.raw.n2;
                    break;
                default:
                    break;
            }
        }
        return sound;
    }
}
