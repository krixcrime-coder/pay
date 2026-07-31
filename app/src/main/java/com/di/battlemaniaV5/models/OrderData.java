package com.di.battlemaniaV5.models;

public class OrderData {

    String orderId;
    String orderNo;
    String pName;
    String pImage;
    String pPrice;
    String address;
    String orderStatus;
    String courierId;
    String trackingId;
    String createdDate;
    String courierLink;
    String userName;
    String additional;

    public OrderData(String orderId, String orderNo, String pName, String pImage, String pPrice, String address, String orderStatus, String courierId, String trackingId, String createdDate, String courierLink,String userName,String additional) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.pName = pName;
        this.pImage = pImage;
        this.pPrice = pPrice;
        this.address = address;
        this.orderStatus = orderStatus;
        this.courierId = courierId;
        this.trackingId = trackingId;
        this.createdDate = createdDate;
        this.courierLink = courierLink;
        this.userName=userName;
        this.additional=additional;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCourierId() {
        return courierId;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCourierLink() {
        return courierLink;
    }

    public void setCourierLink(String courierLink) {
        this.courierLink = courierLink;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }
}

