package com.androidapp.scalar.hailee.models;

/**
 * Created by pasin on 9/28/2018.
 */

public class user {

    private String email;
    private String password;
    private String name;
    private String profileImageUri;

    public user(String email, String password, String name, String profileImageUri){
        this.setEmail(email);
        this.setName(name);
        this.setPassword(password);
        this.setProfileImageUri(profileImageUri);
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }
}
