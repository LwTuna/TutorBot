package com.wipdev.tutorbot.sessions;

import com.wipdev.tutorbot.database.DatabaseHandler;
import org.json.JSONObject;

public class SessionData {

    private String id;

    private boolean loggedIn = false;
    private String userID;


    public SessionData(String id) {
        this.id = id;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean logIn(String username, String password, DatabaseHandler databaseHandler){
        JSONObject res = databaseHandler.logIn(username,password);

        if(res == null){
            return false;
        }else {
            loggedIn = true;
            System.out.println(res.get(databaseHandler.idKey));
            return loggedIn;

        }

    }
}
