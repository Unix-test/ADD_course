package com.appsnipp.modernlogin.controllfeatures.composer.post_controller;

public class post_contents {
    String contents, uid,
            image, video, emotions;

    public post_contents() {
    }

    public post_contents(String contents, String uid, String image, String video, String emotions) {
        this.contents = contents;
        this.uid = uid;
        this.image = image;
        this.video = video;
        this.emotions = emotions;
    }


    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getEmotions() {
        return emotions;
    }

    public void setEmotions(String emotions) {
        this.emotions = emotions;
    }

}
