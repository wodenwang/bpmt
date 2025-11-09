package com.riversoft.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by exizhai on 4/16/2015.
 */
public class JsonPOJO {

    private int id;
    private String name;
    private Date time;
    private Calendar calendar;
    private float number;
    private boolean bool;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
