package com.wipdev.tutorbot;

public class SessionData {

    private String id;

    private boolean loggedIn = false;


    public SessionData(String id) {
        this.id = id;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean logIn(String username, String password){
        //TODO manage user & pw
        loggedIn = true;
        return loggedIn;
    }
}
