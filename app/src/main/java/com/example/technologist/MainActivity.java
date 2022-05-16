package com.example.technologist;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Переменные
    ProgressDialog progressDialog;
    TextView username_logged, answer, title_message, body_message;
    TextInputEditText material, periodicity, power;
    Button logout, to_send;
    String url_logout, url_parameters, username, str_material, str_periodicity, str_power;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Toolbar toolbar;
    // Переменные

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(receiver,new IntentFilter("push_message"));

        // Присвоение значений переменным
        setSupportActionBar(toolbar);
        url_logout = "https://eddfit.ru/soldering/logout.php";
        url_parameters = "https://eddfit.ru/soldering/parameters.php";
        username_logged = findViewById(R.id.username_logged);
        answer = findViewById(R.id.answer);
        title_message = findViewById(R.id.title_message);
        body_message = findViewById(R.id.body_message);
        logout = findViewById(R.id.logout);
        material = findViewById(R.id.material);
        periodicity = findViewById(R.id.periodicity);
        power = findViewById(R.id.power);
        to_send = findViewById(R.id.to_send);
        preferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        username = preferences.getString("USERNAME", "");
        username_logged.setText(username);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загрузка...");
        // Присвоение значений переменным

        // Клик на кнопку "Выход"
        logout.setOnClickListener(view -> {
            progressDialog.show();
            session();
        });
        // Клик на кнопку "Выход"

        // Клик на кнопку "Отправить"
        to_send.setOnClickListener(view -> {
            progressDialog.show();
            str_material = Objects.requireNonNull(material.getText()).toString().trim();
            str_periodicity = Objects.requireNonNull(periodicity.getText()).toString().trim();
            str_power = Objects.requireNonNull(power.getText()).toString().trim();
            StringRequest parameters = new StringRequest(Request.Method.POST, url_parameters, response -> {
                if (response.equalsIgnoreCase("Успешно")) {
                    progressDialog.dismiss();
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show(); } },
                    error -> Toast.makeText(this, "Ошибка соединения", Toast.LENGTH_SHORT).show()){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("material", str_material);
                    params.put("periodicity", str_periodicity);
                    params.put("power", str_power);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(parameters);
            answer.setText("Ожидаем ответ оператора");
            answer.setTextColor(Color.parseColor("#FF0000"));
        });
    }
    // Клик на кнопку "Отправить"

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // intent will holding data show the data here
            String answer_message = intent.getStringExtra("answer_message");
            String title = intent.getStringExtra("title");
            String body = intent.getStringExtra("body");
            answer.setText(answer_message);
            answer.setTextColor(Color.parseColor("#008000"));
            title_message.setText(title);
            body_message.setText(body);
        }
    };

    // Метод сессии пользователя
    private void session() {
        StringRequest logout = new StringRequest(Request.Method.POST, url_logout, response -> {
            if (response.equalsIgnoreCase("Успешно")) {
                progressDialog.dismiss();
                editor = preferences.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish(); } },
                error -> Toast.makeText(this, "Ошибка соединения", Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(logout);
    }
    // Метод сессии пользователя
}