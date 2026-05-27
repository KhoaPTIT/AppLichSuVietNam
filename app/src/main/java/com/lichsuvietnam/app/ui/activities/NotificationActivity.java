package com.lichsuvietnam.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.NotificationEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import com.lichsuvietnam.app.utils.TimeUtils;
import java.util.concurrent.Executors;

public class NotificationActivity extends AppCompatActivity {
    private AppDatabase db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) { finish(); return; }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));
        View emptyState = findViewById(R.id.emptyState);

        long userId = session.getUserId();

        // Mark all as read
        Executors.newSingleThreadExecutor().execute(() -> db.notificationDao().markAllAsRead(userId));

        db.notificationDao().getNotifications(userId).observe(this, notifs -> {
            if (notifs == null || notifs.isEmpty()) {
                rv.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                return;
            }
            rv.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            rv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    LinearLayout row = new LinearLayout(NotificationActivity.this);
                    row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL);
                    row.setBackgroundResource(R.drawable.bg_rounded_card);
                    row.setPadding(dp(14), dp(12), dp(14), dp(12));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.bottomMargin = dp(8); lp.leftMargin = dp(16); lp.rightMargin = dp(16); row.setLayoutParams(lp);

                    FrameLayout circle = new FrameLayout(NotificationActivity.this);
                    circle.setBackgroundResource(R.drawable.bg_circle_red);
                    circle.setLayoutParams(new LinearLayout.LayoutParams(dp(40), dp(40)));
                    TextView tvInit = new TextView(NotificationActivity.this); tvInit.setId(android.R.id.icon);
                    tvInit.setTextSize(16); tvInit.setTextColor(getColor(R.color.red_primary)); tvInit.setTypeface(null, android.graphics.Typeface.BOLD);
                    FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    fp.gravity = Gravity.CENTER; tvInit.setLayoutParams(fp);
                    circle.addView(tvInit); row.addView(circle);

                    LinearLayout info = new LinearLayout(NotificationActivity.this);
                    info.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    ilp.setMarginStart(dp(12)); info.setLayoutParams(ilp);
                    TextView tvMsg = new TextView(NotificationActivity.this); tvMsg.setId(android.R.id.text1); tvMsg.setTextSize(13); tvMsg.setTextColor(getColor(R.color.text_primary));
                    TextView tvTime = new TextView(NotificationActivity.this); tvTime.setId(android.R.id.text2); tvTime.setTextSize(11); tvTime.setTextColor(getColor(R.color.text_tertiary));
                    info.addView(tvMsg); info.addView(tvTime); row.addView(info);
                    return new RecyclerView.ViewHolder(row) {};
                }
                @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
                    NotificationEntity n = notifs.get(pos);
                    ((TextView) h.itemView.findViewById(android.R.id.icon)).setText(n.fromUserName.isEmpty() ? "?" : String.valueOf(n.fromUserName.charAt(0)));
                    ((TextView) h.itemView.findViewById(android.R.id.text1)).setText(n.message);
                    ((TextView) h.itemView.findViewById(android.R.id.text2)).setText(TimeUtils.getRelativeTime(n.createdAt));
                    h.itemView.setOnClickListener(v -> {
                        if (n.postId > 0) {
                            Intent intent = new Intent(NotificationActivity.this, ThreadActivity.class);
                            intent.putExtra("post_id", n.postId);
                            startActivity(intent);
                        }
                    });
                }
                @Override public int getItemCount() { return notifs.size(); }
            });
        });
    }

    private int dp(int dp) { return (int)(dp * getResources().getDisplayMetrics().density); }
}
