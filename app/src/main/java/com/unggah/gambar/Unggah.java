package com.unggah.gambar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Unggah extends AppCompatActivity {

    //Deklarasi variabel
    ImageView gambar;
    Bitmap bitmap;
    final int KODE_AKTIVITAS_GALERI = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unggah);

        //Inisialisasi variabel
        gambar = (ImageView) findViewById(R.id.gambar);

        //Perintahkan gambar
        gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        Unggah.this,
                        new String[] {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        KODE_AKTIVITAS_GALERI
                );
            }
        });
    }

    //Rekues aktivitas


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Seleksi jika kode sama yang di perintahkan
        if(requestCode == KODE_AKTIVITAS_GALERI) {
            //Panggil aktivitas galeri
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Pilih gambar"), KODE_AKTIVITAS_GALERI);
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada aktivitas yang terdeteksi !", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Hasil pengeluaran dari aktivitas baru yang di panggil
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Seleksi jika kode sama dengan yang di perintahkan
        if(requestCode == KODE_AKTIVITAS_GALERI) {
            //Seleksi jika pengeluaran sukses
            if(resultCode == RESULT_OK && data != null) {
                //Dapatkan lokasi penyimpanan gambar
                Uri alamat_tmp = data.getData();
                //Handling kesalahan
                try {
                    InputStream alur_masuk_data = getContentResolver().openInputStream(alamat_tmp);
                    bitmap = BitmapFactory.decodeStream(alur_masuk_data);
                    //Rubah gambar dengan hasil data yang telah di dapat
                    gambar.setImageURI(alamat_tmp);
                    //Unggah gambar
                    unggah();
                } catch (FileNotFoundException kesalahan) {
                    kesalahan.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Dapatkan data base64 pada gambar
    private String gambar(Bitmap bitmap) {
        //Hasilkan data bit larik pada gambar
        ByteArrayOutputStream alur_pengeluaran_data = new ByteArrayOutputStream();
        //Optimasi gambar
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, alur_pengeluaran_data);
        //Hasilkan bit gambar
        byte[] bit_gambar = alur_pengeluaran_data.toByteArray();
        //Enkode bit larik ke-Base64
        String enkode_gambar = Base64.encodeToString(bit_gambar, Base64.DEFAULT);
        //Hasil
        return enkode_gambar;
    }

    //Unggah gambar {Volley}
    private void unggah() {
        Toast.makeText(getApplicationContext(), "Mengunggah gambar keserver", Toast.LENGTH_LONG).show();
        StringRequest rekues_akses_server = new StringRequest(
                Request.Method.POST,
                "http://192.168.2.10/prosedural_master/kontrol/api/mobile/latihan/unggah-gambar/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("HASIL", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError kesalahan) {
                        kesalahan.printStackTrace();
                    }
                }
            ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameter = new HashMap<>();
                parameter.put("gambar", gambar(bitmap).toString());
                return parameter;
            }
        };
        //Masuk dalam antrian akses server
        RequestQueue rekues_akses = Volley.newRequestQueue(this);
        rekues_akses.add(rekues_akses_server);
    }
}
