package com.lichsuvietnam.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.activities.FlashcardActivity;
import com.lichsuvietnam.app.ui.activities.LearningPathDetailActivity;
import com.lichsuvietnam.app.ui.activities.QuizActivity;
import com.lichsuvietnam.app.ui.adapters.LearningPathAdapter;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.LearningPathEntity;
import com.lichsuvietnam.app.data.database.entities.LearningProgressEntity;
import com.lichsuvietnam.app.data.database.entities.QuizResultEntity;
import com.lichsuvietnam.app.data.models.LearningPath;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Fragment chính của chế độ học tập tương tác.
 * Màn hình này hiển thị thống kê học tập, danh sách lộ trình học,
 * nút ôn flashcard và nút làm bài kiểm tra tổng hợp.
 */
public class LearnFragment extends Fragment {
    // View gốc được giữ lại để tải lại dữ liệu khi quay về màn hình.
    private View rootView;
    // Room database dùng để lấy lộ trình học, tiến độ và kết quả quiz.
    private AppDatabase db;
    // Quản lý phiên đăng nhập để lấy tiến độ riêng của người dùng.
    private SessionManager session;

    /**
     * Khởi tạo giao diện học tập và gắn điều hướng tới flashcard/quiz.
     */
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_learn, container, false);
        db = AppDatabase.getInstance(requireContext());
        session = new SessionManager(requireContext());

        // Mở chế độ flashcard để người dùng ôn tập tương tác.
        rootView.findViewById(R.id.cardFlashcards).setOnClickListener(v ->
            startActivity(new Intent(getContext(), FlashcardActivity.class)));

        // Nút quiz mở bài kiểm tra tổng hợp 30 câu.
        View btnQuiz = rootView.findViewById(R.id.cardQuiz);
        if (btnQuiz != null) {
            btnQuiz.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), QuizActivity.class);
                intent.putExtra("is_test", true);
                startActivity(intent);
            });
        }

        return rootView;
    }

    /**
     * Khi quay lại màn hình học, tải lại dữ liệu để cập nhật điểm và tiến độ mới nhất.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null) {
            loadData();
        }
    }

    /**
     * Tải thống kê học tập và danh sách lộ trình.
     * Các truy vấn Room dạng sync chạy trên background thread, sau đó cập nhật UI
     * bằng runOnUiThread để tránh chặn luồng giao diện.
     */
    private void loadData() {
        long userId = session.getUserId();
        boolean isLoggedIn = session.isLoggedIn() && userId > 0;
        RecyclerView rvPaths = rootView.findViewById(R.id.rvPaths);
        rvPaths.setLayoutManager(new LinearLayoutManager(getContext()));

        Executors.newSingleThreadExecutor().execute(() -> {
            int totalPoints = 0;
            int totalCompleted = 0;
            int totalLessons = 0;

            // Lấy điểm từ kết quả quiz, chỉ áp dụng khi người dùng đã đăng nhập.
            List<QuizResultEntity> results;
            if (isLoggedIn) {
                results = db.quizDao().getResultsByUserSync(userId);
                for (QuizResultEntity r : results) totalPoints += r.pointsEarned;
            } else {
                results = new java.util.ArrayList<>();
            }

            List<LearningPathEntity> pathEntities = db.learningDao().getAllPathsSync();
            List<LearningPath> paths = new ArrayList<>();
            for (LearningPathEntity e : pathEntities) {
                int completed = 0;
                if (isLoggedIn) {
                    // Ưu tiên số bài đã hoàn thành ở cấp lesson.
                    completed = db.learningDao().getCompletedLessonCountByPath(userId, e.id);
                    // Kiểm tra thêm tiến độ cấp path cũ để tương thích dữ liệu trước đó.
                    LearningProgressEntity progress = db.learningDao().getProgress(userId, e.id);
                    if (progress != null && progress.completedLessons > completed) {
                        completed = progress.completedLessons;
                    }
                }
                totalCompleted += completed;
                totalLessons += e.totalLessons;
                paths.add(new LearningPath((int) e.id, e.title, e.totalLessons, completed, e.icon));
            }

            int progressPercent = totalLessons > 0 ? (totalCompleted * 100 / totalLessons) : 0;
            final int fPoints = totalPoints;
            final int fProgress = progressPercent;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    TextView tvStreak = rootView.findViewById(R.id.tvStreakValue);
                    TextView tvPoints = rootView.findViewById(R.id.tvPointsValue);
                    TextView tvProgress = rootView.findViewById(R.id.tvProgressValue);
                    if (tvStreak != null) tvStreak.setText(results.size() > 0 ? "1 ngày" : "0 ngày");
                    if (tvPoints != null) tvPoints.setText(String.valueOf(fPoints));
                    if (tvProgress != null) tvProgress.setText(fProgress + "%");

                    rvPaths.setAdapter(new LearningPathAdapter(paths, path -> {
                        Intent intent = new Intent(getContext(), LearningPathDetailActivity.class);
                        intent.putExtra("path_id", (long) path.getId());
                        startActivity(intent);
                    }));
                });
            }
        });
    }
}
