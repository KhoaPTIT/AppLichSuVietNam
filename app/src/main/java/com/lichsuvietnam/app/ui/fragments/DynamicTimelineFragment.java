package com.lichsuvietnam.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.slider.RangeSlider;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.activities.EventDetailActivity;
import com.lichsuvietnam.app.ui.adapters.TimelineAdapter;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import com.lichsuvietnam.app.data.models.HistoryEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị chức năng Đường thời gian động.
 * Người dùng lọc sự kiện theo giai đoạn và khoảng năm, sau đó mở màn hình
 * chi tiết của sự kiện được chọn trong danh sách timeline.
 */
public class DynamicTimelineFragment extends Fragment {
    // CSDL Room dùng để theo dõi và truy vấn sự kiện lịch sử.
    private AppDatabase db;
    // RecyclerView và adapter dùng để hiển thị danh sách sự kiện dạng timeline dọc.
    private RecyclerView rvEvents;
    private TimelineAdapter adapter;
    // Nhãn hiển thị khoảng năm đang lọc và số lượng sự kiện phù hợp.
    private TextView txtYearRange, txtEventCount;
    // Giai đoạn đang lọc, giá trị phải khớp với dữ liệu period trong database.
    private String selectedPeriod = "Tất cả";
    private LinearLayout chipContainer;
    // RangeSlider của Material Components dùng để chọn năm bắt đầu và kết thúc.
    private RangeSlider rangeSlider;
    private int currentFromYear = 0;
    private int currentToYear = 2000;

    /**
     * Khởi tạo giao diện timeline động, kết nối Room, cấu hình bộ lọc
     * và tải danh sách sự kiện ban đầu.
     */
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic_timeline, container, false);
        // AppDatabase là singleton của Room dùng chung trong ứng dụng.
        db = AppDatabase.getInstance(requireContext());

        txtYearRange = view.findViewById(R.id.txtYearRangeDt);
        txtEventCount = view.findViewById(R.id.txtEventCountDt);
        rvEvents = view.findViewById(R.id.rvTimelineEventsDt);
        chipContainer = view.findViewById(R.id.chipContainerDt);

        rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));

        // RangeSlider là widget ngoài từ Material Components.
        // Hai thumb trả về năm bắt đầu/kết thúc để áp dụng bộ lọc.
        rangeSlider = view.findViewById(R.id.rangeSliderDt);
        rangeSlider.setValues(0f, 2000f);
        rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                List<Float> values = slider.getValues();
                currentFromYear = values.get(0).intValue();
                currentToYear = values.get(1).intValue();
                txtYearRange.setText("Năm " + currentFromYear + " – " + currentToYear);
                applyFilters();
            }
        });

        // Tạo các chip lọc theo giai đoạn lịch sử.
        buildChips(view);

        // Tải toàn bộ sự kiện khi chưa có bộ lọc cụ thể.
        loadAllEvents();

        // Nút quay lại màn hình trước.
        ImageView btnBack = view.findViewById(R.id.btnBackDt);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        return view;
    }

    /**
     * Tạo các chip lọc giai đoạn bằng code để có thể cập nhật lại style
     * đang chọn sau mỗi lần người dùng bấm.
     *
     * @param root view gốc của Fragment
     */
    private void buildChips(View root) {
        chipContainer.removeAllViews();
        String[] periods = {"Tất cả", "Bắc thuộc", "Phong kiến", "Cận đại", "Hiện đại"};
        for (String period : periods) {
            TextView chip = new TextView(requireContext());
            chip.setText(period);
            chip.setPadding(36, 14, 36, 14);
            chip.setTextSize(13);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(8);
            chip.setLayoutParams(lp);

            if (period.equals(selectedPeriod)) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected);
                chip.setTextColor(0xFFFFFFFF);
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip);
                chip.setTextColor(getResources().getColor(R.color.text_secondary, null));
            }

            chip.setOnClickListener(v -> {
                selectedPeriod = period;
                buildChips(root);
                applyFilters();
            });
            chipContainer.addView(chip);
        }
    }

    /**
     * Áp dụng bộ lọc giai đoạn và khoảng năm hiện tại.
     * Các hàm DAO của Room trả về LiveData nên observe() sẽ tự cập nhật UI
     * khi dữ liệu trong database thay đổi.
     */
    private void applyFilters() {
        if (selectedPeriod.equals("Tất cả")) {
            db.historyEventDao().getAllEvents().observe(getViewLifecycleOwner(), entities -> {
                List<HistoryEventEntity> filtered = filterByYear(entities);
                displayEvents(filtered);
            });
        } else {
            db.historyEventDao().getEventsByPeriod(selectedPeriod).observe(getViewLifecycleOwner(), entities -> {
                List<HistoryEventEntity> filtered = filterByYear(entities);
                displayEvents(filtered);
            });
        }
    }

    /**
     * Lọc danh sách entity theo yearNumeric để chỉ giữ lại sự kiện nằm
     * trong khoảng năm người dùng chọn trên RangeSlider.
     */
    private List<HistoryEventEntity> filterByYear(List<HistoryEventEntity> entities) {
        List<HistoryEventEntity> filtered = new ArrayList<>();
        for (HistoryEventEntity e : entities) {
            if (e.yearNumeric >= currentFromYear && e.yearNumeric <= currentToYear) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    /**
     * Tải toàn bộ sự kiện lịch sử cho trạng thái ban đầu của màn hình.
     * LiveData giúp danh sách tự làm mới trong vòng đời của Fragment.
     */
    private void loadAllEvents() {
        db.historyEventDao().getAllEvents().observe(getViewLifecycleOwner(), this::displayEvents);
    }

    /**
     * Chuyển entity từ database sang model hiển thị và gắn vào TimelineAdapter.
     * Khi bấm vào một item, Intent của Android mở EventDetailActivity và truyền
     * event_id để màn hình chi tiết tải đúng sự kiện.
     */
    private void displayEvents(List<HistoryEventEntity> entities) {
        txtEventCount.setText(entities.size() + " sự kiện");
        List<HistoryEvent> events = new ArrayList<>();
        for (HistoryEventEntity e : entities) {
            events.add(new HistoryEvent(e.year, e.title, e.description, e.imageUrl));
        }
        adapter = new TimelineAdapter(events, event -> {
            for (HistoryEventEntity entity : entities) {
                if (entity.title.equals(event.getTitle())) {
                    Intent intent = new Intent(requireContext(), EventDetailActivity.class);
                    intent.putExtra("event_id", entity.id);
                    startActivity(intent);
                    break;
                }
            }
        });
        rvEvents.setAdapter(adapter);
    }
}
