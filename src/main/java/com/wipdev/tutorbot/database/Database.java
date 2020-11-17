package com.wipdev.tutorbot.database;

public enum Database {

    User_Data("User_Data", "Userdata"),Questions("Questions","question_list");

    String databaseName;
    String collectionName;

    Database(String databaseName, String collectionName) {
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }
}
