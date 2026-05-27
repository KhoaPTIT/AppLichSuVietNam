package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.PostEntity;
import com.lichsuvietnam.app.data.database.entities.CommentEntity;
import com.lichsuvietnam.app.data.database.entities.PostLikeEntity;
import com.lichsuvietnam.app.data.database.entities.CommentLikeEntity;
import java.util.List;

/**
 * DAO Room cho chức năng tương tác cộng đồng.
 * Quản lý bài viết, bình luận, lượt thích bài viết và lượt thích bình luận.
 */
@Dao
public interface CommunityDao {
    // Nhóm API thao tác với bài viết cộng đồng.
    @Insert
    long insertPost(PostEntity post);

    @Update
    void updatePost(PostEntity post);

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getAllPosts();

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    List<PostEntity> getAllPostsSync();

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    LiveData<PostEntity> getPostById(long postId);

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    PostEntity getPostByIdSync(long postId);

    @Query("UPDATE posts SET likes = likes + 1 WHERE id = :postId")
    void likePost(long postId);

    @Query("UPDATE posts SET likes = CASE WHEN likes > 0 THEN likes - 1 ELSE 0 END WHERE id = :postId")
    void unlikePost(long postId);

    @Query("UPDATE posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    void incrementCommentCount(long postId);

    @Query("UPDATE posts SET commentsCount = CASE WHEN commentsCount > 0 THEN commentsCount - 1 ELSE 0 END WHERE id = :postId")
    void decrementCommentCount(long postId);

    // Đồng bộ số bình luận từ bảng comments để tránh sai lệch bộ đếm.
    @Query("UPDATE posts SET commentsCount = (SELECT COUNT(*) FROM comments WHERE postId = posts.id)")
    void syncAllCommentCounts();

    @Query("UPDATE posts SET commentsCount = (SELECT COUNT(*) FROM comments WHERE postId = :postId) WHERE id = :postId")
    void syncCommentCount(long postId);

    @Query("SELECT * FROM posts WHERE content LIKE '%' || :query || '%' OR topic LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> searchPosts(String query);

    // Lấy bình luận theo thứ tự: bình luận cha trước, reply nằm ngay bên dưới cha.
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY " +
           "CASE WHEN parentCommentId = 0 THEN id ELSE parentCommentId END ASC, " +
           "CASE WHEN parentCommentId = 0 THEN 0 ELSE 1 END ASC, " +
           "createdAt ASC")
    LiveData<List<CommentEntity>> getCommentsByPost(long postId);

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY " +
           "CASE WHEN parentCommentId = 0 THEN id ELSE parentCommentId END ASC, " +
           "CASE WHEN parentCommentId = 0 THEN 0 ELSE 1 END ASC, " +
           "createdAt ASC")
    List<CommentEntity> getCommentsByPostSync(long postId);

    @Insert
    long insertComment(CommentEntity comment);

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    int getCommentCountByPost(long postId);

    @Query("UPDATE comments SET likes = likes + 1 WHERE id = :commentId")
    void likeComment(long commentId);

    @Query("UPDATE comments SET likes = CASE WHEN likes > 0 THEN likes - 1 ELSE 0 END WHERE id = :commentId")
    void unlikeComment(long commentId);

    @Query("DELETE FROM comments WHERE id = :commentId")
    void deleteComment(long commentId);

    @Query("DELETE FROM posts WHERE id = :postId")
    void deletePost(long postId);

    @Query("DELETE FROM comments WHERE postId = :postId")
    void deleteCommentsByPost(long postId);

    @Query("SELECT COUNT(*) FROM posts")
    int getPostCount();

    @Query("SELECT COUNT(*) FROM comments")
    int getCommentCount();

    // Nhóm API theo dõi lượt thích bài viết, tránh một người thích lặp nhiều lần.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertLike(PostLikeEntity like);

    @Query("DELETE FROM post_likes WHERE userId = :userId AND postId = :postId")
    void deleteLike(long userId, long postId);

    @Query("SELECT EXISTS(SELECT 1 FROM post_likes WHERE userId = :userId AND postId = :postId)")
    boolean isPostLiked(long userId, long postId);

    // Nhóm API theo dõi lượt thích bình luận.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCommentLike(CommentLikeEntity like);

    @Query("DELETE FROM comment_likes WHERE userId = :userId AND commentId = :commentId")
    void deleteCommentLike(long userId, long commentId);

    @Query("SELECT EXISTS(SELECT 1 FROM comment_likes WHERE userId = :userId AND commentId = :commentId)")
    boolean isCommentLiked(long userId, long commentId);
}
