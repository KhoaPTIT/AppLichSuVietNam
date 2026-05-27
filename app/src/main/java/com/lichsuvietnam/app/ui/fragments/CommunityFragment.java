package com.lichsuvietnam.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.activities.NewPostActivity;
import com.lichsuvietnam.app.ui.activities.ThreadActivity;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.PostEntity;
import com.lichsuvietnam.app.data.database.entities.PostLikeEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import com.lichsuvietnam.app.utils.TimeUtils;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Fragment Tương tác cộng đồng.
 * Hiển thị danh sách bài viết, cho phép người dùng đã đăng nhập tạo bài,
 * thích bài viết và mở luồng bình luận của từng bài.
 */
public class CommunityFragment extends Fragment {
    // Room database dùng cho bài viết, lượt thích và số bình luận.
    private AppDatabase db;
    // Quản lý phiên đăng nhập để kiểm tra quyền đăng bài/thích bài.
    private SessionManager session;

    /**
     * Khởi tạo giao diện cộng đồng, gắn nút đăng bài và quan sát danh sách bài viết.
     */
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        db = AppDatabase.getInstance(requireContext());
        session = new SessionManager(requireContext());

        TextView txtInitial = view.findViewById(R.id.txtComposeInitial);
        if (session.isLoggedIn()) {
            String name = session.getUserName();
            txtInitial.setText(name.isEmpty() ? "?" : String.valueOf(name.charAt(0)));
        }

        // Dùng chung listener cho thanh soạn bài và nút nổi tạo bài mới.
        View.OnClickListener openNewPost = v -> {
            if (!session.isLoggedIn()) { Toast.makeText(getContext(), "Vui lòng đăng nhập để đăng bài", Toast.LENGTH_SHORT).show(); return; }
            startActivity(new Intent(getContext(), NewPostActivity.class));
        };
        view.findViewById(R.id.composeBar).setOnClickListener(openNewPost);
        view.findViewById(R.id.fabNewPost).setOnClickListener(openNewPost);

        RecyclerView rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        // LiveData từ CommunityDao giúp danh sách bài viết tự cập nhật khi có bài mới.
        db.communityDao().getAllPosts().observe(getViewLifecycleOwner(), entities -> {
            if (entities == null) return;
            long userId = session.getUserId();

            rvPosts.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                @NonNull @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = inflater.inflate(R.layout.item_post, parent, false);
                    return new RecyclerView.ViewHolder(v) {};
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
                    PostEntity e = entities.get(pos);
                    View v = h.itemView;
                    ((TextView) v.findViewById(R.id.tvInitial)).setText(String.valueOf(e.authorName.charAt(0)));
                    ((TextView) v.findViewById(R.id.tvAuthor)).setText(e.authorName);
                    ((TextView) v.findViewById(R.id.tvTime)).setText(TimeUtils.getRelativeTime(e.createdAt));
                    ((TextView) v.findViewById(R.id.tvContent)).setText(e.content);
                    ((TextView) v.findViewById(R.id.tvTopic)).setText(e.topic);

                    // Lấy số bình luận thật từ database để tránh lệch với commentsCount cũ.
                    TextView tvComments = v.findViewById(R.id.tvComments);
                    Executors.newSingleThreadExecutor().execute(() -> {
                        int realCount = db.communityDao().getCommentCountByPost(e.id);
                        if (getActivity() != null) getActivity().runOnUiThread(() -> tvComments.setText(String.valueOf(realCount)));
                    });

                    // Hiển thị và xử lý lượt thích, có bảng PostLikeEntity để chống thích lặp.
                    TextView tvLikes = v.findViewById(R.id.tvLikes);
                    TextView tvLikeEmoji = v.findViewById(R.id.tvLikeEmoji);
                    tvLikes.setText(String.valueOf(e.likes));

                    if (session.isLoggedIn() && userId > 0) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            boolean liked = db.communityDao().isPostLiked(userId, e.id);
                            if (getActivity() != null) getActivity().runOnUiThread(() -> tvLikeEmoji.setAlpha(liked ? 1.0f : 0.6f));
                        });
                    }

                    View btnLike = v.findViewById(R.id.btnLike);
                    btnLike.setOnClickListener(lv -> {
                        if (!session.isLoggedIn() || userId <= 0) { Toast.makeText(getContext(), "Đăng nhập để thích", Toast.LENGTH_SHORT).show(); return; }
                        Executors.newSingleThreadExecutor().execute(() -> {
                            boolean liked = db.communityDao().isPostLiked(userId, e.id);
                            if (liked) {
                                db.communityDao().deleteLike(userId, e.id);
                                db.communityDao().unlikePost(e.id);
                            } else {
                                db.communityDao().insertLike(new PostLikeEntity(userId, e.id));
                                db.communityDao().likePost(e.id);
                            }
                            PostEntity updated = db.communityDao().getPostByIdSync(e.id);
                            boolean nowLiked = !liked;
                            if (getActivity() != null) getActivity().runOnUiThread(() -> {
                                tvLikes.setText(String.valueOf(updated != null ? updated.likes : e.likes));
                                tvLikeEmoji.setAlpha(nowLiked ? 1.0f : 0.6f);
                            });
                        });
                    });

                    v.setOnClickListener(iv -> {
                        Intent intent = new Intent(getContext(), ThreadActivity.class);
                        intent.putExtra("post_id", e.id);
                        startActivity(intent);
                    });
                }

                @Override public int getItemCount() { return entities.size(); }
            });
        });

        return view;
    }
}
