package com.pickanddrop.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.Autocomplete;
//import com.google.android.libraries.places.widget.AutocompleteActivity;
//import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.activities.SelectLocation;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.CreateOrderExpressDeliveryBinding;
import com.pickanddrop.dto.BoxSubCatDTO;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.MultipleDTO;
import com.pickanddrop.dto.Objectpojo;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.CustomSpinnerForAll;
import com.pickanddrop.utils.DrawableClickListener;
import com.pickanddrop.utils.Utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.location.places.ui.PlaceAutocomplete.getPlace;

public class CreateOrderExpressDelivery extends BaseFragment implements AppConstants, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnInfoWindowClickListener {

    private Context context;
    private AppSession appSession;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;


    private Utilities utilities;
    private CreateOrderExpressDeliveryBinding createOrderExpressDeliveryBinding;
    private String countryCode = "", deliveryType = "", pickupLat = "", pickupLong = "",
            alertpickupLat = "", alertpickupLong = "", companyName = "", firstName = "",
            lastName = "", mobile = "", pickUpAddress = "", itemDescription = "", itemQuantity = "",
            deliDate = "", deliTime = "",
            specialInstruction = "", pickDate = "", pickTime = "", pickupLiftGate = "no",
            pickupBuildingType = "no", nonpallet_pallets = "", ClassGood = "", TypeGood = "",
            NoofPallets = "",NoofPallets1 = "", productWidth = "", productHeight = "", productLength = "", productKg = "",
            ptypeofbox = "",
            productMeasure = "", productWeight = "",nameofgood = "";
    //    PlacesClient placesClient ;

    private PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    private static final int REQUEST_PICK_PLACE = 2345;
    private String weightunit = "pound";
    private int mYear, mMonth, mDay;
    private Calendar c;
    private DeliveryDTO.Data deliveryDTO;
    private DrawableClickListener clickListener;

    private DatePickerDialog datePickerDialog;
    TextView tv_pickup_location;
    private String TAG = CreateOrderOne.class.getName();
    private boolean rescheduleStatus = false;
    private DeliveryDTO.Data data;
    private BoxSubCatDTO.DataBox sub;
    private ArrayList<HashMap<String, String>> measureTypeList;
    private ArrayList<HashMap<String, String>> poundTypeList;
    private ArrayList<HashMap<String, String>> classTypeList;
    private ArrayList<HashMap<String, String>> parcelTypeList1;
    private ArrayList<HashMap<String, String>> ptypeofboxTypeList;
    private CustomSpinnerForAll customSpinnerAdapter;
    AlertDialog.Builder alert_builder;
    AlertDialog alertDialog;
    MapView mv_home;
    ImageView alertBack;
    private Button btnSelectLocation;

    private ArrayList<MultipleDTO> deliveryDTOArrayList = new ArrayList<>();
    private MultipleDTO multipleDTO;
    String rech;

    Objectpojo onjectPojo = new Objectpojo();

    final ArrayList<String> listAll3 = new ArrayList<String>();
    HashMap<String, String> hashMapbox = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("delivery_type")) {
            deliveryType = getArguments().getString("delivery_type");
            Log.e("hereis", deliveryType);
        }

        if (deliveryType == "single"){
//            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
//            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
//            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);
            // i will do it latter
        }
        if (deliveryType == "multiple1") {

            if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
                data = getArguments().getParcelable("deliveryDTO");
                // rescheduleStatus = true;
            }
        } else {
            if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
                data = getArguments().getParcelable("deliveryDTO");


                rescheduleStatus = true;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createOrderExpressDeliveryBinding = DataBindingUtil.inflate(inflater, R.layout.create_order_express_delivery, container, false);
        if (deliveryType.equalsIgnoreCase("multiple1")) {

            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);
//            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
//            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
//            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);
//


        } else if(deliveryType.equalsIgnoreCase("miscellaneous")){
            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.VISIBLE);
//            createOrderExpressDeliveryBinding.etPickupDate.setVisibility(View.VISIBLE);
//            createOrderExpressDeliveryBinding.etPickupTime.setVisibility(View.VISIBLE);
//            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);
            //createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);

        }else if (deliveryType.equalsIgnoreCase("single")){
            Log.d("success", "onCreateView: ");
            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);
            // i will do it latter

        }
        else {
//            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
//            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
//            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);
//

            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);
            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);


            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);
            // i will do it latter

        }

        return createOrderExpressDeliveryBinding.getRoot();
    }

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        //Toast.makeText(context,"CreateOrderExpressDelivery",Toast.LENGTH_LONG).show();
//        placesClient = Places.createClient(context);
          alert_builder = new AlertDialog.Builder(context,R.style.DialogTheme);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.select_location_with_map,null);
        mv_home = dialogView.findViewById(R.id.mv_home);
        tv_pickup_location = dialogView.findViewById(R.id.tv_pickup_location);
        btnSelectLocation = dialogView.findViewById(R.id.btn_select_location);
        alertBack = dialogView.findViewById(R.id.alert_back);
        btnSelectLocation.setOnClickListener(this);
        tv_pickup_location.setOnClickListener(this);
        alertBack.setOnClickListener(this);
        setMap();
        alert_builder.setView(dialogView);
        alertDialog = alert_builder.create();



        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();


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

        if (deliveryType == "multiple1") {

            createOrderExpressDeliveryBinding.etFirstName.setText(data.getPickupFirstName());
            createOrderExpressDeliveryBinding.etLastName.setText(data.getPickupLastName());
            createOrderExpressDeliveryBinding.etMobile.setText(data.getPickupMobNumber());
            createOrderExpressDeliveryBinding.etPickupAddress.setText(data.getPickupaddress());
            createOrderExpressDeliveryBinding.etPickupDate.setText(data.getPickupDate());

            if(data.getPickupLiftGate().equalsIgnoreCase("yes")){
                createOrderExpressDeliveryBinding.rbLiftGate.setChecked(true);
                pickupLiftGate = "yes";

            }else if(data.getPickupLiftGate().equalsIgnoreCase("no")){
                createOrderExpressDeliveryBinding.rbLiftGate.setChecked(false);
                pickupLiftGate = "no";
            }

            if(data.getPickupBuildingType().equalsIgnoreCase("yes")){
                pickupBuildingType = "yes";

                createOrderExpressDeliveryBinding.rbInsidePickup.setChecked(true);
            }else if(data.getPickupLiftGate().equalsIgnoreCase("no")){
                pickupBuildingType = "no";

                createOrderExpressDeliveryBinding.rbInsidePickup.setChecked(false);
            }
            createOrderExpressDeliveryBinding.etPickupTime.setText(data.getDeliveryTime());
            createOrderExpressDeliveryBinding.ccp.setCountryForPhoneCode(Integer.parseInt(data.getPickupCountryCode()));
            data.setDeliveryType("multiple1");
            deliveryType = data.getDeliveryType();
            System.out.println("deliverytype1"+data.getDeliveryType());
            pickupLat = data.getPickupLat();
            pickupLong = data.getPickupLong();

        } else {
            if (data.getDeliveryStatus().equals("6")) {
              EnablebefireAcceptDriver(false);
            }else{
                EnablebefireAcceptDriver(true);
            }
            createOrderExpressDeliveryBinding.etFirstName.setText(data.getPickupFirstName());
            createOrderExpressDeliveryBinding.etLastName.setText(data.getPickupLastName());
            createOrderExpressDeliveryBinding.etMobile.setText(data.getPickupMobNumber());
            createOrderExpressDeliveryBinding.etPickupAddress.setText(data.getPickupaddress());
            createOrderExpressDeliveryBinding.etPickSpecialInst.setText(data.getPickupSpecialInst());

            createOrderExpressDeliveryBinding.etPickupDate.setText(data.getPickupDate());

            if(data.getDeliveryType().equals("miscellaneous")){
                for (int i = 0; i < parcelTypeList1.size(); i++) {
                    System.out.println("parcelTypeList1--->" + parcelTypeList1.size());
                    if (data.getTypeGoods() != null && data.getTypeGoods().equalsIgnoreCase(parcelTypeList1.get(i).get(PN_VALUE))) {
                        createOrderExpressDeliveryBinding.spType1.setSelection(i);
                        System.out.println("parcelTypeList1--->" + parcelTypeList1.size());
                        break;
                    }
                }
                createOrderExpressDeliveryBinding.etNumberOfGoods.setText(data.getNameOfGoods());
            }

            createOrderExpressDeliveryBinding.etPickupTime.setText(data.getDeliveryTime());

            System.out.println("getDeliverytype" + data.getDeliveryType());

            if (data.getDeliveryType().equalsIgnoreCase("express")) {

                createOrderExpressDeliveryBinding.spType1.setVisibility(View.VISIBLE);
                createOrderExpressDeliveryBinding.etGoodClass.setVisibility(View.GONE);
                createOrderExpressDeliveryBinding.llLiftGate.setVisibility(View.GONE);
                createOrderExpressDeliveryBinding.etPickupTime.setVisibility(View.GONE);

                for (int i = 0; i < classTypeList.size(); i++) {
                    if (data.getClassGoods() != null && data.getClassGoods().equalsIgnoreCase(classTypeList.get(i).get(PN_VALUE))) {
                        createOrderExpressDeliveryBinding.etGoodClass.setSelection(i);
                        break;
                    }
                }

                for (int i = 0; i < poundTypeList.size(); i++) {
                    System.out.println("sdlkfjlskdfl--->"+data.getWeight_unit());
                    if (data.getWeight_unit() != null && data.getWeight_unit().equalsIgnoreCase(poundTypeList.get(i).get(PN_VALUE))) {
                        createOrderExpressDeliveryBinding.spKgPound.setSelection(i);
                        break;
                    }
                }

            }
            for (int i = 0; i < poundTypeList.size(); i++) {
                System.out.println("sdlkfjlskdfl--->"+data.getWeight_unit());
                if (data.getWeight_unit() != null && data.getWeight_unit().equalsIgnoreCase(poundTypeList.get(i).get(PN_VALUE))) {
                    createOrderExpressDeliveryBinding.spKgPound.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < poundTypeList.size(); i++) {
                if (data.getClassGoods() != null && data.getClassGoods().equalsIgnoreCase(poundTypeList.get(i).get(PN_VALUE))) {
                    createOrderExpressDeliveryBinding.spKgPound.setSelection(i);
                    break;
                }
            }


            for (int i = 0; i < parcelTypeList1.size(); i++) {
                System.out.println("parcelTypeList1--->" + parcelTypeList1.size());
                if (data.getTypeGoods() != null && data.getTypeGoods().equalsIgnoreCase(parcelTypeList1.get(i).get(PN_VALUE))) {
                    createOrderExpressDeliveryBinding.spType1.setSelection(i);
                    System.out.println("parcelTypeList1--->" + parcelTypeList1.size());
                    break;
                }
            }


            for (int i = 0; i < classTypeList.size(); i++) {
                if (data.getClassGoods() != null && data.getClassGoods().equalsIgnoreCase(classTypeList.get(i).get(PN_VALUE))) {
                    createOrderExpressDeliveryBinding.etGoodClass.setSelection(i);
                    break;
                }
            }

            String noOfBox = createOrderExpressDeliveryBinding.etPalletsCount2.getText().toString().trim();
            createOrderExpressDeliveryBinding.etPalletsCount.setText(data.getNoOfPallets());
            createOrderExpressDeliveryBinding.etPalletsCount2.setText(data.getNoOfPallets1());
            createOrderExpressDeliveryBinding.etGoodWidth.setText(data.getProductWidth());
            createOrderExpressDeliveryBinding.etGoodHight.setText(data.getProductHeight());
            createOrderExpressDeliveryBinding.etGoodLength.setText(data.getProductLength());
            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setText(data.getProductWeight());


            if (data.getPickupBuildingType().equals("no")) {
                createOrderExpressDeliveryBinding.rbInsidePickup.setChecked(false);
                pickupBuildingType = "no";
            } else {
                createOrderExpressDeliveryBinding.rbInsidePickup.setChecked(true);
                System.out.println("inside");
                pickupBuildingType = "yes";
            }

            if (data.getPickupLiftGate().equals("yes")) {
                createOrderExpressDeliveryBinding.rbLiftGate.setChecked(true);
                System.out.println("inside");
                pickupLiftGate = "yes";
            } else {
                createOrderExpressDeliveryBinding.rbLiftGate.setChecked(false);
                pickupLiftGate = "no";
            }

            try {
                createOrderExpressDeliveryBinding.ccp.setCountryForPhoneCode(Integer.parseInt(data.getPickupCountryCode()));
            }catch(Exception e){
                e.printStackTrace();
            }

            deliveryType = data.getDeliveryType();
            System.out.println("Apple"+deliveryType);
            System.out.println("Apple"+data.getDeliveryType());

            if(deliveryType == "multiple")
            {
                rech = "multiple";
            }

            pickupLat = data.getPickupLat();
            pickupLong = data.getPickupLong();

        }
    }

    public void EnablebefireAcceptDriver(Boolean value){
        createOrderExpressDeliveryBinding.etFirstName.setEnabled(value);
        createOrderExpressDeliveryBinding.etLastName.setEnabled(value);
        createOrderExpressDeliveryBinding.etMobile.setEnabled(value);
        createOrderExpressDeliveryBinding.ccp.setClickable(value);
        createOrderExpressDeliveryBinding.ccp.setEnabled(value);
        createOrderExpressDeliveryBinding.etPickupAddress.setEnabled(value);
        createOrderExpressDeliveryBinding.rbLiftGate.setEnabled(value);
        createOrderExpressDeliveryBinding.rbInsidePickup.setEnabled(value);
        createOrderExpressDeliveryBinding.etPickupDate.setEnabled(value);
        createOrderExpressDeliveryBinding.etPickupTime.setEnabled(value);
        createOrderExpressDeliveryBinding.etNumberOfGoods.setEnabled(value);
        createOrderExpressDeliveryBinding.etPickSpecialInst.setEnabled(value);
        createOrderExpressDeliveryBinding.spTypeOfBox.setEnabled(value);

        createOrderExpressDeliveryBinding.etPalletsCount.setEnabled(value);
        createOrderExpressDeliveryBinding.etPalletsCount.setClickable(value);
        createOrderExpressDeliveryBinding.etGoodWidth.setEnabled(value);
        createOrderExpressDeliveryBinding.etGoodHight.setClickable(value);
        createOrderExpressDeliveryBinding.etGoodLength.setEnabled(value);
        createOrderExpressDeliveryBinding.etGoodLength.setClickable(value);
        createOrderExpressDeliveryBinding.etPalletsTotalWeight.setEnabled(value);
        createOrderExpressDeliveryBinding.etPalletsTotalWeight.setClickable(value);

        createOrderExpressDeliveryBinding.spKgPound.setClickable(value);
        createOrderExpressDeliveryBinding.etGoodClass.setClickable(value);
        createOrderExpressDeliveryBinding.spType1.setClickable(value);
        createOrderExpressDeliveryBinding.spKgPound.setEnabled(value);
        createOrderExpressDeliveryBinding.etGoodClass.setEnabled(value);
        createOrderExpressDeliveryBinding.spType1.setEnabled(value);


    }

    private void initToolBar() {

    }

    public void setEnableOrDisable(boolean value) {
//        signUpBinding.etCompany.setEnabled(value);
        createOrderExpressDeliveryBinding.etFirstName.setEnabled(value);
        createOrderExpressDeliveryBinding.etLastName.setEnabled(value);
        createOrderExpressDeliveryBinding.etMobile.setEnabled(value);
        createOrderExpressDeliveryBinding.ccp.getSelectedCountryCode();
        createOrderExpressDeliveryBinding.etPickupDate.setEnabled(value);
        createOrderExpressDeliveryBinding.etPickupTime.setEnabled(value);
        createOrderExpressDeliveryBinding.etFirstName.setEnabled(value);
        createOrderExpressDeliveryBinding.rbLiftGate.setEnabled(value);
        createOrderExpressDeliveryBinding.rbLiftGate.setEnabled(value);
        createOrderExpressDeliveryBinding.rbInsidePickup.setEnabled(value);

    }


    private void initView() {
//        createOrderExpressDeliveryBinding.ccp.registerPhoneNumberTextView(createOrderExpressDeliveryBinding.etMobile);
        c = Calendar.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        getLatLong();


        measureTypeList = new ArrayList<>();
        parcelTypeList1 = new ArrayList<>();
        classTypeList = new ArrayList<>();
        poundTypeList = new ArrayList<>();
        ptypeofboxTypeList = new ArrayList<>();

        if(rescheduleStatus){
            deliveryType = data.getDeliveryType();
        }

        if (deliveryType == "express") {
            createOrderExpressDeliveryBinding.toolbarTitle.setText("Express Delivery");
        } else if (deliveryType == "single") {
            createOrderExpressDeliveryBinding.toolbarTitle.setText("Single Delivery");
        } else if (deliveryType == "multiple" || deliveryType == "multiple1") {
            createOrderExpressDeliveryBinding.toolbarTitle.setText("Multiple Delivery");
        }else if (deliveryType == "miscellaneous") {
            createOrderExpressDeliveryBinding.toolbarTitle.setText("Miscellaneous");

            //sangeetha
        }


        createOrderExpressDeliveryBinding.ivBack.setOnClickListener(this);
        createOrderExpressDeliveryBinding.etPickupAddress.setOnClickListener(this);
        createOrderExpressDeliveryBinding.btnNext.setOnClickListener(this);
        createOrderExpressDeliveryBinding.etPickupDate.setOnClickListener(this);
        createOrderExpressDeliveryBinding.etPickupTime.setOnClickListener(this);
        if (deliveryType == "express") {
            createOrderExpressDeliveryBinding.etGoodClass.setVisibility(View.GONE);
        }

        createOrderExpressDeliveryBinding.ccp.registerPhoneNumberTextView(createOrderExpressDeliveryBinding.etMobile);
        createOrderExpressDeliveryBinding.ccp.setDefaultCountryUsingNameCode("US");
        createOrderExpressDeliveryBinding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tt = s.toString();
                if (createOrderExpressDeliveryBinding.ccp.isValid()) {
                    createOrderExpressDeliveryBinding.llMobileNumberError.setVisibility(View.GONE);
                } else {
                    createOrderExpressDeliveryBinding.llMobileNumberError.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final ArrayList<String> listAll = new ArrayList<String>();
        HashMap<String, String> hashMap2 = new HashMap<>();

        hashMap2 = new HashMap<>();
        hashMap2.put(PN_NAME, "");
        hashMap2.put(PN_VALUE, getResources().getString(R.string.type_of_parcel));

        parcelTypeList1.add(hashMap2);
        listAll.add(getResources().getString(R.string.type_of_parcel));

        if (deliveryType.equalsIgnoreCase("single")){
            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.pallet));

            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.box));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.box));
        }
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


//        if (deliveryType.equalsIgnoreCase("multiple")){
//                        hashMap2 = new HashMap<>();
//            hashMap2.put(PN_NAME,"");
//            hashMap2.put(PN_VALUE,"PalletAndBox");
//            parcelTypeList1.add(hashMap2);
//            listAll.add(getResources().getString(R.string.pallet_and_box));
//        }

        if(!deliveryType.equalsIgnoreCase("express") && !deliveryType.equalsIgnoreCase("miscellanea") && !deliveryType.equalsIgnoreCase("single")) {

            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.pallet));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.pallet));

            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME, "");
            hashMap2.put(PN_VALUE, getResources().getString(R.string.box));
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.box));

            hashMap2 = new HashMap<>();
            hashMap2.put(PN_NAME,"");
            hashMap2.put(PN_VALUE,"PalletAndBox");
            parcelTypeList1.add(hashMap2);
            listAll.add(getResources().getString(R.string.pallet_and_box));
        }




        System.out.println("productMeasure1" + productMeasure);


        ArrayAdapter adapter1 = new ArrayAdapter<String>(context, R.layout.spinner_text, listAll);
        adapter1.setDropDownViewResource(R.layout.spinner_item_list);
        createOrderExpressDeliveryBinding.spType1.setAdapter(adapter1);


        //  customSpinnerAdapter = new CustomSpinnerForAll(context, R.layout.spinner_textview, measureTypeList1, getResources().getColor(R.color.black_color), getResources().getColor(R.color.light_black), getResources().getColor(R.color.transparent));
        //     createOrderExpressDeliveryBinding.spType1.setAdapter(customSpinnerAdapter);

        createOrderExpressDeliveryBinding.spType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    productMeasure = parcelTypeList1.get(i).get(PN_VALUE);
                    System.out.println("productMeasure11" + productMeasure);

                    if(deliveryType.equalsIgnoreCase("express")) {
                        selectbox();

                        if (productMeasure.equalsIgnoreCase("Box")) {
                            createOrderExpressDeliveryBinding.etPalletsCount.setHint("Number of Box");
                            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);

                            // added 2

                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);

                        }  else if (productMeasure.equalsIgnoreCase("Envelop")) {
                            System.out.println("Envelop---> ");
                            createOrderExpressDeliveryBinding.etPalletsCount.setHint("Number of Envelop");
                            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);

                            // added 2

                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);

                        }
                    }
//                    else if (productMeasure.equalsIgnoreCase("sinle")){
//                        Log.d("single", "onItemSelected: single");
//                        createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
//                        createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
//                        createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);
//                        // i will do it latter
//                    }

                    else {

                        if (productMeasure.equalsIgnoreCase("Box")) {


                      //      selectbox();

                            // if add the categoriews of box plz remave this code
                            createOrderExpressDeliveryBinding.etPalletsCount.setHint("Number Of Box");
                            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);
                            // End

                            // createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.GONE);
                            // createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);
                            selectbox();
                            //  createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.GONE);
                            //  createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.GONE);
                            // will add the categories of box  selectbox();


                            // added 2

                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);


                        } else if (productMeasure.equalsIgnoreCase("miscellaneous")) {

                            //createOrderExpressDeliveryBinding.etPalletsCount.setHint("Number of Miscellanea");
                            //createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spType1.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.VISIBLE);

                            createOrderExpressDeliveryBinding.etPickupDate.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPickupTime.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);

                            // added 2

                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);



                        } else if (productMeasure.equalsIgnoreCase("Pallet")) {
                            createOrderExpressDeliveryBinding.etPalletsCount.setHint("Number of Pallet");
                            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);


                            // added 2

                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);

                        }
                        else if (productMeasure.equalsIgnoreCase("PalletAndBox")) {
                            getBoxSubCatogryApi();




                            // here is for pallet
//
                            createOrderExpressDeliveryBinding.etPalletsCount.setHint("Number of Pallet");
                            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setHint("Pallet pound");
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);




                            // if add the categoriews of box plz remave this code
                            createOrderExpressDeliveryBinding.etPalletsCount2.setHint("Number Of Box");
                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setHint("Box pound");
                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.VISIBLE);

                            // End

                            // createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.GONE);
                            // createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.VISIBLE);
                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);
                            selectbox();









                            //  createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.GONE);
                            //  createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.GONE);
                            // will add the categories of box  selectbox();


                            // added 2

//                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.llKgPound2.setVisibility(View.GONE);
//

                            // here is for box

//                            createOrderExpressDeliveryBinding.etPalletsCount2.setHint("Number Of Box");
//                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.VISIBLE);// be pound
//                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);
//
//                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
//                            //createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);
//                            selectbox();






//                            createOrderExpressDeliveryBinding.etPalletsCount.setHint("Number of Pallet");
//                            createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.VISIBLE);
//                        // i will'b use it later
////                            createOrderExpressDeliveryBinding.etPalletsCount2.setHint("Number Of Box");
////                            createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.VISIBLE);
////                            createOrderExpressDeliveryBinding.etPickSpecialInst2.setVisibility(View.VISIBLE);
////                            createOrderExpressDeliveryBinding.spKgPound2.setVisibility(View.VISIBLE);
////                            createOrderExpressDeliveryBinding.etPalletsTotalWeight2.setVisibility(View.VISIBLE);
//                            // End
//
//                            // createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.GONE);
//                            // createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.VISIBLE);
//                            createOrderExpressDeliveryBinding.etNumberOfGoods.setVisibility(View.GONE);
//                            createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);
//                            selectbox();
                        }

                    }
                } else {
                    ((TextView) view).setTextSize(16);
                    ((TextView) view).setTextColor(
                            getResources().getColorStateList(R.color.text_hint)
                    );

                    productMeasure = "";
                    createOrderExpressDeliveryBinding.etGoodWidth.setVisibility(View.GONE);
                    createOrderExpressDeliveryBinding.etGoodHight.setVisibility(View.GONE);
                    createOrderExpressDeliveryBinding.etGoodLength.setVisibility(View.GONE);
                    createOrderExpressDeliveryBinding.etPalletsCount.setVisibility(View.GONE);
                    createOrderExpressDeliveryBinding.etPalletsTotalWeight.setVisibility(View.GONE);
                    createOrderExpressDeliveryBinding.etPickSpecialInst.setVisibility(View.GONE);
                    createOrderExpressDeliveryBinding.spKgPound.setVisibility(View.GONE);
                    createOrderExpressDeliveryBinding.etPalletsCount2.setVisibility(View.GONE);
//                    createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
             /*   if(productMeasure != null & !productMeasure.equals(""))
                    productMeasure = measureTypeList1.get(0).get(PN_VALUE);*/
            }
        });


        if(deliveryType.equalsIgnoreCase("miscellaneous")){
            createOrderExpressDeliveryBinding.spType1.setSelection(1);

//            createOrderExpressDeliveryBinding.spType1.setVisibility(View.GONE);

        }


        HashMap<String, String> hashMap1 = new HashMap<>();

        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.feet));
        measureTypeList.add(hashMap1);
        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.cm));
        measureTypeList.add(hashMap1);

        customSpinnerAdapter = new CustomSpinnerForAll(context, R.layout.spinner_textview, measureTypeList, getResources().getColor(R.color.black_color), getResources().getColor(R.color.light_black), getResources().getColor(R.color.transparent));
        createOrderExpressDeliveryBinding.spType.setAdapter(customSpinnerAdapter);

        createOrderExpressDeliveryBinding.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i != 0) {
                productMeasure = measureTypeList.get(i).get(PN_VALUE);
//                } else {
//                    productMeasure = "";
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (productMeasure != null & !productMeasure.equals(""))
                    productMeasure = measureTypeList.get(0).get(PN_VALUE);
            }
        });


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
        createOrderExpressDeliveryBinding.spKgPound.setAdapter(adapterkg);
        createOrderExpressDeliveryBinding.spKgPound2.setAdapter(adapterkg);

        // for box
        createOrderExpressDeliveryBinding.spKgPound2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i != 0) {
                productKg = poundTypeList.get(i).get(PN_VALUE);
                if (productKg.equalsIgnoreCase("Pound")) {
                    weightunit = "pound";
//                        data.setWeight_unit("pound");
                    createOrderExpressDeliveryBinding.etPalletsTotalWeight.setHint("Enter Pound");
                } else if (productKg.equalsIgnoreCase("Kilogram")) {
                    weightunit = "kilogram";
//                        data.setWeight_unit("kilogram");
                    createOrderExpressDeliveryBinding.etPalletsTotalWeight.setHint("Enter Kilogram");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (productKg != null & !productKg.equals(""))
                    productKg = poundTypeList.get(0).get(PN_VALUE);
            }
        });


        createOrderExpressDeliveryBinding.spKgPound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i != 0) {
                    productKg = poundTypeList.get(i).get(PN_VALUE);
                    if (productKg.equalsIgnoreCase("Pound")) {
                        weightunit = "pound";
//                        data.setWeight_unit("pound");
                        createOrderExpressDeliveryBinding.etPalletsTotalWeight.setHint("Enter Pound");
                    } else if (productKg.equalsIgnoreCase("Kilogram")) {
                        weightunit = "kilogram";
//                        data.setWeight_unit("kilogram");
                        createOrderExpressDeliveryBinding.etPalletsTotalWeight.setHint("Enter Kilogram");
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (productKg != null & !productKg.equals(""))
                    productKg = poundTypeList.get(0).get(PN_VALUE);
            }
        });

        final ArrayList<String> listAll1 = new ArrayList<String>();
        HashMap<String, String> hashMapClass = new HashMap<>();
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

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            ListPopupWindow popupWindow = (ListPopupWindow) popup.get(createOrderExpressDeliveryBinding.etGoodClass);

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
        createOrderExpressDeliveryBinding.etGoodClass.setAdapter(adapter);


        //   customSpinnerAdapter = new CustomSpinnerForAll(context, R.layout.spinner_textview, classTypeList, getResources().getColor(R.color.black_color), getResources().getColor(R.color.light_black), getResources().getColor(R.color.transparent));
        //  createOrderExpressDeliveryBinding.etGoodClass.setAdapter(customSpinnerAdapter);

        createOrderExpressDeliveryBinding.etGoodClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        if (deliveryType.equals("express")) {
            createOrderExpressDeliveryBinding.llLiftGate.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.date.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etPickupTime.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.etGoodType.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.tvFourtyfivedoller.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.tvSixtyfivedoller.setVisibility(View.GONE);
            createOrderExpressDeliveryBinding.tvTwodoller.setVisibility(View.VISIBLE);
            createOrderExpressDeliveryBinding.etPickupDate.setVisibility(View.VISIBLE);



            // Nandha set a margin in java code

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) createOrderExpressDeliveryBinding.llInsidePickup.getLayoutParams();
            params.setMargins(0, 0, 0, 0); //substitute parameters for left, top, right, bottom
            createOrderExpressDeliveryBinding.llInsidePickup.setLayoutParams(params);

            // createOrderExpressDeliveryBinding.spType1.setSelection(2);
            // createOrderExpressDeliveryBinding.spType1.setEnabled(false);

            createOrderExpressDeliveryBinding.spType1.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) createOrderExpressDeliveryBinding.etPickupDate.getLayoutParams();
            params1.setMargins(0, 20, 0, 0); //substitute parameters for left, top, right, bottom
            createOrderExpressDeliveryBinding.etPickupDate.setLayoutParams(params1);

        }

        createOrderExpressDeliveryBinding.tvPickinfo.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        utilities.dialogOK(context, getString(R.string.validation_title), "Extra charge of $2 will be added for Inside pickup", getString(R.string.ok), false);
                        break;

                    default:
                        break;
                }
            }

        });

        createOrderExpressDeliveryBinding.tvDropinfo.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        utilities.dialogOK(context, getString(R.string.validation_title), "Extra charge of $2 will be added for Lift gate ", getString(R.string.ok), false);
                        break;

                    default:
                        break;
                }
            }

        });


        createOrderExpressDeliveryBinding.rbInsidePickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deliveryType.equals("express")) {
                    if (createOrderExpressDeliveryBinding.rbInsidePickup.isChecked()) {
                        //  utilities.dialogOK(context, getString(R.string.validation_title), "If seleted the InsidePickup to be added $ 2", getString(R.string.ok), false);
                        pickupBuildingType = "yes";
                    } else {
                        pickupBuildingType = "no";
                    }
                } else if (deliveryType.equals("express")) {
                    if (createOrderExpressDeliveryBinding.rbInsidePickup.isChecked()) {
                        //  utilities.dialogOK(context, getString(R.string.validation_title), "If seleted the InsidePickup to be added $ 2", getString(R.string.ok), false);
                        pickupBuildingType = "yes";
                    } else {
                        pickupBuildingType = "no";
                    }
                }
            }
        });
        createOrderExpressDeliveryBinding.rbLiftGate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (createOrderExpressDeliveryBinding.rbLiftGate.isChecked()) {
                    //  utilities.dialogOK(context, getString(R.string.validation_title), "If seleted the LiftGate to be added $ 2", getString(R.string.ok), false);
                    pickupLiftGate = "yes";
                } else {
                    pickupLiftGate = "no";
                }

            }
        });

        if (deliveryType == "multiple1") {
            setValues();
            //setEnableOrDisable(false);
        } else {
            if (rescheduleStatus) {
//                deliveryType = data.getDeliveryType();
                setValues();
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:

                UserHome userHome = new UserHome();
                Utilities.hideKeyboard(createOrderExpressDeliveryBinding.btnNext);
                countryCode = createOrderExpressDeliveryBinding.ccp.getSelectedCountryCode();
                Log.e(TAG, ">>>>>>>>>>>>>>" + countryCode);


                firstName = createOrderExpressDeliveryBinding.etFirstName.getText().toString();
                lastName = createOrderExpressDeliveryBinding.etLastName.getText().toString();
                mobile = createOrderExpressDeliveryBinding.etMobile.getText().toString();
                pickUpAddress = createOrderExpressDeliveryBinding.etPickupAddress.getText().toString();
                specialInstruction = createOrderExpressDeliveryBinding.etPickSpecialInst.getText().toString();
                pickDate = createOrderExpressDeliveryBinding.etPickupDate.getText().toString();
                pickTime = createOrderExpressDeliveryBinding.etPickupTime.getText().toString();

                TypeGood = createOrderExpressDeliveryBinding.spType1.getSelectedItem().toString();

                System.out.println("spType1" + TypeGood);

                NoofPallets = createOrderExpressDeliveryBinding.etPalletsCount.getText().toString();
                NoofPallets1 = createOrderExpressDeliveryBinding.etPalletsCount2.getText().toString();


                nameofgood = createOrderExpressDeliveryBinding.etNumberOfGoods.getText().toString();

                productWidth = createOrderExpressDeliveryBinding.etGoodWidth.getText().toString();
                productHeight = createOrderExpressDeliveryBinding.etGoodHight.getText().toString();
                productLength = createOrderExpressDeliveryBinding.etGoodLength.getText().toString();
//                productMeasure= createOrderExpressDeliveryBinding.et.getText().toString();
                productWeight = createOrderExpressDeliveryBinding.etPalletsTotalWeight.getText().toString();

                System.out.println("productWeight" + productWeight);

                if (isValid()) {
                    CreateOrderExpressDeliveryDrop createOrderExpressDeliveryDrop = new CreateOrderExpressDeliveryDrop();
                    DeliveryDTO.Data deliveryDTO = null;
                    Bundle bundle = new Bundle();
                    if (rescheduleStatus) {
                        deliveryDTO = data;
                        bundle.putString("rescheduleStatus", "rescheduleStatus");
                    } else if (deliveryType == "multiple1") {
                        deliveryDTO = data;
                        bundle.putString("delivery_type", deliveryType);
                    } else {
                        deliveryDTO = new DeliveryDTO().new Data();
                    }

                    deliveryDTO.setPickupFirstName(firstName);
                    deliveryDTO.setWeight_unit(weightunit);
                    System.out.println("werrrrrrrr--->" + weightunit);
                    deliveryDTO.setPickupLastName(lastName);
                    deliveryDTO.setPickupMobNumber(mobile);
                    deliveryDTO.setPickupaddress(pickUpAddress);
                    deliveryDTO.setPickupLiftGate(pickupLiftGate);
                    deliveryDTO.setPickupBuildingType(pickupBuildingType);
                    deliveryDTO.setPickupDate(pickDate);
                    deliveryDTO.setDeliveryTime(pickTime);
                    deliveryDTO.setClassGoods(ClassGood);
                    deliveryDTO.setTypeGoods(TypeGood.toLowerCase().trim());
                    deliveryDTO.setNoOfPallets(NoofPallets);
                    deliveryDTO.setNoOfPallets1(NoofPallets1);
                    deliveryDTO.setTypeGoodsCategory(ptypeofbox);
                    System.out.println("kjsdfnsdhfhsd--->" + ptypeofbox);
                    deliveryDTO.setProductWidth(productWidth);
                    deliveryDTO.setProductHeight(productHeight);
                    deliveryDTO.setProductLength(productLength);
                    deliveryDTO.setIs_pallet(nonpallet_pallets);
                    deliveryDTO.setNameOfGoods(nameofgood);
//                  deliveryDTO.setProductMeasureType(productMeasure);
                    deliveryDTO.setProductWeight(productWeight);
                    deliveryDTO.setPickupSpecialInst(specialInstruction);
                    deliveryDTO.setPickupLat(pickupLat);
                    deliveryDTO.setPickupLong(pickupLong);
                    deliveryDTO.setDeliveryType(deliveryType);
                    deliveryDTO.setPickupCountryCode(countryCode);
                    bundle.putParcelable("deliveryDTO", deliveryDTO);
                    bundle.putString("delivery_type", deliveryType);
                    System.out.println("Nandha" + deliveryDTO.getTypeGoodsCategory());
                    createOrderExpressDeliveryDrop.setArguments(bundle);

                    Log.d(TAG, "onClick: "+bundle.toString());
                    Log.d("deldto",new Gson().toJson(deliveryDTO));
                    Log.d("deldto",TypeGood.toLowerCase().trim());
                    addFragmentWithoutRemove(R.id.container_main, createOrderExpressDeliveryDrop, "CreateOrderExpressDeliveryDrop");


                }
                break;
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
            case R.id.tv_pickup_location:

                break;
            case R.id.et_pickup_address:

                Intent addressIntent1 = new Intent(context, SelectLocation.class);
                addressIntent1.putExtra("for", "pickup location");
                startActivityForResult(addressIntent1, 1910);

               /* try {
                startActivityForResult(builder.build(getActivity()), REQUEST_PICK_PLACE);
                 } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }*/

//                alertDialog.show();
                    break;
                    case R.id.btn_select_location:
                        pickupLat = alertpickupLat;
                        pickupLong = alertpickupLong;
                        createOrderExpressDeliveryBinding.etPickupAddress.setText(tv_pickup_location.getText().toString());
                        alertDialog.dismiss();
                        break;
                    case R.id.alert_back:
                        alertDialog.dismiss();
                        break;
                    case R.id.et_pickup_date:

                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        datePickerDialog = new DatePickerDialog(context,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        createOrderExpressDeliveryBinding.etPickupDate.setText(Utilities.formatDateShow(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
                                    }
                                }, mYear, mMonth, mDay);
//                calendar.add(Calendar.YEAR, -18);
                        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                        datePickerDialog.show();
                        break;
                    case R.id.et_pickup_time:
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        etTime.setText(selectedHour + ":" + selectedMinute);
                                createOrderExpressDeliveryBinding.etPickupTime.setText(updateTime(selectedHour, selectedMinute));
                            }
                        }, hour, minute, false);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                        break;

        }

    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1910 && resultCode == RESULT_OK) {

                pickupLat = data.getStringExtra("LATITUDE_SEARCH");
                pickupLong = data.getStringExtra("LONGITUDE_SEARCH");
                String address = data.getStringExtra("SearchAddress");

                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong)))
                                .zoom(15)
                                .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null);

                createOrderExpressDeliveryBinding.etPickupAddress.setText(address);



            }

        if (requestCode == REQUEST_PICK_PLACE && resultCode == RESULT_OK) {
//            Place place = PlaceAutocomplete.getPlace(context, data);
            Place place = getPlace(context, data);

            Log.i(getClass().getName(), "Class is >>>>>" + place.getName() + " " + place.getAddress() + "   " + place.getLatLng());
//            alertpickupLat = place.getLatLng().latitude + "";
//            alertpickupLong = place.getLatLng().longitude + "";
            pickupLat = place.getLatLng().latitude + "";
            pickupLong = place.getLatLng().longitude + "";
            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))
                            .zoom(15)
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null);
//            tv_pickup_location.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
            createOrderExpressDeliveryBinding.etPickupAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));

        }
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

    public boolean isValid() {

        if (firstName == null || firstName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_name_or_company), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etFirstName.requestFocus();
            return false;
        }else if (mobile.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_mobile_number), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etMobile.requestFocus();
            return false;
        } else if (!createOrderExpressDeliveryBinding.ccp.isValid()) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_vaild_number), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etMobile.requestFocus();
            return false;
        } else if (!utilities.checkMobile(mobile)) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_mobile_number), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etMobile.requestFocus();
            return false;
        } else if (pickUpAddress == null || pickUpAddress.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_pick_address), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etPickupAddress.requestFocus();
            return false;
        }  else if((pickDate == null || pickDate.equals("") || pickTime == null || pickTime.equals("")) && !deliveryType.equalsIgnoreCase("express")){


                    utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_pickup_date_time), getString(R.string.ok), false);
                    createOrderExpressDeliveryBinding.etPickupDate.requestFocus();
                    return false;

        }else if((pickDate == null || pickDate.equals("")) && deliveryType.equalsIgnoreCase("express")){
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_pickup_date), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etPickupDate.requestFocus();
            return false;
        }else if ((ClassGood == null || ClassGood.equals("")) && !deliveryType.equals("express")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_class_good), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etGoodClass.requestFocus();
            return false;
        }else if (productMeasure.equals("")) {

            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_type_parcel), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.spType1.requestFocus();
            return false;
        }else if(ptypeofbox.equalsIgnoreCase("") && productMeasure.equalsIgnoreCase("Box")){
            utilities.dialogOK(context, getString(R.string.validation_title), "Please, select the box type.", getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.spTypeOfBox.requestFocus();
            return false;
        }else if(ptypeofbox.equalsIgnoreCase("") && productMeasure.equalsIgnoreCase("PalletAndBox")){
            utilities.dialogOK(context, getString(R.string.validation_title), "Please, select the box type.", getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.spTypeOfBox.requestFocus();
            return false;
        }

        else if(nameofgood.equalsIgnoreCase("") && productMeasure.equalsIgnoreCase("Miscellaneous")){
            utilities.dialogOK(context, getString(R.string.validation_title), "Please, enter the name of good.", getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.spTypeOfBox.requestFocus();
            return false;
        }
//        else if (productMeasure.equalsIgnoreCase("PalletAndBox")){
//            utilities.dialogOK(context, getString(R.string.validation_title), "Enter the number of boxes.", getString(R.string.ok), false);
//            createOrderExpressDeliveryBinding.etPalletsCount2.requestFocus();
//        }
        else if (NoofPallets.equals("") && !productMeasure.equalsIgnoreCase("Miscellaneous")) {

            if (productMeasure.equals("Pallet")) {
                utilities.dialogOK(context, getString(R.string.validation_title), "Enter the number of pallets.", getString(R.string.ok), false);
                createOrderExpressDeliveryBinding.etPalletsCount.requestFocus();
                return false;
            }else if (productMeasure.equalsIgnoreCase("Box")){
                    utilities.dialogOK(context, getString(R.string.validation_title), "Enter the number of boxes.", getString(R.string.ok), false);
                    createOrderExpressDeliveryBinding.etPalletsCount.requestFocus();
                    return false;
            }else if (productMeasure.equalsIgnoreCase("Envelop")){
                utilities.dialogOK(context, getString(R.string.validation_title), "Enter the number of envelop.", getString(R.string.ok), false);
                createOrderExpressDeliveryBinding.etPalletsCount.requestFocus();
                return false;
            }

        }else if(!deliveryType.equalsIgnoreCase("Miscellaneous") && (Integer.parseInt(NoofPallets) > 26) && productMeasure.equals("Pallet") ){

                        utilities.dialogOK(context, getString(R.string.validation_title), "Maxmium pallets 26.", getString(R.string.ok), false);
                        createOrderExpressDeliveryBinding.etPalletsCount.requestFocus();
                        return false;


        }else if(!deliveryType.equalsIgnoreCase("Miscellaneous") && (Integer.parseInt(NoofPallets) == 0) && productMeasure.equals("Box") ){
            utilities.dialogOK(context, getString(R.string.validation_title), "Minimum boxes 24.", getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etPalletsCount.requestFocus();
            return false;
        }else if ((productWidth == null || productWidth.equals("")) && productMeasure.equalsIgnoreCase("miscellaneous")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_width), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etGoodWidth.requestFocus();
            return false;
        } else if ((productHeight == null || productHeight.equals("")) && productMeasure.equalsIgnoreCase("miscellaneous")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_height), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etGoodHight.requestFocus();
            return false;
        } else if ((productLength == null || productLength.equals("")) && productMeasure.equalsIgnoreCase("miscellaneous")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_length), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etGoodLength.requestFocus();
            return false;
        } else if (productWeight == null || productWeight.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etPalletsTotalWeight.requestFocus();
            return false;
        } else if ((Integer.parseInt(productWeight) > 30) && productKg.equals("Pound") && deliveryType.equalsIgnoreCase("express")) {

                    utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight_25), getString(R.string.ok), false);
                    createOrderExpressDeliveryBinding.etPalletsTotalWeight.requestFocus();
                    return false;

        } else if ((Integer.parseInt(productWeight) > 13.6078) && (productKg.equals("Kilogram")) && deliveryType.equalsIgnoreCase("express")){
        utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight_11), getString(R.string.ok), false);
        createOrderExpressDeliveryBinding.etPalletsTotalWeight.requestFocus();
        return false;
        }else if (specialInstruction == null || specialInstruction.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_the_special), getString(R.string.ok), false);
            createOrderExpressDeliveryBinding.etPalletsCount.requestFocus();
            return false;
        }

        return true;
    }


    public void selectbox() {


        listAll3.clear();
        ptypeofboxTypeList.clear();


        System.out.println("productMeasureNandha" + productMeasure);

        if (productMeasure.equalsIgnoreCase("miscellaneous")) {

            hashMapbox = new HashMap<>();
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

            }else if (productMeasure.equalsIgnoreCase("multiple")){
                getBoxSubCatogryApi();
            }
        }


        ArrayAdapter adapterbox = new ArrayAdapter<String>(context, R.layout.spinner_text);
        adapterbox.setDropDownViewResource(R.layout.spinner_item_list);
        adapterbox.addAll(listAll3);
        createOrderExpressDeliveryBinding.spTypeOfBox.setAdapter(adapterbox);

        createOrderExpressDeliveryBinding.spTypeOfBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                //  hashMapbox.put(PN_NAME,"");
                                hashMapbox.put(PN_NAME, "");
                                hashMapbox.put(PN_VALUE, getResources().getString(R.string.please_select_box));
                                ptypeofboxTypeList.add(hashMapbox);
//                                listAll3.add(boxes.get(boxsetint).getFull_name());
                                listAll3.add(getResources().getString(R.string.please_select_box));

                                for (int boxsetint = 0;boxsetint<boxes.size();boxsetint++) {
                                    hashMapbox = new HashMap<>();
                                  //  hashMapbox.put(PN_NAME,"");
                                    hashMapbox.put(PN_NAME, boxes.get(boxsetint).getBox_short_code());
                                    hashMapbox.put(PN_VALUE, boxes.get(boxsetint).getFull_name());
                                    ptypeofboxTypeList.add(hashMapbox);
                                    listAll3.add(boxes.get(boxsetint).getFull_name());
                                }

                                ArrayAdapter adapterbox = new ArrayAdapter<String>(context, R.layout.spinner_text);
                                adapterbox.setDropDownViewResource(R.layout.spinner_item_list);
                                adapterbox.addAll(listAll3);
                                createOrderExpressDeliveryBinding.spTypeOfBox.setAdapter(adapterbox);

                          /*      ArrayAdapter adapter1 = new ArrayAdapter<String>(context, R.layout.spinner_text);
                                adapter1.setDropDownViewResource(R.layout.spinner_item_list);
                                adapter1.addAll(listAll3);
                                createOrderExpressDeliveryBinding.spTypeOfBox.setAdapter(adapter1);*/
                                createOrderExpressDeliveryBinding.spTypeOfBox.setVisibility(View.VISIBLE);

                                System.out.println("rescheduleStatus----> 0 "+ rescheduleStatus);
                                System.out.println("rescheduleStatus----> 1 "+  new Gson().toJson(data));
                                System.out.println("rescheduleStatus----> 2 "+  data.getTypeGoodsCategory());
                                System.out.println("rescheduleStatus----> 3 " +listAll3.size());



                                if(rescheduleStatus){



                                    for (int i = 0; i < listAll3.size(); i++) {
                System.out.println("rescheduleStatus----> 4 " +listAll3.size());
//                System.out.println("deliveryDTO.getType_of_truck()" +data.getTypeGoodsCategory());
                                        if (data.getTypeGoodsCategory() != null && data.getTypeGoodsCategory().equalsIgnoreCase(ptypeofboxTypeList.get(i).get(PN_NAME))) {

                                            System.out.println("vehicleCategoryList.size().i" +i);
                                            createOrderExpressDeliveryBinding.spTypeOfBox.setSelection(i);
                                         //   createOrderExpressDeliveryBinding.spTypeOfBox.setEnabled(false);
//                                            System.out.println("vehicleCategoryList.size().i" +i);
                                            break;
                                        } else
                                        {
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

    @Override
    public void onCameraIdle() {
        LatLng latLng = mMap.getCameraPosition().target;
        alertpickupLat = latLng.latitude + "";
        alertpickupLong = latLng.longitude + "";
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

}






