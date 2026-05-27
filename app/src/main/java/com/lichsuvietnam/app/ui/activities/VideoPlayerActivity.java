package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.adapters.VideoAdapter;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.VideoEntity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Màn hình phát video YouTube trong ứng dụng.
 * Activity dùng thư viện ngoài android-youtube-player để nhúng YouTube Player,
 * đồng thời hiển thị các video liên quan bên dưới.
 */
public class VideoPlayerActivity extends AppCompatActivity {
    // View của thư viện ngoài android-youtube-player.
    private YouTubePlayerView youtubePlayerView;

    /**
     * Nhận thông tin video từ Intent, khởi tạo YouTube Player và tải video liên quan.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        String youtubeId = getIntent().getStringExtra("youtube_id");
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        long videoId = getIntent().getLongExtra("video_id", -1);

        ((TextView) findViewById(R.id.txtVideoTitle)).setText(title != null ? title : "");
        ((TextView) findViewById(R.id.txtVideoDesc)).setText(desc != null ? desc : "");

        findViewById(R.id.btnBackVideo).setOnClickListener(v -> finish());

        // YouTubePlayerView là API ngoài nên cần đăng ký theo lifecycle của Activity.
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        getLifecycle().addObserver(youtubePlayerView);

        // Listener của thư viện YouTube Player sẽ được gọi khi player sẵn sàng.
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer player) {
                if (youtubeId != null) {
                    player.loadVideo(youtubeId, 0);
                }
            }
        });

        // Danh sách video liên quan, loại trừ video đang xem.
        RecyclerView rvRelated = findViewById(R.id.rvRelatedVideos);
        rvRelated.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<VideoEntity> allVideos = db.videoDao().getAllVideosSync();
            List<VideoEntity> related = new ArrayList<>();
            for (VideoEntity v : allVideos) {
                if (!v.youtubeId.equals(youtubeId)) {
                    related.add(v);
                }
            }
            runOnUiThread(() -> {
                rvRelated.setAdapter(new VideoAdapter(related, video -> {
                    Intent intent = new Intent(this, VideoPlayerActivity.class);
                    intent.putExtra("youtube_id", video.youtubeId);
                    intent.putExtra("title", video.title);
                    intent.putExtra("description", video.description);
                    intent.putExtra("video_id", video.id);
                    startActivity(intent);
                    finish();
                }));
            });
        });
    }

    /**
     * Giải phóng tài nguyên của YouTubePlayerView để tránh rò rỉ bộ nhớ.
     */
    @Override
    protected void onDestroy() {
        if (youtubePlayerView != null) youtubePlayerView.release();
        super.onDestroy();
    }
}
