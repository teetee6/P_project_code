package org.techtown.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TTSAPI {
    public static void main(String[] args){
        try {
            Log.d("tag","hey2");
            URL url = new URL("https://api.maum.ai/tts/stream");
            String text = URLEncoder.encode(args[0],"UTF-8");
            HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
            myConnection.setRequestMethod("POST");//set post
            //myConnection.setRequestProperty("Content-Type","application/json");
            /*myConnection.setRequestProperty("apiId", "gachon.pproject.6728d71a1dc3c");
            myConnection.setRequestProperty("apiKey", "ed13742e94834e339b70dc8a29a2142f");
            myConnection.setRequestProperty("text", text);
            myConnection.setRequestProperty("voiceName", "baseline_kor");*/
            String postParams = "apiId=gachon.pproject.6728d71a1dc3c&apiKey=ed13742e94834e339b70dc8a29a2142f&voiceName=baseline_kor&text="+text;
            myConnection.setDoInput(true);
            myConnection.setDoOutput(true);//send post data to OutputStream
            DataOutputStream wr = new DataOutputStream(myConnection.getOutputStream());
            Log.d("tag",String.valueOf(wr));
            wr.writeBytes(postParams);
            Log.d("tag", postParams);
            wr.flush();
            wr.close();
            BufferedReader br;
            if(myConnection.getResponseCode() == 200){
                //success
                            /*
                            InputStream responseBody = myConnection.getInputStream();
                            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                            JsonReader jsonReader = new JsonReader(responseBodyReader);
                            jsonReader.beginObject();

                            while(jsonReader.hasNext()){
                                String key = jsonReader.nextName();
                                if(key.equals("")){
                                    String value = jsonReader.nextString();
                                    //
                                    break;
                                }else{
                                    jsonReader.skipValue();//skip values of other keys
                                }
                            }
                            jsonReader.close();
                            myConnection.disconnect();*/

                InputStream is = myConnection.getInputStream();
                int read =0;
                byte[] bytes = new byte[1024];

                File dir  = new File(Environment.getExternalStorageDirectory()+"/TTS");
                if(!dir.exists()){
                    dir.mkdirs();
                    Log.d("Tag","mkdirs");
                }
                String tempName="ttstemp";
                File f = new File(Environment.getExternalStorageDirectory()+File.separator+"TTS/"+tempName+".wav");
                f.createNewFile();
                Log.d("tag","file saved");
                OutputStream outputStream = new FileOutputStream(f);
                while((read = is.read(bytes)) != -1){
                    outputStream.write(bytes,0, read);
                }
                is.close();
                String path = Environment.getExternalStorageDirectory()+File.separator+"TTS/"+tempName+".wav";
                MediaPlayer audioPlayer = new MediaPlayer();
                audioPlayer.setDataSource(path);
                Log.d("tag",path);
                audioPlayer.prepare();
                audioPlayer.start();
            }else{
                //error
                br = new BufferedReader(new InputStreamReader(myConnection.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while((inputLine = br.readLine())!= null){
                    response.append(inputLine);
                }
                br.close();
            }

        }catch (Exception e)
        {
            System.out.println(e);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //networking logic should be here

            }
        });
    }
}
