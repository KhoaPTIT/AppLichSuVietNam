package com.lichsuvietnam.app.data.provider;

import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.models.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Cung cấp dữ liệu tĩnh cho các thành phần giao diện không lấy trực tiếp từ Room DB.
 * Các dữ liệu này dùng cho bản đồ thời gian và một số ảnh minh họa đa phương tiện.
 */
public class DataProvider {

    /**
     * Trả về 20 slide bản đồ mô tả lãnh thổ Việt Nam qua các thời kỳ lịch sử.
     * TimeMapFragment sử dụng danh sách này để hiển thị bản đồ theo thứ tự thời gian.
     * Mỗi slide liên kết tới drawable map_period_XX để chức năng chạy được offline.
     */
    public static List<TimeMapSlide> getTimeMapSlides() {
        List<TimeMapSlide> slides = new ArrayList<>();

        slides.add(new TimeMapSlide(1, "Văn Lang", "Thời kỳ Hùng Vương", "~2879–258 TCN", 1, "",
                "Nhà nước Văn Lang do các vua Hùng cai trị là nhà nước đầu tiên của người Việt. Lãnh thổ trải dài vùng Bắc Bộ và Bắc Trung Bộ ngày nay, chia thành 15 bộ, với trung tâm ở vùng Phong Châu (Phú Thọ). Đây là cội nguồn của nền văn minh sông Hồng.",
                R.drawable.map_period_01));
        slides.add(new TimeMapSlide(2, "Âu Lạc", "Thời An Dương Vương", "257–207 TCN", 2, "",
                "An Dương Vương hợp nhất Âu Việt và Lạc Việt thành nước Âu Lạc, đóng đô tại Cổ Loa (Đông Anh, Hà Nội). Lãnh thổ mở rộng hơn Văn Lang về phía Bắc. Thành Cổ Loa là công trình quân sự đặc sắc thời cổ đại.",
                R.drawable.map_period_02));
        slides.add(new TimeMapSlide(3, "Nam Việt", "Thời Triệu Đà", "207–111 TCN", 3, "",
                "Triệu Đà lập nước Nam Việt bao gồm vùng Lĩnh Nam và lãnh thổ Âu Lạc. Kinh đô đặt tại Phiên Ngung (Quảng Châu).",
                R.drawable.map_period_03));
        slides.add(new TimeMapSlide(4, "Bắc thuộc lần I–III", "Hán – Ngô – Tấn – Tùy – Đường đô hộ", "111 TCN – 905", 4, "",
                "Hơn 1000 năm Bắc thuộc, lãnh thổ Việt bị sáp nhập thành các quận huyện của Trung Quốc. Trong thời kỳ này đã có nhiều cuộc khởi nghĩa lớn.",
                R.drawable.map_period_04));
        slides.add(new TimeMapSlide(5, "Vạn Xuân", "Nhà Tiền Lý", "544–602", 5, "",
                "Lý Bí khởi nghĩa thành công, lập nước Vạn Xuân – nhà nước độc lập ngắn ngủi giữa thời Bắc thuộc.",
                R.drawable.map_period_05));
        slides.add(new TimeMapSlide(6, "Tĩnh Hải Quân", "Họ Khúc tự chủ", "905–938", 6, "",
                "Khúc Thừa Dụ giành quyền tự chủ. Đây là bước đệm cho nền độc lập hoàn toàn sau trận Bạch Đằng 938.",
                R.drawable.map_period_06));
        slides.add(new TimeMapSlide(7, "Đại Cồ Việt", "Nhà Đinh – Tiền Lê", "968–1009", 7, "",
                "Đinh Bộ Lĩnh dẹp loạn 12 sứ quân, thống nhất đất nước và đặt quốc hiệu Đại Cồ Việt, đóng đô tại Hoa Lư.",
                R.drawable.map_period_07));
        slides.add(new TimeMapSlide(8, "Đại Việt thời Lý", "Nhà Lý", "1009–1225", 8, "",
                "Lý Công Uẩn dời đô về Thăng Long (1010), đổi quốc hiệu thành Đại Việt. Nhà Lý đánh bại quân Tống ở trận Như Nguyệt (1077).",
                R.drawable.map_period_08));
        slides.add(new TimeMapSlide(9, "Đại Việt thời Trần", "Nhà Trần", "1225–1400", 9, "",
                "Nhà Trần ba lần đánh thắng quân Nguyên – Mông (1258, 1285, 1288).",
                R.drawable.map_period_09));
        slides.add(new TimeMapSlide(10, "Đại Ngu", "Nhà Hồ", "1400–1407", 10, "",
                "Hồ Quý Ly lập nhà Hồ, đổi quốc hiệu thành Đại Ngu. Triều đại chỉ tồn tại 7 năm.",
                R.drawable.map_period_10));
        slides.add(new TimeMapSlide(11, "Khởi nghĩa Lam Sơn", "Giải phóng khỏi nhà Minh", "1418–1427", 11, "",
                "Lê Lợi phát động khởi nghĩa Lam Sơn, sau 10 năm đánh đuổi hoàn toàn quân Minh.",
                R.drawable.map_period_11));
        slides.add(new TimeMapSlide(12, "Đại Việt thời Hậu Lê", "Nhà Hậu Lê sơ", "1428–1527", 12, "",
                "Lê Thái Tổ lập nhà Hậu Lê. Dưới thời Lê Thánh Tông, Đại Việt đạt đỉnh cao về lãnh thổ và thể chế.",
                R.drawable.map_period_12));
        slides.add(new TimeMapSlide(13, "Nam – Bắc triều", "Lê – Mạc phân tranh", "1527–1592", 13, "",
                "Mạc Đăng Dung cướp ngôi nhà Lê, đất nước chia đôi gần 70 năm.",
                R.drawable.map_period_13));
        slides.add(new TimeMapSlide(14, "Nguyễn Hoàng mở cõi", "Khởi đầu xứ Đàng Trong", "1558–1613", 14, "",
                "Nguyễn Hoàng vào trấn thủ Thuận Hóa, khởi đầu công cuộc Nam tiến.",
                R.drawable.map_period_14));
        slides.add(new TimeMapSlide(15, "Trịnh – Nguyễn phân tranh", "Đàng Ngoài – Đàng Trong", "1627–1672", 15, "",
                "Đất nước chia cắt tại sông Gianh suốt gần 50 năm giao tranh.",
                R.drawable.map_period_15));
        slides.add(new TimeMapSlide(16, "Phong trào Tây Sơn", "Nhà Tây Sơn", "1771–1802", 16, "",
                "Ba anh em Tây Sơn dấy binh, thống nhất đất nước. Quang Trung đại phá 29 vạn quân Thanh (1789).",
                R.drawable.map_period_16));
        slides.add(new TimeMapSlide(17, "Nhà Nguyễn thống nhất", "Triều Nguyễn", "1802–1858", 17, "",
                "Nguyễn Ánh lập nhà Nguyễn, đặt quốc hiệu Việt Nam (1804), đóng đô tại Huế. Lãnh thổ trải dài từ Lạng Sơn đến Cà Mau.",
                R.drawable.map_period_17));
        slides.add(new TimeMapSlide(18, "Pháp xâm lược & đô hộ", "Liên bang Đông Dương", "1858–1945", 18, "",
                "Pháp tấn công Đà Nẵng (1858), dần chiếm toàn bộ Việt Nam. Việt Nam bị chia thành 3 kỳ.",
                R.drawable.map_period_18));
        slides.add(new TimeMapSlide(19, "Chia cắt Bắc – Nam", "Việt Nam 1954–1975", "1954–1975", 19, "",
                "Hiệp định Genève chia Việt Nam tại vĩ tuyến 17. Ngày 30/4/1975, Sài Gòn giải phóng.",
                R.drawable.map_period_19));
        slides.add(new TimeMapSlide(20, "Việt Nam thống nhất", "Cộng hòa XHCN Việt Nam", "1976–nay", 20, "",
                "Ngày 2/7/1976, nước Việt Nam thống nhất. Diện tích khoảng 331.000 km².",
                R.drawable.map_period_20));

        return slides;
    }

    /** Dùng cho ProfileSetupActivity khi người dùng chọn chủ đề quan tâm. */
    public static String[] getTopics() {
        return new String[]{
                "Thời kỳ dựng nước", "Bắc thuộc", "Nhà Lý - Trần", "Nhà Lê",
                "Nhà Nguyễn", "Kháng chiến Pháp", "Kháng chiến Mỹ", "Hiện đại",
                "Nhân vật lịch sử", "Văn hóa - Kiến trúc", "Chiến tranh & Trận đánh", "Ngoại giao"
        };
    }

    /** Dùng cho MediaViewerActivity để lấy danh sách ảnh minh họa nội bộ. */
    public static String[] getMediaImages() {
        return new String[]{
            "event_hai_ba_trung",
            "event_doi_do_thang_long",
            "event_bach_dang_938"
        };
    }
}
