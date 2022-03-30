package com.example.damxat.Model;

// This is the Xat model it contains the sender, the receiver, the message, the constructors and the setters and getters
public class Xat {

    String sender;
    String receiver;
    String message;

    public Xat(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }


    public Xat() {

    }


    public Xat(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }
}
