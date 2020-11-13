package com.wipdev.tutorbot;


import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionManager {

    private Map<String,SessionData> sessions = new HashMap<>();


    public SessionData getSessionData(HttpSession session){
        if(sessions.containsKey(session.getId())){
            return sessions.get(session.getId());
        }
        SessionData sessionData = new SessionData(session.getId());
        sessions.put(session.getId(),sessionData);
        return sessionData;
    }


}
