package com.example.remainderjadwal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.QuotaExceededException;
import com.google.ai.client.generativeai.type.ServerException;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QuizListActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<Quiz> quizzes;
    private EditText etTopic;
    private Button btnGenerateAI;
    private ProgressBar progressBar;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        lv = findViewById(R.id.lvQuizzes);
        etTopic = findViewById(R.id.etTopic);
        btnGenerateAI = findViewById(R.id.btnGenerateAI);
        progressBar = findViewById(R.id.progressBar);

        Button btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, CreateQuizActivity.class)));

        btnGenerateAI.setOnClickListener(v -> {
            String topic = etTopic.getText().toString().trim();
            if (topic.isEmpty()) {
                Toast.makeText(this, "Masukkan topik kuis dulu!", Toast.LENGTH_SHORT).show();
                return;
            }
            generateQuizWithAI(topic);
        });

        refreshList();

        // Klik biasa = Kerjakan Quiz
        lv.setOnItemClickListener((parent, view, position, id) -> {
            if (quizzes != null && position < quizzes.size()) {
                Quiz q = quizzes.get(position);
                Intent i = new Intent(this, TakeQuizActivity.class);
                i.putExtra("quiz", q);
                startActivity(i);
            }
        });

        // Long Press = Hapus Quiz
        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            if (quizzes == null || position >= quizzes.size()) return true;

            Quiz q = quizzes.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Hapus Kuis?")
                    .setMessage("Yakin ingin menghapus\n\"" + q.getTitle() + "\"?")
                    .setPositiveButton("Ya, Hapus", (dialog, which) -> {
                        quizzes.remove(position);
                        QuizStorage.saveQuizzes(this, quizzes);
                        refreshList();
                        Toast.makeText(this, "Kuis berhasil dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Batal", null)
                    .show();

            return true; // Penting! agar tidak memicu klik biasa
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        quizzes = QuizStorage.getQuizzes(this);
        if (quizzes == null) quizzes = new ArrayList<>();

        String[] titles = new String[quizzes.size()];
        for (int i = 0; i < quizzes.size(); i++) {
            titles[i] = quizzes.get(i).getTitle() + " (" + quizzes.get(i).getQuestions().size() + " soal)";
        }
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
    }

    private void generateQuizWithAI(String topic) {
        progressBar.setVisibility(View.VISIBLE);
        btnGenerateAI.setEnabled(false);

        executor.execute(() -> {
            int maxRetries = 2;

            for (int attempt = 0; attempt <= maxRetries; attempt++) {
                try {
                    GenerativeModel model = new GenerativeModel(
                            "gemini-2.5-flash",
                            BuildConfig.GEMINI_API_KEY
                    );

                    GenerativeModelFutures modelFutures = GenerativeModelFutures.from(model);

                    String promptText = "Buat 5 soal pilihan ganda tentang \"" + topic + "\". "
                            + "Gunakan bahasa Indonesia. "
                            + "Balas HANYA dengan JSON array, tanpa penjelasan apapun. "
                            + "Format: [{\"pertanyaan\":\"...\",\"pilihan\":[\"A. ...\",\"B. ...\",\"C. ...\",\"D. ...\"],\"jawaban_benar\":\"A\"}]";

                    Content content = new Content.Builder().addText(promptText).build();

                    ListenableFuture<GenerateContentResponse> future = modelFutures.generateContent(content);
                    GenerateContentResponse response = future.get(60, TimeUnit.SECONDS);

                    String jsonResponse = response.getText();
                    Log.d("QuizAI", "Raw Response: " + jsonResponse);

                    if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                        throw new Exception("AI tidak mengembalikan respon");
                    }

                    jsonResponse = jsonResponse.replace("```json", "")
                            .replace("```", "")
                            .trim();

                    int start = jsonResponse.indexOf("[");
                    int end = jsonResponse.lastIndexOf("]");
                    if (start != -1 && end != -1 && end > start) {
                        jsonResponse = jsonResponse.substring(start, end + 1);
                    }

                    Log.d("QuizAI", "Clean JSON: " + jsonResponse);

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Question>>() {}.getType();
                    List<Question> questions = gson.fromJson(jsonResponse, listType);

                    if (questions == null || questions.isEmpty()) {
                        throw new Exception("Format JSON salah atau soal kosong");
                    }

                    Quiz newQuiz = new Quiz();
                    newQuiz.setTitle("Kuis " + topic + " (AI Generated)");
                    newQuiz.setQuestions(new ArrayList<>(questions));

                    quizzes.add(newQuiz);
                    QuizStorage.saveQuizzes(this, quizzes);

                    mainHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnGenerateAI.setEnabled(true);
                        Toast.makeText(this, "✅ Kuis berhasil dibuat!", Toast.LENGTH_SHORT).show();
                        refreshList();
                    });
                    return;

                } catch (QuotaExceededException e) {
                    Log.e("QuizAI", "Quota Exceeded", e);
                    if (attempt < maxRetries) {
                        try { Thread.sleep(45000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                        continue;
                    }
                    showError("❌ Quota habis. Tunggu beberapa menit atau enable billing.");

                } catch (ServerException e) {
                    Log.e("QuizAI", "Server Error", e);
                    if (attempt < maxRetries) {
                        try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                        continue;
                    }
                    showError("❌ Model tidak ditemukan. Gunakan gemini-2.5-flash");

                } catch (Exception e) {
                    Log.e("QuizAI", "Error", e);
                    if (attempt < maxRetries) {
                        try { Thread.sleep(4000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                        continue;
                    }
                    showError("❌ Gagal: " + e.getMessage());
                }
            }

            // Jika semua retry gagal
            mainHandler.post(() -> {
                progressBar.setVisibility(View.GONE);
                btnGenerateAI.setEnabled(true);
            });
        });
    }

    private void showError(String message) {
        mainHandler.post(() -> {
            progressBar.setVisibility(View.GONE);
            btnGenerateAI.setEnabled(true);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}