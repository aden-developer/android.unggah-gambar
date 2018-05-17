package com.unggah.gambar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unggah:
                startActivity(new Intent(this, Unggah.class));
                break;
            case R.id.potong_unggah:
                startActivity(new Intent(this, Potong_Unggah.class));
                break;
        }
    }
}
