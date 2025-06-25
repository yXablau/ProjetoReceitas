package com.unifebe.edu.projetoreceitas.model;

public class User {
    public String email;
    public String password;

    public User() {} // necessário para Firebase

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
