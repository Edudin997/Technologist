package com.example.technologist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login extends AppCompatActivity {

    // Переменные
    ProgressDialog progressDialog;
    TextInputEditText username, password;
    Button login;
    String url_logged, url_login, str_username, str_password, str_token;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean isLogin;
    // Переменные

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Регистрируем token приложения
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast
                        System.out.println(token);
                        str_token = token;
                    }
                });
        // Регистрируем token приложения

        // Присвоение значений переменным
        url_logged = "https://eddfit.ru/soldering/logged.php";
        url_login = "https://eddfit.ru/soldering/login.php";
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("LOGGED", false);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загрузка...");
        // Присвоение значений переменным

        // Проверка сессии
        if (isLogin) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        // Проверка сессии
        // Тест
        // Клик на кнопку "Войти"
        login.setOnClickListener(v -> {
            progressDialog.show();
            str_username = Objects.requireNonNull(username.getText()).toString().trim();
            str_password = Objects.requireNonNull(password.getText()).toString().trim();
            editor = sharedPreferences.edit();
            StringRequest logged = new StringRequest(Request.Method.POST, url_logged, response -> {
                if (response.equalsIgnoreCase("Успешно")){
                    isLogin = true;
                    editor.putString("USERNAME", str_username);
                    editor.putString("TOKEN", str_token);
                    editor.putBoolean("LOGGED", isLogin);
                    editor.apply();
                    session();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                } else Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            }, error -> Toast.makeText(this, "Ошибка соединения", Toast.LENGTH_SHORT).show()){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("username", str_username);
                    params.put("password", str_password);
                    params.put("token", str_token);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(logged);
        });
        // Клик на кнопку "Войти"
    }

    // Метод сессии пользователя
    private void session() {
        StringRequest login = new StringRequest(Request.Method.POST, url_login, response -> {
                progressDialog.dismiss();
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show(); },
                error -> Toast.makeText(this, "Ошибка соединения", Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("username", str_username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(login);
    }
    // Метод сессии пользователя
}