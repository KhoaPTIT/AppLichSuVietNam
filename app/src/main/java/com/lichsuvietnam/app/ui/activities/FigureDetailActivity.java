package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoricalFigureEntity;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FigureDetailActivity extends AppCompatActivity {
    private AppDatabase db;
    private long figureId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figure_detail);

        db = AppDatabase.getInstance(this);
        figureId = getIntent().getLongExtra("figure_id", -1);
        if (figureId == -1) { finish(); return; }

        db.figureDao().getFigureById(figureId).observe(this, figure -> {
            if (figure == null) return;
            bindFigure(figure);
        });

        findViewById(R.id.btnBackFigureDetail).setOnClickListener(v -> finish());

        // Quiz button
        findViewById(R.id.btnQuizFigure).setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("figure_id", figureId);
            startActivity(intent);
        });
    }

    private void bindFigure(HistoricalFigureEntity f) {
        // Header - image or initial
        String initial = f.name.substring(0, 1);
        TextView txtInitial = findViewById(R.id.txtDetailInitial);
        txtInitial.setText(initial);
        if (f.imageUrl != null && !f.imageUrl.isEmpty()) {
            txtInitial.setVisibility(View.GONE);
            // Load image into avatar area
            ImageView imgAvatar = new ImageView(this);
            imgAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout avatarFrame = (FrameLayout) txtInitial.getParent();
            avatarFrame.addView(imgAvatar, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            ImageUtils.loadCircle(this, f.imageUrl, imgAvatar);
        }
        ((TextView) findViewById(R.id.txtDetailName)).setText(f.name);
        ((TextView) findViewById(R.id.txtDetailTitle)).setText(f.title != null ? f.title : "");

        // Role tag
        TextView txtRole = findViewById(R.id.txtDetailRole);
        if (f.role != null) {
            txtRole.setText(f.role);
            txtRole.setVisibility(View.VISIBLE);
        } else {
            txtRole.setVisibility(View.GONE);
        }

        // Info chips
        ((TextView) findViewById(R.id.txtDetailLifespan)).setText(f.formatLifeSpan());
        ((TextView) findViewById(R.id.txtDetailDynasty)).setText(f.dynasty != null ? f.dynasty : "");
        ((TextView) findViewById(R.id.txtDetailPeriod)).setText(f.period != null ? f.period : "");

        // QueQuan
        TextView txtQueQuan = findViewById(R.id.txtDetailQueQuan);
        txtQueQuan.setText(f.queQuan != null ? f.queQuan : "Không rõ");

        // Biography
        ((TextView) findViewById(R.id.txtDetailBio)).setText(f.biography != null ? f.biography : "");

        // Achievements
        buildAchievements(f);

        // Timeline milestones
        buildTimeline(f);

        // Favorite button
        setupFavorite(f);

        // Related events
        loadRelatedEvents(f);

        // Related figures
        loadRelatedFigures(f);
    }

    private void buildAchievements(HistoricalFigureEntity f) {
        LinearLayout container = findViewById(R.id.achievementsContainer);
        container.removeAllViews();

        if (f.achievements != null && !f.achievements.isEmpty()) {
            String[] items = f.achievements.split(";");
            for (int i = 0; i < items.length; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.TOP);
                row.setPadding(0, i > 0 ? 16 : 0, 0, 0);

                // Number badge
                FrameLayout badge = new FrameLayout(this);
                badge.setBackgroundResource(R.drawable.bg_circle_red_filled);
                LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(dpToPx(28), dpToPx(28));
                badge.setLayoutParams(bp);

                TextView numTv = new TextView(this);
                numTv.setText(String.valueOf(i + 1));
                numTv.setTextColor(0xFFFFFFFF);
                numTv.setTextSize(12);
                numTv.setTypeface(null, Typeface.BOLD);
                FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                fp.gravity = Gravity.CENTER;
                numTv.setLayoutParams(fp);
                badge.addView(numTv);
                row.addView(badge);

                // Text
                TextView tv = new TextView(this);
                tv.setText(items[i].trim());
                tv.setTextColor(getResources().getColor(R.color.text_secondary, null));
                tv.setTextSize(14);
                tv.setLineSpacing(0, 1.4f);
                LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                tlp.setMarginStart(dpToPx(12));
                tv.setLayoutParams(tlp);
                row.addView(tv);

                container.addView(row);
            }
        }
    }

    private void buildTimeline(HistoricalFigureEntity f) {
        LinearLayout section = findViewById(R.id.timelineSection);
        LinearLayout container = findViewById(R.id.timelineContainer);
        container.removeAllViews();

        if (f.timelineMilestones == null || f.timelineMilestones.isEmpty()) {
            section.setVisibility(View.GONE);
            return;
        }

        section.setVisibility(View.VISIBLE);
        String[] milestones = f.timelineMilestones.split(";");

        for (int i = 0; i < milestones.length; i++) {
            String[] parts = milestones[i].split("\\|", 2);
            if (parts.length < 2) continue;

            String year = parts[0].trim();
            String desc = parts[1].trim();

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.TOP);

            // Timeline indicator (dot + line)
            LinearLayout indicator = new LinearLayout(this);
            indicator.setOrientation(LinearLayout.VERTICAL);
            indicator.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dpToPx(24), LinearLayout.LayoutParams.MATCH_PARENT);
            indicator.setLayoutParams(ip);

            View dot = new View(this);
            dot.setBackgroundResource(R.drawable.bg_milestone_dot);
            LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(dpToPx(12), dpToPx(12));
            dp.topMargin = dpToPx(4);
            dot.setLayoutParams(dp);
            indicator.addView(dot);

            if (i < milestones.length - 1) {
                View line = new View(this);
                line.setBackgroundColor(getResources().getColor(R.color.outline_variant, null));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(2), 0, 1);
                lp.topMargin = dpToPx(4);
                line.setLayoutParams(lp);
                indicator.addView(line);
            }
            row.addView(indicator);

            // Content
            LinearLayout content = new LinearLayout(this);
            content.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            cp.setMarginStart(dpToPx(12));
            cp.bottomMargin = dpToPx(16);
            content.setLayoutParams(cp);

            // Year tag
            TextView yearTv = new TextView(this);
            yearTv.setText(year);
            yearTv.setBackgroundResource(R.drawable.bg_year_tag);
            yearTv.setPadding(dpToPx(8), dpToPx(3), dpToPx(8), dpToPx(3));
            yearTv.setTextColor(0xFFFFFFFF);
            yearTv.setTextSize(11);
            yearTv.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams yp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            yearTv.setLayoutParams(yp);
            content.addView(yearTv);

            // Description
            TextView descTv = new TextView(this);
            descTv.setText(desc);
            descTv.setTextColor(getResources().getColor(R.color.text_secondary, null));
            descTv.setTextSize(14);
            descTv.setLineSpacing(0, 1.3f);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dlp.topMargin = dpToPx(6);
            descTv.setLayoutParams(dlp);
            content.addView(descTv);

            row.addView(content);
            container.addView(row);
        }
    }

    private void setupFavorite(HistoricalFigureEntity f) {
        FrameLayout btnFavorite = findViewById(R.id.btnFavorite);
        TextView txtIcon = findViewById(R.id.txtFavoriteIcon);
        txtIcon.setText(f.isFavorite ? "♥" : "♡");

        btnFavorite.setOnClickListener(v -> {
            f.isFavorite = !f.isFavorite;
            txtIcon.setText(f.isFavorite ? "♥" : "♡");
            Executors.newSingleThreadExecutor().execute(() -> db.figureDao().update(f));
            Toast.makeText(this,
                f.isFavorite ? "Đã thêm vào yêu thích" : "Đã bỏ yêu thích",
                Toast.LENGTH_SHORT).show();
        });
    }

    private void loadRelatedEvents(HistoricalFigureEntity f) {
        if (f.relatedEventIds == null || f.relatedEventIds.isEmpty()) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            List<HistoryEventEntity> events = new ArrayList<>();
            for (String idStr : f.relatedEventIds.split(",")) {
                try {
                    long id = Long.parseLong(idStr.trim());
                    HistoryEventEntity event = db.historyEventDao().getEventByIdSync(id);
                    if (event != null) events.add(event);
                } catch (NumberFormatException ignored) {}
            }

            runOnUiThread(() -> {
                if (events.isEmpty()) return;

                LinearLayout section = findViewById(R.id.relatedEventsSection);
                section.setVisibility(View.VISIBLE);
                LinearLayout container = findViewById(R.id.relatedEventsContainer);
                container.removeAllViews();

                for (HistoryEventEntity event : events) {
                    LinearLayout eventRow = new LinearLayout(this);
                    eventRow.setOrientation(LinearLayout.HORIZONTAL);
                    eventRow.setGravity(Gravity.CENTER_VERTICAL);
                    eventRow.setBackgroundResource(R.drawable.bg_info_chip);
                    eventRow.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
                    LinearLayout.LayoutParams elp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    elp.bottomMargin = dpToPx(8);
                    eventRow.setLayoutParams(elp);

                    // Year badge
                    TextView yearTv = new TextView(this);
                    yearTv.setText(event.year);
                    yearTv.setBackgroundResource(R.drawable.bg_year_tag);
                    yearTv.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
                    yearTv.setTextColor(0xFFFFFFFF);
                    yearTv.setTextSize(11);
                    yearTv.setTypeface(null, Typeface.BOLD);
                    eventRow.addView(yearTv);

                    // Event info
                    LinearLayout info = new LinearLayout(this);
                    info.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    ilp.setMarginStart(dpToPx(12));
                    info.setLayoutParams(ilp);

                    TextView titleTv = new TextView(this);
                    titleTv.setText(event.title);
                    titleTv.setTextColor(getResources().getColor(R.color.text_primary, null));
                    titleTv.setTextSize(14);
                    titleTv.setTypeface(null, Typeface.BOLD);
                    info.addView(titleTv);

                    TextView descTv = new TextView(this);
                    descTv.setText(event.description);
                    descTv.setTextColor(getResources().getColor(R.color.text_tertiary, null));
                    descTv.setTextSize(12);
                    descTv.setMaxLines(1);
                    info.addView(descTv);

                    eventRow.addView(info);

                    // Arrow
                    TextView arrow = new TextView(this);
                    arrow.setText("›");
                    arrow.setTextColor(getResources().getColor(R.color.text_tertiary, null));
                    arrow.setTextSize(18);
                    eventRow.addView(arrow);

                    eventRow.setOnClickListener(v -> {
                        Intent intent = new Intent(this, EventDetailActivity.class);
                        intent.putExtra("event_id", event.id);
                        startActivity(intent);
                    });

                    container.addView(eventRow);
                }
            });
        });
    }

    private void loadRelatedFigures(HistoricalFigureEntity f) {
        if (f.relatedFigureIds == null || f.relatedFigureIds.isEmpty()) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Long> ids = new ArrayList<>();
            for (String idStr : f.relatedFigureIds.split(",")) {
                try { ids.add(Long.parseLong(idStr.trim())); } catch (NumberFormatException ignored) {}
            }
            List<HistoricalFigureEntity> related = db.figureDao().getFiguresByIdsSync(ids);

            // Also add same-period figures
            if (f.period != null) {
                List<HistoricalFigureEntity> samePeriod = db.figureDao().getFiguresByPeriodExcludingSync(f.period, f.id);
                for (HistoricalFigureEntity sp : samePeriod) {
                    boolean exists = false;
                    for (HistoricalFigureEntity r : related) {
                        if (r.id == sp.id) { exists = true; break; }
                    }
                    if (!exists && related.size() < 5) related.add(sp);
                }
            }

            runOnUiThread(() -> {
                if (related.isEmpty()) return;

                LinearLayout section = findViewById(R.id.relatedFiguresSection);
                section.setVisibility(View.VISIBLE);
                LinearLayout container = findViewById(R.id.relatedFiguresContainer);
                container.removeAllViews();

                for (HistoricalFigureEntity rf : related) {
                    LinearLayout figLayout = new LinearLayout(this);
                    figLayout.setOrientation(LinearLayout.VERTICAL);
                    figLayout.setGravity(Gravity.CENTER);
                    figLayout.setPadding(0, 0, dpToPx(20), 0);

                    // Avatar circle
                    FrameLayout circle = new FrameLayout(this);
                    circle.setBackgroundResource(R.drawable.bg_circle_red);
                    LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(dpToPx(56), dpToPx(56));
                    circle.setLayoutParams(cp);

                    TextView tvInit = new TextView(this);
                    tvInit.setText(rf.name.substring(0, 1));
                    tvInit.setTextColor(getResources().getColor(R.color.red_primary, null));
                    tvInit.setTextSize(20);
                    tvInit.setTypeface(null, Typeface.BOLD);
                    FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    fp.gravity = Gravity.CENTER;
                    tvInit.setLayoutParams(fp);
                    circle.addView(tvInit);
                    figLayout.addView(circle);

                    // Name
                    TextView tvName = new TextView(this);
                    tvName.setText(rf.name);
                    tvName.setTextSize(12);
                    tvName.setTextColor(getResources().getColor(R.color.text_primary, null));
                    tvName.setTypeface(null, Typeface.BOLD);
                    tvName.setGravity(Gravity.CENTER);
                    tvName.setPadding(0, dpToPx(6), 0, 0);
                    tvName.setMaxLines(2);
                    LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(dpToPx(80), LinearLayout.LayoutParams.WRAP_CONTENT);
                    tvName.setLayoutParams(nlp);
                    figLayout.addView(tvName);

                    // Title
                    TextView tvTitle = new TextView(this);
                    tvTitle.setText(rf.title != null ? rf.title : "");
                    tvTitle.setTextSize(10);
                    tvTitle.setTextColor(getResources().getColor(R.color.text_tertiary, null));
                    tvTitle.setGravity(Gravity.CENTER);
                    tvTitle.setMaxLines(1);
                    tvTitle.setLayoutParams(nlp);
                    figLayout.addView(tvTitle);

                    figLayout.setOnClickListener(v -> {
                        Intent intent = new Intent(this, FigureDetailActivity.class);
                        intent.putExtra("figure_id", rf.id);
                        startActivity(intent);
                        finish();
                    });

                    container.addView(figLayout);
                }
            });
        });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
