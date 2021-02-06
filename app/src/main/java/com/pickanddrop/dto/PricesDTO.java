package com.pickanddrop.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class PricesDTO{

  @SerializedName("result")
  @Expose
  private String result;
  @SerializedName("data")
  @Expose
  private Pricedat Pricedat;
  @SerializedName("error")
  @Expose
  private String error;
  @SerializedName("message")
  @Expose
  private String message;
  @SerializedName("stop_location_final")
  @Expose
  private String stop_location_final;

    public String getStop_location_final() {
        return stop_location_final;
    }

    public void setStop_location_final(String stop_location_final) {
        this.stop_location_final = stop_location_final;
    }

    public void setResult(String result){
   this.result=result;
  }
  public String getResult(){
   return result;
  }
  public void setPricedat(Pricedat Pricedat){
   this.Pricedat=Pricedat;
  }
  public Pricedat getPricedat(){
   return Pricedat;
  }
  public void setError(String error){
   this.error=error;
  }
  public String getError(){
   return error;
  }
  public void setMessage(String message){
   this.message=message;
  }
  public String getMessage(){
   return message;
  }
}