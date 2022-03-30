package com.example.damxat.Model;

import java.util.ArrayList;

// This is the Xat Group model it contains the xat name, the users that are in the xat, the xats, the constructors and the setters and getters
public class XatGroup {
    String name;
    ArrayList<String> users;
    ArrayList<Xat> xats;


    public XatGroup(String name) {
        this.name = name;

    }

    public XatGroup() {
    }


    public String getName() {
        return name;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public ArrayList<Xat> getXats() {
        return xats;
    }
}
