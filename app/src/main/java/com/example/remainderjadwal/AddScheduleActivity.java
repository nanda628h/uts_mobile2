package com.example.remainderjadwal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddScheduleActivity extends AppCompatActivity {

    private EditText etDosen, etRuangan, etJam, etCatatan;
    private Spinner spReminder;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        // Inisialisasi view
        etDosen = findViewById(R.id.etDosen);
        etRuangan = findViewById(R.id.etRuangan);
        etJam = findViewById(R.id.etJam);
        etCatatan = findViewById(R.id.etCatatan);
        spReminder = findViewById(R.id.spReminder);
        btnSave = findViewById(R.id.btnSave);

        // Setup Spinner
        String[] reminderOptions = {"Tanpa Pengingat", "10 menit sebelum", "5 menit sebelum"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, reminderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReminder.setAdapter(adapter);

        // Tombol Simpan
        btnSave.setOnClickListener(v -> {
            String dosen = etDosen.getText().toString().trim();
            String ruangan = etRuangan.getText().toString().trim();
            String jam = etJam.getText().toString().trim();
            String catatan = etCatatan.getText().toString().trim();

            // Validasi input wajib
            if (dosen.isEmpty() || ruangan.isEmpty() || jam.isEmpty()) {
                Toast.makeText(this, "Isi Dosen, Ruangan, dan Jam terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;  // jangan lanjut save
            }

            // Ambil reminder
            int reminderMinutes = 0;
            int selectedPos = spReminder.getSelectedItemPosition();
            if (selectedPos == 1) {
                reminderMinutes = 10;
            } else if (selectedPos == 2) {
                reminderMinutes = 5;
            }

            // Buat objek Schedule
            Schedule s = new Schedule(dosen, ruangan, jam, catatan, reminderMinutes);

            // Simpan ke storage
            ArrayList<Schedule> list = ScheduleStorage.getSchedules(this);
            list.add(s);
            ScheduleStorage.saveSchedules(this, list);

            // Jadwalkan alarm kalau ada reminder
            if (reminderMinutes > 0) {
                AlarmUtil.scheduleReminder(this, s);
            }

            // Hide keyboard manual sebelum finish (kurangin ImeTracker error)
            hideKeyboard();

            // Cek kalau activity masih hidup sebelum finish
            if (!isFinishing()) {
                finish();
            }
        });
    }

    // Method helper buat hide keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}