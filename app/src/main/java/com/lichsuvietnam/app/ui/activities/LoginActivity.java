package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.UserEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private SessionManager session;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);
        db = AppDatabase.getInstance(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        TextView tvForgot = findViewById(R.id.tvForgotPassword);
        TextView tvRegister = findViewById(R.id.tvRegister);

        btnBack.setOnClickListener(v -> finish());
        tvForgot.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String pw = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) { etEmail.setError("Vui lòng nhập email"); return; }
        if (TextUtils.isEmpty(pw)) { etPassword.setError("Vui lòng nhập mật khẩu"); return; }

        btnLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            String hash = SessionManager.hashPassword(pw);
            UserEntity user = db.userDao().authenticate(email, hash);

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (user != null) {
                    if (!user.isActive) {
                        btnLogin.setEnabled(true);
                        Toast.makeText(this, "Tài khoản đã bị khóa. Liên hệ admin.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    session.createSession(user.id, user.name, user.email, user.role);
                    startActivity(new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
