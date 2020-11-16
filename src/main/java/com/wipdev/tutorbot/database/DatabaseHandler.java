package com.wipdev.tutorbot.database;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.Document;
import org.json.JSONObject;

public class DatabaseHandler {


    private MongoClientURI uri;

    private MongoClient mongoClient;

    public void connect(){
        uri = new MongoClientURI("mongodb+srv://Admin:2212@cluster0.jquix.mongodb.net/user_data?retryWrites=true&w=majority");
        mongoClient = new MongoClient(uri);



    }

    public void insertOne(Database database,JSONObject object){
        mongoClient.getDatabase(database.databaseName).getCollection(database.collectionName).insertOne(new Document(object.toMap()));
    }

    public JSONObject find(Database database,String key,Object value){
        var cursor = mongoClient.getDatabase(database.databaseName).getCollection(database.collectionName).find(new BasicDBObject(key,value));
        return new JSONObject( cursor.first().toJson());
    }


}
