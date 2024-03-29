package org.techtown.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import java.util.HashMap;

public class TTSAPI {

    public static void main(String[] args, String title, int page, String type,final View activity){
        try {
            String apiId = activity.getResources().getString(R.string.apiId);
            String apiKey = activity.getResources().getString(R.string.apiKey);

            URL url = new URL("https://api.maum.ai/tts/stream");
            String text = URLEncoder.encode(args[0],"UTF-8");
            HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
            myConnection.setRequestMethod("POST");
            String lang;
            if(type.equals("kor")) {
                lang = "kor";
            }else{
                lang = "eng";
            }
            String postParams = "apiId="+apiId+"&apiKey="+apiKey+"&voiceName=baseline_"+lang+"&text="+text;
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
                InputStream is = myConnection.getInputStream();
                int read =0;
                byte[] bytes = new byte[1024];

                File dir  = new File(Environment.getExternalStorageDirectory()+"/TTS");
                File dir2 = new File(Environment.getExternalStorageDirectory()+"/TTS/"+title);//current name
                if(!dir.exists()){
                    dir.mkdirs();
                    Log.d("Tag","mkdirs1");
                }else if(!dir2.exists()){
                    dir2.mkdirs();
                    Log.d("Tag","mkdirs2");
                }
                String tempName=title+page;//토끼와 거북이 1
                File f = new File(Environment.getExternalStorageDirectory()+File.separator+"TTS/"+title+"/"+tempName+".mp4");
                f.createNewFile();
                String path = Environment.getExternalStorageDirectory()+File.separator+"TTS/"+title+"/"+tempName+".mp4";
                Log.d("tag","file saved: "+tempName);
                Log.d("tag","path:" +path);
                OutputStream outputStream = new FileOutputStream(f);
                while((read = is.read(bytes)) != -1){
                    outputStream.write(bytes,0, read);
                }
                is.close();
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
    }
}
