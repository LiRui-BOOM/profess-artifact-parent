package cn.boom.service.user.dao;

import cn.boom.framework.model.entity.TbUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleDao extends BaseMapper<TbUserRole> {
}
