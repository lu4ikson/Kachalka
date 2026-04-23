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

    String[] exerciseNamesA = {
            "Румынская тяга",
            "Жим гантелей сидя",
            "Тяга горизонтального блока",
            "Жим гантелей под углом",
            "Сгибание ног в тренажёре",
            "Подъём гантелей на бицепс"
    };

    String[] exerciseNamesB = {
            "Приседания",
            "Жим штанги лёжа",
            "Тяга верхнего блока",
            "Выпады с гантелями",
            "Махи в стороны",
            "Разгибание на трицепс в блоке"
    };

    boolean isTrainingA = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        isTrainingA = getSharedPreferences("gym", MODE_PRIVATE)
                .getBoolean("isA", true);
        // создаём упражнения
        createExercises();
        refreshUI();

        Button btnSwitch = findViewById(R.id.btnSwitch);

        btnSwitch.setOnClickListener(v -> {
            saveState(); // сохраняем текущую тренировку

            isTrainingA = !isTrainingA;

            createExercises();
            refreshUI();
        });


        // 👇
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
    private void createExercises() {
        exercises.clear();

        String[] names = isTrainingA ? exerciseNamesA : exerciseNamesB;

        for (String name : names) {
            exercises.add(new Exercise(name));
        }
    }
    // ===== Сохранение =====
    private void saveState() {
        SharedPreferences prefs = getSharedPreferences("gym", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String prefix = isTrainingA ? "A_" : "B_";

        for (int i = 0; i < exercises.size(); i++) {
            for (int j = 0; j < 3; j++) {
                editor.putInt(prefix + "ex_" + i + "_w_" + j, exercises.get(i).weights[j]);
                editor.putInt(prefix + "ex_" + i + "_r_" + j, exercises.get(i).reps[j]);
            }
        }

        editor.apply();
    }
    // ===== Загрузка =====
    private void loadState() {
        SharedPreferences prefs = getSharedPreferences("gym", MODE_PRIVATE);

        String prefix = isTrainingA ? "A_" : "B_";

        for (int i = 0; i < exercises.size(); i++) {
            for (int j = 0; j < 3; j++) {
                exercises.get(i).weights[j] =
                        prefs.getInt(prefix + "ex_" + i + "_w_" + j, 0);

                exercises.get(i).reps[j] =
                        prefs.getInt(prefix + "ex_" + i + "_r_" + j, 0);
            }
        }
    }
    private void refreshUI() {
        container.removeAllViews();

        loadState();

        for (Exercise ex : exercises) {
            addExerciseView(ex);
        }
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


    @Override
    protected void onPause() {
        super.onPause();
        saveState();
        getSharedPreferences("gym", MODE_PRIVATE)
                .edit()
                .putBoolean("isA", isTrainingA)
                .apply();
    }
}