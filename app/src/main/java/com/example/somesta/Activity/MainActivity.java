package com.example.somesta.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.somesta.Marker.ClickableInfo;
import com.example.somesta.Marker.Perusahaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.R;
import com.example.somesta.SearchRecyclerView.SearchAdapter;
import com.example.somesta.StaticData.Model;
import com.example.somesta.databinding.ActivityMainBinding;
import com.example.somesta.seeAll.allAdapter;

import com.example.somesta.utility.GroupAdapter;
import com.example.somesta.utility.ResultAdapter;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.mapsforge.map.rendertheme.renderinstruction.Line;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static TextView reset;
    public static Button filterResets;
    public static IMapController mapController;
    public static BottomSheetDialog viewListDialog;
    Marker markerCurrent;
    private ExtendedFloatingActionButton viewList;
    private String penanda;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private double currentLatitude,currentLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 1000;

    public static MapView map = null;

    public static ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
    ArrayList<RecyclerView> recyclerViews = new ArrayList<>();
    RecyclerView searchRecyclerView;
    public static BottomSheetDialog dialogPerusahaan;
    public static View ViewPerusahaan;
    public static FrameLayout btmView;

    private FloatingActionButton settings;

    public static FloatingSearchView searchView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Logout");
                alert.setMessage("Apakah anda ingin logout dari aplikasi?");
                alert.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Berhasil Logout", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();
            }
        });

        viewList = findViewById(R.id.viewList);
        viewList.setVisibility(View.INVISIBLE);

        //Marker Onclick sheet dialog
        dialogPerusahaan = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme);
        ViewPerusahaan = LayoutInflater.from(this)
                .inflate(R.layout.info_perusahaan, (FrameLayout) dialogPerusahaan.findViewById(R.id.sheets2));
        btmView = (FrameLayout) ViewPerusahaan.findViewById(R.id.sheets2);
        Context ctx = this;

        //Creating Current Location
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setClickable(true);
        mapController = map.getController();
        mapController.setZoom(17);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation(this);
        markerCurrent = new Marker(map);
        markerCurrent.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        markerCurrent.setIcon(getResources().getDrawable(R.drawable.current_loc_marker));
        markerCurrent.setInfoWindow(new InfoWindow(R.layout.clickable_bubble,map) {
            @Override
            public void onOpen(Object item) {
            }
            @Override
            public void onClose() {
            }
        });
        // BottomSheetDialog
        BottomSheetDialog btmSheetDialog = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme);

        View btmSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.btm_sheet, (LinearLayout) btmSheetDialog.findViewById(R.id.linearLayoutFilter));

//        FrameLayout btmView = (FrameLayout) btmSheetView.findViewById(R.id.sheets);

//        BottomSheetBehavior.from(btmView).setState(BottomSheetBehavior.STATE_COLLAPSED);
        btmSheetDialog.setContentView(btmSheetView);

        //ViewListDialog
        viewListDialog = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme);
        View viewListDialogView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.filter_result, (LinearLayout) btmSheetDialog.findViewById(R.id.sheets2));
        viewListDialog.setContentView(viewListDialogView);
        RecyclerView recyclerViewResult = viewListDialogView.findViewById(R.id.filterResult);
        recyclerViewResult.setLayoutManager(new LinearLayoutManager(btmSheetDialog.getContext()));
        ResultAdapter adapterResult = new ResultAdapter(this,new ArrayList<Perusahaan>());
        recyclerViewResult.setAdapter(adapterResult);
        viewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewListDialog.show();
            }
        });
        //Creating 5 RV
        createRV(btmSheetView, R.id.rvGroup, Model.groupClicked);
        createRV(btmSheetView, R.id.rvStatus, Model.statusClicked);
        createRV(btmSheetView, R.id.rvLokasi, Model.lokasiClicked);
        createRV(btmSheetView, R.id.rvKebutuhan, Model.kebutuhanClicked);
        createRV(btmSheetView, R.id.rvJenis, Model.jenisClicked);

        reset = btmSheetView.findViewById(R.id.filterReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.groupClicked.clear();
                Model.statusClicked.clear();
                Model.jenisClicked.clear();
                Model.kebutuhanClicked.clear();
                Model.lokasiClicked.clear();
                for (int i = 0; i < recyclerViews.size(); i++) {
                    RecyclerView recyclerView = recyclerViews.get(i);
                    for (int k = 0; k < recyclerView.getChildCount(); k++) {
                        GroupAdapter.HolderData holder = (GroupAdapter.HolderData) recyclerView.findViewHolderForAdapterPosition(k);
                        holder.group.setChecked(false);
                    }
                }
            }
        });
        //Making the filter button
//        Button filteringBtn = btmSheetView.findViewById(R.id.buttonFiltering);
        filterResets = btmSheetView.findViewById(R.id.buttonFiltering);
        filterResets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Resetting Recycler View
                addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), Model.FBgroupArraySET, Model.groupClicked);
                addingData((GroupAdapter) recyclerViews.get(1).getAdapter(), Model.FBstatusArraySET, Model.statusClicked);
                addingData((GroupAdapter) recyclerViews.get(2).getAdapter(), Model.FBlokasiArraySET, Model.lokasiClicked);
                addingData((GroupAdapter) recyclerViews.get(3).getAdapter(), Model.FBkebutuhanArraySET, Model.kebutuhanClicked);
                addingData((GroupAdapter) recyclerViews.get(4).getAdapter(), Model.FBjenisArraySET, Model.jenisClicked);
                updateMarker();
                //View List
                if(Model.getPerusahaanArrayListFiltered().size() == 0){
                    ((TextView)viewListDialogView.findViewById(R.id.textResult)).setText(getResources().getString(R.string.Response_None));
                }
                else {((TextView)viewListDialogView.findViewById(R.id.textResult)).setText(getResources().getString(R.string.Response_OK));}
                if(Model.groupClicked.size()+Model.statusClicked.size()+Model.lokasiClicked.size()+Model.kebutuhanClicked.size()+Model.jenisClicked.size()!=0){viewList.setVisibility(View.VISIBLE);}
                else {viewList.setVisibility(View.INVISIBLE);}
                adapterResult.setListData(new ArrayList<>(Model.getPerusahaanArrayListFiltered()));
                adapterResult.notifyDataSetChanged();
                btmSheetDialog.dismiss();
            }
        });


        //BottomSheetialog for see All
        BottomSheetDialog btmSheetDialogGroup = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme);
        View btmSheetViewGroup = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.see_all_layout, (LinearLayout) btmSheetDialogGroup.findViewById(R.id.linearLayoutFilter));

//        FrameLayout btmViewGroup = (FrameLayout) btmSheetViewGroup.findViewById(R.id.sheets2);
//        BottomSheetBehavior.from(btmViewGroup).setState(BottomSheetBehavior.STATE_EXPANDED);
        btmSheetDialogGroup.setContentView(btmSheetViewGroup);
        RecyclerView recyclerViewGroup = btmSheetViewGroup.findViewById(R.id.allRv);
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(btmSheetViewGroup.getContext()));
        allAdapter allAdapterGroup = new allAdapter(btmSheetViewGroup.getContext(), new ArrayList<>(), Model.temp);
        recyclerViewGroup.setAdapter(allAdapterGroup);

        btmSheetViewGroup.findViewById(R.id.filterReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.temp.clear();
                for (int k = 0; k < recyclerViewGroup.getChildCount(); k++) {
                    allAdapter.HolderData holder = (com.example.somesta.seeAll.allAdapter.HolderData) recyclerViewGroup.findViewHolderForAdapterPosition(k);
                    holder.group.setChecked(false);
                }
            }
        });

        btmSheetViewGroup.findViewById(R.id.buttonFiltering).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (penanda) {
                    case "Group":
                        Model.groupClicked.clear();
                        Model.groupClicked.addAll(new HashSet<>(Model.temp));
                        addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), Model.FBgroupArraySET, Model.groupClicked);
                        break;
                    case "Status":
                        Model.statusClicked.clear();
                        Model.statusClicked.addAll(new HashSet<>(Model.temp));
                        addingData((GroupAdapter) recyclerViews.get(1).getAdapter(), Model.FBstatusArraySET, Model.statusClicked);
                        break;
                    case "Lokasi":
                        Model.lokasiClicked.clear();
                        Model.lokasiClicked.addAll(new HashSet<>(Model.temp));
                        addingData((GroupAdapter) recyclerViews.get(2).getAdapter(), Model.FBlokasiArraySET, Model.lokasiClicked);
                        break;
                    case "Kebutuhan":
                        Model.kebutuhanClicked.clear();
                        Model.kebutuhanClicked.addAll(new HashSet<>(Model.temp));
                        addingData((GroupAdapter) recyclerViews.get(3).getAdapter(), Model.FBkebutuhanArraySET, Model.kebutuhanClicked);
                        break;
                    case "Jenis":
                        Model.jenisClicked.clear();
                        Model.jenisClicked.addAll(new HashSet<>(Model.temp));
                        addingData((GroupAdapter) recyclerViews.get(4).getAdapter(), Model.FBjenisArraySET, Model.jenisClicked);
                        break;
                }

                btmSheetDialogGroup.dismiss();
            }

        });

        //See all on click
        btmSheetView.findViewById(R.id.allGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penanda = "Group";
                updateSeeAll(btmSheetViewGroup, allAdapterGroup, penanda, Model.FBgroupArraySET, Model.groupClicked);
                btmSheetDialogGroup.show();
            }
        });
        btmSheetView.findViewById(R.id.allStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penanda = "Status";
                updateSeeAll(btmSheetViewGroup, allAdapterGroup, penanda, Model.FBstatusArraySET, Model.statusClicked);
                btmSheetDialogGroup.show();
            }
        });
        btmSheetView.findViewById(R.id.allLokasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penanda = "Lokasi";
                updateSeeAll(btmSheetViewGroup, allAdapterGroup, penanda, Model.FBlokasiArraySET, Model.lokasiClicked);
                btmSheetDialogGroup.show();
            }
        });
        btmSheetView.findViewById(R.id.allKebutuhan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penanda = "Kebutuhan";
                updateSeeAll(btmSheetViewGroup, allAdapterGroup, penanda, Model.FBkebutuhanArraySET, Model.kebutuhanClicked);
                btmSheetDialogGroup.show();
            }
        });
        btmSheetView.findViewById(R.id.allJenis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penanda = "Jenis";
                updateSeeAll(btmSheetViewGroup, allAdapterGroup, penanda, Model.FBjenisArraySET, Model.jenisClicked);
                btmSheetDialogGroup.show();
            }
        });
        findViewById(R.id.location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation(ctx);
            }
        });

        //Firebase References and Data StringSets
        FirebaseDatabase firebaseDatabase;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("dbCustomer");

        //Search Recyclerview
        SearchAdapter searchAdapter;
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchAdapter = new SearchAdapter(this, Model.getPerusahaanArrayListFiltered());
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchAdapter);

        //Search Bar
        searchView = findViewById(R.id.searchView);
//        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                searchAdapter.notifyDataSetChanged();
                String searchWord = newQuery;
                Model.getPerusahaanArrayListFiltered().clear();
                for (Perusahaan perusahaan : Model.getPerusahaanArrayList()) {
                    if (perusahaan.getNama().toLowerCase(Locale.ROOT).contains(searchWord.toLowerCase(Locale.ROOT))
                            ) {
                        Model.getPerusahaanArrayListFiltered().add(perusahaan);
                    }
                    if (Model.getPerusahaanArrayListFiltered().size() == 5) {
                        break;
                    }
                }
                searchRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Model.getPerusahaanArrayListFiltered().clear();
                } else {
                    Model.getPerusahaanArrayListFiltered().clear();
                }
            }
        });


//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                searchAdapter.notifyDataSetChanged();
//                String searchWord = s;
//                perusahaanArrayListFiltered.clear();
//                for (Perusahaan perusahaan : perusahaanArrayList){
//                    if(perusahaan.getNama().toLowerCase(Locale.ROOT).contains(searchWord.toLowerCase(Locale.ROOT))
//                    && (groupClicked.size()==0 || groupClicked.contains(perusahaan.getGroup()))
//                    && (statusClicked.size()==0 || statusClicked.contains(perusahaan.getStatus()))
//                    && (lokasiClicked.size()==0 || lokasiClicked.contains(perusahaan.getTempat()))
//                    && (kebutuhanClicked.size()==0 || kebutuhanClicked.contains(perusahaan.getKebutuhan()))
//                    && (jenisClicked.size()==0 || jenisClicked.contains(perusahaan.getJenis()))){
//                        perusahaanArrayListFiltered.add(perusahaan);
//                    }
//                }
//                searchRecyclerView.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });
//        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(!b){searchRecyclerView = null;}
//            }
//        });
        //Firebase Database Data Grabber
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //resets all arrays on every update
                Model.FBgroupArraySET.clear();
                Model.FBstatusArraySET.clear();
                Model.FBlokasiArraySET.clear();
                Model.FBkebutuhanArraySET.clear();
                Model.FBjenisArraySET.clear();
                Model.getPerusahaanArrayList().clear();
                //Grab data
                for (DataSnapshot FBdata : snapshot.getChildren()) {
                    String dilayani = FBdata.child("dilayani").getValue().toString();
                    String group = FBdata.child("group").getValue().toString();
                    String jenis = FBdata.child("jenis").getValue().toString();
                    String kebutuhan = FBdata.child("kebutuhan").getValue().toString();
                    Float lang = Float.parseFloat(FBdata.child("koor_latitude").getValue().toString());
                    Float longi = Float.parseFloat(FBdata.child("koor_longitude").getValue().toString());
                    GeoPoint point = new GeoPoint(lang, longi);
                    String lokasi = FBdata.child("lokasi").getValue().toString();
                    String nama = FBdata.child("nama").getValue().toString();
                    String pelayanan = FBdata.child("pelayanan").getValue().toString();
                    String penyalur = FBdata.child("penyalur").getValue().toString();
                    String status = FBdata.child("status").getValue().toString();
                    String tipeCustomer = FBdata.child("tipe_customer").getValue().toString();
                    Model.FBgroupArraySET.add(group);
                    Model.FBstatusArraySET.add(status);
                    Model.FBlokasiArraySET.add(lokasi);
                    Model.FBkebutuhanArraySET.add(kebutuhan);
                    Model.FBjenisArraySET.add(jenis);
                    Model.getPerusahaanArrayList().add(new Perusahaan(dilayani, group, jenis, kebutuhan, point, lokasi, nama, pelayanan, penyalur, status, tipeCustomer));
                }
                //Adding Data to List Data
                addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), Model.FBgroupArraySET, Model.groupClicked);
                addingData((GroupAdapter) recyclerViews.get(1).getAdapter(), Model.FBstatusArraySET, Model.statusClicked);
                addingData((GroupAdapter) recyclerViews.get(2).getAdapter(), Model.FBlokasiArraySET, Model.lokasiClicked);
                addingData((GroupAdapter) recyclerViews.get(3).getAdapter(), Model.FBkebutuhanArraySET, Model.kebutuhanClicked);
                addingData((GroupAdapter) recyclerViews.get(4).getAdapter(), Model.FBjenisArraySET, Model.jenisClicked);

                //Adding marker
                addData();
                //Notify adapter, onDataChange works asynchronously
                if (searchAdapter != null) {
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal Mendapatkan Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });

        // Pembuatan sheet dialog
        FloatingActionButton filter_btn = (FloatingActionButton) findViewById(R.id.filter);
        filter_btn.setOnClickListener(new View.OnClickListener() {
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
                        if (!isOpen) {
                            searchRecyclerView.setVisibility(View.INVISIBLE);
                        }
                    }
                });


    }

    private void getLocation(Context ctx) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);

        } else {
            // already permission granted
            // get location here
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    GeoPoint currentLoc = new GeoPoint(currentLatitude,currentLongitude);
                    markerCurrent.setPosition(currentLoc);
                    mapController.setCenter(currentLoc);
                    mapController.setZoom(17);
                }
            });
        }
    }

//    private void getLocation() {
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.getCurrentLocation(LocationServices);
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
//
//        }
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);}

//    private void addData() {
//        map.getOverlays().clear();
//        markers.clear();
//        if (groupClicked.size() + jenisClicked.size() + statusClicked.size()
//                + lokasiClicked.size() + kebutuhanClicked.size() == 0) {
//            for (Perusahaan perusahaan : perusahaanArrayList) {
//                createMarker(perusahaan);
//            }
//        } else {
//            for (Perusahaan perusahaan : perusahaanArrayList) {
//                if ((groupClicked.size() == 0 || groupClicked.contains(perusahaan.getGroup())) &&
//                        (statusClicked.size() == 0 || statusClicked.contains(perusahaan.getStatus())) &&
//                        (lokasiClicked.size() == 0 || lokasiClicked.contains(perusahaan.getTempat())) &&
//                        (kebutuhanClicked.size() == 0 || kebutuhanClicked.contains(perusahaan.getKebutuhan())) &&
//                        (jenisClicked.size() == 0 || jenisClicked.contains(perusahaan.getJenis()))
//                ) {
//                    createMarker(perusahaan);
//                }
//            }
//        }
//        for (int i = 0; i < markers.size(); i++) {
//            map.getOverlays().add(markers.get(i));
//        }
//    }
    private void updateMarker(){
        Model.getPerusahaanArrayListFiltered().clear();
        for (int i = 0; i < markers.size(); i++) {
            if (!((Model.groupClicked.size() == 0 || Model.groupClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getGroup())) &&
                    (Model.statusClicked.size() == 0 || Model.statusClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getStatus())) &&
                    (Model.lokasiClicked.size() == 0 || Model.lokasiClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getTempat())) &&
                    (Model.kebutuhanClicked.size() == 0 || Model.kebutuhanClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getKebutuhan())) &&
                    (Model.jenisClicked.size() == 0 || Model.jenisClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getJenis()))
            )) {markers.get(i).setIcon(getResources().getDrawable(R.drawable.location_off));}
            else {markers.get(i).setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24)); Model.getPerusahaanArrayListFiltered().add(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan());}}
    }
    private void addData() {
            map.getOverlays().clear();
            map.getOverlays().add(markerCurrent);
            for (Perusahaan perusahaan : Model.getPerusahaanArrayList()) {
                createMarker(perusahaan);
            }
//        for (int i = 0; i < markers.size(); i++) {
//            map.getOverlays().add(markers.get(i));
//        }

//        else {
//            for (int i = 0; i < markers.size(); i++) {
//                if (!((groupClicked.size() == 0 || groupClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getGroup())) &&
//                        (statusClicked.size() == 0 || statusClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getStatus())) &&
//                        (lokasiClicked.size() == 0 || lokasiClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getTempat())) &&
//                        (kebutuhanClicked.size() == 0 || kebutuhanClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getKebutuhan())) &&
//                        (jenisClicked.size() == 0 || jenisClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getJenis()))
//                )) {markers.get(i).setIcon(getResources().getDrawable(R.drawable.location_off));}
//                else {markers.get(i).setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24));}
//            }
//        }
    }

//    private void createMarker(Perusahaan perusahaan) {
//        Marker marker = new Marker(map);
//        marker.setPosition(perusahaan.getLocation());
//        InfoWindow infoWindow = new ClickableInfo(R.layout.clickable_bubble, map, perusahaan);
//        marker.setInfoWindow(infoWindow);
//        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
//        marker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24));
//        markers.add(marker);
//    }
    private void createMarker(Perusahaan perusahaan) {
        Marker marker = new Marker(map);
        marker.setPosition(perusahaan.getLocation());
        InfoWindow infoWindow = new ClickableInfo(R.layout.clickable_bubble, map, perusahaan);
        marker.setInfoWindow(infoWindow);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24));
        markers.add(marker);
        map.getOverlays().add(marker);
    }

    public void createRV(View view, int id, HashSet<String> group) {
        RecyclerView recyclerView = view.findViewById(id);
        GroupAdapter groupAdapter = new GroupAdapter(recyclerView.getContext(), new ArrayList<>(), group);
        recyclerView.setAdapter(groupAdapter);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(recyclerView.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViews.add(recyclerView);
    }

    public void updateSeeAll(View btmSheetViewGroup, allAdapter allAdapterGroup, String penanda, Set<String> set, HashSet<String> grouped) {
        Model.temp.clear();
        Model.temp.addAll(grouped);
        TextView namaFilter = btmSheetViewGroup.findViewById(R.id.nama);
        namaFilter.setText(penanda);
        allAdapterGroup.setListData(new ArrayList<>(set));
        allAdapterGroup.notifyDataSetChanged();
    }

    private void addingData(GroupAdapter groupAdapters, Set<String> set, HashSet<String> grouped) {
        List<String> listData = new ArrayList<>();
        int batas = grouped.size();
        final int batasan = 4;
        if (batas >= batasan) {
            listData.addAll(grouped);
        } else {
            listData.addAll(grouped);
            ArrayList<String> tempData = new ArrayList<>(set);
            int i = 0;
            while (listData.size() < 4) {
                if (i == tempData.size()) {
                    break;
                }
                if (!listData.contains(tempData.get(i))) {
                    listData.add(tempData.get(i));
                }
                i++;
            }
        }
        groupAdapters.setListData(listData);
        groupAdapters.notifyDataSetChanged();
    }
}
