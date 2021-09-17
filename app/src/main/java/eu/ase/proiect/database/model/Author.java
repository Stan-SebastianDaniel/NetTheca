package eu.ase.proiect.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "authors")
public class Author implements Serializable {


    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "idAuthor")
    private long idAuthor;
    @ColumnInfo(name = "nameAuthor")
    private String nameAuthor;
    @ColumnInfo(name = "shortBiography")
    private String shortBiography;
    @ColumnInfo(name = "imgUrlAuthor")
    private String imgUrlAuthor;



    @Ignore
    public Author(){

    }

    public Author(long idAuthor, String nameAuthor, String shortBiography, String imgUrlAuthor) {
        this.idAuthor = idAuthor;
        this.nameAuthor = nameAuthor;
        this.shortBiography = shortBiography;
        this.imgUrlAuthor = imgUrlAuthor;
    }


    public long getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(long idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getNameAuthor() {
        return nameAuthor;
    }

    public void setNameAuthor(String nameAuthor) {
        this.nameAuthor = nameAuthor;
    }

    public String getShortBiography() {
        return shortBiography;
    }

    public void setShortBiography(String shortBiography) {
        this.shortBiography = shortBiography;
    }

    public String getImgUrlAuthor() {
        return imgUrlAuthor;
    }

    public void setImgUrlAuthor(String imgUrlAuthor) {
        this.imgUrlAuthor = imgUrlAuthor;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + idAuthor +
                ", name='" + nameAuthor + '\'' +
                ", shortBiography='" + shortBiography + '\'' +
                ", imgUrlAuthor='" + imgUrlAuthor + '\'' +
                '}';
    }
}
