package com.example.remainderjadwal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TakeQuizActivity extends AppCompatActivity {

    private TextView tvTitle, tvQuestion, tvProgress;
    private RadioGroup rgOptions;
    private Button btnNext;

    private Quiz quiz;
    private int currentIndex = 0;
    private int score = 0;
    private final ArrayList<Integer> selectedAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        // Inisialisasi View
        tvTitle = findViewById(R.id.tvTitle);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        rgOptions = findViewById(R.id.rgOptions);
        btnNext = findViewById(R.id.btnNext);

        // Ambil data quiz
        quiz = (Quiz) getIntent().getSerializableExtra("quiz");

        if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            Toast.makeText(this, "Quiz tidak valid!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(quiz.getTitle());

        // Inisialisasi array jawaban
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            selectedAnswers.add(-1);
        }

        showQuestion();

        btnNext.setOnClickListener(v -> nextQuestion());
    }

    private void showQuestion() {
        Question q = quiz.getQuestions().get(currentIndex);

        // Update Progress (contoh: 1/5)
        if (tvProgress != null) {
            tvProgress.setText((currentIndex + 1) + "/" + quiz.getQuestions().size());
        }

        // Tampilkan pertanyaan
        tvQuestion.setText(q.getPertanyaan());

        // Bersihkan dan tambah pilihan jawaban
        rgOptions.removeAllViews();

        List<String> pilihan = q.getPilihan();
        for (String option : pilihan) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            rb.setTextSize(17);
            rb.setPadding(24, 20, 24, 20);

            // Margin antar pilihan
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 8, 0, 8);
            rb.setLayoutParams(params);

            rgOptions.addView(rb);
        }

        // Restore jawaban yang sudah dipilih sebelumnya
        int prev = selectedAnswers.get(currentIndex);
        if (prev != -1 && prev < rgOptions.getChildCount()) {
            ((RadioButton) rgOptions.getChildAt(prev)).setChecked(true);
        }

        // Ubah teks tombol
        btnNext.setText(currentIndex == quiz.getQuestions().size() - 1 ? "Selesai" : "Selanjutnya");
    }

    private void nextQuestion() {
        // Simpan jawaban yang dipilih
        int selected = -1;
        for (int i = 0; i < rgOptions.getChildCount(); i++) {
            if (((RadioButton) rgOptions.getChildAt(i)).isChecked()) {
                selected = i;
                break;
            }
        }

        selectedAnswers.set(currentIndex, selected);

        // Cek jawaban benar
        Question q = quiz.getQuestions().get(currentIndex);
        String jawabanBenar = q.getJawabanBenar();

        int correctIndex = -1;
        if (jawabanBenar != null && jawabanBenar.length() == 1) {
            char letter = Character.toUpperCase(jawabanBenar.charAt(0));
            correctIndex = letter - 'A';   // A=0, B=1, C=2, D=3
        }

        if (selected == correctIndex) {
            score += 20;
        }

        currentIndex++;

        if (currentIndex < quiz.getQuestions().size()) {
            showQuestion();
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        String result = "Quiz Selesai!\n\n" +
                "Skor kamu: " + score + " / " + (quiz.getQuestions().size() * 20) + "\n" +
                "Benar: " + (score / 20) + " dari " + quiz.getQuestions().size() + " soal";

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();

        // Bisa ditambah dialog hasil yang lebih cantik nanti
        finish();
    }
}