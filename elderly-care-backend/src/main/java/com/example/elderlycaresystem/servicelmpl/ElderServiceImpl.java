package com.example.elderlycaresystem.servicelmpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.elderlycaresystem.entity.Elder;
import com.example.elderlycaresystem.exception.BusinessException;
import com.example.elderlycaresystem.repository.ElderRepository;
import com.example.elderlycaresystem.service.ElderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 老人档案服务实现类
 */
@Service
public class ElderServiceImpl implements ElderService {

    @Autowired
    private ElderRepository elderRepository;

    @Override
    public Elder getElderById(Long id) {
        if (id == null) {
            throw new BusinessException("老人ID不能为空");
        }
        return elderRepository.selectById(id);
    }

    @Override
    public Elder getElderByNo(String elderNo) {
        if (!StringUtils.hasText(elderNo)) {
            throw new BusinessException("老人编号不能为空");
        }
        QueryWrapper<Elder> wrapper = new QueryWrapper<>();
        wrapper.eq("elder_no", elderNo);
        return elderRepository.selectOne(wrapper);
    }

    @Override
    public List<Elder> getAllElders() {
        return elderRepository.selectList(null);
    }

    @Override
    public List<Elder> getEldersByCondition(String name, Integer status, String careLevel) {
        QueryWrapper<Elder> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like("name", name);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (StringUtils.hasText(careLevel)) {
            wrapper.eq("care_level", careLevel);
        }
        return elderRepository.selectList(wrapper);
    }

    @Override
    public Long saveElder(Elder elder) {
        if (elder == null) {
            throw new BusinessException("老人信息不能为空");
        }
        
        // 检查老人编号是否已存在
        QueryWrapper<Elder> wrapper = new QueryWrapper<>();
        wrapper.eq("elder_no", elder.getElderNo());
        if (elder.getId() != null) {
            // 更新操作，排除当前老人
            wrapper.ne("id", elder.getId());
        }
        int count = elderRepository.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("老人编号已存在");
        }
        
        if (elder.getId() == null) {
            // 新增老人
            elder.setCreateTime(LocalDateTime.now());
            elderRepository.insert(elder);
            return elder.getId();
        } else {
            // 更新老人
            elderRepository.updateById(elder);
            return elder.getId();
        }
    }

    @Override
    public boolean updateElder(Elder elder) {
        if (elder == null || elder.getId() == null) {
            throw new BusinessException("老人信息不能为空");
        }
        
        // 检查老人编号是否已存在
        QueryWrapper<Elder> wrapper = new QueryWrapper<>();
        wrapper.eq("elder_no", elder.getElderNo()).ne("id", elder.getId());
        int count = elderRepository.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("老人编号已存在");
        }
        
        int result = elderRepository.updateById(elder);
        return result > 0;
    }

    @Override
    public boolean deleteElder(Long id) {
        if (id == null) {
            throw new BusinessException("老人ID不能为空");
        }
        int result = elderRepository.deleteById(id);
        return result > 0;
    }

    @Override
    public int getIn院ElderCount() {
        QueryWrapper<Elder> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1); // 在院状态
        return elderRepository.selectCount(wrapper).intValue();
    }

    @Override
    public List<Elder> getEldersByRoomId(Long roomId) {
        if (roomId == null) {
            throw new BusinessException("房间ID不能为空");
        }
        QueryWrapper<Elder> wrapper = new QueryWrapper<>();
        wrapper.eq("bed_id", roomId); // 注意：这里实际应该是通过bed表关联
        return elderRepository.selectList(wrapper);
    }
}