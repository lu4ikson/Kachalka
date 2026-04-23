package com.example.kachalka;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16,16,16,16);

        scrollView.addView(container);

        setContentView(scrollView);

        loadHistory();
    }

    private void loadHistory() {
        try {
            SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
            String json = prefs.getString("days", "[]");

            JSONArray days = new JSONArray(json);

            for (int d = days.length() - 1; d >= 0; d--) {

                int indexToDelete = d; // важно для удаления

                JSONObject day = days.getJSONObject(d);

                long time = day.getLong("date");
                String date = new SimpleDateFormat("dd.MM.yyyy",
                        Locale.getDefault()).format(new Date(time));

                TextView dateView = new TextView(this);
                dateView.setText("📅 " + date);
                dateView.setTextSize(18);
                container.addView(dateView);

                // КНОПКА УДАЛЕНИЯ
                Button deleteBtn = new Button(this);
                deleteBtn.setText("Удалить");

                deleteBtn.setOnClickListener(v -> deleteDay(indexToDelete));

                container.addView(deleteBtn);

                JSONArray exercises = day.getJSONArray("exercises");

                for (int i = 0; i < exercises.length(); i++) {

                    JSONObject ex = exercises.getJSONObject(i);

                    TextView exView = new TextView(this);
                    exView.setText(ex.getString("name"));
                    container.addView(exView);

                    JSONArray sets = ex.getJSONArray("sets");

                    for (int j = 0; j < sets.length(); j++) {
                        JSONObject s = sets.getJSONObject(j);

                        TextView setView = new TextView(this);
                        setView.setText("  " +
                                s.getInt("w") + " кг x " +
                                s.getInt("r"));

                        container.addView(setView);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteDay(int index) {
        try {
            SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
            String json = prefs.getString("days", "[]");

            JSONArray days = new JSONArray(json);

            JSONArray newDays = new JSONArray();

            for (int i = 0; i < days.length(); i++) {
                if (i != index) {
                    newDays.put(days.get(i));
                }
            }

            prefs.edit().putString("days", newDays.toString()).apply();

            // перерисовываем экран
            container.removeAllViews();
            loadHistory();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}