package com.example.somesta.SearchRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.Activity.MainActivity;
import com.example.somesta.Marker.Perusahaan;
import com.example.somesta.R;
import com.example.somesta.StaticData.Model;
import com.j256.ormlite.stmt.query.In;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashSet;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private final ArrayList<Perusahaan> values;
    private final LayoutInflater inflater;

    public SearchAdapter(Context context, ArrayList<Perusahaan> values) {
        this.values = values;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        final Perusahaan dataPerusahaan = values.get(position);
        holder.textViewNamaPerusahaan.setText(String.valueOf(dataPerusahaan.getNama()));
        holder.textViewLokasiPerusahaan.setText(String.valueOf(dataPerusahaan.getTempat()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), String.valueOf(dataPerusahaan.getNama()), Toast.LENGTH_SHORT).show();
                MainActivity.searchView.setSearchText(String.valueOf(dataPerusahaan.getNama()));
                IMapController mapController = MainActivity.map.getController();
                GeoPoint jogja = new GeoPoint(dataPerusahaan.getLocation());
                mapController.setCenter(jogja);
                mapController.setZoom(19);
                //Clear Marker Logic
//                HashSet<String> a =  new HashSet<>(Model.groupClicked);
//                HashSet<String> b =  new HashSet<>(Model.statusClicked);
//                HashSet<String> c =  new HashSet<>(Model.jenisClicked);
//                HashSet<String> d =  new HashSet<>(Model.kebutuhanClicked);
//                HashSet<String> e =  new HashSet<>(Model.lokasiClicked);
                Model.groupClicked.clear();
                Model.statusClicked.clear();
                Model.jenisClicked.clear();
                Model.kebutuhanClicked.clear();
                Model.lokasiClicked.clear();
                Model.getPerusahaanArrayListFiltered().clear();
                MainActivity.filterResets.performClick();
                clearData();
//                Model.groupClicked = new HashSet<>(a);
//                Model.statusClicked= new HashSet<>(b);
//                Model.jenisClicked= new HashSet<>(c);
//                Model.kebutuhanClicked= new HashSet<>(d);
//                Model.lokasiClicked= new HashSet<>(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNamaPerusahaan;
        TextView textViewLokasiPerusahaan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNamaPerusahaan = itemView.findViewById(R.id.textViewNamaPerusahaan);
            textViewLokasiPerusahaan = itemView.findViewById(R.id.textViewLokasiPerusahaan);
        }
    }
    public void clearData() {
        values.clear();
        notifyDataSetChanged();
    }
}
