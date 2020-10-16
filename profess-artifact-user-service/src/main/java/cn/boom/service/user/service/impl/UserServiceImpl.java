package cn.boom.service.user.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.common.utils.BCryptUtil;
import cn.boom.framework.common.utils.RegexUtils;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.entity.TbUserRole;
import cn.boom.service.user.dao.UserDao;
import cn.boom.service.user.dao.UserRoleDao;
import cn.boom.service.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
//@PropertySource(value = {"classpath:rabbitmq.properties", "classpath:user.properties", "classpath:email.properties"}, encoding = "UTF-8")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserRoleDao userRoleDao;

    @Override
    public TbUser findOneById(Long id) {
        return userDao.selectById(id);
    }

    @Override
    public List<TbUser> findByIds(Long[] ids) {

        if (ids == null || ids.length == 0) {
            return null;
        }

        // 设置查询条件
        QueryWrapper<TbUser> wrapper = new QueryWrapper<TbUser>();

        for (int i = 0; i < ids.length; i++) {

            if (ids[i] == null) {
                ExceptionCast.cast("ids数组中包含null元素！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
            }

            if (i == ids.length - 1) {
                wrapper.eq("id", ids[i]);
            } else {
                wrapper.eq("id", ids[i]).or();
            }
        }

        List<TbUser> userList = userDao.selectList(wrapper);

        // 校验查出的 userList 必须包含 所有的 ids
        if (userList.size() == ids.length) {
            return userList;
        }

        // ids 中包含 数据库中不存在的 用户id
        List<Long> userIds = new ArrayList<Long>();

        for (TbUser user : userList) {
            userIds.add(user.getId());
        }

        // 非法Id集合
        List<Long> illegalUserIds = new ArrayList<Long>();

        for (Long uid : ids) {
            if (!userIds.contains(uid)) {
                illegalUserIds.add(uid);
            }
        }

        ExceptionCast.cast("ids数组包含数据库中不存在的用户id：" + illegalUserIds, ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        return null;
    }

    @Override
    public TbUser findOneByUsername(String username) {

        if (StringUtils.isEmpty(username)) {
            ExceptionCast.cast("username 不能为空！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

        List<TbUser> userList = userDao.selectList(queryWrapper);

        if (userList == null || userList.size() == 0) {
            ExceptionCast.cast("用户不存在！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        return userList.get(0);
    }

    @Override
    public IPage<TbUser> search(int pageNum, int pageSize, TbUser tbUser) {

        if (pageNum < 1) {
            pageNum = 1;
        }

        if (pageSize < 1) {
            pageSize = 5;
        }

        Page<TbUser> page = new Page<>(pageNum, pageSize);

        if (tbUser == null) {
            return userDao.selectPage(page, null);
        }

        QueryWrapper<TbUser> wrapper = new QueryWrapper<>();

        // id 精确匹配
        if (tbUser.getId() != null) {
            wrapper.eq("id", tbUser.getId());
        }

        // username 精确匹配
        if (tbUser.getUsername() != null) {
            wrapper.eq("username", tbUser.getUsername());
        }

        // phone 手机号 精确匹配
        if (!StringUtils.isEmpty(tbUser.getPhone())) {
            wrapper.eq("phone", tbUser.getPhone());
        }

        // ....

        return userDao.selectPage(page, wrapper);
    }

    @Override
    public TbUser add(TbUser user) {

        if (user == null) {
            ExceptionCast.cast("user is null ！");
        }

        if (StringUtils.isEmpty(user.getUsername()) ||
                user.getPassword() == null) {
            ExceptionCast.cast("用户信息不全面！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (isUsernameUsed(user.getUsername())) {
            ExceptionCast.cast("用户名已被使用！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        // 数据校验 end

        user.setPassword(BCryptUtil.encode(user.getPassword()));
        user.setStatus("1");

        userDao.insert(user);

        //为其添加角色...USER
        TbUserRole userRole = new TbUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2L);
        userRole.setStatus("1");
        userRole.setCreated(new Date());
        userRoleDao.insert(userRole);

        // ADMIN
        userRole.setUserId(user.getId());
        userRole.setRoleId(1L);
        userRole.setStatus("0");
        userRole.setCreated(new Date());
        userRoleDao.insert(userRole);

        return user;
    }

    @Override
    public boolean isUsernameUsed(String username) {

        QueryWrapper<TbUser> wrapper = new QueryWrapper<>();

        wrapper.eq("username", username);

        List<TbUser> users = userDao.selectList(wrapper);

        if (users == null || users.size() == 0) {
            return false;
        }

        return true;
    }


    @Override
    public boolean isPhoneUsed(String phone) {

        if (!RegexUtils.validateMobilePhone(phone)) {
            ExceptionCast.cast("手机号不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        QueryWrapper<TbUser> wrapper = new QueryWrapper<>();

        wrapper.eq("phone", phone);

        List<TbUser> users = userDao.selectList(wrapper);

        return users.size() != 0;
    }

    @Override
    public TbUser update(TbUser tbUser) {

        ExceptionCast.cast("方法还未实现！");

        return null;
    }

    @Override
    public TbUser updateStatusById(Long id, String status) {

        if (status == null || !status.equals("0") && !status.equals("1")) {
            ExceptionCast.cast("状态不合法！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }


        TbUser tbUser = userDao.selectById(id);

        if (tbUser == null) {
            ExceptionCast.cast("该用户不存在！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        tbUser.setStatus(status);
        userDao.updateById(tbUser);

        return tbUser;
    }
}

