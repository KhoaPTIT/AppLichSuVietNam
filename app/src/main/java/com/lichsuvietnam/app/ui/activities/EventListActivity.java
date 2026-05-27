package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.adapters.EventAdapter;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Màn hình danh sách sự kiện lịch sử.
 * Chức năng chính gồm lọc sự kiện theo giai đoạn, tìm kiếm theo từ khóa
 * và mở màn hình chi tiết sự kiện khi người dùng chọn một item.
 */
public class EventListActivity extends AppCompatActivity {
    // Room database dùng để lấy danh sách sự kiện và đếm tổng số sự kiện.
    private AppDatabase db;
    // Adapter hiển thị sự kiện, có thể nhóm theo giai đoạn.
    private EventAdapter adapter;
    // Giai đoạn đang được lọc trên chip.
    private String selectedPeriod = "Tất cả";
    private LinearLayout chipContainer;
    private EditText edtSearch;
    private View emptyState;
    private RecyclerView rv;

    /**
     * Khởi tạo RecyclerView, bộ lọc chip, ô tìm kiếm và tải dữ liệu ban đầu.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        db = AppDatabase.getInstance(this);
        emptyState = findViewById(R.id.emptyStateEvents);
        rv = findViewById(R.id.rvEvents);

        findViewById(R.id.btnBackEvents).setOnClickListener(v -> finish());

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(event -> {
            Intent intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("event_id", event.id);
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // Đếm tổng số sự kiện trên background thread để không chặn UI.
        Executors.newSingleThreadExecutor().execute(() -> {
            int count = db.historyEventDao().getCount();
            runOnUiThread(() -> {
                TextView txtCount = findViewById(R.id.txtEventCount);
                txtCount.setText(String.valueOf(count));
            });
        });

        // Tạo bộ lọc theo giai đoạn lịch sử.
        chipContainer = findViewById(R.id.chipContainerEvents);
        buildPeriodChips();

        // TextWatcher bắt từ khóa tìm kiếm và lọc sự kiện theo thời gian thực.
        edtSearch = findViewById(R.id.edtSearchEvents);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadEvents();
                } else {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<HistoryEventEntity> results = db.historyEventDao().searchEventsSync(query);
                        runOnUiThread(() -> {
                            adapter.updateData(results, false);
                            emptyState.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
                            rv.setVisibility(results.isEmpty() ? View.GONE : View.VISIBLE);
                        });
                    });
                }
            }
        });

        // Tải dữ liệu mặc định sau khi màn hình sẵn sàng.
        loadEvents();
    }

    /**
     * Tải sự kiện theo giai đoạn đang chọn.
     * Room LiveData giúp danh sách tự cập nhật khi database thay đổi.
     */
    private void loadEvents() {
        if (selectedPeriod.equals("Tất cả")) {
            db.historyEventDao().getAllEvents().observe(this, this::showEvents);
        } else {
            db.historyEventDao().getEventsByPeriod(selectedPeriod).observe(this, this::showEvents);
        }
    }

    /**
     * Đổ danh sách sự kiện vào adapter và bật/tắt trạng thái rỗng.
     */
    private void showEvents(List<HistoryEventEntity> events) {
        boolean groupByPeriod = selectedPeriod.equals("Tất cả");
        adapter.updateData(events, groupByPeriod);
        emptyState.setVisibility(events == null || events.isEmpty() ? View.VISIBLE : View.GONE);
        rv.setVisibility(events == null || events.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /**
     * Tạo các chip giai đoạn. Khi chọn chip mới, xóa từ khóa tìm kiếm
     * và tải lại dữ liệu theo giai đoạn.
     */
    private void buildPeriodChips() {
        chipContainer.removeAllViews();
        String[] periods = {"Tất cả", "Dựng nước", "Bắc thuộc", "Phong kiến", "Cận đại", "Hiện đại"};
        for (String period : periods) {
            TextView chip = createChip(period, period.equals(selectedPeriod));
            chip.setOnClickListener(v -> {
                selectedPeriod = period;
                buildPeriodChips();
                edtSearch.setText("");
                loadEvents();
            });
            chipContainer.addView(chip);
        }
    }

    /**
     * Tạo một chip lọc với style khác nhau giữa trạng thái đang chọn và chưa chọn.
     */
    private TextView createChip(String text, boolean selected) {
        TextView chip = new TextView(this);
        chip.setText(text);
        chip.setPadding(36, 14, 36, 14);
        chip.setTextSize(12);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(8);
        chip.setLayoutParams(lp);

        if (selected) {
            chip.setBackgroundResource(R.drawable.bg_chip_selected);
            chip.setTextColor(0xFFFFFFFF);
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip);
            chip.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }
        return chip;
    }
}
