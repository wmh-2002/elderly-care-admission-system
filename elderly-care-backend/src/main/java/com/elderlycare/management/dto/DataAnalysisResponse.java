package com.elderlycare.management.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DataAnalysisResponse {
    // 统计概览数据
    private OverviewStats overview;

    // 老人分析数据
    private ElderAnalysisData elderAnalysis;

    // 营收分析数据
    private RevenueAnalysisData revenueAnalysis;

    // 运营分析数据
    private OperationAnalysisData operationAnalysis;

    public DataAnalysisResponse() {
    }

    public DataAnalysisResponse(OverviewStats overview, ElderAnalysisData elderAnalysis,
                               RevenueAnalysisData revenueAnalysis, OperationAnalysisData operationAnalysis) {
        this.overview = overview;
        this.elderAnalysis = elderAnalysis;
        this.revenueAnalysis = revenueAnalysis;
        this.operationAnalysis = operationAnalysis;
    }

    public OverviewStats getOverview() {
        return overview;
    }

    public void setOverview(OverviewStats overview) {
        this.overview = overview;
    }

    public ElderAnalysisData getElderAnalysis() {
        return elderAnalysis;
    }

    public void setElderAnalysis(ElderAnalysisData elderAnalysis) {
        this.elderAnalysis = elderAnalysis;
    }

    public RevenueAnalysisData getRevenueAnalysis() {
        return revenueAnalysis;
    }

    public void setRevenueAnalysis(RevenueAnalysisData revenueAnalysis) {
        this.revenueAnalysis = revenueAnalysis;
    }

    public OperationAnalysisData getOperationAnalysis() {
        return operationAnalysis;
    }

    public void setOperationAnalysis(OperationAnalysisData operationAnalysis) {
        this.operationAnalysis = operationAnalysis;
    }

    public static class OverviewStats {
        private long totalElders;
        private long occupiedBeds;
        private BigDecimal monthlyRevenue;
        private double averageStayDays;

        public OverviewStats() {
        }

        public OverviewStats(long totalElders, long occupiedBeds, BigDecimal monthlyRevenue, double averageStayDays) {
            this.totalElders = totalElders;
            this.occupiedBeds = occupiedBeds;
            this.monthlyRevenue = monthlyRevenue;
            this.averageStayDays = averageStayDays;
        }

        public long getTotalElders() {
            return totalElders;
        }

        public void setTotalElders(long totalElders) {
            this.totalElders = totalElders;
        }

        public long getOccupiedBeds() {
            return occupiedBeds;
        }

        public void setOccupiedBeds(long occupiedBeds) {
            this.occupiedBeds = occupiedBeds;
        }

        public BigDecimal getMonthlyRevenue() {
            return monthlyRevenue;
        }

        public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
            this.monthlyRevenue = monthlyRevenue;
        }

        public double getAverageStayDays() {
            return averageStayDays;
        }

        public void setAverageStayDays(double averageStayDays) {
            this.averageStayDays = averageStayDays;
        }
    }

    public static class ElderAnalysisData {
        private long totalElders;
        private long maleCount;
        private long femaleCount;
        private double averageAge;
        private List<ChartDataItem> genderDistribution;
        private List<ChartDataItem> ageDistribution;
        private List<ChartDataItem> careLevelDistribution;
        private List<ChartDataItem> stayDurationDistribution;
        private List<ElderListItem> elderList;

        public ElderAnalysisData() {
        }

        public ElderAnalysisData(long totalElders, long maleCount, long femaleCount, double averageAge,
                               List<ChartDataItem> genderDistribution, List<ChartDataItem> ageDistribution,
                               List<ChartDataItem> careLevelDistribution, List<ChartDataItem> stayDurationDistribution,
                               List<ElderListItem> elderList) {
            this.totalElders = totalElders;
            this.maleCount = maleCount;
            this.femaleCount = femaleCount;
            this.averageAge = averageAge;
            this.genderDistribution = genderDistribution;
            this.ageDistribution = ageDistribution;
            this.careLevelDistribution = careLevelDistribution;
            this.stayDurationDistribution = stayDurationDistribution;
            this.elderList = elderList;
        }

        public long getTotalElders() {
            return totalElders;
        }

        public void setTotalElders(long totalElders) {
            this.totalElders = totalElders;
        }

        public long getMaleCount() {
            return maleCount;
        }

        public void setMaleCount(long maleCount) {
            this.maleCount = maleCount;
        }

        public long getFemaleCount() {
            return femaleCount;
        }

        public void setFemaleCount(long femaleCount) {
            this.femaleCount = femaleCount;
        }

        public double getAverageAge() {
            return averageAge;
        }

        public void setAverageAge(double averageAge) {
            this.averageAge = averageAge;
        }

        public List<ChartDataItem> getGenderDistribution() {
            return genderDistribution;
        }

        public void setGenderDistribution(List<ChartDataItem> genderDistribution) {
            this.genderDistribution = genderDistribution;
        }

        public List<ChartDataItem> getAgeDistribution() {
            return ageDistribution;
        }

        public void setAgeDistribution(List<ChartDataItem> ageDistribution) {
            this.ageDistribution = ageDistribution;
        }

        public List<ChartDataItem> getCareLevelDistribution() {
            return careLevelDistribution;
        }

        public void setCareLevelDistribution(List<ChartDataItem> careLevelDistribution) {
            this.careLevelDistribution = careLevelDistribution;
        }

        public List<ChartDataItem> getStayDurationDistribution() {
            return stayDurationDistribution;
        }

        public void setStayDurationDistribution(List<ChartDataItem> stayDurationDistribution) {
            this.stayDurationDistribution = stayDurationDistribution;
        }

        public List<ElderListItem> getElderList() {
            return elderList;
        }

        public void setElderList(List<ElderListItem> elderList) {
            this.elderList = elderList;
        }
    }

    public static class RevenueAnalysisData {
        private BigDecimal monthlyRevenue;
        private BigDecimal totalRevenue;
        private BigDecimal pendingPayment;
        private BigDecimal averageRevenuePerElder;
        private List<ChartDataItem> revenueTrend;
        private List<ChartDataItem> feeItemDistribution;
        private List<ChartDataItem> paymentMethodDistribution;
        private List<YearlyComparisonItem> yearlyComparison;
        private List<BillListItem> billList;

        public RevenueAnalysisData() {
        }

        public RevenueAnalysisData(BigDecimal monthlyRevenue, BigDecimal totalRevenue,
                                 BigDecimal pendingPayment, BigDecimal averageRevenuePerElder,
                                 List<ChartDataItem> revenueTrend, List<ChartDataItem> feeItemDistribution,
                                 List<ChartDataItem> paymentMethodDistribution,
                                 List<YearlyComparisonItem> yearlyComparison, List<BillListItem> billList) {
            this.monthlyRevenue = monthlyRevenue;
            this.totalRevenue = totalRevenue;
            this.pendingPayment = pendingPayment;
            this.averageRevenuePerElder = averageRevenuePerElder;
            this.revenueTrend = revenueTrend;
            this.feeItemDistribution = feeItemDistribution;
            this.paymentMethodDistribution = paymentMethodDistribution;
            this.yearlyComparison = yearlyComparison;
            this.billList = billList;
        }

        public BigDecimal getMonthlyRevenue() {
            return monthlyRevenue;
        }

        public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
            this.monthlyRevenue = monthlyRevenue;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public BigDecimal getPendingPayment() {
            return pendingPayment;
        }

        public void setPendingPayment(BigDecimal pendingPayment) {
            this.pendingPayment = pendingPayment;
        }

        public BigDecimal getAverageRevenuePerElder() {
            return averageRevenuePerElder;
        }

        public void setAverageRevenuePerElder(BigDecimal averageRevenuePerElder) {
            this.averageRevenuePerElder = averageRevenuePerElder;
        }

        public List<ChartDataItem> getRevenueTrend() {
            return revenueTrend;
        }

        public void setRevenueTrend(List<ChartDataItem> revenueTrend) {
            this.revenueTrend = revenueTrend;
        }

        public List<ChartDataItem> getFeeItemDistribution() {
            return feeItemDistribution;
        }

        public void setFeeItemDistribution(List<ChartDataItem> feeItemDistribution) {
            this.feeItemDistribution = feeItemDistribution;
        }

        public List<ChartDataItem> getPaymentMethodDistribution() {
            return paymentMethodDistribution;
        }

        public void setPaymentMethodDistribution(List<ChartDataItem> paymentMethodDistribution) {
            this.paymentMethodDistribution = paymentMethodDistribution;
        }

        public List<YearlyComparisonItem> getYearlyComparison() {
            return yearlyComparison;
        }

        public void setYearlyComparison(List<YearlyComparisonItem> yearlyComparison) {
            this.yearlyComparison = yearlyComparison;
        }

        public List<BillListItem> getBillList() {
            return billList;
        }

        public void setBillList(List<BillListItem> billList) {
            this.billList = billList;
        }
    }

    public static class OperationAnalysisData {
        private long totalRooms;
        private long totalBeds;
        private long occupiedBeds;
        private double occupancyRate;
        private List<ChartDataItem> roomTypeDistribution;
        private List<ChartDataItem> bedStatusDistribution;
        private List<ChartDataItem> occupancyTrend;
        private List<ChartDataItem> floorDistribution;
        private List<FacilityItem> facilityList;

        public OperationAnalysisData() {
        }

        public OperationAnalysisData(long totalRooms, long totalBeds, long occupiedBeds, double occupancyRate,
                                   List<ChartDataItem> roomTypeDistribution, List<ChartDataItem> bedStatusDistribution,
                                   List<ChartDataItem> occupancyTrend, List<ChartDataItem> floorDistribution,
                                   List<FacilityItem> facilityList) {
            this.totalRooms = totalRooms;
            this.totalBeds = totalBeds;
            this.occupiedBeds = occupiedBeds;
            this.occupancyRate = occupancyRate;
            this.roomTypeDistribution = roomTypeDistribution;
            this.bedStatusDistribution = bedStatusDistribution;
            this.occupancyTrend = occupancyTrend;
            this.floorDistribution = floorDistribution;
            this.facilityList = facilityList;
        }

        public long getTotalRooms() {
            return totalRooms;
        }

        public void setTotalRooms(long totalRooms) {
            this.totalRooms = totalRooms;
        }

        public long getTotalBeds() {
            return totalBeds;
        }

        public void setTotalBeds(long totalBeds) {
            this.totalBeds = totalBeds;
        }

        public long getOccupiedBeds() {
            return occupiedBeds;
        }

        public void setOccupiedBeds(long occupiedBeds) {
            this.occupiedBeds = occupiedBeds;
        }

        public double getOccupancyRate() {
            return occupancyRate;
        }

        public void setOccupancyRate(double occupancyRate) {
            this.occupancyRate = occupancyRate;
        }

        public List<ChartDataItem> getRoomTypeDistribution() {
            return roomTypeDistribution;
        }

        public void setRoomTypeDistribution(List<ChartDataItem> roomTypeDistribution) {
            this.roomTypeDistribution = roomTypeDistribution;
        }

        public List<ChartDataItem> getBedStatusDistribution() {
            return bedStatusDistribution;
        }

        public void setBedStatusDistribution(List<ChartDataItem> bedStatusDistribution) {
            this.bedStatusDistribution = bedStatusDistribution;
        }

        public List<ChartDataItem> getOccupancyTrend() {
            return occupancyTrend;
        }

        public void setOccupancyTrend(List<ChartDataItem> occupancyTrend) {
            this.occupancyTrend = occupancyTrend;
        }

        public List<ChartDataItem> getFloorDistribution() {
            return floorDistribution;
        }

        public void setFloorDistribution(List<ChartDataItem> floorDistribution) {
            this.floorDistribution = floorDistribution;
        }

        public List<FacilityItem> getFacilityList() {
            return facilityList;
        }

        public void setFacilityList(List<FacilityItem> facilityList) {
            this.facilityList = facilityList;
        }
    }

    public static class ChartDataItem {
        private String name;
        private Number value;
        private String color;

        public ChartDataItem() {
        }

        public ChartDataItem(String name, Number value, String color) {
            this.name = name;
            this.value = value;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Number getValue() {
            return value;
        }

        public void setValue(Number value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class YearlyComparisonItem {
        private String month;
        private Number currentYear;
        private Number lastYear;

        public YearlyComparisonItem() {
        }

        public YearlyComparisonItem(String month, Number currentYear, Number lastYear) {
            this.month = month;
            this.currentYear = currentYear;
            this.lastYear = lastYear;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public Number getCurrentYear() {
            return currentYear;
        }

        public void setCurrentYear(Number currentYear) {
            this.currentYear = currentYear;
        }

        public Number getLastYear() {
            return lastYear;
        }

        public void setLastYear(Number lastYear) {
            this.lastYear = lastYear;
        }
    }

    public static class ElderListItem {
        private String elderNo;
        private String name;
        private String gender;
        private Integer age;
        private String careLevel;
        private String checkinDate;
        private Long stayDays;

        public ElderListItem() {
        }

        public ElderListItem(String elderNo, String name, String gender, Integer age,
                           String careLevel, String checkinDate, Long stayDays) {
            this.elderNo = elderNo;
            this.name = name;
            this.gender = gender;
            this.age = age;
            this.careLevel = careLevel;
            this.checkinDate = checkinDate;
            this.stayDays = stayDays;
        }

        public String getElderNo() {
            return elderNo;
        }

        public void setElderNo(String elderNo) {
            this.elderNo = elderNo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getCareLevel() {
            return careLevel;
        }

        public void setCareLevel(String careLevel) {
            this.careLevel = careLevel;
        }

        public String getCheckinDate() {
            return checkinDate;
        }

        public void setCheckinDate(String checkinDate) {
            this.checkinDate = checkinDate;
        }

        public Long getStayDays() {
            return stayDays;
        }

        public void setStayDays(Long stayDays) {
            this.stayDays = stayDays;
        }
    }

    public static class BillListItem {
        private String billMonth;
        private String elderName;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal pendingAmount;
        private Integer status;
        private String payMethod;

        public BillListItem() {
        }

        public BillListItem(String billMonth, String elderName, BigDecimal totalAmount,
                          BigDecimal paidAmount, BigDecimal pendingAmount, Integer status, String payMethod) {
            this.billMonth = billMonth;
            this.elderName = elderName;
            this.totalAmount = totalAmount;
            this.paidAmount = paidAmount;
            this.pendingAmount = pendingAmount;
            this.status = status;
            this.payMethod = payMethod;
        }

        public String getBillMonth() {
            return billMonth;
        }

        public void setBillMonth(String billMonth) {
            this.billMonth = billMonth;
        }

        public String getElderName() {
            return elderName;
        }

        public void setElderName(String elderName) {
            this.elderName = elderName;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(BigDecimal paidAmount) {
            this.paidAmount = paidAmount;
        }

        public BigDecimal getPendingAmount() {
            return pendingAmount;
        }

        public void setPendingAmount(BigDecimal pendingAmount) {
            this.pendingAmount = pendingAmount;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getPayMethod() {
            return payMethod;
        }

        public void setPayMethod(String payMethod) {
            this.payMethod = payMethod;
        }
    }

    public static class FacilityItem {
        private String roomNo;
        private String roomType;
        private Integer floor;
        private Integer maxBeds;
        private Integer occupiedBeds;
        private String status;
        private String lastMaintenance;

        public FacilityItem() {
        }

        public FacilityItem(String roomNo, String roomType, Integer floor, Integer maxBeds,
                          Integer occupiedBeds, String status, String lastMaintenance) {
            this.roomNo = roomNo;
            this.roomType = roomType;
            this.floor = floor;
            this.maxBeds = maxBeds;
            this.occupiedBeds = occupiedBeds;
            this.status = status;
            this.lastMaintenance = lastMaintenance;
        }

        public String getRoomNo() {
            return roomNo;
        }

        public void setRoomNo(String roomNo) {
            this.roomNo = roomNo;
        }

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        public Integer getFloor() {
            return floor;
        }

        public void setFloor(Integer floor) {
            this.floor = floor;
        }

        public Integer getMaxBeds() {
            return maxBeds;
        }

        public void setMaxBeds(Integer maxBeds) {
            this.maxBeds = maxBeds;
        }

        public Integer getOccupiedBeds() {
            return occupiedBeds;
        }

        public void setOccupiedBeds(Integer occupiedBeds) {
            this.occupiedBeds = occupiedBeds;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLastMaintenance() {
            return lastMaintenance;
        }

        public void setLastMaintenance(String lastMaintenance) {
            this.lastMaintenance = lastMaintenance;
        }
    }
}
