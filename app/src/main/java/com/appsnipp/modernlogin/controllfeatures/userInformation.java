package com.appsnipp.modernlogin.controllfeatures;

public class userInformation {
    String name;
    String quotes;
    String email;
    String phone;
    String dateofbirth;
    String avatar;
    String coverimage;
    String location;
    String alternativename;

    public userInformation() {
    }

    public userInformation(String name, String quotes, String email, String phone, String dateofbirth, String avatar, String coverimage, String location, String alternativename) {
        this.name = name;
        this.quotes = quotes;
        this.email = email;
        this.phone = phone;
        this.dateofbirth = dateofbirth;
        this.avatar = avatar;
        this.coverimage = coverimage;
        this.location = location;
        this.alternativename = alternativename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuotes() {
        return quotes;
    }

    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCoverimage() {
        return coverimage;
    }

    public void setCoverimage(String coverimage) {
        this.coverimage = coverimage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAlternativename() {
        return alternativename;
    }

    public void setAlternativename(String alternativename) {
        this.alternativename = alternativename;
    }
}
