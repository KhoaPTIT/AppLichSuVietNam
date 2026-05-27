package com.lichsuvietnam.app.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.adapters.CommentAdapter;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.CommentEntity;
import com.lichsuvietnam.app.data.database.entities.CommentLikeEntity;
import com.lichsuvietnam.app.data.database.entities.NotificationEntity;
import com.lichsuvietnam.app.data.database.entities.PostEntity;
import com.lichsuvietnam.app.data.models.Comment;
import com.lichsuvietnam.app.utils.SessionManager;
import com.lichsuvietnam.app.utils.TimeUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Màn hình luồng thảo luận của một bài viết cộng đồng.
 * Activity hiển thị bài gốc, danh sách bình luận, trả lời lồng nhau,
 * thích bình luận và tạo thông báo cho tác giả bài viết.
 */
public class ThreadActivity extends AppCompatActivity {
    // Room database dùng cho bài viết, bình luận, lượt thích và thông báo.
    private AppDatabase db;
    // Quản lý tài khoản hiện tại để kiểm tra quyền bình luận/thích.
    private SessionManager session;
    private long postId;
    private long replyToCommentId = 0;  // ID bình luận cha đang được trả lời.
    private String replyToName = null;
    private EditText etComment;
    private TextView tvCommentCount;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();

    /**
     * Khởi tạo luồng thảo luận, tải bài gốc, danh sách bình luận và xử lý gửi bình luận.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);
        postId = getIntent().getLongExtra("post_id", 1);
        tvCommentCount = findViewById(R.id.tvCommentCount);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView tvOriginalAuthor = findViewById(R.id.tvOriginalAuthor);
        TextView tvOriginalContent = findViewById(R.id.tvOriginalContent);
        TextView tvOriginalTime = findViewById(R.id.tvOriginalTime);

        db.communityDao().getPostById(postId).observe(this, post -> {
            if (post != null) {
                tvOriginalAuthor.setText(post.authorName);
                tvOriginalContent.setText(post.content);
                tvOriginalTime.setText(TimeUtils.getRelativeTime(post.createdAt));
            }
        });

        RecyclerView rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        commentAdapter = new CommentAdapter(commentList,
                // Callback trả lời bình luận.
                (comment, position) -> {
                    if (session.isLoggedIn()) {
                        // Lưu ID bình luận cha để giữ cấu trúc luồng bình luận.
                        // Nếu đang trả lời một reply, dùng lại parent gốc.
                        replyToCommentId = comment.getParentCommentId() > 0
                                ? comment.getParentCommentId()
                                : comment.getId();
                        replyToName = comment.getName();
                        etComment.setHint("Tr\u1ea3 l\u1eddi @" + replyToName + "...");
                        etComment.requestFocus();
                    } else {
                        Toast.makeText(this, "Vui l\u00f2ng \u0111\u0103ng nh\u1eadp \u0111\u1ec3 tr\u1ea3 l\u1eddi", Toast.LENGTH_SHORT).show();
                    }
                },
                // Callback thích/bỏ thích bình luận.
                (comment, position) -> {
                    handleCommentLike(comment, position);
                }
        );
        rvComments.setAdapter(commentAdapter);

        db.communityDao().getCommentsByPost(postId).observe(this, entities -> {
            commentList.clear();
            long userId = session.getUserId();

            // Gom nhóm: bình luận cha đứng trước, các reply nằm ngay bên dưới.
            List<CommentEntity> parents = new ArrayList<>();
            Map<Long, List<CommentEntity>> repliesMap = new LinkedHashMap<>();

            for (CommentEntity c : entities) {
                if (c.parentCommentId == 0) {
                    parents.add(c);
                    if (!repliesMap.containsKey(c.id)) {
                        repliesMap.put(c.id, new ArrayList<>());
                    }
                } else {
                    if (!repliesMap.containsKey(c.parentCommentId)) {
                        repliesMap.put(c.parentCommentId, new ArrayList<>());
                    }
                    repliesMap.get(c.parentCommentId).add(c);
                }
            }

            // Làm phẳng danh sách theo thứ tự cha -> các reply.
            for (CommentEntity parent : parents) {
                commentList.add(buildComment(parent, userId));
                List<CommentEntity> replies = repliesMap.get(parent.id);
                if (replies != null) {
                    for (CommentEntity reply : replies) {
                        commentList.add(buildComment(reply, userId));
                    }
                }
            }

            // Xử lý reply bị mồ côi khi parentCommentId trỏ tới bình luận không còn tồn tại.
            for (CommentEntity c : entities) {
                if (c.parentCommentId > 0) {
                    boolean parentExists = false;
                    for (CommentEntity p : parents) {
                        if (p.id == c.parentCommentId) { parentExists = true; break; }
                    }
                    if (!parentExists) {
                        // Tránh thêm trùng reply mồ côi.
                        boolean alreadyAdded = false;
                        for (Comment existing : commentList) {
                            if (existing.getId() == c.id) { alreadyAdded = true; break; }
                        }
                        if (!alreadyAdded) {
                            commentList.add(buildComment(c, userId));
                        }
                    }
                }
            }

            // Cập nhật số bình luận đang hiển thị.
            if (tvCommentCount != null) {
                tvCommentCount.setText(entities.size() + " b\u00ecnh lu\u1eadn");
            }

            // Đồng bộ lại commentsCount của bài viết theo số bình luận thực tế.
            Executors.newSingleThreadExecutor().execute(() -> db.communityDao().syncCommentCount(postId));

            commentAdapter.notifyDataSetChanged();
        });

        etComment = findViewById(R.id.etComment);
        FrameLayout btnSend = findViewById(R.id.btnSend);

        if (session.isLoggedIn()) {
            btnSend.setOnClickListener(v -> {
                String text = etComment.getText().toString().trim();
                if (TextUtils.isEmpty(text)) return;

                final String finalText;
                final boolean isReply;
                final long parentId;
                if (replyToName != null && replyToCommentId > 0) {
                    finalText = "@" + replyToName + " " + text;
                    isReply = true;
                    parentId = replyToCommentId;
                } else {
                    finalText = text;
                    isReply = false;
                    parentId = 0;
                }

                Executors.newSingleThreadExecutor().execute(() -> {
                    CommentEntity comment = new CommentEntity(postId, session.getUserId(), session.getUserName(), finalText);
                    comment.isReply = isReply;
                    comment.parentCommentId = parentId;
                    db.communityDao().insertComment(comment);
                    // Đồng bộ số bình luận sau khi thêm bình luận mới.
                    db.communityDao().syncCommentCount(postId);

                    // Tạo thông báo cho tác giả bài viết nếu người bình luận là người khác.
                    PostEntity post = db.communityDao().getPostByIdSync(postId);
                    if (post != null && post.userId > 0 && post.userId != session.getUserId()) {
                        String msg = session.getUserName() + " \u0111\u00e3 b\u00ecnh lu\u1eadn: " + (finalText.length() > 50 ? finalText.substring(0, 50) + "..." : finalText);
                        NotificationEntity notif = new NotificationEntity(post.userId, session.getUserId(), session.getUserName(), "comment", msg, postId);
                        db.notificationDao().insert(notif);
                    }

                    runOnUiThread(() -> {
                        etComment.setText("");
                        etComment.setHint(getString(R.string.write_comment));
                        replyToName = null;
                        replyToCommentId = 0;
                        Toast.makeText(this, "\u0110\u00e3 \u0111\u0103ng b\u00ecnh lu\u1eadn", Toast.LENGTH_SHORT).show();
                    });
                });
            });
        } else {
            etComment.setEnabled(false);
            etComment.setHint("\u0110\u0103ng nh\u1eadp \u0111\u1ec3 b\u00ecnh lu\u1eadn");
        }
    }

    /**
     * Chuyển CommentEntity từ Room sang model Comment dùng cho adapter.
     * Đồng thời kiểm tra người dùng hiện tại đã thích bình luận hay chưa.
     */
    private Comment buildComment(CommentEntity c, long userId) {
        Comment comment = new Comment(
                c.id,
                c.authorName,
                c.text,
                TimeUtils.getRelativeTime(c.createdAt),
                c.likes,
                c.parentCommentId > 0,
                c.parentCommentId
        );
        if (userId > 0) {
            comment.setLiked(db.communityDao().isCommentLiked(userId, c.id));
        }
        return comment;
    }

    /**
     * Xử lý thích/bỏ thích bình luận.
     * UI được cập nhật lạc quan trước, sau đó Room cập nhật bảng CommentLikeEntity.
     */
    private void handleCommentLike(Comment comment, int position) {
        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Vui l\u00f2ng \u0111\u0103ng nh\u1eadp \u0111\u1ec3 th\u00edch b\u00ecnh lu\u1eadn", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.getId() <= 0) return;

        long userId = session.getUserId();
        long commentId = comment.getId();
        boolean wasLiked = comment.isLiked();

        int newLikes;
        if (wasLiked) {
            newLikes = Math.max(0, comment.getLikes() - 1);
        } else {
            newLikes = comment.getLikes() + 1;
        }
        commentAdapter.updateLikeState(position, newLikes, !wasLiked);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (wasLiked) {
                db.communityDao().deleteCommentLike(userId, commentId);
                db.communityDao().unlikeComment(commentId);
            } else {
                db.communityDao().insertCommentLike(new CommentLikeEntity(userId, commentId));
                db.communityDao().likeComment(commentId);
            }
        });
    }
}
