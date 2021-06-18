package com.example.logistics.recycler;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="item")
public class CardItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="item_id")
    private int id;
    @ColumnInfo(name="item_image")
    private int imgResource;
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
    @ColumnInfo(name="product_type")
    private String productType;
    @ColumnInfo(name="item_product_quantity")
    private int quantityKg;

    public CardItem(int imgResource, String title,
                    Double originLat, Double originLong, Double destinationLat, Double destinationLong,
                    String originLocality, String destinationLocality,
                    String date, String productType, int quantityKg){
        this.imgResource = imgResource;
        this.title = title;
        this.date = date;
        this.originLat = originLat;
        this.originLong = originLong;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
        this.originLocality = originLocality;
        this.destinationLocality = destinationLocality;
        this.productType = productType;
        this.quantityKg = quantityKg;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(int quantityKg) {
        this.quantityKg = quantityKg;
    }

    public int getImgResource() {
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

    public void setImgResource(int imgResource) {
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
