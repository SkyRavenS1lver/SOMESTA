package com.example.somesta.Marker;

import org.osmdroid.util.GeoPoint;

public class Perusahaan {
    String dilayani;
    String group;
    String jenis;
    String kebutuhan;
    GeoPoint location;
    String tempat;
    String nama;
    String pelayanan;
    String penyalur;
    String status;
    String tipeCustomer;

    public Perusahaan(String dilayani, String group, String jenis, String kebutuhan, GeoPoint location, String tempat, String nama, String pelayanan, String penyalur, String status, String tipeCustomer) {
        this.dilayani = dilayani;
        this.group = group;
        this.jenis = jenis;
        this.kebutuhan = kebutuhan;
        this.location = location;
        this.tempat = tempat;
        this.nama = nama;
        this.pelayanan = pelayanan;
        this.penyalur = penyalur;
        this.status = status;
        this.tipeCustomer = tipeCustomer;
    }

    public void setDilayani(String dilayani) {
        this.dilayani = dilayani;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public void setKebutuhan(String kebutuhan) {
        this.kebutuhan = kebutuhan;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public void setTempat(String tempat) {
        this.tempat = tempat;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setPelayanan(String pelayanan) {
        this.pelayanan = pelayanan;
    }

    public void setPenyalur(String penyalur) {
        this.penyalur = penyalur;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTipeCustomer(String tipeCustomer) {
        this.tipeCustomer = tipeCustomer;
    }

    public String getDilayani() {
        return dilayani;
    }

    public String getGroup() {
        return group;
    }

    public String getJenis() {
        return jenis;
    }

    public String getKebutuhan() {
        return kebutuhan;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getTempat() {
        return tempat;
    }

    public String getNama() {
        return nama;
    }

    public String getPelayanan() {
        return pelayanan;
    }

    public String getPenyalur() {
        return penyalur;
    }

    public String getStatus() {
        return status;
    }

    public String getTipeCustomer() {
        return tipeCustomer;
    }
}
