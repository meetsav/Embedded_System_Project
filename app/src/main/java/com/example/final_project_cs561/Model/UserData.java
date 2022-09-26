package com.example.final_project_cs561.Model;

public class UserData {
    public UserData(){

    }
    public UserData(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }
    public UserData(String userName, String userEmail,boolean isPending) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.isPending =isPending;
    }

    private String userName = "";
    private String userEmail="";

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    private String signaturePath = "";
    private boolean isPending;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }
}
