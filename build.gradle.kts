// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    // TIDAK ADA kotlin-android → KARENA KODE JAVA (tetap diperbolehkan)

    // ← BARIS INI YANG DITAMBAHKAN UNTUK GOOGLE SERVICES
    id("com.google.gms.google-services") version "4.4.4" apply false
}