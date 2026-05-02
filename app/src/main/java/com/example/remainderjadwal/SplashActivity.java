package com.example.remainderjadwal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 1;

    private TextView tvGreeting, tvCity, tvWelcome;
    private ImageView ivFlag, ivCityIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inisialisasi view
        tvGreeting = findViewById(R.id.tvGreeting);
        tvCity = findViewById(R.id.tvCity);
        tvWelcome = findViewById(R.id.tvWelcome);
        ivFlag = findViewById(R.id.ivFlag);
        ivCityIcon = findViewById(R.id.ivCityIcon);

        // Cek izin lokasi
        checkLocationPermission();
    }

    // Fungsi applyGradientToWelcomeText tidak perlu diubah, tapi saya pindahkan
    // pemanggilannya agar dipanggil SETELAH teks di-set.
    private void applyGradientToWelcomeText() {
        String text = tvWelcome.getText().toString();
        if (text.isEmpty()) return;

        float textWidth = tvWelcome.getPaint().measureText(text);

        if (textWidth <= 0) {
            tvWelcome.postDelayed(this::applyGradientToWelcomeText, 50);
            return;
        }

        Shader shader = new LinearGradient(
                0, 0, textWidth, tvWelcome.getTextSize(),
                new int[]{0xFF000000, 0xFFFFFFFF},
                null, Shader.TileMode.CLAMP
        );
        tvWelcome.getPaint().setShader(shader);
        tvWelcome.invalidate();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        } else {
            getLocationAndGreet();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndGreet();
        } else {
            setDefaultGreeting();
            goToMain();
        }
    }

    private void getLocationAndGreet() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            setDefaultGreeting();
            goToMain();
            return;
        }

        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location != null) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address addr = addresses.get(0);
                    String city = addr.getLocality();
                    String countryCode = addr.getCountryCode();

                    tvCity.setText(city != null ? city : "Unknown City");

                    if (city != null && city.toLowerCase().contains("bekasi")) {
                        tvGreeting.setText("Halo, Bekasi!");
                        ivCityIcon.setImageResource(R.drawable.ic_bekasi);
                    } else {
                        tvGreeting.setText("Halo!");
                        ivCityIcon.setImageResource(R.drawable.ic_city);
                    }

                    // --- BLOK KODE YANG DIPERBARUI ---
                    if ("ID".equalsIgnoreCase(countryCode)) {
                        // Jika di Indonesia
                        ivFlag.setImageResource(R.drawable.flag_id);
                        tvWelcome.setText("Selamat datang di aplikasi\nReminder Kuliah"); // BARU: Set teks Bhs. Indonesia
                    } else {
                        // Jika di Luar Indonesia
                        ivFlag.setImageResource(R.drawable.flag_us);
                        tvWelcome.setText("Welcome to the\nCollege Reminder application"); // BARU: Set teks Bhs. Inggris
                    }
                    // ---------------------------------

                } else {
                    setDefaultGreeting();
                }
            } else {
                setDefaultGreeting();
            }
        } catch (IOException e) {
            e.printStackTrace();
            setDefaultGreeting();
        }

        // BARU: Panggil efek gradasi SETELAH teks diubah
        tvWelcome.post(this::applyGradientToWelcomeText);

        goToMain();
    }

    private void setDefaultGreeting() {
        tvGreeting.setText("Halo, Bekasi!");
        tvCity.setText("Bekasi");
        // BARU: Tambahkan ini untuk memastikan teks defaultnya benar
        tvWelcome.setText("Selamat datang di aplikasi\nReminder Kuliah");
        ivCityIcon.setImageResource(R.drawable.ic_bekasi);
        ivFlag.setImageResource(R.drawable.flag_id);
    }

    private void goToMain() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 3000);
    }
}
