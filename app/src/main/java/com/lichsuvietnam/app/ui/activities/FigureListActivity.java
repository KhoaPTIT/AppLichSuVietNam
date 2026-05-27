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
import com.lichsuvietnam.app.ui.adapters.FigureAdapter;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoricalFigureEntity;
import java.util.List;
import java.util.concurrent.Executors;

public class FigureListActivity extends AppCompatActivity {
    private AppDatabase db;
    private FigureAdapter adapter;
    private String selectedPeriod = "Tất cả";
    private String selectedRole = "Tất cả";
    private LinearLayout chipContainer, chipContainerRoles;
    private EditText edtSearch;
    private View emptyState;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figure_list);

        db = AppDatabase.getInstance(this);
        emptyState = findViewById(R.id.emptyState);
        rv = findViewById(R.id.rvFigures);

        findViewById(R.id.btnBackFigures).setOnClickListener(v -> finish());

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FigureAdapter(null, figure -> {
            Intent intent = new Intent(this, FigureDetailActivity.class);
            intent.putExtra("figure_id", figure.id);
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // Count
        Executors.newSingleThreadExecutor().execute(() -> {
            int count = db.figureDao().getCount();
            runOnUiThread(() -> {
                TextView txtCount = findViewById(R.id.txtFigureCount);
                txtCount.setText(String.valueOf(count));
            });
        });

        // Period chips
        chipContainer = findViewById(R.id.chipContainerFigures);
        buildPeriodChips();

        // Role chips
        chipContainerRoles = findViewById(R.id.chipContainerRoles);
        buildRoleChips();

        // Search
        edtSearch = findViewById(R.id.edtSearchFigures);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadFigures();
                } else {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<HistoricalFigureEntity> results = db.figureDao().searchFiguresSync(query);
                        runOnUiThread(() -> {
                            adapter.updateData(results);
                            emptyState.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
                            rv.setVisibility(results.isEmpty() ? View.GONE : View.VISIBLE);
                        });
                    });
                }
            }
        });

        // Initial load
        loadFigures();
    }

    private void loadFigures() {
        if (selectedPeriod.equals("Tất cả") && selectedRole.equals("Tất cả")) {
            db.figureDao().getAllFigures().observe(this, this::showFigures);
        } else if (!selectedPeriod.equals("Tất cả") && selectedRole.equals("Tất cả")) {
            db.figureDao().getFiguresByPeriod(selectedPeriod).observe(this, this::showFigures);
        } else if (selectedPeriod.equals("Tất cả") && !selectedRole.equals("Tất cả")) {
            db.figureDao().getFiguresByRole(selectedRole).observe(this, this::showFigures);
        } else {
            db.figureDao().getFiguresByPeriodAndRole(selectedPeriod, selectedRole).observe(this, this::showFigures);
        }
    }

    private void showFigures(List<HistoricalFigureEntity> figures) {
        adapter.updateData(figures);
        emptyState.setVisibility(figures == null || figures.isEmpty() ? View.VISIBLE : View.GONE);
        rv.setVisibility(figures == null || figures.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void buildPeriodChips() {
        chipContainer.removeAllViews();
        String[] periods = {"Tất cả", "Bắc thuộc", "Phong kiến", "Cận đại", "Hiện đại"};
        for (String period : periods) {
            TextView chip = createChip(period, period.equals(selectedPeriod));
            chip.setOnClickListener(v -> {
                selectedPeriod = period;
                buildPeriodChips();
                loadFigures();
            });
            chipContainer.addView(chip);
        }
    }

    private void buildRoleChips() {
        chipContainerRoles.removeAllViews();
        String[] roles = {"Tất cả", "Vua chúa", "Tướng lĩnh", "Nhà cách mạng", "Nhà văn hóa", "Anh hùng dân tộc"};
        for (String role : roles) {
            TextView chip = createChip(role, role.equals(selectedRole));
            chip.setOnClickListener(v -> {
                selectedRole = role;
                buildRoleChips();
                loadFigures();
            });
            chipContainerRoles.addView(chip);
        }
    }

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
