package com.example.somesta.Activity;

import android.app.Activity;
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

import com.arlib.floatingsearchview.FloatingSearchView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity{


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
    ArrayList<RecyclerView.ItemDecoration> itemDecorations = new ArrayList<>();
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
        FloatingSearchView searchView = findViewById(R.id.searchView);
//        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                searchAdapter.notifyDataSetChanged();
                String searchWord = newQuery;
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
                }
                searchRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                perusahaanArrayListFiltered.clear();
                }else{
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
                FBgroupArraySET.clear();
                FBstatusArraySET.clear();
                FBlokasiArraySET.clear();
                FBkebutuhanArraySET.clear();
                FBjenisArraySET.clear();

                //grab group data
                for (DataSnapshot FBdata : snapshot.getChildren()){
                    String data = FBdata.child("group").getValue().toString();
                    FBgroupArraySET.add(data);
                }

                //grab status data
                for (DataSnapshot FBdata : snapshot.getChildren()){
                    String data = FBdata.child("status").getValue().toString();
                    FBstatusArraySET.add(data);
                }
                //grab lokasi data
                for (DataSnapshot FBdata : snapshot.getChildren()){
                    String data = FBdata.child("lokasi").getValue().toString();
                    FBlokasiArraySET.add(data);
                }
                //grab kebutuhan data
                for (DataSnapshot FBdata : snapshot.getChildren()){
                    String data = FBdata.child("kebutuhan").getValue().toString();
                    FBkebutuhanArraySET.add(data);
                }
                //grab jenis data
                for (DataSnapshot FBdata : snapshot.getChildren()){
                    String data = FBdata.child("jenis").getValue().toString();
                    FBjenisArraySET.add(data);
                }

                //Grab data to perusahaanArrayList
                perusahaanArrayList.clear();
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
                    perusahaanArrayList.add(new Perusahaan(dilayani,group,jenis,kebutuhan,point,lokasi,nama,pelayanan,penyalur,status,tipeCustomer));
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
        FloatingActionButton filter_btn = (FloatingActionButton) findViewById(R.id.filter);
        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog btmSheetDialog = new BottomSheetDialog(
                        MainActivity.this, R.style.BottomSheetDialogTheme);

                View btmSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.btm_sheet, (FrameLayout)findViewById(R.id.sheets));

                FrameLayout btmView = (FrameLayout) btmSheetView.findViewById(R.id.sheets);

                BottomSheetBehavior.from(btmView).setState(BottomSheetBehavior.STATE_EXPANDED);
                recyclerViews.clear();
                itemDecorations.clear();

                createRV(btmSheetView,R.id.rvGroup,FBgroupArraySET,R.layout.filter_data,"group");

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
                createRV(btmSheetView,R.id.rvStatus,FBstatusArraySET,R.layout.filter_data,"status");
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
                createRV(btmSheetView,R.id.rvLokasi,FBlokasiArraySET,R.layout.filter_data,"lokasi");

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

                createRV(btmSheetView,R.id.rvKebutuhan,FBkebutuhanArraySET,R.layout.filter_data,"kebutuhan");
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

                createRV(btmSheetView,R.id.rvJenis,FBjenisArraySET,R.layout.filter_data,"jenis");
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
                        for (int i=0;i<recyclerViews.size();i++) {
                            recyclerViews.get(i).removeItemDecoration(itemDecorations.get(i));
                        }
                        recyclerViews.clear();
                        itemDecorations.clear();

                        createRV(btmSheetView,R.id.rvGroup,FBgroupArraySET,R.layout.filter_data,"group");
                        createRV(btmSheetView,R.id.rvStatus,FBstatusArraySET,R.layout.filter_data,"status");
                        createRV(btmSheetView,R.id.rvLokasi,FBlokasiArraySET,R.layout.filter_data,"lokasi");
                        createRV(btmSheetView,R.id.rvKebutuhan,FBkebutuhanArraySET,R.layout.filter_data,"kebutuhan");
                        createRV(btmSheetView,R.id.rvJenis,FBjenisArraySET,R.layout.filter_data,"jenis");
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
//                if (statusClicked.size()>0 && !statusClicked.contains(perusahaan.getGroup())){
//                    continue;
//                }
//                if (lokasiClicked.size()>0 && !lokasiClicked.contains(perusahaan.getGroup())){
//                    continue;
//                }
//                if (kebutuhanClicked.size()>0 && !kebutuhanClicked.contains(perusahaan.getGroup())){
//                    continue;
//                }
//                if (jenisClicked.size()>0 && !jenisClicked.contains(perusahaan.getGroup())){
//                    continue;
//                }
//                createMarker(perusahaan);
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
    private void createRV(View view, int id, Set<String> set, int layout, String tipe){
        RecyclerView recyclerView = view.findViewById(id);
        List<String> FBArray = new ArrayList<String>(set); //convert stringSet to ArrayList
        List<String> listData = FBArray; //Pass Kebutuhan Data From Database To RecycleViewGroup

        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(),
                (new Utility.ColumnQty(view.getContext(),layout)).calculateNoOfColumns());

        recyclerView.setLayoutManager(gridLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new GridSpacing(
                (new Utility.ColumnQty(view.getContext(),layout)).calculateSpacing());
        recyclerView.addItemDecoration(itemDecoration);
        itemDecorations.add(itemDecoration);

        GroupAdapter groupAdapter = new GroupAdapter(view.getContext(), listData, tipe);
        recyclerView.setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();
        recyclerViews.add(recyclerView);
        System.out.println(itemDecorations.size());
        System.out.println(recyclerViews.size());
    }
}
