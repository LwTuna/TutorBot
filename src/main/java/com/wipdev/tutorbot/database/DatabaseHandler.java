package com.wipdev.tutorbot.database;


import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.internals.JacksonMapper;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatabaseHandler {


    Nitrite db;

    public final String userKey = "user", passwordKey = "password";
    public final String idKey = "_id";

    public void connect() {
        db = Nitrite.builder().compressed().filePath("res/data.db").openOrCreate("admin","2212");

    }

    private void insertOne(Database database, JSONObject object) {
        db.getCollection(database.databaseName).insert(new Document(object.toMap()) );
    }

    private JSONObject find(Database database, String key, Object value) {
        Iterator<Document> it= db.getCollection(database.databaseName).find().iterator();
        while (it.hasNext()) {
            var val = it.next();
            if (!val.containsKey(key)) continue;
            if (((String)(val.get(key))).equals(value)) {
                JacksonMapper mapper = new JacksonMapper();
                var ret = new JSONObject( mapper.toJson(mapper.asDocument(val)));
                return ret;
            }
        }
        return null;
    }

    private List<JSONObject> getAll(Database database){
        List<JSONObject> objects = new ArrayList<>();
        Cursor cursor = db.getCollection(database.databaseName).find();
        JacksonMapper mapper = new JacksonMapper();
        for(Document doc  : cursor){
            objects.add(new JSONObject(mapper.toJson(mapper.asDocument(doc))));
        }
        return objects;
    }

    public JSONObject logIn(String username, String password) {
        var object = find(Database.User_Data, userKey, username);
        if (object == null) return null;
        if (object.getString(passwordKey).equals(password)) {
            return object;
        } else {
            return null;
        }
    }

    public boolean contains(Database database,String key,String value){
        return find(database,key,value) !=null;
    }

    public String createNewUser(String username,String password){
        JSONObject object = new JSONObject();
        object.put(userKey,username);
        object.put(passwordKey,password);
        insertOne(Database.User_Data, object);
        JSONObject entry = find(Database.User_Data,userKey,username);
        return entry.get(idKey).toString();
    }

    public void createQuestion(JSONObject question){
        question.put("questionID", getNextQuestionId());
        insertOne(Database.Questions,question);
    }

    public int getNextQuestionId(){
        List<JSONObject> objects = getAllQuestions();

        int maxId = 0;
        for(JSONObject obj:objects){
            if(obj.getInt("questionID") > maxId){
                maxId = obj.getInt("questionID");
            }
        }
        return maxId +1;

    }

    public List<JSONObject> getAllQuestions(){
        return getAll(Database.Questions);
    }

    public void putAnswer(int id, String answer, String userObjectID) {
        JSONObject packet = new JSONObject();
        packet.put("questionID",id);
        packet.put("answer",answer);
        packet.put("userId",userObjectID);
        insertOne(Database.Answers,packet);
    }
}
