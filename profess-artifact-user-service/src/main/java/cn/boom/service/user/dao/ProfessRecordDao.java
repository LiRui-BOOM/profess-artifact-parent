package cn.boom.service.user.dao;

import cn.boom.framework.model.entity.TbProfessRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfessRecordDao extends BaseMapper<TbProfessRecord> {
}
