package com.example.remainderjadwal;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String pertanyaan;          // "pertanyaan" dari JSON
    private List<String> pilihan;       // "pilihan" dari JSON (List<String>)
    private String jawaban_benar;       // "jawaban_benar" dari JSON ("A", "B", "C", "D")

    // Constructor kosong wajib buat Gson parse JSON
    public Question() {}

    // Constructor lengkap (Sudah dibenerin dari typo sebelumnya)
    public Question(String pertanyaan, List<String> pilihan, String jawaban_benar) {
        this.pertanyaan = pertanyaan; // SEBELUMNYA: peejarahrtanyaan
        this.pilihan = pilihan;
        this.jawaban_benar = jawaban_benar;
    }

    // Getter (wajib buat Gson & tampilin di UI)
    public String getPertanyaan() {
        return pertanyaan;
    }

    public List<String> getPilihan() {
        return pilihan;
    }

    public String getJawabanBenar() {
        return jawaban_benar;
    }

    // Helper: dapetin index jawaban benar (0-3) buat cek jawaban user
    public int getCorrectIndex() {
        if (jawaban_benar == null) return -1;
        String upper = jawaban_benar.toUpperCase();
        switch (upper) {
            case "A": return 0;
            case "B": return 1;
            case "C": return 2;
            case "D": return 3;
            default: return -1;
        }
    }

    // Helper: dapetin teks pilihan berdasarkan index (0-3) buat tampilin radio button
    public String getOptionAt(int index) {
        if (pilihan == null || index < 0 || index >= pilihan.size()) {
            return "";
        }
        return pilihan.get(index);
    }
}