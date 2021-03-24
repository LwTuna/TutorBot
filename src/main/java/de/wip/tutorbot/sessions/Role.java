package de.wip.tutorbot.sessions;

public enum Role {
    USER(0),TUTOR(1),PROFESSOR(2);

    int permissionLevel;

    Role(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public static Role getByLevel(int level){
        for(Role role : values()){
            if(role.permissionLevel == level){
                return role;
            }
        }
        return USER;
    }
}
