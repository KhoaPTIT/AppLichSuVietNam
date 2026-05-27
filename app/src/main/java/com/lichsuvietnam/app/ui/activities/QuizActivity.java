package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.LearningProgressEntity;
import com.lichsuvietnam.app.data.database.entities.LessonProgressEntity;
import com.lichsuvietnam.app.data.database.entities.QuizQuestionEntity;
import com.lichsuvietnam.app.data.database.entities.QuizResultEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Màn hình Câu hỏi & bài kiểm tra.
 * Activity tải bộ câu hỏi theo ngữ cảnh (bài học, lộ trình, nhân vật, sự kiện
 * hoặc bài kiểm tra tổng hợp), xử lý chọn đáp án, bỏ qua, tính điểm và lưu kết quả.
 */
public class QuizActivity extends AppCompatActivity {
    // Room database dùng để lấy câu hỏi, lưu kết quả và cập nhật tiến độ học.
    private AppDatabase db;
    // Quản lý phiên đăng nhập để lưu điểm/tiến độ theo người dùng.
    private SessionManager session;
    // Danh sách câu hỏi của phiên quiz hiện tại.
    private List<QuizQuestionEntity> questions;
    private int currentIndex = 0;
    private int correctCount = 0;
    private int wrongCount = 0;
    private int skippedCount = 0;
    private boolean answered = false;
    private boolean processingClick = false;
    private long startTime;
    private long pathId = 0;
    private long lessonId = 0;

    // Handler trên main thread dùng để tự chuyển câu đúng sau một khoảng ngắn.
    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextView tvQuestion, tvCounter, tvFeedback;
    private ProgressBar progressBar;
    private LinearLayout optionsContainer, feedbackLayout;
    private Button btnDontKnow, btnContinue;

    /**
     * Khởi tạo quiz, nhận tham số từ Intent và tải bộ câu hỏi phù hợp.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);
        startTime = SystemClock.elapsedRealtime();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCounter = findViewById(R.id.tvCounter);
        progressBar = findViewById(R.id.progressBar);
        optionsContainer = findViewById(R.id.optionsContainer);
        feedbackLayout = findViewById(R.id.feedbackLayout);
        tvFeedback = findViewById(R.id.tvFeedback);

        btnDontKnow = findViewById(R.id.btnDontKnow);
        btnContinue = findViewById(R.id.btnContinue);

        btnDontKnow.setOnClickListener(v -> handleDontKnow());
        btnContinue.setOnClickListener(v -> goNextOrFinish());

        Executors.newSingleThreadExecutor().execute(() -> {
            pathId = getIntent().getLongExtra("path_id", 0);
            long eventId = getIntent().getLongExtra("event_id", 0);
            long figureId = getIntent().getLongExtra("figure_id", 0);
            boolean isTest = getIntent().getBooleanExtra("is_test", false);
            lessonId = getIntent().getLongExtra("lesson_id", 0);

            if (isTest) {
                questions = db.quizDao().getAllQuestionsRandom(30);
            } else if (figureId > 0) {
                questions = db.quizDao().getQuestionsByFigure(figureId);
                if (questions.isEmpty()) questions = db.quizDao().getRandomQuestions(5);
            } else if (lessonId > 0) {
                // Quiz theo bài học cụ thể, không cắt câu hỏi theo lộ trình nữa.
                questions = db.quizDao().getQuestionsByLesson(lessonId);
                if (questions.isEmpty()) {
                    // Nếu bài học chưa có câu hỏi thì dùng câu hỏi cấp lộ trình.
                    if (pathId > 0) {
                        questions = db.quizDao().getQuestionsByPath(pathId);
                        if (questions.size() > 5) questions = questions.subList(0, 5);
                    }
                    if (questions == null || questions.isEmpty()) {
                        questions = db.quizDao().getRandomQuestions(3);
                    }
                }
            } else if (pathId > 0) {
                questions = db.quizDao().getQuestionsByPath(pathId);
                if (questions.isEmpty()) questions = db.quizDao().getRandomQuestions(5);
            } else if (eventId > 0) {
                questions = db.quizDao().getQuestionsByEvent(eventId);
                if (questions.isEmpty()) questions = db.quizDao().getRandomQuestions(5);
            } else {
                questions = db.quizDao().getRandomQuestions(5);
            }

            runOnUiThread(() -> { if (!questions.isEmpty()) showQuestion(); });
        });
    }

    /**
     * Hiển thị câu hỏi hiện tại, tạo 4 lựa chọn A/B/C/D và cập nhật thanh tiến độ.
     */
    private void showQuestion() {
        answered = false;
        processingClick = false;
        feedbackLayout.setVisibility(View.GONE);
        btnDontKnow.setVisibility(View.VISIBLE);
        btnDontKnow.setEnabled(true);
        btnContinue.setVisibility(View.GONE);

        QuizQuestionEntity q = questions.get(currentIndex);
        tvQuestion.setText(q.question);
        tvCounter.setText((currentIndex + 1) + "/" + questions.size());
        progressBar.setMax(questions.size());
        progressBar.setProgress(currentIndex + 1);

        optionsContainer.removeAllViews();
        String[] opts = {q.optionA, q.optionB, q.optionC, q.optionD};
        for (int i = 0; i < opts.length; i++) {
            final int idx = i;
            View optView = getLayoutInflater().inflate(R.layout.item_quiz_option, optionsContainer, false);
            TextView tvLetter = optView.findViewById(R.id.tvLetter);
            TextView tvOption = optView.findViewById(R.id.tvOption);
            tvLetter.setText(String.valueOf((char) ('A' + i)));
            tvOption.setText(opts[i]);
            optView.setOnClickListener(v -> handleAnswer(idx));
            optionsContainer.addView(optView);
        }
    }

    /**
     * Xử lý khi người dùng chọn một đáp án.
     * Hàm đánh dấu đúng/sai bằng màu nền, khóa các lựa chọn và cập nhật thống kê.
     */
    private void handleAnswer(int selectedIdx) {
        if (answered || processingClick) return;
        processingClick = true;
        answered = true;

        QuizQuestionEntity q = questions.get(currentIndex);
        boolean isCorrect = selectedIdx == q.correctIndex;
        String[] opts = {q.optionA, q.optionB, q.optionC, q.optionD};

        View selectedView = optionsContainer.getChildAt(selectedIdx);
        if (selectedView != null) {
            selectedView.setBackgroundResource(isCorrect ? R.drawable.bg_quiz_correct : R.drawable.bg_quiz_wrong);
        }
        if (!isCorrect) {
            View correctView = optionsContainer.getChildAt(q.correctIndex);
            if (correctView != null) correctView.setBackgroundResource(R.drawable.bg_quiz_correct);
        }

        disableAllOptions();
        btnDontKnow.setVisibility(View.GONE);

        feedbackLayout.setVisibility(View.VISIBLE);
        if (isCorrect) {
            correctCount++;
            feedbackLayout.setBackgroundResource(R.drawable.bg_quiz_correct);
            tvFeedback.setText("\u2705 Ch\u00ednh x\u00e1c!");
            tvFeedback.setTextColor(getResources().getColor(R.color.success, null));
            handler.postDelayed(() -> {
                if (!isFinishing()) goNextOrFinish();
            }, 600);
        } else {
            wrongCount++;
            feedbackLayout.setBackgroundResource(R.drawable.bg_quiz_wrong);
            tvFeedback.setText("\u274C Sai. \u0110\u00e1p \u00e1n: " + opts[q.correctIndex]);
            tvFeedback.setTextColor(getResources().getColor(R.color.error, null));
            btnContinue.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Xử lý khi người dùng chọn "không biết".
     * Đáp án đúng được hiển thị ngay và câu hỏi được tính vào nhóm bỏ qua.
     */
    private void handleDontKnow() {
        if (answered || processingClick) return;
        processingClick = true;
        answered = true;
        skippedCount++;

        QuizQuestionEntity q = questions.get(currentIndex);
        String[] opts = {q.optionA, q.optionB, q.optionC, q.optionD};

        View correctView = optionsContainer.getChildAt(q.correctIndex);
        if (correctView != null) correctView.setBackgroundResource(R.drawable.bg_quiz_correct);

        disableAllOptions();
        btnDontKnow.setEnabled(false);
        btnDontKnow.setVisibility(View.GONE);

        feedbackLayout.setVisibility(View.VISIBLE);
        feedbackLayout.setBackgroundResource(R.drawable.bg_quiz_correct);
        tvFeedback.setText("\uD83D\uDCA1 \u0110\u00e1p \u00e1n: " + opts[q.correctIndex]);
        tvFeedback.setTextColor(getResources().getColor(R.color.text_primary, null));
        btnContinue.setVisibility(View.VISIBLE);
    }

    /**
     * Vô hiệu hóa toàn bộ lựa chọn để tránh bấm nhiều lần sau khi đã trả lời.
     */
    private void disableAllOptions() {
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            optionsContainer.getChildAt(i).setOnClickListener(null);
        }
    }

    /**
     * Chuyển sang câu tiếp theo hoặc lưu kết quả nếu đã hết câu hỏi.
     */
    private void goNextOrFinish() {
        handler.removeCallbacksAndMessages(null);
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion();
        } else {
            saveResult();
        }
    }

    /**
     * Lưu kết quả bài kiểm tra.
     * Nếu đã đăng nhập, kết quả được ghi vào Room, cộng điểm người dùng
     * và cập nhật tiến độ bài học/lộ trình.
     */
    private void saveResult() {
        int timeSeconds = (int) ((SystemClock.elapsedRealtime() - startTime) / 1000);
        long userId = session.getUserId();
        boolean isLoggedIn = session.isLoggedIn() && userId > 0;

        Executors.newSingleThreadExecutor().execute(() -> {
            QuizResultEntity result = new QuizResultEntity(isLoggedIn ? userId : 0, correctCount, questions.size(), timeSeconds);
            int pointsEarned = result.pointsEarned;

            if (isLoggedIn) {
                db.quizDao().insertResult(result);
                db.userDao().addPoints(userId, pointsEarned);

                if (lessonId > 0) {
                    LessonProgressEntity lessonProgress = db.learningDao().getLessonProgress(userId, lessonId);
                    if (lessonProgress == null) {
                        lessonProgress = new LessonProgressEntity(userId, lessonId);
                    }
                    lessonProgress.completed = true;
                    lessonProgress.score = correctCount;
                    lessonProgress.completedAt = System.currentTimeMillis();
                    db.learningDao().insertLessonProgress(lessonProgress);

                    if (pathId > 0) {
                        LearningProgressEntity pathProgress = db.learningDao().getProgress(userId, pathId);
                        if (pathProgress == null) {
                            pathProgress = new LearningProgressEntity(userId, pathId);
                            db.learningDao().insertProgress(pathProgress);
                        }
                        db.learningDao().syncPathProgress(userId, pathId, System.currentTimeMillis());
                    }
                }
            }

            runOnUiThread(() -> {
                Intent intent = new Intent(this, QuizResultActivity.class);
                intent.putExtra("correct", correctCount);
                intent.putExtra("wrong", wrongCount);
                intent.putExtra("skipped", skippedCount);
                intent.putExtra("total", questions.size());
                intent.putExtra("time", timeSeconds);
                intent.putExtra("points", pointsEarned);
                startActivity(intent);
                finish();
            });
        });
    }

    /**
     * Dọn callback còn pending trong Handler để tránh chạy tiếp sau khi Activity bị hủy.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
