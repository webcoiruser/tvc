package com.pickanddrop.dto;

public class ToDo {
    String drop_name;
    String phone_number;
    String drop_addres;
    String drop_price;

    public ToDo(String drop_name, String phone_number, String drop_addres, String drop_price) {
        this.drop_name = drop_name;
        this.phone_number = phone_number;
        this.drop_addres = drop_addres;
        this.drop_price = drop_price;
    }

    public String getDrop_name() {
        return drop_name;
    }

    public void setDrop_name(String drop_name) {
        this.drop_name = drop_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDrop_addres() {
        return drop_addres;
    }

    public void setDrop_addres(String drop_addres) {
        this.drop_addres = drop_addres;
    }

    public String getDrop_price() {
        return drop_price;
    }

    public void setDrop_price(String drop_price) {
        this.drop_price = drop_price;
    }
}
