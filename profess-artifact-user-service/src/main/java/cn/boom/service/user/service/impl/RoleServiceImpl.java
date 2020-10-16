package cn.boom.service.user.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.model.entity.TbRole;
import cn.boom.service.user.dao.RoleDao;
import cn.boom.service.user.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleDao roleDao;

    @Override
    public TbRole findOneById(Long id) {
        return roleDao.selectById(id);
    }

    @Override
    public List<TbRole> findAll() {
        return roleDao.selectList(null);
    }

    @Override
    public IPage<TbRole> search(int pageNum, int pageSize, TbRole role) {

        if (pageNum < 1) {
            pageNum = 1;
        }

        if (pageSize < 1) {
            pageSize = 5;
        }

        Page<TbRole> page = new Page<>(pageNum, pageSize);

        if (role == null) {
            return roleDao.selectPage(page, null);
        }

        QueryWrapper<TbRole> wrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(role.getRoleName())) {
            wrapper.like("role_name", role.getRoleName());
        }

        if (role.getId() != null) {
            wrapper.eq("id", role.getId());
        }

        return roleDao.selectPage(page, wrapper);
    }

    @Override
    public TbRole update(TbRole tbRole) {

        if (tbRole == null || tbRole.getId() == null || StringUtils.isEmpty(tbRole.getRoleName())
                || StringUtils.isEmpty(tbRole.getRoleDesc())) {
            ExceptionCast.cast("参数不完整！");
        }

        TbRole oldRole = roleDao.selectById(tbRole.getId());

        if (oldRole == null) {
            ExceptionCast.cast("该数据在数据库中不存在！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        oldRole.setRoleName(tbRole.getRoleName());
        oldRole.setRoleDesc(tbRole.getRoleDesc());

        roleDao.updateById(oldRole);

        return oldRole;
    }
}
