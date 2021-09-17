package eu.ase.proiect.database.model;

import android.text.format.DateFormat;

import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.Locale;

public class Recenzie {

    private String continut,uId,uImg,uNume;
    private float score;
//    private String timestamp;

    Object timp=ServerValue.TIMESTAMP;

    public Recenzie(){

    }

    public Recenzie(String continut, String uId, String uImg, String uNume, float score) {
        this.continut = continut;
        this.uId = uId;
        this.uImg = uImg;
        this.uNume = uNume;
        this.score = score;
     //   this.timestamp =timestampToString((Long) timp);
    }

    public Recenzie(String continut, String uId, String uImg, String uNume, float score, String timestamp) {
        this.continut = continut;
        this.uId = uId;
        this.uImg = uImg;
        this.uNume = uNume;
        this.score = score;
     //   this.timestamp = timestamp;
    }

    public String getContinut() {
        return continut;
    }

    public void setContinut(String continut) {
        this.continut = continut;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuImg() {
        return uImg;
    }

    public void setuImg(String uImg) {
        this.uImg = uImg;
    }

    public String getuNume() {
        return uNume;
    }

    public void setuNume(String uNume) {
        this.uNume = uNume;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

//    public String getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }


    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;


    }
}
