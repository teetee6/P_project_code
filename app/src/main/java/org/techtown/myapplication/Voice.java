package org.techtown.myapplication;

import android.media.MediaPlayer;
import android.provider.MediaStore;

public class Voice {
    private int page;
    private MediaPlayer audioPlayer;

    Voice(){}

    public Voice(int page, MediaPlayer audioPlayer){
        this.page = page;
        this.audioPlayer = audioPlayer;
    }

    public int getPage(){
        return page;
    }

    public MediaPlayer getMedia(){
        return audioPlayer;
    }
}
