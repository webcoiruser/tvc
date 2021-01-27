package com.pickanddrop.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Awesome Pojo Generator
 * */
public class VechicalCatagoryDTO{
  @SerializedName("result")
  @Expose
  private String result;
  @SerializedName("data")
  @Expose
  private List<Data> data;
  @SerializedName("message")
  @Expose
  private String message;
  public void setResult(String result){
   this.result=result;
  }
  public String getResult(){
   return result;
  }
  public void setData(List<Data> data){
   this.data=data;
  }
  public List<Data> getData(){
   return data;
  }
  public void setMessage(String message){
   this.message=message;
  }
  public String getMessage(){
   return message;
  }

    public class Data{
        @SerializedName("vechile_type_name")
        @Expose
        private String vechile_type_name;
        @SerializedName("vechile_id")
        @Expose
        private Integer vechile_id;
        @SerializedName("vechile_category")
        @Expose
        private String vechile_category;
        @SerializedName("vechile_type")
        @Expose
        private Integer vechile_type;
        public void setVechile_type_name(String vechile_type_name){
            this.vechile_type_name=vechile_type_name;
        }
        public String getVechile_type_name(){
            return vechile_type_name;
        }
        public void setVechile_id(Integer vechile_id){
            this.vechile_id=vechile_id;
        }
        public Integer getVechile_id(){
            return vechile_id;
        }
        public void setVechile_category(String vechile_category){
            this.vechile_category=vechile_category;
        }
        public String getVechile_category(){
            return vechile_category;
        }
        public void setVechile_type(Integer vechile_type){
            this.vechile_type=vechile_type;
        }
        public Integer getVechile_type(){
            return vechile_type;
        }
    }

}