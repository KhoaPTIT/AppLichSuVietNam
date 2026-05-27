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
import com.lichsuvietnam.app.data.database.entities.HistoricalFigureEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminFiguresActivity extends AppCompatActivity {
    private AppDatabase db;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!new SessionManager(this).isAdmin()) { finish(); return; }

        setContentView(R.layout.activity_admin_figures);
        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnBackAdmin).setOnClickListener(v -> finish());
        rv = findViewById(R.id.rvFigures);
        rv.setLayoutManager(new LinearLayoutManager(this));

        EditText etSearch = findViewById(R.id.etSearchFigures);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String q = s.toString().trim();
                if (q.length() >= 2) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<HistoricalFigureEntity> r = db.figureDao().searchFiguresSync(q);
                        runOnUiThread(() -> showFigures(r));
                    });
                } else loadFigures();
            }
        });

        findViewById(R.id.btnAddFigure).setOnClickListener(v -> showEditDialog(null));
        loadFigures();
    }

    private void loadFigures() {
        db.figureDao().getAllFigures().observe(this, figs -> { if (figs != null) showFigures(figs); });
    }

    private void showFigures(List<HistoricalFigureEntity> figs) {
        rv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LinearLayout row = new LinearLayout(AdminFiguresActivity.this);
                row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL);
                row.setBackgroundResource(R.drawable.bg_rounded_card); row.setPadding(dp(14), dp(12), dp(14), dp(12));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = dp(8); lp.leftMargin = dp(16); lp.rightMargin = dp(16); row.setLayoutParams(lp);

                LinearLayout info = new LinearLayout(AdminFiguresActivity.this); info.setOrientation(LinearLayout.VERTICAL);
                info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                TextView tvName = new TextView(AdminFiguresActivity.this); tvName.setId(android.R.id.text1); tvName.setTextSize(13); tvName.setTextColor(getColor(R.color.text_primary)); tvName.setTypeface(null, android.graphics.Typeface.BOLD);
                TextView tvSub = new TextView(AdminFiguresActivity.this); tvSub.setId(android.R.id.text2); tvSub.setTextSize(11); tvSub.setTextColor(getColor(R.color.text_tertiary));
                info.addView(tvName); info.addView(tvSub); row.addView(info);

                TextView btnEdit = new TextView(AdminFiguresActivity.this); btnEdit.setId(android.R.id.button1); btnEdit.setText("✏️"); btnEdit.setTextSize(18); btnEdit.setPadding(dp(8), 0, dp(8), 0);
                TextView btnDel = new TextView(AdminFiguresActivity.this); btnDel.setId(android.R.id.button2); btnDel.setText("🗑️"); btnDel.setTextSize(18); btnDel.setPadding(dp(8), 0, dp(8), 0);
                row.addView(btnEdit); row.addView(btnDel);
                return new RecyclerView.ViewHolder(row) {};
            }
            @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
                HistoricalFigureEntity f = figs.get(pos);
                ((TextView) h.itemView.findViewById(android.R.id.text1)).setText(f.name);
                ((TextView) h.itemView.findViewById(android.R.id.text2)).setText((f.title != null ? f.title : "") + " • " + (f.period != null ? f.period : "") + " • " + f.formatLifeSpan());
                h.itemView.findViewById(android.R.id.button1).setOnClickListener(v -> showEditDialog(f));
                h.itemView.findViewById(android.R.id.button2).setOnClickListener(v ->
                    new AlertDialog.Builder(AdminFiguresActivity.this).setMessage("Xóa \"" + f.name + "\"?").setPositiveButton("Xóa", (d, w) -> {
                        Executors.newSingleThreadExecutor().execute(() -> { db.figureDao().delete(f); runOnUiThread(() -> { Toast.makeText(AdminFiguresActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show(); loadFigures(); }); });
                    }).setNegativeButton("Hủy", null).show());
            }
            @Override public int getItemCount() { return figs.size(); }
        });
    }

    private void showEditDialog(HistoricalFigureEntity existing) {
        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this); ll.setOrientation(LinearLayout.VERTICAL); ll.setPadding(dp(20), dp(10), dp(20), dp(10));
        EditText etName = addField(ll, "Tên", existing != null ? existing.name : "");
        EditText etTitle = addField(ll, "Chức danh", existing != null && existing.title != null ? existing.title : "");
        EditText etBirth = addField(ll, "Năm sinh", existing != null && existing.birthYear != null ? existing.birthYear : "");
        EditText etDeath = addField(ll, "Năm mất", existing != null && existing.deathYear != null ? existing.deathYear : "");
        EditText etDynasty = addField(ll, "Triều đại", existing != null && existing.dynasty != null ? existing.dynasty : "");
        EditText etPeriod = addField(ll, "Thời kỳ", existing != null && existing.period != null ? existing.period : "");
        EditText etRole = addField(ll, "Vai trò", existing != null && existing.role != null ? existing.role : "");
        EditText etBio = addField(ll, "Tiểu sử", existing != null && existing.biography != null ? existing.biography : "");
        EditText etShort = addField(ll, "Mô tả ngắn", existing != null && existing.shortDesc != null ? existing.shortDesc : "");
        sv.addView(ll);

        new AlertDialog.Builder(this).setTitle(existing == null ? "Thêm nhân vật" : "Sửa nhân vật").setView(sv)
            .setPositiveButton("Lưu", (d, w) -> {
                String name = etName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) { Toast.makeText(this, "Nhập tên nhân vật", Toast.LENGTH_SHORT).show(); return; }
                Executors.newSingleThreadExecutor().execute(() -> {
                    HistoricalFigureEntity f = existing != null ? existing : new HistoricalFigureEntity(name);
                    f.name = name; f.title = etTitle.getText().toString().trim();
                    f.birthYear = etBirth.getText().toString().trim(); f.deathYear = etDeath.getText().toString().trim();
                    f.dynasty = etDynasty.getText().toString().trim(); f.period = etPeriod.getText().toString().trim();
                    f.role = etRole.getText().toString().trim(); f.biography = etBio.getText().toString().trim();
                    f.shortDesc = etShort.getText().toString().trim();
                    if (existing != null) db.figureDao().update(f); else db.figureDao().insert(f);
                    runOnUiThread(() -> { Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show(); loadFigures(); });
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
