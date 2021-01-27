package com.pickanddrop.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.dto.googleapis.AddressModel;
import com.pickanddrop.dto.googleapis.GeocodingResponse;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.PlaceDetailsJSONParser;
import com.pickanddrop.utils.PlaceJSONParser;
import com.pickanddrop.utils.Utilities;
import com.pickanddrop.utils.WorkaroundMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class SelectLocation extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener {

    private ArrayList<String> reference_id_list = new ArrayList<String>();
    private ArrayList<String> address_list = new ArrayList<String>();
    DownloadTask placeDetailsDownloadTask;
    private static int clicked_index = 0;
    private GoogleMap mGoogleMap;
    PlacesTask placesTask;
    ParserTask parserTask;
    ParserTask placeDetailsParserTask, placesParserTask;
    final int PLACES = 0;
    private CoordinatorLayout mapcon_cl;
    private GeocodingResponse responsenew;
    final int PLACES_DETAILS = 1;
    private static final String TAG = SelectLocation.class.getName();
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet,home_add_ll,work_add_ll;
    private AppCompatEditText et_location;

    private RelativeLayout main_ll;
    private LinearLayout bottom_sheet_con;

    private AppSession appSession;
    private ListView searchAddressListview;
    private double currentLatitude, currentLongitude;
    private String mPICKUP_ADDRESS ="serarch", from_latitude, from_longitude;
    private Button done_btn;
    private AppCompatImageView back_ahwa;

    private List<AddressModel> mSavedList;
    private List<String> resultDataList = new ArrayList<String>();
    private TextView home_add,work_add,home_title,work_title;
    private boolean comeFTime = true;
    private String from;

    private Utilities utilities;
    private ProgressBar add_load_pb;
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();


    public void setLanguage(String language) {
        appSession.setLanguage(language);
        Locale locale1 = new Locale(language);
        Locale.setDefault(locale1);
        Configuration config1 = new Configuration();
        config1.locale = locale1;
        getResources().updateConfiguration(config1, getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_location);
        add_load_pb = findViewById(R.id.add_load_pb);

        appSession = new AppSession(SelectLocation.this);
        utilities = Utilities.getInstance(SelectLocation.this);

        setLanguage("en");

        currentLatitude = Double.parseDouble(String.valueOf(appSession.getLatitude()));
        currentLongitude = Double.parseDouble(String.valueOf(appSession.getLongitude()));



        searchAddressListview = findViewById(R.id.search_address_listview);
        searchAddressListview.setVisibility(View.VISIBLE);
        done_btn = findViewById(R.id.done_btn);
        mapcon_cl = findViewById(R.id. mapcon_cl);
        main_ll = findViewById(R.id.main_ll);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        back_ahwa= findViewById(R.id.back_ahwa);
        et_location = findViewById(R.id.et_location);
        //bottom_sheet_con = findViewById(R.id.bottom_sheet_con);


        from = getIntent().getStringExtra("for");
        et_location.setHint(from);
        et_location.clearFocus();

        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sheetBehavior.setHideable(true);
        sheetBehavior.setPeekHeight(0);
        //sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //commonMethod.showToast( mapcon_cl.getHeight()+"");
        // int inPixels = (int) getResources().getDimension(R.dimen.sel_l_bsph);
        // sheetBehavior.setPeekHeight(inPixels);
        back_ahwa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                //  Toast.makeText(getApplicationContext(), "OnTouchListener", Toast.LENGTH_SHORT).show();
                //  getAddressNew();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                et_location.clearFocus();
                sheetBehavior.setHideable(true);
                sheetBehavior.setPeekHeight(0);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


            }
        });

        et_location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    // Toast.makeText(getApplicationContext(), "Focus Lose", Toast.LENGTH_SHORT).show();
                } else {
                    // Toast.makeText(getApplicationContext(), "Get Focus", Toast.LENGTH_SHORT).show();
                    sheetBehavior.setPeekHeight(main_ll.getHeight());
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                }

            }
        });


        // callback for do something
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Utilities.hideSoftKeyboard(SelectLocation.this);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        // Toast.makeText(getApplicationContext(), "STATE_HIDDEN", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                        // Toast.makeText(getApplicationContext(), "STATE_EXPANDED", Toast.LENGTH_SHORT).show();

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        Utilities.hideSoftKeyboard(SelectLocation.this);
                        //  Toast.makeText(getApplicationContext(), "STATE_COLLAPSED", Toast.LENGTH_SHORT).show();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //  Toast.makeText(getApplicationContext(), "STATE_DRAGGING", Toast.LENGTH_SHORT).show();

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Utilities.hideSoftKeyboard(SelectLocation.this);
                        // Toast.makeText(getApplicationContext(), "STATE_SETTLING", Toast.LENGTH_SHORT).show();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        et_location.clearFocus();
                        sheetBehavior.setHideable(true);
                        int inPixels = (int) getResources().getDimension(R.dimen.sel_l_bsph);
                        //  sheetBehavior.setPeekHeight(bottom_sheet_con.getHeight());
                        sheetBehavior.setPeekHeight(inPixels);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        searchAddressListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();
               Utilities.hideSoftKeyboard(SelectLocation.this);
                clicked_index = arg2;
                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                if (reference_id_list.size() > 0 && reference_id_list.get(arg2) != null) {
                    // Getting url to the Google Places details api
                    String url = getPlaceDetailsUrl(reference_id_list.get(arg2));
                    Log.d("ParserTask url= ", url);

                    // Start downloading Google Place Details
                    // This causes to execute doInBackground() of DownloadTask class
                    placeDetailsDownloadTask.execute(url);

                }


            }
        });


        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPICKUP_ADDRESS.equals("serarch")) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("SearchAddress", mPICKUP_ADDRESS);
                    returnIntent.putExtra("LATITUDE_SEARCH", "" + from_latitude);
                    returnIntent.putExtra("LONGITUDE_SEARCH", "" + from_longitude);
                    setResult(RESULT_OK, returnIntent);
                    finish();


                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.please_wait), Toast.LENGTH_LONG);

                }

            }
        });

        SearchAddress();


    }

    private String getPlaceDetailsUrl(String ref) {

        //String key = "key=" + getString(R.string.google_maps_key);
        String key = "key= AIzaSyBTqu10MkguASs2-bdVNRSbkEhKeudVRTs";

        // reference of place
        String reference = "reference=" + ref;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = reference + "&" + sensor + "&" + key;
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;
        Log.d("Results ", "getPlaceDetailsUrl = " + url);

        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType = 0;

        // Constructor
        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (downloadType) {
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);

                    break;

                case PLACES_DETAILS:
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
            }
        }
    }

    private void SearchAddress() {
        et_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // cancelText.setVisibility(View.VISIBLE);
                // saveAddressList.setVisibility(View.GONE);
                // searchAddressListview.setVisibility(View.VISIBLE);
                if (et_location.hasFocus() && s.length() > 0) {
                   // bottom_sheet_con.setVisibility(View.GONE);
                    placesTask = new PlacesTask();
                    placesTask.execute(s.toString());

                }

               /* handler.removeCallbacks(input_finish_checker);
                if (et_location.hasFocus() && s.length() > 0) {
                    bottom_sheet_con.setVisibility(View.GONE);

                }*/


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                /*if (s.length() <= 0) {
                    bottom_sheet_con.setVisibility(View.VISIBLE);

                }
                if (s.length() > 2) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                    Log.d("input", s.toString());

                }else{
                    add_load_pb.setVisibility(View.GONE);

                }*/
                if (s.length() <= 0) {
                    /*if(mSavedList.isEmpty()) {
                        cancelText.setVisibility(View.GONE);
                        saveAddressList.setVisibility(View.VISIBLE);
                        searchAddressListview.setVisibility(View.GONE);
                    }else {
                        cancelText.setVisibility(View.GONE);
                        saveAddressList.setVisibility(View.VISIBLE);
                        searchAddressListview.setVisibility(View.GONE);
                    }*/
                   // bottom_sheet_con.setVisibility(View.VISIBLE);

                }
            }
        });


    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                if (et_location.hasFocus() && et_location.getText().toString().length() > 0) {
                    //bottom_sheet_con.setVisibility(View.GONE);
                    //PlacesTask();

                }

            }
        }
    };

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception Durl" , e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";
            // Obtain browser key from https://code.google.com/apis/console
            //String key = "key=" + getString(R.string.google_maps_key);
            String key = "key= AIzaSyBTqu10MkguASs2-bdVNRSbkEhKeudVRTs";
            String input = "";
            //String country = "components=country:IN";
            //  String country = "components=country:SD";
            //  String country = "";
            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            String types = "establishment|geocode&location=" + currentLatitude + "," + currentLongitude + "&radius=500po";
            String language = "language=en";
            // Sensor enabled
            //String country = "components=country:AF";
             String country = "";
            // Building the parameters to the web service
            String parameters = input + "&" + country + "&" + types + "&" + key + "&" + language + "&" + country;
            // Output format
            String output = "json";
            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
            Log.d("url ", url);
            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task ", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Creating ParserTask
            parserTask = new ParserTask(PLACES);
            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        JSONObject jObject;
        int parserType = 0;

        ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            List<HashMap<String, String>> places = null;
            try {

                if (jsonData[0] != null) {
                    jObject = new JSONObject(jsonData[0]);
                    switch (parserType) {
                        case PLACES:
                            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                            // Getting the parsed data as a List construct
                            places = placeJsonParser.parse(jObject);
                            break;
                        case PLACES_DETAILS:
                            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                            // Getting the parsed data as a List construct
                            places = placeDetailsJsonParser.parse(jObject);
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG, "ParserTask Exception: " + e.toString());
            }

            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            switch (parserType) {
                case PLACES:
                    if (result != null) {
                        Log.d("Results ", "ParserTask Result Size" + result.size());
                        reference_id_list.clear();
                        address_list.clear();
                        for (int i = 0; i < result.size(); i++) {
                            Log.d("Results ","ParserTask description json =" + result.get(i));
                            address_list.add(result.get(i).get("description"));
                            reference_id_list.add(result.get(i).get("reference"));
                            Log.d("Results ", "ParserTask description name =" + result.get(i).get("description"));
                        }
                        Log.d("Results ", "ParserTask address_list Size" + address_list.size());
                        AddressAdapterNew adapter = new AddressAdapterNew(SelectLocation.this, address_list);
                        searchAddressListview.setAdapter(adapter);


                    }
                    break;
                case PLACES_DETAILS:
                    if (result != null && result.size() > 0 && address_list.size()>0 && address_list.size()>clicked_index) {
                        double lat = Double.parseDouble(result.get(0).get("lat"));
                        double lng = Double.parseDouble(result.get(0).get("lng"));
                        //  double lat = Double.parseDouble(String.format("%.6f", latitude));
                        //  double lng= Double.parseDouble(String.format("%.6f", longitude));

                        Log.d("Results ", "ParserTask latitude=" + lat + " ,longitude=" + lng);

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("SearchAddress", address_list.get(clicked_index));
                            returnIntent.putExtra("LATITUDE_SEARCH", lat + "");
                            returnIntent.putExtra("LONGITUDE_SEARCH", lng + "");
                            setResult(RESULT_OK, returnIntent);
                            finish();


                    }
                    break;
            }
        }
    }

    private class AddressAdapterNew extends ArrayAdapter<String> {
        List<String> objects;
        Activity activity;

        public AddressAdapterNew(Activity activity, List<String> objects) {
            super(activity, R.layout.select_location, objects);
            this.objects = objects;
            this.activity = activity;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.address_item, null);
            }
            TextView locationName = (TextView) convertView.findViewById(R.id.location_name);
            TextView addressTextview = (TextView) convertView.findViewById(R.id.address_textview);
            String[] total_addressStrings = objects.get(position).split(",");
            if (total_addressStrings.length > 0) {
                String first_name = total_addressStrings[0];

                String last_name = "";
                for (int i = 1; i < total_addressStrings.length; i++) {
                    last_name = last_name + total_addressStrings[i];
                }
                locationName.setText(first_name);
                addressTextview.setText(last_name);
            }
            return convertView;
        }
    }


    private void BackgroundGeocodingTaskNew(double clat, double clong) {

        if (utilities.isNetworkAvailable()) {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(SelectLocation.this, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            {
                et_location.setText(getString(R.string.loading));
               /* String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + clat + ","
                        + clong + "&key=" + getString(R.string.google_maps_key);*/


                String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + clat + ","
                        + clong + "&key=AIzaSyBTqu10MkguASs2-bdVNRSbkEhKeudVRTs";


                StringRequest mRequest2 = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("myapi ", response);
                        try{
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            if (response != null) {
                                Gson gson = new Gson();
                                responsenew = gson.fromJson(response, GeocodingResponse.class);
                                if (responsenew.getStatus().equals("OK") && responsenew.getResults() != null
                                        && responsenew.getResults().size() > 0) {
                                    if (responsenew.getResults().size() > 0 && !responsenew.getResults().get(0).getFormatted_address().isEmpty()) {
                                        mPICKUP_ADDRESS = responsenew.getResults().get(0).getFormatted_address();
                                        et_location.setText(mPICKUP_ADDRESS);
                                        Log.d("padd ", mPICKUP_ADDRESS);


                                    } else {
                                        // CommonMethods.dismissDialog();
                                        // Toast.makeText(getApplicationContext(), getString(R.string.some_went_wrong_try_again), Toast.LENGTH_SHORT).show();


                                    }
                                } else {
                                    // CommonMethods.dismissDialog();
                                    // Toast.makeText(getApplicationContext(), getString(R.string.some_went_wrong_try_again), Toast.LENGTH_SHORT).show();


                                }

                            } else {
                                // CommonMethods.dismissDialog();
                                // Toast.makeText(getApplicationContext(), getString(R.string.some_went_wrong_try_again), Toast.LENGTH_SHORT).show();


                            }

                        }catch (Exception e){
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();

                        utilities.dialogOK(getApplicationContext(), "", error.getMessage().toString(),getString(R.string.ok), false);



                    }
                });

                mRequest2.setRetryPolicy(new DefaultRetryPolicy(
                        2500,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(getApplicationContext()).add(mRequest2);


            }


        } else {

            utilities.dialogOK(getApplicationContext(), "", getResources().getString(R.string.network_error),getString(R.string.ok), false);


        }

    }



    private void getAddressNew() {
        if (utilities.isNetworkAvailable()) {
            // if (getActivity() != null) {
            add_load_pb.setVisibility(View.GONE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    VisibleRegion visibleRegion = mGoogleMap.getProjection().getVisibleRegion();
                    Point x1 = mGoogleMap.getProjection().toScreenLocation(visibleRegion.farRight);
                    Point y = mGoogleMap.getProjection().toScreenLocation(visibleRegion.nearLeft);
                    Point centerPoint = new Point(x1.x / 2, y.y / 2);
                    LatLng centerFromPoint = mGoogleMap.getProjection().fromScreenLocation(centerPoint);
                    double lat = centerFromPoint.latitude;
                    double lon = centerFromPoint.longitude;


                    // double lat = Double.parseDouble(String.format("%.6f", latitude));
                    // double lon = Double.parseDouble(String.format("%.6f", longitude));

                    if ((lat == currentLatitude && lon == currentLongitude)) {
                        // no need to update

                    } else {
                        if (lat != 0.0 && lon != 0.0) {
                            currentLatitude = lat;
                            currentLongitude = lon;
                            from_latitude = String.valueOf(lat);
                            from_longitude = String.valueOf(lon);

                            BackgroundGeocodingTaskNew(currentLatitude, currentLongitude);

                        }
                    }
                }
            });
            // }//


        } else {
            utilities.dialogOK(getApplicationContext(), "", getResources().getString(R.string.network_error),getString(R.string.ok), false);


        }

    }

    @Override
    public void onCameraIdle() {
        if(!comeFTime) {
            getAddressNew();
        }
        comeFTime = false;

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


       // if (currentLatitude != 0.0 && currentLongitude != 0.0)
        {
                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

        }


        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraMoveCanceledListener(this);

    }


}
