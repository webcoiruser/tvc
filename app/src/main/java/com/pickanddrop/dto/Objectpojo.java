package com.pickanddrop.dto;

import java.util.ArrayList;

public class Objectpojo {

    public ArrayList<MultipleDTO> deliveryDTOArrayList;
    public Objectpojo() {
    }

    public Objectpojo( ArrayList<MultipleDTO> deliveryDTOArrayList) {
        this.deliveryDTOArrayList = deliveryDTOArrayList;
    }

    public ArrayList<MultipleDTO> getArrayPjoArrayList() {
        return deliveryDTOArrayList;
    }


    public void setArrayPjoArrayList(ArrayList<MultipleDTO> deliveryDTOArrayList
    ) {
        this.deliveryDTOArrayList = deliveryDTOArrayList;
    }
}




