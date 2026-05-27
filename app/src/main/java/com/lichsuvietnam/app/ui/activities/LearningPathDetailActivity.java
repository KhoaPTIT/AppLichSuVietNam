package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.LearningPathEntity;
import com.lichsuvietnam.app.data.database.entities.LessonEntity;
import com.lichsuvietnam.app.data.database.entities.LessonProgressEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Màn hình chi tiết lộ trình học tương tác.
 * Activity hiển thị danh sách bài học trong một lộ trình, tiến độ hoàn thành
 * và mở QuizActivity cho từng bài khi người dùng chọn.
 */
public class LearningPathDetailActivity extends AppCompatActivity {
    // Room database dùng để lấy lộ trình, bài học và tiến độ học.
    private AppDatabase db;
    // Quản lý phiên đăng nhập để đọc tiến độ riêng của người dùng.
    private SessionManager session;
    // ID lộ trình nhận từ Intent.
    private long pathId;
    private LessonAdapter lessonAdapter;

    /**
     * Khởi tạo màn hình chi tiết lộ trình và tải danh sách bài học.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_path_detail);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);
        pathId = getIntent().getLongExtra("path_id", 1);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvLessons = findViewById(R.id.rvLessons);
        rvLessons.setLayoutManager(new LinearLayoutManager(this));

        loadData(rvLessons);
    }

    /**
     * Tải lại tiến độ khi quay về từ QuizActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView rvLessons = findViewById(R.id.rvLessons);
        if (rvLessons != null) {
            loadData(rvLessons);
        }
    }

    /**
     * Tải thông tin lộ trình, danh sách bài học và trạng thái hoàn thành.
     * Room được gọi trên background thread, sau đó cập nhật RecyclerView trên UI thread.
     */
    private void loadData(RecyclerView rvLessons) {
        long userId = session.getUserId();
        boolean isLoggedIn = session.isLoggedIn() && userId > 0;

        Executors.newSingleThreadExecutor().execute(() -> {
            LearningPathEntity path = db.learningDao().getPathById(pathId);
            List<LessonEntity> lessons = db.learningDao().getLessonsByPathSync(pathId);

            // Tải trạng thái hoàn thành và điểm của từng bài học.
            boolean[] completedArr = new boolean[lessons.size()];
            int[] scoreArr = new int[lessons.size()];
            int completedCount = 0;
            for (int i = 0; i < lessons.size(); i++) {
                if (isLoggedIn) {
                    LessonProgressEntity progress = db.learningDao().getLessonProgress(userId, lessons.get(i).id);
                    if (progress != null && progress.completed) {
                        completedArr[i] = true;
                        scoreArr[i] = progress.score;
                        completedCount++;
                    }
                }
            }

            final int fCompletedCount = completedCount;
            final String pathTitle = path != null ? path.title : "L\u1ed9 tr\u00ecnh h\u1ecdc";
            final String pathIcon = path != null ? path.icon : "\uD83D\uDCDA";

            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.tvPathTitle)).setText(pathTitle);
                ((TextView) findViewById(R.id.tvPathIcon)).setText(pathIcon);

                int total = lessons.size();
                int percent = total > 0 ? (fCompletedCount * 100 / total) : 0;
                ((TextView) findViewById(R.id.tvPathProgress)).setText(
                        fCompletedCount + "/" + total + " b\u00e0i ho\u00e0n th\u00e0nh \u2022 " + percent + "%");

                ProgressBar progressBar = findViewById(R.id.progressBarPath);
                progressBar.setMax(100);
                progressBar.setProgress(percent);

                lessonAdapter = new LessonAdapter(lessons, completedArr, scoreArr, lesson -> {
                    Intent intent = new Intent(this, QuizActivity.class);
                    intent.putExtra("path_id", pathId);
                    intent.putExtra("lesson_id", lesson.id);
                    intent.putExtra("lesson_index", lesson.orderIndex);
                    startActivity(intent);
                });
                rvLessons.setAdapter(lessonAdapter);
            });
        });
    }

    /**
     * Adapter nội bộ hiển thị danh sách bài học thuộc một lộ trình.
     */
    private static class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.VH> {
        private final List<LessonEntity> lessons;
        private final boolean[] completed;
        private final int[] scores;
        private final OnLessonClick listener;

        /** Callback khi người dùng chọn một bài học để làm quiz. */
        interface OnLessonClick { void onClick(LessonEntity lesson); }

        /** Khởi tạo adapter với danh sách bài học, trạng thái hoàn thành và điểm. */
        LessonAdapter(List<LessonEntity> lessons, boolean[] completed, int[] scores, OnLessonClick listener) {
            this.lessons = lessons;
            this.completed = completed;
            this.scores = scores;
            this.listener = listener;
        }

        /** Inflate layout item_lesson.xml cho từng bài học. */
        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
            return new VH(v);
        }

        /** Gắn thông tin bài học và trạng thái hoàn thành vào item. */
        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            LessonEntity lesson = lessons.get(position);
            h.tvLessonNumber.setText(String.valueOf(lesson.orderIndex));
            h.tvLessonTitle.setText(lesson.title);
            h.tvLessonDesc.setText(lesson.description != null ? lesson.description : "");

            if (completed[position]) {
                h.tvStatus.setText("\u2705 Ho\u00e0n th\u00e0nh");
                h.tvStatus.setTextColor(h.itemView.getContext().getResources().getColor(R.color.success));
                h.tvLessonNumber.setBackgroundResource(R.drawable.bg_circle_red_filled);
                h.tvLessonNumber.setTextColor(0xFFFFFFFF);
            } else {
                h.tvStatus.setText("\u25CB Ch\u01b0a h\u1ecdc");
                h.tvStatus.setTextColor(h.itemView.getContext().getResources().getColor(R.color.text_tertiary));
                h.tvLessonNumber.setBackgroundResource(R.drawable.bg_circle_red);
                h.tvLessonNumber.setTextColor(h.itemView.getContext().getResources().getColor(R.color.red_primary));
            }

            h.itemView.setOnClickListener(v -> listener.onClick(lesson));
        }

        /** Trả về số bài học trong lộ trình. */
        @Override public int getItemCount() { return lessons.size(); }

        /** ViewHolder lưu các view con của một item bài học. */
        static class VH extends RecyclerView.ViewHolder {
            TextView tvLessonNumber, tvLessonTitle, tvLessonDesc, tvStatus;
            VH(@NonNull View v) {
                super(v);
                tvLessonNumber = v.findViewById(R.id.tvLessonNumber);
                tvLessonTitle = v.findViewById(R.id.tvLessonTitle);
                tvLessonDesc = v.findViewById(R.id.tvLessonDesc);
                tvStatus = v.findViewById(R.id.tvStatus);
            }
        }
    }
}
