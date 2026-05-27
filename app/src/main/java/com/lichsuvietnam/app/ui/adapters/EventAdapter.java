package com.lichsuvietnam.app.ui.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter hiển thị danh sách sự kiện lịch sử.
 * Adapter hỗ trợ hai chế độ: hiển thị phẳng hoặc nhóm theo giai đoạn lịch sử
 * bằng các header riêng trong RecyclerView.
 */
public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_EVENT = 1;

    private final List<Object> items = new ArrayList<>();
    private final OnEventClick listener;

    /**
     * Callback khi người dùng chọn một sự kiện trong danh sách.
     */
    public interface OnEventClick {
        void onClick(HistoryEventEntity event);
    }

    /**
     * Khởi tạo adapter với callback mở chi tiết sự kiện.
     */
    public EventAdapter(OnEventClick listener) {
        this.listener = listener;
    }

    /**
     * Cập nhật dữ liệu cho adapter.
     *
     * @param events danh sách sự kiện lấy từ Room
     * @param groupByPeriod true nếu cần nhóm sự kiện theo giai đoạn
     */
    public void updateData(List<HistoryEventEntity> events, boolean groupByPeriod) {
        items.clear();
        if (events == null || events.isEmpty()) {
            notifyDataSetChanged();
            return;
        }
        if (groupByPeriod) {
            // Nhóm sự kiện theo giai đoạn và giữ đúng thứ tự chèn.
            LinkedHashMap<String, List<HistoryEventEntity>> grouped = new LinkedHashMap<>();
            // Thứ tự giai đoạn cố định để danh sách dễ theo dõi.
            String[] periodOrder = {
                "Dựng nước", "Bắc thuộc", "Phong kiến", "Cận đại", "Hiện đại"
            };
            for (String p : periodOrder) {
                grouped.put(p, new ArrayList<>());
            }
            for (HistoryEventEntity e : events) {
                String period = e.period != null ? e.period : "Khác";
                if (!grouped.containsKey(period)) {
                    grouped.put(period, new ArrayList<>());
                }
                grouped.get(period).add(e);
            }
            for (Map.Entry<String, List<HistoryEventEntity>> entry : grouped.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    items.add(new SectionHeader(entry.getKey(), entry.getValue().size()));
                    items.addAll(entry.getValue());
                }
            }
        } else {
            items.addAll(events);
        }
        notifyDataSetChanged();
    }

    /**
     * Xác định loại item tại vị trí hiện tại: header giai đoạn hoặc sự kiện.
     */
    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof SectionHeader ? TYPE_HEADER : TYPE_EVENT;
    }

    /**
     * Inflate layout tương ứng với loại item trong RecyclerView.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderVH(inf.inflate(R.layout.item_event_section_header, parent, false));
        }
        return new EventVH(inf.inflate(R.layout.item_event, parent, false));
    }

    /**
     * Gắn dữ liệu header hoặc sự kiện vào ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            SectionHeader header = (SectionHeader) items.get(position);
            ((HeaderVH) holder).txtTitle.setText(header.title);
            ((HeaderVH) holder).txtCount.setText(header.count + " sự kiện");
        } else if (holder instanceof EventVH) {
            HistoryEventEntity event = (HistoryEventEntity) items.get(position);
            EventVH h = (EventVH) holder;
            h.txtYear.setText(event.year);
            h.txtTitle.setText(event.title);
            h.txtDesc.setText(event.description);

            if (event.period != null && !event.period.isEmpty()) {
                h.txtPeriod.setText(event.period);
                h.txtPeriod.setVisibility(View.VISIBLE);
            } else {
                h.txtPeriod.setVisibility(View.GONE);
            }

            String loc = event.location != null ? event.location : "";
            if (!loc.isEmpty()) {
                h.txtLocation.setText("📍 " + loc);
                h.txtLocation.setVisibility(View.VISIBLE);
            } else {
                h.txtLocation.setVisibility(View.GONE);
            }

            h.itemView.setOnClickListener(v -> listener.onClick(event));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder cho item sự kiện.

    static class EventVH extends RecyclerView.ViewHolder {
        TextView txtYear, txtTitle, txtDesc, txtPeriod, txtLocation;

        EventVH(View v) {
            super(v);
            txtYear = v.findViewById(R.id.txtEventYear);
            txtTitle = v.findViewById(R.id.txtEventTitle);
            txtDesc = v.findViewById(R.id.txtEventDesc);
            txtPeriod = v.findViewById(R.id.txtEventPeriod);
            txtLocation = v.findViewById(R.id.txtEventLocation);
        }
    }

    // ViewHolder cho header nhóm giai đoạn.
    static class HeaderVH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCount;

        HeaderVH(View v) {
            super(v);
            txtTitle = v.findViewById(R.id.txtSectionTitle);
            txtCount = v.findViewById(R.id.txtSectionCount);
        }
    }

    // Model nội bộ dùng để biểu diễn header của từng nhóm giai đoạn.

    public static class SectionHeader {
        public final String title;
        public final int count;

        public SectionHeader(String title, int count) {
            this.title = title;
            this.count = count;
        }
    }
}
