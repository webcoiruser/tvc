package com.pickanddrop.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.ActivityMapsBinding;
import com.pickanddrop.dto.LocationTrackDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.DataParser;
import com.pickanddrop.utils.MyConstant;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pickanddrop.utils.AppConstants.APP_TOKEN;

public class MapsActivity extends BaseFragment implements OnMapReadyCallback, View.OnClickListener{

    ProgressDialog mProgressDialog;
    String oderidss;
    String Driveridd;
    Context context;
    ActivityMapsBinding activityMapsBinding;
    ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();

    private GoogleMap mMap;
    LinkedList<LatLng> markerPoints;
    LinkedList<LatLng> diatances;
    LinkedList<Double> adddistance;
    LatLng dest;
    String parsedDistance;
    double lattitude,longitude;
    String response;
    String address;
    Marker mCurrLocationMarker = null;
    String fullAdd=null;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private Bitmap car_bitmap;
    private float start_rotation = 0.0f;
    private String trackRoute = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager=(LocationManager)getActivity(). getSystemService(Context.LOCATION_SERVICE);

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.cae_new_icon);
        Bitmap b = bitmapdraw.getBitmap();
        car_bitmap = Bitmap.createScaledBitmap(b, 80, 80, false);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityMapsBinding = DataBindingUtil.inflate(inflater, R.layout.activity_maps,container,false);
        return activityMapsBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        markerPoints = new LinkedList<>();

        if(getArguments().containsKey("latlang")){
            latLngArrayList = getArguments().getParcelableArrayList("latlang");

        }
        if (getArguments() != null && getArguments().containsKey("trackRoute")) {
            trackRoute = getArguments().getString("trackRoute");
        }
        if (getArguments() != null && getArguments().containsKey("ordeersid")) {
            oderidss = getArguments().getString("ordeersid");
        }
        if (getArguments() != null && getArguments().containsKey("driveridd")) {
            Driveridd = getArguments().getString("driveridd");
        }
        initView();
        initToolBar();
        setMap();
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
               Driverlocationfun();
                ha.postDelayed(this, 3000);
            }
        }, 3000);

    }

    public void setMap() {
        try {
            if (activityMapsBinding.mvMulhome != null) {
                activityMapsBinding.mvMulhome.onCreate(null);
                activityMapsBinding.mvMulhome.onResume();
                activityMapsBinding.mvMulhome.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolBar() {

    }

    private void initView() {

        activityMapsBinding.ivBack.setOnClickListener(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {

            MapsInitializer.initialize(context);
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            LatLng source = new LatLng();
            if (mMap != null) {


                if (trackRoute.equalsIgnoreCase("trackdriver")) {
                    Map<String, String> map = new HashMap<>();
                    System.out.println("orderidssssmulitiple------>"+oderidss);
                    map.put("order_id", oderidss);
                    // map.put("driver_id",Driveridd);
                    map.put("code", APP_TOKEN);

                    APIInterface apiInterface = APIClient.getClient();
                    Call<LocationTrackDTO> call = apiInterface.getDriverLocationkk(map);
                    call.enqueue(new Callback<LocationTrackDTO>() {
                        @Override
                        public void onResponse(Call<LocationTrackDTO> call, Response<LocationTrackDTO> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getResult().equalsIgnoreCase("success")) {
                                    if (response.body().getData().getLatitude() != null) {
                                        System.out.println("Datasdfsdmages:>>>>>>>" + new Gson().toJson(response.body().getData()));
                                        Double lattitude = Double.parseDouble(response.body().getData().getLatitude());
                                        Double longitude = Double.parseDouble(response.body().getData().getLongitude());
                                        LatLng latLng = new LatLng(lattitude, longitude);

                                        if(mCurrLocationMarker==null) {
                                            mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(car_bitmap))
                                                    .title(address));
                                        }



                                        mMap.addMarker(new MarkerOptions()
                                                .position(latLngArrayList.get(0))
                                                .icon(BitmapDescriptorFactory.
                                                        fromResource(R.drawable.area))
                                                .title(getString(R.string.pickup_txt)));

                                        CameraPosition cameraPosition =
                                                new CameraPosition.Builder().target(latLngArrayList.get(0)).zoom(14).build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2500,null);

                                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                            @Override
                                            public void onInfoWindowClick(Marker marker) {
                                                Toast.makeText(getActivity(),fullAdd,Toast.LENGTH_LONG).show();            }
                                        });

                                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker) {
                                                LatLng location = marker.getPosition();
                                                address=getAddress(getActivity(),location.latitude,location.longitude);
                                                marker.setTitle(fullAdd);
                                                return false;
                                            }
                                        });


                                        String url = getDirectionsUrl(latLng,latLngArrayList.get(0),trackRoute);
                                        DownloadTask downloadTask = new DownloadTask();

                                        downloadTask.execute(url);



                                    }

                                }

                            }


                        }

                        @Override
                        public void onFailure(Call<LocationTrackDTO> call, Throwable t) {

                        }
                    });

                }else{
                    for(int i = 0; i < latLngArrayList.size();i++){
                        if(i != 0){
                            markerPoints.add(latLngArrayList.get(i));
                        }
                    }
//                LatLng source = new LatLng(Double.parseDouble("11.9416"), Double.parseDouble("79.8083"));
//                LatLng pick1 = new LatLng(Double.parseDouble("13.0827"), Double.parseDouble("80.2707"));
//                LatLng pick2 = new LatLng(Double.parseDouble("15.1394"), Double.parseDouble("76.9214"));
//                LatLng pick3 = new LatLng(Double.parseDouble("17.3850"), Double.parseDouble("78.4867"));
//                LatLng pick4 = new LatLng(Double.parseDouble("22.7196"), Double.parseDouble("75.8577"));
//                //markerPoints.add(source);
//                markerPoints.add(pick2);
//                markerPoints.add(pick1);
//                markerPoints.add(pick4);
//                markerPoints.add(pick3);m

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder().target(latLngArrayList.get(0)).zoom(14).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2500,null);

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Toast.makeText(getActivity(),fullAdd,Toast.LENGTH_LONG).show();            }
                    });

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            LatLng location = marker.getPosition();
                            address=getAddress(getActivity(),location.latitude,location.longitude);
                            marker.setTitle(fullAdd);
                            return false;
                        }
                    });

                    //  address=getAddress(getActivity(),lattitude,longitude);
                    //  address=getAddress(getActivity(),source.latitude,source.longitude);



                    adddistance=new LinkedList<>();
                    //   adddistance.add(0.0);


                    if(markerPoints.size() >= 1){

                        for(int i=0;i<markerPoints.size();i++) {
                            LatLng point = (LatLng) markerPoints.get(i);
                            System.out.println("destination--->>>>>>>"+"destination="+point.latitude+","+point.longitude);
                            getDistance(latLngArrayList.get(0).latitude,latLngArrayList.get(0).longitude,point.latitude,point.longitude);
                        }

                        System.out.println("Arraylist data markerPoints>>>>>>>>>>"+markerPoints);
                        System.out.println("Arraylist data >>>>>>>>>>"+adddistance);

                        LatLng vaules = null;

                        diatances = new LinkedList<>();
                        diatances = markerPoints;

                        for (int i = 0; i < adddistance.size(); i++)
                        {
                            for (int j = i + 1; j < adddistance.size(); j++)
                            {
                                System.out.println("getValueofiiiiiiiiiii-->"+adddistance.get(i));
                                System.out.println("getValueofjjjjjjjjjjj-->"+adddistance.get(j));
                                if (adddistance.get(i) > adddistance.get(j))
                                {

                                    vaules = diatances.get(i);
                                    diatances.set(i,diatances.get(j));
                                    diatances.set(j,vaules);
                                }
                            }
                        }
                        System.out.println("AddDistance Dataaa >>>>>>>>>>"+diatances);
                        dest =  diatances.get(diatances.size()-1);

                        mMap.addMarker(new MarkerOptions()
                                .position(latLngArrayList.get(0))
                                .icon(BitmapDescriptorFactory.
                                        fromResource(R.drawable.area))
                                .title(getString(R.string.pickup_txt)));
                        System.out.println("MrakerPoints---->"+markerPoints);
                        for(int k=0;k<markerPoints.size();k++){
                            if(dest.latitude==markerPoints.get(k).latitude) {
                                LatLng dest=new LatLng(markerPoints.get(k).latitude,markerPoints.get(k).longitude);
                                mMap.addMarker(new MarkerOptions()
                                        .position(dest)
                                        .icon(BitmapDescriptorFactory.
                                                fromResource(R.drawable.pin2))
                                        .title(getString(R.string.pickup_txt)));
                            }else{
                                LatLng pick=new LatLng(markerPoints.get(k).latitude,markerPoints.get(k).longitude);
                                mMap.addMarker(new MarkerOptions()
                                        .position(pick)
                                        .icon(BitmapDescriptorFactory.
                                                fromResource(R.drawable.pin1))
                                        .title(getString(R.string.pickup_txt)));
                            }

                        }


                        System.out.println("  alla  distance --->"+adddistance);

                        String url = getDirectionsUrl(latLngArrayList.get(0), dest,trackRoute);
                        DownloadTask downloadTask = new DownloadTask();

                        downloadTask.execute(url);
                    }


                    //end else/////////
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void Driverlocationfun() {

        Map<String, String> map = new HashMap<>();
        System.out.println("orderidssssmulitiple------>"+oderidss);
        map.put("order_id", oderidss);
       // map.put("driver_id",Driveridd);
        map.put("code", APP_TOKEN);

        APIInterface apiInterface = APIClient.getClient();
        Call<LocationTrackDTO> call = apiInterface.getDriverLocationkk(map);
        call.enqueue(new Callback<LocationTrackDTO>() {
            @Override
            public void onResponse(Call<LocationTrackDTO> call, Response<LocationTrackDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult().equalsIgnoreCase("success")) {
                        if (response.body().getData().getLatitude() != null) {
                            System.out.println("Datasdfsdmages:>>>>>>>" + new Gson().toJson(response.body().getData()));
                            Double lattitude = Double.parseDouble(response.body().getData().getLatitude());
                            Double longitude = Double.parseDouble(response.body().getData().getLongitude());
                            LatLng latLng = new LatLng(lattitude, longitude);

                            if(map!=null){

                                if(mCurrLocationMarker==null) {
                                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(car_bitmap))
                                            .title(address));
                                }
                                if(mCurrLocationMarker!=null){

                                    double distb = CalculationByDistance(mCurrLocationMarker.getPosition(),latLng);
                                    double newbearing = FindBearing(mCurrLocationMarker,latLng);

                                    Log.d("data",distb+"");
                                    if(distb>15)
                                    {
                                        Log.d("data",distb+"   in if condition");
                                        animateMarker(mCurrLocationMarker,latLng,false);
                                        rotateMarker((float) newbearing,start_rotation, mCurrLocationMarker);

                                    }else {
                                        Log.d("data",distb+"   in else condition");

                                    }

                                }

                            }


                        }/*else{
                            Toast.makeText(context, "Driver didn't check route", Toast.LENGTH_SHORT).show();

                        }*/
                    }
                }


            }

            @Override
            public void onFailure(Call<LocationTrackDTO> call, Throwable t) {

            }
        });

    }


    public void rotateMarker(final Float bearing,final Float st,final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * bearing + (1 - t) * st;

                float aa = 0.0f;
                Log.d("rotateMarker %s", "aa  $aa");
                //aa = if (-rot > 180) rot / 2 else rot;
                if(-rot>180) {
                    aa = rot / 2;
                } else {
                    aa = rot;
                }
                Log.d("rotateMarker %s", "aa  $aa");

                marker.setAnchor(0.5f, 0.5f);
                //marker.rotation = if (-rot > 180) rot / 2 else rot
                if(-rot>180) {
                    marker.setRotation(rot/2);
                } else {
                    marker.setRotation(rot);
                }
                //start_rotation = if (-rot > 180) rot / 2 else rot
                if(-rot>180) {
                    start_rotation = rot / 2;
                } else {
                    start_rotation = rot;
                }

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private double FindBearing(Marker startPoint, LatLng endPoint) {
        double longitude1 = startPoint.getPosition().longitude;
        double latitude1 = Math.toRadians(startPoint.getPosition().latitude);

        double longitude2 = endPoint.longitude;
        double latitude2 = Math.toRadians(endPoint.latitude);

        double longDiff = Math.toRadians(longitude2 - longitude1);

        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return Math.toDegrees(Math.atan2(y, x));
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
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

        return Radius * c * 1000;
    }

    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2){

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 +  "&mode=driving&key=" + getString(R.string.google_maps_key));

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

                        try {

                            Double val = Double.parseDouble(strNew);
                            System.out.println("Distance of ----->" + parsedDistance);
                            adddistance.add(val);
                        }catch(Exception e){}

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
    private String getDirectionsUrl(LatLng origin, LatLng dest,String trackR){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";

        String waypoints = "";
        if (!trackR.equalsIgnoreCase("trackdriver")) {
            for(int i=0;i<markerPoints.size();i++){

                System.out.println("dataaaa"+i);


                LatLng point  = (LatLng) markerPoints.get(i);

                System.out.println("lattt--->"+point.latitude+"---"+"langg---->"+point.longitude);

                if(i==0)
                    waypoints = "waypoints=";
                waypoints += point.latitude + "," + point.longitude + "|";

            }

        }else {
            waypoints = "waypoints=";

        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);

        return url;
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
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;

        }
    }
    public void loadNavigationView(LatLng origin, LatLng dest){
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";

        String waypoints = "";
        for(int i=0;i<markerPoints.size();i++){

            System.out.println("dataaaa"+i);


            LatLng point  = (LatLng) markerPoints.get(i);


            if(i==0)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";

        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+waypoints;
        // Output format
        String output = "json";
//Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving");
        // Building the url to the web service
        String url = "https://www.google.com/maps/dir/?api=1&"+parameters + "&travelmode=driving";

        // String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);


        Uri gmmIntentUri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(getContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }

      /*  Uri navigation = Uri.parse("google.navigation:q="+lat+","+lng+"");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);*/
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
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";

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
            mProgressDialog.dismiss();
            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);

            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
    public String getAddress(Context ctx,double lat,double lang){

        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> adress = geocoder.getFromLocation(lat, lang, 1);
            if(adress.size()>0){
                Address addresss=adress.get(0);
                fullAdd=addresss.getAddressLine(0);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return fullAdd;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }



}