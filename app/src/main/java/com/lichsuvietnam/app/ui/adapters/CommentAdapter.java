package com.lichsuvietnam.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.models.Comment;
import java.util.List;

/**
 * Adapter hiển thị danh sách bình luận trong chức năng cộng đồng.
 * Adapter hỗ trợ bình luận cha, bình luận trả lời, trạng thái đã thích
 * và callback cho hành động trả lời/thích.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final List<Comment> comments;
    private OnReplyClickListener replyListener;
    private OnLikeClickListener likeListener;

    /** Callback khi người dùng bấm trả lời một bình luận. */
    public interface OnReplyClickListener {
        void onReplyClick(Comment comment, int position);
    }

    /** Callback khi người dùng bấm thích hoặc bỏ thích bình luận. */
    public interface OnLikeClickListener {
        void onLikeClick(Comment comment, int position);
    }

    /** Khởi tạo adapter chỉ để hiển thị bình luận, không có hành động phụ. */
    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    /** Khởi tạo adapter có hỗ trợ trả lời bình luận. */
    public CommentAdapter(List<Comment> comments, OnReplyClickListener replyListener) {
        this.comments = comments;
        this.replyListener = replyListener;
    }

    /** Khởi tạo adapter có đầy đủ callback trả lời và thích bình luận. */
    public CommentAdapter(List<Comment> comments,
                          OnReplyClickListener replyListener,
                          OnLikeClickListener likeListener) {
        this.comments = comments;
        this.replyListener = replyListener;
        this.likeListener = likeListener;
    }

    /** Inflate layout item_comment.xml cho từng dòng bình luận. */
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    /** Gắn dữ liệu bình luận, trạng thái like và độ thụt dòng cho reply. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment c = comments.get(position);
        holder.tvInitial.setText(String.valueOf(c.getName().charAt(0)));
        holder.tvName.setText(c.getName());
        holder.tvTime.setText(c.getTime());
        holder.tvText.setText(c.getText());
        holder.tvLikes.setText(String.valueOf(c.getLikes()));

        // Cập nhật giao diện theo trạng thái đã thích/chưa thích.
        updateLikeVisual(holder, c.isLiked());

        // Thụt dòng reply dựa trên parentCommentId để phân biệt với bình luận cha.
        boolean isReply = c.getParentCommentId() > 0;
        if (isReply) {
            holder.itemView.setPadding(dpToPx(holder.itemView, 48), 0, 0, 0);
            // Thu nhỏ avatar của reply để thể hiện cấp bình luận con.
            ViewGroup.LayoutParams avatarParams = holder.avatarContainer.getLayoutParams();
            avatarParams.width = dpToPx(holder.itemView, 26);
            avatarParams.height = dpToPx(holder.itemView, 26);
            holder.avatarContainer.setLayoutParams(avatarParams);
            holder.tvInitial.setTextSize(10);
        } else {
            holder.itemView.setPadding(0, 0, 0, 0);
            ViewGroup.LayoutParams avatarParams = holder.avatarContainer.getLayoutParams();
            avatarParams.width = dpToPx(holder.itemView, 32);
            avatarParams.height = dpToPx(holder.itemView, 32);
            holder.avatarContainer.setLayoutParams(avatarParams);
            holder.tvInitial.setTextSize(12);
        }

        // Nút trả lời chuyển callback về ThreadActivity.
        if (holder.btnReply != null) {
            holder.btnReply.setOnClickListener(v -> {
                if (replyListener != null) {
                    replyListener.onReplyClick(c, holder.getAdapterPosition());
                }
            });
        }

        // Nút thích chuyển callback về ThreadActivity để cập nhật Room.
        if (holder.layoutLike != null) {
            holder.layoutLike.setOnClickListener(v -> {
                if (likeListener != null) {
                    likeListener.onLikeClick(c, holder.getAdapterPosition());
                }
            });
        }
    }

    /**
     * Cập nhật trạng thái thích của một item mà không cần nạp lại toàn bộ danh sách.
     */
    public void updateLikeState(int position, int newLikes, boolean liked) {
        if (position >= 0 && position < comments.size()) {
            Comment c = comments.get(position);
            c.setLikes(newLikes);
            c.setLiked(liked);
            notifyItemChanged(position, "like_update");
        }
    }

    /** Cập nhật màu sắc/độ mờ của icon và số lượt thích. */
    private void updateLikeVisual(ViewHolder holder, boolean liked) {
        if (liked) {
            holder.tvLikeIcon.setText("\uD83D\uDC4D");
            holder.tvLikes.setAlpha(1.0f);
            holder.tvLikeIcon.setAlpha(1.0f);
            holder.tvLikes.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.red_primary));
        } else {
            holder.tvLikeIcon.setText("\uD83D\uDC4D");
            holder.tvLikes.setAlpha(0.6f);
            holder.tvLikeIcon.setAlpha(0.6f);
            holder.tvLikes.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.text_tertiary));
        }
    }

    /** Đổi dp sang pixel để set padding/kích thước avatar động. */
    private int dpToPx(View view, int dp) {
        return (int) (dp * view.getContext().getResources().getDisplayMetrics().density);
    }

    @Override public int getItemCount() { return comments.size(); }

    /** ViewHolder lưu các view con của một dòng bình luận. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvName, tvTime, tvText, tvLikes, tvLikeIcon, btnReply;
        LinearLayout layoutLike;
        View avatarContainer;
        ViewHolder(@NonNull View v) {
            super(v);
            tvInitial = v.findViewById(R.id.tvInitial);
            tvName = v.findViewById(R.id.tvName);
            tvTime = v.findViewById(R.id.tvTime);
            tvText = v.findViewById(R.id.tvText);
            tvLikes = v.findViewById(R.id.tvLikes);
            tvLikeIcon = v.findViewById(R.id.tvLikeIcon);
            btnReply = v.findViewById(R.id.btnReply);
            layoutLike = v.findViewById(R.id.layoutLike);
            avatarContainer = v.findViewById(R.id.avatarContainer);
        }
    }
}
