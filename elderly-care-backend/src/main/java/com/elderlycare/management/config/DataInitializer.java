package com.elderlycare.management.config;

import com.elderlycare.management.entity.*;
import com.elderlycare.management.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SysRoleRepository roleRepository;
    private final SysUserRepository userRepository;
    private final SysDictRepository dictRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final CareLevelRepository careLevelRepository;
    private final FeeItemRepository feeItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SysRoleRepository roleRepository, SysUserRepository userRepository,
                          SysDictRepository dictRepository, RoomRepository roomRepository,
                          BedRepository bedRepository, CareLevelRepository careLevelRepository,
                          FeeItemRepository feeItemRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.dictRepository = dictRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.careLevelRepository = careLevelRepository;
        this.feeItemRepository = feeItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化角色数据
        if (roleRepository.count() == 0) {
            initializeRoles();
        }

        // 初始化用户数据
        if (userRepository.count() == 0) {
            initializeUsers();
        }

        // 初始化字典数据
        if (dictRepository.count() == 0) {
            initializeDicts();
        }

        // 初始化房间和床位数据
        if (roomRepository.count() == 0) {
            initializeRoomsAndBeds();
        }

        // 初始化护理等级数据
        if (careLevelRepository.count() == 0) {
            initializeCareLevels();
        }

        // 初始化费用项目数据
        if (feeItemRepository.count() == 0) {
            initializeFeeItems();
        }
    }

    private void initializeRoles() {
        SysRole role1 = new SysRole();
        role1.setId(1L);
        role1.setRoleName("系统管理员");
        role1.setRemark("拥有所有权限");
        roleRepository.save(role1);

        SysRole role2 = new SysRole();
        role2.setId(2L);
        role2.setRoleName("院长");
        role2.setRemark("查看各类统计报表");
        roleRepository.save(role2);

        SysRole role3 = new SysRole();
        role3.setId(3L);
        role3.setRoleName("护理主管");
        role3.setRemark("管理护理计划与排班");
        roleRepository.save(role3);

        SysRole role4 = new SysRole();
        role4.setId(4L);
        role4.setRoleName("护理员");
        role4.setRemark("填写护理记录");
        roleRepository.save(role4);

        SysRole role5 = new SysRole();
        role5.setId(5L);
        role5.setRoleName("财务人员");
        role5.setRemark("费用计算与收款");
        roleRepository.save(role5);

        SysRole role6 = new SysRole();
        role6.setId(6L);
        role6.setRoleName("前台接待");
        role6.setRemark("老人入住登记");
        roleRepository.save(role6);
    }

    private void initializeUsers() {
        // 创建管理员用户
        SysUser admin = new SysUser();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRealName("超级管理员");
        admin.setEmail("admin@example.com");
        admin.setPhone("13800138001");
        admin.setRoleId(1L);
        admin.setStatus(true);
        userRepository.save(admin);
    }

    private void initializeDicts() {
        // 性别字典
        SysDict dict1 = new SysDict();
        dict1.setId(1L);
        dict1.setDictType("gender");
        dict1.setDictKey("M");
        dict1.setDictValue("男");
        dict1.setSortNo(1);
        dictRepository.save(dict1);

        SysDict dict2 = new SysDict();
        dict2.setId(2L);
        dict2.setDictType("gender");
        dict2.setDictKey("F");
        dict2.setDictValue("女");
        dict2.setSortNo(2);
        dictRepository.save(dict2);

        // 支付方式字典
        SysDict dict3 = new SysDict();
        dict3.setId(3L);
        dict3.setDictType("pay_method");
        dict3.setDictKey("cash");
        dict3.setDictValue("现金");
        dict3.setSortNo(1);
        dictRepository.save(dict3);

        SysDict dict4 = new SysDict();
        dict4.setId(4L);
        dict4.setDictType("pay_method");
        dict4.setDictKey("wx");
        dict4.setDictValue("微信");
        dict4.setSortNo(2);
        dictRepository.save(dict4);

        SysDict dict5 = new SysDict();
        dict5.setId(5L);
        dict5.setDictType("pay_method");
        dict5.setDictKey("ali");
        dict5.setDictValue("支付宝");
        dict5.setSortNo(3);
        dictRepository.save(dict5);
    }

    private void initializeRoomsAndBeds() {
        // 创建房间
        Room room1 = new Room();
        room1.setId(1L);
        room1.setRoomNo("101");
        room1.setRoomType("单人间");
        room1.setFloor(1);
        room1.setMaxBed(1);
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setId(2L);
        room2.setRoomNo("102");
        room2.setRoomType("双人间");
        room2.setFloor(1);
        room2.setMaxBed(2);
        roomRepository.save(room2);

        Room room3 = new Room();
        room3.setId(3L);
        room3.setRoomNo("201");
        room3.setRoomType("三人间");
        room3.setFloor(2);
        room3.setMaxBed(3);
        roomRepository.save(room3);

        // 创建床位
        Bed bed1 = new Bed();
        bed1.setRoomId(1L);
        bed1.setBedNo("101-1");
        bed1.setStatus(0);
        bedRepository.save(bed1);

        Bed bed2 = new Bed();
        bed2.setRoomId(2L);
        bed2.setBedNo("102-1");
        bed2.setStatus(0);
        bedRepository.save(bed2);

        Bed bed3 = new Bed();
        bed3.setRoomId(2L);
        bed3.setBedNo("102-2");
        bed3.setStatus(0);
        bedRepository.save(bed3);

        Bed bed4 = new Bed();
        bed4.setRoomId(3L);
        bed4.setBedNo("201-1");
        bed4.setStatus(0);
        bedRepository.save(bed4);

        Bed bed5 = new Bed();
        bed5.setRoomId(3L);
        bed5.setBedNo("201-2");
        bed5.setStatus(0);
        bedRepository.save(bed5);

        Bed bed6 = new Bed();
        bed6.setRoomId(3L);
        bed6.setBedNo("201-3");
        bed6.setStatus(0);
        bedRepository.save(bed6);
    }

    private void initializeCareLevels() {
        CareLevel level1 = new CareLevel();
        level1.setLevelCode("L1");
        level1.setLevelName("三级护理");
        level1.setDescription("自理老人");
        level1.setDailyPrice(new BigDecimal("30"));
        careLevelRepository.save(level1);

        CareLevel level2 = new CareLevel();
        level2.setLevelCode("L2");
        level2.setLevelName("二级护理");
        level2.setDescription("半自理老人");
        level2.setDailyPrice(new BigDecimal("60"));
        careLevelRepository.save(level2);

        CareLevel level3 = new CareLevel();
        level3.setLevelCode("L3");
        level3.setLevelName("一级护理");
        level3.setDescription("全护理老人");
        level3.setDailyPrice(new BigDecimal("100"));
        careLevelRepository.save(level3);
    }

    private void initializeFeeItems() {
        FeeItem item1 = new FeeItem();
        item1.setItemCode("ACC");
        item1.setItemName("住宿费");
        item1.setUnitPrice(new BigDecimal("50"));
        item1.setItemType("住宿");
        feeItemRepository.save(item1);

        FeeItem item2 = new FeeItem();
        item2.setItemCode("CARE");
        item2.setItemName("护理费");
        item2.setUnitPrice(new BigDecimal("60"));
        item2.setItemType("护理");
        feeItemRepository.save(item2);

        FeeItem item3 = new FeeItem();
        item3.setItemCode("MEAL");
        item3.setItemName("餐饮费");
        item3.setUnitPrice(new BigDecimal("30"));
        item3.setItemType("餐饮");
        feeItemRepository.save(item3);

        FeeItem item4 = new FeeItem();
        item4.setItemCode("MED");
        item4.setItemName("基础医疗");
        item4.setUnitPrice(new BigDecimal("20"));
        item4.setItemType("医疗");
        feeItemRepository.save(item4);
    }
}

