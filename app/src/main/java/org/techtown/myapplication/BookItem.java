package org.techtown.myapplication;

public class BookItem {

    private String name, title_server;
    private  int imgSrc;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getTitle_server(){
        return title_server;
    }

    public void setTitle_server(String title_server){
        this.title_server = title_server;
    }

    public int getImgSrc(){
        return imgSrc;
    }

    public void setImgSrc(int imgSrc){
        this.imgSrc = imgSrc;
    }

}