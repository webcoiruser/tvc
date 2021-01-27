package com.pickanddrop.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.activities.SelectLocation;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.CreateOrderExpressDeliveryDropBinding;
import com.pickanddrop.dto.BoxSubCatDTO;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.MultipleDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.dto.PriceDistanceDTO;
import com.pickanddrop.dto.PricesDTO;
import com.pickanddrop.dto.VechicalCatagoryDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.CustomSpinnerForAll;
import com.pickanddrop.utils.DataParser;
import com.pickanddrop.utils.DatabaseHandler;
import com.pickanddrop.utils.DrawableClickListener;
import com.pickanddrop.utils.MyConstant;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.EditText;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.location.places.ui.PlacePicker.getPlace;

public class CreateOrderExpressDeliveryDrop extends BaseFragment implements AppConstants, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnInfoWindowClickListener {

    private Context context;
    private AppSession appSession;
    ArrayList<MultipleDTO> contacts = new ArrayList<>();
    private Utilities utilities;
    private CreateOrderExpressDeliveryDropBinding createOrderExpressDeliveryDropBinding;
    private String countryCode = "",deliveryType = "",typeGood = "",vehicleCategory="",dropOffLat = "",
            pickupLong = "",specialinsdrop="", pickupLat = "",dropOffLong = "",pickUpAddress="",
            companyName = "", firstName = "", lastName = "", mobile = "", dropOffAddress = "",
            dropOffSpecialInstruction = "", vehicleType = "", parcelHeight = "", parcelWidth = "",
            parcelWeight = "", parcelLenght = "", ClassGood = "",ptypeofbox = "", productMeasure = "",
            TypeGood="",NoofPallets = "",NoofPallets1 = "", dropoffLiftGate="",dropElevator="no",dropBuildingType="no",
            productWidth = "", productHeight = "", productLength = "",productWeight = "",productWeight2 = "",productKg = "",
            alertdropoffLat = "", alertdropoffLong = "";
    private String weightunit = "pound";
    public static String distance_value= "";
    private DeliveryDTO.Data deliveryDTO;
    private VechicalCatagoryDTO.Data vechicalCatagoryDTO;
    private PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    private static final int REQUEST_PICK_PLACE = 1142;
    private CustomSpinnerForAll customSpinnerAdapter;
    private ArrayList<HashMap<String, String>> vehicleCategoryList;
    private ArrayList<HashMap<String, String>> vehicleList;
    private boolean rescheduleStatus = false;
    private String TAG = CreateOrderSecond.class.getName();
    //    private OtherDTO otherDTO;
    //private DeliveryDTO.Data data;
//    private PriceDistanceDTO priceDistanceDTO;
    private Double totalDeliveryCost = 0.0, driverDeliveryCost = 0.0;
    final ArrayList<String> listAll = new ArrayList<String>();
    HashMap<String, String> hashMap1 = new HashMap<>();
    String multiple ;
    TextView tv_pickup_location;
    DatabaseHandler db;
    AlertDialog.Builder alert_builder;
    AlertDialog alertDialog;
    MapView mv_home;
    ImageView alertBack;
    private Button btnSelectLocation;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<HashMap<String, String>> classTypeList;

    final ArrayList<String> listAll3 = new ArrayList<String>();
    HashMap<String, String> hashMapbox = new HashMap<>();
    private ArrayList<HashMap<String, String>> parcelTypeList1;
    private ArrayList<HashMap<String, String>> ptypeofboxTypeList;
    private ArrayList<HashMap<String, String>> poundTypeList;
    EditText etPalletsCountnew1,etPalletsCountnew2;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null && getArguments().containsKey("delivery_type")) {
//            deliveryType = getArguments().getString("delivery_type");
//            Log.e(TAG, deliveryType);
//        }

        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            deliveryDTO = getArguments().getParcelable("deliveryDTO");
            deliveryType = getArguments().getString("delivery_type");
            ClassGood = deliveryDTO.getClassGoods();
            Log.d(TAG, "onCreate----->>>>>>>: "+new Gson().toJson(deliveryDTO));// check data
            productMeasure = deliveryDTO.getTypeGoods();

            Log.e(TAG, deliveryType);
            if(deliveryType == "multiple1")
            {
                Bundle bundle = new Bundle();
                bundle.putString("delivery_type", "multiple1");
            }

            if (deliveryType != "multiple1") {
                if (getArguments().containsKey("rescheduleStatus")) {
                    rescheduleStatus = true;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createOrderExpressDeliveryDropBinding = DataBindingUtil.inflate(inflater, R.layout.create_order_express_delivery_drop, container, false);
        return createOrderExpressDeliveryDropBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        classTypeList = new ArrayList<>();
        ptypeofboxTypeList = new ArrayList<>();
        parcelTypeList1 = new ArrayList<>();
        poundTypeList = new ArrayList<>();

        alert_builder = new AlertDialog.Builder(context,R.style.DialogTheme);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.select_location_with_map,null);
        mv_home = dialogView.findViewById(R.id.mv_home);
        tv_pickup_location = dialogView.findViewById(R.id.tv_pickup_location);
        btnSelectLocation = dialogView.findViewById(R.id.btn_select_location);
        alertBack = dialogView.findViewById(R.id.alert_back);
        btnSelectLocation.setOnClickListener(this);
        tv_pickup_location.setOnClickListener(this);
        createOrderExpressDeliveryDropBinding.btnAdd.setOnClickListener(this);
        alertBack.setOnClickListener(this);
        setMap();
        alert_builder.setView(dialogView);
        alertDialog = alert_builder.create();


        initView();
        initToolBar();


        if (deliveryType == "multiple1") {
            setValues();
            multiple = "multiple1";
            Log.d("Reading: ", "Reading all contacts..");
            contacts = db.getAllContacts();

        }else {

            if (rescheduleStatus) {
                setValues();


            } else {
                //getPrice11();

            }
        }

        if(deliveryType == "multiple"){
            setListClassGoods();

        }


        final ArrayList<String> listAllKg = new ArrayList<String>();
        HashMap<String, String> hashMapkg = new HashMap<>();
        hashMapkg.put(PN_NAME, "");
        hashMapkg.put(PN_VALUE, getResources().getString(R.string.lb));
        poundTypeList.add(hashMapkg);
        listAllKg.add(getResources().getString(R.string.lb));

        hashMapkg = new HashMap<>();
        hashMapkg.put(PN_NAME, "");
        hashMapkg.put(PN_VALUE, getResources().getString(R.string.Kg));
        poundTypeList.add(hashMapkg);
        listAllKg.add(getResources().getString(R.string.Kg));

        System.out.println("productMeasure2" + productMeasure);
        ArrayAdapter adapterkg = new ArrayAdapter<String>(context, R.layout.spinner_text);
        adapterkg.setDropDownViewResource(R.layout.spinner_item_list);
        adapterkg.addAll(listAllKg);
        createOrderExpressDeliveryDropBinding.spKgPound.setAdapter(adapterkg);
        createOrderExpressDeliveryDropBinding.spKgPound2.setAdapter(adapterkg);

        // for box2
        for (int i = 0; i < poundTypeList.size(); i++) {
            System.out.println("sdlkfjlskdfl--->"+deliveryDTO.getWeight_unit());
            if (deliveryDTO.getWeight_unit() != null && deliveryDTO.getWeight_unit().equalsIgnoreCase(poundTypeList.get(i).get(PN_VALUE))) {
                createOrderExpressDeliveryDropBinding.spKgPound2.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < poundTypeList.size(); i++) {
            if (deliveryDTO.getClassGoods() != null && deliveryDTO.getClassGoods().equalsIgnoreCase(poundTypeList.get(i).get(PN_VALUE))) {
                createOrderExpressDeliveryDropBinding.spKgPound2.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < poundTypeList.size(); i++) {
            System.out.println("sdlkfjlskdfl--->"+deliveryDTO.getWeight_unit());
            if (deliveryDTO.getWeight_unit() != null && deliveryDTO.getWeight_unit().equalsIgnoreCase(poundTypeList.get(i).get(PN_VALUE))) {
                createOrderExpressDeliveryDropBinding.spKgPound.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < poundTypeList.size(); i++) {
            if (deliveryDTO.getClassGoods() != null && deliveryDTO.getClassGoods().equalsIgnoreCase(poundTypeList.get(i).get(PN_VALUE))) {
                createOrderExpressDeliveryDropBinding.spKgPound.setSelection(i);
                break;
            }
        }






        createOrderExpressDeliveryDropBinding.spKgPound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                productKg = poundTypeList.get(i).get(PN_VALUE);
                if (productKg.equalsIgnoreCase("Pound")) {
                    weightunit = "pound";
                    createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setHint("Enter Pound");

                } else if (productKg.equalsIgnoreCase("Kilogram")) {
                    weightunit = "kilogram";
                    createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setHint("Enter Kilogram");

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (productKg != null & !productKg.equals(""))
                    productKg = poundTypeList.get(0).get(PN_VALUE);
            }
        });

        createOrderExpressDeliveryDropBinding.spKgPound2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                productKg = poundTypeList.get(i).get(PN_VALUE);
                if (productKg.equalsIgnoreCase("Pound")) {
                    weightunit = "pound";
                    createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setHint("Enter Pound");

                } else if (productKg.equalsIgnoreCase("Kilogram")) {
                    weightunit = "kilogram";
                    createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setHint("Enter Kilogram");

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (productKg != null & !productKg.equals(""))
                    productKg = poundTypeList.get(0).get(PN_VALUE);
            }
        });


    }

    private void setListClassGoods(){

        createOrderExpressDeliveryDropBinding.deliveryItemDetailsContainer.setVisibility(View.VISIBLE);

        List<String> labelcount =  db.getAllLabels();
        if(labelcount.size()>=20){
            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);

        }


        final ArrayList<String> listAll = new ArrayList<String>();
        HashMap<String, String> hashMap2 = new HashMap<>();

        listAll.clear();
        hashMap2.clear();
        parcelTypeList1.clear();

        hashMap2 = new HashMap<>();
        hashMap2.put(PN_NAME, "");
        hashMap2.put(PN_VALUE, getResources().getString(R.string.type_of_parcel));
        parcelTypeList1.add(hashMap2);
        listAll.add(getResources().getString(R.string.type_of_parcel));

        // Nandhakumar
        if(deliveryType.equalsIgnoreCase("express")) {

            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.box));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.box));

            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.envelop));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.envelop));
        }


        // Nandha use Only Miscellanea
        if(deliveryType.equalsIgnoreCase("miscellaneous")) {
            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, "miscellaneous");
            parcelTypeList1.add(hashMap2);
            listAll.add("miscellaneous");
        }
        if (deliveryDTO.getTypeGoods().equalsIgnoreCase("box") || deliveryDTO.getTypeGoods().equalsIgnoreCase("pallet")
                ||deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet And Box")){
            String ltrim , rtrim;
             ltrim =  deliveryDTO.getTypeGoods().replaceAll("^\\s+","");

            if (deliveryDTO.getTypeGoods().equalsIgnoreCase("box")){
                hashMap2 = new HashMap<>();
                hashMap2.put(PN_NAME, "");
                hashMap2.put(PN_VALUE, getResources().getString(R.string.box));
                parcelTypeList1.add(hashMap2);
                listAll.add(getResources().getString(R.string.box));
            }
            else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet")){

                hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet));
           parcelTypeList1.add(hashMap2);
           listAll.add(getResources().getString(R.string.pallet));

            }else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet And Box")){
                hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet_and_box));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.pallet_and_box));


                hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.box));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.box));

            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.pallet));



            }

//
//
//            hashMap2 = new HashMap<>();
//            hashMap2.put(PN_NAME, "");
//            hashMap2.put(PN_VALUE, getResources().getString(R.string.box));
//            parcelTypeList1.add(hashMap2);
//            listAll.add(getResources().getString(R.string.box));
//
//            hashMap2 = new HashMap<>();
//            hashMap2.put(PN_NAME, "");
//            hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet));
//            parcelTypeList1.add(hashMap2);
//            listAll.add(getResources().getString(R.string.pallet));
//
//            hashMap2 = new HashMap<>();
//            hashMap2.put(PN_NAME, "");
//            hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet_and_box));
//            parcelTypeList1.add(hashMap2);
//            listAll.add(getResources().getString(R.string.pallet_and_box));
        }




        if(!deliveryType.equalsIgnoreCase("express") && !deliveryType.equalsIgnoreCase("miscellanea") && !deliveryType.equalsIgnoreCase("multiple")) {

            if(deliveryDTO.getTypeGoods().equalsIgnoreCase("pallet")) {
                hashMap2 = new HashMap<>();
                hashMap2.put(PN_NAME, "");
                hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet));
                parcelTypeList1.add(hashMap2);
                listAll.add(getResources().getString(R.string.pallet));
            }

            if(deliveryDTO.getTypeGoods().equalsIgnoreCase("box")) {
                hashMap2 = new HashMap<>();
                hashMap2.put(PN_NAME, "");
                hashMap2.put(PN_VALUE, getResources().getString(R.string.box));
                parcelTypeList1.add(hashMap2);
                listAll.add(getResources().getString(R.string.box));
            }
            if (deliveryDTO.getTypeGoods().equalsIgnoreCase("PalletAndBox")){
                hashMap2 = new HashMap<>();
                hashMap2.put(PN_NAME, "");
                hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet_and_box));
                parcelTypeList1.add(hashMap2);
                listAll.add(getResources().getString(R.string.pallet_and_box));
            }

        }


        for (int i = 0; i < parcelTypeList1.size(); i++) {
            System.out.println("parcelTypeList1--->" + parcelTypeList1.size());
            if (deliveryDTO.getTypeGoods() != null && deliveryDTO.getTypeGoods().equalsIgnoreCase(parcelTypeList1.get(i).get(PN_VALUE))) {
                createOrderExpressDeliveryDropBinding.spType1.setSelection(i);
                System.out.println("parcelTypeList1--->" + parcelTypeList1.get(i).get(PN_VALUE));
                break;
            }
        }




        System.out.println("productMeasure1" + productMeasure);


        ArrayAdapter adapter1 = new ArrayAdapter<String>(context, R.layout.spinner_text, listAll);
        adapter1.setDropDownViewResource(R.layout.spinner_item_list);
        createOrderExpressDeliveryDropBinding.spType1.setAdapter(adapter1);

        System.out.println("adapter1234" + adapter1);


        createOrderExpressDeliveryDropBinding.spType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    productMeasure = parcelTypeList1.get(i).get(PN_VALUE);
                    System.out.println("productMeasure11" + productMeasure);

                    if(deliveryType.equalsIgnoreCase("express")) {

                    }else {

                        if (productMeasure.equalsIgnoreCase("Box")) {

                            // if add the categoriews of box plz remave this code
                            createOrderExpressDeliveryDropBinding.etPalletsCount.setHint("Number Of Box");
                            createOrderExpressDeliveryDropBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);
                            // End


                            createOrderExpressDeliveryDropBinding.etGoodWidth.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etGoodHight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etGoodLength.setVisibility(View.VISIBLE);
                            //createOrderExpressDeliveryDropBinding.etNumberOfGoods.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.VISIBLE);
                            selectbox();



                            // disable ui
                            createOrderExpressDeliveryDropBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setVisibility(View.GONE);

                        } else if (productMeasure.equalsIgnoreCase("miscellaneous")) {
                            createOrderExpressDeliveryDropBinding.etGoodWidth.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etGoodHight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etGoodLength.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.spKgPound.setVisibility(View.VISIBLE);
                            //createOrderExpressDeliveryDropBinding.etNumberOfGoods.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPalletsCount.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.spType1.setVisibility(View.GONE);




                            // disable ui
                            createOrderExpressDeliveryDropBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setVisibility(View.GONE);


                        } else if (productMeasure.equalsIgnoreCase("Pallet")) {
                            createOrderExpressDeliveryDropBinding.etPalletsCount.setHint("Number of Pallet");
                            createOrderExpressDeliveryDropBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etGoodWidth.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etGoodHight.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etGoodLength.setVisibility(View.GONE);
                            //createOrderExpressDeliveryDropBinding.etNumberOfGoods.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.GONE);


                            // disable ui
                            createOrderExpressDeliveryDropBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setVisibility(View.GONE);

                        } else if (productMeasure.equalsIgnoreCase("Pallet And Box")){


                            // if add the categoriews of box plz remave this code
//                            createOrderExpressDeliveryDropBinding.etPalletsCount.setHint("Number Of Box");
//                            createOrderExpressDeliveryDropBinding.etPalletsCount.setVisibility(View.VISIBLE);
//                            //createOrderExpressDeliveryDropBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryDropBinding.spKgPound.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);
//                            // End
//
//
//
//                            createOrderExpressDeliveryDropBinding.etGoodWidth.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryDropBinding.etGoodHight.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryDropBinding.etGoodLength.setVisibility(View.VISIBLE);
//                            //createOrderExpressDeliveryDropBinding.etNumberOfGoods.setVisibility(View.GONE);
//                            createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.VISIBLE);
//                            selectbox();


                            // if add the categoriews of box plz remave this code
                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setHint("Number Of Box");
                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.spKgPound2.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setVisibility(View.VISIBLE);
                            // End


                            createOrderExpressDeliveryDropBinding.etGoodWidth.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etGoodHight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etGoodLength.setVisibility(View.VISIBLE);
                            //createOrderExpressDeliveryDropBinding.etNumberOfGoods.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.VISIBLE);
                            selectbox();
                            getBoxSubCatogryApi();




                            createOrderExpressDeliveryDropBinding.etPalletsCount.setHint("Number of Pallet");
                            createOrderExpressDeliveryDropBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);

//                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setHint("Number of Pallet");
//                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
////                            createOrderExpressDeliveryDropBinding.etGoodWidth.setVisibility(View.GONE);
////                            createOrderExpressDeliveryDropBinding.etGoodHight.setVisibility(View.GONE);
////                            createOrderExpressDeliveryDropBinding.etGoodLength.setVisibility(View.GONE);
//                            //createOrderExpressDeliveryDropBinding.etNumberOfGoods.setVisibility(View.GONE);
//                            //createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.GONE);
//                            createOrderExpressDeliveryDropBinding.spKgPound2.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setVisibility(View.VISIBLE);

//
//                            // disable ui
//                            createOrderExpressDeliveryDropBinding.spKgPound2.setVisibility(View.GONE);
//                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setVisibility(View.GONE);
//                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setVisibility(View.GONE);


                        }

                    }
                } else {
                    ((TextView) view).setTextSize(16);
                    ((TextView) view).setTextColor(
                            getResources().getColorStateList(R.color.text_hint)
                    );

                    productMeasure = "";
                    createOrderExpressDeliveryDropBinding.etGoodWidth.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.etGoodHight.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.etGoodLength.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.etPalletsCount.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.etPalletsCount2.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.etPickSpecialInst.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.spKgPound.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.spKgPound2.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
             /*   if(productMeasure != null & !productMeasure.equals(""))
                    productMeasure = measureTypeList1.get(0).get(PN_VALUE);*/

            }
        });


        /*final ArrayList<String> listAll1 = new ArrayList<String>();
        HashMap<String, String> hashMapClass = new HashMap<>();

        listAll1.clear();
        hashMapClass.clear();
        classTypeList.clear();

        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, getResources().getString(R.string.class_of_goods));
        classTypeList.add(hashMapClass);
        listAll1.add(getResources().getString(R.string.class_of_goods));

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 50");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 50");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 55");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 55");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 60");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 60");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 65");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 65");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 70");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 70");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 77.5");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 77.5");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 85");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 85");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 92.5");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 92.5");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 100");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 100");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 110");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 110");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 125");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 125");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 150");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 150");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 175");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 175");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 200");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 200");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 250");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 250");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 300");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 300");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 400");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 400");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 500");
        classTypeList.add(hashMapClass);
        listAll1.add("Class 500");

        System.out.println("productMeasure3" + productMeasure);

        for (int i = 0; i < classTypeList.size(); i++) {
            if (deliveryDTO.getClassGoods() != null && deliveryDTO.getClassGoods().equalsIgnoreCase(classTypeList.get(i).get(PN_VALUE))) {
                createOrderExpressDeliveryDropBinding.etGoodClass.setSelection(i);
                break;
            }
        }

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            ListPopupWindow popupWindow = (ListPopupWindow) popup.get(createOrderExpressDeliveryDropBinding.etGoodClass);

            // Set popupWindow height to 500px
            popupWindow.setHeight(550);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.spinner_text);
        adapter.setDropDownViewResource(R.layout.spinner_item_list);
        adapter.addAll(listAll1);
        createOrderExpressDeliveryDropBinding.etGoodClass.setAdapter(adapter);


        createOrderExpressDeliveryDropBinding.etGoodClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    ClassGood = classTypeList.get(i).get(PN_VALUE);
                } else {
                    ((TextView) view).setTextSize(16);
                    ((TextView) view).setTextColor(
                            getResources().getColorStateList(R.color.text_hint)
                    );
                    ClassGood = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/



    }


    public void selectbox() {


        listAll3.clear();
        ptypeofboxTypeList.clear();


        System.out.println("productMeasureNandha" + productMeasure);

        if (productMeasure.equalsIgnoreCase("miscellaneous")) {

            hashMapbox = new HashMap<>();
            hashMapbox.clear();
            hashMapbox.put(PN_NAME, "");
            hashMapbox.put(PN_VALUE, getResources().getString(R.string.please_select_box));
            ptypeofboxTypeList.add(hashMapbox);
            listAll3.add(getResources().getString(R.string.please_select_Miscellanea));


            hashMapbox = new HashMap<>();
            hashMapbox.put(PN_NAME, "");
            hashMapbox.put(PN_VALUE, getResources().getString(R.string.other));
            ptypeofboxTypeList.add(hashMapbox);
            listAll3.add(getResources().getString(R.string.other));
        }else {

            if (productMeasure.equalsIgnoreCase("Box")) {

                getBoxSubCatogryApi();

            }
        }


        ArrayAdapter adapterbox = new ArrayAdapter<String>(context, R.layout.spinner_text);
        adapterbox.setDropDownViewResource(R.layout.spinner_item_list);
        adapterbox.addAll(listAll3);
        createOrderExpressDeliveryDropBinding.spTypeOfBox.setAdapter(adapterbox);

        createOrderExpressDeliveryDropBinding.spTypeOfBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    ptypeofbox= "";
                    System.out.println("productMeasureNandha---> " + ptypeofboxTypeList.get(i).get(PN_VALUE));

                    ptypeofbox = ptypeofboxTypeList.get(i).get(PN_NAME);

                    System.out.println("productMeasureNandha---> " + ptypeofbox);

                }else {
                    ((TextView) view).setTextSize(16);
                    ((TextView) view).setTextColor(
                            getResources().getColorStateList(R.color.text_hint)
                    );

                    ptypeofbox = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void getBoxSubCatogryApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {

            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            Map<String, String> map = new HashMap<>();
            map.put("code", APP_TOKEN);
            APIInterface apiInterface = APIClient.getClient();
            Call<BoxSubCatDTO> call = apiInterface.getBoxSubCatogryApi(map);
            call.enqueue(new Callback<BoxSubCatDTO>() {
                @Override
                public void onResponse(Call<BoxSubCatDTO> call, Response<BoxSubCatDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {

                                ArrayList<BoxSubCatDTO.DataBox> boxes = new  ArrayList<BoxSubCatDTO.DataBox>();
                                if(response.body().getData().size() != 0){

                                    for (int boxint = 0 ; boxint <response.body().getData().size();boxint++){
                                        if(boxes.size() != 0){
                                            if(!boxes.contains(response.body().getData().get(boxint))){
                                                boxes.add(response.body().getData().get(boxint));
                                            }
                                        }else {
                                            boxes.add(response.body().getData().get(boxint));
                                        }

                                    }
                                }

                                hashMapbox = new HashMap<>();
                                hashMapbox.clear();
                                hashMapbox.put(PN_NAME, "");
                                hashMapbox.put(PN_VALUE, getResources().getString(R.string.please_select_box));
                                ptypeofboxTypeList.add(hashMapbox);
                                listAll3.add(getResources().getString(R.string.please_select_box));

                                for (int boxsetint = 0;boxsetint<boxes.size();boxsetint++) {
                                    hashMapbox = new HashMap<>();
                                    hashMapbox.put(PN_NAME, boxes.get(boxsetint).getBox_short_code());
                                    hashMapbox.put(PN_VALUE, boxes.get(boxsetint).getFull_name());
                                    ptypeofboxTypeList.add(hashMapbox);
                                    listAll3.add(boxes.get(boxsetint).getFull_name());
                                }

                                ArrayAdapter adapterbox = new ArrayAdapter<String>(context, R.layout.spinner_text);
                                adapterbox.setDropDownViewResource(R.layout.spinner_item_list);
                                adapterbox.addAll(listAll3);
                                createOrderExpressDeliveryDropBinding.spTypeOfBox.setAdapter(adapterbox);


                                createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.VISIBLE);

                                System.out.println("rescheduleStatus----> 0 "+ rescheduleStatus);
                                System.out.println("rescheduleStatus----> 1 "+  new Gson().toJson(deliveryDTO));
                                System.out.println("rescheduleStatus----> 2 "+  deliveryDTO.getTypeGoodsCategory());
                                System.out.println("rescheduleStatus----> 3 " +listAll3.size());



                                if(rescheduleStatus){
                                    for (int i = 0; i < listAll3.size(); i++) {
                                        System.out.println("rescheduleStatus----> 4 " +listAll3.size());

                                        if (deliveryDTO.getTypeGoodsCategory() != null && deliveryDTO.getTypeGoodsCategory().equalsIgnoreCase(ptypeofboxTypeList.get(i).get(PN_NAME))) {

                                            System.out.println("vehicleCategoryList.size().i" +i);
                                            createOrderExpressDeliveryDropBinding.spTypeOfBox.setSelection(i);

                                            break;
                                        } else {
                                            System.out.println("hahahamahhdfgy");

                                        }
                                    }
                                }

                                System.out.println("response.body()---->  "+new Gson().toJson(response.body()));

                            } else {
                                utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<BoxSubCatDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e(TAG, t.toString());
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);

                }
            });
        }
    }


    public void setEnableOrDisable(boolean value) {
        //signUpBinding.etCompany.setEnabled(value);


    }

    public void setMap() {
        try {
            if (mv_home!= null) {
                mv_home.onCreate(null);
                mv_home.onResume();
                mv_home.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setValues() {



        if(deliveryType == "multiple1"){

            for (int i = 0; i < vehicleList.size(); i++) {
                if (deliveryDTO.getVehicleType() != null && deliveryDTO.getVehicleType().equalsIgnoreCase(vehicleList.get(i).get(PN_VALUE))) {
                    createOrderExpressDeliveryDropBinding.spType.setSelection(i);
                    break;
                }
            }
                deliveryDTO.setDeliveryType("multiple");
                deliveryType = deliveryDTO.getDeliveryType();


            /*Log.d("Reading: ", "Reading all contacts..");
            contacts = db.getAllContacts();

                //Set Multple data Binding
            createOrderExpressDeliveryDropBinding.etFirstName.setText(deliveryDTO.getDropoffFirstName());
            createOrderExpressDeliveryDropBinding.etLastName.setText(deliveryDTO.getDropoffLastName());
            createOrderExpressDeliveryDropBinding.etMobile.setText(deliveryDTO.getDropoffMobNumber());
            createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(deliveryDTO.getDropoffaddress());
            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setText(deliveryDTO.getDropoffSpecialInst());

            if (deliveryDTO.getDropBuildingType().equals("no")) {
                createOrderExpressDeliveryDropBinding.rbInsidePickup.setChecked(false);
                dropBuildingType = "no";
            } else {
                createOrderExpressDeliveryDropBinding.rbInsidePickup.setChecked(true);
                System.out.println("inside");
                dropBuildingType = "yes";
            }

            if (deliveryDTO.getDropElevator().equals("yes")) {
                createOrderExpressDeliveryDropBinding.rbLiftGate.setChecked(true);

                dropElevator = "yes";
                System.out.println("inside");
            } else {
                createOrderExpressDeliveryDropBinding.rbLiftGate.setChecked(false);
                dropElevator = "no";
            }

            */

        }else {

            createOrderExpressDeliveryDropBinding.etFirstName.setText(deliveryDTO.getDropoffFirstName());
            createOrderExpressDeliveryDropBinding.etLastName.setText(deliveryDTO.getDropoffLastName());
            createOrderExpressDeliveryDropBinding.etMobile.setText(deliveryDTO.getDropoffMobNumber());
            createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(deliveryDTO.getDropoffaddress());
            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setText(deliveryDTO.getDropoffSpecialInst());

            if (deliveryDTO.getDropBuildingType().equals("no")) {
                createOrderExpressDeliveryDropBinding.rbInsidePickup.setChecked(false);
                dropBuildingType = "no";
            } else {
                createOrderExpressDeliveryDropBinding.rbInsidePickup.setChecked(true);
                System.out.println("inside");
                dropBuildingType = "yes";
            }

            if (deliveryDTO.getDropElevator().equals("yes")) {
                createOrderExpressDeliveryDropBinding.rbLiftGate.setChecked(true);

                dropElevator = "yes";
                System.out.println("inside");
            } else {
                createOrderExpressDeliveryDropBinding.rbLiftGate.setChecked(false);
                dropElevator = "no";
            }

            dropOffLat = deliveryDTO.getDropoffLat();
            dropOffLong = deliveryDTO.getDropoffLong();

            for (int i = 0; i < vehicleList.size(); i++) {
                if (deliveryDTO.getVehicleType() != null && deliveryDTO.getVehicleType().equalsIgnoreCase(vehicleList.get(i).get(PN_VALUE))) {
                    createOrderExpressDeliveryDropBinding.spType.setSelection(i);
                    break;
                }
            }
            if (deliveryDTO.getDeliveryStatus().equals("6")) {
                setEnableing(false);
            }else{
                setEnableing(true);
            }

        }
    }

    public void setEnableing(Boolean value){
        createOrderExpressDeliveryDropBinding.etPickSpecialInst.setEnabled(value);
        createOrderExpressDeliveryDropBinding.rbInsidePickup.setEnabled(value);
        createOrderExpressDeliveryDropBinding.spType.setClickable(value);
        createOrderExpressDeliveryDropBinding.spType.setEnabled(value);
        createOrderExpressDeliveryDropBinding.llType.setEnabled(value);
        createOrderExpressDeliveryDropBinding.etFirstName.setEnabled(value);
        createOrderExpressDeliveryDropBinding.etLastName.setEnabled(value);
        createOrderExpressDeliveryDropBinding.etMobile.setEnabled(value);
        createOrderExpressDeliveryDropBinding.ccp.setEnabled(value);
        createOrderExpressDeliveryDropBinding.rbLiftGate.setEnabled(value);
        createOrderExpressDeliveryDropBinding.rbInsidePickup.setEnabled(value);
        createOrderExpressDeliveryDropBinding.etDropoffAddress.setEnabled(value);
        createOrderExpressDeliveryDropBinding.spType1.setEnabled(value);
    }

    private void initToolBar() {

    }

    private void initView() {
//        createOrderExpressDeliveryDropBinding.ccp.registerPhoneNumberTextView(createOrderExpressDeliveryDropBinding.etMobile);

//        otherDTO = new OtherDTO();
//        priceDistanceDTO = new PriceDistanceDTO();
        db = new DatabaseHandler(getContext());



        if(deliveryType == "express") {
            createOrderExpressDeliveryDropBinding.toolbarTitle.setText("Express Delivery Order");

        }else if(deliveryType == "single") {
            createOrderExpressDeliveryDropBinding.toolbarTitle.setText("Single Delivery Order");

        }else if(deliveryType == "multiple" || deliveryType == "multiple1") {
            createOrderExpressDeliveryDropBinding.toolbarTitle.setText("Multiple Delivery Order");
            List<String> labels =  db.getAllLabels();
            createOrderExpressDeliveryDropBinding.dropNumber.setText("Stop-"+String.valueOf(labels.size()+1)+" Drop off");
            vehicleCategoryList = new ArrayList<>();

            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.VISIBLE);
            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.GONE);

            int noofPallet = Integer.parseInt(deliveryDTO.getNoOfPallets());
            int noofPallet1 = TextUtils.isEmpty(deliveryDTO.getNoOfPallets1()) ? 0 : Integer.parseInt(deliveryDTO.getNoOfPallets1());
            //Integer.parseInt(deliveryDTO.getNoOfPallets1());
            int dropNoPallet1 = db.getpalletscount();
            int dropNoPallet2 = db.getpalletandboxcount();
            int dropNoPallet4 = db.getpalletandboxcount1();
            int dropNoPallet3 = db.getboxcount();
            int dropNoPallet = 0;
            int dropNoPallet5 = 0;

            System.out.println("palletandbox"+noofPallet1+" pallets is "+noofPallet);
            System.out.println("palletandbox db"+dropNoPallet2+" pallets is db "+dropNoPallet1);


            if(dropNoPallet1!=0){
                    dropNoPallet5 =dropNoPallet5+ dropNoPallet1;

                }
            if(dropNoPallet2!=0){
                    dropNoPallet = dropNoPallet+ dropNoPallet2;

                }
            if(dropNoPallet3!=0){
                    dropNoPallet = dropNoPallet + dropNoPallet3;

                }
            if(dropNoPallet4!=0){
                dropNoPallet5 = dropNoPallet5 + dropNoPallet4;

            }

            System.out.println("noofPallet1---->"+noofPallet+"dropNoPallet5"+dropNoPallet5+"dropNoPallet"+dropNoPallet);
            //System.out.println("DeliveryDtooooSizenew---->"+noofPallet);



            if(noofPallet1!=0){
                if(noofPallet==dropNoPallet5 && noofPallet1==dropNoPallet ){

                    createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                }

            }else{
                if(noofPallet==dropNoPallet5 || noofPallet==dropNoPallet ){

                    createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
                    createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                }


            }

//            System.out.println("DeliveryDtooooSize---->"+dropNoPallet);
//            System.out.println("DeliveryDtooooSizenew---->"+noofPallet);
//
//            if(noofPallet==dropNoPallet ){
//
//
//
//            }
//            else if ( noofPallet1 == dropNoPallet1){
//                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
//                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
//            }


           /* List<String> label =  db.getAllLabels();
            if(label.size()>=2) {
                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
            }*/

        }else if(deliveryType == "miscellaneous") {
            createOrderExpressDeliveryDropBinding.toolbarTitle.setText("Miscellaneous Delivery Order");

        }

        vehicleList = new ArrayList<>();
        createOrderExpressDeliveryDropBinding.btnSubmit.setOnClickListener(this);
        createOrderExpressDeliveryDropBinding.ivBack.setOnClickListener(this);
        createOrderExpressDeliveryDropBinding.etDropoffAddress.setOnClickListener(this);
        createOrderExpressDeliveryDropBinding.llType.setOnClickListener(this);
        createOrderExpressDeliveryDropBinding.ccp.registerPhoneNumberTextView(createOrderExpressDeliveryDropBinding.etMobile);
        createOrderExpressDeliveryDropBinding.ccp.setDefaultCountryUsingNameCode("US");
        createOrderExpressDeliveryDropBinding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tt = s.toString();
                if(createOrderExpressDeliveryDropBinding.ccp.isValid()) {
                    createOrderExpressDeliveryDropBinding.llMobileNumberError.setVisibility(View.GONE);
                } else {
                    createOrderExpressDeliveryDropBinding.llMobileNumberError.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        vechiletypespinner();
        //   customSpinnerAdapter = new CustomSpinnerForAll(context, R.layout.spinner_textview, vehicleList, getResources().getColor(R.color.black_color), getResources().getColor(R.color.light_black), getResources().getColor(R.color.transparent));
        //     createOrderExpressDeliveryDropBinding.spType.setAdapter(customSpinnerAdapter);

        createOrderExpressDeliveryDropBinding.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    vehicleType = vehicleList.get(i).get(PN_VALUE).toLowerCase().trim();
//                    callVechicalCategory(vehicleType.toLowerCase().trim());
                } else {
                    ((TextView) view).setTextSize(16);
                    ((TextView) view).setTextColor(
                            getResources().getColorStateList(R.color.text_hint)
                    );
                    vehicleType = "";
                    createOrderExpressDeliveryDropBinding.llType1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(deliveryType .equals("express") ){
            createOrderExpressDeliveryDropBinding.llLiftGate.setVisibility(View.GONE);
            createOrderExpressDeliveryDropBinding.tvFourtyfivedoller.setVisibility(View.GONE);
            createOrderExpressDeliveryDropBinding.tvSixtyfivedoller.setVisibility(View.GONE);
            createOrderExpressDeliveryDropBinding.tvTwodoller.setVisibility(View.VISIBLE);
            // nandha
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)  createOrderExpressDeliveryDropBinding.llInsidePickup.getLayoutParams();
            params.setMargins(0, 0, 0, 0); //substitute parameters for left, top, right, bottom
            createOrderExpressDeliveryDropBinding.llInsidePickup.setLayoutParams(params);

        }

        createOrderExpressDeliveryDropBinding.tvPickinfo.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        utilities.dialogOK(context, getString(R.string.validation_title), "Extra charge of $2 will be added for Inside drop", getString(R.string.ok), false);
                        break;

                    default:
                        break;
                }
            }

        });

        createOrderExpressDeliveryDropBinding.tvDropinfo.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        utilities.dialogOK(context, getString(R.string.validation_title), "Extra charge of $2 will be added for Lift gate", getString(R.string.ok), false);
                        break;

                    default:
                        break;
                }
            }

        });

        createOrderExpressDeliveryDropBinding.rbInsidePickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!deliveryType.equals("express")) {
                    if (createOrderExpressDeliveryDropBinding.rbInsidePickup.isChecked()) {
                        //   utilities.dialogOK(context, getString(R.string.validation_title), "If seleted the InsideDropup to be added $ 2", getString(R.string.ok), false);
                        dropBuildingType = "yes";
                    } else {
                        dropBuildingType = "no";
                    }
                } else if(deliveryType.equals("express"))
                {
                    if (createOrderExpressDeliveryDropBinding.rbInsidePickup.isChecked()) {
                        //  utilities.dialogOK(context, getString(R.string.validation_title), "If seleted the InsideDropup to be added $ 2", getString(R.string.ok), false);
                        dropBuildingType = "yes";
                    } else {
                        dropBuildingType = "no";
                    }
                }
            }
        });
        createOrderExpressDeliveryDropBinding.rbLiftGate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(createOrderExpressDeliveryDropBinding.rbLiftGate.isChecked())
                {
                    // utilities.dialogOK(context, getString(R.string.validation_title), "If seleted the LiftGate to be added $ 2", getString(R.string.ok), false);
                    dropElevator = "yes";
                }else
                {
                    dropElevator = "no";
                }

            }
        });



//        etPalletsCountnew1   = (EditText) findViewById(R.id.et_pallets_count);
//        etPalletsCountnew2   = (EditText) findViewById(R.id.et_pallets_count2);



        createOrderExpressDeliveryDropBinding.etPalletsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                int value1 = Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString());
//               int value2 = Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount2.getText().toString());


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().trim().length()>0) {
            int noofPallet = Integer.parseInt(deliveryDTO.getNoOfPallets());
            int noofPallet1 = TextUtils.isEmpty(deliveryDTO.getNoOfPallets1()) ? 0 : Integer.parseInt(deliveryDTO.getNoOfPallets1());
            int dropNoPallet1 = db.getpalletscount();
            int dropNoPallet2 = db.getpalletandboxcount();
            int dropNoPallet4 = db.getpalletandboxcount1();
            int dropNoPallet3 = db.getboxcount();
            int dropNoPallet = 0;
            int dropNoPallet5 = 0;
            int noofPallet3 = 0;
            int dropNoPallet6 = 0;
           int current_item_count1 =0;
           int current_item_count2 =0;
           int current_item_count3 =0;







                    System.out.println("palletandbox"+noofPallet1+" pallets is "+noofPallet);
            System.out.println("palletandbox db"+dropNoPallet2+" pallets is db "+dropNoPallet1);


            if(dropNoPallet1!=0){
                    dropNoPallet5 =dropNoPallet5+ dropNoPallet1;

                }
            if(dropNoPallet2!=0){
                    dropNoPallet = dropNoPallet+ dropNoPallet2;

                }
            if(dropNoPallet3!=0){
                    dropNoPallet = dropNoPallet + dropNoPallet3;

                }
            if(dropNoPallet4!=0){
                dropNoPallet5 = dropNoPallet5 + dropNoPallet4;

            }
                    int value1 = TextUtils.isEmpty(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString()) ? 0 : Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString());
                    int value2 = TextUtils.isEmpty(createOrderExpressDeliveryDropBinding.etPalletsCount2.getText().toString()) ? 0 : Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount2.getText().toString());


                    if(dropNoPallet6 == 0){

                dropNoPallet6 = dropNoPallet5 + dropNoPallet + value2;
            }



           // if(noofPallet1!=0){


                    if(noofPallet1!=0){
                        noofPallet3 = noofPallet+noofPallet1;

                    }else{
                        noofPallet3 = noofPallet;

                    }


                    System.out.println("DeliveryDtooooSize1---->"+dropNoPallet);
                    System.out.println("DeliveryDtooooSizenew1---->"+noofPallet);

                    int current_item_count = Integer.parseInt(s.toString().trim());



                    if (current_item_count <= 0) {
                        Toast.makeText(context, "Number of " + productMeasure + " can't be zero ", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    current_item_count1 =(productMeasure.equalsIgnoreCase("Box")) ? Integer.parseInt(s.toString().trim()):0;
                    current_item_count2 =(productMeasure.equalsIgnoreCase("Pallet")) ? Integer.parseInt(s.toString().trim()):0;
                    current_item_count3 =(productMeasure.equalsIgnoreCase("Pallet and Box")) ?Integer.parseInt(s.toString().trim()):0;

//                    if(productMeasure=="Box"){
//
//                        current_item_count1 =  value1;
//                    }
//                    if(productMeasure=="Pallet"){
//
//                        current_item_count2 = Integer.parseInt(s.toString().trim());
//                    }
//                    if(productMeasure=="Pallet and Box"){
//
//                        current_item_count3 = Integer.parseInt(s.toString().trim());
//                    }


                    System.out.println("currentitemcountforbox---->"+current_item_count1 +  "producttype"+productMeasure + "currentitemcountforpallet---->"+current_item_count2 +  "producttype"+productMeasure+ "currentitemcountforpalletandbox---->"+current_item_count3 +  "producttype"+productMeasure);


                    List<String> labelcount = db.getAllLabels();
                    if (labelcount.size() == 0) {
                        if (dropNoPallet6 + current_item_count == noofPallet3) {
                            Toast.makeText(context, "You can't send all " + productMeasure + " in one stop ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }



                    if(noofPallet1==0) {


                        if (dropNoPallet6 + current_item_count > noofPallet3 || dropNoPallet + current_item_count > noofPallet) {
                            Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (dropNoPallet6 + current_item_count > noofPallet3 || dropNoPallet5 + current_item_count > noofPallet){

                            Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                            return;

                        }


                    }else{
                         if (dropNoPallet6 + current_item_count >noofPallet3 ||  dropNoPallet + current_item_count1 > noofPallet1) {
                             System.out.println("currentitemcountforbox---->"+current_item_count1 +  "producttype"+productMeasure);
                            Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                            return;
                        }
                         else if (dropNoPallet6 + current_item_count > noofPallet3 || dropNoPallet5 + current_item_count2 > noofPallet){
                             System.out.println("currentitemcountforpallet---->"+current_item_count2 +  "producttype"+productMeasure);
                             Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                             return;

                         }
                         else if (dropNoPallet6 + current_item_count > noofPallet3 || dropNoPallet5 + current_item_count3 > noofPallet){
                             System.out.println("currentitemcountforpallet---->"+current_item_count3 +  "producttype"+productMeasure);
                             Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                             return;

                         }


                    }

                    if(labelcount.size()==19){

                        System.out.println("noofPallet1----> dropNoPallet"+dropNoPallet+"dropNoPallet6"+dropNoPallet6+"current_item_count"+current_item_count+"dropNoPallet5"+dropNoPallet5);

                         if(noofPallet1==0){


                            if (dropNoPallet + current_item_count == noofPallet){

                                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);

                            }else if(dropNoPallet5 + current_item_count == noofPallet) {

                                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
                            }

                        }else{

                            if (dropNoPallet + current_item_count == noofPallet1 &&  dropNoPallet6 + current_item_count == noofPallet3) {

                                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);

                            }
                            else if(dropNoPallet5 + current_item_count == noofPallet &&  dropNoPallet6 + current_item_count == noofPallet3) {

                                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);


                            }


                        }



                        Toast.makeText(context, "Please drop all your " + productMeasure + " in this last stop " + productMeasure, Toast.LENGTH_SHORT).show();
                        return;

                    }

                    if (labelcount.size() >= 1) {



                     //  System.out.println(" etPalletsCountnew1------"+ etPalletsCountnew1.getText().toString() + "--- etPalletsCountnew2"+ etPalletsCountnew2.getText().toString());

                        //System.out.println("noofPallet1----> dropNoPallet"+dropNoPallet+"dropNoPallet6"+dropNoPallet6+"current_item_count"+current_item_count+"dropNoPallet5"+dropNoPallet5);

//                        Intent myIntent = new Intent(context, SelectLocation.class);
//                        myIntent.putExtra("Box", current_item_count);
//                        startActivity(myIntent);
                        System.out.println("dropNoPallet6new "+dropNoPallet6+"dropNoPallet6neww"+dropNoPallet6+"current_item_countnew"+current_item_count+"dropNoPallet5newww"+dropNoPallet5);

                        if(noofPallet1==0){


                            if (dropNoPallet + current_item_count == noofPallet){

                                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);

                            }else if(dropNoPallet5 + current_item_count == noofPallet) {

                                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
                            }

                        }else{

                            if (dropNoPallet + current_item_count == noofPallet1 &&  dropNoPallet6 + current_item_count == noofPallet3) {
                                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);

                            }
                            else if(dropNoPallet5 + current_item_count == noofPallet &&  dropNoPallet6 + current_item_count == noofPallet3) {

                                createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                                createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);


                            }


                        }

                        if (dropNoPallet6 + current_item_count > noofPallet3) {
                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.VISIBLE);

                        }
                        else if (dropNoPallet6 + current_item_count < noofPallet3) {
                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.VISIBLE);

                        }

                    }



                }

            }
        });




        // here is box etAlletcount2
        createOrderExpressDeliveryDropBinding.etPalletsCount2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().trim().length()>0) {

                    int noofPallet = Integer.parseInt(deliveryDTO.getNoOfPallets());
                    int noofPallet1 = TextUtils.isEmpty(deliveryDTO.getNoOfPallets1()) ? 0 : Integer.parseInt(deliveryDTO.getNoOfPallets1());
                    int dropNoPallet1 = db.getpalletscount();
                    int dropNoPallet2 = db.getpalletandboxcount();
                    int dropNoPallet4 = db.getpalletandboxcount1();
                    int dropNoPallet3 = db.getboxcount();
                    int dropNoPallet = 0;
                    int dropNoPallet5 = 0;
                    int noofPallet3 = 0;
                    int dropNoPallet6 = 0;

                    System.out.println("palletandbox"+noofPallet1+" pallets is "+noofPallet);
                    System.out.println("palletandbox db"+dropNoPallet2+" pallets is db "+dropNoPallet1);


                    if(dropNoPallet1!=0){
                        dropNoPallet5 =dropNoPallet5+ dropNoPallet1;

                    }
                    if(dropNoPallet2!=0){
                        dropNoPallet = dropNoPallet+ dropNoPallet2;

                    }
                    if(dropNoPallet3!=0){
                        dropNoPallet = dropNoPallet + dropNoPallet3;

                    }
                    if(dropNoPallet4!=0){
                        dropNoPallet5 = dropNoPallet5 + dropNoPallet4;

                    }

                    int value1 = TextUtils.isEmpty(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString()) ? 0 : Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString());
                   // int value2 = TextUtils.isEmpty(createOrderExpressDeliveryDropBinding.etPalletsCount2.getText().toString()) ? 0 : Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount2.getText().toString());


                    if(dropNoPallet6 == 0){

                        dropNoPallet6 = dropNoPallet5 + dropNoPallet + value1;
                    }



                    // if(noofPallet1!=0){


                    if(noofPallet1!=0){
                        noofPallet3 = noofPallet+noofPallet1;

                    }

                    System.out.println("DeliveryDtooooSizenew2---->"+noofPallet);
                    System.out.println("DeliveryDtooooSize2---->"+dropNoPallet);

                    int current_item_count = Integer.parseInt(s.toString().trim());



                        int current_item_count2 = TextUtils.isEmpty(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString()) ? 0 : Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString());


                    if (current_item_count <= 0) {
                        Toast.makeText(context, "Number of " + productMeasure + " can't be zero ", Toast.LENGTH_SHORT).show();
                        return;

                    }


                    List<String> labelcount = db.getAllLabels();
                    if (labelcount.size() == 0) {
                        if (dropNoPallet6 + current_item_count == noofPallet3) {
                            Toast.makeText(context, "You can't send all " + productMeasure + " in one stop ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }


                    if(noofPallet1==0) {


                        if (dropNoPallet6 + current_item_count > noofPallet3 || dropNoPallet + current_item_count > noofPallet) {//&& dropNoPallet + current_item_count > noofPallet1|| noofPallet1 < current_item_count
                            Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (dropNoPallet6 + current_item_count > noofPallet3 || dropNoPallet5 + current_item_count > noofPallet){

                            Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                            return;

                        }


                    }else{
                        if (dropNoPallet6 + current_item_count >noofPallet3 ||  dropNoPallet + current_item_count > noofPallet1) {//&& dropNoPallet + current_item_count > noofPallet1|| noofPallet1 < current_item_count
                            Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (dropNoPallet6 + current_item_count > noofPallet3 || dropNoPallet5 + current_item_count2 > noofPallet){

                            Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
                            return;

                        }


                 }



//                    if (dropNoPallet6 + current_item_count > noofPallet3) {
//                        Toast.makeText(context, "Drop Number Of " + productMeasure + " can't be more then pickup number of " + productMeasure, Toast.LENGTH_SHORT).show();
//                        return;
//                    }

                    if(labelcount.size()==19){
                        System.out.println("noofPallet1----> dropNoPallet"+dropNoPallet+"dropNoPallet6"+dropNoPallet6+"current_item_count"+current_item_count+"dropNoPallet5"+dropNoPallet5);


                        if (dropNoPallet + current_item_count == noofPallet1 &&  dropNoPallet6 + current_item_count == noofPallet3) {
                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);

                        }
                        else if(dropNoPallet5 + current_item_count == noofPallet &&  dropNoPallet6 + current_item_count == noofPallet3) {

                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);


                        }

//                        if (dropNoPallet + current_item_count == noofPallet) {
//                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
//
//                        }

                        Toast.makeText(context, "Please drop all your " + productMeasure + " in this last stop " + productMeasure, Toast.LENGTH_SHORT).show();
                        return;

                    }

                    if (labelcount.size() >= 1) {





                       // System.out.println("sksjdkljdslkjs--"+value1+"djkjskljs----"+value2);
//                        Intent mIntent = getIntent();
//                        int intValue = myIntent.getIntExtra("box", 0);
//                        System.out.println("boxselectedfromothereven------------ "+intValue);
                        System.out.println("dropNoPallet6new "+dropNoPallet6+"dropNoPallet6neww"+dropNoPallet6+"current_item_countnew"+current_item_count+"dropNoPallet5newww"+dropNoPallet5);


                        if (dropNoPallet + current_item_count == noofPallet1 &&  dropNoPallet6 + current_item_count == noofPallet3) {
                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);

                        }
                        else if(dropNoPallet5 + current_item_count == noofPallet &&  dropNoPallet6 + current_item_count == noofPallet3) {

                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);


                        }

                        if (dropNoPallet6 + current_item_count > noofPallet3) {
                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.VISIBLE);

                        }
                        else if (dropNoPallet6 + current_item_count < noofPallet3) {
                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.GONE);
                            createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.VISIBLE);

                        }

                    }



                }

            }
        });


//        createOrderExpressDeliveryDropBinding.rgLiftGate.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:


                if (deliveryType.equalsIgnoreCase("multiple") || deliveryType == "multiple1") {
                    MultipleAdd multipleAdd = new MultipleAdd();
                        System.out.println("DeliveryDtooooSize---->"+deliveryDTO);
                    Bundle bundle = new Bundle();

                    if (rescheduleStatus) {
                        callRescheduleOrderBookApi();

                    } else {

                        //Toast.makeText(context,"submitmuladddrop",Toast.LENGTH_LONG).show();
                        int noofPalletn = Integer.parseInt(deliveryDTO.getNoOfPallets());
                        int dropNoPalletn = db.getpalletscount();

                        if(noofPalletn==dropNoPalletn){

                            Toast.makeText(context, "For Adding New drop You need to delete existing one", Toast.LENGTH_SHORT).show();
                            bundle.putParcelable("deliveryDTO", deliveryDTO);
                            multipleAdd.setArguments(bundle);
                            addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");
                            return;

                        }

                        Utilities.hideKeyboard(createOrderExpressDeliveryDropBinding.btnSubmit);
                        countryCode = createOrderExpressDeliveryDropBinding.ccp.getSelectedCountryCode();
                        Log.e(TAG, ">>>>>>>>>>>>>>" + countryCode);

                        firstName = createOrderExpressDeliveryDropBinding.etFirstName.getText().toString();
                        specialinsdrop=createOrderExpressDeliveryDropBinding.etPickSpecialInst.getText().toString();
                        lastName = createOrderExpressDeliveryDropBinding.etLastName.getText().toString();
                        mobile = createOrderExpressDeliveryDropBinding.etMobile.getText().toString();
                        dropOffAddress = createOrderExpressDeliveryDropBinding.etDropoffAddress.getText().toString();


                        TypeGood = createOrderExpressDeliveryDropBinding.spType1.getSelectedItem().toString();

                        System.out.println("spType1" + TypeGood);

                        NoofPallets = createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString();
                        NoofPallets1 = createOrderExpressDeliveryDropBinding.etPalletsCount2.getText().toString();

                        productWidth = createOrderExpressDeliveryDropBinding.etGoodWidth.getText().toString();
                        productHeight = createOrderExpressDeliveryDropBinding.etGoodHight.getText().toString();
                        productLength = createOrderExpressDeliveryDropBinding.etGoodLength.getText().toString();
                        productWeight = createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.getText().toString();
                        productWeight2 = createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.getText().toString();


                        System.out.println("productWeight" + productWeight);


                        if(createOrderExpressDeliveryDropBinding.rbLiftGate.isChecked()){
                            dropElevator="yes";
                        }else{
                            dropElevator="no";
                        }
                        if(createOrderExpressDeliveryDropBinding.rbInsidePickup.isChecked()){
                            dropBuildingType="yes";
                        }else{
                            dropBuildingType="no";
                        }


                        if (isValidForMultipleAdd()) {

                            int noofPallet = Integer.parseInt(deliveryDTO.getNoOfPallets());
                            int noofPallet1 = TextUtils.isEmpty(deliveryDTO.getNoOfPallets1()) ? 0 : Integer.parseInt(deliveryDTO.getNoOfPallets1());
                            int dropNoPallet1 = db.getpalletscount();
                            int dropNoPallet2 = db.getpalletandboxcount();
                            int dropNoPallet4 = db.getpalletandboxcount1();
                            int dropNoPallet3 = db.getboxcount();
                            int dropNoPallet = 0;
                            int dropNoPallet5 = 0;
                            int noofPallet3 = 0;
                            int dropNoPallet6 = 0;

                            System.out.println("palletandbox"+noofPallet1+" pallets is "+noofPallet);
                            System.out.println("palletandbox db"+dropNoPallet2+" pallets is db "+dropNoPallet1);


                            if(dropNoPallet1!=0){
                                dropNoPallet5 =dropNoPallet5+ dropNoPallet1;

                            }
                            if(dropNoPallet2!=0){
                                dropNoPallet = dropNoPallet+ dropNoPallet2;

                            }
                            if(dropNoPallet3!=0){
                                dropNoPallet = dropNoPallet + dropNoPallet3;

                            }
                            if(dropNoPallet4!=0){
                                dropNoPallet5 = dropNoPallet5 + dropNoPallet4;

                            }
                            if(dropNoPallet6 == 0){

                                dropNoPallet6 = dropNoPallet5 + dropNoPallet;
                            }



                            // if(noofPallet1!=0){


                            if(noofPallet1!=0){
                                noofPallet3 = noofPallet+noofPallet1;

                            }else{
                                noofPallet3 = noofPallet;

                            }



                            int current_item_count = Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString().trim());

                            if(current_item_count<=0){
                                Toast.makeText(context, "Number of "+productMeasure +" can't be zero ", Toast.LENGTH_SHORT).show();
                                return;

                            }


                            List<String> labelcount =  db.getAllLabels();
                            if(labelcount.size()+1==1) {

                                if(dropNoPallet6+current_item_count==noofPallet3){
                                    Toast.makeText(context, "You can't send all "+productMeasure +" in one stop ", Toast.LENGTH_SHORT).show();
                                    return;
                                }else if (dropNoPallet6+current_item_count == noofPallet3){
                                    Toast.makeText(context, "You can't send all "+productMeasure +" in one stop ", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            }


                            if(dropNoPallet6+current_item_count>noofPallet3){
                                Toast.makeText(context, "Drop Number Of "+productMeasure +" can't be more then pickup number of "+productMeasure, Toast.LENGTH_SHORT).show();
                                return;
                            }



                            if (deliveryType.equalsIgnoreCase("multiple")) {
                                //MultipleAdd multipleAdd = new MultipleAdd();
                                //Bundle bundle = new Bundle();

                                System.out.println("deliverdto"+deliveryDTO.getDropoffaddress());
                                deliveryDTO.setDropoffFirstName(firstName);
                                deliveryDTO.setDropoffLastName(lastName);
                                deliveryDTO.setDropoffSpecialInst(specialinsdrop);
                                deliveryDTO.setDropoffMobNumber(mobile);
                                deliveryDTO.setDropoffaddress(dropOffAddress);
                                deliveryDTO.setVehicleType(vehicleType);
                                deliveryDTO.setDropoffLat(dropOffLat);
                                deliveryDTO.setDropoffLong(dropOffLong);
                                deliveryDTO.setDropoffCountryCode(countryCode);
                                deliveryDTO.setDropBuildingType(dropBuildingType);
                                deliveryDTO.setDropElevator(dropElevator);
                                deliveryDTO.setType_of_truck(vehicleCategory);


                                Location loc1 = new Location("");
                                loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
                                loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

                                Location loc2 = new Location("");
                                loc2.setLatitude(Double.parseDouble(dropOffLat));
                                loc2.setLongitude(Double.parseDouble(dropOffLong));

                                float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;
                                distance_value = String.valueOf(distanceInmiles);

                                int parcelCount = Integer.parseInt(deliveryDTO.getNoOfPallets());
                                String pallets = deliveryDTO.getTypeGoods();

                                switch (deliveryDTO.getDeliveryType()) {

                                    case "multiple":


                                        if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet")) {
                                            System.out.println("Pallets---> " + deliveryDTO.getTypeGoods());
                                            if (parcelCount == 1) {
                                                totalDeliveryCost = parcelCount * Double.parseDouble("250");
                                            } else if (parcelCount == 2) {
                                                totalDeliveryCost = parcelCount * Double.parseDouble("225");
                                            } else if (parcelCount >= 3) {
                                                totalDeliveryCost = parcelCount * Double.parseDouble("200");
                                            }
                                            if (deliveryDTO.getDropBuildingType().equals("yes")) {
                                                totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                                            }

                                            if (deliveryDTO.getDropElevator().equals("yes")) {
                                                totalDeliveryCost = totalDeliveryCost + Double.parseDouble("65");
                                            }

                                        } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                                            if (parcelCount >= 1 && parcelCount <= 24) {
                                                totalDeliveryCost = parcelCount * Double.parseDouble("8");
                                            } else if (parcelCount >= 25 && parcelCount <= 50) {
                                                totalDeliveryCost = parcelCount * Double.parseDouble("7");
                                            } else if (parcelCount >= 51 && parcelCount <= 100) {
                                                totalDeliveryCost = parcelCount * Double.parseDouble("5");
                                            } else if (parcelCount >= 101 && parcelCount <= 200) {
                                                totalDeliveryCost = parcelCount * Double.parseDouble("4.5");
                                            } else if (parcelCount >= 201) {
                                                totalDeliveryCost = parcelCount * Double.parseDouble("4");
                                            }
                                            if (deliveryDTO.getDropBuildingType().equals("yes")) {
                                                totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                                            }
                                            if (deliveryDTO.getDropElevator().equals("yes")) {
                                                totalDeliveryCost = totalDeliveryCost + Double.parseDouble("65");
                                            }
                                        }

                                        break;

                                }


                                deliveryDTO.setDeliveryCost(String.format("%2f", totalDeliveryCost));
                                System.out.println("totalDeliveryCost---> "+ totalDeliveryCost);


                                db.addContact(new MultipleDTO(
                                        deliveryDTO.getDropBuildingType(),
                                        deliveryDTO.getDropElevator(),
                                        deliveryDTO.getDropoffaddress(),
                                        deliveryDTO.getDropoffSpecialInst(),
                                        deliveryDTO.getDropoffSpecialInst(),
                                        deliveryDTO.getDropoffFirstName(),
                                        deliveryDTO.getDropoffLastName(),
                                        deliveryDTO.getDropoffMobNumber(),
                                        deliveryDTO.getDropoffLat(),
                                        deliveryDTO.getDropoffLong(),
                                        deliveryDTO.getDeliveryCost(),
                                        deliveryDTO.getDropoffCountryCode(),
                                        ClassGood,
                                        TypeGood.toLowerCase().trim(),
                                        NoofPallets,
                                        productWeight,
                                        weightunit,
                                        ptypeofbox,
                                        productWidth,
                                        productHeight,
                                        productLength,
                                        String.valueOf(distanceInmiles),
                                        NoofPallets1
                                ));


                                deliveryDTO.setDeliveryCost(String.format("%2f", totalDeliveryCost));
                                System.out.println("totalDeliveryCost---> "+ totalDeliveryCost);
                                bundle.putParcelable("deliveryDTO", deliveryDTO);
                                multipleAdd.setArguments(bundle);
                                addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");


                                /*List<String> labels =  db.getAllLabels();
                                if(labels.size()>=2) {
                                    createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);

                                }

                                int noofPallets = Integer.parseInt(deliveryDTO.getNoOfPallets());
                                int dropNoPallets = db.getpalletscount();

                                if(noofPallets==dropNoPallets){
                                    createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
                                    createOrderExpressDeliveryDropBinding.uiContainer.setVisibility(View.GONE);


                                }

                                if(labels.size()<20) {

                                    createOrderExpressDeliveryDropBinding.dropNumber.setText("Stop-" + String.valueOf(labels.size() + 1) + " Drop off");

                                    createOrderExpressDeliveryDropBinding.etFirstName.setText("");
                                    createOrderExpressDeliveryDropBinding.etPickSpecialInst.setText("");
                                    createOrderExpressDeliveryDropBinding.etLastName.setText("");
                                    createOrderExpressDeliveryDropBinding.etMobile.setText("");
                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                                    createOrderExpressDeliveryDropBinding.rbLiftGate.setChecked(false);
                                    dropElevator="no";
                                    createOrderExpressDeliveryDropBinding.rbInsidePickup.setChecked(false);
                                    dropBuildingType="no";

                                    //productMeasure="";
                                    productWidth="";
                                    productHeight="";
                                    productLength="";
                                    ptypeofbox="";
                                    //ClassGood="";
                                    createOrderExpressDeliveryDropBinding.etPalletsCount.setText("");
                                    createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setText("");

                                    createOrderExpressDeliveryDropBinding.etGoodWidth.setText("");
                                    createOrderExpressDeliveryDropBinding.etGoodHight.setText("");
                                    createOrderExpressDeliveryDropBinding.etGoodLength.setText("");

                                    createOrderExpressDeliveryDropBinding.imgLogo.requestFocus();
                                    createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.GONE);

                                    if(deliveryType == "multiple"){
                                        setListClassGoods();

                                    }

                                }else{
                                    createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
                                    createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);


                                }*/


                            }

                        }






                        /*int noofPallet = Integer.parseInt(deliveryDTO.getNoOfPallets());
                        int dropNoPallet = db.getpalletscount();

                        if(noofPallet>dropNoPallet){
                            Toast.makeText(context,"You need to select "+dropNoPallet +" / "+noofPallet+" "+productMeasure,Toast.LENGTH_LONG).show();
                            return;
                        }

                        int parcelCount = Integer.parseInt(deliveryDTO.getNoOfPallets());
                        String pallets = deliveryDTO.getTypeGoods();

                        switch (deliveryDTO.getDeliveryType()) {

                            case "multiple":

                                // if(multiple.equalsIgnoreCase("multiple1")) {

                                if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet")) {
                                    System.out.println("Pallets---> " + deliveryDTO.getTypeGoods());
                                    if (parcelCount == 1) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("250");
                                    } else if (parcelCount == 2) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("225");
                                    } else if (parcelCount >= 3) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("200");
                                    }
                                    if (deliveryDTO.getDropBuildingType().equals("yes")) {
                                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                                    }

                                    if (deliveryDTO.getDropElevator().equals("yes")) {
                                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("65");
                                    }

                                } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                                    if (parcelCount >= 1 && parcelCount <= 24) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("8");
                                    } else if (parcelCount >= 25 && parcelCount <= 50) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("7");
                                    } else if (parcelCount >= 51 && parcelCount <= 100) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("5");
                                    } else if (parcelCount >= 101 && parcelCount <= 200) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("4.5");
                                    } else if (parcelCount >= 201) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("4");
                                    }
                                    if (deliveryDTO.getDropBuildingType().equals("yes")) {
                                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                                    }
                                    if (deliveryDTO.getDropElevator().equals("yes")) {
                                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("65");
                                    }
                                }

                                break;

                        }*/



                    }


                   /* deliveryDTO.setDeliveryCost(String.format("%2f", totalDeliveryCost));
                    System.out.println("totalDeliveryCost---> "+ totalDeliveryCost);
                    bundle.putParcelable("deliveryDTO", deliveryDTO);
                    multipleAdd.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");*/


                }else {

                    Utilities.hideKeyboard(createOrderExpressDeliveryDropBinding.btnSubmit);
                    countryCode = createOrderExpressDeliveryDropBinding.ccp.getSelectedCountryCode();
                    Log.e(TAG, ">>>>>>>>>>>>>>" + countryCode);

                    firstName = createOrderExpressDeliveryDropBinding.etFirstName.getText().toString();
                    specialinsdrop=createOrderExpressDeliveryDropBinding.etPickSpecialInst.getText().toString();
                    lastName = createOrderExpressDeliveryDropBinding.etLastName.getText().toString();
                    mobile = createOrderExpressDeliveryDropBinding.etMobile.getText().toString();
                    dropOffAddress = createOrderExpressDeliveryDropBinding.etDropoffAddress.getText().toString();


                    if(createOrderExpressDeliveryDropBinding.rbLiftGate.isChecked()){
                        dropElevator="yes";
                    }else{
                        dropElevator="no";
                    }
                    if(createOrderExpressDeliveryDropBinding.rbInsidePickup.isChecked()){
                        dropBuildingType="yes";
                    }else{
                        dropBuildingType="no";
                    }

                    DeliveryCheckoutExpressDelivery deliveryCheckoutExpressDelivery = new DeliveryCheckoutExpressDelivery();
                    Bundle bundle = new Bundle();
                    System.out.println("higood");
                    deliveryDTO.setDropoffFirstName(firstName);
                    deliveryDTO.setDropoffLastName(lastName);
                    deliveryDTO.setDropoffMobNumber(mobile);
                    deliveryDTO.setDropoffaddress(dropOffAddress);
                    deliveryDTO.setDropoffSpecialInst(specialinsdrop);
                    deliveryDTO.setVehicleType(vehicleType);
                    deliveryDTO.setDropoffLat(dropOffLat);
                    deliveryDTO.setDropoffLong(dropOffLong);
                    deliveryDTO.setDropoffCountryCode(countryCode);
                    deliveryDTO.setDropBuildingType(dropBuildingType);
                    deliveryDTO.setDropElevator(dropElevator);
                    deliveryDTO.setType_of_truck(vehicleCategory);


                    if (rescheduleStatus) {
                        priceCollecrion();
                        callRescheduleOrderBookApi();
                    } else {

                        Location loc1 = new Location("");
                        loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
                        loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

                        Location loc2 = new Location("");
                        loc2.setLatitude(Double.parseDouble(dropOffLat));
                        loc2.setLongitude(Double.parseDouble(dropOffLong));

                        float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;
                        distance_value = String.valueOf(distanceInmiles);
                        int parcelCount = 0;

                        try {
                            deliveryDTO.setDeliveryDistance(String.format("%.2f", Double.parseDouble(distanceInmiles + "")));
                        } catch (Exception e) {
                            e.printStackTrace();
                            deliveryDTO.setDeliveryDistance(String.valueOf(distanceInmiles));

                        }

                        if(!deliveryDTO.getDeliveryType().equalsIgnoreCase("Miscellaneous")) {
                            parcelCount = Integer.parseInt(deliveryDTO.getNoOfPallets());
                        }

                        System.out.println("getIsPallet" + deliveryDTO.getTypeGoods());
                        System.out.println("express" + deliveryDTO.getTypeGoods());
                        System.out.println("express" + deliveryDTO.getDeliveryType());
                        System.out.println("express1" + deliveryDTO.getPickupBuildingType());
                        System.out.println("express2" + deliveryDTO.getDropBuildingType());
                        //call google api for diatance calculation
                       String url = getDirectionsUrl(new LatLng(Double.parseDouble(deliveryDTO.getPickupLat()),
                                Double.parseDouble(deliveryDTO.getPickupLong())),
                                new LatLng(  Double.parseDouble(deliveryDTO.getDropoffLat()),
                                Double.parseDouble(deliveryDTO.getDropoffLong()))
                        );

                        try {
                            DownloadTask downloadTask = new DownloadTask();
                            downloadTask.execute(url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //callOrderBookApi();

                    }
                }


               /*
                Utilities.hideKeyboard(createOrderExpressDeliveryDropBinding.btnSubmit);
                    countryCode = createOrderExpressDeliveryDropBinding.ccp.getSelectedCountryCode();
                    Log.e(TAG, ">>>>>>>>>>>>>>" + countryCode);

                    firstName = createOrderExpressDeliveryDropBinding.etFirstName.getText().toString();
                    specialinsdrop=createOrderExpressDeliveryDropBinding.etPickSpecialInst.getText().toString();
                    lastName = createOrderExpressDeliveryDropBinding.etLastName.getText().toString();
                    mobile = createOrderExpressDeliveryDropBinding.etMobile.getText().toString();
                    dropOffAddress = createOrderExpressDeliveryDropBinding.etDropoffAddress.getText().toString();


                    if(createOrderExpressDeliveryDropBinding.rbLiftGate.isChecked()){
                        dropElevator="yes";
                    }else{
                        dropElevator="no";
                    }
                    if(createOrderExpressDeliveryDropBinding.rbInsidePickup.isChecked()){
                        dropBuildingType="yes";
                    }else{
                        dropBuildingType="no";
                    }

               if (isValid()) {

                    if (deliveryType.equalsIgnoreCase("multiple")) {
                        MultipleAdd multipleAdd = new MultipleAdd();
                        //    System.out.println("DeliveryDtooooSize---->"+deliveryDTO.);
                        Bundle bundle = new Bundle();

                        System.out.println("deliverdto"+deliveryDTO.getDropoffaddress());
                        deliveryDTO.setDropoffFirstName(firstName);
                        deliveryDTO.setDropoffLastName(lastName);
                        deliveryDTO.setDropoffSpecialInst(specialinsdrop);
                        deliveryDTO.setDropoffMobNumber(mobile);
                        deliveryDTO.setDropoffaddress(dropOffAddress);
                        deliveryDTO.setVehicleType(vehicleType);
                        deliveryDTO.setDropoffLat(dropOffLat);
                        deliveryDTO.setDropoffLong(dropOffLong);
                        deliveryDTO.setDropoffCountryCode(countryCode);
                        deliveryDTO.setDropBuildingType(dropBuildingType);
                        deliveryDTO.setDropElevator(dropElevator);
                        deliveryDTO.setType_of_truck(vehicleCategory);

                        if (rescheduleStatus) {
//                            priceCollecrion();
                            callRescheduleOrderBookApi();
                        } else {

                            Location loc1 = new Location("");
                            loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
                            loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

                            Location loc2 = new Location("");
                            loc2.setLatitude(Double.parseDouble(dropOffLat));
                            loc2.setLongitude(Double.parseDouble(dropOffLong));

                            float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;
                            distance_value = String.valueOf(distanceInmiles);

                            int parcelCount = Integer.parseInt(deliveryDTO.getNoOfPallets());
                            String pallets = deliveryDTO.getTypeGoods();

                            switch (deliveryDTO.getDeliveryType()) {

                                case "multiple":

//                                    if(multiple.equalsIgnoreCase("multiple1")) {

                                    if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet")) {
                                        System.out.println("Pallets---> " + deliveryDTO.getTypeGoods());
                                        if (parcelCount == 1) {
                                            totalDeliveryCost = parcelCount * Double.parseDouble("250");
                                        } else if (parcelCount == 2) {
                                            totalDeliveryCost = parcelCount * Double.parseDouble("225");
                                        } else if (parcelCount >= 3) {
                                            totalDeliveryCost = parcelCount * Double.parseDouble("200");
                                        }
                                        if (deliveryDTO.getDropBuildingType().equals("yes")) {
                                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                                        }

                                        if (deliveryDTO.getDropElevator().equals("yes")) {
                                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("65");
                                        }

                                    } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                                        if (parcelCount >= 1 && parcelCount <= 24) {
                                            totalDeliveryCost = parcelCount * Double.parseDouble("8");
                                        } else if (parcelCount >= 25 && parcelCount <= 50) {
                                            totalDeliveryCost = parcelCount * Double.parseDouble("7");
                                        } else if (parcelCount >= 51 && parcelCount <= 100) {
                                            totalDeliveryCost = parcelCount * Double.parseDouble("5");
                                        } else if (parcelCount >= 101 && parcelCount <= 200) {
                                            totalDeliveryCost = parcelCount * Double.parseDouble("4.5");
                                        } else if (parcelCount >= 201) {
                                            totalDeliveryCost = parcelCount * Double.parseDouble("4");
                                        }
                                        if (deliveryDTO.getDropBuildingType().equals("yes")) {
                                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                                        }
                                        if (deliveryDTO.getDropElevator().equals("yes")) {
                                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("65");
                                        }
                                    }

                                    break;

                            }
                        }


                        deliveryDTO.setDeliveryCost(String.format("%2f", totalDeliveryCost));
                        System.out.println("totalDeliveryCost---> "+ totalDeliveryCost);

                        bundle.putParcelable("deliveryDTO", deliveryDTO);
                        multipleAdd.setArguments(bundle);
                        addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");



                    }

                    } else {

                        DeliveryCheckoutExpressDelivery deliveryCheckoutExpressDelivery = new DeliveryCheckoutExpressDelivery();
                        Bundle bundle = new Bundle();
                        System.out.println("higood");
                        deliveryDTO.setDropoffFirstName(firstName);
                        deliveryDTO.setDropoffLastName(lastName);
                        deliveryDTO.setDropoffMobNumber(mobile);
                        deliveryDTO.setDropoffaddress(dropOffAddress);
                        deliveryDTO.setDropoffSpecialInst(specialinsdrop);
                        deliveryDTO.setVehicleType(vehicleType);
                        deliveryDTO.setDropoffLat(dropOffLat);
                        deliveryDTO.setDropoffLong(dropOffLong);
                        deliveryDTO.setDropoffCountryCode(countryCode);
                        deliveryDTO.setDropBuildingType(dropBuildingType);
                        deliveryDTO.setDropElevator(dropElevator);
                        deliveryDTO.setType_of_truck(vehicleCategory);


//                    deliveryDTO.setDropoffLiftGate(dropoffLiftGate);

                        if (rescheduleStatus) {
                            priceCollecrion();
                            callRescheduleOrderBookApi();
                        } else {

                            Location loc1 = new Location("");
                            loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
                            loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

                            Location loc2 = new Location("");
                            loc2.setLatitude(Double.parseDouble(dropOffLat));
                            loc2.setLongitude(Double.parseDouble(dropOffLong));

                            float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;
                            distance_value = String.valueOf(distanceInmiles);
                            int parcelCount = 0;

                            try {
                                deliveryDTO.setDeliveryDistance(String.format("%.2f", Double.parseDouble(distanceInmiles + "")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if(!deliveryDTO.getDeliveryType().equalsIgnoreCase("Miscellaneous")) {
                             parcelCount = Integer.parseInt(deliveryDTO.getNoOfPallets());
                            }

                            System.out.println("getIsPallet" + deliveryDTO.getTypeGoods());
                            System.out.println("express" + deliveryDTO.getTypeGoods());
                            System.out.println("express" + deliveryDTO.getDeliveryType());
                            System.out.println("express1" + deliveryDTO.getPickupBuildingType());
                            System.out.println("express2" + deliveryDTO.getDropBuildingType());
                            callOrderBookApi();

                        }
                    }*/


                break;

            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;

        case R.id.btn_add:

            Utilities.hideKeyboard(createOrderExpressDeliveryDropBinding.btnAdd);
            countryCode = createOrderExpressDeliveryDropBinding.ccp.getSelectedCountryCode();
            Log.e(TAG, ">>>>>>>>>>>>>>" + countryCode);

            firstName = createOrderExpressDeliveryDropBinding.etFirstName.getText().toString();
            specialinsdrop=createOrderExpressDeliveryDropBinding.etPickSpecialInst.getText().toString();
            lastName = createOrderExpressDeliveryDropBinding.etLastName.getText().toString();
            mobile = createOrderExpressDeliveryDropBinding.etMobile.getText().toString();
            dropOffAddress = createOrderExpressDeliveryDropBinding.etDropoffAddress.getText().toString();


            TypeGood = createOrderExpressDeliveryDropBinding.spType1.getSelectedItem().toString();

            System.out.println("spType1" + TypeGood);

            NoofPallets = createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString();

            NoofPallets1 = createOrderExpressDeliveryDropBinding.etPalletsCount2.getText().toString();
            productWidth = createOrderExpressDeliveryDropBinding.etGoodWidth.getText().toString();
            productHeight = createOrderExpressDeliveryDropBinding.etGoodHight.getText().toString();
            productLength = createOrderExpressDeliveryDropBinding.etGoodLength.getText().toString();
            productWeight = createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.getText().toString();
            productWeight2 = createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.getText().toString();

            System.out.println("productWeight" + productWeight);


            if(createOrderExpressDeliveryDropBinding.rbLiftGate.isChecked()){
                dropElevator="yes";
            }else{
                dropElevator="no";
            }
            if(createOrderExpressDeliveryDropBinding.rbInsidePickup.isChecked()){
                dropBuildingType="yes";
            }else{
                dropBuildingType="no";
            }


            if (isValidForMultipleAdd()) {

                int noofPallet = Integer.parseInt(deliveryDTO.getNoOfPallets());
                int noofPallet1 = TextUtils.isEmpty(deliveryDTO.getNoOfPallets1()) ? 0 : Integer.parseInt(deliveryDTO.getNoOfPallets1());

               // int noofPallet1 = Integer.parseInt(deliveryDTO.getNoOfPallets1());
                int dropNoPallet1 = db.getpalletscount();
                int dropNoPallet2 = db.getpalletandboxcount();
                int dropNoPallet4 = db.getpalletandboxcount1();
                int dropNoPallet3 = db.getboxcount();
                int dropNoPallet = 0;
                int dropNoPallet5 = 0;
                int noofPallet3 = 0;
                int dropNoPallet6 = 0;

                System.out.println("palletandbox"+noofPallet1+" pallets is "+noofPallet);
                System.out.println("palletandbox db"+dropNoPallet2+" pallets is db "+dropNoPallet1);


                if(dropNoPallet1!=0){
                    dropNoPallet5 =dropNoPallet5+ dropNoPallet1;

                }
                if(dropNoPallet2!=0){
                    dropNoPallet = dropNoPallet+ dropNoPallet2;

                }
                if(dropNoPallet3!=0){
                    dropNoPallet = dropNoPallet + dropNoPallet3;

                }
                if(dropNoPallet4!=0){
                    dropNoPallet5 = dropNoPallet5 + dropNoPallet4;

                }
                if(dropNoPallet6 == 0){

                    dropNoPallet6 = dropNoPallet5 + dropNoPallet;
                }



                // if(noofPallet1!=0){


                if(noofPallet1!=0){
                    noofPallet3 = noofPallet+noofPallet1;

                }else{
                    noofPallet3 = noofPallet;

                }


                int current_item_count = Integer.parseInt(createOrderExpressDeliveryDropBinding.etPalletsCount.getText().toString().trim());

                if(current_item_count<=0){
                    Toast.makeText(context, "Number of "+productMeasure +" can't be zero ", Toast.LENGTH_SHORT).show();
                    return;

                }

                //Toast.makeText(context,"totalpallets  "+noofPallet+" :: droppalletcount"+dropNoPallet+" :: current"+current_item_count,Toast.LENGTH_LONG).show();

                List<String> labelcount =  db.getAllLabels();
                if(labelcount.size()+1==1) {

                    if(dropNoPallet6+current_item_count==noofPallet3){
                        Toast.makeText(context, "You can't send all "+productMeasure +" in one stop ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }


                if(dropNoPallet6+current_item_count>noofPallet3){
                    Toast.makeText(context, "Drop Number Of "+productMeasure +" can't be more then pickup number of "+productMeasure, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(labelcount.size()==19){
                    Toast.makeText(context, "Please drop all your " + productMeasure + " in this last stop " + productMeasure, Toast.LENGTH_SHORT).show();
                    return;

                }



                if (deliveryType.equalsIgnoreCase("multiple")) {
                    //MultipleAdd multipleAdd = new MultipleAdd();
                    //Bundle bundle = new Bundle();

                    System.out.println("deliverdto"+deliveryDTO.getDropoffaddress());
                    deliveryDTO.setDropoffFirstName(firstName);
                    deliveryDTO.setDropoffLastName(lastName);
                    deliveryDTO.setDropoffSpecialInst(specialinsdrop);
                    deliveryDTO.setDropoffMobNumber(mobile);
                    deliveryDTO.setDropoffaddress(dropOffAddress);
                    deliveryDTO.setVehicleType(vehicleType);
                    deliveryDTO.setDropoffLat(dropOffLat);
                    deliveryDTO.setDropoffLong(dropOffLong);
                    deliveryDTO.setDropoffCountryCode(countryCode);
                    deliveryDTO.setDropBuildingType(dropBuildingType);
                    deliveryDTO.setDropElevator(dropElevator);
                    deliveryDTO.setType_of_truck(vehicleCategory);


                        Location loc1 = new Location("");
                        loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
                        loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

                        Location loc2 = new Location("");
                        loc2.setLatitude(Double.parseDouble(dropOffLat));
                        loc2.setLongitude(Double.parseDouble(dropOffLong));

                        float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;
                        distance_value = String.valueOf(distanceInmiles);

                        int parcelCount = Integer.parseInt(deliveryDTO.getNoOfPallets());
                        String pallets = deliveryDTO.getTypeGoods();

                        switch (deliveryDTO.getDeliveryType()) {

                            case "multiple":


                                if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet")) {
                                    System.out.println("Pallets---> " + deliveryDTO.getTypeGoods());
                                    if (parcelCount == 1) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("250");
                                    } else if (parcelCount == 2) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("225");
                                    } else if (parcelCount >= 3) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("200");
                                    }
                                    if (deliveryDTO.getDropBuildingType().equals("yes")) {
                                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                                    }

                                    if (deliveryDTO.getDropElevator().equals("yes")) {
                                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("65");
                                    }

                                } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                                    if (parcelCount >= 1 && parcelCount <= 24) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("8");
                                    } else if (parcelCount >= 25 && parcelCount <= 50) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("7");
                                    } else if (parcelCount >= 51 && parcelCount <= 100) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("5");
                                    } else if (parcelCount >= 101 && parcelCount <= 200) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("4.5");
                                    } else if (parcelCount >= 201) {
                                        totalDeliveryCost = parcelCount * Double.parseDouble("4");
                                    }
                                    if (deliveryDTO.getDropBuildingType().equals("yes")) {
                                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                                    }
                                    if (deliveryDTO.getDropElevator().equals("yes")) {
                                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("65");
                                    }
                                }

                                break;

                        }


                    deliveryDTO.setDeliveryCost(String.format("%2f", totalDeliveryCost));
                    System.out.println("totalDeliveryCost---> "+ totalDeliveryCost);

                    /*bundle.putParcelable("deliveryDTO", deliveryDTO);
                    multipleAdd.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");*/


                    db.addContact(new MultipleDTO(
                            deliveryDTO.getDropBuildingType(),
                            deliveryDTO.getDropElevator(),
                            deliveryDTO.getDropoffaddress(),
                            deliveryDTO.getDropoffSpecialInst(),
                            deliveryDTO.getDropoffSpecialInst(),
                            deliveryDTO.getDropoffFirstName(),
                            deliveryDTO.getDropoffLastName(),
                            deliveryDTO.getDropoffMobNumber(),
                            deliveryDTO.getDropoffLat(),
                            deliveryDTO.getDropoffLong(),
                            deliveryDTO.getDeliveryCost(),
                            deliveryDTO.getDropoffCountryCode(),
                            ClassGood,
                            TypeGood.toLowerCase().trim(),
                            NoofPallets,
                            productWeight,
                            weightunit,
                            ptypeofbox,
                            productWidth,
                            productHeight,
                            productLength,
                            String.valueOf(distanceInmiles),
                            NoofPallets1
                    ));



                        List<String> labels =  db.getAllLabels();
                    // comment due to new flow new code
                        /*if(labels.size()>=2) {
                            createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);

                        }*/

                    int noofPallets = Integer.parseInt(deliveryDTO.getNoOfPallets());
                    int dropNoPallets = db.getpalletscount();

                    if(noofPallets==dropNoPallets){
                        // comment due to new flow new code
                        //createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
                        //createOrderExpressDeliveryDropBinding.uiContainer.setVisibility(View.GONE);


                        /*createOrderExpressDeliveryDropBinding.uiContainer.setEnabled(false);
                        createOrderExpressDeliveryDropBinding.etFirstName.setVisibility(View.GONE);

                        createOrderExpressDeliveryDropBinding.etMobile.setVisibility(View.GONE);
                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setVisibility(View.GONE);
                        createOrderExpressDeliveryDropBinding.uiContainer.setEnabled(false);
                        createOrderExpressDeliveryDropBinding.uiContainer.setEnabled(false);*/

                    }

                        if(labels.size()<20) {

                            createOrderExpressDeliveryDropBinding.dropNumber.setText("Stop-" + String.valueOf(labels.size() + 1) + " Drop off");

                            createOrderExpressDeliveryDropBinding.etFirstName.setText("");
                            createOrderExpressDeliveryDropBinding.etPickSpecialInst.setText("");
                            createOrderExpressDeliveryDropBinding.etLastName.setText("");
                            createOrderExpressDeliveryDropBinding.etMobile.setText("");
                            createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                            createOrderExpressDeliveryDropBinding.rbLiftGate.setChecked(false);
                            dropElevator="no";
                            createOrderExpressDeliveryDropBinding.rbInsidePickup.setChecked(false);
                            dropBuildingType="no";

                            //productMeasure="";
                            productWidth="";
                            productHeight="";
                            productLength="";
                            ptypeofbox="";
                            //ClassGood="";
                            createOrderExpressDeliveryDropBinding.etPalletsCount.setText("");
                            createOrderExpressDeliveryDropBinding.etPalletsCount2.setText("");
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.setText("");
                            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.setText("");

                            createOrderExpressDeliveryDropBinding.etGoodWidth.setText("");
                            createOrderExpressDeliveryDropBinding.etGoodHight.setText("");
                            createOrderExpressDeliveryDropBinding.etGoodLength.setText("");

                            createOrderExpressDeliveryDropBinding.imgLogo.requestFocus();
                            createOrderExpressDeliveryDropBinding.spTypeOfBox.setVisibility(View.GONE);

                            if(deliveryType == "multiple"){
                                setListClassGoods();

                            }

                        }else{
                            //createOrderExpressDeliveryDropBinding.btnAdd.setVisibility(View.GONE);
                            //createOrderExpressDeliveryDropBinding.btnSubmit.setVisibility(View.VISIBLE);


                        }


                }

            }



            break;
            case R.id.tv_pickup_location:
//                Intent intent = null;
//                try {
//                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                            .build(getActivity());
//                    startActivityForResult(intent, REQUEST_PICK_PLACE);
//                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
                break;
            case R.id.et_dropoff_address:

                Intent addressIntent1 = new Intent(context, SelectLocation.class);
                addressIntent1.putExtra("for", "dropoff location");
                startActivityForResult(addressIntent1, 1910);

               /* try {
//                    Intent intent =
//                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                                    .build(getActivity());
//                    startActivityForResult(intent, REQUEST_PICK_PLACE);
                    startActivityForResult(builder.build(getActivity()), REQUEST_PICK_PLACE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }*/

//                alertDialog.show();
                break;
            case R.id.btn_select_location:
                dropOffLat = alertdropoffLat;
                dropOffLong = alertdropoffLong;
                LatLng latLng = new LatLng(Double.parseDouble(dropOffLat),Double.parseDouble(dropOffLong));
                distancecal(latLng);
//                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(tv_pickup_location.getText().toString());
                alertDialog.dismiss();
                break;
            case R.id.alert_back:
                alertDialog.dismiss();
                break;
//            case R.id.et_dropoff_address:
//                try {
//                    startActivityForResult(builder.build(getActivity()), REQUEST_PICK_PLACE);
//                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//                break;
            case R.id.ll_type:
                createOrderExpressDeliveryDropBinding.spType.performClick();
                break;
            case R.id.ll_type1:
                createOrderExpressDeliveryDropBinding.spType1.performClick();
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1910 && resultCode == RESULT_OK) {

           // pickupLat = data.getStringExtra("LATITUDE_SEARCH");
           // pickupLong = data.getStringExtra("LONGITUDE_SEARCH");
            String address = data.getStringExtra("SearchAddress");


            alertdropoffLat = data.getStringExtra("LATITUDE_SEARCH");
            alertdropoffLong = data.getStringExtra("LONGITUDE_SEARCH");

            dropOffLat = data.getStringExtra("LATITUDE_SEARCH");
            dropOffLong = data.getStringExtra("LONGITUDE_SEARCH");
            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(new LatLng(Double.parseDouble(dropOffLat), Double.parseDouble(dropOffLong)))
                            .zoom(15)
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null);
            distancecal(new LatLng(Double.parseDouble(dropOffLat), Double.parseDouble(dropOffLong)),address);



        }

        if (requestCode == REQUEST_PICK_PLACE && resultCode == RESULT_OK) {
            Place place = getPlace(context, data);

//            Place place = PlaceAutocomplete.getPlace(context, data);
            Log.i(getClass().getName(), "Class is >>>>>" + place.getName() + " " + place.getAddress() + "   " + place.getLatLng());
            alertdropoffLat = place.getLatLng().latitude + "";
            alertdropoffLong = place.getLatLng().longitude + "";

            dropOffLat = place.getLatLng().latitude + "";
            dropOffLong =  place.getLatLng().longitude + "";
            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))
                            .zoom(15)
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null);
            //distancecal(place.getLatLng());
//            tv_pickup_location.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));

//            final Place place = PlacePicker.getPlace(context, data);

//            Log.i(getClass().getName(), "Class is >>>>>" + place.getName() + " " + place.getAddress() + "   " + place.getLatLng());
//            dropOffLat = place.getLatLng().latitude + "";
//            dropOffLong = place.getLatLng().longitude + "";

//            distancecal(place.getLatLng());

        }
    }

    public void distancecal(LatLng latLng){
        Location loc1 = new Location("");
        loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
        loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

        Location loc2 = new Location("");
        loc2.setLatitude(latLng.latitude);
        loc2.setLongitude(latLng.longitude);

        float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;

        System.out.println("distance mail::::-->"+distanceInmiles);

        switch (deliveryDTO.getDeliveryType()) {

            case "express":
//                if(distanceInmiles <= (Float.parseFloat(priceDistanceDTO.getVehicle().getMax_mile()))) {
                if(distanceInmiles <= 30){//greater
                    /*    if (distanceInmiles >= 1)*/
                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                    //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);
                    /*else {
                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                        utilities.dialogOK(context, "", context.getResources().getString(R.string.distance_error_express), context.getResources().getString(R.string.ok), false);
                    }*/

                }else{
                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.distance_error_express1), context.getResources().getString(R.string.ok), false);
                }

/*  Bala

                    else {

                        // it can be remove in drop addresses

//                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
//                        utilities.dialogOK(context, "", context.getResources().getString(R.string.distance_error_express), context.getResources().getString(R.string.ok), false);

                        new AlertDialog.Builder(context)
                                .setMessage(getString(R.string.above_30_mile))
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
                                        deliveryDTO.setDeliveryType("single");
                                        vechiletypespinner();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
*/
                break;
            case "single":
                if(distanceInmiles <= 70){
                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                    //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                }else if(distanceInmiles > 70){

                    if(deliveryDTO.getTypeGoods().equalsIgnoreCase("pallet")){

                        utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single), getString(R.string.ok), new OnDialogConfirmListener() {
                            @Override
                            public void onYes() {
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                                //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                            }
                            @Override
                            public void onNo() {
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                            }
                        });

                    }else if(deliveryDTO.getTypeGoods().equalsIgnoreCase("box")){

                        utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single_box), getString(R.string.ok), new OnDialogConfirmListener() {
                            @Override
                            public void onYes() {
                                 createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                                //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                            }
                            @Override
                            public void onNo() {
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                            }
                        });

                    }
                }
                break;
            case "multiple":
//                    if(distanceInmiles <= 70){
                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

//                    }else if(distanceInmiles > 70){
//
//                        if(deliveryDTO.getTypeGoods().equalsIgnoreCase("pallet")){
//
//                            utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single), getString(R.string.ok), new OnDialogConfirmListener() {
//                                @Override
//                                public void onYes() {
//                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
//                                }
//                                @Override
//                                public void onNo() {
//                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
//                                }
//                            });
//
//                        }else if(deliveryDTO.getTypeGoods().equalsIgnoreCase("box")){
//
//                            utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single_box), getString(R.string.ok), new OnDialogConfirmListener() {
//                                @Override
//                                public void onYes() {
//                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
//                                }
//                                @Override
//                                public void onNo() {
//                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
//                                }
//                            });
//
//                        }
//                    }

                break;
            case "miscellaneous":
                if(distanceInmiles <= 70){
                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                    //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                }else if(distanceInmiles > 70) {

                    if (deliveryDTO.getTypeGoods().equalsIgnoreCase("miscellaneous")) {

                        utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single), getString(R.string.ok), new OnDialogConfirmListener() {
                            @Override
                            public void onYes() {
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                                //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                            }

                            @Override
                            public void onNo() {
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                            }
                        });

                    }
                }
        }
        System.out.println("distanceInmiles-->"+ distanceInmiles);

    }

    public void distancecal(LatLng latLng,String address){
        Location loc1 = new Location("");
        loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
        loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

        Location loc2 = new Location("");
        loc2.setLatitude(latLng.latitude);
        loc2.setLongitude(latLng.longitude);

        float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;

        System.out.println("distance mail::::-->"+distanceInmiles);

        switch (deliveryDTO.getDeliveryType()) {

            case "express":
//                if(distanceInmiles <= (Float.parseFloat(priceDistanceDTO.getVehicle().getMax_mile()))) {
            if(distanceInmiles <= 30){//greater
                /*    if (distanceInmiles >= 1)*/
                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);
                    /*else {
                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                        utilities.dialogOK(context, "", context.getResources().getString(R.string.distance_error_express), context.getResources().getString(R.string.ok), false);
                    }*/

                }else{
                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.distance_error_express1), context.getResources().getString(R.string.ok), false);
                }

/*  Bala

                    else {

                        // it can be remove in drop addresses

//                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
//                        utilities.dialogOK(context, "", context.getResources().getString(R.string.distance_error_express), context.getResources().getString(R.string.ok), false);

                        new AlertDialog.Builder(context)
                                .setMessage(getString(R.string.above_30_mile))
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
                                        deliveryDTO.setDeliveryType("single");
                                        vechiletypespinner();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
*/
                break;
            case "single":
                if(distanceInmiles <= 70){
                    //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                }else if(distanceInmiles > 70){

                    if(deliveryDTO.getTypeGoods().equalsIgnoreCase("pallet")){

                        utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single), getString(R.string.ok), new OnDialogConfirmListener() {
                            @Override
                            public void onYes() {
                                //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                            }
                            @Override
                            public void onNo() {
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                            }
                        });

                    }else if(deliveryDTO.getTypeGoods().equalsIgnoreCase("box")){

                        utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single_box), getString(R.string.ok), new OnDialogConfirmListener() {
                            @Override
                            public void onYes() {
                               // createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                            }
                            @Override
                            public void onNo() {
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                            }
                        });

                    }
                }
                break;
            case "multiple":
//                    if(distanceInmiles <= 70){
                //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

//                    }else if(distanceInmiles > 70){
//
//                        if(deliveryDTO.getTypeGoods().equalsIgnoreCase("pallet")){
//
//                            utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single), getString(R.string.ok), new OnDialogConfirmListener() {
//                                @Override
//                                public void onYes() {
//                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
//                                }
//                                @Override
//                                public void onNo() {
//                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
//                                }
//                            });
//
//                        }else if(deliveryDTO.getTypeGoods().equalsIgnoreCase("box")){
//
//                            utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single_box), getString(R.string.ok), new OnDialogConfirmListener() {
//                                @Override
//                                public void onYes() {
//                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
//                                }
//                                @Override
//                                public void onNo() {
//                                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
//                                }
//                            });
//
//                        }
//                    }

                break;
            case "miscellaneous":
                if(distanceInmiles <= 70){
                    //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                    createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                }else if(distanceInmiles > 70) {

                    if (deliveryDTO.getTypeGoods().equalsIgnoreCase("miscellaneous")) {

                        utilities.dialogOKre(context, "Note", context.getResources().getString(R.string.distance_error_single), getString(R.string.ok), new OnDialogConfirmListener() {
                            @Override
                            public void onYes() {
                                //createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText(address);

                            }

                            @Override
                            public void onNo() {
                                createOrderExpressDeliveryDropBinding.etDropoffAddress.setText("");
                            }
                        });

                    }
                }
        }
        System.out.println("distanceInmiles-->"+ distanceInmiles);

    }

    public String getAddressFromLatLong(double latitude, double longitude, boolean status) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            return address + " " + city;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public boolean isValidForMultipleAdd() {
        if (firstName == null || firstName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_name_or_company), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etFirstName.requestFocus();
            return false;
        } else if (mobile.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_mobile_number), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etMobile.requestFocus();
            return false;
        } else if(!createOrderExpressDeliveryDropBinding.ccp.isValid()) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_vaild_number), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etMobile.requestFocus();
            return false;
        } else if (!utilities.checkMobile(mobile)) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_mobile_number), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etMobile.requestFocus();
            return false;
        } else if (dropOffAddress == null || dropOffAddress.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_drop_address), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etDropoffAddress.requestFocus();
            return false;
        }else if ((ClassGood == null || ClassGood.equals("")) && !deliveryType.equals("express")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_class_good), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etGoodClass.requestFocus();
            return false;
        }else if (productMeasure.equals("")) {

            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_type_parcel), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.spType1.requestFocus();
            return false;
        }else if(ptypeofbox.equalsIgnoreCase("") && productMeasure.equalsIgnoreCase("Box")){
            utilities.dialogOK(context, getString(R.string.validation_title), "Please, select the box type.", getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.spTypeOfBox.requestFocus();
            return false;
        }else if(ptypeofbox.equalsIgnoreCase("") && productMeasure.equalsIgnoreCase("PalletAndBox")){
            utilities.dialogOK(context, getString(R.string.validation_title), "Please, select the box type.", getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.spTypeOfBox.requestFocus();
            return false;
        }
//        else if (productMeasure.equalsIgnoreCase("PalletAndBox")){
//            utilities.dialogOK(context, getString(R.string.validation_title), "Enter the number of boxes.", getString(R.string.ok), false);
//            createOrderExpressDeliveryDropBinding.etPalletsCount2.requestFocus();
//        }
        else if (NoofPallets.equals("") && !productMeasure.equalsIgnoreCase("Miscellaneous")) {

            if (productMeasure.equals("Pallet")) {
                utilities.dialogOK(context, getString(R.string.validation_title), "Enter the number of pallets.", getString(R.string.ok), false);
                createOrderExpressDeliveryDropBinding.etPalletsCount.requestFocus();
                return false;
            }else if (productMeasure.equalsIgnoreCase("Box")){
                utilities.dialogOK(context, getString(R.string.validation_title), "Enter the number of boxes.", getString(R.string.ok), false);
                createOrderExpressDeliveryDropBinding.etPalletsCount.requestFocus();
                return false;
            }else if (productMeasure.equalsIgnoreCase("Envelop")){
                utilities.dialogOK(context, getString(R.string.validation_title), "Enter the number of envelop.", getString(R.string.ok), false);
                createOrderExpressDeliveryDropBinding.etPalletsCount.requestFocus();
                return false;
            }

        }else if(!deliveryType.equalsIgnoreCase("Miscellaneous") && (Integer.parseInt(NoofPallets) > 26) && productMeasure.equals("Pallet") ){

            utilities.dialogOK(context, getString(R.string.validation_title), "Maxmium pallets 26.", getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPalletsCount.requestFocus();
            return false;


        }else if(!deliveryType.equalsIgnoreCase("Miscellaneous") && (Integer.parseInt(NoofPallets) == 0) && productMeasure.equals("Box") ){
            utilities.dialogOK(context, getString(R.string.validation_title), "Minimum boxes 24.", getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPalletsCount.requestFocus();
            return false;
        }else if ((productWidth == null || productWidth.equals("")) && productMeasure.equalsIgnoreCase("miscellaneous")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_width), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etGoodWidth.requestFocus();
            return false;
        } else if ((productHeight == null || productHeight.equals("")) && productMeasure.equalsIgnoreCase("miscellaneous")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_height), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etGoodHight.requestFocus();
            return false;
        } else if ((productLength == null || productLength.equals("")) && productMeasure.equalsIgnoreCase("miscellaneous")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_length), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etGoodLength.requestFocus();
            return false;
        } else if (productWeight == null || productWeight.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.requestFocus();
            return false;
        } else if ((Integer.parseInt(productWeight) > 30) && productKg.equals("Pound") && deliveryType.equalsIgnoreCase("express")) {

            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight_25), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.requestFocus();
            return false;

        } else if ((Integer.parseInt(productWeight) > 13.6078) && (productKg.equals("Kilogram")) && deliveryType.equalsIgnoreCase("express")){
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight_11), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight.requestFocus();
            return false;
        }
        else if (productWeight == null || productWeight.equals("")) {
            // started for pounds2
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.requestFocus();
            return false;
        } else if ((Integer.parseInt(productWeight) > 30) && productKg.equals("Pound") && deliveryType.equalsIgnoreCase("express")) {
            // started for pounds2
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight_25), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.requestFocus();
            return false;

        } else if ((Integer.parseInt(productWeight) > 13.6078) && (productKg.equals("Kilogram")) && deliveryType.equalsIgnoreCase("express")){
            // started for pounds2
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight_11), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPalletsTotalWeight2.requestFocus();
            return false;
        }



        else if (specialinsdrop == null || specialinsdrop.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_the_special), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPickSpecialInst.requestFocus();
            return false;
        }

        return true;
    }

    public boolean isValid() {
        if (firstName == null || firstName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_name_or_company), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etFirstName.requestFocus();
            return false;
        } /*else if (lastName == null || lastName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_last_name), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etLastName.requestFocus();
            return false;
        } */else if (mobile.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_mobile_number), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etMobile.requestFocus();
            return false;
        } else if(!createOrderExpressDeliveryDropBinding.ccp.isValid()) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_vaild_number), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etMobile.requestFocus();
            return false;
        } else if (!utilities.checkMobile(mobile)) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_mobile_number), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etMobile.requestFocus();
            return false;
        } else if (dropOffAddress == null || dropOffAddress.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_drop_address), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etDropoffAddress.requestFocus();
            return false;
        }else if (specialinsdrop == null || specialinsdrop.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_the_special), getString(R.string.ok), false);
            createOrderExpressDeliveryDropBinding.etPickSpecialInst.requestFocus();
            return false;
        }

//        else if (vehicleType == null || vehicleType.equals("")) {
//            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_vehicle), getString(R.string.ok), false);
//            createOrderExpressDeliveryDropBinding.spType.requestFocus();
//            return false;
//        } else if (vehicleCategory == null || vehicleCategory.equals("")) {
//            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_vehicle), getString(R.string.ok), false);
//            createOrderExpressDeliveryDropBinding.spType1.requestFocus();
//            return false;
//        }

        return true;
    }

    public  void callRescheduleOrderBookApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {

            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            Map<String, String> map = new HashMap<>();
            map.put("nameOfGoods",deliveryDTO.getNameOfGoods());
            map.put("weight_unit", deliveryDTO.getWeight_unit());
            System.out.println("werrrrrrrr--->"+deliveryDTO.getWeight_unit());
            map.put("user_id", appSession.getUser().getData().getUserId());
            map.put("order_id", deliveryDTO.getOrderId());
            map.put("typeGoodsCategory",deliveryDTO.getTypeGoodsCategory());
            map.put("pickup_comapny_name", deliveryDTO.getPickupComapnyName());
            map.put("pickup_first_name", deliveryDTO.getPickupFirstName());
            map.put("pickup_last_name", deliveryDTO.getPickupLastName());
            map.put("pickup_mob_number", deliveryDTO.getPickupMobNumber());
            map.put("pickupaddress", deliveryDTO.getPickupaddress());
            map.put("item_description", deliveryDTO.getItemDescription());
            map.put("item_quantity", deliveryDTO.getItemQuantity());
            map.put("delivery_date", deliveryDTO.getPickupDate());
            map.put("pickup_special_inst", deliveryDTO.getPickupSpecialInst());
            map.put("dropoff_first_name", deliveryDTO.getDropoffFirstName());
            map.put("dropoff_last_name", deliveryDTO.getDropoffLastName());
            map.put("dropoff_mob_number", deliveryDTO.getDropoffMobNumber());
            map.put("dropoff_special_inst", deliveryDTO.getDropoffSpecialInst());
            map.put("dropoffaddress", deliveryDTO.getDropoffaddress());
            map.put("parcel_height", deliveryDTO.getProductHeight());
            map.put("parcel_width", deliveryDTO.getProductWidth());
            map.put("parcel_lenght", deliveryDTO.getProductLength());
            map.put("parcel_weight", deliveryDTO.getProductWeight());
            map.put("delivery_type", deliveryDTO.getDeliveryType());
            map.put("driver_delivery_cost", deliveryDTO.getDriverDeliveryCost());
            map.put("delivery_distance", deliveryDTO.getDeliveryDistance());
//            map.put("delivery_cost", deliveryDTO.getDeliveryCost());
            map.put("dropoff_comapny_name", deliveryDTO.getDropoffComapnyName());
            map.put("vehicle_type", deliveryDTO.getVehicleType());
            map.put("pickUpLat", deliveryDTO.getPickupLat());
            map.put("pickUpLong", deliveryDTO.getPickupLong());
            map.put("dropOffLong", deliveryDTO.getDropoffLong());
            map.put("dropOffLat", deliveryDTO.getDropoffLat());
            map.put("delivery_time", deliveryDTO.getDeliveryTime());
            map.put("pickup_building_type",deliveryDTO.getPickupBuildingType());
            map.put("pickup_elevator",deliveryDTO.getPickupLiftGate());
            map.put("drop_elevator",deliveryDTO.getDropElevator());
            map.put("drop_building_type",deliveryDTO.getDropBuildingType());
            System.out.println("eeeeeeeeeeeeee-->"+deliveryDTO.getDropElevator());
            System.out.println("ffffffffff-->"+deliveryDTO.getDropBuildingType());
            map.put("type_of_truck",deliveryDTO.getType_of_truck());
            map.put("classGoods",deliveryDTO.getClassGoods());
            map.put("typeGoods",deliveryDTO.getTypeGoods());
            map.put("noOfPallets",deliveryDTO.getNoOfPallets());

            map.put("is_pallet", deliveryDTO.getIs_pallet());
            map.put(PN_APP_TOKEN, APP_TOKEN);
            map.put("dropoff_country_code", deliveryDTO.getDropoffCountryCode());
            map.put("pickup_country_code",  deliveryDTO.getPickupCountryCode());


            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.callRescheduleOrderApi(map);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
                                    @Override
                                    public void onYes() {
                                        ((DrawerContentSlideActivity) context).popAllFragment();
                                    }

                                    @Override
                                    public void onNo() {
                                    }
                                });
                            } else {
                                utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtherDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e(TAG, t.toString());
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);

                }
            });
        }
    }

    public void callVechicalCategory(String type) {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);



            APIInterface apiInterface = APIClient.getClient();
            Call<VechicalCatagoryDTO> call = apiInterface.callgetVechileTypeCategory(APP_TOKEN,type);

            call.enqueue(new Callback<VechicalCatagoryDTO>() {
                @Override
                public void onResponse(Call<VechicalCatagoryDTO> call, Response<VechicalCatagoryDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {

                                final ArrayList<String> listAll1 = new ArrayList<String>();
                                HashMap<String, String> hashMap1 = new HashMap<>();
                                hashMap1 = new HashMap<>();
                                hashMap1.put(PN_NAME, "");
                                hashMap1.put(PN_VALUE, "Select the Vehicle Category");
                                vehicleCategoryList.add(hashMap1);
                                listAll1.add("Select the Vehicle Category");

                                for(int i = 0;response.body().getData().size()>i;i++) {
                                    hashMap1 = new HashMap<>();
                                    hashMap1.put(PN_NAME, "");
                                    hashMap1.put(PN_VALUE, response.body().getData().get(i).getVechile_category());
                                    vehicleCategoryList.add(hashMap1);
                                    listAll1.add(response.body().getData().get(i).getVechile_category());
                                }

                                ArrayAdapter adapter1 = new ArrayAdapter<String>(context, R.layout.spinner_text);
                                adapter1.setDropDownViewResource(R.layout.spinner_item_list);
                                adapter1.addAll(listAll1);
                                createOrderExpressDeliveryDropBinding.spType1.setAdapter(adapter1);
                                createOrderExpressDeliveryDropBinding.llType1.setVisibility(View.VISIBLE);

//            signUpBinding.spType.setSelection(adapter.getCount());
//            customSpinnerAdapter = new CustomSpinnerForAll(context, R.layout.spinner_textview, vehicleList, getResources().getColor(R.color.black_color), getResources().getColor(R.color.light_black), getResources().getColor(R.color.transparent));
//            signUpBinding.spType.setAdapter(customSpinnerAdapter);

                                createOrderExpressDeliveryDropBinding.spType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        if (i != 0) {
                                            vehicleCategory = vehicleCategoryList.get(i).get(PN_VALUE);
                                            System.out.println("vehicleCategory"+vehicleCategory);

                                        } else {
                                            ((TextView) view).setTextSize(16);
                                            ((TextView) view).setTextColor(
                                                    getResources().getColorStateList(R.color.text_hint)
                                            );
                                            vehicleCategory = "";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                if(multiple == "multiple1"){
                                    for (int i = 0; i < vehicleCategoryList.size(); i++) {
                                        System.out.println("vehicleCategoryList.size()" +vehicleCategoryList.size());
                                        System.out.println("deliveryDTO.getType_of_truck()" +deliveryDTO.getType_of_truck());
                                        if (deliveryDTO.getType_of_truck() != null && deliveryDTO.getType_of_truck().equalsIgnoreCase(vehicleCategoryList.get(i).get(PN_VALUE))) {
                                            createOrderExpressDeliveryDropBinding.spType1.setSelection(i);
                                            System.out.println("vehicleCategoryList.size().i" +i);


                                            break;
                                        }
                                    }
                                   // createOrderExpressDeliveryDropBinding.spType1.setEnabled(false);
                                }

                                if(rescheduleStatus){
                                    for (int i = 0; i < vehicleCategoryList.size(); i++) {
                                        System.out.println("vehicleCategoryList.size()" +vehicleCategoryList.size());
                                        System.out.println("deliveryDTO.getType_of_truck()" +deliveryDTO.getType_of_truck());
                                        if (deliveryDTO.getType_of_truck() != null && deliveryDTO.getType_of_truck().equalsIgnoreCase(vehicleCategoryList.get(i).get(PN_VALUE))) {
                                            createOrderExpressDeliveryDropBinding.spType1.setSelection(i);
                                            System.out.println("vehicleCategoryList.size().i" +i);

                                            break;
                                        }
                                    }
                                   // createOrderExpressDeliveryDropBinding.spType1.setEnabled(false);

                                }

                            } else {
                                utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<VechicalCatagoryDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                    Log.e(TAG, t.toString());
                }
            });

        }
    }


    private void priceCollecrion(){

        Location loc1 = new Location("");
        loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
        loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

        Location loc2 = new Location("");
        loc2.setLatitude(Double.parseDouble(dropOffLat));
        loc2.setLongitude(Double.parseDouble(dropOffLong));

        float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;

        int parcelCount = Integer.parseInt(deliveryDTO.getNoOfPallets());
        switch (deliveryDTO.getDeliveryType()) {
            case "express":

                if( deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                    // if(deliveryDTO.getPickupBuildingType().equals("yes")&& deliveryDTO.getDropBuildingType().equals("yes")) {
                    if (parcelCount >= 1 && parcelCount <= 2) {
//                        System.out.println("PBPAPAPAPAPAPAPAPA" + parcelCount);
                        totalDeliveryCost =  Double.parseDouble("25");
                    }
                 /*   else if (parcelCount >= 25 && parcelCount <= 50) {
                        totalDeliveryCost = parcelCount * Double.parseDouble("7") + Double.parseDouble("25")+ Double.parseDouble("2");
                    } else if (parcelCount >= 51 && parcelCount <= 100) {
                        totalDeliveryCost = parcelCount * Double.parseDouble("5") + Double.parseDouble("25")+ Double.parseDouble("2");
                    } else if (parcelCount >= 101 && parcelCount <= 200) {
                        totalDeliveryCost = parcelCount * Double.parseDouble("4.5") + Double.parseDouble("25")+ Double.parseDouble("2");
                    } else if (parcelCount >= 201) {
                        totalDeliveryCost = parcelCount * Double.parseDouble("4") + Double.parseDouble("25")+ Double.parseDouble("2");
                    }*/
                    if(deliveryDTO.getPickupBuildingType().equals("yes")&&deliveryDTO.getDropBuildingType().equals("yes")){
                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                    } else if(deliveryDTO.getPickupBuildingType().equals("yes") || deliveryDTO.getDropBuildingType().equals("yes")) {
                        totalDeliveryCost = totalDeliveryCost + Double.parseDouble("45");
                    }

                }

                else if( deliveryDTO.getTypeGoods().equalsIgnoreCase("Envelop")) {
                      //  if (parcelCount >= 1 && parcelCount <= 24) {
                            System.out.println("PBPAPAPAPAPAPAPAPA" + parcelCount);
                            totalDeliveryCost =  Double.parseDouble("25") ;
                      //  }
                /*else if (parcelCount >= 25 && parcelCount <= 50) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("7") + Double.parseDouble("25") + Double.parseDouble("2");
                        } else if (parcelCount >= 51 && parcelCount <= 100) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("5") + Double.parseDouble("25") + Double.parseDouble("2");
                        } else if (parcelCount >= 101 && parcelCount <= 200) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("4.5") + Double.parseDouble("25") + Double.parseDouble("2");
                        } else if (parcelCount >= 201) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("4") + Double.parseDouble("25") + Double.parseDouble("2");
                        }*/
                        if(deliveryDTO.getPickupBuildingType().equals("yes")&&deliveryDTO.getDropBuildingType().equals("yes")){
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("4");
                        } else if(deliveryDTO.getPickupBuildingType().equals("yes") || deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("2");
                        }

                }

                               /* if (parcelCount >= 1) {
                                  for (int i = 0; priceDistanceDTO.getVehicle().getPallets().size() > i; i++) {
                                       if (priceDistanceDTO.getVehicle().getPallets().get(i).getPallets().equals("3")) {

                                           // totalDeliveryCost = parcelCount * Double.parseDouble("250");

                                            totalDeliveryCost = (parcelCount * Double.parseDouble(priceDistanceDTO.getVehicle().getPallets().get(i).getPrice())) + Double.parseDouble(priceDistanceDTO.getVehicle().getPrice());

                                        }
                                   }
//                            totalDeliveryCost =  parcelCount * Double.parseDouble(otherDTO.getVehicle().getIfPalletOne());
                                } else if (parcelCount == 0) {
                                    for (int i = 0; priceDistanceDTO.getVehicle().getPallets().size() > i; i++) {
                                        if (priceDistanceDTO.getVehicle().getPallets().get(i).getPallets().equals("2")) {
                                            totalDeliveryCost = (parcelCount * Double.parseDouble(priceDistanceDTO.getVehicle().getPallets().get(i).getPrice()))+ Double.parseDouble(priceDistanceDTO.getVehicle().getPrice());
                                        }
                                    }
//                            totalDeliveryCost =  parcelCount * Double.parseDouble(otherDTO.getVehicle().getIfPalletTwo());
                                } else if (parcelCount == 0) {
                                    for (int i = 0; priceDistanceDTO.getVehicle().getPallets().size() > i; i++) {
                                        if (priceDistanceDTO.getVehicle().getPallets().get(i).getPallets().equals("3")) {
                                            totalDeliveryCost = (parcelCount * Double.parseDouble(priceDistanceDTO.getVehicle().getPallets().get(i).getPrice()))+ Double.parseDouble(priceDistanceDTO.getVehicle().getPrice());
                                        }
                                    }
//                            totalDeliveryCost =  parcelCount * Double.parseDouble(otherDTO.getVehicle().getIfPalletMore());
                                }*/
                break;
            case "single":
                if( deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet")) {
                    if (distanceInmiles <= 70) {
                        if (parcelCount == 1) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("250");
                        } else if (parcelCount == 2) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("225");
                        } else if (parcelCount >= 3) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("200");
                        }
                        if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("220");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("155");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("175");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("155");
                        } else if (deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("175");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                        } else if (deliveryDTO.getDropElevator().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("110");
                        } else if (deliveryDTO.getDropElevator().equals("yes") || deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") || deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                        }
                    } else if (distanceInmiles > 70) {
                        float addDistanceInMiles = distanceInmiles - 70;
                        if (parcelCount == 1) {
                            totalDeliveryCost = (parcelCount * Double.parseDouble("250")) + (addDistanceInMiles * 0.65);
                        } else if (parcelCount == 2) {
                            totalDeliveryCost = (parcelCount * Double.parseDouble("225")) + (addDistanceInMiles * 0.65);
                        } else if (parcelCount >= 3) {

                            totalDeliveryCost = (parcelCount * Double.parseDouble("200")) + (addDistanceInMiles * 0.65);
                        }
                        if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("220");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("155");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("175");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("155");
                        } else if (deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("175");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                        } else if (deliveryDTO.getDropElevator().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("110");
                        } else if (deliveryDTO.getDropElevator().equals("yes") || deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") || deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                        }
                    }
                }else if( deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                    if (distanceInmiles <= 70) {
                        if ( parcelCount >= 1 && parcelCount <= 24) {
                            System.out.println("PBPAPAPAPAPAPAPAPA"+parcelCount);
                            totalDeliveryCost = parcelCount * Double.parseDouble("8");
                        }else if (parcelCount >= 25 && parcelCount <= 50) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("7");
                        } else if (parcelCount >= 51 && parcelCount <= 100) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("5");
                        }
                        else if (parcelCount >= 101 && parcelCount <= 200) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("4.5");
                        }
                        else if (parcelCount >= 201 ) {
                            totalDeliveryCost = parcelCount * Double.parseDouble("4");
                        }

                        if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("220");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("155");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("175");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("155");
                        } else if (deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("175");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                        } else if (deliveryDTO.getDropElevator().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("110");
                        } else if (deliveryDTO.getDropElevator().equals("yes") || deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") || deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                        }
                    } else if (distanceInmiles > 70) {
                        float addDistanceInMiles = distanceInmiles - 70;
                        if (parcelCount >= 1 && parcelCount <= 24) {
                            totalDeliveryCost = (parcelCount * Double.parseDouble("8")) + (addDistanceInMiles * 0.69);
                        }  else if (parcelCount >= 25 && parcelCount <= 50) {
                            totalDeliveryCost = (parcelCount * Double.parseDouble("7")) + (addDistanceInMiles * 0.69);
                        } else if (parcelCount >= 51 && parcelCount <= 100) {

                            totalDeliveryCost = (parcelCount * Double.parseDouble("5")) + (addDistanceInMiles * 0.69);
                        }
                        else if (parcelCount >= 101 && parcelCount <= 200) {

                            totalDeliveryCost = (parcelCount * Double.parseDouble("4.5")) + (addDistanceInMiles * 0.69);
                        }
                        else if (parcelCount >= 201) {

                            totalDeliveryCost = (parcelCount * Double.parseDouble("4")) + (addDistanceInMiles * 0.69);
                        }
                        if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("220");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("155");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("175");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("155");
                        } else if (deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("175");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") && deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                        } else if (deliveryDTO.getDropElevator().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getPickupLiftGate().equals("yes") && deliveryDTO.getDropElevator().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getDropBuildingType().equals("yes") && deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("110");
                        } else if (deliveryDTO.getDropElevator().equals("yes") || deliveryDTO.getPickupLiftGate().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("130");
                        } else if (deliveryDTO.getPickupBuildingType().equals("yes") || deliveryDTO.getDropBuildingType().equals("yes")) {
                            totalDeliveryCost = totalDeliveryCost + Double.parseDouble("90");
                        }
                    }else if( deliveryDTO.getTypeGoods().equalsIgnoreCase("Miscellaneous")) {
                        if (distanceInmiles <= 70) {
                            if (parcelCount == 1) {
                                totalDeliveryCost = parcelCount * Double.parseDouble("250");
                            } else if (parcelCount == 2) {
                                totalDeliveryCost = parcelCount * Double.parseDouble("225");
                            } else if (parcelCount >= 3) {
                                totalDeliveryCost = parcelCount * Double.parseDouble("200");
                            }
                        } else if (distanceInmiles > 70) {
                            float addDistanceInMiles = distanceInmiles - 70;
                            if (parcelCount == 1) {
                                totalDeliveryCost = (parcelCount * Double.parseDouble("250")) + (addDistanceInMiles * 0.65);
                            } else if (parcelCount == 2) {
                                totalDeliveryCost = (parcelCount * Double.parseDouble("225")) + (addDistanceInMiles * 0.65);
                            } else if (parcelCount >= 3) {

                                totalDeliveryCost = (parcelCount * Double.parseDouble("200")) + (addDistanceInMiles * 0.65);
                            }
                        }
                    }
                }
                break;
            case "multiple":
                break;
        }

//                        if(parcelCount == 1){
//                            totalDeliveryCost =  parcelCount * Double.parseDouble("250");
//                        }else if(parcelCount == 2){
//                            totalDeliveryCost =  parcelCount * Double.parseDouble("225");
//                        }else if(parcelCount >= 3){
//                            totalDeliveryCost =  parcelCount * Double.parseDouble("200");
//                        }

//                        if (vehicleType.equalsIgnoreCase(getString(R.string.bike))) {
//                            totalDeliveryCost = distanceInmiles * Double.parseDouble(otherDTO.getVehicle().getMotorbike());
//                        } else if (vehicleType.equalsIgnoreCase(getString(R.string.car))) {
//                            totalDeliveryCost = distanceInmiles * Double.parseDouble(otherDTO.getVehicle().getCar());
//                        } else if (vehicleType.equalsIgnoreCase(getString(R.string.van))) {
//                            totalDeliveryCost = distanceInmiles * Double.parseDouble(otherDTO.getVehicle().getVan());
//                        } else {
//                            totalDeliveryCost = distanceInmiles * Double.parseDouble(otherDTO.getVehicle().getTruck());
//                        }

//            driverDeliveryCost = totalDeliveryCost + (Float.parseFloat(priceDistanceDTO.getVehicle().getPrice()));
//          /              driverDeliveryCost = totalDeliveryCost + (Float.parseFloat("25"));

//                        driverDeliveryCost = totalDeliveryCost - ((totalDeliveryCost * Float.parseFloat(otherDTO.getVehicle().getDriverPercentage()) / 100));

        try {
            deliveryDTO.setDeliveryDistance(String.format("%.2f", Double.parseDouble(distanceInmiles + "")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String price = totalDeliveryCost + "";
            if (price.contains(",")) {
                price = price.replaceAll(",", "");
            } else {
                price = totalDeliveryCost+"";
            }

            deliveryDTO.setDeliveryCost(String.format("%2f", Double.parseDouble(price)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String price = driverDeliveryCost + "";
            if (price.contains(",")) {
                price = price.replaceAll(",", "");
            } else {
                price = driverDeliveryCost+"";
            }

            deliveryDTO.setDriverDeliveryCost(String.format("%2f", Double.parseDouble(price)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callOrderBookApi() {

        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {

            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            Map<String, String> map = new HashMap<>();
            map.put("user_id", appSession.getUser().getData().getUserId());
            map.put("parcel_height", deliveryDTO.getProductHeight());
            map.put("parcel_width", deliveryDTO.getProductWidth());
            map.put("parcel_lenght", deliveryDTO.getProductLength());
            map.put("parcel_weight", deliveryDTO.getProductWeight());
            map.put("delivery_type", deliveryDTO.getDeliveryType());
            //map.put("delivery_distance", distance_value);
            map.put("delivery_distance", deliveryDTO.getDeliveryDistance());
            map.put("pickup_building_type",deliveryDTO.getPickupBuildingType());
            map.put("drop_building_type",deliveryDTO.getDropBuildingType());
            map.put("drop_elevator",deliveryDTO.getDropElevator());
            map.put("type_of_truck",deliveryDTO.getType_of_truck());
            map.put("typeGoodsCategory",deliveryDTO.getTypeGoodsCategory());
            map.put("vehicle_type", deliveryDTO.getVehicleType());
            map.put("nameOfGoods",deliveryDTO.getNameOfGoods());
            map.put("weight_unit", deliveryDTO.getWeight_unit());
            map.put("pickUpLat", deliveryDTO.getPickupLat());
            map.put("pickUpLong", deliveryDTO.getPickupLong());
            map.put("dropOffLong", deliveryDTO.getDropoffLong());
            map.put("dropOffLat", deliveryDTO.getDropoffLat());
            map.put("delivery_time", deliveryDTO.getDeliveryTime());
            map.put(PN_APP_TOKEN, APP_TOKEN);
            map.put("dropoff_country_code", deliveryDTO.getDropoffCountryCode());
            map.put("pickup_country_code", deliveryDTO.getPickupCountryCode());
            map.put("pickup_elevator", deliveryDTO.getPickupLiftGate());
            map.put("classGoods", deliveryDTO.getClassGoods());
            map.put("typeGoods", deliveryDTO.getTypeGoods());
            map.put("noOfPallets", deliveryDTO.getNoOfPallets());
            map.put("is_pallet", deliveryDTO.getIs_pallet());
            System.out.println("ashjkahaskj-----> "+map);
           // System.out.println("goodsjdfkname-----> "+new Gson().toJson(deliveryDTO));
            APIInterface apiInterface = APIClient.getClient();
            Call<PricesDTO> call = apiInterface.priceCalculationApi(map);
            call.enqueue(new Callback<PricesDTO>() {
                @Override
                public void onResponse(Call<PricesDTO> call, Response<PricesDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {


                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {

                                System.out.println("successsbvvsdesponkkce----> "+new Gson().toJson(response.body().getPricedat()));
                                deliveryDTO.setNo_tax_delivery_cost(response.body().getPricedat().getTotal());
                                deliveryDTO.setDeliveryCost(response.body().getPricedat().getTotalPrice());

                                Log.d("multiplecost",new Gson().toJson(response.body()));
                                Log.d("multiplecost",new Gson().toJson(response.body().getResult()));
                                Log.d("multiplecost",new Gson().toJson(response.body().getPricedat()));

                                DeliveryCheckoutExpressDelivery deliveryCheckoutExpressDelivery = new DeliveryCheckoutExpressDelivery();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("deliveryDTO", deliveryDTO);
                                deliveryCheckoutExpressDelivery.setArguments(bundle);
                                addFragmentWithoutRemove(R.id.container_main, deliveryCheckoutExpressDelivery, "DeliveryCheckoutExpressDelivery");

//                                utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
//                                    @Override
//                                    public void onYes() {
//                                        System.out.println("success.responce----> "+response.body());
////                                        ((DrawerContentSlideActivity) context).popAllFragment();
//                                    }
//                                    @Override
//                                    public void onNo() {
//
//                                    }
//                                });
                            } else {
                                utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PricesDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e(TAG, t.toString());
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);

                }
            });
        }
    }


    public void vechiletypespinner(){
        listAll.clear();
        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.vehicle_type));
        vehicleList.add(hashMap1);
        listAll.add(getResources().getString(R.string.vehicle_type));

        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.bike));
        vehicleList.add(hashMap1);
        listAll.add(getResources().getString(R.string.bike));

        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.car));
        vehicleList.add(hashMap1);
        listAll.add(getResources().getString(R.string.car));

        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.van));
        vehicleList.add(hashMap1);
        listAll.add(getResources().getString(R.string.van));

/* nandha
    //    if(deliveryDTO.getDeliveryType().equals("express")) {
            hashMap1 = new HashMap<>();
            hashMap1.put(PN_NAME, "");
            hashMap1.put(PN_VALUE, getResources().getString(R.string.truck));
            vehicleList.add(hashMap1);
            listAll.add(getResources().getString(R.string.mini_truck));
    //    }
*/


        if(!deliveryDTO.getDeliveryType().equals("express")) {
            hashMap1 = new HashMap<>();
            hashMap1.put(PN_NAME, "");
            hashMap1.put(PN_VALUE, getResources().getString(R.string.truck));
            vehicleList.add(hashMap1);
            listAll.add(getResources().getString(R.string.truck));
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.spinner_text);
        adapter.setDropDownViewResource(R.layout.spinner_item_list);
        adapter.addAll(listAll);
        createOrderExpressDeliveryDropBinding.spType.setAdapter(adapter);
    }

//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        switch (checkedId){
//            case R.id.rb_inside_pickup:
//                dropoffLiftGate = "insidePickup";
//                break;
//            case R.id.rb_lift_gate:
//                dropoffLiftGate = "liftGate";
//                break;
//        }
//    }


    @Override
    public void onCameraIdle() {
        LatLng latLng = mMap.getCameraPosition().target;
        alertdropoffLat = latLng.latitude + "";
        alertdropoffLong = latLng.longitude + "";
//    distancecal(latLng);
        tv_pickup_location.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
//        createOrderExpressDeliveryBinding.etPickupAddress.setText(getAddressFromLatLong(latLng.latitude, latLng.longitude, false));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            MapsInitializer.initialize(context);
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setOnCameraIdleListener(this);
            mMap.setOnInfoWindowClickListener(this);
            if (mMap != null) {
                getLatLong();

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                final LatLng latLng = mMap.getCameraPosition().target;
//                    callGetNotifyCountApi();
//                    getNearDriver(latLng.latitude, latLng.longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getLatLong() {
        try {
            if (appSession.getLatitude() != null && !appSession.getLatitude().equals("") && !appSession.getLatitude().equals("0.0")) {
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(appSession.getLatitude()), Double.parseDouble(appSession.getLongitude())))
                                .zoom(15)
                                .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null);
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    appSession.setLatitude(location.getLatitude() + "");
                                    appSession.setLongitude(location.getLongitude() + "");

                                    if (appSession.getLatitude() != null && !appSession.getLatitude().equals("0.0") && !appSession.getLatitude().equals("0") && !appSession.getLatitude().equals("")) {
//                                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(appSession.getLatitude()), Double.parseDouble(appSession.getLongitude()))));
//                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                                        CameraPosition cameraPosition =
                                                new CameraPosition.Builder()
                                                        .target(new LatLng(Double.parseDouble(appSession.getLatitude()), Double.parseDouble(appSession.getLongitude())))
                                                        .zoom(15)
                                                        .build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null);
                                    }
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.e("","data :"+data.toString());
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String given_sendeor="sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            JSONObject json = null;
            JSONArray legs = null;

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, String> {

        // Parsing the data in non-ui thread


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... jsonData) {
            return jsonData[0];
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(String response) {

            try {

                JSONObject jsonObject = new JSONObject(response);
                JSONArray array = jsonObject.getJSONArray("routes");
                JSONObject routes = array.getJSONObject(0);
                JSONArray legs = routes.getJSONArray("legs");
                JSONObject steps = legs.getJSONObject(0);
                JSONObject distance = steps.getJSONObject("distance");
                String parsedDistance = distance.getString("text");

                Log.d("newdata",parsedDistance);

                //String strNew = parsedDistance.replace("km", "").trim();
                String  strNew = parsedDistance.substring(0,parsedDistance.length()-2);

                if(strNew.contains(",")){
                    strNew = strNew.replace(",$", "").replace(",","").trim();
                }

                Double val = Double.parseDouble(strNew);
                deliveryDTO.setDeliveryDistance(String.valueOf(val));
                Log.d("newdata",deliveryDTO.getDeliveryDistance());
                callOrderBookApi();


           } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}


