package com.example.somesta.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.somesta.databinding.ActivityMainBinding;
import com.example.somesta.seeAll.allAdapter;

import com.example.somesta.utility.CustomAlert;
import com.example.somesta.utility.DotProgressBar;
import com.example.somesta.utility.GroupAdapter;
import com.example.somesta.utility.KeyboardUtils;
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
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    AlertDialog dialog;
    public static TextView reset;
    public static double kebutuhanMin, kebutuhanMax, persenMin, persenMax;
    public static int jumlahKebutuhan = 0, jumlahMarketShare = 0;
    public static FloatingActionButton showInfo;


    String hMin, hMax, pMin, pMax;
    public static EditText ETKebutuhanMin, ETKebutuhanMax, ETpersenMin, ETpersenMax;

    private long mLastClickTime = 0;
    private boolean keyboardVisibility = false;
    public static boolean recVisibility = false;
    public static Button filterResets;
    public static IMapController mapController;
    public static BottomSheetDialog viewListDialog;
    Marker markerCurrent;
    public static ExtendedFloatingActionButton viewList;
    private String penanda;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private double currentLatitude,currentLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 1000;

    public static MapView map = null;
    public static HashSet<String> groupClicked = new HashSet<>();
    public static HashSet<String> temp = new HashSet<>();
    public static HashSet<String> statusClicked = new HashSet<>();
    public static HashSet<String> jenisClicked = new HashSet<>();
    public static HashSet<String> layananClicked = new HashSet<>();
    public static HashSet<String> tipeCustClicked = new HashSet<>();
    public static HashSet<String> lokasiClicked = new HashSet<>();
    public static ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<RecyclerView> recyclerViews = new ArrayList<>();
    RecyclerView searchRecyclerView;
    Set<String> FBjenisArraySET, FBstatusArraySET, FBlokasiArraySET, FBgroupArraySET, FBlayananArraySET, FBtipeCustArraySET;
    public static BottomSheetDialog dialogPerusahaan;
    public static View ViewPerusahaan;
    private FloatingActionButton settings;
    public static FloatingSearchView searchView, seeAllSearchView;


    //Semua data perusahaan disimpan di array ini
    public static ArrayList<Perusahaan> perusahaanArrayList = new ArrayList<>();
    //Array untuk menyimpan data yang ter-filter untuk searchView
    public static ArrayList<Perusahaan> perusahaanArrayListFiltered = new ArrayList<>();
    //Array untuk temporary search dari lihat semua
    public static ArrayList<String> searchTemporary = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeLoading();
        //Logika button informasi
        showInfo = findViewById(R.id.informasi);
        showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformation();
            }
        });
        //logika exit button
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
                        clearAllData();
                        perusahaanArrayListFiltered.clear();
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
        viewList.setVisibility(View.GONE);

        //Marker Onclick sheet dialog
        dialogPerusahaan = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme);
        ViewPerusahaan = LayoutInflater.from(this)
                .inflate(R.layout.info_perusahaan, (LinearLayout) dialogPerusahaan.findViewById(R.id.sheets2));
//        btmView = (FrameLayout) ViewPerusahaan.findViewById(R.id.sheets2);
        Context ctx = this;


        // Pembuatan map
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setClickable(true);
        mapController = map.getController();
        mapController.setZoom(17);
        //Pembuatan logika lokasi saat ini
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation(this);
        // Pembuatan marker lokasi saat ini
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

        // BtmSheetView filter awal
        View btmSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.btm_sheet, (LinearLayout) btmSheetDialog.findViewById(R.id.linearLayoutFilter));
        btmSheetDialog.setContentView(btmSheetView);
        ETKebutuhanMin = btmSheetView.findViewById(R.id.hargaMinimum);
        ETKebutuhanMax = btmSheetView.findViewById(R.id.hargaMaximum);
        ETpersenMin = btmSheetView.findViewById(R.id.persenMinimum);
        ETpersenMax = btmSheetView.findViewById(R.id.persenMaximum);

        //ViewListDialog hasil filter
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

        //Creating 6 RV untuk btmsheetview filter awal
        createRV(btmSheetView, R.id.rvGroup, groupClicked);
        createRV(btmSheetView, R.id.rvStatus, statusClicked);
        createRV(btmSheetView, R.id.rvLokasi, lokasiClicked);
        createRV(btmSheetView, R.id.rvJenis, jenisClicked);
        createRV(btmSheetView, R.id.rvLayanan, layananClicked);
        createRV(btmSheetView, R.id.rvTipe, tipeCustClicked);

        //Reset button filter awal
        reset = btmSheetView.findViewById(R.id.filterReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllData();
                for (int i = 0; i < recyclerViews.size(); i++) {
                    RecyclerView recyclerView = recyclerViews.get(i);
                    for (int k = 0; k < recyclerView.getChildCount(); k++) {
                        GroupAdapter.HolderData holder = (GroupAdapter.HolderData) recyclerView.findViewHolderForAdapterPosition(k);
                        holder.group.setChecked(false);
                    }
                }
                ETKebutuhanMin.setText("");
                ETKebutuhanMax.setText("");
                ETpersenMin.setText("");
                ETpersenMax.setText("");
            }
        });

        // Making the filter button
        filterResets =  btmSheetView.findViewById(R.id.buttonFiltering);
        filterResets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
                jumlahMarketShare = 0;
                jumlahKebutuhan = 0;
                updateMarker();
                //Logika muncul dan hilang dari tombol viewlist dan showinfo
                if(perusahaanArrayListFiltered.size() == 0){
                    ((TextView)viewListDialogView.findViewById(R.id.textResult)).setText(getResources().getString(R.string.Response_None));
                    showInfo.setVisibility(View.GONE);
                }
                else {((TextView)viewListDialogView.findViewById(R.id.textResult)).setText(getResources().getString(R.string.Response_OK));
                    showInfo.setVisibility(View.VISIBLE);
                    mapController.setZoom(5);
                    mapController.setCenter(new GeoPoint(0.7893,118));}
                if(groupClicked.size()+statusClicked.size()+lokasiClicked.size()+layananClicked.size()+jenisClicked.size()+tipeCustClicked.size()!=0 ||
                        (!(pMin.equals("") && pMax.equals("") && hMin.equals("")&& hMax.equals(""))))
                {viewList.setVisibility(View.VISIBLE);}
                else {viewList.setVisibility(View.GONE);showInfo.setVisibility(View.GONE);}
                adapterResult.setListData(new ArrayList<>(perusahaanArrayListFiltered));
                adapterResult.notifyDataSetChanged();
                btmSheetDialog.dismiss();
                dialog.dismiss();
                map.invalidate();
            }
        });


        //BottomSheetialog untuk tombol lihat semua
        BottomSheetDialog btmSheetDialogGroup = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme);
        View btmSheetViewGroup = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.see_all_layout, (LinearLayout) btmSheetDialogGroup.findViewById(R.id.linearLayoutFilter));
        btmSheetDialogGroup.setContentView(btmSheetViewGroup);
        RecyclerView recyclerViewGroup = btmSheetViewGroup.findViewById(R.id.allRv);
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(btmSheetViewGroup.getContext()));
        allAdapter allAdapterGroup = new allAdapter(btmSheetViewGroup.getContext(), new ArrayList<>(), temp);
        recyclerViewGroup.setAdapter(allAdapterGroup);
        //Fitur search di lihat semua
        seeAllSearchView = btmSheetDialogGroup.findViewById(R.id.searchView);
        seeAllSearchView.clearFocus();
        seeAllSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    searchTemporary.clear();
                } else {
                    searchTemporary.clear();
                }
            }
        });
        seeAllSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                ArrayList<String> listData = new ArrayList<>();
                if (penanda.equals("Group/Holding")) {
                    listData = new ArrayList<>(FBgroupArraySET);
                }
                if (penanda.equals("Lokasi")) {
                    listData = new ArrayList<>(FBlokasiArraySET);
                }
                if (penanda.equals("Jenis")) {
                    listData = new ArrayList<>(FBjenisArraySET);
                }
                if (newQuery.isEmpty() || oldQuery.isEmpty()) {allAdapterGroup.setListData(listData);}
                else {allAdapterGroup.setListData(searchTemporary);}
                    allAdapterGroup.notifyDataSetChanged();
                    String searchWord = newQuery;
                    searchTemporary.clear();
                    for (String filters : listData) {
                        if (filters.toLowerCase(Locale.ROOT).contains(searchWord.toLowerCase(Locale.ROOT))
                        ) {
                            searchTemporary.add(filters);
                        }
                    }
                }
        });
        // reset button di btmsheet lihat semua
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
        // Button filter lihat semua
        btmSheetViewGroup.findViewById(R.id.buttonFiltering).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (penanda) {
                    case "Group/Holding":
                        groupClicked.clear();
                        groupClicked.addAll(new HashSet<>(temp));
                        addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), FBgroupArraySET, groupClicked);
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
                }
                btmSheetDialogGroup.dismiss();
            }
        });

        //Logika klik lihat semua
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
        // Logika klik tombol lokasi saat ini
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
        FBlokasiArraySET = new HashSet<String>(); //removes duplicated strings
        FBjenisArraySET = new HashSet<String>(); //removes duplicated strings
        FBstatusArraySET = new HashSet<String>(new ArrayList<String>(Arrays.asList(new String[]{"Aktif","Inaktif"}))); //Karena sudah paten 2 pilihan
        FBtipeCustArraySET = new HashSet<String>(new ArrayList<String>(Arrays.asList(new String[]{"Direct","Indirect"}))); //Karena sudah paten 2 pilihan
        FBlayananArraySET = new HashSet<String>(new ArrayList<String>(Arrays.asList(new String[]{"LOCO","FRANCO","VHS","FMS"}))); //Karena sudah paten 4 pilihan


        //Search Recyclerview
        SearchAdapter searchAdapter;
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchAdapter = new SearchAdapter(this, perusahaanArrayListFiltered);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchAdapter);
        searchRecyclerView.setVisibility(View.GONE);

        //Search Bar
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                searchAdapter.notifyDataSetChanged();
                String searchWord = newQuery;
                perusahaanArrayListFiltered.clear();
                for (Perusahaan perusahaan : perusahaanArrayList) {
                    if (perusahaan.getNama().toLowerCase(Locale.ROOT).contains(searchWord.toLowerCase(Locale.ROOT))
                    ) {perusahaanArrayListFiltered.add(perusahaan);}
                    if (perusahaanArrayListFiltered.size() == 5) {break;}
                }
                recVisibility = true;
                searchRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        // Logika saat fokus searchview berganti
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

        //Firebase Database Data Grabber
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                //resets all arrays on every update
                clearAllDataSets();
                //Grab data
                for (DataSnapshot FBdata : snapshot.getChildren()) {
                    String kompetitor = FBdata.child("kompetitor").getValue().toString();
                    String group = FBdata.child("group").getValue().toString();
                    String jenis = FBdata.child("jenis").getValue().toString();
                    String kebutuhan = FBdata.child("kebutuhan").getValue().toString();
                    Float lang = Float.parseFloat(FBdata.child("koor_latitude").getValue().toString());
                    Float longi = Float.parseFloat(FBdata.child("koor_longitude").getValue().toString());
                    GeoPoint point = new GeoPoint(lang, longi);
                    String lokasi = FBdata.child("lokasi").getValue().toString();
                    String nama = FBdata.child("nama").getValue().toString();
                    String layanan = FBdata.child("layanan").getValue().toString();
                    String penyalur = FBdata.child("penyalur").getValue().toString();
                    String status = FBdata.child("status").getValue().toString();
                    String tipeCustomer = FBdata.child("tipe_customer").getValue().toString();
                    String marketShare = FBdata.child("market_share").getValue().toString();
                    FBgroupArraySET.add(group);
                    FBlokasiArraySET.add(lokasi);
                    FBjenisArraySET.add(jenis);
                    perusahaanArrayList.add(new Perusahaan(kompetitor, group, jenis, kebutuhan, point, lokasi, nama, layanan, penyalur, status, tipeCustomer,marketShare));
                }
                //Adding Data to List Data
                addAllProcess();

                //Adding marker
                addData();
                //Notify adapter, onDataChange works asynchronously
                if (searchAdapter != null) {searchAdapter.notifyDataSetChanged();}
                dialog.dismiss();
            }
            // Jika penarikan data gagal
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal Mendapatkan Data Terbaru", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Penampilan filter
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
                        // Cek jika keyboard tidak muncul, maka perlu menghilangkan recycler view search
                        if (!isOpen) {
                            recVisibility = isOpen;
                            searchRecyclerView.setVisibility(View.GONE);
                        }
                    }
                });
        // Cek apakah keyboard muncul atau tidak
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                keyboardVisibility = isVisible;
            }
        });
    }
    //Pembuatan show info kebutuhan total dan market share
    private void showInformation() {
        CustomAlert alertBuilder = new CustomAlert(MainActivity.this,"Informasi Total Kebutuhan dan Market Share Perusahaan Terfilter"
                , "Kebutuhan Total Per Bulan:\n"+jumlahKebutuhan+" KL\nMarket Share:\n"+jumlahMarketShare+" KL");
        alertBuilder.show();
    }
    // hapus semua data set yang diperoleh dari firebase untuk pembuatan filter
    private void clearAllDataSets() {
        FBgroupArraySET.clear();
        FBlokasiArraySET.clear();
        FBjenisArraySET.clear();
        perusahaanArrayList.clear();
    }

    //Update recyvler view filter
    private void addAllProcess() {
        addingData((GroupAdapter) recyclerViews.get(0).getAdapter(), FBgroupArraySET, groupClicked);
        addingData((GroupAdapter) recyclerViews.get(1).getAdapter(), FBstatusArraySET, statusClicked);
        addingData((GroupAdapter) recyclerViews.get(2).getAdapter(), FBlokasiArraySET, lokasiClicked);
        addingData((GroupAdapter) recyclerViews.get(3).getAdapter(), FBjenisArraySET, jenisClicked);
        addingData((GroupAdapter) recyclerViews.get(4).getAdapter(), FBlayananArraySET, layananClicked);
        addingData((GroupAdapter) recyclerViews.get(5).getAdapter(), FBtipeCustArraySET, tipeCustClicked);
    }
    // hapus semua list yang digunakan dalam filter
    public static void clearAllData() {
        groupClicked.clear();
        statusClicked.clear();
        jenisClicked.clear();
        lokasiClicked.clear();
        layananClicked.clear();
        tipeCustClicked.clear();
        kebutuhanMin =0d;
        kebutuhanMax = 999999999999d;
        persenMin = 0;
        persenMax = 100;
        jumlahMarketShare = 0;
        jumlahKebutuhan = 0;
    }
    @Override
    public void onBackPressed() {
        //Melakukan cek apakah keyboard turun dan recyvler view hilang saat menekan tombol back smartphone
        if (!keyboardVisibility && !recVisibility){
            // jika tombol bek dipencet 2 kali dalam kurun waktu 3000 milisec (3 detik), maka akan reset semua dan keluar aplikasi
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){
                MainActivity.clearAllData();
                MainActivity.perusahaanArrayListFiltered.clear();
                MainActivity.filterResets.performClick();
                finishAffinity();
                finish();
            }
            Toast.makeText(this,"Ketuk Sekali Lagi untuk Keluar Dari Aplikasi",Toast.LENGTH_SHORT).show();
            mLastClickTime = SystemClock.elapsedRealtime();
        }
        // Jika saat klik back masih ada recyvler view dari search view, maka back tersebut akan menghilangkan recycler view search
        else if (!keyboardVisibility && recVisibility){searchRecyclerView.setVisibility(View.GONE);recVisibility = false;}
        else {super.onBackPressed();}
    }
    // Fungsi yang digunakan untuk mendapatkan lokasi saat ini dari pengguna
    private void getLocation(Context ctx) {
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);}
        else {fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                if (location != null) {
                    map.getOverlays().remove(markerCurrent);
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    GeoPoint currentLoc = new GeoPoint(currentLatitude,currentLongitude);
                    markerCurrent.setPosition(currentLoc);
                    map.getOverlays().add(markerCurrent);
                    mapController.setZoom(17);
                    mapController.setCenter(currentLoc);
                    dialog.dismiss();
                }
            });
        }
    }

    // Fungsi untuk melakukan update icon marker
    private void updateMarker(){
        perusahaanArrayListFiltered.clear();
        for (int i = 0; i < markers.size(); i++) {
            Perusahaan tempPerusahaan = ((ClickableInfo)markers.get(i).getInfoWindow()).getPerusahaan();
            double kebutuhan = Double.parseDouble(tempPerusahaan.getKebutuhan());
            double persen = Double.parseDouble(tempPerusahaan.getMarket_share());
            if (!(kebutuhan <=kebutuhanMax && kebutuhan >= kebutuhanMin && persen <=persenMax && persen >= persenMin &&
                    (groupClicked.size() == 0 || groupClicked.contains(tempPerusahaan.getGroup())) &&
                    (statusClicked.size() == 0 || statusClicked.contains(tempPerusahaan.getStatus())) &&
                    (lokasiClicked.size() == 0 || lokasiClicked.contains(tempPerusahaan.getTempat())) &&
                    (layananClicked.size() == 0 || layananClicked.contains(tempPerusahaan.getLayanan())) &&
                    (jenisClicked.size() == 0 || jenisClicked.contains(tempPerusahaan.getJenis()) &&
                            (tipeCustClicked.size() == 0 || tipeCustClicked.contains(tempPerusahaan.getTipeCustomer())))))
            {markers.get(i).setIcon(getResources().getDrawable(R.drawable.location_off));}
            else {markers.get(i).setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24));
                perusahaanArrayListFiltered.add(tempPerusahaan);
                double jumlah = Double.parseDouble(tempPerusahaan.getKebutuhan());
                double persentase = Double.parseDouble(tempPerusahaan.getMarket_share());
                jumlahKebutuhan+=jumlah;jumlahMarketShare+=(persentase*jumlah/100d);}
            }
    }

    // Reset map dan pembuatan marker semua perusahaan
    private void addData() {
        map.getOverlays().clear();
        getLocation(this);
        markers.clear();
        for (Perusahaan perusahaan : perusahaanArrayList) {
            createMarker(perusahaan);
        }
    }
    // Pembuatan marker perusahaan
    private void createMarker(Perusahaan perusahaan) {
        Marker marker = new Marker(map);
        marker.setPosition(perusahaan.getLocation());
        InfoWindow infoWindow = new ClickableInfo(R.layout.clickable_bubble, map, perusahaan);
        marker.setInfoWindow(infoWindow);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        if (perusahaanArrayListFiltered.size() == 0 || perusahaanArrayListFiltered.contains(perusahaan)){
        marker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_on_24));}
        else {marker.setIcon(getResources().getDrawable(R.drawable.location_off));}
        markers.add(marker);
        map.getOverlays().add(marker);
    }

    // Fungsi pembuatan recycler view untuk filter awal
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
    // Update recyvler view pada btm sheet lihat semua
    public void updateSeeAll(View btmSheetViewGroup, allAdapter allAdapterGroup, String penanda, Set<String> set, HashSet<String> grouped) {
        temp.clear();
        temp.addAll(grouped);
        TextView namaFilter = btmSheetViewGroup.findViewById(R.id.nama);
        namaFilter.setText(penanda);
        allAdapterGroup.setListData(new ArrayList<>(set));
        allAdapterGroup.notifyDataSetChanged();
    }
    // Logika untuk membagi data yang akan ditampilkan pada recycler view dari btmsheet filter awal
    private void addingData(GroupAdapter groupAdapters, Set<String> set, HashSet<String> grouped) {
        List<String> listData = new ArrayList<>();
        int batas = grouped.size();
        final int batasan = 4;
        if (batas >= batasan) {listData.addAll(grouped);}
        else {
            listData.addAll(grouped);
            ArrayList<String> tempData = new ArrayList<>(set);
            int i = 0;
            while (listData.size() < 4) {
                if (i == tempData.size()) {break;}
                String data = tempData.get(i);
                if (!listData.contains(data)) {listData.add(data);}
                i++;
            }
        }
        groupAdapters.setListData(listData);
        groupAdapters.notifyDataSetChanged();
    }
    public void makeLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.loading_bar,null);
        builder.setView(view);
        ((DotProgressBar)view.findViewById(R.id.dotProgressBar)).startAnimation();
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
