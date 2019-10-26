package com.example.mapgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static android.media.CamcorderProfile.get;

public class historyView extends AppCompatActivity  {


    private MapView mMapView;
    private LinearLayout LinearLayoutHistoryMaps;
    private RelativeLayout RelativeLayoutHistoryMaps;
    private Button BtnBack;
    private String TAG = "MapViewFragment";
    ArrayList<PolylineInfo> PolylineInfoList = new ArrayList<PolylineInfo>();
    private FirebaseAuth auth;
    FirebaseUser firebaseUser;
    List<String> PolylineData = new ArrayList<>();
    List<List> PolylineDataListArray = new ArrayList<>();
//AppCompatActivity

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        getHistory();


        setContentView(R.layout.activity_history_view);
        LinearLayoutHistoryMaps = findViewById(R.id.LinearLayoutHistoryMaps);
        BtnBack = findViewById(R.id.BtnBack);
        //RelativeLayoutHistoryMaps = findViewById(R.id.RelativeLayoutHistoryMaps);




        //Fragment mFragment = new FragmentGoogleMapsPreview();
        //getSupportFragmentManager().beginTransaction().replace(R.id.RelativeLayoutHistoryMaps, mFragment).commit();





        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void getHistory(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Trip");


        myRef.child(firebaseUser.getUid()).orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                PolylineInfoList.clear();
                PolylineDataListArray.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    PolylineData = new ArrayList<>();
                    String key = postSnapshot.getKey();
                    List<String> ListofValues = null;
                    PolylineInfo polylineInfo = new PolylineInfo();
                    //PolylineInfo polylineInfo = postSnapshot.getValue(PolylineInfo.class);
                    //PolylineInfoList.add(polylineInfo);

                    for (DataSnapshot postSnapshotList: dataSnapshot.child(key).child("polylineDataPointGoogleMap").getChildren()) {
                        Map<String, String> map = (Map<String, String>) postSnapshotList.getValue();
                        PolylineData.add(String.valueOf(map.get("latitude")).toString());
                        PolylineData.add(String.valueOf(map.get("longitude")).toString());
                    }
                    PolylineDataListArray.add(PolylineData);
                    Map<String, String> map = (Map<String, String>) postSnapshot.getValue();


                    polylineInfo.setUserEndTime(map.get("userEndTime").toString());
                    polylineInfo.setUserStartTime(map.get("userStartTime").toString());
                    //ArrayList lat = map.get("latitude");
                    polylineInfo.setPolylineDataPointGoogleMap(PolylineData);
                    PolylineInfoList.add(polylineInfo);

                }

                if(PolylineInfoList.size()>0){
                    for (int i = 0; i < PolylineInfoList.size(); i++) {
                        FragmentGoogleMapsPreview fragmentGoogleMapsPreview = new FragmentGoogleMapsPreview(PolylineInfoList.get(i), PolylineDataListArray.get(i), i);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.LinearLayoutHistoryMaps, fragmentGoogleMapsPreview);
                        fragmentTransaction.commit();
                    }

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //toastMsg(databaseError.getMessage());
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }




}
