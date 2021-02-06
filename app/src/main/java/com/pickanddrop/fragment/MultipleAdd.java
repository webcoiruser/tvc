package com.pickanddrop.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.adapter.MultipleDropAdapter;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.CreateOrderExpressDeliveryBinding;
import com.pickanddrop.databinding.CreateOrderExpressDeliveryDropBinding;
import com.pickanddrop.databinding.RvMultiListBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.DeliveryMultipleDTO;
import com.pickanddrop.dto.DeliverySendMultipleDataDTO;
import com.pickanddrop.dto.MultipleDTO;
import com.pickanddrop.dto.Objectpojo;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.dto.PricesDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.DataParser;
import com.pickanddrop.utils.DatabaseHandler;
import com.pickanddrop.utils.MyConstant;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.OnItemClickListener;
import com.pickanddrop.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultipleAdd extends BaseFragment implements View.OnClickListener, AppConstants, FragmentManager.OnBackStackChangedListener {
    private RvMultiListBinding multiListBinding;
    private MultipleDropAdapter multipleDropAdapter;
    private String TAG = MultipleAdd.class.getName();
    private Context context;
    public Handler handler;
    private AppSession appSession;
    private Utilities utilities;
    private DeliveryDTO.Data deliveryDTO;
    private DeliveryDTO myDTO;
    private CreateOrderExpressDeliveryDropBinding createOrderExpressDeliveryDropBinding;
    private CreateOrderExpressDeliveryBinding createOrderExpressDeliveryBinding;
    private String status = "";
    private LinearLayoutManager linearLayoutManager;
//    private ArrayList<MultipleDTO> deliveryDTOArrayList = new ArrayList<>();
    Objectpojo onjectPojo = new Objectpojo();
    public static  String totalPrice = "";
    MultipleDTO multipleDTO = new MultipleDTO();
    private Double totalDeliveryCost = 0.0;
    LinkedList<LatLng> markerPoints;
    LinkedList<LatLng> diatances;
    LinkedList<Double> adddistance;
    LatLng dest;
    String response;
    String parsedDistance;
    DatabaseHandler db;
    private String deliveryId = "";
    private boolean multiple_type = false,history = false;
    ArrayList<MultipleDTO> contacts = new ArrayList<>();
    private int mYear = 0 , mMonth, mDay;
    private Calendar c;
    private DatePickerDialog datePickerDialog;
    Boolean deliverstatus = false,deliverstatuscurrent = false,payment_status = false;
    ArrayList<MultipleDTO> multipleDTOArrayList = new ArrayList<>();

    ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
    private String trackRoute = "trackallroute";

    public void onMapReadydata() {

        multipleDTOArrayList = new ArrayList<>();

        if(multiple_type){
            multipleDTOArrayList = contacts;
        }else {
            multipleDTOArrayList = db.getAllContacts();
        }
        markerPoints =  new LinkedList<>();
      System.out.println("143333---> "+ new Gson().toJson(multipleDTOArrayList));


        LatLng source = new LatLng(Double.parseDouble(deliveryDTO.getPickupLat()), Double.parseDouble(deliveryDTO.getPickupLong()));

//        System.out.println("3---> "+ deliveryDTO.getPickupLat() + deliveryDTO.getPickupLong());

        for(int i= 0;i< multipleDTOArrayList.size();i++){
            LatLng pick1 = new LatLng(Double.parseDouble(multipleDTOArrayList.get(i).getDropoffLat()), Double.parseDouble(multipleDTOArrayList.get(i).getDropoffLong()));
            markerPoints.add(pick1);
            System.out.println("i---> "+ i);
        }

        System.out.println("3---> "+ "test");
//        LatLng pick1 = new LatLng(Double.parseDouble("13.0827"), Double.parseDouble("80.2707"));
//        LatLng pick2 = new LatLng(Double.parseDouble("15.1394"), Double.parseDouble("76.9214"));
//        LatLng pick3 = new LatLng(Double.parseDouble("17.3850"), Double.parseDouble("78.4867"));
//        LatLng pick4 = new LatLng(Double.parseDouble("22.7196"), Double.parseDouble("75.8577"));
//        //markerPoints.add(source);
//        markerPoints.add(pick2);
//        markerPoints.add(pick1);
//        markerPoints.add(pick4);
//        markerPoints.add(pick3);
        adddistance = new LinkedList<>();
        //   adddistance.add(0.0);
        if (markerPoints.size() >= 1) {
            for (int i = 0; i < markerPoints.size(); i++) {
                LatLng point = (LatLng) markerPoints.get(i);
                System.out.println("destination--->>>>>>>" + "destination=" + point.latitude + "," + point.longitude);
                getDistance(source.latitude, source.longitude, point.latitude, point.longitude);
            }

//            System.out.println("Arraylist data markerPoints>>>>>>>>>>" + markerPoints);
//            System.out.println("Arraylist data >>>>>>>>>>" + adddistance);
            LatLng vaules = null;
            diatances = new LinkedList<>();
            diatances = markerPoints;
            for (int i = 0; i < adddistance.size(); i++) {
                for (int j = i + 1; j < adddistance.size(); j++) {
//                    System.out.println("getValueofiiiiiiiiiii-->" + adddistance.get(i));
//                    System.out.println("getValueofjjjjjjjjjjj-->" + adddistance.get(j));
                    if (adddistance.get(i) > adddistance.get(j)) {
                        vaules = diatances.get(i);
                        diatances.set(i, diatances.get(j));
                        diatances.set(j, vaules);
                    }
                }
            }

            System.out.println("AddDistance Dataaa >>>>>>>>>>" + diatances);

            dest = diatances.get(diatances.size() - 1);
            String url = getDirectionsUrl(source, dest);
            DownloadTask downloadTask = new DownloadTask();

             downloadTask.execute(url);
            //Location loc1 = new Location("");
            //loc1.setLatitude(Double.parseDouble(deliveryDTO.getDeliveryDistance()));
            String  myDist = deliveryDTO.getDeliveryDistance();
            //myDis = myDis*0.62137;

            Location loc1 = new Location("");
            loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
            loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

            Location loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(deliveryDTO.getDropoffLat()));
            loc2.setLongitude(Double.parseDouble(deliveryDTO.getDropoffLong()));

           //distance(loc1.getLatitude(),loc1.getLongitude(),loc2.getLatitude(),loc2.getLongitude());
            //float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;

            if(deliverstatuscurrent ) {
                multiListBinding.tvTotalcost.setText("$ "+deliveryDTO.getDeliveryCost());
                multiListBinding.tvTotaldistance.setText(deliveryDTO.getTotal_distance());
            }else if(history){
                multiListBinding.tvTotalcost.setText("$ "+deliveryDTO.getDeliveryCost());
                multiListBinding.tvTotaldistance.setText(deliveryDTO.getTotal_distance());

            }else {
                callOrderMultipleBookApi();
            }
            multiListBinding.llDistanceValue.setVisibility(View.VISIBLE);


//            double distb =
                    //CalculationByDistance(source,dest);

//            Log.d(TAG, "onMapReadydata: "+distb+new Gson().toJson(deliveryDTO));


//            double disinmiles = Double.parseDouble(deliveryDTO.getDeliveryDistance());
//            //disinmiles = disinmiles*0.62137;
//            //deliveryBookExpressDeliveryBinding.etDistance.setText(disinmiles + " " + getString(R.string.mile));
//            deliveryBookExpressDeliveryBinding.etDistance.setText(String.format("%.3f",Double.parseDouble(String.valueOf(disinmiles))) + " " + getString(R.string.mile));




        }else {
               // multiListBinding.tvTotalcost.setText(getString(R.string.us_dollar) + " " + String.valueOf(Double.valueOf(totalPrice)));
            multiListBinding.tvTotalcost.setText(getString(R.string.us_dollar) + " " + deliveryDTO.getDeliveryCost());

                //multiListBinding.tvTotalcost.setText(getString(R.string.us_dollar) + " " + "0");

           // multiListBinding.tvTotaldistance.setText("0" + " km");
            multiListBinding.tvTotaldistance.setText(deliveryDTO.getTotal_distance());
            multiListBinding.llDistanceValue.setVisibility(View.VISIBLE);
        }

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        Log.d(TAG, "distance: %2f "+dist);
        deliveryDTO.setDeliveryDistance(""+dist);

        if(deliverstatuscurrent ) {
            multiListBinding.tvTotalcost.setText("$ "+deliveryDTO.getDeliveryCost());
            multiListBinding.tvTotaldistance.setText(deliveryDTO.getTotal_distance());
        }else if(history){
            multiListBinding.tvTotalcost.setText("$ "+deliveryDTO.getDeliveryCost());
            multiListBinding.tvTotaldistance.setText(deliveryDTO.getTotal_distance());

        }else {
            callOrderMultipleBookApi();
        }
//                        multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(MyConstant.DISTANCE)) + " Mile");
//            multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getDeliveryDistance())) + " Mile");

//        multiListBinding.tvTotaldistance.setText(valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);
       // multiListBinding.tvTotaldistance.setText(String.format("%.2f",dist));

        multiListBinding.llDistanceValue.setVisibility(View.VISIBLE);

        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }//

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = (int) 3958.8; //6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

            //deliveryDTO.setDeliveryDistance(MyConstant.DISTANCE);
            deliveryDTO.setDeliveryDistance(""+kmInDec);

            if(deliverstatuscurrent ) {
                multiListBinding.tvTotalcost.setText("$ "+deliveryDTO.getDeliveryCost());
                multiListBinding.tvTotaldistance.setText(deliveryDTO.getTotal_distance());
            }else if(history){
                multiListBinding.tvTotalcost.setText("$ "+deliveryDTO.getDeliveryCost());
                multiListBinding.tvTotaldistance.setText(deliveryDTO.getTotal_distance());
            }else {
                callOrderMultipleBookApi();
            }
//                        multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(MyConstant.DISTANCE)) + " Mile");
//            multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getDeliveryDistance())) + " Mile");

//        multiListBinding.tvTotaldistance.setText(valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);
        multiListBinding.tvTotaldistance.setText(" " + kmInDec);

        multiListBinding.llDistanceValue.setVisibility(View.VISIBLE);


        return Radius * c * 1000;
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";

        String waypoints = "";
        for(int i=0;i<markerPoints.size();i++){

            System.out.println("dataaaa"+i);


            LatLng point  = (LatLng) markerPoints.get(i);

            System.out.println("lattt--->"+point.latitude+"---"+"langg---->"+point.longitude);

            if(i==0)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";

        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);

        return url;
    }

    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2){

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 +  "&mode=driving&key=AIzaSyC8zLVIAUFLybEitHDR2JviLTXJy7JVeZc");

                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance=distance.getString("text");
                    String strNew = parsedDistance.replace("km", "").trim();
                    if(strNew.contains(",")){
                        strNew = strNew.replace(",$", "").replace(",","");
                    }
                    if(strNew.equalsIgnoreCase("1 m")){
                        System.out.println("jkf");
                    }else {
                        if(strNew.contains("m")){
                        strNew = strNew.replace( "m","").trim();
                        strNew = String.valueOf(Double.parseDouble(strNew)/1000);
                        }
                        Double val = Double.parseDouble(strNew);
                        System.out.println("Distance of ----->" + parsedDistance);
                        adddistance.add(val);
                    }


                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return parsedDistance;

    }

    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downlo", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onBackStackChanged() {
        if(multiple_type){
           System.out.println("multiple_type");
        }else {
            System.out.println("hjcs");
        }
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
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

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressDialog = ProgressDialog.show(context, null, null);
//            mProgressDialog.setContentView(R.layout.progress_loader);
//            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            mProgressDialog.setCancelable(false);
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                //Log.d("ParserTask",jsonData[0].toString());
               // DataParser parser = new DataParser();
               // Log.d("ParserTask", parser.toString());

                // Starts parsing data
               // routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
              //  Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;

        }

        // Executes in UI thread, after the parsing process
        @Override
      protected void onPostExecute(List<List<HashMap<String, String>>> result) {


            callOrderMultipleBookApi();
         /*  Handler refresh = new Handler(Looper.getMainLooper());
            refresh.post(new Runnable() {
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;
                String distance = "";
                String duration = "";

                @Override
                public void run() {

                    // Traversing through all the routes
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();


                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            if(j==0){    // Get distance from the list
                                distance = (String)point.get("distance");
                                continue;
                            }else if(j==1){ // Get duration from the list
                                duration = (String)point.get("duration");
                                continue;
                            }

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        }
                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(5);
                        lineOptions.color(Color.RED);
                    }

                    // Drawing polyline in the Google Map for the i-th route
                    if(lineOptions != null) {

                        deliveryDTO.setDeliveryDistance(MyConstant.DISTANCE);

                        if(deliverstatuscurrent ) {
                            multiListBinding.tvTotalcost.setText("$ "+deliveryDTO.getDeliveryCost());
                            multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getTotal_distance()))+ " Mile");
                            Log.e("error","imranget"+deliveryDTO.getDeliveryCost());
                        }else if(history){
                            multiListBinding.tvTotalcost.setText("$ "+deliveryDTO.getDeliveryCost());
                            multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getTotal_distance()))+ " Mile");

                        }else {
                            callOrderMultipleBookApi();
                        }
//                        multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(MyConstant.DISTANCE)) + " Mile");
                      // multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getDeliveryDistance())) + " Mile");


                        multiListBinding.llDistanceValue.setVisibility(View.VISIBLE);
                    }
                    else {
                        Log.d("onPostExecute","without Polylines drawn");
                    }

                }
            });*/


        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            deliveryDTO = getArguments().getParcelable("deliveryDTO");
            //myDTO = getArguments().getParcelable("deliveryDTO");
            Log.d(TAG, "onCreate data: "+new Gson().toJson(deliveryDTO));
            Log.d(TAG, "onCreate: "+deliveryDTO.getTypeGoods());

        }

        if (getArguments() != null && getArguments().containsKey("delivery")) {
            deliveryId = getArguments().getString("delivery");
            Log.e(TAG, deliveryId);
            deliverstatuscurrent = true;


            if (getArguments().containsKey("multiple_data")){
                multiple_type = true;
            }

            if(getArguments().containsKey("history")){
                history = true;
            }

            if (getArguments().containsKey("payment")) {
                payment_status = true;
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        multiListBinding = DataBindingUtil.inflate(inflater, R.layout.rv_multi_list, container, false);
        return multiListBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("from_payment_request")) {
            multiListBinding.toolbar.setVisibility(View.GONE);

        }


        callOnedata();
        //callUserDeliveryApi();


    }

    private void callOnedata(){

        context = getActivity();
        c = Calendar.getInstance();

        appSession = new AppSession(context);

        utilities = Utilities.getInstance(context);
        markerPoints = new LinkedList<>();
        contacts = new ArrayList<MultipleDTO>();
        totalPrice = "0";

        if(multiple_type){
            if (history) {
                multiListBinding.btnRouteing.setVisibility(View.GONE);
                multiListBinding.btnSummit.setVisibility(View.GONE);
                multiListBinding.btnSignup.setVisibility(View.GONE);
            }

            if(!payment_status){
                multiListBinding.btnSummit.setText("CANCEL");
                multiListBinding.btnSignup.setText("RESCHEDULE");
            }else {
                multiListBinding.btnSummit.setText("CANCEL");
                multiListBinding.btnSignup.setText("PAYMENT");
            }




            multiListBinding.etPickupTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        etTime.setText(selectedHour + ":" + selectedMinute);
                            multiListBinding.etPickupTime.setText(updateTime(selectedHour, selectedMinute));
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

            multiListBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
            multiListBinding.etPickupTime.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
            multiListBinding.etPickupDate.setEnabled(false);
            multiListBinding.etPickupTime.setEnabled(false);
//            multiListBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
            if (!utilities.isNetworkAvailable())
                utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
            else {
                final ProgressDialog mProgressDialog;
                mProgressDialog = ProgressDialog.show(context, null, null);
                mProgressDialog.setContentView(R.layout.progress_loader);
                mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mProgressDialog.setCancelable(false);
                Map<String, String> map = new HashMap<>();
                map.put("delivery_id", deliveryId);
                Log.d(TAG, "callOnedata: "+new Gson().toJson(deliveryId));
                map.put(PN_APP_TOKEN, APP_TOKEN);
                APIInterface apiInterface = APIClient.getClient();
                Call<DeliveryDTO> call = apiInterface.callDeliveryDetailsApi(map);
                call.enqueue(new Callback<DeliveryDTO>() {
                    @Override
                    public void onResponse(Call<DeliveryDTO> call, Response<DeliveryDTO> response) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body().getResult().equalsIgnoreCase("success")) {
                                    if(response.body().getMultipledata() !=null){


                                        Handler refresh = new Handler(Looper.getMainLooper());
                                        refresh.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                deliveryDTO = response.body().getMultipledata().getDeliveryDTO();
                                                System.out.println("rescheduleStatusssss---->  "+new Gson().toJson(deliveryDTO));
                                                if (deliveryDTO.getDeliveryStatus().equals("7") && !payment_status) {
                                                    deliverstatus = true;

                                                    multiListBinding.btnSummit.setVisibility(View.GONE);
                                                    multiListBinding.btnSignup.setVisibility(View.GONE);
//                                                    multiListBinding.btnSignup.setEnabled(false);
//                                                    multiListBinding.btnSummit.setEnabled(false);
                                                    //multiListBinding.btnSummit.setAlpha(Float.parseFloat("0.2"));
                                                    //multiListBinding.btnSignup.setAlpha(Float.parseFloat("0.2"));

                                                }

                                                if (deliveryDTO.getDeliveryStatus().equals("6")) {
                                                    trackRoute = "trackdriver";
                                                    multiListBinding.btnRouteing.setVisibility(View.VISIBLE);
                                                    //multiListBinding.btnSummit.setVisibility(View.VISIBLE);
                                                }else{
                                                    multiListBinding.btnRouteing.setVisibility(View.GONE);
                                                }
                                                if (deliveryDTO.getDeliveryStatus().equals("7")) {
                                                    multiListBinding.btnRouteing.setVisibility(View.VISIBLE);

                                                }

                                                if (deliveryDTO.getIs_paid().equals("no") && !deliveryDTO.getDeliveryStatus().equals("1")&& !payment_status ) {
                                                    multiListBinding.btnReport.setVisibility(View.VISIBLE);
//                                                    multiListBinding.btnSignup.setVisibility(View.VISIBLE);

                                                    Log.d(TAG, "run: "+payment_status+" and report is "+multiListBinding.btnReport.getText()+" btn "+multiListBinding.btnSignup.getText());
                                                }
                                                multiListBinding.etPickupDate.setText(deliveryDTO.getPickupDate());
                                                multiListBinding.etPickupTime.setText(deliveryDTO.getDeliveryTime());
                                                contacts = response.body().getMultipledata().getMultipleDTOArrayList();
                                                Log.d(TAG, "datam: "+new Gson().toJson(response.body().getMultipledata().getMultipleDTOArrayList()));
                                                if (deliveryDTO.getDeliveryStatus().equals("1")) {
                                                    multiListBinding.btnRouteing.setVisibility(View.GONE);
                                                    multiListBinding.btnSummit.setVisibility(View.GONE);
                                                    multiListBinding.btnSignup.setVisibility(View.GONE);
                                                    multiListBinding.btnReport.setVisibility(View.GONE);

                                                }
                                                setvalue();
                                                onMapReadydata();
                                                initView();

                                                if (deliveryDTO.getDeliveryStatus().equals("1")) {
                                                    multiListBinding.btnRouteing.setVisibility(View.GONE);
                                                    multiListBinding.btnSummit.setVisibility(View.VISIBLE);
                                                    multiListBinding.btnSignup.setVisibility(View.VISIBLE);
                                                    multiListBinding.btnReport.setVisibility(View.GONE);

//                                                    multiListBinding.btnRouteing.setVisibility(View.GONE);
//                                                    multiListBinding.btnSummit.setVisibility(View.VISIBLE);
//                                                    multiListBinding.btnSignup.setVisibility(View.VISIBLE);
//                                                    multiListBinding.btnReport.setVisibility(View.GONE);

                                                }

                                                if (deliveryDTO.getDeliveryStatus().equals("6")) {
                                                    //multiListBinding.btnRouteing.setVisibility(View.GONE);
                                                    multiListBinding.btnSummit.setVisibility(View.GONE);
                                                    multiListBinding.btnSignup.setVisibility(View.GONE);
                                                    multiListBinding.btnReport.setVisibility(View.GONE);

                                                }
                                                if (deliveryDTO.getDeliveryStatus().equals("2")) {

                                                    multiListBinding.btnSignup.setVisibility(View.VISIBLE);
                                                    multiListBinding.btnSummit.setVisibility(View.VISIBLE);
                                                    multiListBinding.btnReport.setVisibility(View.GONE);
                                                }
                                                if (deliveryDTO.getDeliveryStatus().equals("3")) {
                                                    multiListBinding.btnRouteing.setVisibility(View.GONE);
                                                    multiListBinding.btnSignup.setVisibility(View.GONE);
                                                    multiListBinding.btnSummit.setVisibility(View.GONE);
                                                    multiListBinding.btnReport.setVisibility(View.GONE);
                                                }
                                                if (deliveryDTO.getIs_paid().equals("no") && deliveryDTO.getDeliveryStatus().equals("6")&& !payment_status ) {
                                                    multiListBinding.btnReport.setVisibility(View.VISIBLE);
                                                    multiListBinding.btnRouteing.setVisibility(View.GONE);
                                                    multiListBinding.btnSignup.setVisibility(View.GONE);
                                                    multiListBinding.btnSummit.setVisibility(View.VISIBLE);


                                                }


                                            }
                                        });


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
                    public void onFailure(Call<DeliveryDTO> call, Throwable t) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                        Log.e(TAG, t.toString());
                    }
                });
            }


        }else {
            appSession.setMultipleBack(true);
            db = new DatabaseHandler(getContext());
            setvalue();

            /*db.addContact(new MultipleDTO(deliveryDTO.getDropBuildingType(), deliveryDTO.getDropElevator(),
                    deliveryDTO.getDropoffaddress(),deliveryDTO.getDropoffSpecialInst(),
                    deliveryDTO.getPickupSpecialInst(), deliveryDTO.getDropoffFirstName(),
                    deliveryDTO.getPickupLastName(), deliveryDTO.getDropoffMobNumber(), deliveryDTO.getDropoffLat(),
                    deliveryDTO.getDropoffLong(),deliveryDTO.getDeliveryCost(), deliveryDTO.getDropoffCountryCode(),
                    deliveryDTO.getClassGoods(), deliveryDTO.getTypeGoods(), deliveryDTO.getNoOfPallets(),
                    deliveryDTO.getProductWeight(),deliveryDTO.getWeight_unit(),deliveryDTO.getTypeGoodsCategory()));*/

            Log.d("Reading: ", "Reading all contacts..");
            contacts = db.getAllContacts();
            initView();

            try{
                int noofPallet = Integer.parseInt(deliveryDTO.getNoOfPallets());
                int dropNoPallet = db.getpalletscount();

                Log.d(TAG, "callOnedata: "+deliveryDTO.getNoOfPallets1()+" and data is "+dropNoPallet);
                if(dropNoPallet==noofPallet){
                    multiListBinding.btnSignup.setVisibility(View.GONE);

                }

                List<String> labelcount =  db.getAllLabels();
                if(labelcount.size()>=20){
                    multiListBinding.btnSignup.setVisibility(View.GONE);


                }


            }catch(Exception e){
                e.printStackTrace();
            }

            if (!utilities.isNetworkAvailable())
                utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
            else {
                onMapReadydata();
            }

        }

    }


    private void setvalue() {
        multiListBinding.tvPickInside.setText( deliveryDTO.getPickupBuildingType());
        multiListBinding.tvPickLiftgate.setText(deliveryDTO.getPickupLiftGate());
        multiListBinding.tvPickPersonaddres.setText(deliveryDTO.getPickupaddress());
        multiListBinding.tvPickPersonname.setText(deliveryDTO.getPickupFirstName());
        multiListBinding.tvPickPersonmobile.setText(deliveryDTO.getPickupMobNumber());
        multiListBinding.typeOrderss.setText(deliveryDTO.getDeliveryType().toUpperCase());
        multiListBinding.tvPickSpecialinstpick.setText(deliveryDTO.getPickupSpecialInst());
        if(deliveryDTO.getDeliveryType().trim().equalsIgnoreCase("multiple")){
            multiListBinding.typeOrderss.setText("Multiple");
            multiListBinding.tvGoodsClass.setText(deliveryDTO.getClassGoods());
            multiListBinding.tvGoodsClass.setText(deliveryDTO.getClassGoods());
//            multiListBinding.tvTotalBp.setText("Total "+deliveryDTO.getTypeGoods());
//            multiListBinding.tvCountPb.setText(deliveryDTO.getNoOfPallets());

            multiListBinding.tvTotalBp.setText("Total pallets");
            multiListBinding.tvCountPb.setText(deliveryDTO.getNoOfPallets());
            multiListBinding.tvTotalBox.setText("Total boxes");
            multiListBinding.tvCountBox.setText(deliveryDTO.getNoOfPallets1());

            Log.d(TAG, "setvalue: multipleAdd "+deliveryDTO.getNoOfPallets1());

        }

        multiListBinding.etPickupDate.setText(deliveryDTO.getPickupDate());
        multiListBinding.etPickupTime.setText(deliveryDTO.getDeliveryTime());
        multiListBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
        multiListBinding.etPickupTime.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
        multiListBinding.etPickupDate.setEnabled(false);
        multiListBinding.etPickupTime.setEnabled(false);
    }


    private void initView() {

        if(2 > contacts.size() && !multiple_type){
            multiListBinding.btnSummit.setAlpha(Float.parseFloat("0.2"));
            multiListBinding.btnSummit.setEnabled(true);
            }


            linearLayoutManager = new LinearLayoutManager(context);
            multipleDropAdapter = new MultipleDropAdapter(context, contacts,multiple_type,deliverstatus,onItemClickCallback);
            multiListBinding.dropRecycler.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setReverseLayout(false);
            multiListBinding.dropRecycler.scrollToPosition(contacts.size());
            multiListBinding.dropRecycler.setAdapter(multipleDropAdapter);
            String totalPrice1 = "0";
            totalDeliveryCost = 0.0;
            System.out.println("totalPrice1"+contacts.size());
        latLngArrayList.add(new LatLng(Double.parseDouble(deliveryDTO.getPickupLat()),Double.parseDouble(deliveryDTO.getPickupLong())));
        for(int i = 0; i < contacts.size();i++){
            latLngArrayList.add(new LatLng(Double.parseDouble(contacts.get(i).getDropoffLat()),Double.parseDouble(contacts.get(i).getDropoffLong())));
        }

        for(int i=0;i < contacts.size();i++) {
            if (contacts.get(i).getDeliveryCost() != null) {
                totalDeliveryCost = totalDeliveryCost + (Double.parseDouble(contacts.get(i).getDeliveryCost()));
            }
        }

                totalPrice = String.valueOf(totalDeliveryCost);

            Log.d("totalPrice ",totalDeliveryCost.toString());
            Log.d("totalPrice ",totalPrice.toLowerCase());

             multiListBinding.tvTotalcost.setText(getString(R.string.us_dollar) + " " + String.valueOf(Double.valueOf(totalPrice)));
            multiListBinding.tvTotaldistance.setText(deliveryDTO.getTotal_distance());

            if(contacts.size() == 20)
            {
                multiListBinding.btnSignup.setVisibility(View.GONE);
            }

            try {

                int noofPallet = Integer.parseInt(deliveryDTO.getNoOfPallets());
                int dropNoPallet = db.getpalletscount();

                if (dropNoPallet < noofPallet) {
                    multiListBinding.btnSignup.setVisibility(View.VISIBLE);

                }

                List<String> labelcount = db.getAllLabels();
                if (labelcount.size() >= 20) {
                    multiListBinding.btnSignup.setVisibility(View.GONE);


                }
            }catch(Exception e){
                e.printStackTrace();
            }

//        try {
//            multipleDTO.setTotalcost(String.format("%2f", Double.parseDouble(totalPrice)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        multiListBinding.ivBack.setOnClickListener(this);
            multiListBinding.btnRouteing.setOnClickListener(this);

        multiListBinding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ((DrawerContentSlideActivity) context).popFragment();
                if(multiple_type){

                    if(multiListBinding.btnSignup.getText().equals("RESCHEDULE")){
                        CreateOrderExpressDelivery createOrderExpressDelivery = new CreateOrderExpressDelivery();
                        Bundle bundle = new Bundle();
                        multiListBinding.btnSignup.setText("SUBMIT");
                        multiListBinding.etPickupDate.setEnabled(true);
                        multiListBinding.etPickupTime.setEnabled(true);
                        multiListBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black_color)));
                        multiListBinding.etPickupTime.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black_color)));
//                        bundle.putString("delivery_type", "multiple");
//                        bundle.putParcelable("deliveryDTO", deliveryDTO);
//                        createOrderExpressDelivery.setArguments(bundle);
//                        addFragmentWithoutRemove(R.id.container_main,createOrderExpressDelivery,"CreateOrderExpressDelivery");


//                        CreateOrderExpressDeliveryDrop createOrderExpressDelivery = new CreateOrderExpressDeliveryDrop();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("delivery_type", "multiple1");
//                        bundle.putParcelable("deliveryDTO", deliveryDTO);
//                        createOrderExpressDelivery.setArguments(bundle);
//                        addFragmentWithoutRemove(R.id.container_main, createOrderExpressDelivery, "CreateOrderExpressDelivery");

                    }else if(multiListBinding.btnSignup.getText().equals("SUBMIT")){
                        callRescheduleMultipleOrderAPI();

                    }else if(multiListBinding.btnSignup.getText().equals("PAYMENT") && payment_status){
                        CardStripePayment cardStripePayment = new CardStripePayment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("deliveryDTO", deliveryDTO);
                        cardStripePayment.setArguments(bundle);
                        addFragmentWithoutRemove(R.id.container_main, cardStripePayment, "cardStripePayment");
                    }

                }else {
                //CreateOrderExpressDelivery createOrderExpressDelivery = new CreateOrderExpressDelivery();

                CreateOrderExpressDeliveryDrop createOrderExpressDelivery = new CreateOrderExpressDeliveryDrop();
                Bundle bundle = new Bundle();
                bundle.putString("delivery_type", "multiple1");
                bundle.putParcelable("deliveryDTO", deliveryDTO);
                createOrderExpressDelivery.setArguments(bundle);
                addFragmentWithoutRemove(R.id.container_main, createOrderExpressDelivery, "CreateOrderExpressDelivery");


            }
            }
        });

        multiListBinding.btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardStripePayment cardStripePayment = new CardStripePayment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("deliveryDTO", deliveryDTO);
                cardStripePayment.setArguments(bundle);
                addFragmentWithoutRemove(R.id.container_main, cardStripePayment, "cardStripePayment");
            }
        });

        multiListBinding.btnSummit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(multiple_type){

                    new AlertDialog.Builder(context)
                            .setMessage(getString(R.string.are_you_cancel))
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    callCancelDeliveryApi();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else {
                    //Toast.makeText(context,"gofromMultipleAdd", Toast.LENGTH_LONG).show();
                    DeliveryCheckoutExpressDelivery deliveryCheckoutExpressDelivery = new DeliveryCheckoutExpressDelivery();
                    Bundle bundle = new Bundle();
                    bundle.putString("delivery_type", "multiple");
                    System.out.println("totalPrice----> "+totalPrice);
///                    deliveryDTO.setDeliveryCost(totalPrice);
                    bundle.putParcelable("deliveryDTO", deliveryDTO);
                    deliveryCheckoutExpressDelivery.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, deliveryCheckoutExpressDelivery, "DeliveryCheckoutExpressDelivery");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
            case R.id.btn_routeing:
                MapsActivity mapsActivity =  new MapsActivity();
                Bundle bundle = new Bundle();
                //bundle.putString("ordeersid",deliveryId);
                bundle.putString("ordeersid",deliveryDTO.getOrderId());
                bundle.putString("driveridd",deliveryDTO.getDriverId());
                bundle.putParcelableArrayList("latlang",latLngArrayList);
                bundle.putString("trackRoute",trackRoute);
                mapsActivity.setArguments(bundle);
                addFragmentWithoutRemove(R.id.container_main, mapsActivity, "MapsActivity");
                break;
        }
    }

    public void callRescheduleMultipleOrderAPI() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            deliveryDTO.setUserId(appSession.getUser().getData().getUserId());
            System.out.println("etPickupDate---> "+ multiListBinding.etPickupDate.getText().toString());
            deliveryDTO.setPickupDate(multiListBinding.etPickupDate.getText().toString());
            System.out.println("etPickupDate---> "+ deliveryDTO.getPickupDate());
            deliveryDTO.setDeliveryTime(multiListBinding.etPickupTime.getText().toString());
            DeliveryMultipleDTO deliveryMultipleDTO =  new DeliveryMultipleDTO();
            deliveryMultipleDTO.setDeliveryDTO(deliveryDTO);
            deliveryMultipleDTO.setMultipleDTOArrayList(contacts);
            System.out.println(new Gson().toJson(deliveryMultipleDTO));
            Log.d(TAG, "callRescheduleMultipleOrderAPI: "+ new Gson().toJson(deliveryMultipleDTO));
            DeliverySendMultipleDataDTO deliverySendMultipleDataDTO = new DeliverySendMultipleDataDTO();
            deliverySendMultipleDataDTO.setData(APP_TOKEN);
            deliverySendMultipleDataDTO.setDeliveryMultipleDTO(deliveryMultipleDTO);
            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.rescheduleMultipleOrderAPI(deliverySendMultipleDataDTO);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {

                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                multiListBinding.btnSignup.setText("RESCHEDULE");
                                multiListBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
                                multiListBinding.etPickupTime.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
                                multiListBinding.etPickupDate.setEnabled(false);
                                multiListBinding.etPickupTime.setEnabled(false);
                                utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
                                    @Override
                                    public void onYes() {
                                        ((DrawerContentSlideActivity) context).popAllFragment();
                                    }
                                    @Override
                                    public void onNo() {
                                    }
                                });
                            }else {
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

    public void callCancelDeliveryApi() {

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
            map.put("order_id", deliveryId);
            map.put(PN_APP_TOKEN, APP_TOKEN);

            Call<OtherDTO> call;
            APIInterface apiInterface = APIClient.getClient();

                call = apiInterface.callCancelOrderApi(map);

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
                                        ((DrawerContentSlideActivity) context).popFragment();
                                            replaceFragmentWithoutBack(R.id.container_main, new CurrentList(), "CurrentList");
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
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

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

    OnItemClickListener.OnItemClickCallback onItemClickCallback = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            deletePlace(position+1);
        }
    };


    private void deletePlace(int position){
        System.out.println("postion---> "+position);
        db  = new DatabaseHandler(context);
        db.deletetitle(contacts.get(position).getDropoffMobNumber());
//        deliveryDTOArrayList.remove(position);
        contacts = db.getAllContacts();
        onMapReadydata();
        initView();

        //        multipleDropAdapter.notifyDataSetChanged();
//        notifyDataSetChanged();
    }


    public void callOrderMultipleBookApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            deliveryDTO.setUserId(appSession.getUser().getData().getUserId());

            DeliveryMultipleDTO deliveryMultipleDTO =  new DeliveryMultipleDTO();
            deliveryMultipleDTO.setDeliveryDTO(deliveryDTO);
//            deliveryMultipleDTO.setMultipleDTOArrayList(db.getAllContacts());
            deliveryMultipleDTO.setMultipleDTOArrayList(contacts);
            System.out.println(new Gson().toJson(deliveryMultipleDTO));
            DeliverySendMultipleDataDTO deliverySendMultipleDataDTO = new DeliverySendMultipleDataDTO();
            deliverySendMultipleDataDTO.setData(APP_TOKEN);
            deliverySendMultipleDataDTO.setDeliveryMultipleDTO(deliveryMultipleDTO);

            Log.d("datamulcal",new Gson().toJson(deliverySendMultipleDataDTO));
            System.out.println("newdta"+new Gson().toJson(deliverySendMultipleDataDTO));
            Log.d(TAG, "callOrderMultipleBookApi: "+new Gson().toJson(deliveryDTO));
            APIInterface apiInterface = APIClient.getClient();
            Call<PricesDTO> call = apiInterface.priceCalculationForMultipleAPI(deliverySendMultipleDataDTO);
            call.enqueue(new Callback<PricesDTO>() {
                @Override
                public void onResponse(Call<PricesDTO> call, Response<PricesDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {

                            if (response.body().getResult().equalsIgnoreCase("success")) {

                                deliveryDTO.setDeliveryCost(String.format("%.2f",Double.parseDouble(response.body().getPricedat().getTotalPrice())));
                                deliveryDTO.setNo_tax_delivery_cost(response.body().getPricedat().getTotal());
                                deliveryDTO.setTotal_distance(response.body().getPricedat().getStop_location_final());
                                deliveryDTO.setDeliveryDistance(String.format("%.2f",Double.parseDouble(response.body().getPricedat().getStop_location_final())));

                                Log.d(TAG, "onResponse: "+new Gson().toJson(response.body()));

                                for (int priint =0 ;priint < response.body().getPricedat().getDrop().size();priint ++){
                                    contacts.get(priint).setDeliveryCost(response.body().getPricedat().getDrop().get(priint).getTotalPrice());
                                }
                                multiListBinding.tvTotalcost.setText(getString(R.string.us_dollar) + " "+String.format("%.2f",Double.parseDouble(deliveryDTO.getDeliveryCost())));
                                multiListBinding.tvTotaldistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getTotal_distance()))+ " Mile");
                                multiListBinding.dropRecycler.setAdapter(multipleDropAdapter);
                                multipleDropAdapter.notifyDataSetChanged();

//                                db.delete();

//                                utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
//                                    @Override
//                                    public void onYes() {
//
//                                        System.out.println("success.responce----> "+new Gson().toJson(response.body()));
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



}
