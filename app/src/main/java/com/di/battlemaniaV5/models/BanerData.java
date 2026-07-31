package com.di.battlemaniaV5.models;

public class BanerData {

    String id;
    String title;
    String image;
    String linkType;
    String link;
    String linkId;
    String linkname;

    public BanerData(String id, String title, String image, String linkType, String link, String linkId, String linkname) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.linkType=linkType;
        this.link = link;
        this.linkId = linkId;
        this.linkname = linkname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkname() {
        return linkname;
    }

    public void setLinkname(String linkname) {
        this.linkname = linkname;
    }
}
