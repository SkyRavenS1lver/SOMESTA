package com.example.somesta.Activity;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import com.example.somesta.Marker.ClickableInfo;
import com.example.somesta.Marker.Perusahaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.R;
import com.example.somesta.SearchRecyclerView.SearchAdapter;
import com.example.somesta.databinding.ActivityMainBinding;
import com.example.somesta.seeAll.allAdapter;
import com.example.somesta.utility.GroupAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
    RecyclerView recyclerView;
    GroupAdapter groupAdapter;
    FlexboxLayoutManager layoutManager;

    private ActivityMainBinding binding;
    public static  MapView map = null;
    LocationManager locationManager;
    public HashSet<String> groupClicked = new HashSet<>();
    public static HashSet<String> temp = new HashSet<>();
    public HashSet<String> statusClicked = new HashSet<>();
    public HashSet<String> jenisClicked = new HashSet<>();
    public HashSet<String> kebutuhanClicked = new HashSet<>();
    public HashSet<String> lokasiClicked = new HashSet<>();
    public static ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();

    ArrayList<RecyclerView> recyclerViews = new ArrayList<>();
    FloatingActionButton filterBtn;
    RecyclerView searchRecyclerView;
    Set<String>  FBjenisArraySET;
    Set<String>  FBstatusArraySET;
    Set<String>  FBlokasiArraySET;
    Set<String>  FBkebutuhanArraySET;
    Set<String>  FBgroupArraySET;
    //Semua data perusahaan disimpan di array ini
    private ArrayList<Perusahaan> perusahaanArrayList = new ArrayList<>();
    //Array untuk menyimpan data yang ter-filter untuk searchView
    private ArrayList<Perusahaan> perusahaanArrayListFiltered = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // BottomSheetDialog
        BottomSheetDialog btmSheetDialog = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme);

        View btmSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.btm_sheet, (FrameLayout)findViewById(R.id.sheets));

        FrameLayout btmView = (FrameLayout) btmSheetView.findViewById(R.id.sheets);

        BottomSheetBehavior.from(btmView).setState(BottomSheetBehavior.STATE_EXPANDED);
        btmSheetDialog.setContentView(btmSheetView);

        //Creating 5 RV
        createRV(btmSheetView,R.id.rvGroup,groupClicked);
        createRV(btmSheetView,R.id.rvStatus,statusClicked);
        createRV(btmSheetView,R.id.rvLokasi,lokasiClicked);
        createRV(btmSheetView,R.id.rvKebutuhan,kebutuhanClicked);
        createRV(btmSheetView,R.id.rvJenis,jenisClicked);
        //making the reset click
        TextView reset = btmSheetView.findViewById(R.id.filterReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupClicked.clear();
                statusClicked.clear();
                jenisClicked.clear();
                kebutuhanClicked.clear();
                lokasiClicked.clear();
                for (int i = 0; i< recyclerViews.size();i++){
                    RecyclerView recyclerView = recyclerViews.get(i);
                    for (int k = 0; k < recyclerView.getChildCount(); k++) {
                        GroupAdapter.HolderData holder = (GroupAdapter.HolderData) recyclerView.findViewHolderForAdapterPosition(k);
                        holder.group.setChecked(false);
                    }
                }
            }
        });





        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Context ctx = this;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setClickable(true);





        //mylocation maker
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        IMapController mapController = map.getController();
        mapController.setZoom(17);
        GeoPoint startPoint = new GeoPoint(-7.795425625273463, 110.36488798392885);
        mapController.setCenter(startPoint);




        //Making the filter button
        Button filter_btn = btmSheetView.findViewById(R.id.buttonFiltering);
        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                btmSheetDialog.dismiss();
            }
        });

        //seeAll logic
//            BottomSheetDialog btmSheetDialog = new BottomSheetDialog(
//                    MainActivity.this, R.style.BottomSheetDialogTheme);
//            View btmSheetView = LayoutInflater.from(getApplicationContext())
//                    .inflate(R.layout.see_all_layout, (FrameLayout)findViewById(R.id.sheets));
//            TextView namaFilter = btmSheetView.findViewById(R.id.nama);
//            namaFilter.setText(nama);
//            FrameLayout btmView = (FrameLayout) btmSheetView.findViewById(R.id.sheets);
//            BottomSheetBehavior.from(btmView).setState(BottomSheetBehavior.STATE_EXPANDED);
//            btmSheetDialogs.setContentView(btmSheetView);
//
//            RecyclerView recyclerView = btmSheetView.findViewById(R.id.allRv);
//            List<String> listData = new ArrayList<>(set);
//            recyclerView.setLayoutManager(new LinearLayoutManager(btmSheetView.getContext()));
//            allAdapter allAdapter = new allAdapter(btmSheetView.getContext(), listData, grouped, nama);
//            recyclerView.setAdapter(allAdapter);
//            TextView resets = btmSheetView.findViewById(R.id.filterReset);
//            resets.setOnClickListener(new View.OnClickListener() {
//                                          @Override
//                                          public void onClick(View view) {
////                temporary.clear();
//                                              for (int k = 0; k < recyclerView.getChildCount(); k++) {
//                                                  allAdapter.HolderData holder = (com.example.somesta.seeAll.allAdapter.HolderData) recyclerView.findViewHolderForAdapterPosition(k);
//                                                  holder.group.setChecked(false);
//                                              }
//                                          }
//                                      }
//            );
//            Button btnFilter = btmSheetView.findViewById(R.id.buttonFiltering);
//            btnFilter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                HashSet<String> temp = new HashSet(temporary);
//                    if (nama.equals("Group")) {
////                    groupClicked.clear();groupClicked.addAll(temp);
//                        addingData((GroupAdapter) recyclerViews.get(0).getAdapter(),FBgroupArraySET,groupClicked);
//                        System.out.println(groupClicked);
//                        System.out.println("---");}
////                else if (nama.equals("Status")) {statusClicked.clear();statusClicked.addAll(temporary);
////                    addingData((GroupAdapter) recyclerViews.get(1).getAdapter(),FBstatusArraySET,statusClicked);}
////                else if (nama.equals("Lokasi")) {lokasiClicked.clear();lokasiClicked.addAll(temporary);
////                    addingData((GroupAdapter) recyclerViews.get(2).getAdapter(),FBlokasiArraySET,lokasiClicked);}
////                else if (nama.equals("Kebutuhan")) {kebutuhanClicked.clear();kebutuhanClicked.addAll(temporary);
////                    addingData((GroupAdapter) recyclerViews.get(3).getAdapter(),FBkebutuhanArraySET,kebutuhanClicked);}
////                else if (nama.equals("Jenis")) {jenisClicked.clear();jenisClicked.addAll(temporary);
////                    addingData((GroupAdapter) recyclerViews.get(4).getAdapter(),FBjenisArraySET,jenisClicked);}
//                    btmSheetDialogs.dismiss();
//                }
//
//            });
//            btmSheetDialogs.show();

        BottomSheetDialog btmSheetDialogGroup = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme);
        View btmSheetViewGroup = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.see_all_layout, (FrameLayout) findViewById(R.id.sheets2));
        TextView namaFilter = btmSheetViewGroup.findViewById(R.id.nama);
        namaFilter.setText("Group");
        FrameLayout btmViewGroup = (FrameLayout) btmSheetViewGroup.findViewById(R.id.sheets2);
        BottomSheetBehavior.from(btmViewGroup).setState(BottomSheetBehavior.STATE_EXPANDED);
        btmSheetDialogGroup.setContentView(btmSheetViewGroup);
        RecyclerView recyclerViewGroup = btmSheetViewGroup.findViewById(R.id.allRv);
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(btmSheetViewGroup.getContext()));
        allAdapter allAdapterGroup = new allAdapter(btmSheetViewGroup.getContext(), new ArrayList<>(), temp);
        recyclerViewGroup.setAdapter(allAdapterGroup);
        btmSheetViewGroup.findViewById(R.id.filterReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp.clear();
                for (int k = 0; k < recyclerViewGroup.getChildCount(); k++) {
                    allAdapter.HolderData holder = (com.example.somesta.seeAll.allAdapter.HolderData) recyclerViewGroup.findViewHolderForAdapterPosition(k);
                    holder.group.setChecked(false);
                }
            }
        });
        btmSheetViewGroup.findViewById(R.id.buttonFiltering).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                groupClicked.clear();
                groupClicked.addAll(new HashSet<>(temp));
//                groupClicked.clear();
////                groupClicked.addAll((HashSet<String>) temp.clone());
//                for (int k = 0; k < recyclerViewGroup.getChildCount(); k++) {
//                    allAdapter.HolderData holder = (com.example.somesta.seeAll.allAdapter.HolderData) recyclerViewGroup.findViewHolderForAdapterPosition(k);
//                    if (holder.group.isChecked()){
//                        groupClicked.add(holder.group.getText().toString());
//                    }
//                }
                addingData((GroupAdapter) recyclerViews.get(0).getAdapter(),FBgroupArraySET,groupClicked);
                System.out.println(temp);
                System.out.println("---");
                btmSheetDialogGroup.dismiss();
            }

        });



        btmSheetView.findViewById(R.id.allGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp.clear();
                temp.addAll(groupClicked);
                System.out.println(temp);
                System.out.println(groupClicked);
                System.out.println("########");
                allAdapterGroup.updateListData(new ArrayList<>(FBgroupArraySET));
                allAdapterGroup.notifyDataSetChanged();
                btmSheetDialogGroup.show();
//                checkBoxes(btmSheetView,FBgroupArraySET,groupClicked, "Group");
            }
        });
//        allJenis = btmSheetView.findViewById(R.id.allJenis);
//        allKebutuhan = btmSheetView.findViewById(R.id.allKebutuhan);
//        allLokasi = btmSheetView.findViewById(R.id.allLokasi);
//        allStatus = btmSheetView.findViewById(R.id.allStatus);
//
//        allJenis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkBoxes(btmSheetView,FBjenisArraySET,jenisClicked, "Jenis");
//            }
//        });
//        allKebutuhan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkBoxes(btmSheetView,FBkebutuhanArraySET,kebutuhanClicked, "Kebutuhan");
//            }
//        });
//        allLokasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkBoxes(btmSheetView,FBlokasiArraySET,lokasiClicked, "Lokasi");
//            }
//        });
//        allStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkBoxes(btmSheetView,FBstatusArraySET,statusClicked, "Status");
//            }
//        });

        //Firebase References and Data StringSets
        FirebaseDatabase firebaseDatabase;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("dbCustomer");
        FBgroupArraySET = new HashSet<String>(); //removes duplicated strings
        FBstatusArraySET = new HashSet<String>(); //removes duplicated strings
        FBlokasiArraySET = new HashSet<String>(); //removes duplicated strings
        FBkebutuhanArraySET = new HashSet<String>(); //removes duplicated strings
        FBjenisArraySET = new HashSet<String>(); //removes duplicated strings

        //Search Recyclerview
        SearchAdapter searchAdapter;
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchAdapter = new SearchAdapter(this,perusahaanArrayListFiltered);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchAdapter);

        //Search Bar
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchAdapter.notifyDataSetChanged();
                String searchWord = s;
                perusahaanArrayListFiltered.clear();
                for (Perusahaan perusahaan : perusahaanArrayList){
                    if(perusahaan.getNama().toLowerCase(Locale.ROOT).contains(searchWord.toLowerCase(Locale.ROOT))
                            && (groupClicked.size()==0 || groupClicked.contains(perusahaan.getGroup()))
                            && (statusClicked.size()==0 || statusClicked.contains(perusahaan.getStatus()))
                            && (lokasiClicked.size()==0 || lokasiClicked.contains(perusahaan.getTempat()))
                            && (kebutuhanClicked.size()==0 || kebutuhanClicked.contains(perusahaan.getKebutuhan()))
                            && (jenisClicked.size()==0 || jenisClicked.contains(perusahaan.getJenis()))){
                        perusahaanArrayListFiltered.add(perusahaan);
                    }
                    if (perusahaanArrayListFiltered.size() == 5){
                        break;
                    }
                }
                searchRecyclerView.setVisibility(View.VISIBLE);
                return false;
            }
        });


        //Firebase Database Data Grabber
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //resets all arrays on every update
                FBgroupArraySET.clear();
                FBstatusArraySET.clear();
                FBlokasiArraySET.clear();
                FBkebutuhanArraySET.clear();
                FBjenisArraySET.clear();
                perusahaanArrayList.clear();
                //Grab data
                for (DataSnapshot FBdata : snapshot.getChildren()){
                    String dilayani = FBdata.child("dilayani").getValue().toString();
                    String group = FBdata.child("group").getValue().toString();
                    String jenis = FBdata.child("jenis").getValue().toString();
                    String kebutuhan = FBdata.child("kebutuhan").getValue().toString();
                    Float lang = Float.parseFloat(FBdata.child("koor_latitude").getValue().toString());
                    Float longi =Float.parseFloat(FBdata.child("koor_longitude").getValue().toString());
                    GeoPoint point = new GeoPoint(lang,longi);
                    String lokasi = FBdata.child("lokasi").getValue().toString();
                    String nama = FBdata.child("nama").getValue().toString();
                    String pelayanan = FBdata.child("pelayanan").getValue().toString();
                    String penyalur = FBdata.child("penyalur").getValue().toString();
                    String status = FBdata.child("status").getValue().toString();
                    String tipeCustomer = FBdata.child("tipe_customer").getValue().toString();
                    FBgroupArraySET.add(group);
                    FBstatusArraySET.add(status);
                    FBlokasiArraySET.add(lokasi);
                    FBkebutuhanArraySET.add(kebutuhan);
                    FBjenisArraySET.add(jenis);
                    perusahaanArrayList.add(new Perusahaan(dilayani,group,jenis,kebutuhan,point,lokasi,nama,pelayanan,penyalur,status,tipeCustomer));
                }
                //Adding Data to List Data
                addingData((GroupAdapter) recyclerViews.get(0).getAdapter(),FBgroupArraySET,groupClicked);
                addingData((GroupAdapter) recyclerViews.get(1).getAdapter(),FBstatusArraySET,statusClicked);
                addingData((GroupAdapter) recyclerViews.get(2).getAdapter(),FBlokasiArraySET,lokasiClicked);
                addingData((GroupAdapter) recyclerViews.get(3).getAdapter(),FBkebutuhanArraySET,kebutuhanClicked);
                addingData((GroupAdapter) recyclerViews.get(4).getAdapter(),FBjenisArraySET,jenisClicked);

                //Adding marker
                addData();
                //Notify adapter, onDataChange works asynchronously
                if(searchAdapter != null){
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal Mendapatkan Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });

        // Pembuatan sheet dialog
        filterBtn = (FloatingActionButton) findViewById(R.id.filter);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btmSheetDialog.show();
            }
        });
        KeyboardVisibilityEvent.setEventListener(
                MainActivity.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (!isOpen){
                            searchRecyclerView.setVisibility(View.INVISIBLE);
                        }
                    }
                });


    }

    private void addData(){
        map.getOverlays().clear();
        markers.clear();
        if (groupClicked.size()+jenisClicked.size()+statusClicked.size()
                +lokasiClicked.size()+kebutuhanClicked.size() == 0){
        for (Perusahaan perusahaan : perusahaanArrayList){
            createMarker(perusahaan);}
        }
        else {
            for (Perusahaan perusahaan : perusahaanArrayList){
                if ((groupClicked.size()==0 || groupClicked.contains(perusahaan.getGroup())) &&
                    (statusClicked.size()==0 || statusClicked.contains(perusahaan.getStatus())) &&
                    (lokasiClicked.size()==0 || lokasiClicked.contains(perusahaan.getTempat())) &&
                    (kebutuhanClicked.size()==0 || kebutuhanClicked.contains(perusahaan.getKebutuhan())) &&
                    (jenisClicked.size()==0 || jenisClicked.contains(perusahaan.getJenis()))
                ){
                    createMarker(perusahaan);
                }
            }
        }
        for (int i=0;i < markers.size();i++){
            map.getOverlays().add(markers.get(i));
        }
    }

    private void createMarker(Perusahaan perusahaan){
        Marker marker = new Marker(map);
        marker.setPosition(perusahaan.getLocation());
        InfoWindow infoWindow = new ClickableInfo(R.layout.clickable_bubble, map,perusahaan);
        marker.setInfoWindow(infoWindow);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24));
        markers.add(marker);
    }
    private void addingData(GroupAdapter groupAdapters, Set<String> set, HashSet<String> grouped){
        List<String> listData = new ArrayList<>();
        int batas = grouped.size();
        final int batasan = 4;
        if (batas >= batasan){
            listData.addAll(grouped);
        }
        else {
            listData.addAll(grouped);
            ArrayList<String> tempData = new ArrayList<>(set);
            int i = 0 ;
            while (listData.size() < 4){
                if (i == tempData.size()){
                    break;
                }
                if (listData.contains(tempData.get(i))){
                i++;
                continue;}
                else {listData.add(tempData.get(i));i++;}


            }

            }
        System.out.println(listData);
        System.out.println("listData+++");
        System.out.println(grouped);
        System.out.println("grouped+++");

//            tempData.addAll(new ArrayList<>(set));
//            List<String> temporaryHash = new ArrayList<>(new HashSet<String>(tempData));
//            batas = temporaryHash.size();
//            if (batas > batasan){batas = batasan;}
//            for (int i = 0; i < batas; i++){
//                listData.add(temporaryHash.get(i));
//            }}
//            HashSet<String> tempData = new HashSet<String>();
//            tempData.addAll(new ArrayList<>(grouped));
//            tempData.addAll(new ArrayList<>(set));
//            List<String> temporaryHash = new ArrayList<>(tempData);
//            batas = temporaryHash.size();
//            if (batas > batasan){batas = batasan;}
//            for (int i = 0; i < batas; i++){
//                listData.add(temporaryHash.get(i));}
        groupAdapters.updateListData(listData);
        groupAdapters.notifyDataSetChanged();
//        groupAdapters.setListData(listData);
//        groupAdapters.notifyDataSetChanged();
    }

    //See all checkboxes
//    public void checkBoxes(View views, Set<String> set, HashSet<String> grouped, String nama){
//        BottomSheetDialog btmSheetDialogs = new BottomSheetDialog(
//                MainActivity.this, R.style.BottomSheetDialogTheme);
//        View btmSheetView = LayoutInflater.from(getApplicationContext())
//                .inflate(R.layout.see_all_layout, (FrameLayout)findViewById(R.id.sheets));
//        TextView namaFilter = btmSheetView.findViewById(R.id.nama);
//        namaFilter.setText(nama);
//        FrameLayout btmView = (FrameLayout) btmSheetView.findViewById(R.id.sheets);
//        BottomSheetBehavior.from(btmView).setState(BottomSheetBehavior.STATE_EXPANDED);
//        btmSheetDialogs.setContentView(btmSheetView);
//
//        RecyclerView recyclerView = btmSheetView.findViewById(R.id.allRv);
//        List<String> listData = new ArrayList<>(set);
//        recyclerView.setLayoutManager(new LinearLayoutManager(btmSheetView.getContext()));
//        allAdapter allAdapter = new allAdapter(btmSheetView.getContext(), listData, grouped, nama);
//        recyclerView.setAdapter(allAdapter);
//        TextView resets = btmSheetView.findViewById(R.id.filterReset);
//        resets.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                temporary.clear();
//                for (int k = 0; k < recyclerView.getChildCount(); k++) {
//                    allAdapter.HolderData holder = (com.example.somesta.seeAll.allAdapter.HolderData) recyclerView.findViewHolderForAdapterPosition(k);
//                    holder.group.setChecked(false);
//                }
//            }
//        }
//        );
//        Button btnFilter = btmSheetView.findViewById(R.id.buttonFiltering);
//        btnFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                HashSet<String> temp = new HashSet(temporary);
//                if (nama.equals("Group")) {
////                    groupClicked.clear();groupClicked.addAll(temp);
//                    addingData((GroupAdapter) recyclerViews.get(0).getAdapter(),FBgroupArraySET,groupClicked);
//                    System.out.println(groupClicked);
//                    System.out.println("---");}
////                else if (nama.equals("Status")) {statusClicked.clear();statusClicked.addAll(temporary);
////                    addingData((GroupAdapter) recyclerViews.get(1).getAdapter(),FBstatusArraySET,statusClicked);}
////                else if (nama.equals("Lokasi")) {lokasiClicked.clear();lokasiClicked.addAll(temporary);
////                    addingData((GroupAdapter) recyclerViews.get(2).getAdapter(),FBlokasiArraySET,lokasiClicked);}
////                else if (nama.equals("Kebutuhan")) {kebutuhanClicked.clear();kebutuhanClicked.addAll(temporary);
////                    addingData((GroupAdapter) recyclerViews.get(3).getAdapter(),FBkebutuhanArraySET,kebutuhanClicked);}
////                else if (nama.equals("Jenis")) {jenisClicked.clear();jenisClicked.addAll(temporary);
////                    addingData((GroupAdapter) recyclerViews.get(4).getAdapter(),FBjenisArraySET,jenisClicked);}
//                btmSheetDialogs.dismiss();
//            }
//
//        });
//        btmSheetDialogs.show();
//
//    }
    public void createRV(View view, int id, HashSet<String> group){
        RecyclerView recyclerView = view.findViewById(id);
        GroupAdapter groupAdapter = new GroupAdapter(recyclerView.getContext(), new ArrayList<>(), group);
        recyclerView.setAdapter(groupAdapter);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(recyclerView.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViews.add(recyclerView);
    }

}
