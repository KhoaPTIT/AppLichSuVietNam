package com.lichsuvietnam.app.utils;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

/**
 * Lớp tiện ích tải ảnh cho nhiều chức năng: bản đồ, sự kiện, thư viện ảnh,
 * cộng đồng và video. Nếu source bắt đầu bằng "http" thì tải từ Internet,
 * ngược lại source được hiểu là tên drawable nội bộ.
 * Glide là API ngoài được dùng để tối ưu cache, scale và hiển thị ảnh.
 */
public class ImageUtils {

    /**
     * Tải ảnh theo kiểu centerCrop.
     * Hàm này dùng API ngoài Glide cho cả URL từ xa và drawable nội bộ.
     */
    public static void load(Context context, String source, ImageView target) {
        if (source == null || source.isEmpty()) return;
        if (source.startsWith("http")) {
            Glide.with(context).load(source).centerCrop().into(target);
        } else {
            int resId = context.getResources().getIdentifier(source, "drawable", context.getPackageName());
            if (resId != 0) {
                Glide.with(context).load(resId).centerCrop().into(target);
            }
        }
    }

    /**
     * Tải ảnh và cắt thành hình tròn bằng transform CircleCrop của Glide.
     */
    public static void loadCircle(Context context, String source, ImageView target) {
        if (source == null || source.isEmpty()) return;
        if (source.startsWith("http")) {
            Glide.with(context).load(source).transform(new CircleCrop()).into(target);
        } else {
            int resId = context.getResources().getIdentifier(source, "drawable", context.getPackageName());
            if (resId != 0) {
                Glide.with(context).load(resId).transform(new CircleCrop()).into(target);
            }
        }
    }

    /**
     * Tải ảnh bo góc.
     * Bán kính truyền vào theo dp và được đổi sang pixel trước khi đưa cho Glide.
     */
    public static void loadRounded(Context context, String source, ImageView target, int radiusDp) {
        if (source == null || source.isEmpty()) return;
        int radiusPx = (int) (radiusDp * context.getResources().getDisplayMetrics().density);
        if (source.startsWith("http")) {
            Glide.with(context).load(source).transform(new CenterCrop(), new RoundedCorners(radiusPx)).into(target);
        } else {
            int resId = context.getResources().getIdentifier(source, "drawable", context.getPackageName());
            if (resId != 0) {
                Glide.with(context).load(resId).transform(new CenterCrop(), new RoundedCorners(radiusPx)).into(target);
            }
        }
    }

    /**
     * Tải ảnh theo kiểu fitCenter.
     * TimeMapFragment dùng hàm này để ảnh bản đồ được hiển thị đầy đủ, không bị cắt.
     */
    public static void loadFitCenter(Context context, String source, ImageView target) {
        if (source == null || source.isEmpty()) return;
        if (source.startsWith("http")) {
            Glide.with(context).load(source).fitCenter().into(target);
        } else {
            int resId = context.getResources().getIdentifier(source, "drawable", context.getPackageName());
            if (resId != 0) {
                Glide.with(context).load(resId).fitCenter().into(target);
            }
        }
    }
}
