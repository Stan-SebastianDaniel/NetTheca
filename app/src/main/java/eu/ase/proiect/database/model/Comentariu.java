package eu.ase.proiect.database.model;

import java.io.Serializable;

public class Comentariu implements Serializable {

    private String continut,uId,uImg,uNume;

    public Comentariu(){}

    public Comentariu(String continut, String uId, String uImg, String uNume) {
        this.continut = continut;
        this.uId = uId;
        this.uImg = uImg;
        this.uNume = uNume;
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


    @Override
    public String toString() {
        return "Comentariu{" +
                "continut='" + continut + '\'' +
                ", uId='" + uId + '\'' +
                ", uImg='" + uImg + '\'' +
                ", uNume='" + uNume + '\'' +
                '}';
    }
}
