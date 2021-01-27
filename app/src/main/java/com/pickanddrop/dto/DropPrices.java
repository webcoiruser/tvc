package com.pickanddrop.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class DropPrices{
    
  @SerializedName("totalPrice")
  @Expose
  private String totalPrice;
  @SerializedName("pallets")
  @Expose
  private String pallets;
  @SerializedName("drop_no")
  @Expose
  private String drop_no;
  @SerializedName("box")
  @Expose
  private String box;
  public void setTotalPrice(String totalPrice){
   this.totalPrice=totalPrice;
  }
  public String getTotalPrice(){
   return totalPrice;
  }
  public void setPallets(String pallets){
   this.pallets=pallets;
  }
  public String getPallets(){
   return pallets;
  }
  public void setDrop_no(String drop_no){
   this.drop_no=drop_no;
  }
  public String getDrop_no(){
   return drop_no;
  }
  public void setBox(String box){
   this.box=box;
  }
  public String getBox(){
   return box;
  }
}