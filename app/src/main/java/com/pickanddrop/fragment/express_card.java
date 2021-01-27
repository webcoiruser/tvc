package com.pickanddrop.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.databinding.ExpressesCardBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.CustomSpinnerForAll;
import com.pickanddrop.utils.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class express_card extends BaseFragment implements AppConstants, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private ExpressesCardBinding expressesCardBinding;
    private String countryCode = "", deliveryType = "", pickupLat = "", pickupLong = "", companyName = "", firstName = "", lastName = "", mobile = "", pickUpAddress = "", itemDescription = "", itemQuantity = "", deliDate = "", deliTime = "", specialInstruction = ""
            ,pickDate = "", pickTime = "", pickupLiftGate = "",nonpallet_pallets = "", ClassGood = "", TypeGood = "", NoofPallets = "", productWidth = "", productHeight = "", productLength = "", productMeasure = "", productWeight;
    private PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    private static final int REQUEST_PICK_PLACE = 2345;
    private int mYear, mMonth, mDay;
    private Calendar c;
    private DatePickerDialog datePickerDialog;
    private String TAG = CreateOrderOne.class.getName();
    private boolean rescheduleStatus = false;
    private DeliveryDTO.Data data;
    private ArrayList<HashMap<String, String>> measureTypeList;
    private CustomSpinnerForAll customSpinnerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("delivery_type")) {
            deliveryType = getArguments().getString("delivery_type");
            Log.e(TAG, deliveryType);
        }

        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            data = getArguments().getParcelable("deliveryDTO");
            rescheduleStatus = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        expressesCardBinding = DataBindingUtil.inflate(inflater, R.layout.expresses_card, container, false);
        return expressesCardBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        //Toast.makeText(context,"Express_card",Toast.LENGTH_LONG).show();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();


        if (rescheduleStatus) {
            setValues();
        }
    }



    private void setValues() {
        expressesCardBinding.etFirstName.setText(data.getPickupFirstName());
        expressesCardBinding.etLastName.setText(data.getPickupLastName());
        expressesCardBinding.etMobile.setText(data.getPickupMobNumber());
        expressesCardBinding.etPickupAddress.setText(data.getPickupaddress());
        expressesCardBinding.etPickSpecialInst.setText(data.getPickupSpecialInst());
        // pickupLiftGate = expressesCardBinding.etPickSpecialInst.getText().toString();
        expressesCardBinding.etPickupDate.setText(data.getPickupDate());
        expressesCardBinding.etPickupTime.setText(data.getDeliveryTime());
     //   expressesCardBinding.etGoodClass.setSe(data.getClassGoods());
      //  expressesCardBinding.etGoodType.setText(data.getTypeGoods());
        expressesCardBinding.etPalletsCount.setText(data.getNoOfPallets());
        expressesCardBinding.etGoodWidth.setText(data.getProductWidth());
        expressesCardBinding.etGoodHight.setText(data.getProductHeight());
        expressesCardBinding.etGoodLength.setText(data.getProductLength());
        expressesCardBinding.etPalletsTotalWeight.setText(data.getProductWeight());
        if(data.getPickupLiftGate().equals("no")){
            expressesCardBinding.rbInsidePickup.setChecked(true);
        }else if(data.getPickupLiftGate().equals("yes")){
            expressesCardBinding.rbLiftGate.setChecked(true);
        }
        if(data.getIs_pallet().equals("no")){
            expressesCardBinding.rbNonPallets.setChecked(true);
        }else if(data.getPickupLiftGate().equals("yes")){
            expressesCardBinding.rbPallets.setChecked(true);
        }


        expressesCardBinding.ccp.setCountryForPhoneCode(Integer.parseInt(data.getPickupCountryCode()));

        deliveryType = data.getDeliveryType();
        pickupLat = data.getPickupLat();
        pickupLong = data.getPickupLong();

        expressesCardBinding.etPickupAddress.setEnabled(false);
    }

    private void initToolBar() {

    }

    private void initView() {
//        expressesCardBinding.ccp.registerPhoneNumberTextView(expressesCardBinding.etMobile);
        c = Calendar.getInstance();
        measureTypeList = new ArrayList<>();
        expressesCardBinding.ivBack.setOnClickListener(this);
        expressesCardBinding.etPickupAddress.setOnClickListener(this);
        expressesCardBinding.btnNext.setOnClickListener(this);
        expressesCardBinding.etPickupDate.setOnClickListener(this);
        expressesCardBinding.etPickupTime.setOnClickListener(this);

        expressesCardBinding.ccp.registerPhoneNumberTextView(expressesCardBinding.etMobile);
        expressesCardBinding.ccp.setDefaultCountryUsingNameCode("US");
        expressesCardBinding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tt = s.toString();
                if(expressesCardBinding.ccp.isValid()) {
                    expressesCardBinding.llMobileNumberError.setVisibility(View.GONE);
                } else {
                    expressesCardBinding.llMobileNumberError.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final ArrayList<String> listAll1 = new ArrayList<String>();
        HashMap<String, String> hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE,getResources().getString(R.string.class_of_goods) );
        measureTypeList.add(hashMapClass);
        listAll1.add(getResources().getString(R.string.class_of_goods));

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 50");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 50");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 55");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 55");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 60");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 60");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 65");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 50");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 70");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 70");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 77.5");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 77.5");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 85");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 85");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 92.5");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 92.5");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 100");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 50");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 110");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 110");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 125");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 125");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 150");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 50");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 175");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 175");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 200");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 200");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 250");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 250");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 300");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 300");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 400");
        measureTypeList.add(hashMapClass);
        listAll1.add("Class 400");

        hashMapClass = new HashMap<>();
        hashMapClass.put(PN_NAME, "");
        hashMapClass.put(PN_VALUE, "Class 500");
        listAll1.add("Class 500");

        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.spinner_text);
        adapter.setDropDownViewResource(R.layout.spinner_item_list);
        adapter.addAll(listAll1);
        expressesCardBinding.etGoodClass.setAdapter(adapter);


        expressesCardBinding.etGoodClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    productMeasure = measureTypeList.get(i).get(PN_VALUE);
                } else {
                    ((TextView) view).setTextSize(16);
                    ((TextView) view).setTextColor(
                            getResources().getColorStateList(R.color.text_hint)
                    );
                    productMeasure = "";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        expressesCardBinding.rbInsidePickup.setOnClickListener(this);
        expressesCardBinding.rbLiftGate.setOnClickListener(this);
        expressesCardBinding.rgPallets.setOnCheckedChangeListener(this);
    }




       /*
        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.feet));
        measureTypeList.add(hashMap1);
        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.cm));
        measureTypeList.add(hashMap1);

        customSpinnerAdapter = new CustomSpinnerForAll(context, R.layout.spinner_textview, measureTypeList, getResources().getColor(R.color.black_color), getResources().getColor(R.color.light_black), getResources().getColor(R.color.transparent));
        expressesCardBinding.spType.setAdapter(customSpinnerAdapter);

        expressesCardBinding.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                if(productMeasure != null & !productMeasure.equals(""))
                    productMeasure = measureTypeList.get(0).get(PN_VALUE);
            }
        });*/
        /*Start*/


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                UserHome userHome = new UserHome();
                Utilities.hideKeyboard(expressesCardBinding.btnNext);
                countryCode = expressesCardBinding.ccp.getSelectedCountryCode();
                Log.e(TAG, ">>>>>>>>>>>>>>"+countryCode);

                firstName = expressesCardBinding.etFirstName.getText().toString();
                lastName = expressesCardBinding.etLastName.getText().toString();
                mobile = expressesCardBinding.etMobile.getText().toString();
                pickUpAddress = expressesCardBinding.etPickupAddress.getText().toString();
                specialInstruction = expressesCardBinding.etPickSpecialInst.getText().toString();
//                pickupLiftGate = expressesCardBinding.etPickSpecialInst.getText().toString();
                pickDate = expressesCardBinding.etPickupDate.getText().toString();
                pickTime= expressesCardBinding.etPickupTime.getText().toString();
                ClassGood= expressesCardBinding.etGoodClass.getSelectedItem().toString();
        //        TypeGood= expressesCardBinding.etGoodType.getText().toString();
                NoofPallets= expressesCardBinding.etPalletsCount.getText().toString();
                productWidth= expressesCardBinding.etGoodWidth.getText().toString();
                productHeight= expressesCardBinding.etGoodHight.getText().toString();
                productLength= expressesCardBinding.etGoodLength.getText().toString();
//                productMeasure= expressesCardBinding.et.getText().toString();
                productWeight= expressesCardBinding.etPalletsTotalWeight.getText().toString();

                if (isValid()) {
                    Express_order_drop express_order_drop = new Express_order_drop();
                    DeliveryDTO.Data deliveryDTO = null;
                    Bundle bundle = new Bundle();
                    if (rescheduleStatus) {
                        deliveryDTO = data;
                        bundle.putString("rescheduleStatus", "rescheduleStatus");
                    } else {
                        deliveryDTO = new DeliveryDTO().new Data();
                    }

                    deliveryDTO.setPickupFirstName(firstName);
                    deliveryDTO.setPickupLastName(lastName);
                    deliveryDTO.setPickupMobNumber(mobile);
                    deliveryDTO.setPickupaddress(pickUpAddress);
                    deliveryDTO.setPickupLiftGate(pickupLiftGate);
                    deliveryDTO.setPickupDate(pickDate);
                    deliveryDTO.setDeliveryTime(pickTime);
                    deliveryDTO.setClassGoods(ClassGood);
                    deliveryDTO.setTypeGoods(TypeGood);
                    deliveryDTO.setNoOfPallets(NoofPallets);
                    deliveryDTO.setProductWidth(productWidth);
                    deliveryDTO.setProductHeight(productHeight);
                    deliveryDTO.setProductLength(productLength);
                    deliveryDTO.setIs_pallet(nonpallet_pallets);
//                    deliveryDTO.setProductMeasureType(productMeasure);
                    deliveryDTO.setProductWeight(productWeight);
                    deliveryDTO.setPickupSpecialInst(specialInstruction);
                    deliveryDTO.setPickupLat(pickupLat);
                    deliveryDTO.setPickupLong(pickupLong);
                    deliveryDTO.setDeliveryType(deliveryType);
                    deliveryDTO.setPickupCountryCode(countryCode);
//                    userHome.handler.removeCallbacks(userHome.myRunnable);
                    bundle.putParcelable("deliveryDTO", deliveryDTO);
                    bundle.putString("delivery_type",deliveryType);
                    express_order_drop.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, express_order_drop, "CreateOrderExpressDeliveryDrop");
                }
                break;
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
            case R.id.et_pickup_address:
                try {
                    startActivityForResult(builder.build(getActivity()), REQUEST_PICK_PLACE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
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
                                expressesCardBinding.etPickupDate.setText(Utilities.formatDateShow(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
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
                        expressesCardBinding.etPickupTime.setText(updateTime(selectedHour, selectedMinute));
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
        if (requestCode == REQUEST_PICK_PLACE && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(context, data);

            Log.i(getClass().getName(), "Class is >>>>>" + place.getName() + " " + place.getAddress() + "   " + place.getLatLng());
            pickupLat = place.getLatLng().latitude + "";
            pickupLong = place.getLatLng().longitude + "";
            expressesCardBinding.etPickupAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
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

            return address +" "+city;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isValid() {
        if (firstName == null || firstName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_first_name), getString(R.string.ok), false);
            expressesCardBinding.etFirstName.requestFocus();
            return false;
        } else if (lastName == null || lastName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_last_name), getString(R.string.ok), false);
            expressesCardBinding.etLastName.requestFocus();
            return false;
        } else if (mobile.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_mobile_number), getString(R.string.ok), false);
            expressesCardBinding.etMobile.requestFocus();
            return false;
        } else if(!expressesCardBinding.ccp.isValid()) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_vaild_number), getString(R.string.ok), false);
            expressesCardBinding.etMobile.requestFocus();
            return false;
        } else if (!utilities.checkMobile(mobile)) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_mobile_number), getString(R.string.ok), false);
            expressesCardBinding.etMobile.requestFocus();
            return false;
        } else if (pickUpAddress == null || pickUpAddress.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_pick_address), getString(R.string.ok), false);
            expressesCardBinding.etPickupAddress.requestFocus();
            return false;
        } else if (pickDate == null) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_deli_date), getString(R.string.ok), false);
            expressesCardBinding.etPickupDate.requestFocus();
            return false;
        } else if (ClassGood == null || ClassGood.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_class_good), getString(R.string.ok), false);
            expressesCardBinding.etGoodClass.requestFocus();
            return false;
        }/* else if (TypeGood == null || TypeGood.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_type_good), getString(R.string.ok), false);
            expressesCardBinding.etGoodType.requestFocus();
            return false;
        }*/else if (NoofPallets == null || NoofPallets.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_no_of_pallets), getString(R.string.ok), false);
            expressesCardBinding.etPalletsCount.requestFocus();
            return false;
        }else if (Integer.parseInt(NoofPallets) > 26) {
            utilities.dialogOK(context, getString(R.string.validation_title), "Maxmium pallets 26.", getString(R.string.ok), false);
            expressesCardBinding.etPalletsCount.requestFocus();
            return false;
        }  else if (specialInstruction == null || specialInstruction.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_the_special), getString(R.string.ok), false);
            expressesCardBinding.etPalletsCount.requestFocus();
            return false;
        }

        /*else if (productWidth == null || productWidth.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_width), getString(R.string.ok), false);
            expressesCardBinding.etGoodWidth.requestFocus();
            return false;
        }else if (productHeight == null || productHeight.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_height), getString(R.string.ok), false);
            expressesCardBinding.etGoodHight.requestFocus();
            return false;
        }else if (productLength == null || productLength.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_length), getString(R.string.ok), false);
            expressesCardBinding.etGoodLength.requestFocus();
            return false;
        }*//*else if (productWeight == null || productWeight.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight), getString(R.string.ok), false);
            expressesCardBinding.etPalletsTotalWeight.requestFocus();
            return false;
        }*//*else if (pickupLiftGate == null || pickupLiftGate.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_pickup_lifegate_date), getString(R.string.ok), false);
            expressesCardBinding.rgLiftGate.requestFocus();
            return false;
        }*//*else if (nonpallet_pallets == null || nonpallet_pallets.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_pallets), getString(R.string.ok), false);
            expressesCardBinding.rgPallets.requestFocus();
            return false;
        }*/
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_inside_pickup:
                pickupLiftGate = "no";
                break;
            case R.id.rb_lift_gate:
                pickupLiftGate = "yes";
                break;
            case R.id.rb_pallets:
                nonpallet_pallets = "yes";
                break;
            case R.id.rb_non_pallets:
                nonpallet_pallets = "no";
                break;
        }
    }

/*    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(isChecked) {
            case R.id.rb_inside_pickup:
                if (checked)
                    pickupLiftGate = "no";
                break;
            case R.id.rb_lift_gate:
                if (checked)
                    pickupLiftGate = "yes";
                break;
    }*/

/*    public void onCheckboxClicked(int checkedId ) {
        // Is the view now checked?
        boolean checked = ((CheckBox) checkedId).isChecked();

        // Check which checkbox was clicked
        switch(checkedId) {
            case R.id.rb_inside_pickup:
                if (checked)
                    pickupLiftGate = "no";
                break;
            case R.id.rb_lift_gate:
                if (checked)
                    pickupLiftGate = "yes";
                break;
        }
    }*/

   /* @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean checkedId) {
        switch (checkedId){
            case R.id.rb_inside_pickup:
                pickupLiftGate = "no";
                break;
            case R.id.rb_lift_gate:
                pickupLiftGate = "yes";
                break;
            case R.id.rb_pallets:
                nonpallet_pallets = "yes";
                break;
            case R.id.rb_non_pallets:
                nonpallet_pallets = "no";
                break;
        }

    }*/
}
