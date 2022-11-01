package com.example.somesta.Activity;

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
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.R;
import com.example.somesta.SearchRecyclerView.SearchAdapter;
import com.example.somesta.databinding.ActivityMainBinding;
import com.example.somesta.seeAll.allAdapter;
import com.example.somesta.utility.GridSpacing;
import com.example.somesta.utility.GroupAdapter;
import com.example.somesta.utility.Utility;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
    //Lihat Semua Button
    TextView allGroup;
    TextView allStatus;
    TextView allLokasi;
    TextView allKebutuhan;
    TextView allJenis;

    private ActivityMainBinding binding;
    public static  MapView map = null;
    LocationManager locationManager;
    public static ArrayList<String> groupClicked = new ArrayList<>();
    public static ArrayList<String> statusClicked = new ArrayList<>();
    public static ArrayList<String> jenisClicked = new ArrayList<>();
    public static ArrayList<String> kebutuhanClicked = new ArrayList<>();
    public static ArrayList<String> lokasiClicked = new ArrayList<>();
    public static ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();

    ArrayList<RecyclerView> recyclerViews = new ArrayList<>();
    FloatingActionButton filter_btn;
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
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        IMapController mapController = map.getController();
        mapController.setZoom(17);
        GeoPoint startPoint = new GeoPoint(-7.795425625273463, 110.36488798392885);
        mapController.setCenter(startPoint);

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
//                    //grab group data
//                    for (DataSnapshot FBdata : snapshot.getChildren()){
//                        String data = FBdata.child("group").getValue().toString();
//
//                    }
//
//                    //grab status data
//                    for (DataSnapshot FBdata : snapshot.getChildren()){
//                        String data = FBdata.child("status").getValue().toString();
//
//                    }
//                    //grab lokasi data
//                    for (DataSnapshot FBdata : snapshot.getChildren()){
//                        String data = FBdata.child("lokasi").getValue().toString();
//
//                    }
//                    //grab kebutuhan data
//                    for (DataSnapshot FBdata : snapshot.getChildren()){
//                        String data = FBdata.child("kebutuhan").getValue().toString();
//
//                    }
//                    //grab jenis data
//                    for (DataSnapshot FBdata : snapshot.getChildren()){
//                        String data = FBdata.child("jenis").getValue().toString();
//                    }
                }
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
        filter_btn = (FloatingActionButton) findViewById(R.id.filter);
        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating 5 RV
                RecyclerView recyclerView;
                recyclerView = btmSheetView.findViewById(R.id.rvGroup);
                recyclerViews.add(recyclerView);

                recyclerView = btmSheetView.findViewById(R.id.rvStatus);
                recyclerViews.add(recyclerView);

                recyclerView = btmSheetView.findViewById(R.id.rvLokasi);
                recyclerViews.add(recyclerView);

                recyclerView = btmSheetView.findViewById(R.id.rvKebutuhan);
                recyclerViews.add(recyclerView);

                recyclerView = btmSheetView.findViewById(R.id.rvJenis);
                recyclerViews.add(recyclerView);

                createRV(btmSheetView,FBgroupArraySET,groupClicked,0);

//                // Creating the RV for group
//                RecyclerView recyclerViewGroup = btmSheetView.findViewById(R.id.rvGroup);
//                GroupAdapter groupAdapterGroup;
//
//                List<String> FBgroupArray = new ArrayList<String>(FBgroupArraySET); //convert stringSet to ArrayList
//                List<String> listDataGroup = FBgroupArray; //Pass Group Data From Database To RecycleViewGroup
//                GridLayoutManager gridLayoutManagerGroup = new GridLayoutManager(btmSheetView.getContext(),
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());
//                recyclerViewGroup.setLayoutManager(gridLayoutManagerGroup);
//                recyclerViewGroup.addItemDecoration(new GridSpacing(
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
//                groupAdapterGroup = new GroupAdapter(btmSheetView.getContext(), listDataGroup, "group");
//                recyclerViewGroup.setAdapter(groupAdapterGroup);
//                groupAdapterGroup.notifyDataSetChanged();
                createRV(btmSheetView,FBstatusArraySET,statusClicked,1);
//
//                //Creating RV Status
//                RecyclerView recyclerViewStatus = btmSheetView.findViewById(R.id.rvStatus);
//                GroupAdapter groupAdapterStatus;
//
//                List<String> FBstatusArray = new ArrayList<String>(FBstatusArraySET); //convert stringSet to ArrayList
//                List<String> listDataStatus = FBstatusArray; //Pass Status Data From Database To RecycleViewGroup
//
//                GridLayoutManager gridLayoutManagerStatus = new GridLayoutManager(btmSheetView.getContext(),
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());
//
//                recyclerViewStatus.setLayoutManager(gridLayoutManagerStatus);
//                recyclerViewStatus.addItemDecoration(new GridSpacing(
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
//                groupAdapterStatus = new GroupAdapter(btmSheetView.getContext(), listDataStatus, "status");
//                recyclerViewStatus.setAdapter(groupAdapterStatus);
//                groupAdapterStatus.notifyDataSetChanged();
                createRV(btmSheetView,FBlokasiArraySET,lokasiClicked,2);

//                //Creating RV Lokasi
//                RecyclerView recyclerViewLokasi = btmSheetView.findViewById(R.id.rvLokasi);
//                GroupAdapter groupAdapterLokasi;
//
//                List<String> FBlokasiArray = new ArrayList<String>(FBlokasiArraySET); //convert stringSet to ArrayList
//                List<String> listDataLokasi = FBlokasiArray; //Pass Lokasi Data From Database To RecycleViewGroup
//
//                GridLayoutManager gridLayoutManagerLokasi = new GridLayoutManager(btmSheetView.getContext(),
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());
//
//                recyclerViewLokasi.setLayoutManager(gridLayoutManagerLokasi);
//                recyclerViewLokasi.addItemDecoration(new GridSpacing(
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
//                groupAdapterLokasi = new GroupAdapter(btmSheetView.getContext(), listDataLokasi, "lokasi");
//                recyclerViewLokasi.setAdapter(groupAdapterLokasi);
//                groupAdapterLokasi.notifyDataSetChanged();

                createRV(btmSheetView,FBkebutuhanArraySET,kebutuhanClicked,3);
//                //Creating RV Kebutuhan
//                RecyclerView recyclerViewKebutuhan = btmSheetView.findViewById(R.id.rvKebutuhan);
//
//                List<String> FBkebutuhanArray = new ArrayList<String>(FBkebutuhanArraySET); //convert stringSet to ArrayList
//                List<String> listDataKebutuhan = FBkebutuhanArray; //Pass Kebutuhan Data From Database To RecycleViewGroup
//
//                GridLayoutManager gridLayoutManagerKebutuhan = new GridLayoutManager(btmSheetView.getContext(),
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());
//
//                recyclerViewKebutuhan.setLayoutManager(gridLayoutManagerKebutuhan);
//                recyclerViewKebutuhan.addItemDecoration(new GridSpacing(
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
//
//                GroupAdapter groupAdapterKebutuhan = new GroupAdapter(btmSheetView.getContext(), listDataKebutuhan, "kebutuhan");
//                recyclerViewKebutuhan.setAdapter(groupAdapterKebutuhan);
//                groupAdapterKebutuhan.notifyDataSetChanged();

                createRV(btmSheetView,FBjenisArraySET,jenisClicked, 4);
//                //Creating RV Jenis
//                RecyclerView recyclerViewJenis = btmSheetView.findViewById(R.id.rvJenis);
//                List<String> FBjenisArray = new ArrayList<String>(FBjenisArraySET); //convert stringSet to ArrayList
//                List<String> listDataJenis = FBjenisArray; //Pass Jenis Data From Database To RecycleViewGroup
//
//                GridLayoutManager gridLayoutManagerJenis = new GridLayoutManager(btmSheetView.getContext(),
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());
//
//                recyclerViewJenis.setLayoutManager(gridLayoutManagerJenis);
//                recyclerViewJenis.addItemDecoration(new GridSpacing(
//                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
//
//                GroupAdapter groupAdapterJenis = new GroupAdapter(btmSheetView.getContext(), listDataJenis, "jenis");
//                recyclerViewJenis.setAdapter(groupAdapterJenis);
//                groupAdapterJenis.notifyDataSetChanged();

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
                allGroup = btmSheetView.findViewById(R.id.allGroup);
                allJenis = btmSheetView.findViewById(R.id.allJenis);
                allKebutuhan = btmSheetView.findViewById(R.id.allKebutuhan);
                allLokasi = btmSheetView.findViewById(R.id.allLokasi);
                allStatus = btmSheetView.findViewById(R.id.allStatus);

                allGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBoxes(btmSheetView,FBgroupArraySET,groupClicked, "Group");
                    }
                });
                allJenis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBoxes(btmSheetView,FBjenisArraySET,jenisClicked, "Jenis");
                    }
                });
                allKebutuhan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBoxes(btmSheetView,FBkebutuhanArraySET,kebutuhanClicked, "Kebutuhan");
                    }
                });
                allLokasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBoxes(btmSheetView,FBlokasiArraySET,lokasiClicked, "Lokasi");
                    }
                });
                allStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBoxes(btmSheetView,FBstatusArraySET,statusClicked, "Status");
                    }
                });





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

                btmSheetDialog.setContentView(btmSheetView);
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
    private void createRV(View view, Set<String> set, ArrayList<String> grouped, int urutan){
        List<String> listData = new ArrayList<>();
        RecyclerView recyclerView = recyclerViews.get(urutan);
        int batas = grouped.size();
        if (batas > 8){
            listData.addAll(grouped);
        }
        else {
            ArrayList<String> tempData = new ArrayList<>();
            tempData.addAll(grouped);
            tempData.addAll(new ArrayList<>(set));
            List<String> temporaryHash = new ArrayList<>(new HashSet<String>(tempData));
            batas = temporaryHash.size();
            if (batas > 8){batas = 8;}
            for (int i = 0; i < batas; i++){
                listData.add(temporaryHash.get(i));
            }}
//        List<String> FBArray = new ArrayList<String>(set); //convert stringSet to ArrayList
//        List<String> listData = FBArray; //Pass Kebutuhan Data From Database To RecycleViewGroup
//        tempData.addAll(grouped);
//        tempData.addAll(new ArrayList<>(set));
//        List<String> temporaryHash = new ArrayList<>(new HashSet<String>(tempData));
//        int batas = temporaryHash.size();
//        List<String> listData = new ArrayList<>();
//        if (batas > 8){
//            batas = 8;
//            for (int i = 0; i < batas; i++){
//                listData.add(temporaryHash.get(i));
//            }
//        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(),
                (new Utility.ColumnQty(view.getContext(),R.layout.filter_data)).calculateNoOfColumns());

        recyclerView.setLayoutManager(gridLayoutManager);
//        RecyclerView.ItemDecoration itemDecoration = new GridSpacing(
//                (new Utility.ColumnQty(view.getContext(),R.layout.filter_data)).calculateSpacing());
//        recyclerView.addItemDecoration(itemDecoration);

        GroupAdapter groupAdapter = new GroupAdapter(view.getContext(), listData, grouped);
//        recyclerView.setAdapter(groupAdapter);
        recyclerViews.get(urutan).setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();

    }

    //See all checkboxes
    public void checkBoxes(View views, Set<String> set, ArrayList<String> grouped, String nama){
        BottomSheetDialog btmSheetDialogs = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme);

        View btmSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.see_all_layout, (FrameLayout)findViewById(R.id.sheets));
        TextView namaFilter = btmSheetView.findViewById(R.id.nama);
        ArrayList<String> temporary = new ArrayList<>(grouped);
        namaFilter.setText(nama);


        FrameLayout btmView = (FrameLayout) btmSheetView.findViewById(R.id.sheets);
        BottomSheetBehavior.from(btmView).setState(BottomSheetBehavior.STATE_EXPANDED);
        btmSheetDialogs.setContentView(btmSheetView);
        Button btnFilter = btmSheetView.findViewById(R.id.buttonFiltering);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nama.equals("Group")) {groupClicked = new ArrayList<>(temporary); createRV(views, set, groupClicked,0);}
                else if (nama.equals("Status")) {statusClicked = new ArrayList<>(temporary); createRV(views, set, statusClicked,1);}
                else if (nama.equals("Lokasi")) {lokasiClicked = new ArrayList<>(temporary); createRV(views, set, lokasiClicked,2);}
                else if (nama.equals("Kebutuhan")) {kebutuhanClicked = new ArrayList<>(temporary); createRV(views, set, kebutuhanClicked,3);}
                else if (nama.equals("Jenis")) {jenisClicked = new ArrayList<>(temporary); createRV(views, set, jenisClicked,4);}
                btmSheetDialogs.dismiss();
//                for (int j = 0; j < rvGroup.getChildCount(); j++) {
//                    GroupAdapter.HolderData holder = (GroupAdapter.HolderData) rvGroup.findViewHolderForAdapterPosition(j);
//                    if (){holder.group.setChecked(false);}

                }

        });




        RecyclerView recyclerView = btmSheetView.findViewById(R.id.allRv);
        List<String> listData = new ArrayList<>(set);
        recyclerView.setLayoutManager(new LinearLayoutManager(btmSheetView.getContext()));
        allAdapter allAdapter = new allAdapter(btmSheetView.getContext(), listData, temporary, nama);
        recyclerView.setAdapter(allAdapter);
        allAdapter.notifyDataSetChanged();
        TextView resets = btmSheetView.findViewById(R.id.filterReset);
        resets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temporary.clear();
                for (int k = 0; k < recyclerView.getChildCount(); k++) {
                    allAdapter.HolderData holder = (com.example.somesta.seeAll.allAdapter.HolderData) recyclerView.findViewHolderForAdapterPosition(k);
                    holder.group.setChecked(false);
                }
            }
        }
        );
        btmSheetDialogs.show();

    }

}
