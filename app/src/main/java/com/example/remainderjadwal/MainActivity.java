package com.example.remainderjadwal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Schedule> list = new ArrayList<>(); // Inisialisasi langsung untuk hindari NullPointer
    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Penting: SetTheme sebelum setContentView jika kamu pakai tema custom
        setContentView(R.layout.activity_main);

        // 1. Inisialisasi view
        RecyclerView rv = findViewById(R.id.rvSchedule);
        ImageButton btnAdd = findViewById(R.id.btnAdd);
        ImageButton btnTheme = findViewById(R.id.btnTheme);
        Button btnQuiz = findViewById(R.id.btnQuiz);

        // 2. Setup RecyclerView (Lakukan ini sebelum load data agar adapter siap)
        adapter = new ScheduleAdapter(this, list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // 3. Tombol Tambah Jadwal
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddScheduleActivity.class));
        });

        // 4. Tombol Ganti Tema (Perbaikan Logika)
        btnTheme.setOnClickListener(v -> {
            int currentMode = AppCompatDelegate.getDefaultNightMode();
            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            // recreate() tidak wajib di versi library terbaru, tapi baik untuk memastikan stabilitas warna
        });

        // 5. Tombol Kuis
        btnQuiz.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, QuizListActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        // Load data dari storage
        ArrayList<Schedule> updatedList = ScheduleStorage.getSchedules(this);
        if (updatedList != null) {
            list.clear();
            list.addAll(updatedList);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}