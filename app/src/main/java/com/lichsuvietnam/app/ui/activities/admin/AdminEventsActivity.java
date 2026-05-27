package com.lichsuvietnam.app.ui.activities.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminEventsActivity extends AppCompatActivity {
    private AppDatabase db;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!new SessionManager(this).isAdmin()) { finish(); return; }

        setContentView(R.layout.activity_admin_events);
        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnBackAdmin).setOnClickListener(v -> finish());
        rv = findViewById(R.id.rvEvents);
        rv.setLayoutManager(new LinearLayoutManager(this));

        EditText etSearch = findViewById(R.id.etSearchEvents);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String q = s.toString().trim();
                if (q.length() >= 2) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<HistoryEventEntity> r = db.historyEventDao().searchEventsSync(q);
                        runOnUiThread(() -> showEvents(r));
                    });
                } else loadEvents();
            }
        });

        findViewById(R.id.btnAddEvent).setOnClickListener(v -> showEditDialog(null));
        loadEvents();
    }

    private void loadEvents() {
        db.historyEventDao().getAllEvents().observe(this, events -> { if (events != null) showEvents(events); });
    }

    private void showEvents(List<HistoryEventEntity> events) {
        rv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LinearLayout row = new LinearLayout(AdminEventsActivity.this);
                row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL);
                row.setBackgroundResource(R.drawable.bg_rounded_card); row.setPadding(dp(14), dp(12), dp(14), dp(12));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = dp(8); lp.leftMargin = dp(16); lp.rightMargin = dp(16); row.setLayoutParams(lp);

                LinearLayout info = new LinearLayout(AdminEventsActivity.this); info.setOrientation(LinearLayout.VERTICAL);
                info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                TextView tvTitle = new TextView(AdminEventsActivity.this); tvTitle.setId(android.R.id.text1); tvTitle.setTextSize(13); tvTitle.setTextColor(getColor(R.color.text_primary)); tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                TextView tvSub = new TextView(AdminEventsActivity.this); tvSub.setId(android.R.id.text2); tvSub.setTextSize(11); tvSub.setTextColor(getColor(R.color.text_tertiary));
                info.addView(tvTitle); info.addView(tvSub); row.addView(info);

                TextView btnEdit = new TextView(AdminEventsActivity.this); btnEdit.setId(android.R.id.button1); btnEdit.setText("✏️"); btnEdit.setTextSize(18); btnEdit.setPadding(dp(8), 0, dp(8), 0);
                TextView btnDel = new TextView(AdminEventsActivity.this); btnDel.setId(android.R.id.button2); btnDel.setText("🗑️"); btnDel.setTextSize(18); btnDel.setPadding(dp(8), 0, dp(8), 0);
                row.addView(btnEdit); row.addView(btnDel);
                return new RecyclerView.ViewHolder(row) {};
            }
            @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
                HistoryEventEntity e = events.get(pos);
                ((TextView) h.itemView.findViewById(android.R.id.text1)).setText(e.title);
                ((TextView) h.itemView.findViewById(android.R.id.text2)).setText("Năm " + e.year + " • " + (e.period != null ? e.period : "") + " • " + (e.location != null ? e.location : ""));
                h.itemView.findViewById(android.R.id.button1).setOnClickListener(v -> showEditDialog(e));
                h.itemView.findViewById(android.R.id.button2).setOnClickListener(v ->
                    new AlertDialog.Builder(AdminEventsActivity.this).setMessage("Xóa sự kiện \"" + e.title + "\"?").setPositiveButton("Xóa", (d, w) -> {
                        Executors.newSingleThreadExecutor().execute(() -> { db.historyEventDao().delete(e); runOnUiThread(() -> { Toast.makeText(AdminEventsActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show(); loadEvents(); }); });
                    }).setNegativeButton("Hủy", null).show());
            }
            @Override public int getItemCount() { return events.size(); }
        });
    }

    private void showEditDialog(HistoryEventEntity existing) {
        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this); ll.setOrientation(LinearLayout.VERTICAL); ll.setPadding(dp(20), dp(10), dp(20), dp(10));
        EditText etTitle = addField(ll, "Tiêu đề", existing != null ? existing.title : "");
        EditText etYear = addField(ll, "Năm", existing != null ? existing.year : "");
        EditText etDesc = addField(ll, "Mô tả", existing != null ? existing.description : "");
        EditText etFull = addField(ll, "Nội dung đầy đủ", existing != null && existing.fullContent != null ? existing.fullContent : "");
        EditText etLoc = addField(ll, "Địa điểm", existing != null && existing.location != null ? existing.location : "");
        EditText etPeriod = addField(ll, "Thời kỳ", existing != null && existing.period != null ? existing.period : "");
        EditText etCategory = addField(ll, "Danh mục", existing != null && existing.category != null ? existing.category : "");
        EditText etKeyFig = addField(ll, "Nhân vật chính", existing != null && existing.keyFigures != null ? existing.keyFigures : "");
        sv.addView(ll);

        new AlertDialog.Builder(this).setTitle(existing == null ? "Thêm sự kiện" : "Sửa sự kiện").setView(sv)
            .setPositiveButton("Lưu", (d, w) -> {
                String title = etTitle.getText().toString().trim();
                String year = etYear.getText().toString().trim();
                String desc = etDesc.getText().toString().trim();
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(year) || TextUtils.isEmpty(desc)) { Toast.makeText(this, "Điền đủ tiêu đề, năm, mô tả", Toast.LENGTH_SHORT).show(); return; }
                Executors.newSingleThreadExecutor().execute(() -> {
                    HistoryEventEntity e = existing != null ? existing : new HistoryEventEntity(year, title, desc);
                    e.title = title; e.year = year; e.description = desc;
                    e.fullContent = etFull.getText().toString().trim();
                    e.location = etLoc.getText().toString().trim();
                    e.period = etPeriod.getText().toString().trim();
                    e.category = etCategory.getText().toString().trim();
                    e.keyFigures = etKeyFig.getText().toString().trim();
                    try { e.yearNumeric = Integer.parseInt(year.replaceAll("[^0-9-]", "")); } catch (Exception ex) { e.yearNumeric = 0; }
                    if (existing != null) db.historyEventDao().update(e); else db.historyEventDao().insert(e);
                    runOnUiThread(() -> { Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show(); loadEvents(); });
                });
            }).setNegativeButton("Hủy", null).show();
    }

    private EditText addField(LinearLayout parent, String hint, String value) {
        EditText et = new EditText(this); et.setHint(hint); et.setText(value); et.setTextSize(13);
        et.setBackgroundResource(R.drawable.bg_rounded_input_bg); et.setPadding(dp(12), dp(8), dp(12), dp(8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(8); et.setLayoutParams(lp);
        parent.addView(et); return et;
    }

    private int dp(int dp) { return (int)(dp * getResources().getDisplayMetrics().density); }
}
