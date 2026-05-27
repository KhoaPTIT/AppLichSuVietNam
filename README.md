# Lịch Sử Việt Nam Tương Tác - Android App 

App học Lịch Sử Việt Nam tương tác với đầy đủ database, authentication, CRUD.

## Kiến trúc

```
com.lichsuvietnam.app/
├── MyApp.java                  
├── database/
│   ├── AppDatabase.java        
│   ├── DatabaseSeeder.java       
│   ├── entities/            
│   │   ├── UserEntity
│   │   ├── HistoryEventEntity
│   │   ├── QuizQuestionEntity
│   │   ├── QuizResultEntity
│   │   ├── FlashcardEntity
│   │   ├── FlashcardProgressEntity
│   │   ├── LearningPathEntity
│   │   ├── LearningProgressEntity
│   │   ├── PostEntity
│   │   ├── CommentEntity
│   │   ├── BookmarkEntity
│   │   └── SearchHistoryEntity
│   └── dao/                     
│       ├── UserDao
│       ├── HistoryEventDao
│       ├── QuizDao
│       ├── FlashcardDao
│       ├── LearningDao
│       ├── CommunityDao
│       ├── BookmarkDao
│       └── SearchHistoryDao
├── utils/
│   ├── SessionManager.java       
│   └── TimeUtils.java            
├── activities/                  
├── fragments/                   
├── adapters/                     
└── models/                      
```

## Tính năng hoàn chỉnh

### Authentication 
- Đăng ký tài khoản (lưu vào DB, hash password SHA-256)
- Đăng nhập (xác thực từ DB)
- Quên mật khẩu (flow 3 bước)
- Chế độ khách
- Session management (tự đăng nhập lại)

### Database (Room - 12 bảng)
- 11 sự kiện lịch sử chi tiết
- 15 câu hỏi quiz (3 mức độ)
- 15 flashcards
- 5 lộ trình học tập
- Bài viết & bình luận cộng đồng
- Bookmark/lưu sự kiện
- Lịch sử tìm kiếm

### Timeline (Trang chủ)
- Hiển thị sự kiện từ DB, sắp xếp theo năm
- Lọc theo thời kỳ (Bắc thuộc, Phong kiến, Cận đại, Hiện đại)
- Click vào xem chi tiết đầy đủ

### Tìm kiếm 
- Full-text search trong DB (title, description, location, nhân vật)
- Lưu lịch sử tìm kiếm
- Kết quả real-time khi gõ

### Quiz 
- 15 câu hỏi từ DB, random mỗi lần chơi
- Đếm thời gian thực
- Tính điểm, xếp hạng (A+ → D)
- Lưu kết quả vào DB
- Cộng điểm cho user

### Flashcards 
- 15 thẻ từ DB
- Theo dõi tiến trình (nhớ/chưa nhớ)
- Ưu tiên hiện thẻ chưa thuộc
- Đếm số lần ôn tập

### Cộng đồng (CRUD)
- Hiển thị bài viết từ DB
- Xem thread + bình luận
- Đăng bình luận mới (lưu DB)
- Like bài viết

### Bookmark
- Lưu/bỏ lưu sự kiện yêu thích
- Đếm số bookmark trên profile

### Profile
- Hiển thị thông tin thật từ DB
- Điểm số, giờ học, số bookmark
- Phân biệt user đăng nhập vs khách

## Cài đặt
1. Mở Android Studio → File → Open → chọn thư mục LichSuVietNam
2. Đợi Gradle sync
3. Run trên emulator hoặc thiết bị thật
4. Lần đầu chạy: DB tự động seed dữ liệu mẫu

## Yêu cầu
- Android Studio Hedgehog+ | Gradle 8.2 | Min SDK 24 | JDK 17
