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
        showIngfo(this.getView());
    }
    public void showIngfo(View view){
        BottomSheetDialog btmSheetDialog = new BottomSheetDialog(
                view.getContext(), R.style.BottomSheetDialogTheme);

        View btmSheetView = LayoutInflater.from(view.getContext().getApplicationContext())
                .inflate(R.layout.info_perusahaan, (FrameLayout) view.findViewById(R.id.sheets2));

        FrameLayout btmView = (FrameLayout) btmSheetView.findViewById(R.id.sheets2);
        //Setter Text BTM sheet
        TextView tv1 = btmView.findViewById(R.id.namaPerusahaan);
        tv1.setText(perusahaan.nama);
        TextView tv2 = btmView.findViewById(R.id.txtDilayani);
        tv2.setText(perusahaan.dilayani);
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
        tv8.setText(perusahaan.pelayanan);
        TextView tv9 = btmView.findViewById(R.id.txtPenyalur);
        tv9.setText(perusahaan.penyalur);
        TextView tv10 = btmView.findViewById(R.id.txtStatus);
        tv10.setText(perusahaan.status);
        TextView tv11 = btmView.findViewById(R.id.txtTipe);
        tv11.setText(perusahaan.tipeCustomer);

        BottomSheetBehavior.from(btmView).setState(BottomSheetBehavior.STATE_EXPANDED);
        MainActivity.map.getController().setZoom(19);
        btmSheetDialog.setContentView(btmSheetView);
        btmSheetDialog.show();
    }

    @Override
    public void onClose() {

    }
}
