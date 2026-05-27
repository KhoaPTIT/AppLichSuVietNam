package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import com.lichsuvietnam.app.data.database.entities.HistoricalFigureEntity;
import com.lichsuvietnam.app.data.database.entities.SearchHistoryEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Màn hình tìm kiếm và lọc nhanh nội dung lịch sử.
 * Người dùng có thể tìm sự kiện, nhân vật, địa danh; các truy vấn hợp lệ
 * được lưu vào lịch sử tìm kiếm cho tài khoản đã đăng nhập.
 */
public class SearchActivity extends AppCompatActivity {
    // Room database dùng để tìm sự kiện, nhân vật và lưu lịch sử tìm kiếm.
    private AppDatabase db;
    // Quản lý trạng thái đăng nhập để xác định có lưu lịch sử hay không.
    private SessionManager session;
    private EditText etSearch;
    private LinearLayout recentContainer;
    // Tab hiện tại quyết định hiển thị gợi ý sự kiện, nhân vật hoặc địa danh.
    private String currentTab = "events";

    /**
     * Khởi tạo màn hình tìm kiếm, gắn TextWatcher cho ô nhập và cấu hình tab lọc.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        etSearch = findViewById(R.id.etSearch);
        recentContainer = findViewById(R.id.recentContainer);
        etSearch.requestFocus();

        // TextWatcher là API Android dùng để bắt thay đổi nội dung nhập theo thời gian thực.
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    performSearch(query);
                } else {
                    showDefaultContent();
                }
            }
        });

        TextView tabEvents = findViewById(R.id.tabEvents);
        TextView tabFigures = findViewById(R.id.tabFigures);
        TextView tabPlaces = findViewById(R.id.tabPlaces);
        tabEvents.setOnClickListener(v -> { selectTab(tabEvents, tabFigures, tabPlaces); currentTab = "events"; showDefaultContent(); });
        tabFigures.setOnClickListener(v -> { selectTab(tabFigures, tabEvents, tabPlaces); currentTab = "figures"; showDefaultContent(); });
        tabPlaces.setOnClickListener(v -> { selectTab(tabPlaces, tabEvents, tabFigures); currentTab = "places"; showDefaultContent(); });

        showDefaultContent();
    }

    /**
     * Cập nhật style cho tab đang được chọn và đưa các tab còn lại về trạng thái thường.
     */
    private void selectTab(TextView selected, TextView other1, TextView other2) {
        selected.setTextColor(getResources().getColor(R.color.red_primary, null));
        selected.setTypeface(null, android.graphics.Typeface.BOLD);
        other1.setTextColor(getResources().getColor(R.color.text_tertiary, null));
        other1.setTypeface(null, android.graphics.Typeface.NORMAL);
        other2.setTextColor(getResources().getColor(R.color.text_tertiary, null));
        other2.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    /**
     * Hiển thị nội dung mặc định khi người dùng chưa nhập đủ từ khóa:
     * lịch sử tìm kiếm gần đây hoặc các gợi ý theo tab.
     */
    private void showDefaultContent() {
        String query = etSearch.getText().toString().trim();
        if (query.length() >= 2) { performSearch(query); return; }

        recentContainer.removeAllViews();
        switch (currentTab) {
            case "figures": showSuggestions("Nhân vật gợi ý", new String[]{"Hai Bà Trưng", "Ngô Quyền", "Trần Hưng Đạo", "Lê Lợi", "Quang Trung", "Hồ Chí Minh", "Võ Nguyên Giáp"}, "👤 "); break;
            case "places": showSuggestions("Địa danh gợi ý", new String[]{"Mê Linh", "Bạch Đằng", "Thăng Long", "Lam Sơn", "Điện Biên Phủ", "Sài Gòn", "Huế"}, "📍 "); break;
            default: loadRecentSearches(); break;
        }
    }

    /**
     * Tạo danh sách gợi ý có thể bấm để điền nhanh vào ô tìm kiếm.
     */
    private void showSuggestions(String title, String[] items, String prefix) {
        recentContainer.removeAllViews();
        addSectionTitle(title);
        for (String s : items) addClickableItem(prefix + s, s);
    }

    /**
     * Tải lịch sử tìm kiếm gần đây của người dùng đã đăng nhập.
     * Truy vấn chạy trên background thread để tránh khóa UI.
     */
    private void loadRecentSearches() {
        long userId = session.getUserId();
        boolean isLoggedIn = session.isLoggedIn() && userId > 0;
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> recents;
            if (isLoggedIn) {
                recents = db.searchHistoryDao().getRecentSearchesSync(userId);
            } else {
                recents = new java.util.ArrayList<>();
            }
            runOnUiThread(() -> {
                recentContainer.removeAllViews();
                // Lọc bỏ truy vấn quá ngắn hoặc chỉ gồm số/khoảng trắng.
                boolean hasValidRecent = false;
                for (String r : recents) {
                    if (r != null && r.trim().length() >= 2 && !r.trim().matches("^[\\s\\d]+$")) {
                        if (!hasValidRecent) { addSectionTitle("Tìm kiếm gần đây"); hasValidRecent = true; }
                        addClickableItem("🕐 " + r.trim(), r.trim());
                    }
                }
                if (!hasValidRecent) {
                    addSectionTitle("Gợi ý tìm kiếm");
                    for (String d : new String[]{"Hai Bà Trưng", "Chiến thắng Bạch Đằng", "Nhà Lý"}) addClickableItem("🔍 " + d, d);
                }
            });
        });
    }

    /**
     * Thêm tiêu đề nhỏ cho từng nhóm kết quả hoặc gợi ý.
     */
    private void addSectionTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextSize(12);
        title.setTextColor(getResources().getColor(R.color.text_tertiary, null));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 8, 0, 12);
        recentContainer.addView(title);
    }

    /**
     * Thêm một dòng gợi ý có thể bấm để chạy lại tìm kiếm với từ khóa tương ứng.
     */
    private void addClickableItem(String display, String searchTerm) {
        TextView tv = new TextView(this);
        tv.setText(display);
        tv.setTextSize(13);
        tv.setPadding(0, 14, 0, 14);
        tv.setTextColor(getResources().getColor(R.color.text_secondary, null));
        tv.setOnClickListener(v -> { etSearch.setText(searchTerm); etSearch.setSelection(searchTerm.length()); performSearch(searchTerm); });
        recentContainer.addView(tv);
    }

    /**
     * Thực hiện tìm kiếm sự kiện và nhân vật theo từ khóa.
     * DAO Room trả về LiveData cho sự kiện; phần tìm nhân vật chạy bằng truy vấn sync
     * trên background thread rồi trả kết quả về UI thread.
     */
    private void performSearch(String query) {
        long userId = session.getUserId();
        boolean isLoggedIn = session.isLoggedIn() && userId > 0;
        // Chỉ lưu lịch sử tìm kiếm hợp lệ cho người dùng đã đăng nhập.
        if (isLoggedIn && query.length() >= 2) {
            Executors.newSingleThreadExecutor().execute(() -> db.searchHistoryDao().insert(new SearchHistoryEntity(userId, query)));
        }

        db.historyEventDao().searchEvents(query).observe(this, results -> {
            recentContainer.removeAllViews();
            Executors.newSingleThreadExecutor().execute(() -> {
                List<HistoricalFigureEntity> figureResults = db.figureDao().searchFiguresSync(query);
                runOnUiThread(() -> {
                    if (results.isEmpty() && figureResults.isEmpty()) {
                        TextView tv = new TextView(this);
                        tv.setText("Không tìm thấy kết quả cho \"" + query + "\"");
                        tv.setTextSize(14); tv.setPadding(0, 80, 0, 0);
                        tv.setGravity(android.view.Gravity.CENTER);
                        tv.setTextColor(getResources().getColor(R.color.text_secondary, null));
                        recentContainer.addView(tv);
                        return;
                    }

                    if (!figureResults.isEmpty()) addSectionTitle("Nhân vật (" + figureResults.size() + ")");
                    for (HistoricalFigureEntity figure : figureResults) {
                        LinearLayout item = new LinearLayout(this);
                        item.setOrientation(LinearLayout.VERTICAL);
                        item.setBackgroundResource(R.drawable.bg_rounded_card);
                        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mp.bottomMargin = 12; item.setLayoutParams(mp); item.setPadding(24, 14, 24, 14);

                        TextView tvTag = new TextView(this); tvTag.setText("NHÂN VẬT"); tvTag.setTextSize(10); tvTag.setTextColor(getResources().getColor(R.color.gold_dark, null)); tvTag.setTypeface(null, android.graphics.Typeface.BOLD); item.addView(tvTag);
                        TextView tvTitle = new TextView(this); tvTitle.setText(figure.name); tvTitle.setTextSize(13); tvTitle.setTextColor(getResources().getColor(R.color.text_primary, null)); tvTitle.setTypeface(null, android.graphics.Typeface.BOLD); tvTitle.setPadding(0, 4, 0, 0); item.addView(tvTitle);
                        TextView tvSub = new TextView(this); tvSub.setText((figure.title != null ? figure.title : "") + " • " + (figure.dynasty != null ? figure.dynasty : "")); tvSub.setTextSize(11); tvSub.setTextColor(getResources().getColor(R.color.text_tertiary, null)); item.addView(tvSub);
                        item.setOnClickListener(v -> startActivity(new Intent(this, FigureDetailActivity.class).putExtra("figure_id", figure.id)));
                        recentContainer.addView(item);
                    }

                    if (!results.isEmpty()) addSectionTitle("Sự kiện (" + results.size() + ")");
                    for (HistoryEventEntity event : results) {
                        LinearLayout item = new LinearLayout(this);
                        item.setOrientation(LinearLayout.VERTICAL);
                        item.setBackgroundResource(R.drawable.bg_rounded_card);
                        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mp.bottomMargin = 12; item.setLayoutParams(mp); item.setPadding(24, 14, 24, 14);

                        TextView tvTitle = new TextView(this); tvTitle.setText(event.title); tvTitle.setTextSize(13); tvTitle.setTextColor(getResources().getColor(R.color.text_primary, null)); tvTitle.setTypeface(null, android.graphics.Typeface.BOLD); item.addView(tvTitle);
                        TextView tvSub = new TextView(this); tvSub.setText("Năm " + event.year + " • " + (event.location != null ? event.location : "")); tvSub.setTextSize(11); tvSub.setTextColor(getResources().getColor(R.color.text_tertiary, null)); item.addView(tvSub);
                        item.setOnClickListener(v -> startActivity(new Intent(this, EventDetailActivity.class).putExtra("event_id", event.id)));
                        recentContainer.addView(item);
                    }
                });
            });
        });
    }
}
