package com.example.test1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN);

    /**
     * Calendar 객체를 화면 표시용 날짜 문자열로 변환
     * 예: "2024년 03월 15일"
     */
    public static String formatDateForDisplay(Calendar calendar) {
        if (calendar == null) return "";
        return displayFormat.format(calendar.getTime());
    }

    /**
     * Calendar 객체를 유통기한 형식 문자열로 변환
     * 예: "D-5", "오늘", "만료됨"
     */
    public static String convertToExpiryFormat(Calendar targetDate) {
        if (targetDate == null) return "";

        Calendar today = Calendar.getInstance();

        // 시간을 00:00:00으로 설정하여 날짜만 비교
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar target = (Calendar) targetDate.clone();
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        long diffInMillis = target.getTimeInMillis() - today.getTimeInMillis();
        int diffInDays = (int) (diffInMillis / (24 * 60 * 60 * 1000));

        if (diffInDays < 0) {
            return "만료됨";
        } else if (diffInDays == 0) {
            return "오늘";
        } else {
            return "D-" + diffInDays;
        }
    }

    /**
     * 유통기한 문자열에서 D-day 숫자 추출
     * 예: "D-5" -> 5, "오늘" -> 0, "만료됨" -> -1
     */
    public static int extractDaysFromExpiry(String expiry) {
        if (expiry == null || expiry.isEmpty()) return -1;

        if ("만료됨".equals(expiry)) {
            return -1;
        } else if ("오늘".equals(expiry)) {
            return 0;
        } else if (expiry.startsWith("D-")) {
            try {
                return Integer.parseInt(expiry.substring(2));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * 유통기한이 임박한지 확인 (3일 이하)
     */
    public static boolean isExpiryUrgent(String expiry) {
        int days = extractDaysFromExpiry(expiry);
        return days >= 0 && days <= 3;
    }

    /**
     * 유통기한이 주의해야 할 상태인지 확인 (7일 이하)
     */
    public static boolean isExpiryWarning(String expiry) {
        int days = extractDaysFromExpiry(expiry);
        return days >= 0 && days <= 7;
    }
}