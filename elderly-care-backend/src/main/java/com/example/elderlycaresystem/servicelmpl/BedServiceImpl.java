package com.example.elderlycaresystem.servicelmpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.elderlycaresystem.entity.Bed;
import com.example.elderlycaresystem.exception.BusinessException;
import com.example.elderlycaresystem.repository.BedRepository;
import com.example.elderlycaresystem.service.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 床位服务实现类
 */
@Service
public class BedServiceImpl implements BedService {

    @Autowired
    private BedRepository bedRepository;

    @Override
    public Bed getBedById(Long id) {
        if (id == null) {
            throw new BusinessException("床位ID不能为空");
        }
        return bedRepository.selectById(id);
    }

    @Override
    public List<Bed> getAllBeds() {
        return bedRepository.selectList(null);
    }

    @Override
    public List<Bed> getBedsByRoomId(Long roomId) {
        if (roomId == null) {
            throw new BusinessException("房间ID不能为空");
        }
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", roomId);
        return bedRepository.selectList(wrapper);
    }

    @Override
    public List<Bed> getBedsByStatus(Integer status) {
        if (status == null) {
            throw new BusinessException("状态不能为空");
        }
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        return bedRepository.selectList(wrapper);
    }

    @Override
    public Long saveBed(Bed bed) {
        if (bed == null) {
            throw new BusinessException("床位信息不能为空");
        }
        
        // 检查房间和床号是否已存在
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", bed.getRoomId()).eq("bed_no", bed.getBedNo());
        if (bed.getId() != null) {
            // 更新操作，排除当前床位
            wrapper.ne("id", bed.getId());
        }
        int count = bedRepository.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该房间床号已存在");
        }
        
        if (bed.getId() == null) {
            // 新增床位
            bedRepository.insert(bed);
            return bed.getId();
        } else {
            // 更新床位
            bedRepository.updateById(bed);
            return bed.getId();
        }
    }

    @Override
    public boolean updateBed(Bed bed) {
        if (bed == null || bed.getId() == null) {
            throw new BusinessException("床位信息不能为空");
        }
        
        // 检查房间和床号是否已存在
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", bed.getRoomId()).eq("bed_no", bed.getBedNo()).ne("id", bed.getId());
        int count = bedRepository.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该房间床号已存在");
        }
        
        int result = bedRepository.updateById(bed);
        return result > 0;
    }

    @Override
    public boolean deleteBed(Long id) {
        if (id == null) {
            throw new BusinessException("床位ID不能为空");
        }
        int result = bedRepository.deleteById(id);
        return result > 0;
    }

    @Override
    public int getAvailableBedCount() {
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0); // 状态为0表示空闲
        return bedRepository.selectCount(wrapper).intValue();
    }

    @Override
    public int getUsedBedCount() {
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1); // 状态为1表示已入住
        return bedRepository.selectCount(wrapper).intValue();
    }

    @Override
    public int getUnderRepairBedCount() {
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 2); // 状态为2表示维修
        return bedRepository.selectCount(wrapper).intValue();
    }

    @Override
    public Bed getBedByRoomIdAndBedNo(Long roomId, String bedNo) {
        if (roomId == null || !StringUtils.hasText(bedNo)) {
            throw new BusinessException("房间ID和床号不能为空");
        }
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", roomId).eq("bed_no", bedNo);
        return bedRepository.selectOne(wrapper);
    }
}