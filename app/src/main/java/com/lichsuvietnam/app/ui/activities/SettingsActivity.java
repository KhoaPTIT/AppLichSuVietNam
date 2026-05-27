package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.utils.SessionManager;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SessionManager session = new SessionManager(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Switch switchNotif = findViewById(R.id.switchNotif);
        Switch switchDark = findViewById(R.id.switchDark);
        switchNotif.setChecked(true);

        // Change password button
        View btnChangePw = findViewById(R.id.btnChangePassword);
        if (btnChangePw != null) {
            if (session.isLoggedIn()) {
                btnChangePw.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));
            } else {
                btnChangePw.setVisibility(android.view.View.GONE);
            }
        }

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, WelcomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
    }
}
