package com.lichsuvietnam.app.ui.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.PostEntity;
import com.lichsuvietnam.app.utils.SessionManager;

/**
 * Màn hình tạo bài viết cộng đồng.
 * Người dùng chọn chủ đề, nhập nội dung và lưu bài viết mới vào Room database.
 */
public class NewPostActivity extends AppCompatActivity {
    private String selectedTopic = "Thảo luận";
    private EditText edtContent;

    /**
     * Khởi tạo thông tin tác giả, chip chủ đề và hành động đăng bài.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        SessionManager session = new SessionManager(this);
        AppDatabase db = AppDatabase.getInstance(this);

        edtContent = findViewById(R.id.edtContent);

        // Hiển thị tên và chữ cái đại diện của tác giả.
        String name = session.isLoggedIn() ? session.getUserName() : "Khách";
        ((TextView) findViewById(R.id.txtPostAuthor)).setText(name);
        ((TextView) findViewById(R.id.txtPostInitial)).setText(name.isEmpty() ? "?" : String.valueOf(name.charAt(0)));

        // Tạo chip chủ đề để gắn nhãn cho bài viết cộng đồng.
        LinearLayout topicChips = findViewById(R.id.topicChips);
        String[] topics = {"Thảo luận", "Hỏi đáp", "Đánh giá", "Chia sẻ kiến thức"};
        for (String topic : topics) {
            TextView chip = new TextView(this);
            chip.setText(topic);
            chip.setPadding(36, 14, 36, 14);
            chip.setTextSize(13);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(8);
            chip.setLayoutParams(lp);

            updateChipStyle(chip, topic.equals(selectedTopic));

            chip.setOnClickListener(v -> {
                selectedTopic = topic;
                for (int i = 0; i < topicChips.getChildCount(); i++) {
                    TextView c = (TextView) topicChips.getChildAt(i);
                    updateChipStyle(c, c.getText().toString().equals(selectedTopic));
                }
            });
            topicChips.addView(chip);
        }

        // Đóng màn hình tạo bài.
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());

        // Hai nút đăng bài dùng chung một listener để tránh lặp logic.
        android.view.View.OnClickListener postAction = v -> {
            String content = edtContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!session.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để đăng bài", Toast.LENGTH_SHORT).show();
                return;
            }

            PostEntity post = new PostEntity(session.getUserId(), name, content, selectedTopic);
            new Thread(() -> {
                db.communityDao().insertPost(post);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }).start();
        };

        findViewById(R.id.btnPost).setOnClickListener(postAction);
        findViewById(R.id.btnPostBottom).setOnClickListener(postAction);
    }

    /**
     * Cập nhật màu nền và màu chữ cho chip chủ đề theo trạng thái được chọn.
     */
    private void updateChipStyle(TextView chip, boolean selected) {
        if (selected) {
            chip.setBackgroundResource(R.drawable.bg_chip_selected);
            chip.setTextColor(0xFFFFFFFF);
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip);
            chip.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }
    }
}
