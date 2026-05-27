package com.lichsuvietnam.app.ui.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.concurrent.Executors;

public class AdminDashboardActivity extends AppCompatActivity {
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(this);
        if (!session.isAdmin()) { Toast.makeText(this, "Không có quyền truy cập", Toast.LENGTH_SHORT).show(); finish(); return; }

        setContentView(R.layout.activity_admin_dashboard);
        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnBackAdmin).setOnClickListener(v -> finish());
        findViewById(R.id.cardUsers).setOnClickListener(v -> startActivity(new Intent(this, AdminUsersActivity.class)));
        findViewById(R.id.cardCommunity).setOnClickListener(v -> startActivity(new Intent(this, AdminCommunityActivity.class)));
        findViewById(R.id.cardEvents).setOnClickListener(v -> startActivity(new Intent(this, AdminEventsActivity.class)));
        findViewById(R.id.cardFigures).setOnClickListener(v -> startActivity(new Intent(this, AdminFiguresActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int users = db.userDao().getUserCount();
            int posts = db.communityDao().getPostCount();
            int events = db.historyEventDao().getCount();
            int figures = db.figureDao().getCount();
            int comments = db.communityDao().getCommentCount();
            int questions = db.quizDao().getQuestionCount();
            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.tvStatUsers)).setText(String.valueOf(users));
                ((TextView) findViewById(R.id.tvStatPosts)).setText(String.valueOf(posts));
                ((TextView) findViewById(R.id.tvStatEvents)).setText(String.valueOf(events));
                ((TextView) findViewById(R.id.tvStatFigures)).setText(String.valueOf(figures));
                ((TextView) findViewById(R.id.tvStatComments)).setText(String.valueOf(comments));
                ((TextView) findViewById(R.id.tvStatQuestions)).setText(String.valueOf(questions));
            });
        });
    }
}
