package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.lichsuvietnam.app.data.provider.DataProvider;
import com.lichsuvietnam.app.R;

public class ProfileSetupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        Button btnStart = findViewById(R.id.btnStart);
        TextView tvSkip = findViewById(R.id.tvSkip);

        for (String topic : DataProvider.getTopics()) {
            Chip chip = new Chip(this);
            chip.setText(topic);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.input_bg);
            chip.setCheckedIconVisible(true);
            chipGroup.addView(chip);
        }

        tvSkip.setOnClickListener(v -> goToMain());
        btnStart.setOnClickListener(v -> goToMain());
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
