package com.example.logistics.recycler;

import android.location.Address;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.util.List;

@Entity(tableName="item")
public class CardItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="item_id")
    private int id;
    @ColumnInfo(name="item_image")
    private String imgResource;
    @ColumnInfo(name="item_title")
    private String title;
    @ColumnInfo(name="item_origin_lat")
    private Double originLat;
    @ColumnInfo(name="item_origin_long")
    private Double originLong;
    @ColumnInfo(name="item_destination_lat")
    private Double destinationLat;
    @ColumnInfo(name="item_destination_long")
    private Double destinationLong;
    @ColumnInfo(name="item_locality_origin")
    private String originLocality;
    @ColumnInfo(name="item_locality_destination")
    private String destinationLocality;
    @ColumnInfo(name="item_date")
    private String date;

    public CardItem(String imgResource, String title,
                    Double originLat, Double originLong, Double destinationLat, Double destinationLong,
                    String originLocality, String destinationLocality,
                    String date){
        this.imgResource = imgResource;
        this.title = title;
        this.date = date;
        this.originLat = originLat;
        this.originLong = originLong;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
        this.originLocality = originLocality;
        this.destinationLocality = destinationLocality;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImgResource(String imgResource) {
        this.imgResource = imgResource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(Double originLat) {
        this.originLat = originLat;
    }

    public Double getOriginLong() {
        return originLong;
    }

    public void setOriginLong(Double originLong) {
        this.originLong = originLong;
    }

    public Double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public Double getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(Double destinationLong) {
        this.destinationLong = destinationLong;
    }

    public String getOriginLocality() {
        return originLocality;
    }

    public void setOriginLocality(String originLocality) {
        this.originLocality = originLocality;
    }

    public String getDestinationLocality() {
        return destinationLocality;
    }

    public void setDestinationLocality(String destinationLocality) {
        this.destinationLocality = destinationLocality;
    }
}
