package cn.boom.service.user.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.model.entity.TbRole;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.entity.TbUserRole;
import cn.boom.framework.model.entity.union.UserRoleEntity;
import cn.boom.service.user.dao.RoleDao;
import cn.boom.service.user.dao.UserDao;
import cn.boom.service.user.dao.UserRoleDao;
import cn.boom.service.user.service.UserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private UserDao userDao;

    @Resource
    private RoleDao roleDao;

    @Override
    public TbUserRole findOneById(Long id) {
        return userRoleDao.selectById(id);
    }

    @Override
    public IPage<UserRoleEntity> search(int pageNum, int pageSize, TbUserRole tbUserRole) {

        if (pageNum < 1) {
            pageNum = 1;
        }

        if (pageSize < 1) {
            pageSize = 5;
        }

        QueryWrapper<TbUserRole> wrapper = new QueryWrapper<>();

        if (tbUserRole.getId() != null) {
            wrapper.eq("id", tbUserRole.getId());
        }

        if (tbUserRole.getUserId() != null) {
            wrapper.eq("user_id", tbUserRole.getUserId());
        }

        if (tbUserRole.getRoleId() != null) {
            wrapper.eq("role_id", tbUserRole.getRoleId());
        }

        if (!StringUtils.isEmpty(tbUserRole.getStatus())) {
            wrapper.eq("status", tbUserRole.getStatus());
        }

        List<TbUserRole> tbUserRoles = userRoleDao.selectList(wrapper);

        List<UserRoleEntity> retList = new ArrayList<>();

        int start = (pageNum - 1) * pageSize;
        int end = pageNum * pageSize > tbUserRoles.size() ? tbUserRoles.size() : pageNum * pageSize;

        for (int i = start; i < end; i++) {

            TbUser user = userDao.selectById(tbUserRoles.get(i).getUserId());
            TbRole tbRole = roleDao.selectById(tbUserRoles.get(i).getRoleId());

            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setTbRole(tbRole);
            userRoleEntity.setTbUser(user);
            userRoleEntity.setTbUserRole(tbUserRoles.get(i));
            retList.add(userRoleEntity);
        }

        Page<UserRoleEntity> page = new Page<>(pageNum, pageSize);

        page.setRecords(retList);
        page.setTotal(tbUserRoles.size());
        page.setCurrent(pageNum);
        page.setSize(pageSize);

        return page;
    }

    @Override
    public TbUserRole updateStatus(Long id, String status) {

        if (id == null || !status.equals("0") && !status.equals("1")) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbUserRole userRole = findOneById(id);

        if (userRole == null) {
            ExceptionCast.cast("该数据在数据库中不存在！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        userRole.setStatus(status);
        userRoleDao.updateById(userRole);

        return userRole;
    }

    @Override
    public TbUserRole add(TbUserRole tbUserRole) {

        if (tbUserRole == null || tbUserRole.getRoleId() == null
                || tbUserRole.getUserId() == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        tbUserRole.setCreated(new Date());
        tbUserRole.setStatus("1"); //可用

        userRoleDao.insert(tbUserRole);

        return tbUserRole;
    }

    @Override
    public UserRoleEntity add(UserRoleEntity userRoleEntity) {

        /*参数校验开始*/
        if (userRoleEntity == null || userRoleEntity.getTbUser() == null || userRoleEntity.getTbRole() == null || userRoleEntity.getTbUserRole() == null
                || StringUtils.isEmpty(userRoleEntity.getTbUser().getUsername()) || userRoleEntity.getTbRole().getId() == null
                || StringUtils.isEmpty(userRoleEntity.getTbUserRole().getStatus()) || !userRoleEntity.getTbUserRole().getStatus().equals("0") && !userRoleEntity.getTbUserRole().getStatus().equals("1")) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        QueryWrapper<TbUser> userWrapper = new QueryWrapper<>();
        userWrapper.eq("username", userRoleEntity.getTbUser().getUsername());
        List<TbUser> users = userDao.selectList(userWrapper);

        if (users == null || users.size() == 0) {
            ExceptionCast.cast("该用户在数据库中不存在！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        TbUser tbUser = users.get(0);

        TbRole tbRole = roleDao.selectById(userRoleEntity.getTbRole().getId());

        if (tbRole == null) {
            ExceptionCast.cast("该角色在数据库中不存在！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        /*校验结束*/
        TbUserRole userRole = new TbUserRole();

        userRole.setRoleId(tbRole.getId());
        userRole.setUserId(tbUser.getId());
        userRole.setStatus(userRoleEntity.getTbUserRole().getStatus());
        userRole.setCreated(new Date());

        //校验数据重复
        if (hasUserRole(tbUser.getId(), tbRole.getId())) {
            ExceptionCast.cast("该用户拥有此角色，请进行授权/撤销授权操作", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        userRoleDao.insert(userRole);

        userRoleEntity.setTbUser(tbUser);
        userRoleEntity.setTbRole(tbRole);
        userRoleEntity.setTbUserRole(userRole);

        return userRoleEntity;
    }

    @Override
    public boolean hasUserRole(Long userId, Long roleId) {

        QueryWrapper<TbUserRole> wrapper = new QueryWrapper<>();

        wrapper.eq("user_id", userId);
        wrapper.eq("role_id", roleId);

        return userRoleDao.selectList(wrapper).size() != 0;
    }
}
