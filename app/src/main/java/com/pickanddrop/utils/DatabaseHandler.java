package com.pickanddrop.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import com.pickanddrop.dto.MultipleDTO;


import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "dropMultiple";
    private static final String TABLE_CONTACTS = "droptables";
    //   private static final String KEY_ID = "id";
    private static final String KEY_NAME = "dropoff_first_name";
    private static final String KEY_NUMBER = "dropoff_mob_number";
    private static final String KEY_DROP = "dropoffaddress";
    private static final String KEY_PRICE = "delivery_cost";
    private static final String KEY_DROP_BUILDING_TYPE = "drop_building_type";
    private static final String KEY_CLASS_GOOD = "classGoods";
    private static final String KEY_DRIVER_DELIVERY_COST = "driver_delivery_cost";
    private static final String KEY_TYPE_GOODS = "typeGoods";
    private static final String KEY_NO_OF_PALLETS = "noOfPallets";
    private static final String KEY_NO_OF_PALLETS1 = "noOfPallets1";
    private static final String KEY_PARCEL_WEIGHT = "parcel_weight";

    private static final String KEY_PARCEL_WIDTH = "parcel_width";
    private static final String KEY_PARCEL_HEIGHT = "parcel_height";
    private static final String KEY_PARCEL_LENGTH = "parcel_length";

    private static final String KEY_STOP_DISTANCE = "stop_distance";

    private static final String KEY_DROP_OFF_LAT = "dropOffLat";
    private static final String KEY_DROP_OFF_LONG = "dropOffLong";
    private static final String KEY_COUNTRY_CODE = "countryCode";
    private static final String KEY_PICKUP_SPECIAL_INST = "pickup_special_inst";
    private static final String KEY_DROP_SPECIAL_INST = "drop_special_inst";
    private static final String KEY_DROP_ELEVATOR = "drop_elevator";
    private static final String KEY_WEIGHT_UNIT = "weight_unit";
    private static final String KEY_TYPEGOODSCATEGORY = "typeGoodsCategory";


    private static final String LOG = "";

//                        deliveryDTO.setDropoffMobNumber(mobile);
//                        deliveryDTO.setDropoffaddress(dropOffAddress);
//                        deliveryDTO.setVehicleType(vehicleType);
//                        deliveryDTO.setDropoffLat(dropOffLat);
//                        deliveryDTO.setDropoffLong(dropOffLong);
//                        deliveryDTO.setDropoffCountryCode(countryCode);
//                        deliveryDTO.setDropBuildingType(dropBuildingType);
//                        deliveryDTO.setDropElevator(dropElevator);

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_NAME + " TEXT,"+KEY_NUMBER +" TEXT ,"
                + KEY_DROP + " TEXT," + KEY_PRICE +" TEXT, " + KEY_DROP_BUILDING_TYPE + " TEXT ,"
                +  KEY_CLASS_GOOD +" TEXT,"+KEY_DRIVER_DELIVERY_COST +" TEXT," + KEY_TYPE_GOODS +" TEXT, " + KEY_NO_OF_PALLETS + " TEXT ,"
                + KEY_PARCEL_WEIGHT + " TEXT," + KEY_DROP_OFF_LAT +" TEXT, " + KEY_DROP_OFF_LONG + " TEXT ,"
                + KEY_COUNTRY_CODE + " TEXT," + KEY_PICKUP_SPECIAL_INST +" TEXT, " + KEY_DROP_SPECIAL_INST +" TEXT, "
                + KEY_DROP_ELEVATOR + " TEXT, " + KEY_WEIGHT_UNIT + " TEXT ," + KEY_TYPEGOODSCATEGORY + " TEXT, "
                + KEY_PARCEL_WIDTH + " TEXT, "
                + KEY_PARCEL_HEIGHT + " TEXT, "
                + KEY_PARCEL_LENGTH + " TEXT, "
                + KEY_STOP_DISTANCE + " TEXT, "+ KEY_NO_OF_PALLETS1 + " TEXT "
                +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }


//    private static final String KEY_CLASS_GOOD = "classGoods";
//    private static final String KEY_TYPE_GOODS = "typeGoods";
//    private static final String KEY_NO_OF_PALLETS = "noOfPallets";
//    private static final String KEY_PARCEL_WEIGHT = "parcel_weight";
//    private static final String KEY_DROP_OFF_LAT = "dropOffLat";
//    private static final String KEY_DROP_OFF_LONG = "dropOffLong";
//    private static final String KEY_COUNTRY_CODE = "countryCode";
//    private static final String KEY_PICKUP_SPECIAL_INST = "pickup_special_inst";
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    public void delete() {
        // Drop older table if existed
           SQLiteDatabase db = this.getWritableDatabase();
           db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
           onCreate(db);
//        db.execSQL("delete from "+ TABLE_CONTACTS);
        // Create tables again
    }
/*    public Cursor fetch() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_DROP}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }*/

    public int getpalletscount(){
        int value = 0;

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if(cursor != null){

                   if(cursor.getString(7).equalsIgnoreCase("pallet")) {
                        if (!TextUtils.isEmpty(cursor.getString(7))) {
                            //if(cursor.getString(7).equalsIgnoreCase("pallet"))
                            {
                                value = value + Integer.parseInt(cursor.getString(8));

                            }//adding 2nd column data
                        }
                    }
                }


            } while (cursor.moveToNext());
        }
        // closing conn// closing connection        // closing connection// closing c// closing connection        // closin// closing connection        // closing connectiong connectiononnection        // closing connection// closing connection        // closing connection// closing connection        // closing connection// closing connection        // closing connectionection  // closing connection        // closing connection// closing connection        // closing connection      // closing connection

        cursor.close();
        db.close();
        // returning lables
        return value;
    }

    public int getboxcount(){
        int value = 0;
//        int value1 = 0;
//        int value2 = 0;
//        int value3 = 0;


        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                    if(cursor != null){
                        if(!TextUtils.isEmpty(cursor.getString(7))){
                           if(cursor.getString(7).equalsIgnoreCase("box")){
                                value = value + Integer.parseInt(cursor.getString(8));

                            }//adding 2nd column data


                        }
                    }



                //adding 2nd column data
            } while (cursor.moveToNext());

          // value = value;
        }
        // closing connection
        cursor.close();
        db.close();



        // returning lables
        return value;
    }


    public int getpalletandboxcount(){
        int value = 0;

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if(cursor != null){

                     if(cursor.getString(7).equalsIgnoreCase("pallet and box")){
                        value = value + Integer.parseInt(cursor.getString(22));



                    }
                }


            } while (cursor.moveToNext());

        }
        // closing conn// closing connection        // closing connection// closing c// closing connection        // closin// closing connection        // closing connectiong connectiononnection        // closing connection// closing connection        // closing connection// closing connection        // closing connection// closing connection        // closing connectionection  // closing connection        // closing connection// closing connection        // closing connection      // closing connection

        cursor.close();
        db.close();
        // returning lables
        return value;
    }


    public int getpalletandboxcount1(){
        int value = 0;


        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if(cursor != null){

                    if(cursor.getString(7).equalsIgnoreCase("pallet and box")){

                        value = value + Integer.parseInt(cursor.getString(8));


                    }
                }


            } while (cursor.moveToNext());

        }
        // closing conn// closing connection        // closing connection// closing c// closing connection        // closin// closing connection        // closing connectiong connectiononnection        // closing connection// closing connection        // closing connection// closing connection        // closing connection// closing connection        // closing connectionection  // closing connection        // closing connection// closing connection        // closing connection      // closing connection

        cursor.close();
        db.close();
        // returning lables
        return value;
    }



    public List<String> getAllDropnames(){
        List<String> list = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));//adding 2nd column data
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return list;
    }

    public List<String> getAllLabels(){
        List<String> list = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(2));//adding 2nd column data
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return list;
    }



/*    public List<ToDo> getAllToDos() {
        List<ToDo> ToDos = new ArrayList<ToDo>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ToDo td = new ToDo();
                td.setDrop_addres(c.getString((c.getColumnIndex(KEY_DROP))));
                td.setDrop_name(c.getString((c.getColumnIndex(KEY_NAME))));
                td.setDrop_price(c.getString((c.getColumnIndex(KEY_PRICE))));
                td.setPhone_number(c.getString((c.getColumnIndex(KEY_NUMBER))));

                // adding to ToDo list
                ToDos.add(td);
            } while (c.moveToNext());
        }

        return ToDos;
    }*/



/*    public ArrayList getRegistrationData(){
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery,null,null);
        ArrayList dataModelArrayList = new ArrayList();
        if(cursor.moveToFirst()){
            do {
                MultipleDTO regdm = new MultipleDTO();
                regdm.setDropaddress(cursor.getString(cursor.getColumnIndex(KEY_DROP)));
                dataModelArrayList.add(regdm);

            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return dataModelArrayList;
    }*/


    public void deletetitle(String placeid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_NUMBER + "=\"" + placeid+"\"", null) ;
    }


    // code to add the new contact
    public void addContact(MultipleDTO contact) {
        SQLiteDatabase db = this.getWritableDatabase();



        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getDropoffFirstName());
        values.put(KEY_NUMBER, contact.getDropoffMobNumber());
        values.put(KEY_DROP, contact.getDropoffaddress());
        values.put(KEY_PRICE, contact.getDeliveryCost());
        values.put(KEY_DROP_BUILDING_TYPE, contact.getDropBuildingType());
        values.put(KEY_CLASS_GOOD, contact.getClassGoods());
        values.put(KEY_TYPE_GOODS, contact.getTypeGoods());
        values.put(KEY_NO_OF_PALLETS, contact.getNoOfPallets());
        values.put(KEY_NO_OF_PALLETS1, contact.getNoOfPallets1());
        values.put(KEY_PARCEL_WEIGHT, contact.getProductWeight());
        values.put(KEY_DROP_OFF_LAT, contact.getDropoffLat());
        values.put(KEY_DROP_OFF_LONG, contact.getDropoffLong());
        values.put(KEY_COUNTRY_CODE, contact.getDropoffCountryCode());
        values.put(KEY_PICKUP_SPECIAL_INST, contact.getPickupSpecialInst());
        values.put(KEY_DROP_SPECIAL_INST, contact.getDropoff_special_inst());
        values.put(KEY_DROP_ELEVATOR, contact.getDropElevator());
        values.put(KEY_WEIGHT_UNIT,contact.getWeight_unit());
        values.put(KEY_TYPEGOODSCATEGORY,contact.getTypeGoodsCategory());

        values.put(KEY_PARCEL_WIDTH, contact.getProductWidth());
        values.put(KEY_PARCEL_HEIGHT,contact.getProductHeight());
        values.put(KEY_PARCEL_LENGTH,contact.getProductLength());
        values.put(KEY_STOP_DISTANCE,contact.getStopDistance());


        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    // code to get all contacts in a list view
    public ArrayList<MultipleDTO> getAllContacts() {
        ArrayList<MultipleDTO> contactList = new ArrayList<MultipleDTO>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                MultipleDTO contact = new MultipleDTO();
                contact.setDropoffFirstName(cursor.getString(0));
                contact.setDropoffMobNumber(cursor.getString(1));
                contact.setDropoffaddress(cursor.getString(2));
                contact.setDeliveryCost(cursor.getString(3));
                //contact.setDeliveryCost(MultipleAdd.totalPrice);
                contact.setDropBuildingType(cursor.getString(4));
                contact.setClassGoods(cursor.getString(5));
                contact.setTypeGoods(cursor.getString(7));
                contact.setNoOfPallets(cursor.getString(8));
                contact.setNoOfPallets1(cursor.getString(22));
                contact.setProductWeight(cursor.getString(9));
                contact.setDropoffLat(cursor.getString(10));
                contact.setDropoffLong(cursor.getString(11));

                contact.setDropoffCountryCode(cursor.getString(12));
                contact.setPickupSpecialInst(cursor.getString(13));
                contact.setDropoff_special_inst(cursor.getString(14));
                contact.setDropElevator(cursor.getString(15));
                contact.setWeight_unit(cursor.getString(16));
                contact.setTypeGoodsCategory(cursor.getString(17));

                contact.setProductWidth(cursor.getString(18));
                contact.setProductHeight(cursor.getString(19));
                contact.setProductLength(cursor.getString(20));
                contact.setStopDistance(cursor.getString(21));

//
//                values.put(KEY_NAME, contact.getDropoffFirstName());
//                values.put(KEY_NUMBER, contact.getDropoffMobNumber());
//                values.put(KEY_DROP, contact.getDropoffaddress());
//                values.put(KEY_PRICE, contact.getDeliveryCost());
//                values.put(KEY_DROP_BUILDING_TYPE, contact.getDropBuildingType());
//                values.put(KEY_CLASS_GOOD, contact.getClassGoods());
//                values.put(KEY_TYPE_GOODS, contact.getTypeGoods());
//                values.put(KEY_NO_OF_PALLETS, contact.getNoOfPallets());
//                values.put(KEY_PARCEL_WEIGHT, contact.getProductWeight());
//                values.put(KEY_DROP_OFF_LAT, contact.getDropoffLat());
//                values.put(KEY_DROP_OFF_LONG, contact.getDropoffLong());
//                values.put(KEY_COUNTRY_CODE, contact.getDropoffCountryCode());
//                values.put(KEY_PICKUP_SPECIAL_INST, contact.getPickupSpecialInst());
//                values.put(KEY_DROP_ELEVATOR, contact.getDropElevator());

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


}