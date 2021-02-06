package com.pickanddrop.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
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
import com.pickanddrop.databinding.RouteLayoutBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.LocationTrackDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.DataParser;
import com.pickanddrop.utils.MyConstant;
import com.pickanddrop.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Route extends BaseFragment implements AppConstants, View.OnClickListener, OnMapReadyCallback {
    LatLng destination;
    LatLng curentlocation;
    String url;
    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    Marker mCurrLocationMarker = null;
    private RouteLayoutBinding routeLayoutBinding;
    private String TAG = Route.class.getName();
    private GoogleMap mMap;
    private DeliveryDTO.Data deliveryDTO;
    ArrayList<LatLng> markerPoints;
    double lattitude,longitude;
    String address;
    ProgressDialog mProgressDialog;
    String fullAdd=null;
    int PERMISSION_ALL = 1;
    String oderidss;
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

        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            deliveryDTO = getArguments().getParcelable("deliveryDTO");
            //   System.out.println(" GSSSOsddfNNNN--->----> " + new Gson().toJson(deliveryDTO));
        }

        if (getArguments() != null && getArguments().containsKey("trackRoute")) {
            trackRoute = getArguments().getString("trackRoute");
        }

        if (getArguments() != null && getArguments().containsKey("ordeersid")) {
            oderidss = getArguments().getString("ordeersid");
        }

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.new_car_icon);
        Bitmap b = bitmapdraw.getBitmap();
        car_bitmap = Bitmap.createScaledBitmap(b, 120, 120, false);

        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                driverLocationget();
                ha.postDelayed(this, 3000);
            }
        }, 3000);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        routeLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.route_layout, container, false);
        return routeLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        //Toast.makeText(context,"Route",Toast.LENGTH_LONG).show();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();
        setMap();
    }

    private void initToolBar() {

    }

    private void initView() {
        routeLayoutBinding.ivBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
        }
    }

    public void setMap() {
        try {
            if (routeLayoutBinding.mvHome != null) {
                routeLayoutBinding.mvHome.onCreate(null);
                routeLayoutBinding.mvHome.onResume();
                routeLayoutBinding.mvHome.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            MapsInitializer.initialize(context);
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (mMap != null) {

               /* LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!hasPermissions(getContext(), PERMISSIONS)) {
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
                 //LatLng source = new LatLng(Double.parseDouble(deliveryDTO.getPickupLat()), Double.parseDouble(deliveryDTO.getPickupLong()));

                String droplat = getArguments().getString("DropLat");
                String droplang = getArguments().getString("DropLang");
                //LatLng destination = new LatLng(Double.parseDouble(deliveryDTO.getDropoffLat()), Double.parseDouble(deliveryDTO.getDropoffLong()));
                LocationManager locationManagers = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                Location location = locationManager.getLastKnownLocation(locationManagers.getBestProvider(criteria, false));
                double lat = location.getLatitude();
                double lng = location.getLongitude();*/


                Map<String, String> map = new HashMap<>();
                System.out.println("orderidsssssingle------>"+oderidss);
                // map.put("driver_id",deliveryDTO.getDriverId());
                map.put("order_id", oderidss);
                map.put("code", APP_TOKEN);

                APIInterface apiInterface = APIClient.getClient();
                Call<LocationTrackDTO> call = apiInterface.getDriverLocationkk(map);
                call.enqueue(new Callback<LocationTrackDTO>() {
                    @Override
                    public void onResponse(Call<LocationTrackDTO> call, Response<LocationTrackDTO> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                System.out.println("Data images:>>>>>>>" + new Gson().toJson(response.body().getData()));
                                if(response.body().getData().getLatitude()!=null){
                                    Double  lattitude = Double.parseDouble(response.body().getData().getLatitude());
                                    Double  longitude =  Double.parseDouble(response.body().getData().getLongitude());
                                    LatLng curentlocation = new LatLng(lattitude, longitude);

                                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                                            .position(curentlocation)
                                            .icon(BitmapDescriptorFactory.fromBitmap(car_bitmap))
                                            .title(address));

                                    if (trackRoute.equalsIgnoreCase("trackdriver")) {

                                        //       if (deliveryDTO.getDeliveryType()!=null&&deliveryDTO.getDeliveryType().trim().equalsIgnoreCase("MULTIPLE")) {

                                        destination = new LatLng(Double.parseDouble(deliveryDTO.getPickupLat()), Double.parseDouble(deliveryDTO.getPickupLong()));
                                        // destination = new LatLng(Double.parseDouble(droplat), Double.parseDouble(droplang));
                                        url = getDirectionsUrl(curentlocation, destination);

                                        CameraPosition cameraPosition =
                                                new CameraPosition.Builder().target(curentlocation).zoom(16).build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null);


                                        mMap.addMarker(new MarkerOptions()
                                                .position(curentlocation)
                                                .icon(BitmapDescriptorFactory.
                                                        fromResource(R.drawable.pin2))
                                                .title(getString(R.string.pickup_txt)));


                                        mMap.addMarker(new MarkerOptions()
                                                .position(destination)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin1))
                                                .title(getString(R.string.drop_off_txt)));
                                        //  .snippet(deliveryDTO.getDropoffaddress()));
                                    }else{
                                        LatLng source = new LatLng(Double.parseDouble(deliveryDTO.getPickupLat()), Double.parseDouble(deliveryDTO.getPickupLong()));
                                        LatLng destination = new LatLng(Double.parseDouble(deliveryDTO.getDropoffLat()), Double.parseDouble(deliveryDTO.getDropoffLong()));
                                        url = getDirectionsUrl(source, destination);

                                        CameraPosition cameraPosition =
                                                new CameraPosition.Builder().target(source).zoom(16).build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2500,null);

                                        mMap.addMarker(new MarkerOptions()
                                                .position(source)
                                                .icon(BitmapDescriptorFactory.
                                                        fromResource(R.drawable.pin2))
                                                .title(getString(R.string.pickup_txt))
                                                .snippet(deliveryDTO.getPickupaddress()));

                                        mMap.addMarker(new MarkerOptions()
                                                .position(destination)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin1))
                                                .title(getString(R.string.drop_off_txt))
                                                .snippet(deliveryDTO.getDropoffaddress()));
                                    }

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


                                    try {
                                        DownloadTask downloadTask = new DownloadTask();
                                        downloadTask.execute(url);
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void driverLocationget() {
        Map<String, String> map = new HashMap<>();
        System.out.println("orderidsssssingle------>"+oderidss);
        // map.put("driver_id",deliveryDTO.getDriverId());
        map.put("order_id", oderidss);
        map.put("code", APP_TOKEN);

        APIInterface apiInterface = APIClient.getClient();
        Call<LocationTrackDTO> call = apiInterface.getDriverLocationkk(map);
        call.enqueue(new Callback<LocationTrackDTO>() {
            @Override
            public void onResponse(Call<LocationTrackDTO> call, Response<LocationTrackDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult().equalsIgnoreCase("success")) {
                        System.out.println("Data images:>>>>>>>" + new Gson().toJson(response.body().getData()));
                        if(response.body().getData().getLatitude()!=null){
                            Double  lattitude = Double.parseDouble(response.body().getData().getLatitude());
                            Double  longitude =  Double.parseDouble(response.body().getData().getLongitude());
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
            Log.e(TAG, "Distance is >>>>>"+ sb.toString());

            data = sb.toString();
            try {
                jsonObject = new JSONObject(data);
                jsonArray = jsonObject.getJSONArray("routes");
                legs = jsonArray.getJSONObject(0).getJSONArray("legs");
                json = legs.getJSONObject(0).getJSONObject("distance");
                String distance = json.getString("text");

                Log.e(TAG, "Distance is >>>>>"+ distance);
                Log.e(TAG, "Total Response is >>>>>"+ jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
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

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

        }

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
                lineOptions.width(20);
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


}
