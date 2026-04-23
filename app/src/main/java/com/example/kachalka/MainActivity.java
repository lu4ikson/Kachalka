package com.example.kachalka;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LinearLayout container;
    List<Exercise> exercises = new ArrayList<>();

    String[] exerciseNames = {
            "Румынская тяга",
            "Жим гантелей сидя",
            "Тяга горизонтального блока",
            "Жим гантелей под углом",
            "Сгибание ног в тренажёре",
            "Подъём гантелей на бицепс"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        // создаём упражнения
        for (String name : exerciseNames) {
            Exercise ex = new Exercise(name);
            exercises.add(ex);
        }

        // загружаем сохранённые значения
        loadState();

        // рисуем UI
        for (Exercise ex : exercises) {
            addExerciseView(ex);
        }

        // 👇 ВОТ СЮДА ВСТАВЛЯЕШЬ
        Button btnSave = findViewById(R.id.btnSave);
        Button btnHistory = findViewById(R.id.btnHistory);

        btnSave.setOnClickListener(v -> {
            saveDay();
            saveState();
        });

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
    }

    private void addExerciseView(Exercise ex) {

        TextView title = new TextView(this);
        title.setText(ex.name);
        title.setTextSize(18);
        title.setPadding(0, 20, 0, 10);
        container.addView(title);

        // ===== ВЕС =====
        LinearLayout weightRow = new LinearLayout(this);
        weightRow.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < 3; i++) {

            LinearLayout block = new LinearLayout(this);
            block.setOrientation(LinearLayout.HORIZONTAL);
            block.setPadding(10, 0, 10, 0);
            block.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            Button minus = new Button(this);
            minus.setText("-");
            minus.setTextSize(16);
            minus.setLayoutParams(new LinearLayout.LayoutParams(
                    100, 100   // ширина, высота
            ));

            TextView weight = new TextView(this);
            weight.setText(String.valueOf(ex.weights[i]));
            weight.setTextSize(18);
            weight.setPadding(10, 0, 10, 0);

            Button plus = new Button(this);
            plus.setText("+");
            plus.setTextSize(16);
            plus.setLayoutParams(new LinearLayout.LayoutParams(
                    100, 100
            ));

            int index = i;

            minus.setOnClickListener(v -> {
                if (ex.weights[index] > 0) {
                    ex.weights[index] -= 5;
                    weight.setText(String.valueOf(ex.weights[index]));
                }
            });

            plus.setOnClickListener(v -> {
                ex.weights[index] += 5;
                weight.setText(String.valueOf(ex.weights[index]));
            });

            block.addView(minus);
            block.addView(weight);
            block.addView(plus);

            weightRow.addView(block);
        }

        container.addView(weightRow);

        // ===== ПОВТОРЫ =====
        LinearLayout repsRow = new LinearLayout(this);
        repsRow.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < 3; i++) {

            LinearLayout block = new LinearLayout(this);
            block.setOrientation(LinearLayout.HORIZONTAL);
            block.setPadding(10, 10, 10, 10);
            block.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            Button minus = new Button(this);
            minus.setText("-");
            minus.setTextSize(16);
            minus.setLayoutParams(new LinearLayout.LayoutParams(
                    100, 100   // ширина, высота
            ));

            TextView reps = new TextView(this);
            reps.setText(String.valueOf(ex.reps[i]));
            reps.setTextSize(18);
            reps.setPadding(10, 0, 10, 0);

            Button plus = new Button(this);
            plus.setText("+");
            plus.setTextSize(16);
            plus.setLayoutParams(new LinearLayout.LayoutParams(
                    100, 100
            ));

            int index = i;

            minus.setOnClickListener(v -> {
                if (ex.reps[index] > 0) {
                    ex.reps[index] -= 1;
                    reps.setText(String.valueOf(ex.reps[index]));
                }
            });

            plus.setOnClickListener(v -> {
                ex.reps[index] += 1;
                reps.setText(String.valueOf(ex.reps[index]));
            });

            block.addView(minus);
            block.addView(reps);
            block.addView(plus);

            repsRow.addView(block);
        }

        container.addView(repsRow);
    }
    // ===== Сохранение =====
    private void saveState() {
        SharedPreferences prefs = getSharedPreferences("gym", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (int i = 0; i < exercises.size(); i++) {
            for (int j = 0; j < 3; j++) {
                editor.putInt("ex_" + i + "_w_" + j, exercises.get(i).weights[j]);
                editor.putInt("ex_" + i + "_r_" + j, exercises.get(i).reps[j]);
            }
        }

        editor.apply();
    }
    private void saveDay() {
        try {
            SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
            String json = prefs.getString("days", "[]");

            JSONArray days = new JSONArray(json);

            JSONObject day = new JSONObject();
            day.put("date", System.currentTimeMillis());

            JSONArray exArray = new JSONArray();

            for (Exercise ex : exercises) {
                JSONObject exObj = new JSONObject();
                exObj.put("name", ex.name);

                JSONArray sets = new JSONArray();

                for (int i = 0; i < 3; i++) {
                    JSONObject set = new JSONObject();
                    set.put("w", ex.weights[i]);
                    set.put("r", ex.reps[i]);
                    sets.put(set);
                }

                exObj.put("sets", sets);
                exArray.put(exObj);
            }

            day.put("exercises", exArray);
            days.put(day);

            prefs.edit().putString("days", days.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ===== Загрузка =====
    private void loadState() {
        SharedPreferences prefs = getSharedPreferences("gym", MODE_PRIVATE);

        for (int i = 0; i < exercises.size(); i++) {
            for (int j = 0; j < 3; j++) {
                exercises.get(i).weights[j] =
                        prefs.getInt("ex_" + i + "_w_" + j, 0);

                exercises.get(i).reps[j] =
                        prefs.getInt("ex_" + i + "_r_" + j, 0);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
}