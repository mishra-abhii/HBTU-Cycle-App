package com.hbtu.cycleapp.model;

public class User {
    String Name, Email, Branch, Roll_No, Session, Uid;
    String cycleTaken;

//    public User() {}

    public User(String name, String email, String branch, String roll_no, String year, String cycleTaken, String Uid) {
        this.Name = name;
        this.Email = email;
        this.Branch = branch;
        this.Roll_No = roll_no;
        this.Session = year;
        this.cycleTaken = cycleTaken;
        this.Uid = Uid;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getBranch() {
        return Branch;
    }

    public String getRoll_No() {
        return Roll_No;
    }

    public String getSession() {
        return Session;
    }

    public String getCycleTaken() {
        return cycleTaken;
    }

    public String getUid() {
        return Uid;
    }
}
