package cn.boom.service.auth.dao;

import cn.boom.framework.model.entity.TbUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<TbUser> {
}
