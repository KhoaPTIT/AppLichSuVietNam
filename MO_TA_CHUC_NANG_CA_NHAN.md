# Mô tả phần code cá nhân thực hiện

Tài liệu này liệt kê các file liên quan đến 10 chức năng đã được comment bằng tiếng Việt trong code. Các comment tập trung vào lớp, hàm xử lý chính, DAO Room và các API gọi ngoài như `Intent`, `LiveData`, `Glide`, `RangeSlider`, `YouTubePlayerView`.

## 1. Tìm kiếm và lọc sự kiện

- `app/src/main/java/com/lichsuvietnam/app/ui/activities/SearchActivity.java`
  - Xử lý tìm kiếm sự kiện, nhân vật, địa danh.
  - Lưu và hiển thị lịch sử tìm kiếm gần đây cho người dùng đã đăng nhập.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/EventListActivity.java`
  - Hiển thị danh sách sự kiện.
  - Lọc theo giai đoạn bằng chip và tìm kiếm bằng ô nhập.
- `app/src/main/java/com/lichsuvietnam/app/ui/adapters/EventAdapter.java`
  - Adapter hiển thị sự kiện, hỗ trợ nhóm theo giai đoạn.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/HistoryEventDao.java`
  - DAO chứa query lấy, lọc và tìm kiếm sự kiện.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/SearchHistoryDao.java`
  - DAO lưu và lấy lịch sử tìm kiếm.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/SearchHistoryEntity.java`
  - Entity lưu từ khóa tìm kiếm của người dùng.

## 2. Chia sẻ và lưu trữ mục yêu thích

- `app/src/main/java/com/lichsuvietnam/app/ui/activities/EventDetailActivity.java`
  - Xử lý lưu/bỏ lưu sự kiện yêu thích.
  - Mở thảo luận cộng đồng và mở tài liệu liên quan bằng trình duyệt.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/SavedActivity.java`
  - Hiển thị danh sách sự kiện đã lưu của người dùng.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/NewPostActivity.java`
  - Cho phép người dùng chia sẻ nội dung/hỏi đáp/kiến thức trong cộng đồng.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/BookmarkDao.java`
  - DAO lưu, xóa, kiểm tra và lấy danh sách bookmark.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/BookmarkEntity.java`
  - Entity lưu quan hệ userId - eventId cho mục yêu thích.

## 3. Tương tác cộng đồng

- `app/src/main/java/com/lichsuvietnam/app/ui/fragments/CommunityFragment.java`
  - Hiển thị bài viết cộng đồng, tạo bài mới, thích bài viết và mở luồng bình luận.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/NewPostActivity.java`
  - Tạo bài viết cộng đồng theo chủ đề.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/ThreadActivity.java`
  - Hiển thị bài gốc, bình luận, reply, like bình luận và tạo thông báo.
- `app/src/main/java/com/lichsuvietnam/app/ui/adapters/CommentAdapter.java`
  - Adapter hiển thị bình luận cha/reply và trạng thái like.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/CommunityDao.java`
  - DAO quản lý bài viết, bình luận và lượt thích.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/PostEntity.java`
  - Entity lưu bài viết cộng đồng.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/CommentEntity.java`
  - Entity lưu bình luận và reply.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/PostLikeEntity.java`
  - Entity chống thích trùng bài viết.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/CommentLikeEntity.java`
  - Entity chống thích trùng bình luận.
- `app/src/main/java/com/lichsuvietnam/app/data/models/Comment.java`
  - Model hiển thị bình luận cho adapter.

## 4. Thông tin chi tiết sự kiện

- `app/src/main/java/com/lichsuvietnam/app/ui/activities/EventDetailActivity.java`
  - Hiển thị tiêu đề, năm, địa điểm, nội dung đầy đủ, ảnh chính, nhân vật liên quan, video, nút lưu và tài liệu.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/HistoryEventEntity.java`
  - Entity lưu dữ liệu sự kiện dùng cho danh sách, tìm kiếm, chi tiết và timeline.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/HistoryEventDao.java`
  - DAO lấy sự kiện theo id và các query liên quan.

## 5. Hình ảnh lịch sử

- `app/src/main/java/com/lichsuvietnam/app/ui/activities/ImageGalleryActivity.java`
  - Hiển thị ảnh sự kiện dạng lưới và lọc theo giai đoạn.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/MediaViewerActivity.java`
  - Xem ảnh từng ảnh với nút trước/sau.
- `app/src/main/java/com/lichsuvietnam/app/utils/ImageUtils.java`
  - Lớp bọc API Glide để tải ảnh từ URL hoặc drawable.
- `app/src/main/java/com/lichsuvietnam/app/data/provider/DataProvider.java`
  - Cung cấp danh sách ảnh nội bộ cho MediaViewerActivity.

## 6. Video & tài liệu đa phương tiện

- `app/src/main/java/com/lichsuvietnam/app/ui/activities/VideoListActivity.java`
  - Hiển thị danh sách video và mở YouTube hoặc trang tìm kiếm YouTube.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/VideoPlayerActivity.java`
  - Phát video bằng thư viện ngoài `android-youtube-player`.
- `app/src/main/java/com/lichsuvietnam/app/ui/adapters/VideoAdapter.java`
  - Adapter hiển thị video liên quan/danh sách video.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/VideoDao.java`
  - DAO truy vấn video theo sự kiện, toàn bộ video và video liên quan.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/VideoEntity.java`
  - Entity lưu thông tin video và xử lý nguồn thumbnail.

## 7. Bản đồ thời gian

- `app/src/main/java/com/lichsuvietnam/app/ui/fragments/TimeMapFragment.java`
  - Hiển thị bản đồ theo các giai đoạn lịch sử bằng nút trước/sau và SeekBar.
- `app/src/main/java/com/lichsuvietnam/app/data/models/TimeMapSlide.java`
  - Model dữ liệu cho từng slide bản đồ.
- `app/src/main/java/com/lichsuvietnam/app/data/provider/DataProvider.java`
  - Cung cấp 20 slide bản đồ lịch sử, dùng drawable `map_period_XX`.
- `app/src/main/java/com/lichsuvietnam/app/utils/ImageUtils.java`
  - Tải ảnh bản đồ bằng Glide khi cần.

## 8. Đường thời gian động

- `app/src/main/java/com/lichsuvietnam/app/ui/fragments/DynamicTimelineFragment.java`
  - Lọc sự kiện theo giai đoạn và khoảng năm bằng Material `RangeSlider`.
- `app/src/main/java/com/lichsuvietnam/app/ui/adapters/TimelineAdapter.java`
  - Adapter hiển thị danh sách sự kiện dạng timeline.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/HistoryEventDao.java`
  - DAO lấy dữ liệu sự kiện theo giai đoạn và toàn bộ sự kiện.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/HistoryEventEntity.java`
  - Dùng `yearNumeric` để lọc/sắp xếp trên timeline.

## 9. Chế độ học tập tương tác

- `app/src/main/java/com/lichsuvietnam/app/ui/fragments/LearnFragment.java`
  - Hiển thị thống kê học tập, điểm, tiến độ và danh sách lộ trình học.
  - Điều hướng tới flashcard, quiz tổng hợp và chi tiết lộ trình.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/LearningPathDetailActivity.java`
  - Hiển thị các bài học trong lộ trình và trạng thái hoàn thành từng bài.
  - Mở QuizActivity theo bài học được chọn.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/FlashcardActivity.java`
  - Chế độ ôn tập flashcard tương tác: lật thẻ, đánh dấu nhớ/chưa nhớ và ôn lại.
- `app/src/main/java/com/lichsuvietnam/app/ui/adapters/LearningPathAdapter.java`
  - Adapter hiển thị lộ trình học và thanh tiến độ.
- `app/src/main/java/com/lichsuvietnam/app/data/models/LearningPath.java`
  - Model giao diện tính phần trăm tiến độ của lộ trình.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/LearningDao.java`
  - DAO quản lý lộ trình, bài học và tiến độ học.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/FlashcardDao.java`
  - DAO quản lý flashcard và tiến độ ôn tập.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/LearningPathEntity.java`
  - Entity lưu lộ trình học.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/LessonEntity.java`
  - Entity lưu bài học.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/LearningProgressEntity.java`
  - Entity lưu tiến độ cấp lộ trình.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/LessonProgressEntity.java`
  - Entity lưu tiến độ từng bài học.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/FlashcardEntity.java`
  - Entity lưu nội dung flashcard.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/FlashcardProgressEntity.java`
  - Entity lưu trạng thái nhớ/chưa nhớ của từng flashcard.

## 10. Câu hỏi & bài kiểm tra

- `app/src/main/java/com/lichsuvietnam/app/ui/activities/QuizActivity.java`
  - Tải câu hỏi theo bài học, lộ trình, sự kiện, nhân vật hoặc bài kiểm tra tổng hợp.
  - Xử lý chọn đáp án, bỏ qua, tính điểm, lưu kết quả và cập nhật tiến độ học.
- `app/src/main/java/com/lichsuvietnam/app/ui/activities/QuizResultActivity.java`
  - Hiển thị kết quả bài kiểm tra, số câu đúng/sai/bỏ qua, thời gian và điểm nhận được.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/QuizDao.java`
  - DAO truy vấn ngân hàng câu hỏi và lưu kết quả quiz.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/QuizQuestionEntity.java`
  - Entity lưu câu hỏi, lựa chọn, đáp án đúng và ngữ cảnh câu hỏi.
- `app/src/main/java/com/lichsuvietnam/app/data/database/entities/QuizResultEntity.java`
  - Entity lưu kết quả một lần làm quiz.
- `app/src/main/java/com/lichsuvietnam/app/data/database/dao/LearningDao.java`
  - Được QuizActivity dùng để cập nhật tiến độ bài học/lộ trình sau khi làm bài.

## Ghi chú nộp bài

- Code đã được comment bằng tiếng Việt ở các lớp, hàm chính và các API gọi ngoài.
- Project không có sẵn GitHub remote trong workspace, nên bản nộp được đóng gói dưới dạng file zip.
