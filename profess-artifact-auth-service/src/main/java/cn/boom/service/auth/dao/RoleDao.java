package cn.boom.service.auth.dao;

import cn.boom.framework.model.entity.TbRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleDao extends BaseMapper<TbRole> {
}
