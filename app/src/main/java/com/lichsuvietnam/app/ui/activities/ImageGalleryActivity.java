package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import java.util.List;

/**
 * Màn hình Hình ảnh lịch sử.
 * Activity lấy các sự kiện có ảnh minh họa từ Room, hiển thị dạng lưới
 * và cho phép lọc ảnh theo giai đoạn lịch sử.
 */
public class ImageGalleryActivity extends AppCompatActivity {
    // Room database dùng để lấy sự kiện và ảnh minh họa.
    private AppDatabase db;
    // Giai đoạn đang được chọn trong bộ lọc chip.
    private String selectedPeriod = "Tất cả";
    private RecyclerView rv;

    /**
     * Khởi tạo lưới ảnh, chip lọc giai đoạn và tải dữ liệu ảnh ban đầu.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rv = findViewById(R.id.rvImages);
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        // Tạo chip lọc ảnh theo giai đoạn lịch sử.
        LinearLayout chipContainer = findViewById(R.id.chipContainerGallery);
        String[] periods = {"Tất cả", "Bắc thuộc", "Phong kiến", "Cận đại", "Hiện đại"};
        for (String period : periods) {
            TextView chip = new TextView(this);
            chip.setText(period);
            chip.setPadding(36, 14, 36, 14);
            chip.setTextSize(12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(8);
            chip.setLayoutParams(lp);
            chip.setBackgroundResource(period.equals(selectedPeriod) ? R.drawable.bg_chip_selected : R.drawable.bg_chip);
            chip.setTextColor(period.equals(selectedPeriod) ? 0xFFFFFFFF : getResources().getColor(R.color.text_secondary, null));

            chip.setOnClickListener(v -> {
                selectedPeriod = period;
                // Cập nhật lại style của toàn bộ chip sau khi chọn giai đoạn mới.
                for (int i = 0; i < chipContainer.getChildCount(); i++) {
                    TextView c = (TextView) chipContainer.getChildAt(i);
                    boolean sel = c.getText().toString().equals(selectedPeriod);
                    c.setBackgroundResource(sel ? R.drawable.bg_chip_selected : R.drawable.bg_chip);
                    c.setTextColor(sel ? 0xFFFFFFFF : getResources().getColor(R.color.text_secondary, null));
                }
                loadImages();
            });
            chipContainer.addView(chip);
        }

        loadImages();
    }

    /**
     * Tải sự kiện theo giai đoạn đang chọn.
     * Mỗi sự kiện cung cấp imageUrl để hiển thị trong thư viện ảnh.
     */
    private void loadImages() {
        if (selectedPeriod.equals("Tất cả")) {
            db.historyEventDao().getAllEvents().observe(this, this::showImages);
        } else {
            db.historyEventDao().getEventsByPeriod(selectedPeriod).observe(this, this::showImages);
        }
    }

    /**
     * Hiển thị danh sách ảnh bằng RecyclerView dạng lưới.
     * ImageUtils dùng Glide để tải ảnh từ URL/drawable vào từng ImageView.
     */
    private void showImages(List<HistoryEventEntity> events) {
        rv.setAdapter(new RecyclerView.Adapter<ImageVH>() {
            @Override public ImageVH onCreateViewHolder(ViewGroup parent, int viewType) {
                LinearLayout ll = new LinearLayout(ImageGalleryActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setPadding(8, 8, 8, 8);
                ImageView iv = new ImageView(ImageGalleryActivity.this);
                iv.setId(android.R.id.icon);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(120)));
                iv.setClipToOutline(true);
                ll.addView(iv);
                TextView tv = new TextView(ImageGalleryActivity.this);
                tv.setId(android.R.id.text1);
                tv.setPadding(4, 8, 4, 0);
                tv.setTextSize(12);
                tv.setMaxLines(2);
                ll.addView(tv);
                TextView tv2 = new TextView(ImageGalleryActivity.this);
                tv2.setId(android.R.id.text2);
                tv2.setPadding(4, 2, 4, 0);
                tv2.setTextSize(10);
                tv2.setTextColor(getResources().getColor(R.color.text_tertiary, null));
                ll.addView(tv2);
                return new ImageVH(ll);
            }
            @Override public void onBindViewHolder(ImageVH h, int pos) {
                HistoryEventEntity e = events.get(pos);
                if (e.imageUrl != null) ImageUtils.load(ImageGalleryActivity.this, e.imageUrl, h.iv);
                h.title.setText(e.title);
                h.title.setTextColor(getResources().getColor(R.color.text_primary, null));
                h.subtitle.setText("Năm " + e.year + " · " + (e.period != null ? e.period : ""));
                h.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(ImageGalleryActivity.this, EventDetailActivity.class);
                    intent.putExtra("event_id", e.id);
                    startActivity(intent);
                });
            }
            @Override public int getItemCount() { return events.size(); }
        });
    }

    /** ViewHolder cho một ô ảnh lịch sử trong lưới. */
    static class ImageVH extends RecyclerView.ViewHolder {
        ImageView iv; TextView title, subtitle;
        ImageVH(View v) {
            super(v);
            iv = v.findViewById(android.R.id.icon);
            title = v.findViewById(android.R.id.text1);
            subtitle = v.findViewById(android.R.id.text2);
        }
    }

    /** Đổi dp sang pixel khi tạo kích thước ảnh bằng code. */
    private int dpToPx(int dp) { return (int)(dp * getResources().getDisplayMetrics().density); }
}
