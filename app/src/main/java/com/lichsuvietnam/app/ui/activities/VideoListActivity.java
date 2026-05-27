package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.VideoEntity;
import java.util.List;

/**
 * Màn hình Video & tài liệu đa phương tiện.
 * Activity hiển thị toàn bộ video lịch sử trong Room và mở YouTube hoặc
 * trang tìm kiếm YouTube khi người dùng chọn một video.
 */
public class VideoListActivity extends AppCompatActivity {
    /**
     * Khởi tạo danh sách video, quan sát dữ liệu từ Room và tạo item video bằng code.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvAllVideos);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // VideoDao trả LiveData để danh sách tự cập nhật khi dữ liệu video thay đổi.
        AppDatabase.getInstance(this).videoDao().getAllVideos().observe(this, videos -> {
            rv.setAdapter(new RecyclerView.Adapter<VideoVH>() {
                @Override public VideoVH onCreateViewHolder(ViewGroup parent, int viewType) {
                    LinearLayout card = new LinearLayout(VideoListActivity.this);
                    card.setOrientation(LinearLayout.HORIZONTAL);
                    card.setBackgroundResource(R.drawable.bg_rounded_card);
                    card.setPadding(dp(12), dp(12), dp(12), dp(12));
                    LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    clp.bottomMargin = dp(10);
                    card.setLayoutParams(clp);
                    card.setGravity(Gravity.CENTER_VERTICAL);

                    // Khung thumbnail của video.
                    FrameLayout thumbFrame = new FrameLayout(VideoListActivity.this);
                    LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(dp(130), dp(80));
                    thumbFrame.setLayoutParams(fp);
                    ImageView thumb = new ImageView(VideoListActivity.this);
                    thumb.setId(android.R.id.icon);
                    thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    thumb.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    thumbFrame.addView(thumb);
                    // Icon play phủ trên thumbnail.
                    TextView play = new TextView(VideoListActivity.this);
                    play.setText("▶");
                    play.setTextSize(24);
                    play.setTextColor(0xFFFFFFFF);
                    FrameLayout.LayoutParams pp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    pp.gravity = Gravity.CENTER;
                    play.setLayoutParams(pp);
                    thumbFrame.addView(play);
                    // Nhãn thời lượng video ở góc thumbnail.
                    TextView dur = new TextView(VideoListActivity.this);
                    dur.setId(android.R.id.summary);
                    dur.setTextSize(10);
                    dur.setTextColor(0xFFFFFFFF);
                    dur.setBackgroundColor(0xAA000000);
                    dur.setPadding(dp(6), dp(2), dp(6), dp(2));
                    FrameLayout.LayoutParams dp2 = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    dp2.gravity = Gravity.BOTTOM | Gravity.END;
                    dp2.setMargins(0, 0, dp(4), dp(4));
                    dur.setLayoutParams(dp2);
                    thumbFrame.addView(dur);
                    card.addView(thumbFrame);

                    // Khu vực hiển thị tiêu đề, mô tả và link YouTube.
                    LinearLayout info = new LinearLayout(VideoListActivity.this);
                    info.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    ilp.setMarginStart(dp(12));
                    info.setLayoutParams(ilp);
                    TextView title = new TextView(VideoListActivity.this);
                    title.setId(android.R.id.text1);
                    title.setTextSize(14);
                    title.setMaxLines(2);
                    info.addView(title);
                    TextView desc = new TextView(VideoListActivity.this);
                    desc.setId(android.R.id.text2);
                    desc.setTextSize(12);
                    desc.setPadding(0, dp(4), 0, 0);
                    info.addView(desc);
                    // Dòng link mở YouTube.
                    TextView ytLink = new TextView(VideoListActivity.this);
                    ytLink.setId(android.R.id.message);
                    ytLink.setTextSize(11);
                    ytLink.setPadding(0, dp(6), 0, 0);
                    ytLink.setTextColor(getResources().getColor(R.color.blue_accent, null));
                    ytLink.setText("▶ Xem trên YouTube");
                    info.addView(ytLink);
                    card.addView(info);
                    return new VideoVH(card);
                }

                @Override public void onBindViewHolder(VideoVH h, int pos) {
                    VideoEntity v = videos.get(pos);
                    // ImageUtils dùng Glide để tải thumbnail từ YouTube hoặc drawable mặc định.
                    ImageUtils.load(VideoListActivity.this, v.getThumbnailUrl(), h.thumb);
                    h.title.setText(v.title);
                    h.title.setTextColor(getResources().getColor(R.color.text_primary, null));
                    h.desc.setText((v.source != null ? v.source : "") + " · " + (v.description != null ? v.description : ""));
                    h.desc.setTextColor(getResources().getColor(R.color.text_tertiary, null));
                    h.duration.setText(v.duration != null ? v.duration : "");
                    // Mở YouTube khi bấm vào card hoặc dòng link.
                    h.itemView.setOnClickListener(view -> openYouTube(v.youtubeId));
                    h.ytLink.setOnClickListener(view -> openYouTube(v.youtubeId));
                }

                @Override public int getItemCount() { return videos.size(); }
            });
        });
    }

    /**
     * Mở video bằng YouTube app hoặc trình duyệt.
     * Nếu youtubeId có tiền tố "search:" thì mở trang kết quả tìm kiếm YouTube.
     */
    private void openYouTube(String youtubeId) {
        // Nếu youtubeId bắt đầu bằng "search:" thì tạo URL tìm kiếm.
        if (youtubeId != null && youtubeId.startsWith("search:")) {
            String query = youtubeId.substring(7);
            try {
                query = java.net.URLEncoder.encode(query, "UTF-8");
            } catch (Exception ignored) {}
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/results?search_query=" + query));
            startActivity(webIntent);
        } else {
            // Ưu tiên mở YouTube app, nếu lỗi thì mở dự phòng bằng trình duyệt.
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeId));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtubeId));
            try {
                startActivity(appIntent);
            } catch (Exception e) {
                startActivity(webIntent);
            }
        }
    }

    /** ViewHolder cho một dòng video được tạo bằng code. */
    static class VideoVH extends RecyclerView.ViewHolder {
        ImageView thumb; TextView title, desc, duration, ytLink;
        VideoVH(View v) {
            super(v);
            thumb = v.findViewById(android.R.id.icon);
            title = v.findViewById(android.R.id.text1);
            desc = v.findViewById(android.R.id.text2);
            duration = v.findViewById(android.R.id.summary);
            ytLink = v.findViewById(android.R.id.message);
        }
    }

    /** Đổi dp sang pixel khi dựng layout video động. */
    private int dp(int dp) { return (int)(dp * getResources().getDisplayMetrics().density); }
}
