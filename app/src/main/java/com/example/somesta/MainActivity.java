package com.example.somesta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private ActivityMainBinding binding;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Firebase References and Data StringSets
        FirebaseDatabase firebaseDatabase;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("dbCustomer");
        Set<String>  FBgroupArraySET = new HashSet<String>(); //removes duplicated strings
        Set<String>  FBstatusArraySET = new HashSet<String>(); //removes duplicated strings
        Set<String>  FBlokasiArraySET = new HashSet<String>(); //removes duplicated strings
        Set<String>  FBkebutuhanArraySET = new HashSet<String>(); //removes duplicated strings
        Set<String>  FBjenisArraySET = new HashSet<String>(); //removes duplicated strings

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
                //grab kebutuhan data
                for (DataSnapshot FBdata : snapshot.getChildren()){
                    String data = FBdata.child("jenis").getValue().toString();
                    FBjenisArraySET.add(data);
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

                // Creating the RV for group
                RecyclerView recyclerViewGroup = btmSheetView.findViewById(R.id.rvGroup);
                GroupAdapter groupAdapterGroup;

                List<String> FBgroupArray = new ArrayList<String>(FBgroupArraySET); //convert stringSet to ArrayList
                List<String> listDataGroup = FBgroupArray; //Pass Group Data From Database To RecycleViewGroup
                GridLayoutManager gridLayoutManagerGroup = new GridLayoutManager(btmSheetView.getContext(),
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());
                recyclerViewGroup.setLayoutManager(gridLayoutManagerGroup);
                recyclerViewGroup.addItemDecoration(new GridSpacing(
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
                groupAdapterGroup = new GroupAdapter(btmSheetView.getContext(), listDataGroup);
                recyclerViewGroup.setAdapter(groupAdapterGroup);
                groupAdapterGroup.notifyDataSetChanged();

                //Creating RV Status
                RecyclerView recyclerViewStatus = btmSheetView.findViewById(R.id.rvStatus);
                GroupAdapter groupAdapterStatus;

                List<String> FBstatusArray = new ArrayList<String>(FBstatusArraySET); //convert stringSet to ArrayList
                List<String> listDataStatus = FBstatusArray; //Pass Status Data From Database To RecycleViewGroup

                GridLayoutManager gridLayoutManagerStatus = new GridLayoutManager(btmSheetView.getContext(),
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());

                recyclerViewStatus.setLayoutManager(gridLayoutManagerStatus);
                recyclerViewStatus.addItemDecoration(new GridSpacing(
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
                groupAdapterStatus = new GroupAdapter(btmSheetView.getContext(), listDataStatus);
                recyclerViewStatus.setAdapter(groupAdapterStatus);
                groupAdapterStatus.notifyDataSetChanged();

                //Creating RV Lokasi
                RecyclerView recyclerViewLokasi = btmSheetView.findViewById(R.id.rvLokasi);
                GroupAdapter groupAdapterLokasi;

                List<String> FBlokasiArray = new ArrayList<String>(FBlokasiArraySET); //convert stringSet to ArrayList
                List<String> listDataLokasi = FBlokasiArray; //Pass Lokasi Data From Database To RecycleViewGroup

                GridLayoutManager gridLayoutManagerLokasi = new GridLayoutManager(btmSheetView.getContext(),
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());

                recyclerViewLokasi.setLayoutManager(gridLayoutManagerLokasi);
                recyclerViewLokasi.addItemDecoration(new GridSpacing(
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
                groupAdapterLokasi = new GroupAdapter(btmSheetView.getContext(), listDataLokasi);
                recyclerViewLokasi.setAdapter(groupAdapterLokasi);
                groupAdapterLokasi.notifyDataSetChanged();

                //Creating RV Kebutuhan
                RecyclerView recyclerViewKebutuhan = btmSheetView.findViewById(R.id.rvKebutuhan);

                List<String> FBkebutuhanArray = new ArrayList<String>(FBkebutuhanArraySET); //convert stringSet to ArrayList
                List<String> listDataKebutuhan = FBkebutuhanArray; //Pass Kebutuhan Data From Database To RecycleViewGroup

                GridLayoutManager gridLayoutManagerKebutuhan = new GridLayoutManager(btmSheetView.getContext(),
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());

                recyclerViewKebutuhan.setLayoutManager(gridLayoutManagerKebutuhan);
                recyclerViewKebutuhan.addItemDecoration(new GridSpacing(
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));

                GroupAdapter groupAdapterKebutuhan = new GroupAdapter(btmSheetView.getContext(), listDataKebutuhan);
                recyclerViewKebutuhan.setAdapter(groupAdapterKebutuhan);
                groupAdapterKebutuhan.notifyDataSetChanged();

                //Creating RV Jenis
                RecyclerView recyclerViewJenis = btmSheetView.findViewById(R.id.rvJenis);
                List<String> FBjenisArray = new ArrayList<String>(FBjenisArraySET); //convert stringSet to ArrayList
                List<String> listDataJenis = FBjenisArray; //Pass Jenis Data From Database To RecycleViewGroup

                GridLayoutManager gridLayoutManagerJenis = new GridLayoutManager(btmSheetView.getContext(),
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());

                recyclerViewJenis.setLayoutManager(gridLayoutManagerJenis);
                recyclerViewJenis.addItemDecoration(new GridSpacing(
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));

                GroupAdapter groupAdapterJenis = new GroupAdapter(btmSheetView.getContext(), listDataJenis );
                recyclerViewJenis.setAdapter(groupAdapterJenis);
                groupAdapterJenis.notifyDataSetChanged();



                btmSheetDialog.setContentView(btmSheetView);
                btmSheetDialog.show();
            }
        });

        // Pembuatan BTM Navbar
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        navView = findViewById(R.id.nav_view);

        navView = (BottomNavigationView) findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_bookmark)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        int id = item.getItemId();
        System.out.println(id);
        switch(id){
            case R.id.navigation_home:
                Toast.makeText(this, "HOME", Toast.LENGTH_SHORT).show();
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                return true;
            case R.id.navigation_notifications:
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                Toast.makeText(this, "NOTIF", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.navigation_bookmark:
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                Toast.makeText(this, "BOOKMARK", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}