package com.pickanddrop.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BoxSubCatDTO {

@SerializedName("result")
@Expose
    private String result;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    ArrayList<DataBox> data = new ArrayList<DataBox>();
    @SerializedName("message")
    @Expose
    private String message;


    // Getter Methods

    public String getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    // Setter Methods

    public void setResult( String result ) {
        this.result = result;
    }

    public void setError( String error ) {
        this.error = error;
    }

    public ArrayList<DataBox> getData() {
        return data;
    }

    public void setData(ArrayList<DataBox> data) {
        this.data = data;
    }

    public void setMessage(String message ) {
        this.message = message;
    }



    public class DataBox{
        @SerializedName("box_type")
        @Expose
        private String box_type;
        @SerializedName("measure")
        @Expose
        private String measure;
        @SerializedName("box_short_code")
        @Expose
        private String box_short_code;
        @SerializedName("full_name")
        @Expose
        private String full_name;

        // Getter Methods

        public String getBox_type() {
            return box_type;
        }

        public String getMeasure() {
            return measure;
        }

        public String getBox_short_code() {
            return box_short_code;
        }

        public String getFull_name() {
            return full_name;
        }

        // Setter Methods

        public void setBox_type( String box_type ) {
            this.box_type = box_type;
        }

        public void setMeasure( String measure ) {
            this.measure = measure;
        }

        public void setBox_short_code( String box_short_code ) {
            this.box_short_code = box_short_code;
        }

        public void setFull_name( String full_name ) {
            this.full_name = full_name;
        }

    }


}
