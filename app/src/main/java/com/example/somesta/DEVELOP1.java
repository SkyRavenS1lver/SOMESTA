package com.example.somesta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class DEVELOP1 extends AppCompatActivity {

    SearchView searchView;
    GroupAdapter itemAdapter;
    RecyclerView normal_recycler;
    private searchAdapter adapter;
    private ArrayList<String> cars = new ArrayList<String>();
    private ArrayList<searchData> perusahaanArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_develop1);

        normal_recycler = findViewById(R.id.normal_recycler);
        normal_recycler.setLayoutManager(new LinearLayoutManager(this));
        perusahaanArrayList = new ArrayList<>();
        adapter = new searchAdapter(DEVELOP1.this, perusahaanArrayList);
        normal_recycler.setAdapter(adapter);
        normal_recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter.setOnItemClickListenerCUSTOM(new searchAdapter.onItemClickListenerCUSTOM() {
            @Override
            public void onItemClick(int position) {
                String nama = perusahaanArrayList.get(position).getNamaPerusahaan();
                Toast.makeText(DEVELOP1.this, nama, Toast.LENGTH_SHORT).show();
            }
        });

        createListData();

//        searchView = findViewById(R.id.searchView);
//        searchView.clearFocus();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                filterList(s);
//                return true;
//            }
//        });
//        cars.add("Volvo");
//        cars.add("BMW");
//        cars.add("Ford");
//        cars.add("Mazda");
//
//        LinearLayout linearLayoutParent = findViewById(R.id.linearLayoutParent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void createListData() {
        // Add data to the RecycleView
        searchData dataPerusahaan = new searchData("PT SEXOPHONE AUDIO", "JOGJA");
        perusahaanArrayList.add(dataPerusahaan);

        dataPerusahaan = new searchData("PT SURAKARTA", "SURAKARTA");
        perusahaanArrayList.add(dataPerusahaan);
        dataPerusahaan = new searchData("PT PAPUA", "PAPUA");
        perusahaanArrayList.add(dataPerusahaan);
        System.out.println(perusahaanArrayList);
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        adapter.notifyDataSetChanged();
    }

    private String getDataOnPosition(int position){
        return "Data: "+ perusahaanArrayList.get(position);
    }

    private void filterList(String text){
        List<String> filteredList = new ArrayList<>();
        for (String car : cars){
            if (car.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(car);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(this, "Data Not Found", Toast.LENGTH_SHORT).show();
        }

    }
}