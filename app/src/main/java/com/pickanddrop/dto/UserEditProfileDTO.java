package com.pickanddrop.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class UserEditProfileDTO{
  @SerializedName("result")
  @Expose
  private String result;
  @SerializedName("data")
  @Expose
  private Data data;
  @SerializedName("error")
  @Expose
  private String error;
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

    public class Data{
        @SerializedName("country_code")
        @Expose
        private String country_code;
        @SerializedName("firstname")
        @Expose
        private String firstname;
        @SerializedName("profile_image")
        @Expose
        private String profile_image;
        @SerializedName("user_id")
        @Expose
        private String user_id;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("lastname")
        @Expose
        private String lastname;
        public void setCountry_code(String country_code){
            this.country_code=country_code;
        }
        public String getCountry_code(){
            return country_code;
        }
        public void setFirstname(String firstname){
            this.firstname=firstname;
        }
        public String getFirstname(){
            return firstname;
        }
        public void setProfile_image(String profile_image){
            this.profile_image=profile_image;
        }
        public String getProfile_image(){
            return profile_image;
        }
        public void setUser_id(String user_id){
            this.user_id=user_id;
        }
        public String getUser_id(){
            return user_id;
        }
        public void setPhone(String phone){
            this.phone=phone;
        }
        public String getPhone(){
            return phone;
        }
        public void setEmail(String email){
            this.email=email;
        }
        public String getEmail(){
            return email;
        }
        public void setLastname(String lastname){
            this.lastname=lastname;
        }
        public String getLastname(){
            return lastname;
        }
    }

}