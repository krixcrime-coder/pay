package com.app.rush47.models;

/**
 * One slide in the top auto-scroll banner. redirectUrl is where tapping
 * the banner opens - empty means the banner just isn't tappable.
 */
public class Banner {

    private final String imageUrl;
    private final String redirectUrl;

    public Banner(String imageUrl, String redirectUrl) {
        this.imageUrl = imageUrl;
        this.redirectUrl = redirectUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
