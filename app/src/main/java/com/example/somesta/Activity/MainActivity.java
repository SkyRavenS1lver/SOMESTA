package com.example.somesta.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.R;
import com.example.somesta.SearchRecyclerView.SearchAdapter;
import com.example.somesta.seeAll.allAdapter;
import com.example.somesta.utility.GroupAdapter;
import com.example.somesta.utility.ResultAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public double kebutuhanMin;
    public double kebutuhanMax;
    public double persenMin;
    public double persenMax;
    String hMin;
    String hMax;
    String pMin;
    String pMax;

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
    public HashSet<String> groupClicked = new HashSet<>();
    public static HashSet<String> temp = new HashSet<>();
    public HashSet<String> statusClicked = new HashSet<>();
    public HashSet<String> jenisClicked = new HashSet<>();
//    public HashSet<Double> kebutuhanClicked = new HashSet<>();
    public HashSet<String> lokasiClicked = new HashSet<>();
    public HashSet<String> layananClicked = new HashSet<>();
//    public HashSet<Double> marketShareFilter = new HashSet<>();
    public HashSet<String> tipeCustClicked = new HashSet<>();
    public static ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<RecyclerView> recyclerViews = new ArrayList<>();
    RecyclerView searchRecyclerView;
    Set<String> FBjenisArraySET;
    Set<String> FBstatusArraySET;
    Set<String> FBlokasiArraySET;
    Set<String> FBkebutuhanArraySET;
    Set<String> FBgroupArraySET;
    Set<String> FBlayananArraySET;
    Set<String> FBmarketShareArraySET;
    Set<String> FBtipeCustArraySET;
    public static BottomSheetDialog dialogPerusahaan;
    public static View ViewPerusahaan;
    public static FrameLayout btmView;

    public static FloatingSearchView searchView;


    //Semua data perusahaan disimpan di array ini
    private ArrayList<Perusahaan> perusahaanArrayList = new ArrayList<>();
    //Array untuk menyimpan data yang ter-filter untuk searchView
    private ArrayList<Perusahaan> perusahaanArrayListFiltered = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewList = findViewById(R.id.viewList);
        viewList.setVisibility(View.GONE);

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
                .inflate(R.layout.filter_result, (LinearLayout) viewListDialog.findViewById(R.id.sheets2));
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

        //Creating RV
        createRV(btmSheetView, R.id.rvGroup, groupClicked);
        createRV(btmSheetView, R.id.rvStatus, statusClicked);
        createRV(btmSheetView, R.id.rvLokasi, lokasiClicked);
        createRV(btmSheetView, R.id.rvJenis, jenisClicked);
        createRV(btmSheetView, R.id.rvLayanan, layananClicked);
        createRV(btmSheetView, R.id.rvTipe, tipeCustClicked);

        TextView reset = btmSheetView.findViewById(R.id.filterReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllData();
//                groupClicked.clear();
//                statusClicked.clear();
//                jenisClicked.clear();
//                kebutuhanClicked.clear();
//                lokasiClicked.clear();
//                marketShareFilter.clear();
//                layananClicked.clear();
//                tipeCustClicked.clear();
                for (int i = 0; i < recyclerViews.size(); i++) {
                    RecyclerView recyclerView = recyclerViews.get(i);
                    for (int k = 0; k < recyclerView.getChildCount(); k++) {
                        GroupAdapter.HolderData holder = (GroupAdapter.HolderData) recyclerView.findViewHolderForAdapterPosition(k);
                        holder.group.setChecked(false);
                    }
                }
                ((EditText) btmSheetView.findViewById(R.id.hargaMinimum)).setText("");
                ((EditText) btmSheetView.findViewById(R.id.hargaMaximum)).setText("");
                ((EditText) btmSheetView.findViewById(R.id.persenMinimum)).setText("");
                ((EditText) btmSheetView.findViewById(R.id.persenMaximum)).setText("");
            }
        });
        //Making the filter button
//        Button filteringBtn = btmSheetView.findViewById(R.id.buttonFiltering);
        btmSheetView.findViewById(R.id.buttonFiltering).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Resetting Recycler View
                addAllProcess();
                hMin = ((EditText)btmSheetView.findViewById(R.id.hargaMinimum)).getText().toString();
                hMax = ((EditText)btmSheetView.findViewById(R.id.hargaMaximum)).getText().toString();
                pMin = ((EditText)btmSheetView.findViewById(R.id.persenMinimum)).getText().toString();
                pMax = ((EditText)btmSheetView.findViewById(R.id.persenMaximum)).getText().toString();
                if (hMin.equals("")){kebutuhanMin = 0;}else {kebutuhanMin = Double.parseDouble(hMin);}
                if (hMax.equals("")){kebutuhanMax = 999999999999d;}else {kebutuhanMax = Double.parseDouble(hMax);}
                if (pMin.equals("")){persenMin = 0;}else {persenMin = Double.parseDouble(pMin);}
                if (pMax.equals("")){persenMax = 100000;}else { persenMax = Double.parseDouble(pMax);}
                System.out.println("+++++ "+kebutuhanMin);
                System.out.println("+++++ "+kebutuhanMax);
                System.out.println("+++++ "+persenMin);
                System.out.println("+++++ "+persenMax);
//                addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), FBgroupArraySET, groupClicked);
//                addingData((GroupAdapter) recyclerViews.get(1).getAdapter(), FBstatusArraySET, statusClicked);
//                addingData((GroupAdapter) recyclerViews.get(2).getAdapter(), FBlokasiArraySET, lokasiClicked);
//                addingData((GroupAdapter) recyclerViews.get(3).getAdapter(), FBjenisArraySET, jenisClicked);
//                addingData((GroupAdapter) recyclerViews.get(4).getAdapter(), FBlayananArraySET, layananClicked);
//                addingData((GroupAdapter) recyclerViews.get(5).getAdapter(), FBtipeCustArraySET, tipeCustClicked);
                updateMarker();
                //View List
                if(perusahaanArrayListFiltered.size() == 0){
                    ((TextView)viewListDialogView.findViewById(R.id.textResult)).setText(getResources().getString(R.string.Response_None));
                }
                else {((TextView)viewListDialogView.findViewById(R.id.textResult)).setText(getResources().getString(R.string.Response_OK));}
                if(groupClicked.size()+statusClicked.size()+lokasiClicked.size()+layananClicked.size()+jenisClicked.size()+tipeCustClicked.size()!=0 ||
                        (!(pMin.equals("") && pMax.equals("") && hMin.equals("")&& hMax.equals(""))))
                {viewList.setVisibility(View.VISIBLE);}
                else {viewList.setVisibility(View.INVISIBLE);}
                adapterResult.setListData(new ArrayList<>(perusahaanArrayListFiltered));
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
                switch (penanda) {
                    case "Group/Holding":
                        groupClicked.clear();
                        groupClicked.addAll(new HashSet<>(temp));
                        addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), FBgroupArraySET, groupClicked);
                        break;
                    case "Status":
                        statusClicked.clear();
                        statusClicked.addAll(new HashSet<>(temp));
                        addingData((GroupAdapter) recyclerViews.get(1).getAdapter(), FBstatusArraySET, statusClicked);
                        break;
                    case "Lokasi":
                        lokasiClicked.clear();
                        lokasiClicked.addAll(new HashSet<>(temp));
                        addingData((GroupAdapter) recyclerViews.get(2).getAdapter(), FBlokasiArraySET, lokasiClicked);
                        break;
                    case "Jenis":
                        jenisClicked.clear();
                        jenisClicked.addAll(new HashSet<>(temp));
                        addingData((GroupAdapter) recyclerViews.get(3).getAdapter(), FBjenisArraySET, jenisClicked);
                        break;
                    case "Layanan":
                        layananClicked.clear();
                        layananClicked.addAll(new HashSet<>(temp));
                        addingData((GroupAdapter) recyclerViews.get(4).getAdapter(), FBlayananArraySET, layananClicked);
                        break;
                    case "Tipe Customer":
                        layananClicked.clear();
                        layananClicked.addAll(new HashSet<>(temp));
                        addingData((GroupAdapter) recyclerViews.get(5).getAdapter(), FBtipeCustArraySET, tipeCustClicked);
                        break;
                }

                btmSheetDialogGroup.dismiss();
            }

        });

        //See all on click
        btmSheetView.findViewById(R.id.allGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penanda = "Group/Holding";
                updateSeeAll(btmSheetViewGroup, allAdapterGroup, penanda, FBgroupArraySET, groupClicked);
                btmSheetDialogGroup.show();
            }
        });
        btmSheetView.findViewById(R.id.allLokasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penanda = "Lokasi";
                updateSeeAll(btmSheetViewGroup, allAdapterGroup, penanda, FBlokasiArraySET, lokasiClicked);
                btmSheetDialogGroup.show();
            }
        });
        btmSheetView.findViewById(R.id.allJenis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penanda = "Jenis";
                updateSeeAll(btmSheetViewGroup, allAdapterGroup, penanda, FBjenisArraySET, jenisClicked);
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
        FBgroupArraySET = new HashSet<String>(); //removes duplicated strings
//        FBstatusArraySET = new HashSet<String>(); //removes duplicated strings
        FBstatusArraySET = new HashSet<String>(new ArrayList<String>(Arrays.asList(new String[]{"Aktif","Inaktif"}))); //removes duplicated strings
        FBtipeCustArraySET = new HashSet<String>(new ArrayList<String>(Arrays.asList(new String[]{"Direct","Indirect"}))); //removes duplicated strings
        FBlayananArraySET = new HashSet<String>(new ArrayList<String>(Arrays.asList(new String[]{"LOCO","FRANCO","VHS","FMS"}))); //removes duplicated strings
        FBlokasiArraySET = new HashSet<String>(); //removes duplicated strings
        FBjenisArraySET = new HashSet<String>(); //removes duplicated strings

        //Search Recyclerview
        SearchAdapter searchAdapter;
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchAdapter = new SearchAdapter(this, perusahaanArrayListFiltered);
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
                perusahaanArrayListFiltered.clear();
                for (Perusahaan perusahaan : perusahaanArrayList) {
                    if (perusahaan.getNama().toLowerCase(Locale.ROOT).contains(searchWord.toLowerCase(Locale.ROOT))
                            ) {
                        perusahaanArrayListFiltered.add(perusahaan);
                    }
                    if (perusahaanArrayListFiltered.size() == 5) {
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
                    perusahaanArrayListFiltered.clear();
                } else {
                    perusahaanArrayListFiltered.clear();
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
                clearAllDataSets();
//                FBgroupArraySET.clear();
//                FBstatusArraySET.clear();
//                FBlokasiArraySET.clear();
//                FBkebutuhanArraySET.clear();
//                FBjenisArraySET.clear();
//                perusahaanArrayList.clear();
                //Grab data
                for (DataSnapshot FBdata : snapshot.getChildren()) {
                    String penyalur = FBdata.child("penyalur").getValue().toString();
                    String group = FBdata.child("group").getValue().toString();
                    String jenis = FBdata.child("jenis").getValue().toString();
                    String kebutuhan = FBdata.child("kebutuhan").getValue().toString();
                    Float lang = Float.parseFloat(FBdata.child("koor_latitude").getValue().toString());
                    Float longi = Float.parseFloat(FBdata.child("koor_longitude").getValue().toString());
                    GeoPoint point = new GeoPoint(lang, longi);
                    String lokasi = FBdata.child("lokasi").getValue().toString();
                    String nama = FBdata.child("nama").getValue().toString();
                    String layanan = FBdata.child("layanan").getValue().toString();
                    String kompetitor = FBdata.child("kompetitor").getValue().toString();
                    String status = FBdata.child("status").getValue().toString();
                    String tipeCustomer = FBdata.child("tipe_customer").getValue().toString();
                    String marketShare = FBdata.child("market_share").getValue().toString();
                    FBgroupArraySET.add(group);
//                    FBstatusArraySET.add(status);
                    FBlokasiArraySET.add(lokasi);
//                    FBkebutuhanArraySET.add(kebutuhan);
                    FBjenisArraySET.add(jenis);
                    perusahaanArrayList.add(new Perusahaan(kompetitor, group, jenis, kebutuhan, point, lokasi, nama, layanan, penyalur, status, tipeCustomer, marketShare));
                }
                //Adding Data to List Data
                addAllProcess();
//                addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), FBgroupArraySET, groupClicked);
//                addingData((GroupAdapter) recyclerViews.get(1).getAdapter(), FBstatusArraySET, statusClicked);
//                addingData((GroupAdapter) recyclerViews.get(2).getAdapter(), FBlokasiArraySET, lokasiClicked);
//                addingData((GroupAdapter) recyclerViews.get(3).getAdapter(), FBjenisArraySET, jenisClicked);
//                addingData((GroupAdapter) recyclerViews.get(4).getAdapter(), FBlayananArraySET, layananClicked);
//                addingData((GroupAdapter) recyclerViews.get(5).getAdapter(), FBtipeCustArraySET, tipeCustClicked);

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

    private void clearAllDataSets() {
        FBgroupArraySET.clear();
        FBlokasiArraySET.clear();
        FBjenisArraySET.clear();
        perusahaanArrayList.clear();
    }

    private void clearAllData() {
        groupClicked.clear();
        statusClicked.clear();
        jenisClicked.clear();
//        kebutuhanClicked.clear();
        lokasiClicked.clear();
//        marketShareFilter.clear();
        layananClicked.clear();
        tipeCustClicked.clear();
        kebutuhanMin =0d;
        kebutuhanMax = 999999999999d;
        persenMin = 0;
        persenMax = 100;
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
        perusahaanArrayListFiltered.clear();
        for (int i = 0; i < markers.size(); i++) {
//            createRV(btmSheetView, R.id.rvGroup, groupClicked);
//            createRV(btmSheetView, R.id.rvStatus, statusClicked);
//            createRV(btmSheetView, R.id.rvLokasi, lokasiClicked);
//            createRV(btmSheetView, R.id.rvJenis, jenisClicked);
//            createRV(btmSheetView, R.id.rvLayanan, layananClicked);
//            createRV(btmSheetView, R.id.rvTipe, tipeCustClicked);
            double kebutuhan = Double.parseDouble(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getKebutuhan());
            double persen = Double.parseDouble(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getMarket_share());
            if (!(kebutuhan <=kebutuhanMax && kebutuhan >= kebutuhanMin && persen <=persenMax && persen >= persenMin &&
                    (groupClicked.size() == 0 || groupClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getGroup())) &&
                    (statusClicked.size() == 0 || statusClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getStatus())) &&
                    (lokasiClicked.size() == 0 || lokasiClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getTempat())) &&
                    (layananClicked.size() == 0 || layananClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getKebutuhan())) &&
                    (jenisClicked.size() == 0 || jenisClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getJenis()) && (tipeCustClicked.size() == 0 || tipeCustClicked.contains(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan().getJenis())))
            )) {markers.get(i).setIcon(getResources().getDrawable(R.drawable.location_off));}
            else {markers.get(i).setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24)); perusahaanArrayListFiltered.add(((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan());}}
    }
    private void addData() {
        map.getOverlays().clear();
        map.getOverlays().add(markerCurrent);
        for (Perusahaan perusahaan : perusahaanArrayList) {
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


//    private void createMarker(Perusahaan perusahaan) {
//        Marker marker = new Marker(map);
//        marker.setPosition(perusahaan.getLocation());
//        InfoWindow infoWindow = new ClickableInfo(R.layout.clickable_bubble, map, perusahaan);
//        marker.setInfoWindow(infoWindow);
//        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
//        marker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24));
//        markers.add(marker);
//    }
    }
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
        temp.clear();
        temp.addAll(grouped);
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
    private void addAllProcess(){
        addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), FBgroupArraySET, groupClicked);
        addingData((GroupAdapter) recyclerViews.get(1).getAdapter(), FBstatusArraySET, statusClicked);
        addingData((GroupAdapter) recyclerViews.get(2).getAdapter(), FBlokasiArraySET, lokasiClicked);
        addingData((GroupAdapter) recyclerViews.get(3).getAdapter(), FBjenisArraySET, jenisClicked);
        addingData((GroupAdapter) recyclerViews.get(4).getAdapter(), FBlayananArraySET, layananClicked);
        addingData((GroupAdapter) recyclerViews.get(5).getAdapter(), FBtipeCustArraySET, tipeCustClicked);
    }
}
