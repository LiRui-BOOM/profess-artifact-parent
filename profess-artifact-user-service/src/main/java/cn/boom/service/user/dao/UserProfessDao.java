package cn.boom.service.user.dao;

import cn.boom.framework.model.entity.TbUserProfess;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfessDao extends BaseMapper<TbUserProfess> {
}
