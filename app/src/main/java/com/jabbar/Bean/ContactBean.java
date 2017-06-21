package com.jabbar.Bean;

/**
 * Created by hardikjani on 6/13/17.
 */

public class ContactBean {

    public int id;
    public String name;
    public String number;
    public String status;
    public String avatar;
    public String location;
    public String last_seen;
    public int favorite;

    public ContactBean(int id, String name, String number, String status, String avatar, String location, String last_seen, int favorite) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.status = status;
        this.avatar = avatar;
        System.out.println("=====location====" + location);
        this.location = location;
        this.last_seen = last_seen;
        this.favorite = favorite;
    }

    public ContactBean(int id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.status = "";
        this.avatar = "";
        this.location = "";
        this.last_seen = "";
        this.favorite = 0;
    }
}
