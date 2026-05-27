package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.UserEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword, etPassword2;
    private AppDatabase db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());
        findViewById(R.id.btnRegister).setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pw = etPassword.getText().toString();
        String pw2 = etPassword2.getText().toString();

        if (TextUtils.isEmpty(name)) { etName.setError("Vui lòng nhập tên"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Vui lòng nhập email"); return; }
        if (pw.length() < 8) { etPassword.setError("Mật khẩu tối thiểu 8 ký tự"); return; }
        if (!pw.equals(pw2)) { etPassword2.setError("Mật khẩu không khớp"); return; }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (db.userDao().countByEmail(email) > 0) {
                runOnUiThread(() -> Toast.makeText(this, "Email đã được sử dụng", Toast.LENGTH_SHORT).show());
                return;
            }

            String hash = SessionManager.hashPassword(pw);
            UserEntity user = new UserEntity(name, email, hash);
            long userId = db.userDao().insert(user);

            runOnUiThread(() -> {
                session.createSession(userId, name, email);
                startActivity(new Intent(this, ProfileSetupActivity.class));
                finish();
            });
        });
    }
}
