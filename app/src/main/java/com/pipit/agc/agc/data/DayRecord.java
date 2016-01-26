package com.pipit.agc.agc.data;

import com.pipit.agc.agc.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Eric on 1/10/2016.
 */
public class DayRecord {
    private long id;
    private String comment;
    private Date date;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(Date date){
        this.date=date;
    }

    public Date getDate(){
        return this.date;
    }

    public String getDateString(){
        return Util.dateToString(getDate());
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return comment;
    }

    public boolean compareToDate(Date otherdate){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(this.date).equals(fmt.format(otherdate));
    }

}
