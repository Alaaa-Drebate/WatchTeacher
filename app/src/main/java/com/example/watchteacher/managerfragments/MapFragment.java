package com.example.watchteacher.managerfragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.ManagerRegisterActivity;
import com.example.watchteacher.R;
import com.example.watchteacher.adapters.TeachersAdapter;
import com.example.watchteacher.helper.GpsTracker;
import com.example.watchteacher.model.LatLon;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.SharedPrefManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static String TAG = "mapFragment";
    private static final float NORMAL_ZOOM = 15f;

    int myId;

    private double schoolLat;
    private double schoolLon;

    ArrayList<User> teachers;

    private GoogleMap mMap;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting manager id to get all teachers associated with him
        myId = SharedPrefManager.getInstance(requireContext()).getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        try {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true)
                .mapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(!SharedPrefManager.getInstance(requireContext()).isLocationSet()){
            handleLocation();
        }else{
            //in case we've already stored the school location
            LatLon latLon = SharedPrefManager.getInstance(requireContext()).getSchoolLocation();
            schoolLat = latLon.getLat();
            schoolLon = latLon.getLon();

        }

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(schoolLat, schoolLon);
        mMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("school location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, NORMAL_ZOOM));

        //1st : get teachers,
        //2nd : mark them on map :)
        getTeachersByManagerId();
    }


    public void markTeachersOnMap(){
        if(!teachers.isEmpty()) {
            for (User teacher : teachers) {
                LatLng teacherLocation = new LatLng(teacher.getLat(), teacher.getLon());
                mMap.addMarker(new MarkerOptions()
                        .position(teacherLocation)
                        .title(teacher.getName()))
                        .setIcon(BitmapFromVector(getContext(), R.drawable.ic_person));
            }
        }else
            Toast.makeText(requireContext(), "No assigned teachers yet!", Toast.LENGTH_SHORT).show();
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void handleLocation() {

        LayoutInflater factory = LayoutInflater.from(requireContext());
        final View view = factory.inflate(R.layout.location_confirmation_dialog, null);
        final AlertDialog locationConfirmationDialog = new AlertDialog.Builder(requireContext()).create();
        locationConfirmationDialog.setView(view);

        TextView yes = view.findViewById(R.id.yes_btn);
        TextView no = view.findViewById(R.id.no_btn);


        yes.setOnClickListener(v -> {

            GpsTracker tracker = new GpsTracker(requireContext());
            if(!tracker.canGetLocation()){
                tracker.showSettingsAlert();
            }else {
                Location location = tracker.getLocation();
                if(location != null){
                    SharedPrefManager.getInstance(requireContext()).setSchoolLocation(true);
                    schoolLat = location.getLatitude();
                    schoolLon = location.getLongitude();
                    SharedPrefManager.getInstance(requireContext()).setSchoolLocation(schoolLat, schoolLon);
                    Log.e("lat", schoolLat+"");
                    Log.e("lon", schoolLon+"");
                    getTeachersByManagerId();
                }else {
                    Toast.makeText(requireContext(), "there was problem with getting your location please try again", Toast.LENGTH_SHORT).show();
                }
                locationConfirmationDialog.dismiss();
            }

        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationConfirmationDialog.dismiss();
                Toast.makeText(requireContext(), "sorry no school location provided yet", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
        locationConfirmationDialog.show();

    }


    public void getTeachersByManagerId(){
        final ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        teachers = new ArrayList<>();

        String managerId = String.valueOf(SharedPrefManager.getInstance(requireContext()).getUserId());

        AndroidNetworking.get("http://nawar.scit.co/oup/school-reports/api/get-users.php?admin_id=" + managerId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();

                        try {
                            //converting response to json object
                            JSONObject obj = response;

                            //if no error in response
                            if (obj.getInt("status") == 1) {


                                //getting the user from the response
                                JSONArray reportsArray = obj.getJSONArray("data");
                                User user;
                                for(int i = 0; i < reportsArray.length(); i++){
                                    JSONObject userJson = reportsArray.getJSONObject(i);
                                    Log.e("teacherrrrrr: ", userJson.getString("name"));
                                    Log.e("teacherrrrrr: ", userJson.getString("id"));
                                    Log.e("teacherrrrrr: ", userJson.getString("phone_number"));
                                    Log.e("teacherrrrrr: ", userJson.getString("lat"));
                                    Log.e("teacherrrrrr: ", userJson.getString("lng"));

                                    user = new User(
                                            userJson.getString("name"),
                                            Integer.parseInt(userJson.getString("id")),
                                            userJson.getString("phone_number"),
                                            Double.parseDouble(userJson.getString("lat")),
                                            Double.parseDouble(userJson.getString("lng"))
                                    );
                                    teachers.add(user);
                                }
                                //after getting all of my teachers set them on map for me
                                markTeachersOnMap();

                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Toast.makeText(requireContext(), anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}