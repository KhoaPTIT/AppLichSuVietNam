package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.BookmarkEntity;
import com.lichsuvietnam.app.data.database.entities.HistoricalFigureEntity;
import com.lichsuvietnam.app.ui.adapters.VideoAdapter;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.List;
import java.util.concurrent.Executors;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Màn hình thông tin chi tiết sự kiện.
 * Activity hiển thị nội dung sự kiện, nhân vật liên quan, ảnh/video minh họa,
 * đồng thời hỗ trợ lưu yêu thích, mở thảo luận và tìm tài liệu bên ngoài.
 */
public class EventDetailActivity extends AppCompatActivity {
    // Room database dùng để lấy sự kiện, bookmark, nhân vật và video liên quan.
    private AppDatabase db;
    // Quản lý phiên đăng nhập để bật/tắt chức năng lưu yêu thích.
    private SessionManager session;
    private long eventId;
    private boolean isSaved = false;
    private String eventTitle = "";

    /**
     * Khởi tạo màn hình chi tiết, nhận event_id từ Intent và tải dữ liệu từ Room.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);
        eventId = getIntent().getLongExtra("event_id", 1);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        db.historyEventDao().getEventById(eventId).observe(this, event -> {
            if (event == null) return;
            eventTitle = event.title;

            ((TextView) findViewById(R.id.tvTitle)).setText(event.title);
            ((TextView) findViewById(R.id.tvSubtitle)).setText(event.year + " SCN \u2022 " + (event.location != null ? event.location : ""));
            ((TextView) findViewById(R.id.tvContent)).setText(event.fullContent != null ? event.fullContent : event.description);
            ((TextView) findViewById(R.id.tvYearTag)).setText("N\u0103m " + event.year);

            // ImageUtils dùng Glide để tải ảnh chính của sự kiện từ URL/drawable.
            if (event.imageUrl != null) ImageUtils.load(this, event.imageUrl, (ImageView) findViewById(R.id.ivHero));

            // Tạo danh sách nhân vật liên quan; mỗi nhân vật có thể bấm để mở chi tiết.
            if (event.keyFigures != null) {
                LinearLayout figuresContainer = findViewById(R.id.figuresContainer);
                figuresContainer.removeAllViews();
                for (String name : event.keyFigures.split(",")) {
                    name = name.trim();
                    if (name.isEmpty()) continue;
                    final String figureName = name;

                    LinearLayout figLayout = new LinearLayout(this);
                    figLayout.setOrientation(LinearLayout.VERTICAL);
                    figLayout.setGravity(Gravity.CENTER);
                    figLayout.setPadding(0, 0, dp(24), 0);

                    FrameLayout circle = new FrameLayout(this);
                    circle.setBackgroundResource(R.drawable.bg_circle_red);
                    circle.setLayoutParams(new LinearLayout.LayoutParams(dp(56), dp(56)));
                    TextView tvInit = new TextView(this);
                    tvInit.setText(String.valueOf(name.charAt(0)));
                    tvInit.setTextColor(getResources().getColor(R.color.red_primary, null));
                    tvInit.setTextSize(18);
                    tvInit.setTypeface(null, android.graphics.Typeface.BOLD);
                    FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    fp.gravity = Gravity.CENTER;
                    tvInit.setLayoutParams(fp);
                    circle.addView(tvInit);
                    figLayout.addView(circle);

                    TextView tvName = new TextView(this);
                    tvName.setText(name);
                    tvName.setTextSize(11);
                    tvName.setTextColor(getResources().getColor(R.color.text_primary, null));
                    tvName.setTypeface(null, android.graphics.Typeface.BOLD);
                    tvName.setGravity(Gravity.CENTER);
                    tvName.setPadding(0, dp(6), 0, 0);
                    tvName.setMaxLines(2);
                    tvName.setLayoutParams(new LinearLayout.LayoutParams(dp(72), LinearLayout.LayoutParams.WRAP_CONTENT));
                    figLayout.addView(tvName);

                    figLayout.setOnClickListener(v -> {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<HistoricalFigureEntity> found = db.figureDao().searchFiguresSync(figureName);
                            runOnUiThread(() -> {
                                if (!found.isEmpty()) {
                                    startActivity(new Intent(this, FigureDetailActivity.class).putExtra("figure_id", found.get(0).id));
                                } else {
                                    Toast.makeText(this, "Ch\u01b0a c\u00f3 th\u00f4ng tin v\u1ec1 " + figureName, Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    });
                    figuresContainer.addView(figLayout);
                }
            }
        });

        // Chức năng lưu/bỏ lưu sự kiện yêu thích bằng BookmarkDao của Room.
        Button btnSave = findViewById(R.id.btnSave);
        long userId = session.getUserId();
        if (session.isLoggedIn() && userId > 0) {
            Executors.newSingleThreadExecutor().execute(() -> {
                isSaved = db.bookmarkDao().isBookmarked(userId, eventId);
                runOnUiThread(() -> btnSave.setText(isSaved ? "\u0110\u00e3 l\u01b0u \u2605" : "L\u01b0u"));
            });
            btnSave.setOnClickListener(v -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    if (isSaved) { db.bookmarkDao().delete(userId, eventId); isSaved = false; }
                    else { db.bookmarkDao().insert(new BookmarkEntity(userId, eventId, "Y\u00eau th\u00edch")); isSaved = true; }
                    runOnUiThread(() -> {
                        btnSave.setText(isSaved ? "\u0110\u00e3 l\u01b0u \u2605" : "L\u01b0u");
                        Toast.makeText(this, isSaved ? "\u0110\u00e3 l\u01b0u v\u00e0o b\u1ed9 s\u01b0u t\u1eadp" : "\u0110\u00e3 b\u1ecf l\u01b0u", Toast.LENGTH_SHORT).show();
                    });
                });
            });
        } else {
            btnSave.setText("\u0110\u0103ng nh\u1eadp \u0111\u1ec3 l\u01b0u");
            btnSave.setOnClickListener(v -> Toast.makeText(this, "\u0110\u0103ng nh\u1eadp \u0111\u1ec3 s\u1eed d\u1ee5ng", Toast.LENGTH_SHORT).show());
        }

        findViewById(R.id.btnDiscuss).setOnClickListener(v ->
            startActivity(new Intent(this, ThreadActivity.class).putExtra("post_id", 1L)));

        // Nút hình ảnh mở thư viện ảnh lịch sử.
        findViewById(R.id.btnMedia).setOnClickListener(v ->
            startActivity(new Intent(this, ImageGalleryActivity.class)));

        // Nút tài liệu mở trình duyệt để tìm tài liệu lịch sử bên ngoài.
        View btnDoc = findViewById(R.id.btnDoc);
        if (btnDoc != null) {
            btnDoc.setOnClickListener(v -> openGoogleSearch());
        }

        loadVideos();
    }

    /**
     * Mở Google Search bằng Intent ACTION_VIEW.
     * Đây là API Android gọi ứng dụng trình duyệt bên ngoài để tìm tài liệu liên quan.
     */
    private void openGoogleSearch() {
        String query = eventTitle + " t\u00e0i li\u1ec7u l\u1ecbch s\u1eed Vi\u1ec7t Nam";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)));
        startActivity(intent);
    }

    /**
     * Tải video liên quan đến sự kiện từ Room và hiển thị bằng VideoAdapter.
     * Khi chọn video, Intent mở YouTube app; nếu thất bại thì mở dự phòng bằng trình duyệt.
     */
    private void loadVideos() {
        db.videoDao().getVideosByEvent(eventId).observe(this, videos -> {
            if (videos != null && !videos.isEmpty()) {
                findViewById(R.id.tvVideoHeader).setVisibility(View.VISIBLE);
                RecyclerView rvVideos = findViewById(R.id.rvVideos);
                rvVideos.setVisibility(View.VISIBLE);
                rvVideos.setLayoutManager(new LinearLayoutManager(this));
                rvVideos.setAdapter(new VideoAdapter(videos, video -> {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.youtubeId))); }
                    catch (Exception e) { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + video.youtubeId))); }
                }));
            }
        });
    }

    /** Đổi đơn vị dp sang pixel để tạo view động đúng kích thước trên từng màn hình. */
    private int dp(int dp) { return (int)(dp * getResources().getDisplayMetrics().density); }
}
