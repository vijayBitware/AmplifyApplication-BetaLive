package com.amplify.model.GetLikedMe;

import com.amplify.webservice.BaseResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bitware on 20/2/18.
 */

public class UserDatum extends BaseResponse {

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
    @SerializedName("like_flag")
    @Expose
    private String likeFlag;

    @SerializedName("distance")
    @Expose
    private String distance;

    @SerializedName("bio")
    @Expose
    private String bio;

    @SerializedName("flagged")
    @Expose
    private String flagged;

    private String isLiked;

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

    public String getLikeFlag() {
        return likeFlag;
    }

    public void setLikeFlag(String likeFlag) {
        this.likeFlag = likeFlag;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(String isActive) {
        this.isLiked = isActive;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFlagged() {
        return flagged;
    }

    public void setFlagged(String flagged) {
        this.flagged = flagged;
    }
}

