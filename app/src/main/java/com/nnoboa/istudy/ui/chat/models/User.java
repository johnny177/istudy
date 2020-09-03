package com.nnoboa.istudy.ui.chat.models;

public class User {

    /**User's Full name*/
    public String fullname;

    /**User's username*/
    public String mUsername;

    /**User's email*/
    public String mEmail;

    /**User's Phone*/
    public String mPhone;

    /**User's UID*/
    public String mUID;

    /**User's PhotoUrl*/
    public String mPhotoUrl;

    /**User's first login **/
    public Long mTimeStamp;

    /**User's Bio*/
    public String mBiography;

    /**user,s Display name*/
    public String mDisplayName;

    /**Empty Constructor*/
    public User() {}

    /**
     *
     * @param fullname is user's full name
     * @param username auto genrated (can be changed later) username
     * @param uid is the unique id of the user
     * @param email is user email
     * @param phone is user phone number;
     * @param url is the photo url
     * @param timeStamp is the unix epoch tie th user first logged in or registered;
     * @param bio is the biography of user
     * @param displayName is the name visible to other users in chat;
     */

    public User(String fullname, String username,
                String uid, String email, String phone,
                String url, long timeStamp, String bio, String displayName){
        mEmail = email;
        this.fullname = fullname;
        mPhone = phone;
        mPhotoUrl = url;
        mUID = uid;
        mUsername = username;
        mTimeStamp =timeStamp;
        mBiography = bio;
        mDisplayName = displayName;
    }

    public Long getmTimeStamp() {
        return mTimeStamp;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getFullname() {
        return fullname;
    }

    public String getmPhone() {
        return mPhone;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public String getmUID() {
        return mUID;
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmBiography() {
        return mBiography;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public void setmTimeStamp(Long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmUID(String mUID) {
        this.mUID = mUID;
    }

    public void setmBiography(String mBiography) {
        this.mBiography = mBiography;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }
}
