package com.tonyjs.pocketview.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tonyjs on 15. 1. 7..
 */
public class Feed extends BaseObject {
    private int color;
    public Feed(){}
    public Feed(int color) {
        this.color = color;
    }

    @SerializedName("tags") private ArrayList<String> tags;
    @SerializedName("type") private String type;
    @SerializedName("comments") private Comments comments;
    @SerializedName("filter") private String filter;
    @SerializedName("created_time") private String createdTime;
    @SerializedName("link") private String link;
    @SerializedName("likes") private Likes likes;
    @SerializedName("images") private Images images;
    @SerializedName("caption") private Caption caption;
    @SerializedName("user_has_liked") private boolean userLiked;
    @SerializedName("id") private String id;
    @SerializedName("user") private User user;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Likes getLikes() {
        return likes;
    }

    public void setLikes(Likes likes) {
        this.likes = likes;
    }

    public Caption getCaption() {
        return caption;
    }

    public void setCaption(Caption caption) {
        this.caption = caption;
    }

    public boolean isUserLiked() {
        return userLiked;
    }

    public void setUserLiked(boolean userLiked) {
        this.userLiked = userLiked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "tags=" + tags +
                ", type='" + type + '\'' +
                ", comments=" + comments +
                ", filter='" + filter + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", link='" + link + '\'' +
                ", likes=" + likes +
                ", images=" + images +
                ", caption=" + caption +
                ", userLiked=" + userLiked +
                ", id='" + id + '\'' +
                ", user=" + user +
                '}';
    }
}
