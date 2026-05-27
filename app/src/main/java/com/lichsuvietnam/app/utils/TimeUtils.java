package com.lichsuvietnam.app.utils;

public class TimeUtils {
    public static String getRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / 60000;
        long hours = diff / 3600000;
        long days = diff / 86400000;

        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";
        if (hours < 24) return hours + " giờ trước";
        if (days < 7) return days + " ngày trước";
        return days / 7 + " tuần trước";
    }
}
