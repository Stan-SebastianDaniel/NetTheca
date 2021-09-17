package eu.ase.proiect.util;

import java.util.HashMap;
import java.util.Map;

import eu.ase.proiect.database.model.Book;

public class User {

    private  int idUser;
    private  String email;
    private  String username;
    private  String password;
    // 0 - barbat, 1 -feminin
    private  String sex;

    public static Map<Long, Book> mapFavoriteBook= new HashMap<Long, Book>();
    public static Map<Integer,Book> mapBookRead = new HashMap<Integer, Book>();

    public User(int idUser, String email, String username, String password, String sex) {
        this.idUser = idUser;
        this.email = email;
        this.username = username;
        this.password = password;
        this.sex = sex;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
