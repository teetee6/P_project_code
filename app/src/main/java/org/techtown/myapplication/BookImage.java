package org.techtown.myapplication;

import android.graphics.Bitmap;

public class BookImage {
    private BookAdapter.ViewHolder view;
    private Bitmap bitmap;
    private String Rsc;
    public BookImage(){};

    public BookAdapter.ViewHolder getView(){
        return view;
    }

    public void setView(BookAdapter.ViewHolder view){
        this.view = view;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getRsc(){
        return Rsc;
    }

    public void setRsc(String Rsc){
        this.Rsc = Rsc;
    }
}
