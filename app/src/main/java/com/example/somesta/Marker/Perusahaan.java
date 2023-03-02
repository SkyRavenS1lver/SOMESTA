package com.example.somesta.Marker;

import org.osmdroid.util.GeoPoint;

public class Perusahaan {
    String kompetitor;
    String group;
    String jenis;
    String kebutuhan;
    GeoPoint location;
    String tempat;
    String nama;
    String layanan;
    String penyalur;
    String status;
    String tipeCustomer;
    String market_share;

    public Perusahaan(String kompetitor, String group, String jenis, String kebutuhan, GeoPoint location, String tempat, String nama, String layanan, String penyalur, String status, String tipeCustomer, String market_share) {
        this.kompetitor = kompetitor;
        this.group = group;
        this.jenis = jenis;
        this.kebutuhan = kebutuhan;
        this.location = location;
        this.tempat = tempat;
        this.nama = nama;
        this.layanan = layanan;
        this.penyalur = penyalur;
        this.status = status;
        this.tipeCustomer = tipeCustomer;
        this.market_share = market_share;
    }

    public String getKompetitor() {
        return kompetitor;
    }

    public void setKompetitor(String kompetitor) {
        this.kompetitor = kompetitor;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getKebutuhan() {
        return kebutuhan;
    }

    public void setKebutuhan(String kebutuhan) {
        this.kebutuhan = kebutuhan;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getTempat() {
        return tempat;
    }

    public void setTempat(String tempat) {
        this.tempat = tempat;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getLayanan() {
        return layanan;
    }

    public void setLayanan(String layanan) {
        this.layanan = layanan;
    }

    public String getPenyalur() {
        return penyalur;
    }

    public void setPenyalur(String penyalur) {
        this.penyalur = penyalur;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipeCustomer() {
        return tipeCustomer;
    }

    public void setTipeCustomer(String tipeCustomer) {
        this.tipeCustomer = tipeCustomer;
    }

    public String getMarket_share() {
        return market_share;
    }

    public void setMarket_share(String market_share) {
        this.market_share = market_share;
    }
}