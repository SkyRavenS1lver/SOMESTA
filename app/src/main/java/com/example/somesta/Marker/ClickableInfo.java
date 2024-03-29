package com.example.somesta.Marker;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.somesta.Activity.MainActivity;
import com.example.somesta.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class ClickableInfo extends InfoWindow {
    private Perusahaan perusahaan;
    public ClickableInfo(int layoutResId, MapView mapView, Perusahaan perusahaan) {
        super(layoutResId, mapView);
        this.perusahaan = perusahaan;
    }

    @Override
    public void onOpen(Object item) {
        showIngfo(MainActivity.ViewPerusahaan);
    }

    public Perusahaan getPerusahaan() {
        return perusahaan;
    }

    public void showIngfo(View btmView){
        //Setter Text BTM sheet
        TextView tv1 = btmView.findViewById(R.id.namaPerusahaan);
        tv1.setText(perusahaan.nama);
        TextView tv2 = btmView.findViewById(R.id.txtDilayani);
        tv2.setText(perusahaan.kompetitor);
        TextView tv3 = btmView.findViewById(R.id.txtGroup);
        tv3.setText(perusahaan.group);
        TextView tv4 = btmView.findViewById(R.id.txtJenis);
        tv4.setText(perusahaan.jenis);
        TextView tv5 = btmView.findViewById(R.id.txtKebutuhan);
        tv5.setText(perusahaan.kebutuhan);
        TextView tv6 = btmView.findViewById(R.id.txtKoordinat);
        String latitude = perusahaan.location.getLatitude()+"";
        String longitude = perusahaan.location.getLongitude()+"";
        tv6.setText(latitude+" ,\n "+longitude);
        TextView tv7 = btmView.findViewById(R.id.txtLokasi);
        tv7.setText(perusahaan.tempat);
        TextView tv8 = btmView.findViewById(R.id.txtPelayanan);
        tv8.setText(perusahaan.layanan);
        TextView tv9 = btmView.findViewById(R.id.txtPenyalur);
        tv9.setText(perusahaan.penyalur);
        TextView tv10 = btmView.findViewById(R.id.txtStatus);
        tv10.setText(perusahaan.status);
        TextView tv11 = btmView.findViewById(R.id.txtTipe);
        tv11.setText(perusahaan.tipeCustomer);
        TextView tv12 = btmView.findViewById(R.id.marketShare);
        tv12.setText(perusahaan.market_share);
        MainActivity.map.getController().setZoom(19);
        MainActivity.dialogPerusahaan.setContentView(btmView);
        MainActivity.dialogPerusahaan.show();
    }

    @Override
    public void onClose() {
    }
}
