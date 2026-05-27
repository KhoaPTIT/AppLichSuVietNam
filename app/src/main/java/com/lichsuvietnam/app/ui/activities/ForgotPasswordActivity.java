package com.lichsuvietnam.app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    private int step = 0; // 0=email, 1=otp, 2=reset, 3=done
    private LinearLayout layoutEmail, layoutOtp, layoutReset, layoutDone;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ImageButton btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutOtp = findViewById(R.id.layoutOtp);
        layoutReset = findViewById(R.id.layoutReset);
        layoutDone = findViewById(R.id.layoutDone);

        btnBack.setOnClickListener(v -> {
            if (step > 0) { step--; updateStep(); } else finish();
        });

        findViewById(R.id.btnSendOtp).setOnClickListener(v -> { step = 1; updateStep(); });
        findViewById(R.id.btnConfirmOtp).setOnClickListener(v -> { step = 2; updateStep(); });
        findViewById(R.id.btnResetPassword).setOnClickListener(v -> { step = 3; updateStep(); });
        findViewById(R.id.btnBackToLogin).setOnClickListener(v -> finish());

        updateStep();
    }

    private void updateStep() {
        layoutEmail.setVisibility(step == 0 ? View.VISIBLE : View.GONE);
        layoutOtp.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        layoutReset.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        layoutDone.setVisibility(step == 3 ? View.VISIBLE : View.GONE);

        String[] titles = {
            getString(R.string.forgot_title),
            getString(R.string.enter_otp),
            getString(R.string.reset_password),
            getString(R.string.reset_success)
        };
        tvTitle.setText(titles[step]);
    }
}
