package com.example.reviewfood;

public class User {
    private String email;
    private String fullname;
    private boolean type;
    private String birthday;
    private boolean gender;
    private String imageUri;

    public User() {}


    public User(String email, String fullname, String imageUri, boolean type, String birthday) {
        this.email= email;
        this.fullname = fullname;
        this.imageUri = imageUri;
        this.type = type;
        this.birthday = birthday;
    }

    public User(String email, String fullname, String imageUri, String birthday) {
        this.email= email;
        this.fullname = fullname;
        this.imageUri = imageUri;
        this.birthday = birthday;
    }
    public User(String email) {
        this.email = email;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }


    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public boolean isType() {
        return type;
    }

    public String getBirthday() {
        return birthday;
    }

    public boolean isGender() {
        return gender;
    }


    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public String getImageUri() {
        return imageUri;
    }

}
