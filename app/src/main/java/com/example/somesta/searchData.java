package com.example.somesta;

public class searchData {

    private String namaPerusahaan;
    private String lokasiPerusahaan;

    //constructors

    public searchData(String namaPerusahaan, String lokasiPerusahaan) {
        this.namaPerusahaan = namaPerusahaan;
        this.lokasiPerusahaan = lokasiPerusahaan;
    }


    //Getters

    public String getNamaPerusahaan() {
        return namaPerusahaan;
    }

    public String getLokasiPerusahaan() {
        return lokasiPerusahaan;
    }
}
