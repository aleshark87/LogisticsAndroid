package com.example.logistics.recycler;

public class CardItem {
    private String imgResource;
    private String title;
    private String destArr;
    private String date;

    public CardItem(String imgResource, String title, String destArr, String date){
            this.imgResource = imgResource;
            this.title = title;
            this.destArr = destArr;
            this.date = date;
    }

    public String getImgResource() {
        return imgResource;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDestArr() {
        return destArr;
    }
}
