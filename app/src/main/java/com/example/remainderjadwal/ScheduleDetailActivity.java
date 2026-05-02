package com.example.remainderjadwal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        // Inisialisasi view
        TextView tvDosen = findViewById(R.id.tvDosen);
        TextView tvRuangan = findViewById(R.id.tvRuangan);
        TextView tvJam = findViewById(R.id.tvJam);
        TextView tvCatatan = findViewById(R.id.tvCatatan);
        Button btnBack = findViewById(R.id.btnBack);

        // Ambil data dari intent (PAKAI PARCELABLE, BUKAN SERIALIZABLE!)
        Schedule s = getIntent().getParcelableExtra("schedule");

        // Cek apakah data tidak null
        if (s != null) {
            tvDosen.setText("Dosen: " + s.getDosen());
            tvRuangan.setText("Ruangan: " + s.getRuangan());
            tvJam.setText("Jam: " + s.getJam());
            String catatan = s.getCatatan();
            tvCatatan.setText("Catatan: " + (catatan == null || catatan.isEmpty() ? "-" : catatan));
        } else {
            Toast.makeText(this, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish(); // Keluar dari halaman detail
        }

        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());
    }
}