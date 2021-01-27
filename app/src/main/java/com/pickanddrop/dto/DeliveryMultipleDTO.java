package com.pickanddrop.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DeliveryMultipleDTO {

    @SerializedName("pickData")
    @Expose
    DeliveryDTO.Data deliveryDTO;
    @SerializedName("dropdata")
    @Expose
    ArrayList<MultipleDTO> multipleDTOArrayList;

    public DeliveryDTO.Data getDeliveryDTO() {
        return deliveryDTO;
    }

    public void setDeliveryDTO(DeliveryDTO.Data deliveryDTO) {
        this.deliveryDTO = deliveryDTO;
    }

    public ArrayList<MultipleDTO> getMultipleDTOArrayList() {
        return multipleDTOArrayList;
    }

    public void setMultipleDTOArrayList(ArrayList<MultipleDTO> multipleDTOArrayList) {
        this.multipleDTOArrayList = multipleDTOArrayList;
    }
}
