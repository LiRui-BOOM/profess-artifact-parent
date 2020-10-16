package cn.boom.service.user.service;

import cn.boom.framework.model.entity.TbUserRole;
import cn.boom.framework.model.entity.union.UserRoleEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface UserRoleService {

    public abstract TbUserRole findOneById(Long id);

    public abstract IPage<UserRoleEntity> search(int pageNum, int pageSize, TbUserRole tbUserRole);

    public abstract TbUserRole updateStatus(Long id, String status);

    public abstract TbUserRole add(TbUserRole tbUserRole);

    public abstract UserRoleEntity add(UserRoleEntity userRoleEntity);

    public abstract boolean hasUserRole(Long userId, Long roleId);
}
