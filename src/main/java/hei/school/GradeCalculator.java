package hei.school;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class GradeCalculator {

    private static final BigDecimal TWENTY = new BigDecimal("20");
    private static final BigDecimal MAX_PENALTY_DAYS = new BigDecimal("5");
    private static final BigDecimal PENALTY_PER_DAY = new BigDecimal("0.10");

    private GradeCalculator() {
    }

    public static int lateDays(LocalDate deadline, LocalDateTime submittedAt) {
        if (submittedAt == null || deadline == null || !submittedAt.toLocalDate().isAfter(deadline)) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(deadline, submittedAt.toLocalDate());
        return (int) Math.max(1, days);
    }

    public static BigDecimal gradeOutOf20(BigDecimal rawPoints, BigDecimal maxPoints) {
        return rawPoints.multiply(TWENTY).divide(maxPoints, 1, RoundingMode.HALF_UP);
    }

    public static BigDecimal penalizedGrade(BigDecimal rawPoints, BigDecimal maxPoints, LocalDate deadline,
            LocalDateTime submittedAt) {
        BigDecimal grade = gradeOutOf20(rawPoints, maxPoints);
        int daysLate = lateDays(deadline, submittedAt);
        if (daysLate == 0) {
            return grade;
        }

        BigDecimal penaltyDays = BigDecimal.valueOf(Math.min(daysLate, MAX_PENALTY_DAYS.intValue()));
        BigDecimal penalty = penaltyDays.multiply(PENALTY_PER_DAY);
        BigDecimal factor = BigDecimal.ONE.subtract(penalty);
        BigDecimal penalized = grade.multiply(factor).setScale(1, RoundingMode.HALF_UP);
        if (penalized.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }
        return penalized;
    }
}
