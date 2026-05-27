package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;

/**
 * Màn hình kết quả Câu hỏi & bài kiểm tra.
 * Activity nhận thống kê từ QuizActivity qua Intent, hiển thị điểm, thời gian,
 * số câu đúng/sai/bỏ qua và điều hướng làm lại hoặc về trang chính.
 */
public class QuizResultActivity extends AppCompatActivity {
    /**
     * Khởi tạo giao diện kết quả quiz và đọc dữ liệu tổng kết từ Intent extras.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        int correct = getIntent().getIntExtra("correct", 0);
        int wrong = getIntent().getIntExtra("wrong", 0);
        int skipped = getIntent().getIntExtra("skipped", 0);
        int total = getIntent().getIntExtra("total", 0);
        int timeSeconds = getIntent().getIntExtra("time", 0);
        int points = getIntent().getIntExtra("points", 0);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvTotal = findViewById(R.id.tvTotal);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvPoints = findViewById(R.id.tvPointsEarned);
        TextView tvRank = findViewById(R.id.tvRank);

        tvScore.setText(String.valueOf(correct));
        tvTotal.setText("/" + total);
        tvTime.setText(String.format("%d:%02d", timeSeconds / 60, timeSeconds % 60));
        tvPoints.setText("+" + points + " \u0111i\u1ec3m");

        float pct = total > 0 ? (float) correct / total * 100 : 0;
        String rank = pct >= 90 ? "A+" : pct >= 80 ? "A" : pct >= 70 ? "B+" : pct >= 60 ? "B" : pct >= 50 ? "C" : "D";
        tvRank.setText(rank);

        // Thống kê số câu đúng, sai và bỏ qua.
        TextView tvCorrectCount = findViewById(R.id.tvCorrectCount);
        TextView tvWrongCount = findViewById(R.id.tvWrongCount);
        TextView tvSkippedCount = findViewById(R.id.tvSkippedCount);

        tvCorrectCount.setText(String.valueOf(correct));
        tvWrongCount.setText(String.valueOf(wrong));
        tvSkippedCount.setText(String.valueOf(skipped));

        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            finish();
        });

        findViewById(R.id.btnHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }
}
