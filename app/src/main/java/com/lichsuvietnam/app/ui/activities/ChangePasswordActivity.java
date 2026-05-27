package com.lichsuvietnam.app.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.UserEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.concurrent.Executors;

public class ChangePasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        SessionManager session = new SessionManager(this);
        AppDatabase db = AppDatabase.getInstance(this);

        if (!session.isLoggedIn()) { finish(); return; }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        EditText etOld = findViewById(R.id.etOldPassword);
        EditText etNew = findViewById(R.id.etNewPassword);
        EditText etConfirm = findViewById(R.id.etConfirmPassword);

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String oldPw = etOld.getText().toString();
            String newPw = etNew.getText().toString();
            String confirm = etConfirm.getText().toString();

            if (TextUtils.isEmpty(oldPw)) { etOld.setError("Nhập mật khẩu cũ"); return; }
            if (newPw.length() < 8) { etNew.setError("Tối thiểu 8 ký tự"); return; }
            if (!newPw.equals(confirm)) { etConfirm.setError("Mật khẩu không khớp"); return; }

            Executors.newSingleThreadExecutor().execute(() -> {
                UserEntity user = db.userDao().getUserById(session.getUserId());
                if (user == null) { runOnUiThread(() -> Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show()); return; }
                String oldHash = SessionManager.hashPassword(oldPw);
                if (!oldHash.equals(user.passwordHash)) {
                    runOnUiThread(() -> { etOld.setError("Mật khẩu cũ không đúng"); Toast.makeText(this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show(); });
                    return;
                }
                String newHash = SessionManager.hashPassword(newPw);
                db.userDao().updatePassword(user.id, newHash);
                runOnUiThread(() -> { Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show(); finish(); });
            });
        });
    }
}
