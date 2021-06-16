package com.example.logistics.recycler;

import android.location.Address;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

@Entity(tableName="item")
public class CardItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="item_id")
    private int id;
    @ColumnInfo(name="item_image")
    private String imgResource;
    @ColumnInfo(name="item_title")
    private String title;
    @ColumnInfo(name="item_addr_origin")
    private String serializedAddressOrigin;
    @ColumnInfo(name="item_addr_destination")
    private String serializedAddressDestination;
    @ColumnInfo(name="item_date")
    private String date;
    @Ignore
    private Address addressOrigin;
    @Ignore
    private Address addressDestination;
    @Ignore
    private Gson serializer;

    public CardItem(String imgResource, String title, String serializedAddressOrigin, String serializedAddressDestination, String date){
        this.imgResource = imgResource;
        this.title = title;
        this.serializedAddressOrigin = serializedAddressOrigin;
        this.serializedAddressDestination = serializedAddressDestination;
        this.date = date;
    }

    public CardItem(String imgResource, String title, Address origin, Address destination, String date){
        this.imgResource = imgResource;
        this.title = title;
        this.addressOrigin = origin;
        this.addressDestination = destination;
        this.date = date;
        this.serializedAddressOrigin = serializer.toJson(origin);
        this.serializedAddressDestination = serializer.toJson(destination);
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

    public Address getAddressOrigin() { return addressOrigin; }

    public Address getAddressDestination() { return addressDestination; }

    public int getId() {
        return id;
    }

    public String getSerializedAddressOrigin() {
        return serializedAddressOrigin;
    }

    public String getSerializedAddressDestination() {
        return serializedAddressDestination;
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

    public void setSerializedAddressOrigin(String serializedAddressOrigin) {
        this.serializedAddressOrigin = serializedAddressOrigin;
    }

    public void setSerializedAddressDestination(String serializedAddressDestination) {
        this.serializedAddressDestination = serializedAddressDestination;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
