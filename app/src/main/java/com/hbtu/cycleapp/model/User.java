package com.hbtu.cycleapp.model;

public class User {
    String Name, Email, Branch, Roll_No, Session;

    public User(String name, String email, String branch, String roll_no, String year) {
        this.Name = name;
        this.Email = email;
        this.Branch = branch;
        this.Roll_No = roll_no;
        this.Session = year;
    }

    public User() {}

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
}
