package com.example.vaultnet.model;

public class User {
    private int    id;
    private String name;
    private String email;

    public int    getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "'}";
    }
}