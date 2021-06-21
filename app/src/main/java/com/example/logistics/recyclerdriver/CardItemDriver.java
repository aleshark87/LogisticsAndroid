package com.example.logistics.recyclerdriver;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "card_driver")
public class CardItemDriver {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "card_driver_id")
    private int id;

    @ColumnInfo(name="card_driver_image")
    private String imgResource;

    @ColumnInfo(name="card_driver_name")
    private String driverName;

    @ColumnInfo(name="card_driver_capacity")
    private int capacity;

    @ColumnInfo(name="card_driver_timework")
    private String timeWork;

    @ColumnInfo(name="card_driver_hired")
    private boolean hired;

    public CardItemDriver(String imgResource, String driverName, int capacity, String timeWork,
                          boolean hired) {
        this.imgResource = imgResource;
        this.driverName = driverName;
        this.capacity = capacity;
        this.timeWork = timeWork;
        this.hired = hired;
    }

    public boolean isHired() {
        return hired;
    }

    public void setHired(boolean hired) {
        this.hired = hired;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgResource() {
        return imgResource;
    }

    public void setImgResource(String imgResource) {
        this.imgResource = imgResource;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getTimeWork() {
        return timeWork;
    }

    public void setTimeWork(String timeWork) {
        this.timeWork = timeWork;
    }
}
