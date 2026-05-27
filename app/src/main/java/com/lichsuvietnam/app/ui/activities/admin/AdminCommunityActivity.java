package com.lichsuvietnam.app.ui.activities.admin;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.CommentEntity;
import com.lichsuvietnam.app.data.database.entities.PostEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import com.lichsuvietnam.app.utils.TimeUtils;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminCommunityActivity extends AppCompatActivity {
    private AppDatabase db;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!new SessionManager(this).isAdmin()) { finish(); return; }

        setContentView(R.layout.activity_admin_community);
        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnBackAdmin).setOnClickListener(v -> finish());
        findViewById(R.id.btnSyncCounts).setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                db.communityDao().syncAllCommentCounts();
                runOnUiThread(() -> { Toast.makeText(this, "Đã đồng bộ số comment", Toast.LENGTH_SHORT).show(); loadPosts(); });
            });
        });

        rv = findViewById(R.id.rvPosts);
        rv.setLayoutManager(new LinearLayoutManager(this));
        loadPosts();
    }

    private void loadPosts() {
        db.communityDao().getAllPosts().observe(this, posts -> {
            if (posts == null) return;
            rv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    LinearLayout row = new LinearLayout(AdminCommunityActivity.this);
                    row.setOrientation(LinearLayout.VERTICAL); row.setBackgroundResource(R.drawable.bg_rounded_card);
                    row.setPadding(dp(14), dp(12), dp(14), dp(12));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.bottomMargin = dp(8); lp.leftMargin = dp(16); lp.rightMargin = dp(16); row.setLayoutParams(lp);

                    TextView tvAuthor = new TextView(AdminCommunityActivity.this); tvAuthor.setId(android.R.id.text1); tvAuthor.setTextSize(13); tvAuthor.setTextColor(getColor(R.color.text_primary)); tvAuthor.setTypeface(null, android.graphics.Typeface.BOLD);
                    TextView tvContent = new TextView(AdminCommunityActivity.this); tvContent.setId(android.R.id.text2); tvContent.setTextSize(12); tvContent.setTextColor(getColor(R.color.text_secondary)); tvContent.setMaxLines(2);
                    TextView tvStats = new TextView(AdminCommunityActivity.this); tvStats.setId(android.R.id.message); tvStats.setTextSize(11); tvStats.setTextColor(getColor(R.color.text_tertiary));

                    LinearLayout actions = new LinearLayout(AdminCommunityActivity.this); actions.setOrientation(LinearLayout.HORIZONTAL);
                    Button btnComments = new Button(AdminCommunityActivity.this); btnComments.setId(android.R.id.button1); btnComments.setTextSize(10); btnComments.setAllCaps(false); btnComments.setText("Xem comment");
                    btnComments.setBackgroundResource(R.drawable.bg_chip); btnComments.setPadding(dp(8), 0, dp(8), 0);
                    Button btnDelete = new Button(AdminCommunityActivity.this); btnDelete.setId(android.R.id.button2); btnDelete.setTextSize(10); btnDelete.setAllCaps(false); btnDelete.setText("Xóa bài");
                    btnDelete.setBackgroundResource(R.drawable.bg_chip); btnDelete.setTextColor(getColor(R.color.error)); btnDelete.setPadding(dp(8), 0, dp(8), 0);
                    LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(32));
                    blp.setMarginEnd(dp(8)); btnComments.setLayoutParams(blp); btnDelete.setLayoutParams(blp);
                    actions.addView(btnComments); actions.addView(btnDelete);

                    row.addView(tvAuthor); row.addView(tvContent); row.addView(tvStats); row.addView(actions);
                    return new RecyclerView.ViewHolder(row) {};
                }
                @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
                    PostEntity p = posts.get(pos);
                    ((TextView) h.itemView.findViewById(android.R.id.text1)).setText(p.authorName + " • " + TimeUtils.getRelativeTime(p.createdAt));
                    ((TextView) h.itemView.findViewById(android.R.id.text2)).setText(p.content);
                    ((TextView) h.itemView.findViewById(android.R.id.message)).setText(p.likes + " likes • " + p.commentsCount + " comments • " + p.topic);
                    h.itemView.findViewById(android.R.id.button1).setOnClickListener(v -> showComments(p.id));
                    h.itemView.findViewById(android.R.id.button2).setOnClickListener(v ->
                        new AlertDialog.Builder(AdminCommunityActivity.this).setMessage("Xóa bài viết này?").setPositiveButton("Xóa", (d, w) -> {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                db.communityDao().deleteCommentsByPost(p.id);
                                db.communityDao().deletePost(p.id);
                                runOnUiThread(() -> Toast.makeText(AdminCommunityActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show());
                            });
                        }).setNegativeButton("Hủy", null).show());
                }
                @Override public int getItemCount() { return posts.size(); }
            });
        });
    }

    private void showComments(long postId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CommentEntity> comments = db.communityDao().getCommentsByPostSync(postId);
            runOnUiThread(() -> {
                if (comments.isEmpty()) { Toast.makeText(this, "Chưa có bình luận", Toast.LENGTH_SHORT).show(); return; }
                String[] items = new String[comments.size()];
                for (int i = 0; i < comments.size(); i++) items[i] = comments.get(i).authorName + ": " + comments.get(i).text;
                new AlertDialog.Builder(this).setTitle("Bình luận (" + comments.size() + ")").setItems(items, (d, which) -> {
                    new AlertDialog.Builder(this).setMessage("Xóa comment này?").setPositiveButton("Xóa", (d2, w) -> {
                        CommentEntity c = comments.get(which);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            db.communityDao().deleteComment(c.id);
                            db.communityDao().syncCommentCount(postId);
                            runOnUiThread(() -> Toast.makeText(this, "Đã xóa comment", Toast.LENGTH_SHORT).show());
                        });
                    }).setNegativeButton("Hủy", null).show();
                }).show();
            });
        });
    }

    private int dp(int dp) { return (int)(dp * getResources().getDisplayMetrics().density); }
}
