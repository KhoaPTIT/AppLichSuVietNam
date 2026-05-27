package com.lichsuvietnam.app.ui.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.data.provider.DataProvider;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.R;

/**
 * Màn hình xem ảnh đa phương tiện dạng từng ảnh.
 * Activity dùng danh sách ảnh tĩnh từ DataProvider và cho phép chuyển ảnh
 * bằng nút trước/sau.
 */
public class MediaViewerActivity extends AppCompatActivity {
    // Vị trí ảnh hiện tại trong mảng images.
    private int currentIndex = 0;
    private String[] images;
    private ImageView ivMain;
    private TextView tvCounter, tvCaption;

    /**
     * Khởi tạo danh sách ảnh, ánh xạ view và gắn sự kiện chuyển ảnh.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_viewer);

        images = DataProvider.getMediaImages();
        ivMain = findViewById(R.id.ivMain);
        tvCounter = findViewById(R.id.tvCounter);
        tvCaption = findViewById(R.id.tvCaption);

        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
        findViewById(R.id.btnPrev).setOnClickListener(v -> {
            if (currentIndex > 0) { currentIndex--; showImage(); }
        });
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (currentIndex < images.length - 1) { currentIndex++; showImage(); }
        });

        showImage();
    }

    /**
     * Hiển thị ảnh hiện tại và cập nhật bộ đếm.
     * ImageUtils gọi Glide để tải drawable nội bộ vào ImageView.
     */
    private void showImage() {
        ImageUtils.load(this, images[currentIndex], ivMain);
        tvCounter.setText((currentIndex + 1) + " / " + images.length);
    }
}
