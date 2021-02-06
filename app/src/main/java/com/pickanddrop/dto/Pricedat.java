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
    @SerializedName("stop_location_final")
    @Expose
    private String stop_location_final;

    public String getStop_location_final() {
        return stop_location_final;
    }

    public void setStop_location_final(String stop_location_final) {
        this.stop_location_final = stop_location_final;
    }

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