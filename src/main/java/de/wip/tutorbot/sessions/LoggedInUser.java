package de.wip.tutorbot.sessions;

public class LoggedInUser {

    private final String idInDB;
    private final String username;
    private final String realname;
    private final Role role;

    public LoggedInUser( String idInDB, String username, Role role,String realname) {
        this.idInDB = idInDB;
        this.username = username;
        this.role = role;
        this.realname = realname;
    }
    public LoggedInUser(String idInDB,String username,int role,String realname){
        this(idInDB,username,Role.getByLevel(role),realname);
    }



    public String getIdInDB() {
        return idInDB;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getRealname() {
        return realname;
    }
}
