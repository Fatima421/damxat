package com.example.damxat.Model;

// This is the User model it contains the user id, the username, the status, the constructors and the setters and getters
public class User {
    String id;
    String username;
    String status;
    String token;

    public User(String id, String username, String status) {
        this.id = id;
        this.username = username;
    }

    public User(String id, String username, String status, String token) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.token = token;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
