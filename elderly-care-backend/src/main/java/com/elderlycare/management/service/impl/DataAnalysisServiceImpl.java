package com.elderlycare.management.service.impl;

import com.elderlycare.management.dto.DataAnalysisResponse;
import com.elderlycare.management.entity.*;
import com.elderlycare.management.repository.*;
import com.elderlycare.management.service.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {

    @Autowired
    private ElderRepository elderRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private CareRecordRepository careRecordRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    public DataAnalysisResponse.ElderAnalysisData getElderAnalysisData() {
        List<Elder> activeElders = elderRepository.findByStatus(1);

        long totalElders = activeElders.size();

        // 性别统计
        long maleCount = activeElders.stream()
                .filter(elder -> "M".equals(elder.getGender()))
                .count();
        long femaleCount = totalElders - maleCount;

        // 平均年龄
        double averageAge = activeElders.stream()
                .filter(elder -> elder.getBirthDate() != null)
                .mapToDouble(elder -> Period.between(elder.getBirthDate(), LocalDate.now()).getYears())
                .average()
                .orElse(0.0);

        // 性别分布
        List<DataAnalysisResponse.ChartDataItem> genderDistribution = Arrays.asList(
                new DataAnalysisResponse.ChartDataItem("男性", maleCount, "#409EFF"),
                new DataAnalysisResponse.ChartDataItem("女性", femaleCount, "#F56C6C")
        );

        // 年龄分布
        List<DataAnalysisResponse.ChartDataItem> ageDistribution = calculateAgeDistribution(activeElders);

        // 护理等级分布
        List<DataAnalysisResponse.ChartDataItem> careLevelDistribution = calculateCareLevelDistribution(activeElders);

        // 入住时长分布
        List<DataAnalysisResponse.ChartDataItem> stayDurationDistribution = calculateStayDurationDistribution(activeElders);

        // 老人列表（最近的10个）
        List<DataAnalysisResponse.ElderListItem> elderList = activeElders.stream()
                .sorted((a, b) -> b.getCheckinDate().compareTo(a.getCheckinDate()))
                .limit(10)
                .map(this::convertToElderListItem)
                .collect(Collectors.toList());

        return new DataAnalysisResponse.ElderAnalysisData(
                totalElders, maleCount, femaleCount, averageAge,
                genderDistribution, ageDistribution, careLevelDistribution,
                stayDurationDistribution, elderList
        );
    }

    @Override
    public DataAnalysisResponse.RevenueAnalysisData getRevenueAnalysisData() {
        // 本月营收
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        BigDecimal monthlyRevenue = billRepository.sumTotalAmountByCreateTimeBetween(startOfMonth, endOfMonth);
        if (monthlyRevenue == null) monthlyRevenue = BigDecimal.ZERO;

        // 累计营收
        BigDecimal totalRevenue = billRepository.sumTotalAmount();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        // 待收款（未缴清的账单）
        BigDecimal pendingPayment = billRepository.sumUnpaidAmount();
        if (pendingPayment == null) pendingPayment = BigDecimal.ZERO;

        // 人均营收
        long activeElderCount = elderRepository.countByStatus(1);
        BigDecimal averageRevenuePerElder = activeElderCount > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(activeElderCount), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        // 月度营收趋势（最近12个月）
        List<DataAnalysisResponse.ChartDataItem> revenueTrend = calculateMonthlyRevenueTrend();

        // 费用项目占比
        List<DataAnalysisResponse.ChartDataItem> feeItemDistribution = calculateFeeItemDistribution();

        // 支付方式统计
        List<DataAnalysisResponse.ChartDataItem> paymentMethodDistribution = calculatePaymentMethodDistribution();

        // 年度对比
        List<DataAnalysisResponse.YearlyComparisonItem> yearlyComparison = calculateYearlyComparison();

        // 账单列表（最近的10个）
        List<DataAnalysisResponse.BillListItem> billList = billRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::convertToBillListItem)
                .collect(Collectors.toList());

        return new DataAnalysisResponse.RevenueAnalysisData(
                monthlyRevenue, totalRevenue, pendingPayment, averageRevenuePerElder,
                revenueTrend, feeItemDistribution, paymentMethodDistribution,
                yearlyComparison, billList
        );
    }

    @Override
    public DataAnalysisResponse.OperationAnalysisData getOperationAnalysisData() {
        // 房间统计
        long totalRooms = roomRepository.count();
        long totalBeds = bedRepository.count();
        long occupiedBeds = bedRepository.countByStatus(1);
        double occupancyRate = totalBeds > 0 ? (double) occupiedBeds / totalBeds * 100 : 0.0;

        // 房间类型分布
        List<DataAnalysisResponse.ChartDataItem> roomTypeDistribution = calculateRoomTypeDistribution();

        // 床位状态分布
        List<DataAnalysisResponse.ChartDataItem> bedStatusDistribution = calculateBedStatusDistribution();

        // 入住率趋势
        List<DataAnalysisResponse.ChartDataItem> occupancyTrend = calculateOccupancyTrend();

        // 楼层分布
        List<DataAnalysisResponse.ChartDataItem> floorDistribution = calculateFloorDistribution();

        // 设施列表
        List<DataAnalysisResponse.FacilityItem> facilityList = roomRepository.findAll().stream()
                .limit(20)
                .map(this::convertToFacilityItem)
                .collect(Collectors.toList());

        return new DataAnalysisResponse.OperationAnalysisData(
                totalRooms, totalBeds, occupiedBeds, occupancyRate,
                roomTypeDistribution, bedStatusDistribution, occupancyTrend,
                floorDistribution, facilityList
        );
    }

    @Override
    public DataAnalysisResponse getAllAnalysisData() {
        DataAnalysisResponse response = new DataAnalysisResponse();
        response.setElderAnalysis(getElderAnalysisData());
        response.setRevenueAnalysis(getRevenueAnalysisData());
        response.setOperationAnalysis(getOperationAnalysisData());
        return response;
    }

    // 辅助方法
    private List<DataAnalysisResponse.ChartDataItem> calculateAgeDistribution(List<Elder> elders) {
        Map<String, Long> ageGroups = new LinkedHashMap<>();
        ageGroups.put("60-69岁", 0L);
        ageGroups.put("70-79岁", 0L);
        ageGroups.put("80-89岁", 0L);
        ageGroups.put("90岁以上", 0L);

        elders.stream()
                .filter(elder -> elder.getBirthDate() != null)
                .forEach(elder -> {
                    int age = Period.between(elder.getBirthDate(), LocalDate.now()).getYears();
                    if (age >= 90) {
                        ageGroups.put("90岁以上", ageGroups.get("90岁以上") + 1);
                    } else if (age >= 80) {
                        ageGroups.put("80-89岁", ageGroups.get("80-89岁") + 1);
                    } else if (age >= 70) {
                        ageGroups.put("70-79岁", ageGroups.get("70-79岁") + 1);
                    } else if (age >= 60) {
                        ageGroups.put("60-69岁", ageGroups.get("60-69岁") + 1);
                    }
                });

        return ageGroups.entrySet().stream()
                .map(entry -> new DataAnalysisResponse.ChartDataItem(entry.getKey(), entry.getValue(), "#67C23A"))
                .collect(Collectors.toList());
    }

    private List<DataAnalysisResponse.ChartDataItem> calculateCareLevelDistribution(List<Elder> elders) {
        Map<String, Long> careLevelCounts = elders.stream()
                .collect(Collectors.groupingBy(Elder::getCareLevel, Collectors.counting()));

        Map<String, String> careLevelNames = Map.of(
                "L1", "三级护理",
                "L2", "二级护理",
                "L3", "一级护理"
        );

        return careLevelCounts.entrySet().stream()
                .map(entry -> {
                    String levelName = careLevelNames.getOrDefault(entry.getKey(), entry.getKey());
                    return new DataAnalysisResponse.ChartDataItem(levelName, entry.getValue(), "#E6A23C");
                })
                .collect(Collectors.toList());
    }

    private List<DataAnalysisResponse.ChartDataItem> calculateStayDurationDistribution(List<Elder> elders) {
        Map<String, Long> durationGroups = new LinkedHashMap<>();
        durationGroups.put("0-6月", 0L);
        durationGroups.put("6-12月", 0L);
        durationGroups.put("1-2年", 0L);
        durationGroups.put("2-3年", 0L);
        durationGroups.put("3年以上", 0L);

        elders.stream()
                .filter(elder -> elder.getCheckinDate() != null)
                .forEach(elder -> {
                    long days = ChronoUnit.DAYS.between(elder.getCheckinDate(), LocalDate.now());
                    double years = days / 365.0;
                    if (years >= 3) {
                        durationGroups.put("3年以上", durationGroups.get("3年以上") + 1);
                    } else if (years >= 2) {
                        durationGroups.put("2-3年", durationGroups.get("2-3年") + 1);
                    } else if (years >= 1) {
                        durationGroups.put("1-2年", durationGroups.get("1-2年") + 1);
                    } else if (days >= 180) {
                        durationGroups.put("6-12月", durationGroups.get("6-12月") + 1);
                    } else {
                        durationGroups.put("0-6月", durationGroups.get("0-6月") + 1);
                    }
                });

        return durationGroups.entrySet().stream()
                .map(entry -> new DataAnalysisResponse.ChartDataItem(entry.getKey(), entry.getValue(), "#409EFF"))
                .collect(Collectors.toList());
    }

    private List<DataAnalysisResponse.ChartDataItem> calculateMonthlyRevenueTrend() {
        List<DataAnalysisResponse.ChartDataItem> trend = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

            LocalDateTime startTime = monthStart.atStartOfDay();
            LocalDateTime endTime = monthEnd.atTime(23, 59, 59);

            BigDecimal revenue = billRepository.sumTotalAmountByCreateTimeBetween(startTime, endTime);
            if (revenue == null) revenue = BigDecimal.ZERO;

            String monthLabel = monthStart.format(DateTimeFormatter.ofPattern("M月"));
            trend.add(new DataAnalysisResponse.ChartDataItem(monthLabel, revenue, "#409EFF"));
        }

        return trend;
    }

    private List<DataAnalysisResponse.ChartDataItem> calculateFeeItemDistribution() {
        // 统计各费用项目的总金额
        List<Object[]> results = billDetailRepository.sumAmountByItemCode();
        return results.stream()
                .map(row -> {
                    String itemCode = (String) row[0];
                    BigDecimal amount = (BigDecimal) row[1];
                    // 这里可以根据itemCode查询FeeItem表获取项目名称
                    String itemName = getFeeItemName(itemCode);
                    return new DataAnalysisResponse.ChartDataItem(itemName, amount, "#E6A23C");
                })
                .collect(Collectors.toList());
    }

    private List<DataAnalysisResponse.ChartDataItem> calculatePaymentMethodDistribution() {
        // 统计各支付方式的使用情况
        List<Object[]> results = billRepository.countByPaymentMethod();
        return results.stream()
                .map(row -> {
                    String payMethod = (String) row[0];
                    Long count = (Long) row[1];
                    String methodName = getPaymentMethodName(payMethod);
                    return new DataAnalysisResponse.ChartDataItem(methodName, count, "#67C23A");
                })
                .collect(Collectors.toList());
    }

    private List<DataAnalysisResponse.YearlyComparisonItem> calculateYearlyComparison() {
        List<DataAnalysisResponse.YearlyComparisonItem> comparison = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate currentYearMonth = now.minusMonths(i);
            LocalDate lastYearMonth = currentYearMonth.minusYears(1);

            // 当前年数据
            BigDecimal currentYearRevenue = getMonthlyRevenue(currentYearMonth);
            BigDecimal lastYearRevenue = getMonthlyRevenue(lastYearMonth);

            String monthLabel = currentYearMonth.format(DateTimeFormatter.ofPattern("M月"));
            comparison.add(new DataAnalysisResponse.YearlyComparisonItem(
                    monthLabel, currentYearRevenue, lastYearRevenue));
        }

        return comparison;
    }

    private List<DataAnalysisResponse.ChartDataItem> calculateRoomTypeDistribution() {
        List<Object[]> results = roomRepository.countByRoomType();
        return results.stream()
                .map(row -> new DataAnalysisResponse.ChartDataItem((String) row[0], (Long) row[1], "#409EFF"))
                .collect(Collectors.toList());
    }

    private List<DataAnalysisResponse.ChartDataItem> calculateBedStatusDistribution() {
        Map<Integer, Long> statusCounts = bedRepository.findAll().stream()
                .collect(Collectors.groupingBy(Bed::getStatus, Collectors.counting()));

        Map<String, Integer> statusMap = Map.of(
                "空闲", 0,
                "已入住", 1,
                "维修中", 2
        );

        return statusMap.entrySet().stream()
                .map(entry -> {
                    Long count = statusCounts.getOrDefault(entry.getValue(), 0L);
                    return new DataAnalysisResponse.ChartDataItem(entry.getKey(), count, "#67C23A");
                })
                .collect(Collectors.toList());
    }

    private List<DataAnalysisResponse.ChartDataItem> calculateOccupancyTrend() {
        // 这里可以实现更复杂的入住率趋势计算
        // 暂时返回模拟数据
        return Arrays.asList(
                new DataAnalysisResponse.ChartDataItem("1月", 85, "#409EFF"),
                new DataAnalysisResponse.ChartDataItem("2月", 87, "#409EFF"),
                new DataAnalysisResponse.ChartDataItem("3月", 88, "#409EFF"),
                new DataAnalysisResponse.ChartDataItem("4月", 89, "#409EFF"),
                new DataAnalysisResponse.ChartDataItem("5月", 90, "#409EFF"),
                new DataAnalysisResponse.ChartDataItem("6月", 91, "#409EFF")
        );
    }

    private List<DataAnalysisResponse.ChartDataItem> calculateFloorDistribution() {
        List<Object[]> results = roomRepository.countByFloor();
        return results.stream()
                .map(row -> new DataAnalysisResponse.ChartDataItem(
                        row[0] + "楼", (Long) row[1], "#E6A23C"))
                .collect(Collectors.toList());
    }

    // 辅助转换方法
    private DataAnalysisResponse.ElderListItem convertToElderListItem(Elder elder) {
        int age = elder.getBirthDate() != null ?
                Period.between(elder.getBirthDate(), LocalDate.now()).getYears() : 0;
        long stayDays = elder.getCheckinDate() != null ?
                ChronoUnit.DAYS.between(elder.getCheckinDate(), LocalDate.now()) : 0;

        return new DataAnalysisResponse.ElderListItem(
                elder.getElderNo(),
                elder.getName(),
                "M".equals(elder.getGender()) ? "男" : "女",
                age,
                getCareLevelText(elder.getCareLevel()),
                elder.getCheckinDate() != null ? elder.getCheckinDate().toString() : "",
                stayDays
        );
    }

    private DataAnalysisResponse.BillListItem convertToBillListItem(Bill bill) {
        Elder elder = bill.getElder();
        return new DataAnalysisResponse.BillListItem(
                bill.getBillMonth(),
                elder != null ? elder.getName() : "",
                bill.getTotalAmount(),
                bill.getPaidAmount(),
                bill.getTotalAmount().subtract(bill.getPaidAmount()),
                bill.getStatus(),
                bill.getPaymentMethod()
        );
    }

    private DataAnalysisResponse.FacilityItem convertToFacilityItem(Room room) {
        List<Bed> beds = bedRepository.findByRoomId(room.getId());
        int occupiedBeds = (int) beds.stream().filter(bed -> bed.getStatus() == 1).count();

        return new DataAnalysisResponse.FacilityItem(
                room.getRoomNo(),
                room.getRoomType(),
                room.getFloor(),
                room.getMaxBed(),
                occupiedBeds,
                "normal", // 暂时默认为正常状态
                LocalDate.now().minusDays(30).toString() // 模拟最后维修时间
        );
    }

    // 工具方法
    private BigDecimal getMonthlyRevenue(LocalDate month) {
        LocalDateTime start = month.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = month.withDayOfMonth(month.lengthOfMonth()).atTime(23, 59, 59);
        BigDecimal revenue = billRepository.sumTotalAmountByCreateTimeBetween(start, end);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    private String getFeeItemName(String itemCode) {
        // 这里应该从FeeItem表查询，暂时返回itemCode
        return itemCode;
    }

    private String getPaymentMethodName(String payMethod) {
        // 这里应该从字典表查询，暂时返回payMethod
        return payMethod;
    }

    private String getCareLevelText(String careLevel) {
        Map<String, String> careLevelMap = Map.of(
                "L1", "三级护理",
                "L2", "二级护理",
                "L3", "一级护理"
        );
        return careLevelMap.getOrDefault(careLevel, careLevel);
    }
}
