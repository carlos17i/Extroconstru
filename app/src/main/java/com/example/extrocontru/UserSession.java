package com.example.extrocontru;

public class UserSession {
    private static UserSession instance;
    private String userId;
    private String displayName;

    private UserSession() {
        // Constructor privado para evitar instanciación directa
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
