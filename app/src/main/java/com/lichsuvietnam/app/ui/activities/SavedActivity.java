package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import com.lichsuvietnam.app.utils.SessionManager;

/**
 * Màn hình lưu trữ mục yêu thích.
 * Activity đọc danh sách sự kiện đã bookmark của người dùng đang đăng nhập
 * và cho phép bấm vào từng mục để xem lại chi tiết sự kiện.
 */
public class SavedActivity extends AppCompatActivity {
    /**
     * Khởi tạo danh sách bookmark, xử lý trạng thái chưa đăng nhập/rỗng
     * và gắn adapter hiển thị sự kiện đã lưu.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        SessionManager session = new SessionManager(this);
        AppDatabase db = AppDatabase.getInstance(this);
        long userId = session.getUserId();

        RecyclerView rv = findViewById(R.id.rvSaved);
        rv.setLayoutManager(new LinearLayoutManager(this));
        View emptyState = findViewById(R.id.emptyState);

        if (!session.isLoggedIn() || userId <= 0) {
            rv.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        // BookmarkDao trả LiveData để danh sách tự cập nhật khi người dùng lưu/bỏ lưu.
        db.bookmarkDao().getBookmarkedEvents(userId).observe(this, events -> {
            if (events == null || events.isEmpty()) {
                rv.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            } else {
                rv.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);

                rv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @Override public RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
                        LinearLayout row = new LinearLayout(SavedActivity.this);
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setGravity(Gravity.CENTER_VERTICAL);
                        row.setBackgroundResource(R.drawable.bg_rounded_card);
                        row.setPadding(dp(14), dp(14), dp(14), dp(14));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.bottomMargin = dp(10);
                        lp.leftMargin = dp(16);
                        lp.rightMargin = dp(16);
                        row.setLayoutParams(lp);

                        TextView year = new TextView(SavedActivity.this);
                        year.setId(android.R.id.text1);
                        year.setBackgroundResource(R.drawable.bg_year_tag);
                        year.setPadding(dp(10), dp(6), dp(10), dp(6));
                        year.setTextColor(0xFFFFFFFF);
                        year.setTextSize(12);
                        year.setTypeface(null, Typeface.BOLD);
                        row.addView(year);

                        LinearLayout info = new LinearLayout(SavedActivity.this);
                        info.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                        ilp.setMarginStart(dp(14));
                        info.setLayoutParams(ilp);
                        TextView title = new TextView(SavedActivity.this);
                        title.setId(android.R.id.text2);
                        title.setTextSize(14);
                        title.setTypeface(null, Typeface.BOLD);
                        info.addView(title);
                        TextView sub = new TextView(SavedActivity.this);
                        sub.setId(android.R.id.message);
                        sub.setTextSize(11);
                        info.addView(sub);
                        row.addView(info);

                        TextView bookmark = new TextView(SavedActivity.this);
                        bookmark.setText("★");
                        bookmark.setTextColor(getResources().getColor(R.color.gold, null));
                        bookmark.setTextSize(20);
                        row.addView(bookmark);

                        return new RecyclerView.ViewHolder(row) {};
                    }

                    @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
                        HistoryEventEntity e = events.get(pos);
                        ((TextView) h.itemView.findViewById(android.R.id.text1)).setText(e.year);
                        TextView title = h.itemView.findViewById(android.R.id.text2);
                        title.setText(e.title);
                        title.setTextColor(getResources().getColor(R.color.text_primary, null));
                        TextView sub = h.itemView.findViewById(android.R.id.message);
                        sub.setText((e.location != null ? e.location : "") + " · " + (e.period != null ? e.period : ""));
                        sub.setTextColor(getResources().getColor(R.color.text_tertiary, null));
                        h.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(SavedActivity.this, EventDetailActivity.class);
                            intent.putExtra("event_id", e.id);
                            startActivity(intent);
                        });
                    }
                    @Override public int getItemCount() { return events.size(); }
                });
            }
        });
    }

    /** Đổi dp sang pixel khi tạo item động bằng code. */
    private int dp(int dp) { return (int)(dp * getResources().getDisplayMetrics().density); }
}
