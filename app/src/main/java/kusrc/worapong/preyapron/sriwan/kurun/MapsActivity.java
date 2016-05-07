package kusrc.worapong.preyapron.sriwan.kurun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Criteria criteria;
    private boolean gpsABoolean, networkABoolean;
    private double myLatADouble, myLngADouble;
    private String[] resultStrings;
    private double[] buildLatDoubles = {13.12362768,13.12512183,13.12090057,13.11748381};
    private double[] buildLngDoubles = {100.91835022,100.9192729,100.91940165,100.92124701};
    private boolean myStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Setup
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        resultStrings = getIntent().getStringArrayExtra("Result");

    }   // Main Method

    //Create Inner Class
    public class SynLatLngAllUser extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://swiftcodingthai.com/keng/php_get_user_master.php").build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("19April", "doIn ==> " + e.toString());
                return null;
            }

        }   // doInBack

        @Override
        protected void onPostExecute(String strJSON) {
            super.onPostExecute(strJSON);

            Log.d("18April", "strJSON ==> " + strJSON);

            try {

                JSONArray jsonArray = new JSONArray(strJSON);
                for (int i=0;i<jsonArray.length();i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String strName = jsonObject.getString("Name");
                    String strAvata = jsonObject.getString("Avata");
                    String strLat = jsonObject.getString("Lat");
                    String strLng = jsonObject.getString("Lng");

                    makeAllMarker(strName, strAvata, strLat, strLng);



                }       //for

            } catch (Exception e) {
                Log.d("19April", "strJSON error ==>" + e.toString());
            }   // try


        }   // onPost
    }   // SynLatLng Class

    private void makeAllMarker(String strName,
                               String strAvata,
                               String strLat,
                               String strLng) {

        LatLng latLng = new LatLng(Double.parseDouble(strLat),
                Double.parseDouble(strLng));

        int intAvata = findIconMarker(strAvata);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(intAvata))
                .title(strName));



    }   // makeAllMarker


    private void myLoop() {

        Log.d("5April", "Location = " + myLatADouble + " , " + myLngADouble);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLoop();
            }
        }, 3000);

    } // myLoop

    @Override
    protected void onResume() {
        super.onResume();

        locationManager.removeUpdates(locationListener);

        //นี่คือค่าเริ่มต้นของ Map ถ้าไม่ได้ต่อ GPS หรือ Net
        myLatADouble = 13.668066;
        myLngADouble = 100.622454;

        Location networkLocation = findLocation(LocationManager.NETWORK_PROVIDER, "Cannot Connected Internet");
        if (networkLocation != null) {
            myLatADouble = networkLocation.getLatitude();
            myLngADouble = networkLocation.getLongitude();
        }
        Location gpsLocation = findLocation(LocationManager.GPS_PROVIDER, "No Card GPS");
        if (gpsLocation != null) {
            myLatADouble = gpsLocation.getLatitude();
            myLngADouble = gpsLocation.getLongitude();
        }

    }   // onResume

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    public Location findLocation(String strProvider,
                                 String strError) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {

            locationManager.requestLocationUpdates(strProvider,
                    1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        } else {
            Log.d("5April", strError);
        }

        return location;
    }


    //Class
    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            myLatADouble = location.getLatitude();
            myLngADouble = location.getLongitude();

        }   // onLocationChange

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        goToCenterMap();

        createAllMarker();

    }   // onMapReady

    private void createAllMarker() {

        mMap.clear();   // Delete All Marker

        // Create Marker Building


        String[] baseStrings = {"ด่านที่ 1", "ด่านที่ 2", "ด่านที่ 3", "ด่านที่ 4"};
        int[] iconBaseInts = {5,6,7,8};

        for (int i=0;i<baseStrings.length;i++) {

            makeAllMarker(baseStrings[i],
                    Integer.toString(iconBaseInts[i]),
                    Double.toString(buildLatDoubles[i]),
                    Double.toString(buildLngDoubles[i]));



        }   // for


        //Update Lat, Lng to mySQL
        updateLatLngToMySQL();

        //Synchronize Lat, Lng All User
        SynLatLngAllUser synLatLngAllUser = new SynLatLngAllUser();

        synLatLngAllUser.execute();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createAllMarker();
            }
        }, 3000);   // เวลาที่ใช้อัพเดท Server 3 วินาที


    }   // createAllMarker

    private void updateLatLngToMySQL() {

        String strID = resultStrings[0];
        Log.d("18April", "id ==> " + strID);

        String strLat = Double.toString(myLatADouble);
        String strLng = Double.toString(myLngADouble);

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("isAdd", "true")
                .add("id", strID)
                .add("Lat", strLat)
                .add("Lng", strLng)
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://swiftcodingthai.com/keng/php_edit_location.php")
                .post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("18April", "error ==> " + e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {

                } catch (Exception e) {
                    Log.d("18April", "error ==> " + e.toString());
                }

            }
        });

        //Find Distance
        double myDistance = distance(myLatADouble, myLngADouble,
                buildLatDoubles[0], buildLngDoubles[0]);
        Log.d("7MayV1", "myDistance กับ ฐานที่ 1 ==> " + myDistance);
        if (myDistance <= 10 && myStatus) {
            myAlert("ฐานที่ 1", R.drawable.base1);
        }

    }   // update

    private void myAlert(final String strMessage,
                         final int intIcon) {

        myStatus = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setCancelable(false);
        builder.setTitle(strMessage);
        builder.setMessage("คุณต้องตอบคำถาม ให้ถูกมากกว่า 3 ข้อขึ้นไปถึงจะสามารถไป ฐานต่อไปได้");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(MapsActivity.this, QuestionActivity.class);
                intent.putExtra("Base", strMessage);
                intent.putExtra("Icon", intIcon);
                startActivity(intent);

                dialogInterface.dismiss();

            }
        });
        builder.show();

    }   // myAlert


    //นี่คือ เมทอด ที่หาระยะ ระหว่างจุด
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000; // หน่วยเป็น เมตร

        return (dist);
    }   // distance

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }   // deg2rad

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }   // rad2deg



    private int findIconMarker(String resultString) {

        int intIcon = R.drawable.kon48;
        int intKey = Integer.parseInt(resultString);

        switch (intKey) {

            case 0:
                intIcon = R.drawable.kon48;
                break;
            case 1:
                intIcon = R.drawable.rat48;
                break;
            case 2:
                intIcon = R.drawable.bird48;
                break;
            case 3:
                intIcon = R.drawable.doremon48;
                break;
            case 4:
                intIcon = R.drawable.nobita48;
                break;
            case 5:
                intIcon = R.drawable.base1;
                break;
            case 6:
                intIcon = R.drawable.base2;
                break;
            case 7:
                intIcon = R.drawable.base3;
                break;
            case 8:
                intIcon = R.drawable.base4;
                break;

        }   // switch

        return intIcon;
    }

    private void goToCenterMap() {

        try {

            LatLng centerLatLng = new LatLng(myLatADouble, myLngADouble);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 16));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }   // toToCenterMap

}   // Main Class