
package com.amplify.model.MyProfileRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyProfile {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fb_profile_pic")
    @Expose
    private String fbProfilePic;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("fb_location")
    @Expose
    private String fbLocation;
    @SerializedName("device_geo_location")
    @Expose
    private String deviceGeoLocation;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("viewed_count")
    @Expose
    private String viewedCount;
    @SerializedName("likes_count")
    @Expose
    private String likesCount;
    @SerializedName("fb_relationship_status")
    @Expose
    private String fbRelationshipStatus;
    @SerializedName("fb_friends_count")
    @Expose
    private String fbFriendsCount;
    @SerializedName("instagram")
    @Expose
    private String instagram;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("snapchat")
    @Expose
    private String snapchat;
    @SerializedName("kik")
    @Expose
    private String kik;
    @SerializedName("views_count")
    @Expose
    private String viewsCount;
    @SerializedName("like_cnt")
    @Expose
    private String likeCnt;
    @SerializedName("crop_profile")
    @Expose
    private String crop_profile;

    public String getCrop_profile() {
        return crop_profile;
    }

    public void setCrop_profile(String crop_profile) {
        this.crop_profile = crop_profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFbProfilePic() {
        return fbProfilePic;
    }

    public void setFbProfilePic(String fbProfilePic) {
        this.fbProfilePic = fbProfilePic;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFbLocation() {
        return fbLocation;
    }

    public void setFbLocation(String fbLocation) {
        this.fbLocation = fbLocation;
    }

    public String getDeviceGeoLocation() {
        return deviceGeoLocation;
    }

    public void setDeviceGeoLocation(String deviceGeoLocation) {
        this.deviceGeoLocation = deviceGeoLocation;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getViewedCount() {
        return viewedCount;
    }

    public void setViewedCount(String viewedCount) {
        this.viewedCount = viewedCount;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getFbRelationshipStatus() {
        return fbRelationshipStatus;
    }

    public void setFbRelationshipStatus(String fbRelationshipStatus) {
        this.fbRelationshipStatus = fbRelationshipStatus;
    }

    public String getFbFriendsCount() {
        return fbFriendsCount;
    }

    public void setFbFriendsCount(String fbFriendsCount) {
        this.fbFriendsCount = fbFriendsCount;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getSnapchat() {
        return snapchat;
    }

    public void setSnapchat(String snapchat) {
        this.snapchat = snapchat;
    }

    public String getKik() {
        return kik;
    }

    public void setKik(String kik) {
        this.kik = kik;
    }

    public String getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(String viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt(String likeCnt) {
        this.likeCnt = likeCnt;
    }

}
