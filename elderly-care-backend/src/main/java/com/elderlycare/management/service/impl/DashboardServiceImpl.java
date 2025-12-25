package com.elderlycare.management.service.impl;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.Bed;
import com.elderlycare.management.entity.Elder;
import com.elderlycare.management.entity.Room;
import com.elderlycare.management.entity.SysUser;
import com.elderlycare.management.repository.BedRepository;
import com.elderlycare.management.repository.ElderRepository;
import com.elderlycare.management.repository.RoomRepository;
import com.elderlycare.management.repository.SysUserRepository;
import com.elderlycare.management.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.function.Function;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ElderRepository elderRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private BedRepository bedRepository;

    @Override
    public DashboardResponse getDashboardData() {
        // Fetch main statistics
        DashboardStats stats = getDashboardStats();
        
        // Fetch additional stats
        List<DashboardStatItem> additionalStats = getAdditionalStats();
        
        // Fetch care level distribution
        List<DashboardChartItem> careLevelDistribution = getCareLevelDistribution();
        
        // Fetch room occupancy
        List<DashboardChartItem> roomOccupancy = getRoomOccupancy();
        
        // Fetch news/activities
        List<DashboardNewsItem> newsList = getNewsList();
        
        // Calculate occupancy rate
        double occupancyRate = calculateOccupancyRate();
        
        // Mock user data for now (in real implementation, this would come from authentication context)
        String username = "管理员";
        String currentDate = getCurrentDate();
        int notificationCount = 3;
        
        return new DashboardResponse(
            stats,
            additionalStats,
            careLevelDistribution,
            roomOccupancy,
            newsList,
            occupancyRate,
            notificationCount,
            username,
            currentDate
        );
    }
    
    private DashboardStats getDashboardStats() {
        // Count total elders (active residents)
        List<Elder> elders = elderRepository.findByStatus(1); // Assuming status 1 means active
        long totalElders = elders.size();
        
        // Count total rooms
        long totalRooms = roomRepository.count();
        
        // Count vacant beds - beds with status 0 (available)
        List<Bed> allBeds = bedRepository.findAll();
        long vacantBeds = allBeds.stream()
                .filter(bed -> bed.getStatus() != null && bed.getStatus() == 0)
                .count();
        
        // Count total staff
        long totalStaff = sysUserRepository.count();
        
        return new DashboardStats((int) totalElders, (int) totalRooms, (int) vacantBeds, (int) totalStaff);
    }
    
    private List<DashboardStatItem> getAdditionalStats() {
        // Mock data - in real implementation, these would be calculated from database
        return Arrays.asList(
            new DashboardStatItem(24, "今日活动"),
            new DashboardStatItem(8, "家属探访"),
            new DashboardStatItem(5, "待处理事项")
        );
    }
    
    private List<DashboardChartItem> getCareLevelDistribution() {
        // Count elders by care level from database
        List<Elder> elders = elderRepository.findByStatus(1); // Only active elders
        
        // Group by care level
        java.util.Map<String, Long> careLevelCounts = elders.stream()
                .map(Elder::getCareLevel)
                .collect(java.util.stream.Collectors.groupingBy(
                        java.util.function.Function.identity(),
                        java.util.stream.Collectors.counting()
                ));
        
        // Define colors for care levels (assuming standard levels)
        java.util.Map<String, String> colorMap = java.util.Map.of(
            "一级护理", "#F56C6C",
            "二级护理", "#E6A23C", 
            "三级护理", "#67C23A"
        );
        
        return careLevelCounts.entrySet().stream()
                .map(entry -> new DashboardChartItem(
                    entry.getKey(),
                    entry.getValue().intValue(),
                    colorMap.getOrDefault(entry.getKey(), "#909399") // default color
                ))
                .toList();
    }
    
    private List<DashboardChartItem> getRoomOccupancy() {
        // Get all rooms and group by room type to get occupancy by type
        List<Room> rooms = roomRepository.findAll();
        
        // Group rooms by type and count occupied vs vacant
        Map<String, Long> roomTypeCounts = rooms.stream()
                .collect(Collectors.groupingBy(
                    Room::getRoomType,
                    Collectors.counting()
                ));
        
        // Since we need occupied vs vacant by type, we'll need to join with bed data
        // For this mock, we'll return some realistic data based on room types
        // In a real implementation, you'd join room and bed data more thoroughly
        return Arrays.asList(
            new DashboardChartItem("单人间", 8, "#409EFF"),
            new DashboardChartItem("双人间", 15, "#409EFF"),
            new DashboardChartItem("多人间", 21, "#409EFF")
        );
    }
    
    private List<DashboardNewsItem> getNewsList() {
        // Mock recent activities/news
        return Arrays.asList(
            new DashboardNewsItem(
                "新老人入住",
                "张三老人于今日成功办理入住手续，已分配至301房间",
                "2023/4/17 20:46",
                "success",
                "Check"
            ),
            new DashboardNewsItem(
                "健康检查提醒",
                "提醒护理员明天为李四老人进行每周例行健康检查",
                "2023/4/18 15:30",
                "warning",
                "Bell"
            ),
            new DashboardNewsItem(
                "房间维护完成",
                "201房间空调维护工作已完成，老人已返回房间",
                "2023/4/18 20:46",
                "info",
                "Document"
            ),
            new DashboardNewsItem(
                "家属探访",
                "王五老人的家属将于明天下午来院探访",
                "2023/4/19 10:15",
                "primary",
                "Calendar"
            )
        );
    }
    
    private double calculateOccupancyRate() {
        // Calculate occupancy rate: (total occupied beds / total available beds) * 100
        List<Bed> allBeds = bedRepository.findAll();
        if (allBeds.isEmpty()) {
            return 0.0;
        }

        long totalBeds = allBeds.size();
        long occupiedBeds = allBeds.stream()
                .filter(bed -> bed.getStatus() != null && bed.getStatus() == 1) // status 1 means occupied
                .count();

        double occupancyRate = totalBeds > 0 ? (double) occupiedBeds / totalBeds * 100 : 0.0;
        
        // Round to 2 decimal places
        return Math.round(occupancyRate * 100.0) / 100.0;
    }
    
    
    
    private String getCurrentDate() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 E");
        return now.format(formatter).replace("E", getWeekdayChinese(now.getDayOfWeek().getValue()));
    }
    
    private String getWeekdayChinese(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return "星期一";
            case 2: return "星期二";
            case 3: return "星期三";
            case 4: return "星期四";
            case 5: return "星期五";
            case 6: return "星期六";
            case 7: return "星期日";
            default: return "";
        }
    }
}