package com.example.reviewfood;

public class User {
    private String email;
    private String fullname;
    private String birthday;
    private String gender;
    private String imageUri;

    public User() {}

    public User(String email, String fullname, String imageUri, String birthday) {
        this.email= email;
        this.fullname = fullname;
        this.imageUri = imageUri;
        this.birthday = birthday;
    }

    public User(String email) {
        this.email = email;
    }

    public void setEmail(String email) { this.email = email; }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getBirthday() {
        return birthday;
    }

    public String isGender() {
        return gender;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public String getImageUri() {
        return imageUri;
    }

}
