package de.wip.tutorbot.sessions;

import java.util.HashMap;
import java.util.Map;

public class SessionDataHandler {

    private Map<String,LoggedInUser> loggedInUserMap = new HashMap<>();


    public void put(String sessionId,LoggedInUser loggedInUser){
        loggedInUserMap.put(sessionId,loggedInUser);
    }

    public void remove(String sessionId){
        loggedInUserMap.remove(sessionId);
    }

    public LoggedInUser getLoggedInUser(String sessionId){
        return loggedInUserMap.getOrDefault(sessionId,null);
    }

    public boolean isLoggedIn(String sessionId){
        return getLoggedInUser(sessionId) != null;
    }



}
