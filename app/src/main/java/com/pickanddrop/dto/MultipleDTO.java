package com.pickanddrop.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MultipleDTO {


//    @SerializedName("dropname")
//    String dropname;
//    @SerializedName("number1")
//    String number1;
//    @SerializedName("drop")
//    String drop;
//    @SerializedName("mulprice")
//    String mulprice;
//    @SerializedName("totalcost")
//    String totalcost;
//

    @SerializedName("typeGoodsCategory")
    @Expose
    private String typeGoodsCategory;
    @SerializedName("drop_building_type")
    @Expose
    private String dropBuildingType;
    @SerializedName("weight_unit")
    @Expose
    private String weight_unit;

    @SerializedName("classGoods")
    @Expose
    private String classGoods;
    @SerializedName("typeGoods")
    @Expose
    private String typeGoods;
    @SerializedName("noOfPallets")
    @Expose
    private String noOfPallets;
    @SerializedName("noOfPallets1")
    @Expose
    private String noOfPallets1;
    @SerializedName("parcel_weight")
    @Expose
    private String productWeight;





    @SerializedName("parcel_width")
    @Expose
    private String productWidth;

    @SerializedName("parcel_height")
    @Expose
    private String productHeight;

    public String getStopDistance() {
        return stopDistance;
    }

    public void setStopDistance(String stopDistance) {
        this.stopDistance = stopDistance;
    }

    @SerializedName("parcel_length")
    @Expose
    private String productLength;


    @SerializedName("stop_distance")
    @Expose
    private String stopDistance;


    public String getProductWidth() {
        return productWidth;
    }

    public void setProductWidth(String productWidth) {
        this.productWidth = productWidth;
    }

    public String getProductHeight() {
        return productHeight;
    }

    public void setProductHeight(String productHeight) {
        this.productHeight = productHeight;
    }

    public String getProductLength() {
        return productLength;
    }

    public void setProductLength(String productLength) {
        this.productLength = productLength;
    }

//    public String getNoOfPallets1() {
//        return noOfPallets1;
//    }
//
//    public void setNoOfPallets1(String noOfPallets1) {
//        this.noOfPallets1 = noOfPallets1;
//    }

    @SerializedName("drop_elevator")
    @Expose
    private String dropElevator;
    @SerializedName("dropoffaddress")
    @Expose
    private String dropoffaddress;
    @SerializedName("pickup_special_inst")
    @Expose
    private String pickupSpecialInst;

    @SerializedName("dropoff_special_inst")
    @Expose
    private String dropoff_special_inst;

    @SerializedName("dropoff_first_name")
    @Expose
    private String dropoffFirstName;
    @SerializedName("dropoff_last_name")
    @Expose
    private String dropoffLastName;
    @SerializedName("dropoff_mob_number")
    @Expose
    private String dropoffMobNumber;
    @SerializedName("dropoff_lat")
    @Expose
    private String dropoffLat;
    @SerializedName("dropoff_long")
    @Expose
    private String dropoffLong;
    @SerializedName("delivery_cost")
    @Expose
    private String deliveryCost;
    @SerializedName("dropoff_country_code")
    @Expose
    private String dropoffCountryCode;

    @SerializedName("delivery_status")
    @Expose
    private String delivery_status;




    public MultipleDTO(){}

    public MultipleDTO(String dropBuildingType, String dropElevator, String dropoffaddress, String dropoff_special_inst,String pickupSpecialInst, String dropoffFirstName, String dropoffLastName, String dropoffMobNumber, String dropoffLat, String dropoffLong, String deliveryCost, String dropoffCountryCode, String classGoods, String typeGoods, String noOfPallets, String productWeight,
                       String weight_unit,String typeGoodsCategory,
                       String productWidth,String productHeight,String productLength,String stopDistance,String noOfPallets1) {
        this.dropBuildingType = dropBuildingType;
        this.dropElevator = dropElevator;
        this.dropoffaddress = dropoffaddress;
        this.pickupSpecialInst = pickupSpecialInst;
        this.dropoff_special_inst=dropoff_special_inst;
        this.dropoffFirstName = dropoffFirstName;
        this.dropoffLastName = dropoffLastName;
        this.dropoffMobNumber = dropoffMobNumber;
        this.dropoffLat = dropoffLat;
        this.dropoffLong = dropoffLong;
        this.deliveryCost = deliveryCost;
        this.dropoffCountryCode = dropoffCountryCode;
        this.classGoods = classGoods;
        this.typeGoods = typeGoods;
        this.noOfPallets = noOfPallets;
        this.productWeight = productWeight;
        this.weight_unit = weight_unit;
        this.typeGoodsCategory = typeGoodsCategory;
        this.productWidth = productWidth;
        this.productHeight = productHeight;
        this.productLength = productLength;
        this.stopDistance = stopDistance;
        this.noOfPallets1 = noOfPallets1;

    }




    public String getDropBuildingType() {
        return dropBuildingType;
    }

    public void setDropBuildingType(String dropBuildingType) {
        this.dropBuildingType = dropBuildingType;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }

    public String getDropElevator() {
        return dropElevator;
    }

    public void setDropElevator(String dropElevator) {
        this.dropElevator = dropElevator;
    }

    public String getDropoffaddress() {
        return dropoffaddress;
    }

    public void setDropoffaddress(String dropoffaddress) {
        this.dropoffaddress = dropoffaddress;
    }

    public String getPickupSpecialInst() {
        return pickupSpecialInst;
    }

    public String getDropoff_special_inst() {
        return dropoff_special_inst;
    }

    public void setDropoff_special_inst(String dropoff_special_inst) {
        this.dropoff_special_inst = dropoff_special_inst;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public String getTypeGoodsCategory() {
        return typeGoodsCategory;
    }

    public void setTypeGoodsCategory(String typeGoodsCategory) {
        this.typeGoodsCategory = typeGoodsCategory;
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public void setPickupSpecialInst(String pickupSpecialInst) {
        this.pickupSpecialInst = pickupSpecialInst;
    }

    public String getDropoffFirstName() {
        return dropoffFirstName;
    }

    public void setDropoffFirstName(String dropoffFirstName) {
        this.dropoffFirstName = dropoffFirstName;
    }

    public String getDropoffLastName() {
        return dropoffLastName;
    }

    public void setDropoffLastName(String dropoffLastName) {
        this.dropoffLastName = dropoffLastName;
    }

    public String getDropoffMobNumber() {
        return dropoffMobNumber;
    }

    public void setDropoffMobNumber(String dropoffMobNumber) {
        this.dropoffMobNumber = dropoffMobNumber;
    }

    public String getDropoffLat() {
        return dropoffLat;
    }

    public void setDropoffLat(String dropoffLat) {
        this.dropoffLat = dropoffLat;
    }

    public String getDropoffLong() {
        return dropoffLong;
    }

    public void setDropoffLong(String dropoffLong) {
        this.dropoffLong = dropoffLong;
    }

    public String getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(String deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public String getDropoffCountryCode() {
        return dropoffCountryCode;
    }

    public void setDropoffCountryCode(String dropoffCountryCode) {
        this.dropoffCountryCode = dropoffCountryCode;
    }

    public String getClassGoods() {
        return classGoods;
    }

    public void setClassGoods(String classGoods) {
        this.classGoods = classGoods;
    }

    public String getTypeGoods() {
        return typeGoods;
    }

    public void setTypeGoods(String typeGoods) {
        this.typeGoods = typeGoods;
    }

    public String getNoOfPallets() {
        return noOfPallets;
    }

    public void setNoOfPallets(String noOfPallets) {
        this.noOfPallets = noOfPallets;
    }

    public String getNoOfPallets1() {
        return noOfPallets1;
    }

    public void setNoOfPallets1(String noOfPallets1) {
        this.noOfPallets1 = noOfPallets1;
    }


    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }
}
