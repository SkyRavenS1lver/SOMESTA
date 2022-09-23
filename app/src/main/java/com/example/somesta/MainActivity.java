package com.example.somesta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pembuatan sheet dialog
        FloatingActionButton filter_btn = (FloatingActionButton) findViewById(R.id.filter);
        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog btmSheetDialog = new BottomSheetDialog(
                        MainActivity.this, R.style.BottomSheetDialogTheme);
                View btmSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.btm_sheet, (FrameLayout)findViewById(R.id.shiet));

                // Creating the RV for group
                RecyclerView recyclerView = btmSheetView.findViewById(R.id.rvGroup);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(btmSheetView.getContext(),
                        LinearLayoutManager.HORIZONTAL, false);;
                GroupAdapter groupAdapter;
                List<String> listData = Arrays.asList(getResources().getStringArray(R.array.dataGroup));

                GridLayoutManager gridLayoutManager = new GridLayoutManager(btmSheetView.getContext(),
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateNoOfColumns());
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.addItemDecoration(new GridSpacing(
                        (new Utility.ColumnQty(btmSheetView.getContext(),R.layout.filter_data)).calculateSpacing()));
                groupAdapter = new GroupAdapter(btmSheetView.getContext(), listData);
                recyclerView.setAdapter(groupAdapter);
                groupAdapter.notifyDataSetChanged();



                // Filter Button inside btm sheet dialog
//                btmSheetView.findViewById(R.id.filterConfirm).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        btmSheetDialog.dismiss();
//                    }
//                });
                btmSheetDialog.setContentView(btmSheetView);
                btmSheetDialog.show();
            }
        });

        // Pembuatan BTM Navbar
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_bookmark)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}