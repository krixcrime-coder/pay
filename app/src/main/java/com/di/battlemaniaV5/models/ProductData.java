package com.di.battlemaniaV5.models;

public class ProductData {

    String pId;
    String pName;
    String pImage;
    String pShortDesc;
    String pDesc;
    String paPrice;
    String psPrice;

    public ProductData(String pId, String pName, String pImage, String pShortDesc, String pDesc, String paPrice, String psPrice) {
        this.pId = pId;
        this.pName = pName;
        this.pImage = pImage;
        this.pShortDesc = pShortDesc;
        this.pDesc = pDesc;
        this.paPrice = paPrice;
        this.psPrice = psPrice;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
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

    public String getpShortDesc() {
        return pShortDesc;
    }

    public void setpShortDesc(String pShortDesc) {
        this.pShortDesc = pShortDesc;
    }

    public String getpDesc() {
        return pDesc;
    }

    public void setpDesc(String pDesc) {
        this.pDesc = pDesc;
    }

    public String getPaPrice() {
        return paPrice;
    }

    public void setPaPrice(String paPrice) {
        this.paPrice = paPrice;
    }

    public String getPsPrice() {
        return psPrice;
    }

    public void setPsPrice(String psPrice) {
        this.psPrice = psPrice;
    }
}
