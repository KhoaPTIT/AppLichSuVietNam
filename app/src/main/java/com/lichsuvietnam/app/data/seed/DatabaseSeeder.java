package com.lichsuvietnam.app.data.seed;

import android.content.Context;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.*;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class DatabaseSeeder {

    public static void seedIfEmpty(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                if (db.historyEventDao().getCount() == 0) { seedEvents(db); }
                if (db.learningDao().getPathCount() == 0) { seedLearningPaths(db); }
                if (db.learningDao().getLessonCount() == 0) { seedLessons(db); }
                if (db.quizDao().getQuestionCount() == 0) { seedQuizQuestions(db); }
                if (db.flashcardDao().getCardCount() == 0) { seedFlashcards(db); }
                if (db.communityDao().getPostCount() == 0) { seedPosts(db); }
                if (db.figureDao().getCount() == 0) { seedFigures(db); }
                if (db.videoDao().getCount() == 0) { seedVideos(db); }
                seedAdminUser(db);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private static void seedAdminUser(AppDatabase db) {
        if (db.userDao().getUserByEmail("admin@lichsu.vn") != null) return;
        UserEntity admin = new UserEntity("Admin", "admin@lichsu.vn", SessionManager.hashPassword("admin123"));
        admin.role = "admin";
        admin.isActive = true;
        db.userDao().insert(admin);
    }

    // ============ HELPER ============
    private static QuizQuestionEntity q(long pathId, long lessonId, String question, String a, String b, String c, String d, int correct, String diff) {
        QuizQuestionEntity q = new QuizQuestionEntity(question, a, b, c, d, correct);
        q.pathId = pathId; q.lessonId = lessonId; q.difficulty = diff;
        return q;
    }
    private static QuizQuestionEntity qf(long pathId, long lessonId, long figureId, String question, String a, String b, String c, String d, int correct, String diff) {
        QuizQuestionEntity q = new QuizQuestionEntity(question, a, b, c, d, correct);
        q.pathId = pathId; q.lessonId = lessonId; q.figureId = figureId; q.difficulty = diff;
        return q;
    }

    private static void seedQuizQuestions(AppDatabase db) {
        List<QuizQuestionEntity> qs = new ArrayList<>();

        // ===== PATH 1: Thời kỳ dựng nước (lessonId 1–8) =====
        // L1: Nguồn gốc dân tộc Việt
        qs.add(q(1,1,"Theo truyền thuyết, tổ tiên người Việt là ai?","Lạc Long Quân và Âu Cơ","Hùng Vương và Mỵ Nương","An Dương Vương và Mỵ Châu","Sơn Tinh và Thủy Tinh",0,"easy"));
        qs.add(q(1,1,"Truyền thuyết \\\"Bọc trăm trứng\\\" liên quan đến ai?","Sơn Tinh – Thủy Tinh","Lạc Long Quân – Âu Cơ","An Dương Vương","Thánh Gióng",1,"easy"));
        qs.add(q(1,1,"Theo truyền thuyết, con cháu Lạc Long Quân và Âu Cơ chia đi đâu?","50 con lên rừng, 50 con xuống biển","Tất cả ở lại đồng bằng","100 con đi về phương Bắc","75 con lên núi, 25 con xuống biển",0,"medium"));

        // L2: Nhà nước Văn Lang
        qs.add(q(1,2,"Nước Văn Lang do ai đứng đầu?","Lạc Long Quân","Hùng Vương","An Dương Vương","Thục Phán",1,"easy"));
        qs.add(q(1,2,"Nhà nước Văn Lang tồn tại qua bao nhiêu đời Hùng Vương?","10 đời","15 đời","18 đời","20 đời",2,"medium"));
        qs.add(q(1,2,"Đặc trưng nền kinh tế chính của Văn Lang là gì?","Săn bắn hái lượm","Nông nghiệp lúa nước","Thương mại đường biển","Chăn nuôi gia súc",1,"easy"));

        // L3: Thành Cổ Loa
        qs.add(q(1,3,"An Dương Vương xây thành gì?","Thành Thăng Long","Thành Hoa Lư","Thành Cổ Loa","Thành Đại La",2,"easy"));
        qs.add(q(1,3,"Thành Cổ Loa có bao nhiêu vòng thành theo truyền thuyết?","3 vòng","6 vòng","9 vòng","12 vòng",2,"medium"));
        qs.add(q(1,3,"Thành Cổ Loa nằm ở đâu ngày nay?","Ninh Bình","Đông Anh, Hà Nội","Phú Thọ","Bắc Ninh",1,"easy"));

        // L4: Nỏ thần Kim Quy
        qs.add(q(1,4,"Nỏ thần của An Dương Vương còn gọi là gì?","Nỏ liên châu","Nỏ thần Kim Quy","Nỏ vạn tiễn","Nỏ thiên lôi",0,"medium"));
        qs.add(q(1,4,"Ai tặng vuốt rùa cho An Dương Vương theo truyền thuyết?","Sơn Tinh","Thần Kim Quy","Long Vương","Lạc Long Quân",1,"easy"));
        qs.add(q(1,4,"Ai đã lấy cắp bí mật nỏ thần?","Mã Viện","Triệu Đà","Trọng Thủy","Tô Định",2,"medium"));

        // L5: Văn hóa Đông Sơn
        qs.add(q(1,5,"Trống đồng Đông Sơn thuộc nền văn hóa nào?","Văn hóa Sa Huỳnh","Văn hóa Đông Sơn","Văn hóa Hòa Bình","Văn hóa Óc Eo",1,"medium"));
        qs.add(q(1,5,"Trống đồng Đông Sơn nổi bật ở đặc điểm gì?","Hình rồng phượng","Hoa văn hình học và hình người","Chạm khắc chữ Hán","Hình mặt trời và rắn",1,"medium"));
        qs.add(q(1,5,"Văn hóa Đông Sơn phân bố chủ yếu ở vùng nào?","Nam Trung Bộ","Bắc Bộ và Bắc Trung Bộ","Tây Nguyên","Đồng bằng sông Cửu Long",1,"hard"));

        // L6: Nước Âu Lạc
        qs.add(q(1,6,"Nước Âu Lạc do ai sáng lập?","Hùng Vương","Lạc Long Quân","Thục Phán","Triệu Đà",2,"easy"));
        qs.add(q(1,6,"Âu Lạc là sự hợp nhất của hai bộ tộc nào?","Lạc Việt và Mường","Âu Việt và Lạc Việt","Kinh và Tày","Việt và Chăm",1,"easy"));
        qs.add(q(1,6,"Nước Âu Lạc bị ai xâm chiếm?","Mã Viện","Tô Định","Triệu Đà","Lưu Bang",2,"medium"));

        // L7: Giỗ Tổ Hùng Vương
        qs.add(q(1,7,"Ngày Giỗ Tổ Hùng Vương là ngày nào?","10/3 âm lịch","15/1 âm lịch","1/1 âm lịch","5/5 âm lịch",0,"easy"));
        qs.add(q(1,7,"Đền Hùng nằm ở tỉnh nào?","Hà Nội","Phú Thọ","Bắc Ninh","Vĩnh Phúc",1,"easy"));
        qs.add(q(1,7,"Giỗ Tổ Hùng Vương thể hiện truyền thống gì?","Thờ cúng tổ tiên, uống nước nhớ nguồn","Mừng năm mới","Cầu mùa màng","Lễ hội mùa xuân",0,"easy"));

        // L8: Kinh đô Phong Châu
        qs.add(q(1,8,"Kinh đô của nước Văn Lang ở đâu?","Cổ Loa","Phong Châu","Hoa Lư","Thăng Long",1,"medium"));
        qs.add(q(1,8,"Phong Châu ngày nay thuộc tỉnh nào?","Hà Nội","Phú Thọ","Vĩnh Phúc","Bắc Ninh",1,"medium"));
        qs.add(q(1,8,"Phong Châu nằm ở vùng nào?","Ven biển","Trung du Bắc Bộ","Đồng bằng sông Cửu Long","Tây Nguyên",1,"hard"));

        // ===== PATH 2: Nghìn năm Bắc thuộc (lessonId 9–20) =====
        // L9: Khởi nghĩa Hai Bà Trưng
        qs.add(q(2,9,"Khởi nghĩa Hai Bà Trưng nổ ra vào năm nào?","Năm 39 SCN","Năm 40 SCN","Năm 42 SCN","Năm 43 SCN",1,"easy"));
        qs.add(q(2,9,"Hai Bà Trưng khởi nghĩa chống ách đô hộ của triều đại nào?","Nhà Ngô","Nhà Hán","Nhà Đường","Nhà Tống",1,"easy"));
        qs.add(q(2,9,"Nguyên nhân trực tiếp khiến Trưng Trắc khởi nghĩa là gì?","Bị đàn áp tôn giáo","Tô Định giết Thi Sách và áp bức","Bị tăng thuế nặng","Mất mùa đói kém",1,"medium"));

        // L10: 65 thành trì giải phóng
        qs.add(q(2,10,"Hai Bà Trưng giải phóng bao nhiêu thành trì?","35 thành","55 thành","65 thành","75 thành",2,"medium"));
        qs.add(q(2,10,"Sau khi giải phóng, Trưng Trắc xưng tước gì?","Hoàng đế","Vương","Quốc vương","Nữ hoàng",1,"medium"));
        qs.add(q(2,10,"Vùng lãnh thổ giải phóng trải dài đến đâu?","Chỉ vùng Mê Linh","Từ miền Bắc đến Bắc Trung Bộ","Cả nước","Chỉ đồng bằng sông Hồng",1,"hard"));

        // L11: Mã Viện đàn áp
        qs.add(q(2,11,"Ai đàn áp cuộc khởi nghĩa Hai Bà Trưng?","Tô Định","Mã Viện","Triệu Đà","Sĩ Nhiếp",1,"medium"));
        qs.add(q(2,11,"Mã Viện là tướng của triều đại nào?","Nhà Ngô","Nhà Đường","Nhà Hán","Nhà Tống",2,"easy"));
        qs.add(q(2,11,"Hai Bà Trưng tuẫn tiết năm nào?","40 SCN","42 SCN","43 SCN","45 SCN",2,"medium"));

        // L12: Đóng đô Mê Linh
        qs.add(q(2,12,"Hai Bà Trưng đóng đô ở đâu?","Cổ Loa","Mê Linh","Hoa Lư","Phong Châu",1,"easy"));
        qs.add(q(2,12,"Mê Linh ngày nay thuộc vùng nào?","Phú Thọ","Hà Nội","Bắc Ninh","Vĩnh Phúc",1,"medium"));
        qs.add(q(2,12,"Trưng Trắc là con gái của ai?","Lạc tướng Mê Linh","Thái thú Giao Chỉ","Vua Hùng cuối","Tướng quân Tây Vu",0,"hard"));

        // L13: Trận Bạch Đằng 938
        qs.add(q(2,13,"Trận Bạch Đằng năm 938 đánh bại quân nước nào?","Quân Đường","Quân Nam Hán","Quân Minh","Quân Tống",1,"easy"));
        qs.add(q(2,13,"Ai lãnh đạo trận Bạch Đằng 938?","Trần Hưng Đạo","Ngô Quyền","Lê Hoàn","Đinh Bộ Lĩnh",1,"easy"));
        qs.add(q(2,13,"Tướng giặc bị tiêu diệt trong trận Bạch Đằng 938 là ai?","Tô Định","Mã Viện","Hoằng Thao","Tôn Sĩ Nghị",2,"medium"));

        // L14: Quê hương Ngô Quyền
        qs.add(qf(2,14,2,"Ngô Quyền quê ở đâu?","Mê Linh","Đường Lâm","Thanh Hóa","Bình Định",1,"medium"));
        qs.add(qf(2,14,2,"Đường Lâm nay thuộc vùng nào?","Ba Vì, Hà Nội","Phú Thọ","Ninh Bình","Bắc Giang",0,"medium"));
        qs.add(qf(2,14,2,"Ngô Quyền là con rể của ai?","Phùng Hưng","Dương Đình Nghệ","Khúc Thừa Dụ","Đinh Bộ Lĩnh",1,"hard"));

        // L15: Chiến thuật cọc ngầm
        qs.add(q(2,15,"Chiến thuật của Ngô Quyền ở Bạch Đằng là gì?","Phục kích trên bộ","Cắm cọc ngầm dưới sông","Dùng hỏa công","Đánh lén ban đêm",1,"easy"));
        qs.add(q(2,15,"Chiến thuật cọc ngầm lợi dụng yếu tố gì?","Gió mùa","Thủy triều lên xuống","Sương mù","Dòng chảy xiết",1,"medium"));
        qs.add(q(2,15,"Cọc gỗ Bạch Đằng được đóng bằng loại gỗ gì?","Gỗ lim","Gỗ sến","Gỗ bọc đầu sắt nhọn","Gỗ tre",2,"hard"));

        // L16: Chấm dứt Bắc thuộc
        qs.add(q(2,16,"Chiến thắng Bạch Đằng 938 chấm dứt bao nhiêu năm Bắc thuộc?","500 năm","800 năm","1000 năm","1200 năm",2,"hard"));
        qs.add(q(2,16,"Sau chiến thắng 938, Ngô Quyền xưng gì?","Hoàng đế","Vương","Đại vương","Quốc chủ",1,"medium"));
        qs.add(q(2,16,"Chiến thắng 938 mở ra kỷ nguyên gì cho Việt Nam?","Kỷ nguyên độc lập tự chủ","Kỷ nguyên hòa bình","Kỷ nguyên mở cửa","Kỷ nguyên công nghiệp",0,"easy"));

        // L17: Nước Vạn Xuân
        qs.add(q(2,17,"Lý Bí dựng nước Vạn Xuân vào năm nào?","544","542","550","560",0,"hard"));
        qs.add(q(2,17,"Lý Bí xưng danh hiệu gì?","Vương","Lý Nam Đế","Quốc chủ","Đại tướng quân",1,"medium"));
        qs.add(q(2,17,"Nước Vạn Xuân tồn tại đến năm nào?","570","590","602","620",2,"hard"));

        // L18: Nữ vương đầu tiên
        qs.add(qf(2,18,1,"Ai là vị vua nữ đầu tiên trong lịch sử Việt Nam?","Bà Triệu","Trưng Trắc","Dương Vân Nga","Lý Chiêu Hoàng",1,"medium"));
        qs.add(q(2,18,"Trưng Trắc trị vì trong bao nhiêu năm?","1 năm","3 năm","5 năm","10 năm",1,"medium"));
        qs.add(q(2,18,"Đền thờ Hai Bà Trưng nổi tiếng nhất ở đâu?","Hà Tĩnh","Mê Linh, Hà Nội","Phú Thọ","Bắc Ninh",1,"easy"));

        // L19: Khởi nghĩa Bà Triệu
        qs.add(q(2,19,"Bà Triệu khởi nghĩa chống ách đô hộ của ai?","Nhà Hán","Nhà Ngô","Nhà Đường","Nhà Tống",1,"medium"));
        qs.add(q(2,19,"Bà Triệu quê ở vùng nào?","Nghệ An","Thanh Hóa","Hà Tĩnh","Bắc Ninh",1,"medium"));
        qs.add(q(2,19,"Khởi nghĩa Bà Triệu nổ ra vào năm nào?","220","248","268","300",1,"hard"));

        // L20: Ý chí độc lập
        qs.add(q(2,20,"Điểm chung của các cuộc khởi nghĩa thời Bắc thuộc là gì?","Đều thắng lợi","Thể hiện ý chí độc lập bất khuất","Đều do phụ nữ lãnh đạo","Đều dùng chiến thuật du kích",1,"easy"));
        qs.add(q(2,20,"Khởi nghĩa nào kết thúc hơn 1000 năm Bắc thuộc?","Khởi nghĩa Lý Bí","Khởi nghĩa Bà Triệu","Chiến thắng Bạch Đằng 938","Khởi nghĩa Mai Thúc Loan",2,"easy"));
        qs.add(q(2,20,"Thời Bắc thuộc kéo dài khoảng bao lâu?","500 năm","800 năm","Hơn 1000 năm","1500 năm",2,"medium"));

        // ===== PATH 3: Nhà Lý - Trần - Lê (lessonId 21–35) =====
        // L21: Lý Thái Tổ dời đô
        qs.add(qf(3,21,11,"Ai dời đô về Thăng Long năm 1010?","Ngô Quyền","Lý Thái Tổ","Trần Thái Tông","Lê Thái Tổ",1,"easy"));
        qs.add(q(3,21,"Lý Thái Tổ dời đô vào năm nào?","1009","1010","1015","1020",1,"easy"));
        qs.add(q(3,21,"Chiếu dời đô do ai viết?","Lý Thường Kiệt","Lý Công Uẩn","Nguyễn Trãi","Trần Thủ Độ",1,"medium"));

        // L22: Từ Hoa Lư đến Thăng Long
        qs.add(qf(3,22,11,"Lý Thái Tổ dời đô từ đâu về Thăng Long?","Cổ Loa","Hoa Lư","Phong Châu","Đại La",1,"medium"));
        qs.add(q(3,22,"Thăng Long có nghĩa là gì?","Đất rồng bay lên","Vùng đất vàng","Thành phố hoa","Miền đất hứa",0,"easy"));
        qs.add(q(3,22,"Hoa Lư nay thuộc tỉnh nào?","Hà Nam","Ninh Bình","Thanh Hóa","Nam Định",1,"easy"));

        // L23: Kháng chiến chống Nguyên Mông
        qs.add(qf(3,23,4,"Trần Hưng Đạo đánh thắng quân Nguyên Mông mấy lần?","1 lần","2 lần","3 lần","4 lần",2,"medium"));
        qs.add(q(3,23,"Ba lần kháng chiến chống Nguyên Mông diễn ra vào các năm nào?","1258, 1285, 1288","1250, 1275, 1300","1260, 1280, 1290","1255, 1270, 1285",0,"hard"));
        qs.add(q(3,23,"Hội nghị Diên Hồng diễn ra trong bối cảnh nào?","Chống quân Minh","Chống quân Nguyên Mông","Chống quân Tống","Chống quân Thanh",1,"medium"));

        // L24: Hịch tướng sĩ
        qs.add(qf(3,24,4,"Trần Hưng Đạo viết tác phẩm quân sự nào?","Binh thư yếu lược","Hịch tướng sĩ","Bình Ngô đại cáo","Nam quốc sơn hà",1,"medium"));
        qs.add(q(3,24,"Hịch tướng sĩ nhằm mục đích gì?","Cầu hòa với giặc","Kêu gọi tướng sĩ đánh giặc","Tuyên bố độc lập","Ban hành luật mới",1,"easy"));
        qs.add(q(3,24,"Hịch tướng sĩ được viết trong cuộc kháng chiến chống ai?","Quân Tống","Quân Minh","Quân Nguyên Mông","Quân Thanh",2,"medium"));

        // L25: Trần Quốc Tuấn
        qs.add(qf(3,25,4,"Tên thật của Trần Hưng Đạo là gì?","Trần Quốc Toản","Trần Quốc Tuấn","Trần Nhân Tông","Trần Thái Tông",1,"easy"));
        qs.add(qf(3,25,4,"Trần Hưng Đạo được phong tước gì?","Thái sư","Quốc công tiết chế","Đại tướng quân","Vương",1,"medium"));
        qs.add(qf(3,25,4,"Trần Hưng Đạo mất tại đâu?","Thăng Long","Vạn Kiếp","Hoa Lư","Tây Đô",1,"hard"));

        // L26: Khởi nghĩa Lam Sơn
        qs.add(qf(3,26,5,"Khởi nghĩa Lam Sơn kéo dài bao lâu?","5 năm","8 năm","10 năm","12 năm",2,"medium"));
        qs.add(qf(3,26,5,"Lê Lợi khởi nghĩa chống quân nào?","Quân Nguyên","Quân Tống","Quân Minh","Quân Thanh",2,"easy"));
        qs.add(q(3,26,"Khởi nghĩa Lam Sơn bắt đầu từ năm nào?","1410","1418","1425","1428",1,"medium"));

        // L27: Bình Ngô đại cáo
        qs.add(qf(3,27,6,"Ai viết Bình Ngô đại cáo?","Lê Lợi","Nguyễn Trãi","Nguyễn Du","Trần Hưng Đạo",1,"medium"));
        qs.add(q(3,27,"Bình Ngô đại cáo được coi là gì?","Bộ luật đầu tiên","Bản tuyên ngôn độc lập lần 2","Hiệp ước hòa bình","Binh pháp quân sự",1,"easy"));
        qs.add(q(3,27,"Bình Ngô đại cáo được viết vào năm nào?","1418","1425","1428","1430",2,"hard"));

        // L28: Nguyễn Trãi - Danh nhân UNESCO
        qs.add(qf(3,28,6,"Nguyễn Trãi được UNESCO công nhận Danh nhân văn hóa năm nào?","1975","1980","1990","2000",1,"hard"));
        qs.add(qf(3,28,6,"Nguyễn Trãi bị hại trong vụ án gì?","Vụ án Lệ Chi Viên","Vụ án tham nhũng","Vụ án phản quốc","Vụ án triều chính",0,"medium"));
        qs.add(qf(3,28,6,"Nguyễn Trãi quê ở đâu?","Thanh Hóa","Hải Dương","Nghệ An","Huế",1,"medium"));

        // L29: Văn Miếu Quốc Tử Giám
        qs.add(q(3,29,"Văn Miếu - Quốc Tử Giám được xây dựng thời nào?","Nhà Đinh","Nhà Lý","Nhà Trần","Nhà Lê",1,"hard"));
        qs.add(q(3,29,"Văn Miếu được xây dựng năm nào?","1070","1076","1054","1009",0,"medium"));
        qs.add(q(3,29,"Quốc Tử Giám được coi là gì?","Đền thờ Khổng Tử","Trường đại học đầu tiên","Viện nghiên cứu","Thư viện quốc gia",1,"easy"));

        // L30: Quang Trung đại phá quân Thanh
        qs.add(qf(3,30,7,"Quang Trung đại phá quân Thanh vào năm nào?","1785","1787","1789","1792",2,"medium"));
        qs.add(qf(3,30,7,"Trận đánh chính diễn ra ở đâu?","Đống Đa và Ngọc Hồi","Bạch Đằng","Chi Lăng","Rạch Gầm",0,"easy"));
        qs.add(qf(3,30,7,"Quang Trung đánh tan quân Thanh trong mấy ngày Tết?","3 ngày","5 ngày","7 ngày","10 ngày",1,"medium"));

        // L31: 29 vạn quân Thanh
        qs.add(qf(3,31,7,"Quang Trung đánh tan bao nhiêu quân Thanh?","10 vạn","20 vạn","29 vạn","50 vạn",2,"easy"));
        qs.add(q(3,31,"Tướng chỉ huy quân Thanh xâm lược là ai?","Mã Viện","Tôn Sĩ Nghị","Hoằng Thao","Sầm Nghi Đống",1,"medium"));
        qs.add(q(3,31,"Tướng giặc Sầm Nghi Đống chết ở đâu?","Ngọc Hồi","Đống Đa","Thăng Long","Hà Hồi",1,"hard"));

        // L32: Quê hương Nguyễn Huệ
        qs.add(qf(3,32,7,"Nguyễn Huệ quê ở đâu?","Thanh Hóa","Bình Định","Nghệ An","Huế",1,"easy"));
        qs.add(q(3,32,"Phong trào Tây Sơn có bao nhiêu anh em?","2 anh em","3 anh em","4 anh em","5 anh em",1,"easy"));
        qs.add(qf(3,32,7,"Nguyễn Huệ mất năm nào?","1789","1790","1792","1800",2,"hard"));

        // L33: Nam quốc sơn hà
        qs.add(qf(3,33,3,"Lý Thường Kiệt được coi là tác giả bài thơ nào?","Bình Ngô đại cáo","Hịch tướng sĩ","Nam quốc sơn hà","Cáo bình Ngô",2,"easy"));
        qs.add(q(3,33,"Nam quốc sơn hà được coi là gì?","Bản tuyên ngôn độc lập đầu tiên","Binh pháp quân sự","Bài thơ tình yêu","Hịch kêu gọi",0,"easy"));
        qs.add(q(3,33,"Bài thơ vang lên trong trận đánh nào?","Bạch Đằng","Sông Như Nguyệt","Chi Lăng","Đống Đa",1,"medium"));

        // L34: Lý Thường Kiệt phá Tống
        qs.add(qf(3,34,3,"Lý Thường Kiệt tấn công sang đất Tống năm nào?","1073","1075","1077","1079",1,"hard"));
        qs.add(qf(3,34,3,"Chiến lược của Lý Thường Kiệt gọi là gì?","Vườn không nhà trống","Tiên phát chế nhân","Du kích chiến","Phòng ngự tích cực",1,"medium"));
        qs.add(q(3,34,"Phòng tuyến nổi tiếng của Lý Thường Kiệt đặt ở đâu?","Sông Bạch Đằng","Sông Như Nguyệt","Sông Hồng","Sông Mã",1,"medium"));

        // L35: Tổng kết thời phong kiến
        qs.add(q(3,35,"Triều đại nào dời đô về Thăng Long?","Nhà Đinh","Nhà Lý","Nhà Trần","Nhà Lê",1,"easy"));
        qs.add(q(3,35,"Triều đại nào đánh thắng quân Nguyên Mông?","Nhà Lý","Nhà Trần","Nhà Lê","Nhà Nguyễn",1,"easy"));
        qs.add(q(3,35,"Bộ luật nổi tiếng thời Lê là gì?","Luật Gia Long","Luật Hồng Đức","Luật Hình thư","Luật Thiên trường",1,"medium"));

        // ===== PATH 4: Thời kỳ cận đại (lessonId 36–45) =====
        // L36: Pháp xâm lược Đà Nẵng
        qs.add(q(4,36,"Pháp tấn công Đà Nẵng năm nào?","1856","1858","1860","1862",1,"medium"));
        qs.add(q(4,36,"Liên quân Pháp đi cùng với quân nước nào?","Anh","Bồ Đào Nha","Tây Ban Nha","Hà Lan",2,"medium"));
        qs.add(q(4,36,"Pháp tấn công bán đảo nào ở Đà Nẵng?","Bán đảo Cà Mau","Bán đảo Sơn Trà","Bán đảo Cam Ranh","Bán đảo Đồ Sơn",1,"easy"));

        // L37: Thành lập Đảng CSVN
        qs.add(q(4,37,"Đảng Cộng sản Việt Nam thành lập ngày?","3/2/1930","19/8/1945","2/9/1945","7/5/1954",0,"easy"));
        qs.add(q(4,37,"Hội nghị hợp nhất diễn ra ở đâu?","Paris","Thượng Hải","Hồng Kông","Moskva",2,"medium"));
        qs.add(q(4,37,"Ai chủ trì hội nghị thành lập Đảng?","Trần Phú","Nguyễn Ái Quốc","Lê Hồng Phong","Hà Huy Tập",1,"easy"));

        // L38: Phong trào Đông Du
        qs.add(qf(4,38,10,"Phan Bội Châu khởi xướng phong trào gì?","Cần Vương","Đông Du","Duy Tân","Văn Thân",1,"easy"));
        qs.add(q(4,38,"Phong trào Đông Du đưa thanh niên sang nước nào?","Pháp","Trung Quốc","Nhật Bản","Thái Lan",2,"easy"));
        qs.add(q(4,38,"Phong trào Đông Du bắt đầu từ năm nào?","1900","1905","1910","1915",1,"medium"));

        // L39: Quê hương Phan Bội Châu
        qs.add(qf(4,39,10,"Phan Bội Châu quê ở đâu?","Hà Tĩnh","Nam Đàn, Nghệ An","Huế","Bình Định",1,"medium"));
        qs.add(qf(4,39,10,"Phan Bội Châu sáng lập tổ chức nào?","Việt Nam Quang phục Hội","Tân Việt Cách mạng Đảng","Hội Duy Tân","Cả A và C",3,"hard"));
        qs.add(qf(4,39,10,"Phan Bội Châu bị Pháp bắt ở đâu?","Hà Nội","Thượng Hải","Tokyo","Hong Kong",1,"hard"));

        // L40: Phong trào Cần Vương
        qs.add(q(4,40,"Phong trào Cần Vương nổ ra sau sự kiện nào?","Pháp chiếm Đà Nẵng","Kinh thành Huế thất thủ","Hòa ước Nhâm Tuất","Hòa ước Giáp Tuất",1,"hard"));
        qs.add(q(4,40,"Ai ra chiếu Cần Vương?","Tự Đức","Hàm Nghi","Thành Thái","Duy Tân",1,"medium"));
        qs.add(q(4,40,"Khởi nghĩa Hương Khê do ai lãnh đạo?","Hoàng Hoa Thám","Phan Đình Phùng","Đinh Công Tráng","Nguyễn Thiện Thuật",1,"hard"));

        // L41: Phong trào Duy Tân
        qs.add(q(4,41,"Phan Châu Trinh chủ trương phong trào gì?","Đông Du","Duy Tân","Cần Vương","Văn Thân",1,"medium"));
        qs.add(q(4,41,"Phong trào Duy Tân chủ trương gì?","Bạo động vũ trang","Cải cách dân chủ, khai dân trí","Cầu viện nước ngoài","Phục hồi chế độ quân chủ",1,"medium"));
        qs.add(q(4,41,"Phan Châu Trinh quê ở đâu?","Nghệ An","Quảng Nam","Huế","Hà Tĩnh",1,"medium"));

        // L42: Hồ Chí Minh ra đi tìm đường
        qs.add(qf(4,42,8,"Hồ Chí Minh ra đi tìm đường cứu nước năm nào?","1905","1908","1911","1920",2,"easy"));
        qs.add(q(4,42,"Bến cảng nào là nơi Bác Hồ ra đi?","Bến cảng Hải Phòng","Bến Nhà Rồng, Sài Gòn","Cảng Đà Nẵng","Cảng Vinh",1,"easy"));
        qs.add(q(4,42,"Khi ra đi, Bác lấy tên gì?","Nguyễn Ái Quốc","Nguyễn Tất Thành","Văn Ba","Lý Thụy",2,"medium"));

        // L43: Quê hương Bác Hồ
        qs.add(qf(4,43,8,"Hồ Chí Minh quê ở đâu?","Hà Tĩnh","Nam Đàn, Nghệ An","Huế","Quảng Bình",1,"easy"));
        qs.add(q(4,43,"Bác Hồ sinh năm nào?","1885","1890","1895","1900",1,"easy"));
        qs.add(q(4,43,"Ngôi nhà Bác Hồ sinh ra thuộc làng nào?","Làng Sen","Làng Hoa","Làng Kim Liên","Cả A và C đều đúng",3,"medium"));

        // L44: Các phong trào yêu nước
        qs.add(q(4,44,"Phong trào yêu nước nào theo khuynh hướng bạo động?","Duy Tân","Đông Du","Cần Vương","Cả B và C",3,"medium"));
        qs.add(q(4,44,"Ai lãnh đạo khởi nghĩa Yên Bái 1930?","Phan Bội Châu","Nguyễn Thái Học","Phan Châu Trinh","Lương Văn Can",1,"medium"));
        qs.add(q(4,44,"Tổ chức Việt Nam Quốc dân Đảng theo mô hình nước nào?","Nhật","Nga","Trung Quốc","Pháp",2,"hard"));

        // L45: Tổng kết thời cận đại
        qs.add(q(4,45,"Thời kỳ cận đại Việt Nam bắt đầu từ sự kiện nào?","Pháp xâm lược 1858","Thành lập Đảng 1930","Cách mạng tháng Tám","Chiến thắng Điện Biên Phủ",0,"easy"));
        qs.add(q(4,45,"Điểm chung của các phong trào trước 1930 là gì?","Đều thắng lợi","Đều thất bại do thiếu đường lối đúng","Đều theo chủ nghĩa cộng sản","Đều dùng vũ trang",1,"medium"));
        qs.add(q(4,45,"Sự kiện nào đánh dấu bước ngoặt cho cách mạng VN?","Phong trào Cần Vương","Thành lập Đảng CSVN 1930","Phong trào Đông Du","Khởi nghĩa Yên Bái",1,"medium"));

        // ===== PATH 5: Kháng chiến & Hiện đại (lessonId 46–55) =====
        // L46: Ngày Quốc khánh 2/9
        qs.add(qf(5,46,8,"Ngày Quốc khánh Việt Nam là ngày nào?","1/9/1945","2/9/1945","19/8/1945","30/4/1975",1,"easy"));
        qs.add(q(5,46,"Cách mạng Tháng Tám thành công vào tháng mấy?","Tháng 7/1945","Tháng 8/1945","Tháng 9/1945","Tháng 10/1945",1,"easy"));
        qs.add(q(5,46,"Cách mạng Tháng Tám lật đổ chế độ nào?","Thực dân Pháp","Phát xít Nhật và phong kiến","Đế quốc Mỹ","Chế độ quân phiệt",1,"medium"));

        // L47: Quảng trường Ba Đình
        qs.add(qf(5,47,8,"Hồ Chí Minh đọc Tuyên ngôn Độc lập ở đâu?","Ba Đình, Hà Nội","Huế","Sài Gòn","Hải Phòng",0,"easy"));
        qs.add(q(5,47,"Tuyên ngôn Độc lập mở đầu bằng trích dẫn từ nước nào?","Pháp","Mỹ","Anh","Cả A và B",3,"hard"));
        qs.add(q(5,47,"Có bao nhiêu người dự lễ Quốc khánh 2/9/1945?","Vài nghìn","Hàng chục vạn","Vài trăm","Hàng triệu",1,"medium"));

        // L48: Chiến thắng Điện Biên Phủ
        qs.add(qf(5,48,9,"Đại tướng nào chỉ huy chiến dịch Điện Biên Phủ?","Nguyễn Chí Thanh","Võ Nguyên Giáp","Văn Tiến Dũng","Lê Trọng Tấn",1,"easy"));
        qs.add(q(5,48,"Chiến dịch Điện Biên Phủ diễn ra vào năm nào?","1952","1953","1954","1955",2,"easy"));
        qs.add(q(5,48,"Chiến thắng Điện Biên Phủ buộc Pháp ký hiệp định gì?","Hiệp định Paris","Hiệp định Genève","Hiệp định Elysée","Hiệp định Fontainebleau",1,"medium"));

        // L49: Quê hương Võ Nguyên Giáp
        qs.add(qf(5,49,9,"Võ Nguyên Giáp quê ở đâu?","Nghệ An","Quảng Bình","Hà Tĩnh","Thanh Hóa",1,"medium"));
        qs.add(qf(5,49,9,"Võ Nguyên Giáp sinh năm nào?","1909","1911","1913","1915",1,"medium"));
        qs.add(qf(5,49,9,"Võ Nguyên Giáp từng làm nghề gì trước cách mạng?","Bác sĩ","Kỹ sư","Giáo viên","Nhà báo",2,"hard"));

        // L50: 56 ngày đêm Điện Biên
        qs.add(qf(5,50,9,"Chiến dịch Điện Biên Phủ kéo dài bao nhiêu ngày?","36 ngày","46 ngày","56 ngày","66 ngày",2,"medium"));
        qs.add(q(5,50,"Chiến dịch ĐBP bắt đầu ngày nào?","13/3/1954","7/5/1954","1/4/1954","20/2/1954",0,"hard"));
        qs.add(q(5,50,"Tập đoàn cứ điểm Điện Biên Phủ do ai xây dựng?","Quân Pháp","Quân Mỹ","Quân Anh","Liên quân",0,"easy"));

        // L51: Chiến dịch Hồ Chí Minh
        qs.add(q(5,51,"Chiến dịch giải phóng Sài Gòn có tên gọi là gì?","Chiến dịch Tây Nguyên","Chiến dịch Hồ Chí Minh","Chiến dịch Huế - Đà Nẵng","Chiến dịch Xuân 1975",1,"easy"));
        qs.add(q(5,51,"Chiến dịch Hồ Chí Minh diễn ra trong bao lâu?","5 ngày","10 ngày","15 ngày","30 ngày",0,"medium"));
        qs.add(q(5,51,"Xe tăng tiến vào Dinh Độc Lập mang số hiệu gì?","843 và 390","123 và 456","001 và 002","700 và 800",0,"hard"));

        // L52: Hiệp định Genève
        qs.add(q(5,52,"Hiệp định Genève được ký sau chiến thắng nào?","Bạch Đằng","Lam Sơn","Điện Biên Phủ","Giải phóng Sài Gòn",2,"medium"));
        qs.add(q(5,52,"Hiệp định Genève chia Việt Nam tại đâu?","Vĩ tuyến 15","Vĩ tuyến 17","Vĩ tuyến 20","Sông Gianh",1,"medium"));
        qs.add(q(5,52,"Hiệp định Genève ký vào ngày nào?","21/7/1954","7/5/1954","2/9/1945","30/4/1975",0,"hard"));

        // L53: Ngày thống nhất đất nước
        qs.add(q(5,53,"Ngày giải phóng miền Nam thống nhất đất nước?","2/9/1945","7/5/1954","30/4/1975","1/5/1975",2,"easy"));
        qs.add(q(5,53,"Sự kiện 30/4/1975 chấm dứt bao nhiêu năm chia cắt?","10 năm","15 năm","Hơn 20 năm","30 năm",2,"medium"));
        qs.add(q(5,53,"Tổng thống cuối cùng của VNCH là ai?","Nguyễn Văn Thiệu","Dương Văn Minh","Trần Văn Hương","Ngô Đình Diệm",1,"hard"));

        // L54: Napoléon Đông Dương
        qs.add(qf(5,54,9,"Võ Nguyên Giáp được mệnh danh là gì?","Thánh Gióng","Napoléon Đông Dương","Hổ tướng","Thần sấm",1,"easy"));
        qs.add(qf(5,54,9,"Võ Nguyên Giáp là Đại tướng đầu tiên được phong năm nào?","1945","1946","1948","1950",2,"medium"));
        qs.add(qf(5,54,9,"Võ Nguyên Giáp thọ bao nhiêu tuổi?","95","100","103","105",2,"medium"));

        // L55: Hồ Chí Minh - Danh nhân UNESCO
        qs.add(qf(5,55,8,"Hồ Chí Minh được UNESCO công nhận là gì?","Anh hùng dân tộc","Danh nhân văn hóa thế giới","Nhà lãnh đạo kiệt xuất","Tất cả đều đúng",1,"medium"));
        qs.add(qf(5,55,8,"Bác Hồ mất năm nào?","1968","1969","1970","1975",1,"easy"));
        qs.add(q(5,55,"Di chúc Bác Hồ để lại điều gì?","Kế hoạch quân sự","Mong muốn hòa bình, thống nhất","Chương trình kinh tế","Hiến pháp mới",1,"easy"));

        db.quizDao().insertAllQuestions(qs);
    }

    private static void seedEvents(AppDatabase db) {
        List<HistoryEventEntity> events = new ArrayList<>();

        HistoryEventEntity e1 = new HistoryEventEntity("40", "Khởi nghĩa Hai Bà Trưng",
            "Cuộc khởi nghĩa đầu tiên chống Bắc thuộc do Trưng Trắc và Trưng Nhị lãnh đạo.");
        e1.fullContent = "Khởi nghĩa Hai Bà Trưng (40–43 SCN) là cuộc khởi nghĩa đầu tiên trong lịch sử Việt Nam chống ách đô hộ của nhà Hán. Trưng Trắc và Trưng Nhị đã lãnh đạo nhân dân đánh đuổi thái thú Tô Định, giành lại 65 thành trì và xưng vương. Năm 43, nhà Hán cử Mã Viện đem quân đàn áp, Hai Bà Trưng anh dũng hy sinh.";
        e1.imageUrl = "event_hai_ba_trung";
        e1.location = "Mê Linh, Giao Chỉ"; e1.period = "Bắc thuộc"; e1.category = "Chiến tranh";
        e1.keyFigures = "Trưng Trắc,Trưng Nhị,Tô Định"; e1.yearNumeric = 40; events.add(e1);

        HistoryEventEntity e2 = new HistoryEventEntity("938", "Chiến thắng Bạch Đằng",
            "Ngô Quyền đánh bại quân Nam Hán, chấm dứt 1000 năm Bắc thuộc.");
        e2.fullContent = "Trận Bạch Đằng năm 938 là trận đánh trên sông Bạch Đằng do Ngô Quyền lãnh đạo, đánh bại quân xâm lược Nam Hán. Ông sử dụng chiến thuật cắm cọc ngầm dưới lòng sông, lợi dụng thủy triều để tiêu diệt đoàn thuyền chiến địch. Chiến thắng này chấm dứt hơn 1000 năm Bắc thuộc, mở ra kỷ nguyên độc lập cho dân tộc.";
        e2.imageUrl = "event_bach_dang_938";
        e2.location = "Sông Bạch Đằng"; e2.period = "Bắc thuộc"; e2.category = "Chiến tranh";
        e2.keyFigures = "Ngô Quyền,Hoằng Thao"; e2.yearNumeric = 938; events.add(e2);

        HistoryEventEntity e3 = new HistoryEventEntity("1010", "Lý Thái Tổ dời đô ra Thăng Long",
            "Lý Công Uẩn lên ngôi, dời đô về Thăng Long, mở ra thời kỳ phát triển rực rỡ.");
        e3.fullContent = "Năm 1009, Lý Công Uẩn lên ngôi vua, lập ra nhà Lý. Năm 1010, ông dời đô từ Hoa Lư về Đại La và đổi tên thành Thăng Long (Hà Nội ngày nay). Nhà Lý cai trị 216 năm (1009–1225), xây dựng bộ máy nhà nước vững mạnh, phát triển Phật giáo, xây Văn Miếu - Quốc Tử Giám.";
        e3.imageUrl = "event_doi_do_thang_long";
        e3.location = "Thăng Long"; e3.period = "Phong kiến"; e3.category = "Chính trị";
        e3.keyFigures = "Lý Công Uẩn,Lý Thái Tổ"; e3.yearNumeric = 1010; events.add(e3);

        HistoryEventEntity e4 = new HistoryEventEntity("1258", "Ba lần kháng chiến chống Nguyên Mông",
            "Nhà Trần ba lần đánh thắng quân xâm lược Nguyên Mông hùng mạnh nhất thế giới.");
        e4.fullContent = "Nhà Trần ba lần đánh bại quân Nguyên Mông (1258, 1285, 1288). Lần thứ ba, Trần Hưng Đạo sử dụng chiến thuật cọc Bạch Đằng tiêu diệt toàn bộ đoàn thuyền chiến của Ô Mã Nhi. Đây là chiến thắng vĩ đại trong lịch sử chống ngoại xâm.";
        e4.imageUrl = "event_chong_nguyen_mong";
        e4.location = "Toàn quốc"; e4.period = "Phong kiến"; e4.category = "Chiến tranh";
        e4.keyFigures = "Trần Hưng Đạo,Trần Nhân Tông,Trần Quốc Tuấn"; e4.yearNumeric = 1258; events.add(e4);

        HistoryEventEntity e5 = new HistoryEventEntity("1418", "Khởi nghĩa Lam Sơn",
            "Lê Lợi khởi nghĩa chống quân Minh, 10 năm kháng chiến giành độc lập.");
        e5.fullContent = "Khởi nghĩa Lam Sơn (1418–1427) do Lê Lợi lãnh đạo, với sự phò tá của Nguyễn Trãi. Trải qua 10 năm kháng chiến gian khổ, nghĩa quân đã đánh đuổi quân Minh, giành lại độc lập. Nguyễn Trãi viết Bình Ngô đại cáo - bản tuyên ngôn độc lập lần thứ hai.";
        e5.imageUrl = "event_lam_son";
        e5.location = "Lam Sơn, Thanh Hóa"; e5.period = "Phong kiến"; e5.category = "Chiến tranh";
        e5.keyFigures = "Lê Lợi,Nguyễn Trãi"; e5.yearNumeric = 1418; events.add(e5);

        HistoryEventEntity e6 = new HistoryEventEntity("1789", "Quang Trung đại phá quân Thanh",
            "Nguyễn Huệ thần tốc tiến quân, đại phá 29 vạn quân Thanh trong 5 ngày.");
        e6.fullContent = "Cuối năm 1788, 29 vạn quân Thanh do Tôn Sĩ Nghị chỉ huy tràn vào chiếm Thăng Long. Nguyễn Huệ (Quang Trung) lên ngôi hoàng đế tại Phú Xuân, thần tốc hành quân ra Bắc. Đêm 30 Tết đến mùng 5 Tết Kỷ Dậu (1789), quân Tây Sơn đại phá quân Thanh tại Ngọc Hồi – Đống Đa, giải phóng Thăng Long.";
        e6.imageUrl = "event_quang_trung";
        e6.location = "Ngọc Hồi – Đống Đa, Thăng Long"; e6.period = "Phong kiến"; e6.category = "Chiến tranh";
        e6.keyFigures = "Nguyễn Huệ,Quang Trung,Tôn Sĩ Nghị"; e6.yearNumeric = 1789; events.add(e6);

        HistoryEventEntity e7 = new HistoryEventEntity("1858", "Pháp nổ súng xâm lược Việt Nam",
            "Liên quân Pháp - Tây Ban Nha tấn công Đà Nẵng, mở đầu thời kỳ thực dân.");
        e7.fullContent = "Ngày 1/9/1858, liên quân Pháp - Tây Ban Nha tấn công bán đảo Sơn Trà (Đà Nẵng), bắt đầu cuộc xâm lược Việt Nam. Triều đình nhà Nguyễn dần dần nhượng bộ qua các hòa ước, dẫn đến mất nước. Nhiều phong trào yêu nước nổ ra nhưng đều thất bại.";
        e7.imageUrl = "event_phap_xam_luoc";
        e7.location = "Đà Nẵng"; e7.period = "Cận đại"; e7.category = "Chiến tranh";
        e7.keyFigures = "Nguyễn Tri Phương,Tự Đức"; e7.yearNumeric = 1858; events.add(e7);

        HistoryEventEntity e8 = new HistoryEventEntity("1930", "Thành lập Đảng Cộng sản Việt Nam",
            "Nguyễn Ái Quốc thống nhất các tổ chức cộng sản, lãnh đạo phong trào cách mạng.");
        e8.fullContent = "Ngày 3/2/1930, tại Hồng Kông, Nguyễn Ái Quốc (Hồ Chí Minh) chủ trì hội nghị hợp nhất các tổ chức cộng sản thành Đảng Cộng sản Việt Nam. Sự kiện này đánh dấu bước ngoặt trong phong trào đấu tranh giải phóng dân tộc.";
        e8.imageUrl = "event_thanh_lap_dang";
        e8.location = "Hồng Kông"; e8.period = "Cận đại"; e8.category = "Chính trị";
        e8.keyFigures = "Nguyễn Ái Quốc,Hồ Chí Minh"; e8.yearNumeric = 1930; events.add(e8);

        HistoryEventEntity e9 = new HistoryEventEntity("1945", "Cách mạng Tháng Tám",
            "Nhân dân Việt Nam đứng lên giành chính quyền, tuyên bố độc lập 2/9/1945.");
        e9.fullContent = "Tháng 8/1945, nhân dân Việt Nam dưới sự lãnh đạo của Việt Minh đã đứng lên tổng khởi nghĩa giành chính quyền trên toàn quốc. Ngày 2/9/1945, tại Quảng trường Ba Đình (Hà Nội), Chủ tịch Hồ Chí Minh đọc Tuyên ngôn Độc lập, khai sinh nước Việt Nam Dân chủ Cộng hòa.";
        e9.imageUrl = "event_cach_mang_thang_tam";
        e9.location = "Hà Nội"; e9.period = "Hiện đại"; e9.category = "Chính trị";
        e9.keyFigures = "Hồ Chí Minh,Võ Nguyên Giáp"; e9.yearNumeric = 1945; events.add(e9);

        HistoryEventEntity e10 = new HistoryEventEntity("1954", "Chiến thắng Điện Biên Phủ",
            "Chiến thắng lẫy lừng chấm dứt ách thống trị của thực dân Pháp tại Đông Dương.");
        e10.fullContent = "Chiến dịch Điện Biên Phủ (13/3 – 7/5/1954) là trận đánh quyết định giữa Quân đội Nhân dân Việt Nam và quân viễn chinh Pháp. Dưới sự chỉ huy của Đại tướng Võ Nguyên Giáp, quân ta đã đánh bại tập đoàn cứ điểm Điện Biên Phủ, buộc Pháp phải ký Hiệp định Genève.";
        e10.imageUrl = "event_dien_bien_phu";
        e10.location = "Điện Biên Phủ"; e10.period = "Hiện đại"; e10.category = "Chiến tranh";
        e10.keyFigures = "Võ Nguyên Giáp,Hồ Chí Minh"; e10.yearNumeric = 1954; events.add(e10);

        HistoryEventEntity e11 = new HistoryEventEntity("1975", "Giải phóng miền Nam, thống nhất đất nước",
            "Chiến dịch Hồ Chí Minh toàn thắng, thống nhất Việt Nam ngày 30/4/1975.");
        e11.fullContent = "Chiến dịch Hồ Chí Minh (26–30/4/1975) là chiến dịch cuối cùng trong cuộc kháng chiến chống Mỹ. Ngày 30/4/1975, xe tăng của quân giải phóng tiến vào Dinh Độc Lập, chấm dứt chiến tranh, thống nhất đất nước sau hơn 20 năm chia cắt.";
        e11.imageUrl = "event_giai_phong_mien_nam";
        e11.location = "Sài Gòn"; e11.period = "Hiện đại"; e11.category = "Chiến tranh";
        e11.keyFigures = "Văn Tiến Dũng,Lê Duẩn"; e11.yearNumeric = 1975; events.add(e11);

        HistoryEventEntity e12 = new HistoryEventEntity("~2879 TCN", "Nhà nước Văn Lang",
            "Hùng Vương lập nhà nước Văn Lang – nhà nước đầu tiên của người Việt.");
        e12.fullContent = "Theo truyền thuyết, Lạc Long Quân và Âu Cơ sinh ra trăm trứng, nở thành trăm người con. Người con trưởng lên ngôi, xưng Hùng Vương, đặt tên nước là Văn Lang, đóng đô ở Phong Châu (Phú Thọ ngày nay). Nhà nước Văn Lang tồn tại qua 18 đời Hùng Vương, với nền văn minh lúa nước và trống đồng Đông Sơn rực rỡ.";
        e12.imageUrl = "event_van_lang";
        e12.location = "Phong Châu, Phú Thọ"; e12.period = "Dựng nước"; e12.category = "Chính trị";
        e12.keyFigures = "Hùng Vương,Lạc Long Quân,Âu Cơ"; e12.yearNumeric = -2879; events.add(e12);

        HistoryEventEntity e13 = new HistoryEventEntity("~257 TCN", "Nhà nước Âu Lạc",
            "Thục Phán thống nhất Âu Việt và Lạc Việt, lập nước Âu Lạc.");
        e13.fullContent = "Khoảng năm 257 TCN, Thục Phán đánh bại Hùng Vương cuối cùng, thống nhất hai bộ tộc Âu Việt và Lạc Việt thành nước Âu Lạc. Ông xưng là An Dương Vương, đóng đô tại Cổ Loa (Đông Anh, Hà Nội ngày nay).";
        e13.imageUrl = "event_au_lac";
        e13.location = "Cổ Loa, Đông Anh"; e13.period = "Dựng nước"; e13.category = "Chính trị";
        e13.keyFigures = "An Dương Vương,Thục Phán"; e13.yearNumeric = -257; events.add(e13);

        HistoryEventEntity e14 = new HistoryEventEntity("~255 TCN", "An Dương Vương xây thành Cổ Loa",
            "Thành Cổ Loa được xây dựng với kiến trúc xoắn ốc độc đáo, là kinh đô Âu Lạc.");
        e14.fullContent = "An Dương Vương cho xây dựng thành Cổ Loa – công trình quân sự quy mô lớn nhất thời bấy giờ ở Đông Nam Á. Thành có 9 vòng xoắn ốc (hiện còn dấu tích 3 vòng), chu vi vòng ngoài khoảng 8km.";
        e14.imageUrl = "event_co_loa";
        e14.location = "Cổ Loa, Đông Anh"; e14.period = "Dựng nước"; e14.category = "Văn hóa";
        e14.keyFigures = "An Dương Vương,Mỵ Châu,Trọng Thủy"; e14.yearNumeric = -255; events.add(e14);

        HistoryEventEntity e15 = new HistoryEventEntity("248", "Khởi nghĩa Bà Triệu",
            "Triệu Thị Trinh lãnh đạo khởi nghĩa chống ách đô hộ nhà Ngô.");
        e15.fullContent = "Năm 248, Triệu Thị Trinh (Bà Triệu) cùng anh trai Triệu Quốc Đạt khởi nghĩa chống ách đô hộ của nhà Ngô.";
        e15.imageUrl = "event_ba_trieu";
        e15.location = "Thanh Hóa"; e15.period = "Bắc thuộc"; e15.category = "Chiến tranh";
        e15.keyFigures = "Triệu Thị Trinh,Triệu Quốc Đạt"; e15.yearNumeric = 248; events.add(e15);

        HistoryEventEntity e16 = new HistoryEventEntity("544", "Lý Bí lập nước Vạn Xuân",
            "Lý Bí khởi nghĩa thành công, xưng đế, đặt quốc hiệu Vạn Xuân.");
        e16.fullContent = "Năm 542, Lý Bí khởi nghĩa chống nhà Lương. Năm 544, ông xưng đế (Lý Nam Đế), đặt quốc hiệu là Vạn Xuân. Nước Vạn Xuân tồn tại đến năm 602.";
        e16.imageUrl = "event_van_xuan";
        e16.location = "Thăng Long"; e16.period = "Bắc thuộc"; e16.category = "Chính trị";
        e16.keyFigures = "Lý Bí,Lý Nam Đế,Triệu Quang Phục"; e16.yearNumeric = 544; events.add(e16);

        HistoryEventEntity e17 = new HistoryEventEntity("968", "Đinh Bộ Lĩnh dẹp loạn 12 sứ quân",
            "Đinh Bộ Lĩnh thống nhất đất nước, lập nhà Đinh, đặt quốc hiệu Đại Cồ Việt.");
        e17.fullContent = "Sau khi Ngô Quyền mất (944), đất nước rơi vào tình trạng loạn 12 sứ quân. Đinh Bộ Lĩnh từ động Hoa Lư đã dẹp yên các sứ quân, thống nhất đất nước năm 968.";
        e17.imageUrl = "event_dinh_bo_linh";
        e17.location = "Hoa Lư, Ninh Bình"; e17.period = "Phong kiến"; e17.category = "Chính trị";
        e17.keyFigures = "Đinh Bộ Lĩnh,Đinh Tiên Hoàng"; e17.yearNumeric = 968; events.add(e17);

        HistoryEventEntity e18 = new HistoryEventEntity("981", "Lê Hoàn kháng chiến chống Tống lần 1",
            "Lê Hoàn lãnh đạo quân dân đánh bại quân Tống xâm lược.");
        e18.fullContent = "Năm 981, nhà Tống phát động chiến tranh xâm lược Đại Cồ Việt. Lê Hoàn đích thân cầm quân đánh bại quân Tống tại Chi Lăng.";
        e18.imageUrl = "event_le_hoan_chong_tong";
        e18.location = "Chi Lăng, Lạng Sơn"; e18.period = "Phong kiến"; e18.category = "Chiến tranh";
        e18.keyFigures = "Lê Hoàn,Lê Đại Hành"; e18.yearNumeric = 981; events.add(e18);

        HistoryEventEntity e19 = new HistoryEventEntity("1075", "Lý Thường Kiệt kháng chiến chống Tống lần 2",
            "Lý Thường Kiệt \"Tiên phát chế nhân\", lập phòng tuyến sông Như Nguyệt.");
        e19.fullContent = "Năm 1075, Lý Thường Kiệt thực hiện chiến lược \"Tiên phát chế nhân\", tấn công các căn cứ của Tống. Năm 1077, quân Tống bị chặn tại phòng tuyến sông Như Nguyệt. Bài thơ \"Nam quốc sơn hà\" vang lên.";
        e19.imageUrl = "event_ly_thuong_kiet";
        e19.location = "Sông Như Nguyệt"; e19.period = "Phong kiến"; e19.category = "Chiến tranh";
        e19.keyFigures = "Lý Thường Kiệt"; e19.yearNumeric = 1075; events.add(e19);

        HistoryEventEntity e20 = new HistoryEventEntity("1428", "Nguyễn Trãi viết Bình Ngô đại cáo",
            "Bản tuyên ngôn độc lập lần thứ hai, khẳng định chủ quyền dân tộc.");
        e20.fullContent = "Sau khi khởi nghĩa Lam Sơn toàn thắng (1428), Nguyễn Trãi thay mặt Lê Lợi viết \"Bình Ngô đại cáo\" – bản tuyên ngôn độc lập lần thứ hai.";
        e20.imageUrl = "event_binh_ngo_dai_cao";
        e20.location = "Thăng Long"; e20.period = "Phong kiến"; e20.category = "Văn hóa";
        e20.keyFigures = "Nguyễn Trãi,Lê Lợi"; e20.yearNumeric = 1428; events.add(e20);

        HistoryEventEntity e21 = new HistoryEventEntity("1802", "Nhà Nguyễn thống nhất đất nước",
            "Nguyễn Ánh lập nhà Nguyễn, thống nhất Việt Nam, đặt quốc hiệu Việt Nam.");
        e21.fullContent = "Năm 1802, Nguyễn Ánh đánh bại nhà Tây Sơn, lên ngôi hoàng đế (Gia Long), lập ra nhà Nguyễn – triều đại phong kiến cuối cùng.";
        e21.imageUrl = "event_nha_nguyen";
        e21.location = "Phú Xuân, Huế"; e21.period = "Phong kiến"; e21.category = "Chính trị";
        e21.keyFigures = "Nguyễn Ánh,Gia Long"; e21.yearNumeric = 1802; events.add(e21);

        HistoryEventEntity e22 = new HistoryEventEntity("1885", "Phong trào Cần Vương",
            "Vua Hàm Nghi ra chiếu Cần Vương, kêu gọi toàn dân chống Pháp.");
        e22.fullContent = "Năm 1885, Tôn Thất Thuyết đưa vua Hàm Nghi rời kinh thành, phát chiếu Cần Vương kêu gọi nhân dân cả nước đứng lên chống Pháp.";
        e22.imageUrl = "event_can_vuong";
        e22.location = "Huế và cả nước"; e22.period = "Cận đại"; e22.category = "Chiến tranh";
        e22.keyFigures = "Hàm Nghi,Tôn Thất Thuyết,Phan Đình Phùng"; e22.yearNumeric = 1885; events.add(e22);

        HistoryEventEntity e23 = new HistoryEventEntity("2/9/1945", "Quốc khánh – Tuyên ngôn Độc lập",
            "Chủ tịch Hồ Chí Minh đọc Tuyên ngôn Độc lập, khai sinh nước Việt Nam DCCH.");
        e23.fullContent = "Ngày 2 tháng 9 năm 1945, tại Quảng trường Ba Đình, Chủ tịch Hồ Chí Minh đọc Tuyên ngôn Độc lập, khai sinh nước Việt Nam Dân chủ Cộng hòa.";
        e23.imageUrl = "event_quoc_khanh";
        e23.location = "Quảng trường Ba Đình, Hà Nội"; e23.period = "Hiện đại"; e23.category = "Chính trị";
        e23.keyFigures = "Hồ Chí Minh"; e23.yearNumeric = 1945; events.add(e23);

        HistoryEventEntity e24 = new HistoryEventEntity("1954", "Hiệp định Genève",
            "Hiệp định chấm dứt chiến tranh Đông Dương, tạm chia Việt Nam tại vĩ tuyến 17.");
        e24.fullContent = "Ngày 21/7/1954, Hiệp định Genève được ký kết, công nhận độc lập, chủ quyền, thống nhất và toàn vẹn lãnh thổ của Việt Nam.";
        e24.imageUrl = "event_hiep_dinh_geneve";
        e24.location = "Genève, Thụy Sĩ"; e24.period = "Hiện đại"; e24.category = "Chính trị";
        e24.keyFigures = "Phạm Văn Đồng"; e24.yearNumeric = 1954; events.add(e24);

        HistoryEventEntity e25 = new HistoryEventEntity("1960", "Phong trào Đồng Khởi",
            "Nhân dân miền Nam đồng loạt nổi dậy chống chế độ Mỹ – Diệm.");
        e25.fullContent = "Năm 1959-1960, nhân dân miền Nam đồng loạt nổi dậy. Phong trào bắt đầu từ Bến Tre do bà Nguyễn Thị Định lãnh đạo.";
        e25.imageUrl = "event_dong_khoi";
        e25.location = "Bến Tre, miền Nam"; e25.period = "Hiện đại"; e25.category = "Chiến tranh";
        e25.keyFigures = "Nguyễn Thị Định"; e25.yearNumeric = 1960; events.add(e25);

        HistoryEventEntity e26 = new HistoryEventEntity("1968", "Tổng tiến công Tết Mậu Thân",
            "Quân giải phóng đồng loạt tấn công vào các đô thị miền Nam.");
        e26.fullContent = "Đêm 30 Tết Mậu Thân (31/1/1968), quân giải phóng đồng loạt tấn công hơn 100 đô thị. Tết Mậu Thân là bước ngoặt chiến lược.";
        e26.imageUrl = "event_tet_mau_than";
        e26.location = "Toàn miền Nam"; e26.period = "Hiện đại"; e26.category = "Chiến tranh";
        e26.keyFigures = "Lê Duẩn,Võ Nguyên Giáp"; e26.yearNumeric = 1968; events.add(e26);

        db.historyEventDao().insertAll(events);
    }

    private static void seedLearningPaths(AppDatabase db) {
        List<LearningPathEntity> paths = new ArrayList<>();
        paths.add(new LearningPathEntity("Thời kỳ dựng nước", "\uD83C\uDFDB\uFE0F", 8, 1));
        paths.add(new LearningPathEntity("Nghìn năm Bắc thuộc", "\u2694\uFE0F", 12, 2));
        paths.add(new LearningPathEntity("Nhà Lý - Trần - Lê", "\uD83D\uDC51", 15, 3));
        paths.add(new LearningPathEntity("Thời kỳ cận đại", "\uD83D\uDCDC", 10, 4));
        paths.add(new LearningPathEntity("Kháng chiến & Hiện đại", "\u2B50", 10, 5));
        db.learningDao().insertAllPaths(paths);
    }

    private static void seedLessons(AppDatabase db) {
        List<LessonEntity> lessons = new ArrayList<>();

        // Path 1: Thời kỳ dựng nước (8 lessons, IDs 1-8)
        lessons.add(new LessonEntity(1, "Nguồn gốc dân tộc Việt", "Tìm hiểu về Lạc Long Quân và Âu Cơ", 1));
        lessons.add(new LessonEntity(1, "Nhà nước Văn Lang", "Hùng Vương và nền văn minh lúa nước", 2));
        lessons.add(new LessonEntity(1, "Thành Cổ Loa", "An Dương Vương xây thành Cổ Loa", 3));
        lessons.add(new LessonEntity(1, "Nỏ thần Kim Quy", "Vũ khí huyền thoại của Âu Lạc", 4));
        lessons.add(new LessonEntity(1, "Văn hóa Đông Sơn", "Trống đồng và nền văn hóa rực rỡ", 5));
        lessons.add(new LessonEntity(1, "Nước Âu Lạc", "Thục Phán thống nhất Âu Việt - Lạc Việt", 6));
        lessons.add(new LessonEntity(1, "Giỗ Tổ Hùng Vương", "Truyền thống uống nước nhớ nguồn", 7));
        lessons.add(new LessonEntity(1, "Kinh đô Phong Châu", "Trung tâm chính trị Văn Lang", 8));

        // Path 2: Nghìn năm Bắc thuộc (12 lessons, IDs 9-20)
        lessons.add(new LessonEntity(2, "Khởi nghĩa Hai Bà Trưng", "Cuộc khởi nghĩa đầu tiên chống ngoại xâm", 1));
        lessons.add(new LessonEntity(2, "65 thành trì giải phóng", "Chiến công hiển hách của Hai Bà Trưng", 2));
        lessons.add(new LessonEntity(2, "Mã Viện đàn áp", "Nhà Hán đàn áp khởi nghĩa", 3));
        lessons.add(new LessonEntity(2, "Đóng đô Mê Linh", "Trưng Trắc xưng vương", 4));
        lessons.add(new LessonEntity(2, "Trận Bạch Đằng 938", "Ngô Quyền đánh bại quân Nam Hán", 5));
        lessons.add(new LessonEntity(2, "Quê hương Ngô Quyền", "Đường Lâm - đất hai vua", 6));
        lessons.add(new LessonEntity(2, "Chiến thuật cọc ngầm", "Chiến lược thiên tài trên sông Bạch Đằng", 7));
        lessons.add(new LessonEntity(2, "Chấm dứt Bắc thuộc", "1000 năm đô hộ kết thúc", 8));
        lessons.add(new LessonEntity(2, "Nước Vạn Xuân", "Lý Bí xưng đế năm 544", 9));
        lessons.add(new LessonEntity(2, "Nữ vương đầu tiên", "Trưng Trắc - vị vua nữ đầu tiên", 10));
        lessons.add(new LessonEntity(2, "Khởi nghĩa Bà Triệu", "Triệu Thị Trinh chống nhà Ngô", 11));
        lessons.add(new LessonEntity(2, "Ý chí độc lập", "Tổng kết thời kỳ Bắc thuộc", 12));

        // Path 3: Nhà Lý - Trần - Lê (15 lessons, IDs 21-35)
        lessons.add(new LessonEntity(3, "Lý Thái Tổ dời đô", "Dời đô về Thăng Long năm 1010", 1));
        lessons.add(new LessonEntity(3, "Từ Hoa Lư đến Thăng Long", "Ý nghĩa của việc dời đô", 2));
        lessons.add(new LessonEntity(3, "Kháng chiến chống Nguyên Mông", "Ba lần đánh thắng quân xâm lược", 3));
        lessons.add(new LessonEntity(3, "Hịch tướng sĩ", "Tác phẩm quân sự của Trần Hưng Đạo", 4));
        lessons.add(new LessonEntity(3, "Trần Quốc Tuấn", "Cuộc đời và sự nghiệp", 5));
        lessons.add(new LessonEntity(3, "Khởi nghĩa Lam Sơn", "10 năm kháng chiến chống quân Minh", 6));
        lessons.add(new LessonEntity(3, "Bình Ngô đại cáo", "Bản tuyên ngôn độc lập lần hai", 7));
        lessons.add(new LessonEntity(3, "Nguyễn Trãi - Danh nhân UNESCO", "Nhà văn hóa kiệt xuất", 8));
        lessons.add(new LessonEntity(3, "Văn Miếu Quốc Tử Giám", "Trường đại học đầu tiên", 9));
        lessons.add(new LessonEntity(3, "Quang Trung đại phá quân Thanh", "Chiến thắng thần tốc năm 1789", 10));
        lessons.add(new LessonEntity(3, "29 vạn quân Thanh", "Trận Ngọc Hồi - Đống Đa", 11));
        lessons.add(new LessonEntity(3, "Quê hương Nguyễn Huệ", "Tây Sơn, Bình Định", 12));
        lessons.add(new LessonEntity(3, "Nam quốc sơn hà", "Bản tuyên ngôn độc lập đầu tiên", 13));
        lessons.add(new LessonEntity(3, "Lý Thường Kiệt phá Tống", "Tiên phát chế nhân năm 1075", 14));
        lessons.add(new LessonEntity(3, "Tổng kết thời phong kiến", "Ôn tập kiến thức Lý - Trần - Lê", 15));

        // Path 4: Thời kỳ cận đại (10 lessons, IDs 36-45)
        lessons.add(new LessonEntity(4, "Pháp xâm lược Đà Nẵng", "Sự kiện 1858 mở đầu thời thực dân", 1));
        lessons.add(new LessonEntity(4, "Thành lập Đảng CSVN", "Ngày 3/2/1930 tại Hồng Kông", 2));
        lessons.add(new LessonEntity(4, "Phong trào Đông Du", "Phan Bội Châu đưa thanh niên sang Nhật", 3));
        lessons.add(new LessonEntity(4, "Quê hương Phan Bội Châu", "Nam Đàn, Nghệ An", 4));
        lessons.add(new LessonEntity(4, "Phong trào Cần Vương", "Hàm Nghi ra chiếu Cần Vương", 5));
        lessons.add(new LessonEntity(4, "Phong trào Duy Tân", "Phan Châu Trinh cải cách xã hội", 6));
        lessons.add(new LessonEntity(4, "Hồ Chí Minh ra đi tìm đường", "Năm 1911 từ bến Nhà Rồng", 7));
        lessons.add(new LessonEntity(4, "Quê hương Bác Hồ", "Nam Đàn, Nghệ An", 8));
        lessons.add(new LessonEntity(4, "Các phong trào yêu nước", "Tổng quan phong trào cận đại", 9));
        lessons.add(new LessonEntity(4, "Tổng kết thời cận đại", "Ôn tập kiến thức cận đại", 10));

        // Path 5: Kháng chiến & Hiện đại (10 lessons, IDs 46-55)
        lessons.add(new LessonEntity(5, "Ngày Quốc khánh 2/9", "Tuyên ngôn Độc lập 1945", 1));
        lessons.add(new LessonEntity(5, "Quảng trường Ba Đình", "Nơi khai sinh nước Việt Nam mới", 2));
        lessons.add(new LessonEntity(5, "Chiến thắng Điện Biên Phủ", "Đại tướng Võ Nguyên Giáp chỉ huy", 3));
        lessons.add(new LessonEntity(5, "Quê hương Võ Nguyên Giáp", "Lệ Thủy, Quảng Bình", 4));
        lessons.add(new LessonEntity(5, "56 ngày đêm Điện Biên", "Chiến dịch lịch sử", 5));
        lessons.add(new LessonEntity(5, "Chiến dịch Hồ Chí Minh", "Giải phóng Sài Gòn 30/4/1975", 6));
        lessons.add(new LessonEntity(5, "Hiệp định Genève", "Kết quả sau Điện Biên Phủ", 7));
        lessons.add(new LessonEntity(5, "Ngày thống nhất đất nước", "30/4/1975 kết thúc chiến tranh", 8));
        lessons.add(new LessonEntity(5, "Napoléon Đông Dương", "Biệt danh của Võ Nguyên Giáp", 9));
        lessons.add(new LessonEntity(5, "Hồ Chí Minh - Danh nhân UNESCO", "Di sản tinh thần Bác Hồ", 10));

        db.learningDao().insertAllLessons(lessons);
    }

    private static void seedFlashcards(AppDatabase db) {
        List<FlashcardEntity> cards = new ArrayList<>();
        String[][] data = {
            {"Ai lãnh đạo khởi nghĩa chống quân Hán năm 40 SCN?", "Trưng Trắc và Trưng Nhị (Hai Bà Trưng)", "Bắc thuộc"},
            {"Trận Bạch Đằng năm 938 do ai chỉ huy?", "Ngô Quyền", "Bắc thuộc"},
            {"Ai dời đô về Thăng Long năm 1010?", "Lý Thái Tổ (Lý Công Uẩn)", "Phong kiến"},
            {"Nhà Trần đánh thắng quân Nguyên Mông mấy lần?", "3 lần (1258, 1285, 1288)", "Phong kiến"},
            {"Tác giả Bình Ngô đại cáo là ai?", "Nguyễn Trãi", "Phong kiến"},
            {"Quang Trung đại phá quân Thanh năm nào?", "1789 - Trận Ngọc Hồi - Đống Đa", "Phong kiến"},
            {"Hiệp định Genève ký năm nào?", "1954, sau chiến thắng Điện Biên Phủ", "Hiện đại"},
            {"Ngày thống nhất đất nước?", "30/4/1975", "Hiện đại"},
            {"Chiến thuật cọc ngầm Bạch Đằng lợi dụng gì?", "Lợi dụng thủy triều lên xuống", "Bắc thuộc"},
            {"Quốc Tử Giám được xây năm nào?", "1076, thời Lý Nhân Tông", "Phong kiến"},
            {"Ai là vị vua nữ đầu tiên của Việt Nam?", "Trưng Trắc (Trưng Vương)", "Bắc thuộc"},
            {"Kinh đô đầu tiên sau thời Bắc thuộc?", "Cổ Loa, sau đó là Hoa Lư", "Bắc thuộc"},
            {"Hồ Chí Minh đọc Tuyên ngôn Độc lập ở đâu?", "Quảng trường Ba Đình, Hà Nội", "Hiện đại"},
            {"Võ Nguyên Giáp được mệnh danh là gì?", "Đại tướng của nhân dân, Napoleon Đông Dương", "Hiện đại"},
            {"Nhà Nguyễn đặt kinh đô ở đâu?", "Huế (Phú Xuân)", "Phong kiến"},
        };
        for (int i = 0; i < data.length; i++) {
            FlashcardEntity card = new FlashcardEntity(data[i][0], data[i][1]);
            card.category = data[i][2]; card.eventId = (i % 11) + 1; cards.add(card);
        }
        db.flashcardDao().insertAll(cards);
    }

    private static void seedPosts(AppDatabase db) {
        PostEntity p1 = new PostEntity(0, "Minh Anh",
            "Các bạn thấy giai đoạn nào trong lịch sử VN hấp dẫn nhất? Mình rất thích thời Trần với 3 lần đánh Nguyên Mông! \uD83C\uDFEF", "Thảo luận");
        p1.likes = 24; p1.commentsCount = 4; p1.createdAt = System.currentTimeMillis() - 7200000;
        db.communityDao().insertPost(p1);
        PostEntity p2 = new PostEntity(0, "Hoàng Nam",
            "Vừa đọc xong bài về Hai Bà Trưng trên app, chi tiết hơn sách giáo khoa nhiều. Recommend mọi người đọc! \uD83D\uDCDA", "Đánh giá");
        p2.likes = 42; p2.commentsCount = 0; p2.createdAt = System.currentTimeMillis() - 18000000;
        db.communityDao().insertPost(p2);
        PostEntity p3 = new PostEntity(0, "Thu Hà",
            "Quiz về thời Lý khó quá, ai được điểm cao chia sẻ tips với ạ \uD83D\uDE05", "Hỏi đáp");
        p3.likes = 18; p3.commentsCount = 0; p3.createdAt = System.currentTimeMillis() - 86400000;
        db.communityDao().insertPost(p3);

        CommentEntity c1 = new CommentEntity(1, 0, "Hoàng Nam", "Mình thích thời Lê Lợi, 10 năm kháng chiến rất hào hùng! \u2694\uFE0F");
        c1.likes = 12; c1.createdAt = System.currentTimeMillis() - 3600000; db.communityDao().insertComment(c1);
        CommentEntity c2 = new CommentEntity(1, 0, "Thu Hà", "Đồng ý! Thời Trần rất hay, nhất là trận Bạch Đằng 1288");
        c2.likes = 8; c2.createdAt = System.currentTimeMillis() - 2700000; db.communityDao().insertComment(c2);
        CommentEntity c3 = new CommentEntity(1, 0, "Minh Anh", "@Thu Hà Đúng rồi, chiến thuật cọc gỗ quá thiên tài!");
        c3.likes = 5; c3.isReply = true; c3.parentCommentId = 2; c3.createdAt = System.currentTimeMillis() - 1800000; db.communityDao().insertComment(c3);
        CommentEntity c4 = new CommentEntity(1, 0, "Văn Đức", "Thời kỳ Tây Sơn cũng rất thú vị, Quang Trung đánh tan 29 vạn quân Thanh");
        c4.likes = 15; c4.createdAt = System.currentTimeMillis() - 1200000; db.communityDao().insertComment(c4);
        db.communityDao().syncAllCommentCounts();
    }

    private static void seedFigures(AppDatabase db) {
        List<HistoricalFigureEntity> figures = new ArrayList<>();
        // f1..f13 — keeping identical to original for stability
        HistoricalFigureEntity f1 = new HistoricalFigureEntity("Hai Bà Trưng");
        f1.title = "Nữ vương"; f1.birthYear = "?"; f1.deathYear = "43 SCN"; f1.dynasty = "Thời Bắc thuộc"; f1.period = "Bắc thuộc";
        f1.role = "Anh hùng dân tộc"; f1.queQuan = "Mê Linh, Giao Chỉ";
        f1.shortDesc = "Lãnh đạo cuộc khởi nghĩa đầu tiên chống ngoại xâm";
        f1.biography = "Trưng Trắc và Trưng Nhị là hai chị em, con gái Lạc tướng huyện Mê Linh. Năm 40 SCN, hai bà phất cờ khởi nghĩa chống ách đô hộ nhà Hán, nhanh chóng chiếm 65 thành. Trưng Trắc xưng vương, đóng đô ở Mê Linh. Năm 43, quân Hán do Mã Viện kéo sang đàn áp, hai bà tuẫn tiết trên sông Hát.";
        f1.achievements = "Lãnh đạo cuộc khởi nghĩa đầu tiên chống ngoại xâm do phụ nữ đứng đầu;Giải phóng 65 thành trì;Xưng vương, trị vì 3 năm (40–43)";
        f1.relatedEventIds = "1"; f1.relatedFigureIds = "2";
        f1.timelineMilestones = "? |Sinh ra tại Mê Linh, con gái Lạc tướng;40 SCN|Phất cờ khởi nghĩa chống quân Hán;40 SCN|Giải phóng 65 thành trì, xưng vương;43 SCN|Quân Mã Viện đàn áp, hai bà tuẫn tiết";
        f1.imageUrl = "figure_hai_ba_trung";
        f1.isFeatured = true; figures.add(f1);

        // (Same figure seeds as original - keeping them identical to save space)
        // ... figures f2 through f13 remain exactly the same as original ...

        HistoricalFigureEntity f2 = new HistoricalFigureEntity("Ngô Quyền");
        f2.title = "Vua"; f2.birthYear = "898"; f2.deathYear = "944"; f2.dynasty = "Nhà Ngô"; f2.period = "Bắc thuộc";
        f2.role = "Vua chúa"; f2.queQuan = "Đường Lâm, Ba Vì, Hà Nội";
        f2.shortDesc = "Chấm dứt 1000 năm Bắc thuộc bằng chiến thắng Bạch Đằng";
        f2.biography = "Ngô Quyền quê ở Đường Lâm (Ba Vì, Hà Nội), con rể Dương Đình Nghệ. Khi Dương Đình Nghệ bị Kiều Công Tiễn giết, Ngô Quyền đem quân ra đánh. Biết quân Nam Hán sẽ sang cứu viện, ông bày trận cọc trên sông Bạch Đằng, đánh tan quân xâm lược, mở ra kỷ nguyên độc lập.";
        f2.achievements = "Chiến thắng Bạch Đằng 938;Chấm dứt 1000 năm Bắc thuộc;Khai sinh nền độc lập tự chủ";
        f2.relatedEventIds = "2"; f2.relatedFigureIds = "1,3";
        f2.timelineMilestones = "898|Sinh tại Đường Lâm;931|Theo Dương Đình Nghệ dấy binh;937|Dương Đình Nghệ bị sát hại;938|Chiến thắng Bạch Đằng, xưng vương;939|Xây dựng triều đình nhà Ngô;944|Qua đời, hưởng thọ 47 tuổi";
        f2.imageUrl = "figure_ngo_quyen";
        f2.isFeatured = true; figures.add(f2);

        HistoricalFigureEntity f3 = new HistoricalFigureEntity("Lý Thường Kiệt");
        f3.title = "Thái úy"; f3.birthYear = "1019"; f3.deathYear = "1105"; f3.dynasty = "Nhà Lý"; f3.period = "Phong kiến";
        f3.role = "Tướng lĩnh"; f3.queQuan = "Thăng Long";
        f3.shortDesc = "Phá Tống bình Chiêm, tác giả Nam quốc sơn hà";
        f3.biography = "Lý Thường Kiệt là danh tướng kiệt xuất đời Lý. Ông chủ động tấn công sang đất Tống (tiên phát chế nhân), rồi xây phòng tuyến sông Như Nguyệt đánh bại quân Tống xâm lược. Ông được coi là tác giả bài thơ Nam quốc sơn hà — bản tuyên ngôn độc lập đầu tiên.";
        f3.achievements = "Phá Tống bình Chiêm;Chiến thắng sông Như Nguyệt 1077;Tác giả Nam quốc sơn hà";
        f3.relatedEventIds = "3"; f3.relatedFigureIds = "4,5";
        f3.timelineMilestones = "1019|Sinh tại Thăng Long;1061|Được phong Thái úy;1075|Tấn công sang đất Tống;1077|Chiến thắng sông Như Nguyệt;1105|Qua đời, thọ 86 tuổi";
        f3.imageUrl = "figure_ly_thuong_kiet";
        f3.isFeatured = true; figures.add(f3);

        HistoricalFigureEntity f4 = new HistoricalFigureEntity("Trần Hưng Đạo");
        f4.title = "Quốc công tiết chế"; f4.birthYear = "1228"; f4.deathYear = "1300"; f4.dynasty = "Nhà Trần"; f4.period = "Phong kiến";
        f4.role = "Tướng lĩnh"; f4.queQuan = "Nam Định";
        f4.shortDesc = "Ba lần đánh bại quân Nguyên Mông, tác giả Hịch tướng sĩ";
        f4.biography = "Hưng Đạo Đại Vương Trần Quốc Tuấn là vị tướng tài ba nhất thời Trần. Ông ba lần chỉ huy đánh bại quân Nguyên Mông (1258, 1285, 1288). Tác phẩm Hịch tướng sĩ là áng văn bất hủ. Khi mất, ông được nhân dân tôn thờ như thánh.";
        f4.achievements = "Ba lần đánh bại quân Nguyên Mông;Viết Hịch tướng sĩ;Binh pháp Vạn Kiếp tông bí truyền thư";
        f4.relatedEventIds = "4"; f4.relatedFigureIds = "3,5";
        f4.timelineMilestones = "1228|Sinh ra, tên thật Trần Quốc Tuấn;1258|Tham gia kháng chiến chống Nguyên lần 1;1284|Được phong Quốc công tiết chế;1285|Đánh thắng quân Nguyên lần 2;1288|Đại thắng Bạch Đằng lần 3;1300|Qua đời tại Vạn Kiếp";
        f4.imageUrl = "figure_tran_hung_dao";
        f4.isFeatured = true; figures.add(f4);

        HistoricalFigureEntity f5 = new HistoricalFigureEntity("Lê Lợi");
        f5.title = "Hoàng đế"; f5.birthYear = "1385"; f5.deathYear = "1433"; f5.dynasty = "Nhà Hậu Lê"; f5.period = "Phong kiến";
        f5.role = "Vua chúa"; f5.queQuan = "Lam Sơn, Thanh Hóa";
        f5.shortDesc = "Lãnh đạo khởi nghĩa Lam Sơn, khai sáng nhà Hậu Lê";
        f5.biography = "Lê Lợi quê ở Lam Sơn (Thanh Hóa). Năm 1418, ông dựng cờ khởi nghĩa chống quân Minh. Trải 10 năm nằm gai nếm mật cùng quân sư Nguyễn Trãi, ông giải phóng đất nước. Năm 1428, lên ngôi hoàng đế, lấy hiệu Lê Thái Tổ.";
        f5.achievements = "Lãnh đạo khởi nghĩa Lam Sơn 10 năm;Đánh đuổi quân Minh;Khai sáng nhà Hậu Lê";
        f5.relatedEventIds = "5"; f5.relatedFigureIds = "6";
        f5.timelineMilestones = "1385|Sinh tại Lam Sơn, Thanh Hóa;1418|Dựng cờ khởi nghĩa Lam Sơn;1424|Tiến quân vào Nghệ An;1426|Đại thắng Chi Lăng – Xương Giang;1428|Lên ngôi, hiệu Lê Thái Tổ;1433|Qua đời, thọ 48 tuổi";
        f5.imageUrl = "figure_le_loi";
        f5.isFeatured = true; figures.add(f5);

        HistoricalFigureEntity f6 = new HistoricalFigureEntity("Nguyễn Trãi");
        f6.title = "Danh nhân văn hóa thế giới"; f6.birthYear = "1380"; f6.deathYear = "1442"; f6.dynasty = "Nhà Hậu Lê"; f6.period = "Phong kiến";
        f6.role = "Nhà văn hóa"; f6.queQuan = "Hải Dương";
        f6.shortDesc = "Quân sư Lam Sơn, tác giả Bình Ngô đại cáo, Danh nhân UNESCO";
        f6.biography = "Nguyễn Trãi là nhà chính trị, quân sự, ngoại giao và nhà thơ kiệt xuất. Ông là quân sư cho Lê Lợi trong khởi nghĩa Lam Sơn, tác giả Bình Ngô đại cáo — bản tuyên ngôn độc lập thứ hai. Năm 1442, ông bị oan trong vụ án Lệ Chi Viên. UNESCO công nhận Danh nhân văn hóa thế giới 1980.";
        f6.achievements = "Quân sư khởi nghĩa Lam Sơn;Viết Bình Ngô đại cáo;Danh nhân văn hóa UNESCO 1980";
        f6.relatedEventIds = "5"; f6.relatedFigureIds = "5";
        f6.timelineMilestones = "1380|Sinh tại Chi Ngại, Hải Dương;1400|Đỗ Thái học sinh dưới triều Hồ;1418|Gia nhập khởi nghĩa Lam Sơn;1427|Viết Bình Ngô đại cáo;1442|Bị hại trong vụ án Lệ Chi Viên;1980|UNESCO công nhận Danh nhân văn hóa";
        f6.imageUrl = "figure_nguyen_trai";
        f6.isFeatured = true; figures.add(f6);

        HistoricalFigureEntity f7 = new HistoricalFigureEntity("Quang Trung – Nguyễn Huệ");
        f7.title = "Hoàng đế"; f7.birthYear = "1753"; f7.deathYear = "1792"; f7.dynasty = "Nhà Tây Sơn"; f7.period = "Phong kiến";
        f7.role = "Vua chúa"; f7.queQuan = "Tây Sơn, Bình Định";
        f7.shortDesc = "Thiên tài quân sự, đại phá 29 vạn quân Thanh trong 5 ngày";
        f7.biography = "Nguyễn Huệ là lãnh tụ phong trào Tây Sơn, thiên tài quân sự lớn nhất lịch sử Việt Nam. Ông đánh bại chúa Nguyễn, chúa Trịnh, thống nhất đất nước. Năm 1789, lên ngôi hoàng đế, thần tốc hành quân đại phá 29 vạn quân Thanh chỉ trong 5 ngày Tết.";
        f7.achievements = "Thống nhất Đàng Trong – Đàng Ngoài;Đại phá 29 vạn quân Thanh trong 5 ngày;Cải cách giáo dục, đề cao chữ Nôm";
        f7.relatedEventIds = "6"; f7.relatedFigureIds = "5,6";
        f7.timelineMilestones = "1753|Sinh tại Tây Sơn, Bình Định;1771|Cùng anh em dựng cờ khởi nghĩa;1777|Đánh bại chúa Nguyễn ở Gia Định;1786|Tiến quân ra Bắc, diệt chúa Trịnh;1788|Lên ngôi hoàng đế, hiệu Quang Trung;1789|Đại phá quân Thanh tại Đống Đa;1792|Đột ngột qua đời, thọ 40 tuổi";
        f7.imageUrl = "figure_quang_trung";
        f7.isFeatured = true; figures.add(f7);

        HistoricalFigureEntity f8 = new HistoricalFigureEntity("Hồ Chí Minh");
        f8.title = "Chủ tịch nước"; f8.birthYear = "1890"; f8.deathYear = "1969"; f8.dynasty = "Hiện đại"; f8.period = "Hiện đại";
        f8.role = "Nhà cách mạng"; f8.queQuan = "Nam Đàn, Nghệ An";
        f8.shortDesc = "Vị lãnh tụ vĩ đại, khai sinh nước Việt Nam mới";
        f8.biography = "Hồ Chí Minh sinh tại Nghệ An, ra đi tìm đường cứu nước năm 1911. Sáng lập Đảng Cộng sản Việt Nam (1930), lãnh đạo Cách mạng tháng Tám (1945), đọc Tuyên ngôn Độc lập khai sinh nước Việt Nam Dân chủ Cộng hòa. Ông là linh hồn hai cuộc kháng chiến chống Pháp và Mỹ.";
        f8.achievements = "Sáng lập Đảng Cộng sản Việt Nam;Đọc Tuyên ngôn Độc lập 2/9/1945;Lãnh đạo kháng chiến chống Pháp và Mỹ;Danh nhân văn hóa UNESCO";
        f8.relatedEventIds = "8,9,10"; f8.relatedFigureIds = "9,10";
        f8.timelineMilestones = "1890|Sinh tại Nam Đàn, Nghệ An;1911|Ra đi tìm đường cứu nước;1920|Tham gia sáng lập Đảng CS Pháp;1930|Sáng lập Đảng Cộng sản Việt Nam;1941|Về nước, thành lập Việt Minh;1945|Lãnh đạo CM tháng Tám, đọc Tuyên ngôn;1954|Chiến thắng Điện Biên Phủ;1969|Qua đời, để lại Di chúc";
        f8.imageUrl = "figure_ho_chi_minh";
        f8.isFeatured = true; figures.add(f8);

        HistoricalFigureEntity f9 = new HistoricalFigureEntity("Võ Nguyên Giáp");
        f9.title = "Đại tướng"; f9.birthYear = "1911"; f9.deathYear = "2013"; f9.dynasty = "Hiện đại"; f9.period = "Hiện đại";
        f9.role = "Tướng lĩnh"; f9.queQuan = "Lệ Thủy, Quảng Bình";
        f9.shortDesc = "Đại tướng đầu tiên, chiến thắng Điện Biên Phủ lừng lẫy";
        f9.biography = "Võ Nguyên Giáp là Đại tướng đầu tiên của Quân đội Nhân dân Việt Nam. Ông chỉ huy chiến dịch Điện Biên Phủ lịch sử (1954) làm chấn động địa cầu. Giới quân sự quốc tế gọi ông là Napoléon đỏ, một trong những danh tướng vĩ đại nhất thế kỷ 20.";
        f9.achievements = "Chỉ huy chiến thắng Điện Biên Phủ 1954;Đại tướng đầu tiên của QĐNDVN;Danh tướng vĩ đại nhất thế kỷ 20";
        f9.relatedEventIds = "9,10,11"; f9.relatedFigureIds = "8";
        f9.timelineMilestones = "1911|Sinh tại Lệ Thủy, Quảng Bình;1940|Tham gia cách mạng;1944|Thành lập đội Việt Nam Tuyên truyền Giải phóng quân;1948|Được phong Đại tướng;1954|Chỉ huy chiến thắng Điện Biên Phủ;1975|Tham gia chỉ đạo chiến dịch HCM;2013|Qua đời, thọ 103 tuổi";
        f9.imageUrl = "figure_vo_nguyen_giap";
        f9.isFeatured = true; figures.add(f9);

        HistoricalFigureEntity f10 = new HistoricalFigureEntity("Phan Bội Châu");
        f10.title = "Nhà cách mạng"; f10.birthYear = "1867"; f10.deathYear = "1940"; f10.dynasty = "Cận đại"; f10.period = "Cận đại";
        f10.role = "Nhà cách mạng"; f10.queQuan = "Nam Đàn, Nghệ An";
        f10.shortDesc = "Nhà yêu nước tiêu biểu, khởi xướng phong trào Đông Du";
        f10.biography = "Phan Bội Châu là nhà yêu nước tiêu biểu đầu thế kỷ 20. Ông khởi xướng phong trào Đông Du (1905), đưa thanh niên sang Nhật du học để về cứu nước. Sáng lập Việt Nam Quang phục Hội, suốt đời hoạt động không mệt mỏi cho độc lập dân tộc.";
        f10.achievements = "Khởi xướng phong trào Đông Du;Sáng lập Việt Nam Quang phục Hội;Nhà yêu nước tiêu biểu đầu thế kỷ 20";
        f10.relatedEventIds = "7"; f10.relatedFigureIds = "8";
        f10.timelineMilestones = "1867|Sinh tại Nam Đàn, Nghệ An;1900|Đỗ Giải nguyên;1904|Sáng lập Duy Tân Hội;1905|Khởi xướng phong trào Đông Du;1912|Thành lập Việt Nam Quang phục Hội;1925|Bị Pháp bắt tại Thượng Hải;1940|Qua đời tại Huế";
        f10.imageUrl = "figure_phan_boi_chau";
        f10.isFeatured = false; figures.add(f10);

        HistoricalFigureEntity f11 = new HistoricalFigureEntity("Lý Thái Tổ");
        f11.title = "Hoàng đế"; f11.birthYear = "974"; f11.deathYear = "1028"; f11.dynasty = "Nhà Lý"; f11.period = "Phong kiến";
        f11.role = "Vua chúa"; f11.queQuan = "Bắc Ninh";
        f11.shortDesc = "Khai sáng nhà Lý, dời đô về Thăng Long";
        f11.biography = "Lý Công Uẩn sinh năm 974, lớn lên ở chùa. Năm 1009, ông lên ngôi vua, lập ra triều Lý. Năm 1010, ông ban chiếu dời đô từ Hoa Lư về Đại La, đổi tên thành Thăng Long. Quyết định này đã thay đổi vận mệnh dân tộc suốt nhiều thế kỷ.";
        f11.achievements = "Khai sáng triều Lý;Dời đô về Thăng Long 1010;Đặt nền móng cho Hà Nội ngày nay";
        f11.relatedEventIds = "3"; f11.relatedFigureIds = "3";
        f11.timelineMilestones = "974|Sinh tại Bắc Ninh;1009|Lên ngôi, lập triều Lý;1010|Dời đô về Thăng Long;1028|Qua đời, thọ 55 tuổi";
        f11.imageUrl = "figure_ly_thai_to";
        f11.isFeatured = false; figures.add(f11);

        HistoricalFigureEntity f12 = new HistoricalFigureEntity("Trần Nhân Tông");
        f12.title = "Hoàng đế – Phật hoàng"; f12.birthYear = "1258"; f12.deathYear = "1308"; f12.dynasty = "Nhà Trần"; f12.period = "Phong kiến";
        f12.role = "Vua chúa"; f12.queQuan = "Thăng Long";
        f12.shortDesc = "Vua anh hùng chống Nguyên Mông, sáng lập Thiền phái Trúc Lâm";
        f12.biography = "Trần Nhân Tông là vị vua anh minh nhất triều Trần. Ông hai lần lãnh đạo kháng chiến chống quân Nguyên Mông (1285, 1288). Sau khi nhường ngôi, ông lên Yên Tử tu hành và sáng lập Thiền phái Trúc Lâm Yên Tử — dòng thiền thuần Việt.";
        f12.achievements = "Lãnh đạo kháng chiến chống Nguyên Mông;Sáng lập Thiền phái Trúc Lâm Yên Tử;Tổ chức Hội nghị Diên Hồng";
        f12.relatedEventIds = "4"; f12.relatedFigureIds = "4";
        f12.timelineMilestones = "1258|Sinh tại Thăng Long;1278|Lên ngôi vua;1285|Kháng chiến chống Nguyên lần 2;1288|Đại thắng Bạch Đằng;1293|Nhường ngôi, lên Yên Tử;1308|Qua đời tại Yên Tử";
        f12.imageUrl = "figure_tran_nhan_tong";
        f12.isFeatured = false; figures.add(f12);

        HistoricalFigureEntity f13 = new HistoricalFigureEntity("Nguyễn Du");
        f13.title = "Đại thi hào"; f13.birthYear = "1766"; f13.deathYear = "1820"; f13.dynasty = "Nhà Nguyễn"; f13.period = "Phong kiến";
        f13.role = "Nhà văn hóa"; f13.queQuan = "Nghi Xuân, Hà Tĩnh";
        f13.shortDesc = "Tác giả Truyện Kiều, Danh nhân văn hóa UNESCO";
        f13.biography = "Nguyễn Du là đại thi hào của dân tộc Việt Nam, tác giả Truyện Kiều — kiệt tác văn học bằng chữ Nôm. Tác phẩm với 3.254 câu thơ lục bát đã trở thành biểu tượng văn hóa Việt Nam. UNESCO công nhận Nguyễn Du là Danh nhân văn hóa thế giới năm 2013.";
        f13.achievements = "Viết Truyện Kiều — kiệt tác văn học;Danh nhân văn hóa UNESCO 2013;Đóng góp lớn cho văn học chữ Nôm";
        f13.relatedEventIds = ""; f13.relatedFigureIds = "6,7";
        f13.timelineMilestones = "1766|Sinh tại Thăng Long;1786|Gia đình gặp biến cố do Tây Sơn;1802|Ra làm quan triều Nguyễn;1813|Đi sứ nhà Thanh;1820|Qua đời tại Huế;2013|UNESCO vinh danh Danh nhân văn hóa";
        f13.imageUrl = "figure_nguyen_du";
        f13.isFeatured = false; figures.add(f13);

        db.figureDao().insertAll(figures);
    }

    private static void seedVideos(AppDatabase db) {
        List<VideoEntity> videos = new ArrayList<>();
        VideoEntity v1 = new VideoEntity("search:56 ngày đêm Điện Biên Phủ phim tài liệu VTV", "56 Ngày Đêm Điện Biên Phủ");
        v1.eventId = 10; v1.description = "Tài liệu lịch sử về chiến dịch Điện Biên Phủ"; v1.source = "VTV"; v1.duration = "10:30"; v1.thumbnail = "video_dien_bien_phu"; videos.add(v1);
        VideoEntity v2 = new VideoEntity("search:Bác Hồ đọc Tuyên ngôn Độc lập 2/9/1945 Ba Đình", "Ngày Độc Lập 2/9/1945");
        v2.eventId = 9; v2.description = "Bác Hồ đọc Tuyên ngôn Độc lập tại Ba Đình"; v2.source = "VTV"; v2.duration = "7:20"; v2.thumbnail = "video_quoc_khanh"; videos.add(v2);
        VideoEntity v3 = new VideoEntity("search:Hiệp định Genève 1954 phim tài liệu", "Hiệp định Genève 1954");
        v3.eventId = 10; v3.description = "Tài liệu về Hiệp định Genève"; v3.source = "VTV4"; v3.duration = "5:40"; v3.thumbnail = "video_hiep_dinh_geneve"; videos.add(v3);
        VideoEntity v4 = new VideoEntity("search:Chiến thắng Bạch Đằng 938 Ngô Quyền lịch sử", "Chiến thắng Bạch Đằng 938");
        v4.eventId = 2; v4.description = "Tài liệu lịch sử về trận Bạch Đằng"; v4.source = "Việt Sử Kiêu Hùng"; v4.duration = "6:00"; v4.thumbnail = "video_bach_dang"; videos.add(v4);
        VideoEntity v5 = new VideoEntity("search:Quang Trung đại phá quân Thanh 1789 phim tài liệu", "Quang Trung đại phá quân Thanh");
        v5.eventId = 6; v5.description = "Phim tài liệu về vua Quang Trung"; v5.source = "VTV"; v5.duration = "9:45"; v5.thumbnail = "video_quang_trung"; videos.add(v5);
        VideoEntity v6 = new VideoEntity("search:Chiến dịch Hồ Chí Minh 1975 giải phóng miền Nam phim tài liệu", "Chiến dịch Hồ Chí Minh 1975");
        v6.eventId = 11; v6.description = "Phim tài liệu giải phóng miền Nam"; v6.source = "ĐTHVN"; v6.duration = "11:20"; v6.thumbnail = "video_giai_phong"; videos.add(v6);
        VideoEntity v7 = new VideoEntity("search:Đại tướng Võ Nguyên Giáp phỏng vấn Điện Biên Phủ", "Tướng Võ Nguyên Giáp kể lại");
        v7.eventId = 10; v7.description = "Phỏng vấn Đại tướng Võ Nguyên Giáp"; v7.source = "VTV"; v7.duration = "8:15"; v7.thumbnail = "video_vo_nguyen_giap"; videos.add(v7);
        VideoEntity v8 = new VideoEntity("search:Khởi nghĩa Lam Sơn Lê Lợi Nguyễn Trãi phim tài liệu", "Khởi nghĩa Lam Sơn");
        v8.eventId = 5; v8.description = "Tài liệu về cuộc khởi nghĩa 10 năm"; v8.source = "Việt Sử Kiêu Hùng"; v8.duration = "12:00"; v8.thumbnail = "video_lam_son"; videos.add(v8);
        db.videoDao().insertAll(videos);
    }
}
