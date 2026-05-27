package com.lichsuvietnam.app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.FlashcardEntity;
import com.lichsuvietnam.app.data.database.entities.FlashcardProgressEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Màn hình ôn tập flashcard trong chế độ học tập tương tác.
 * Người dùng bấm thẻ để lật câu hỏi/đáp án, sau đó tự đánh giá đã nhớ
 * hoặc chưa nhớ để hệ thống lưu tiến độ ôn tập.
 */
public class FlashcardActivity extends AppCompatActivity {
    // Room database dùng để lấy flashcard và lưu tiến độ ôn.
    private AppDatabase db;
    // Quản lý phiên đăng nhập để lưu tiến độ theo từng người dùng.
    private SessionManager session;
    // Bộ thẻ đang ôn trong phiên hiện tại.
    private List<FlashcardEntity> deck;
    // Danh sách gốc dùng để khởi tạo phiên ôn tập.
    private List<FlashcardEntity> allOriginalCards;
    private int currentIndex = 0;
    private boolean flipped = false;
    private int rememberedCount = 0;
    private int notRememberedCount = 0;
    private int totalCardsInSession = 0;

    private TextView tvContent, tvLabel, tvHint, tvCounter;
    private ProgressBar progressBar;
    private LinearLayout cardView, buttonsContainer, summaryLayout;

    /**
     * Khởi tạo màn hình flashcard, tải bộ thẻ cần ôn và gắn sự kiện lật/đánh giá thẻ.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvCounter = findViewById(R.id.tvCounter);
        progressBar = findViewById(R.id.progressBar);
        cardView = findViewById(R.id.cardView);
        tvContent = findViewById(R.id.tvContent);
        tvLabel = findViewById(R.id.tvLabel);
        tvHint = findViewById(R.id.tvHint);
        buttonsContainer = findViewById(R.id.buttonsContainer);
        summaryLayout = findViewById(R.id.summaryLayout);

        cardView.setOnClickListener(v -> {
            flipped = !flipped;
            showCard();
        });

        findViewById(R.id.btnNotRemembered).setOnClickListener(v -> {
            if (!flipped) return; // Chỉ cho đánh giá khi đã lật sang mặt đáp án.
            handleNotRemembered();
        });
        findViewById(R.id.btnRemembered).setOnClickListener(v -> {
            if (!flipped) return; // Chỉ cho đánh giá khi đã lật sang mặt đáp án.
            handleRemembered();
        });

        findViewById(R.id.btnFinish).setOnClickListener(v -> finish());
        findViewById(R.id.btnReviewAgain).setOnClickListener(v -> startReviewAgain());

        Executors.newSingleThreadExecutor().execute(() -> {
            long userId = session.getUserId();
            boolean isLoggedIn = session.isLoggedIn() && userId > 0;

            List<FlashcardEntity> loaded = null;
            if (isLoggedIn) {
                loaded = db.flashcardDao().getUnmasteredCards(userId, 15);
            }
            if (loaded == null || loaded.isEmpty()) {
                loaded = db.flashcardDao().getAllFlashcardsSync();
            }

            // Tạo bản sao có thể chỉnh sửa để xóa/thêm thẻ trong phiên ôn.
            deck = new ArrayList<>(loaded);
            allOriginalCards = new ArrayList<>(loaded);
            totalCardsInSession = deck.size();

            runOnUiThread(() -> {
                if (!deck.isEmpty()) {
                    showCard();
                } else {
                    Toast.makeText(this, "B\u1ea1n \u0111\u00e3 ho\u00e0n th\u00e0nh t\u1ea5t c\u1ea3 flashcards!", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });
    }

    /**
     * Hiển thị thẻ hiện tại.
     * Nếu flipped = false thì hiển thị câu hỏi, nếu true thì hiển thị đáp án
     * và các nút tự đánh giá.
     */
    private void showCard() {
        if (currentIndex >= deck.size()) {
            showSummary();
            return;
        }

        FlashcardEntity card = deck.get(currentIndex);
        tvCounter.setText((currentIndex + 1) + "/" + deck.size());
        progressBar.setMax(deck.size());
        progressBar.setProgress(currentIndex + 1);

        if (!flipped) {
            // Mặt trước: câu hỏi.
            tvLabel.setText("C\u00C2U H\u1ECEI");
            tvLabel.setTextColor(getResources().getColor(R.color.text_tertiary, null));
            tvContent.setText(card.question);
            tvContent.setTextColor(getResources().getColor(R.color.text_primary, null));
            tvHint.setVisibility(View.VISIBLE);
            cardView.setBackgroundResource(R.drawable.bg_rounded_card);
            // Ẩn nút đánh giá khi chưa xem đáp án.
            buttonsContainer.setVisibility(View.INVISIBLE);
        } else {
            // Mặt sau: đáp án.
            tvLabel.setText("\u0110\u00C1P \u00C1N");
            tvLabel.setTextColor(getResources().getColor(R.color.red_primary, null));
            tvContent.setText(card.answer);
            tvContent.setTextColor(getResources().getColor(R.color.red_dark, null));
            tvHint.setVisibility(View.GONE);
            cardView.setBackgroundResource(R.drawable.bg_flashcard_answer);
            // Hiện nút đánh giá khi đã xem đáp án.
            buttonsContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Xử lý khi người dùng chọn "chưa nhớ".
     * Thẻ được đưa xuống cuối bộ để người dùng gặp lại trong cùng phiên ôn.
     */
    private void handleNotRemembered() {
        notRememberedCount++;

        // Lưu tiến độ nếu người dùng đã đăng nhập.
        saveProgressForCurrentCard(false);

        // Đưa thẻ xuống cuối bộ để ôn lại.
        FlashcardEntity card = deck.get(currentIndex);
        deck.remove(currentIndex);
        deck.add(card);

        // Không tăng currentIndex vì thẻ hiện tại đã bị xóa khỏi vị trí này.
        // Thẻ kế tiếp sẽ nằm ngay tại cùng index.
        flipped = false;

        if (deck.isEmpty()) {
            showSummary();
        } else {
            // Đảm bảo index vẫn hợp lệ sau khi xóa và thêm lại thẻ.
            if (currentIndex >= deck.size()) {
                currentIndex = 0;
            }
            showCard();
        }
    }

    /**
     * Xử lý khi người dùng chọn "đã nhớ".
     * Thẻ được loại khỏi bộ ôn hiện tại và lưu trạng thái remembered vào Room.
     */
    private void handleRemembered() {
        rememberedCount++;

        // Lưu tiến độ nếu người dùng đã đăng nhập.
        saveProgressForCurrentCard(true);

        // Xóa thẻ khỏi bộ ôn vì người dùng đã nhớ.
        deck.remove(currentIndex);

        flipped = false;

        if (deck.isEmpty()) {
            showSummary();
        } else {
            if (currentIndex >= deck.size()) {
                currentIndex = 0;
            }
            showCard();
        }
    }

    /**
     * Lưu tiến độ ôn tập cho thẻ hiện tại.
     * FlashcardDao dùng Room để insert hoặc update bản ghi flashcard_progress.
     */
    private void saveProgressForCurrentCard(boolean remembered) {
        long userId = session.getUserId();
        boolean isLoggedIn = session.isLoggedIn() && userId > 0;

        if (!isLoggedIn) return;

        // Lấy thẻ hiện tại trước khi thẻ có thể bị xóa khỏi deck.
        FlashcardEntity card;
        if (currentIndex < deck.size()) {
            card = deck.get(currentIndex);
        } else {
            return;
        }

        long cardId = card.id;
        Executors.newSingleThreadExecutor().execute(() -> {
            FlashcardProgressEntity progress = db.flashcardDao().getProgress(userId, cardId);
            if (progress == null) {
                progress = new FlashcardProgressEntity(userId, cardId);
                progress.remembered = remembered;
                progress.reviewCount = 1;
                db.flashcardDao().insertProgress(progress);
            } else {
                db.flashcardDao().updateProgress(userId, cardId, remembered, System.currentTimeMillis());
            }
        });
    }

    /**
     * Hiển thị tổng kết phiên ôn tập gồm tổng số thẻ, số thẻ nhớ và chưa nhớ.
     */
    private void showSummary() {
        // Ẩn khu vực thẻ.
        cardView.setVisibility(View.GONE);
        buttonsContainer.setVisibility(View.GONE);
        tvHint.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvCounter.setVisibility(View.GONE);

        // Hiển thị khu vực tổng kết.
        summaryLayout.setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.tvSummaryTotal)).setText(String.valueOf(totalCardsInSession));
        ((TextView) findViewById(R.id.tvSummaryRemembered)).setText(String.valueOf(rememberedCount));
        ((TextView) findViewById(R.id.tvSummaryNotRemembered)).setText(String.valueOf(notRememberedCount));

        // Hiện nút ôn lại nếu vẫn còn thẻ chưa nhớ trong deck.
        Button btnReviewAgain = findViewById(R.id.btnReviewAgain);
        if (!deck.isEmpty()) {
            btnReviewAgain.setVisibility(View.VISIBLE);
            btnReviewAgain.setText("\u00D4n l\u1ea1i " + deck.size() + " th\u1ebb ch\u01b0a nh\u1edb");
        } else {
            btnReviewAgain.setVisibility(View.GONE);
        }
    }

    /**
     * Bắt đầu vòng ôn lại với các thẻ còn lại trong deck.
     */
    private void startReviewAgain() {
        // Reset bộ đếm cho vòng ôn mới.
        rememberedCount = 0;
        notRememberedCount = 0;
        totalCardsInSession = deck.size();
        currentIndex = 0;
        flipped = false;

        // Ẩn tổng kết và hiển thị lại khu vực thẻ.
        summaryLayout.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        tvCounter.setVisibility(View.VISIBLE);

        showCard();
    }
}
