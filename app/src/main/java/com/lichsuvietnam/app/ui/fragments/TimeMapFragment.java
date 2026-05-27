package com.lichsuvietnam.app.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.provider.DataProvider;
import com.lichsuvietnam.app.data.models.TimeMapSlide;
import java.util.List;

/**
 * Fragment hiển thị chức năng Bản đồ thời gian.
 * Người dùng có thể chuyển từng giai đoạn bằng nút trước/sau hoặc SeekBar,
 * sau đó màn hình cập nhật ảnh bản đồ, năm, triều đại và phần mô tả tương ứng.
 */
public class TimeMapFragment extends Fragment {

    // Danh sách dữ liệu cho toàn bộ các mốc bản đồ lịch sử.
    private List<TimeMapSlide> slides;

    // Vị trí slide hiện tại, được dùng chung cho nút điều hướng và SeekBar.
    private int currentIndex = 0;

    // Các thành phần giao diện được ánh xạ từ fragment_time_map.xml.
    private ImageView ivTimeMap;
    private TextView txtSlideIndicator;
    private TextView txtSlideYear;
    private TextView txtSlidePeriod;
    private TextView txtSlideTitle;
    private TextView txtSlideDescription;
    private ImageView btnPrev, btnNext;
    private SeekBar seekBarTimeline;
    private TextView txtSeekStart, txtSeekEnd;

    /**
     * Khởi tạo giao diện bản đồ thời gian, lấy dữ liệu slide và gắn sự kiện
     * cho các nút điều hướng, SeekBar.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_map, container, false);

        // Lấy dữ liệu tĩnh từ DataProvider để màn hình vẫn chạy được khi offline.
        slides = DataProvider.getTimeMapSlides();

        // Ánh xạ các view cần cập nhật trong quá trình đổi mốc thời gian.
        ivTimeMap = view.findViewById(R.id.ivTimeMap);
        txtSlideIndicator = view.findViewById(R.id.txtSlideIndicator);
        txtSlideYear = view.findViewById(R.id.txtSlideYear);
        txtSlidePeriod = view.findViewById(R.id.txtSlidePeriod);
        txtSlideTitle = view.findViewById(R.id.txtSlideTitle);
        txtSlideDescription = view.findViewById(R.id.txtSlideDescription);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);
        seekBarTimeline = view.findViewById(R.id.seekBarTimeline);
        txtSeekStart = view.findViewById(R.id.txtSeekStart);
        txtSeekEnd = view.findViewById(R.id.txtSeekEnd);

        // Cấu hình SeekBar dựa theo số lượng slide bản đồ.
        if (slides != null && !slides.isEmpty()) {
            seekBarTimeline.setMax(slides.size() - 1);
            seekBarTimeline.setProgress(0);

            // Hiển thị nhãn mốc đầu và mốc cuối của đường thời gian.
            txtSeekStart.setText(slides.get(0).getYearLabel());
            txtSeekEnd.setText(slides.get(slides.size() - 1).getYearLabel());
        }

        // Chuyển về giai đoạn lịch sử trước đó.
        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                seekBarTimeline.setProgress(currentIndex);
                updateSlide(true);
            }
        });

        // Chuyển sang giai đoạn lịch sử tiếp theo.
        btnNext.setOnClickListener(v -> {
            if (slides != null && currentIndex < slides.size() - 1) {
                currentIndex++;
                seekBarTimeline.setProgress(currentIndex);
                updateSlide(true);
            }
        });

        // Lắng nghe SeekBar để người dùng nhảy trực tiếp tới một mốc thời gian.
        seekBarTimeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && slides != null && progress >= 0 && progress < slides.size()) {
                    currentIndex = progress;
                    updateSlide(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Hiển thị dữ liệu slide đầu tiên khi mở màn hình.
        updateSlide(false);

        return view;
    }

    /**
     * Đổ dữ liệu của slide đang chọn lên giao diện.
     * Hàm này đồng bộ nội dung chữ, ảnh bản đồ, trạng thái nút và hiệu ứng
     * chuyển cảnh theo giá trị currentIndex.
     *
     * @param animate true nếu cần chạy hiệu ứng khi đổi slide
     */
    private void updateSlide(boolean animate) {
        if (slides == null || slides.isEmpty()) return;

        TimeMapSlide slide = slides.get(currentIndex);

        // Cập nhật thông tin chữ tương ứng với slide hiện tại.
        txtSlideIndicator.setText((currentIndex + 1) + " / " + slides.size());
        txtSlideYear.setText(slide.getYearLabel());
        txtSlidePeriod.setText(slide.getPeriod());
        txtSlideTitle.setText(slide.getTitle());
        txtSlideDescription.setText(slide.getDescription());

        // Ưu tiên ảnh drawable đóng gói sẵn, sau đó mới dùng URL dự phòng.
        if (getContext() != null) {
            if (slide.hasLocalImage()) {
                // Kiểm tra an toàn để tránh lỗi nếu resource ảnh không tồn tại.
                try {
                    ivTimeMap.setImageResource(slide.getLocalImageResId());
                } catch (Exception e) {
                    // Nếu thiếu resource nội bộ thì thử tải ảnh từ URL dự phòng.
                    loadImageFromUrl(slide.getImageUrl());
                }
            } else if (slide.getImageUrl() != null && !slide.getImageUrl().isEmpty()) {
                loadImageFromUrl(slide.getImageUrl());
            } else {
                // Không có ảnh thì hiển thị placeholder mặc định.
                ivTimeMap.setImageResource(R.drawable.bg_rounded_card);
            }
        }

        // Cập nhật trạng thái bật/tắt của nút trước/sau theo vị trí hiện tại.
        btnPrev.setEnabled(currentIndex > 0);
        btnPrev.setAlpha(currentIndex > 0 ? 1.0f : 0.35f);
        btnNext.setEnabled(currentIndex < slides.size() - 1);
        btnNext.setAlpha(currentIndex < slides.size() - 1 ? 1.0f : 0.35f);

        // Chỉ chạy hiệu ứng khi thao tác đổi slide yêu cầu.
        if (animate) {
            animateContentChange();
        }
    }

    /**
     * Tải ảnh bản đồ từ URL vào ImageView.
     * ImageUtils là lớp bọc API ngoài Glide, giúp Fragment không phụ thuộc trực tiếp
     * vào chi tiết cấu hình request của Glide.
     */
    private void loadImageFromUrl(String url) {
        if (getContext() != null && url != null && !url.isEmpty()) {
            ImageUtils.loadFitCenter(getContext(), url, ivTimeMap);
        }
    }

    /**
     * Chạy hiệu ứng mờ/di chuyển nhẹ khi người dùng đổi giai đoạn bản đồ.
     * ObjectAnimator và AnimatorSet là API animation của Android.
     */
    private void animateContentChange() {
        // Làm ảnh bản đồ hiện rõ dần.
        ObjectAnimator fadeImage = ObjectAnimator.ofFloat(ivTimeMap, "alpha", 0.5f, 1.0f);
        fadeImage.setDuration(300);

        // Đẩy tiêu đề lên nhẹ để tạo cảm giác chuyển nội dung.
        ObjectAnimator slideTitle = ObjectAnimator.ofFloat(txtSlideTitle, "translationY", 20f, 0f);
        slideTitle.setDuration(250);

        ObjectAnimator fadeTitle = ObjectAnimator.ofFloat(txtSlideTitle, "alpha", 0f, 1f);
        fadeTitle.setDuration(250);

        // Đẩy phần mô tả lên nhẹ đồng bộ với tiêu đề.
        ObjectAnimator slideDesc = ObjectAnimator.ofFloat(txtSlideDescription, "translationY", 20f, 0f);
        slideDesc.setDuration(300);

        ObjectAnimator fadeDesc = ObjectAnimator.ofFloat(txtSlideDescription, "alpha", 0f, 1f);
        fadeDesc.setDuration(300);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(fadeImage, slideTitle, fadeTitle, slideDesc, fadeDesc);
        set.start();
    }
}
