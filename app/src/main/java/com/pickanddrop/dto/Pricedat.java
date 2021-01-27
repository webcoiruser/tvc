package com.pickanddrop.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
/**
 * Awesome Pojo Generator
 * */
public class Pricedat{

  @SerializedName("drop")
  @Expose
  private ArrayList<DropPrices> drop;
  @SerializedName("total")
  @Expose
  private String total;
  @SerializedName("distanceCost")
  @Expose
  private String distanceCost;
  @SerializedName("serviceTax")
  @Expose
  private String serviceTax;
  @SerializedName("totalPrice")
  @Expose
  private String totalPrice;

  public void setDrop(ArrayList<DropPrices> drop){
   this.drop=drop;
  }

  public ArrayList<DropPrices> getDrop(){
   return drop;
  }
  public void setTotal(String total){
   this.total=total;
  }
  public String getTotal(){
   return total;
  }
  public void setDistanceCost(String distanceCost){
   this.distanceCost=distanceCost;
  }
  public String getDistanceCost(){
   return distanceCost;
  }
  public void setServiceTax(String serviceTax){
   this.serviceTax=serviceTax;
  }
  public String getServiceTax(){
   return serviceTax;
  }
  public void setTotalPrice(String totalPrice){
   this.totalPrice=totalPrice;
  }
  public String getTotalPrice(){
   return totalPrice;
  }
}