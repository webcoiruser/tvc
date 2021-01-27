package com.pickanddrop.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliverySendMultipleDataDTO {
    @SerializedName("code")
    @Expose
    String data;
    @SerializedName("data")
    @Expose
    DeliveryMultipleDTO deliveryMultipleDTO;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public DeliveryMultipleDTO getDeliveryMultipleDTO() {
        return deliveryMultipleDTO;
    }

    public void setDeliveryMultipleDTO(DeliveryMultipleDTO deliveryMultipleDTO) {
        this.deliveryMultipleDTO = deliveryMultipleDTO;
    }
}
