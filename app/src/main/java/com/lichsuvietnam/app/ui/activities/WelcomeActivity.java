package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.utils.SessionManager;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView heroImage = findViewById(R.id.heroImage);
        ImageUtils.load(this, "img_welcome_hero", heroImage);

        SessionManager session = new SessionManager(this);

        findViewById(R.id.btnLogin).setOnClickListener(v ->
            startActivity(new Intent(this, LoginActivity.class)));

        findViewById(R.id.btnRegister).setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.btnGuest).setOnClickListener(v -> {
            session.setGuest();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
