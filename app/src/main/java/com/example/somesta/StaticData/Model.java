package com.example.somesta.StaticData;

import com.example.somesta.Marker.Perusahaan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Model {
    public static HashSet<String> groupClicked = new HashSet<>();
    public static HashSet<String> temp = new HashSet<>();
    public static HashSet<String> statusClicked = new HashSet<>();
    public static HashSet<String> jenisClicked = new HashSet<>();
    public static HashSet<String> kebutuhanClicked = new HashSet<>();
    public static HashSet<String> lokasiClicked = new HashSet<>();
    public static Set<String> FBjenisArraySET = new HashSet<String>();
    public static Set<String> FBstatusArraySET= new HashSet<String>() ;
    public static Set<String> FBlokasiArraySET = new HashSet<String>();
    public static Set<String> FBkebutuhanArraySET = new HashSet<String>();
    public static Set<String> FBgroupArraySET = new HashSet<String>();
    //Semua data perusahaan disimpan di array ini
    private static ArrayList<Perusahaan> perusahaanArrayList = new ArrayList<>();
    //Array untuk menyimpan data yang ter-filter untuk searchView
    private static ArrayList<Perusahaan> perusahaanArrayListFiltered = new ArrayList<>();

    public static ArrayList<Perusahaan> getPerusahaanArrayList() {
        return perusahaanArrayList;
    }

    public static void setPerusahaanArrayList(ArrayList<Perusahaan> perusahaanArrayList) {
        Model.perusahaanArrayList = perusahaanArrayList;
    }

    public static ArrayList<Perusahaan> getPerusahaanArrayListFiltered() {
        return perusahaanArrayListFiltered;
    }

    public static void setPerusahaanArrayListFiltered(ArrayList<Perusahaan> perusahaanArrayListFiltered) {
        Model.perusahaanArrayListFiltered = perusahaanArrayListFiltered;
    }
}
