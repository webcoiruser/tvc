package com.pickanddrop.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class ProfileDTO{
  @SerializedName("result")
  @Expose
  private String result;
  @SerializedName("data")
  @Expose
  private Data data;
  @SerializedName("message")
  @Expose
  private String message;
  public void setResult(String result){
   this.result=result;
  }
  public String getResult(){
   return result;
  }
  public void setData(Data data){
   this.data=data;
  }
  public Data getData(){
   return data;
  }
  public void setMessage(String message){
   this.message=message;
  }
  public String getMessage(){
   return message;
  }


    public class Data
    {
        @SerializedName("firstname")
        @Expose
        private String firstname;
        @SerializedName("vehicle_no")
        @Expose
        private String vehicle_no;
        @SerializedName("vehicle_type")
        @Expose
        private String vehicle_type;
        @SerializedName("type_of_truck")
        @Expose
        private String type_of_truck;
        @SerializedName("lastname")
        @Expose
        private String lastname;
        @SerializedName("country_code")
        @Expose
        private Integer country_code;
        @SerializedName("dot_number")
        @Expose
        private String dot_number;
        @SerializedName("profile_image")
        @Expose
        private String profile_image;
        @SerializedName("user_type")
        @Expose
        private Integer user_type;
        @SerializedName("user_id")
        @Expose
        private Integer user_id;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("social_security_number")
        @Expose
        private String social_security_number;
        @SerializedName("licence_image")
        @Expose
        private String licence_image;
        @SerializedName("insurance_image")
        @Expose
        private String insurance_image;
        @SerializedName("email")
        @Expose
        private String email;
        public void setFirstname(String firstname){
            this.firstname=firstname;
        }
        public String getFirstname(){
            return firstname;
        }
        public void setVehicle_no(String vehicle_no){
            this.vehicle_no=vehicle_no;
        }
        public Object getVehicle_no(){
            return vehicle_no;
        }
        public void setVehicle_type(String vehicle_type){
            this.vehicle_type=vehicle_type;
        }
        public String getVehicle_type(){
            return vehicle_type;
        }
        public void setType_of_truck(String type_of_truck){
            this.type_of_truck=type_of_truck;
        }
        public String getType_of_truck(){
            return type_of_truck;
        }
        public void setLastname(String lastname){
            this.lastname=lastname;
        }
        public String getLastname(){
            return lastname;
        }
        public void setCountry_code(Integer country_code){
            this.country_code=country_code;
        }
        public Integer getCountry_code(){
            return country_code;
        }
        public void setDot_number(String dot_number){
            this.dot_number=dot_number;
        }
        public String getDot_number(){
            return dot_number;
        }
        public void setProfile_image(String profile_image){
            this.profile_image=profile_image;
        }
        public String getProfile_image(){
            return profile_image;
        }
        public void setUser_type(Integer user_type){
            this.user_type=user_type;
        }
        public Integer getUser_type(){
            return user_type;
        }
        public void setUser_id(Integer user_id){
            this.user_id=user_id;
        }
        public Integer getUser_id(){
            return user_id;
        }
        public void setPhone(String phone){
            this.phone=phone;
        }
        public String getPhone(){
            return phone;
        }
        public void setSocial_security_number(String social_security_number){
            this.social_security_number=social_security_number;
        }
        public String getSocial_security_number(){
            return social_security_number;
        }
        public void setLicence_image(String licence_image){
            this.licence_image=licence_image;
        }
        public String getLicence_image(){
            return licence_image;
        }
        public void setInsurance_image(String insurance_image){
            this.insurance_image=insurance_image;
        }
        public String getInsurance_image(){
            return insurance_image;
        }
        public void setEmail(String email){
            this.email=email;
        }
        public String getEmail(){
            return email;
        }
    }
}
