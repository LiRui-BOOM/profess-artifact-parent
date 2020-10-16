package cn.boom.service.auth.service.impl;

import cn.boom.framework.model.entity.TbRole;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.entity.TbUserRole;
import cn.boom.service.auth.dao.RoleDao;
import cn.boom.service.auth.dao.UserDao;
import cn.boom.service.auth.dao.UserRoleDao;
import cn.boom.service.auth.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private RoleDao roleDao;

    @Resource
    private UserRoleDao userRoleDao;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        QueryWrapper<TbUser> UserQueryWrapper = new QueryWrapper<TbUser>();
        UserQueryWrapper.eq("username", s);
        List<TbUser> userList = userDao.selectList(UserQueryWrapper);

        if (userList == null || userList.size() == 0) {
            throw new UsernameNotFoundException("username:" + s);
        }

        TbUser tbUser = userList.get(0);

        // 封装角色信息
        QueryWrapper<TbUserRole> userRoleQueryWrapper = new QueryWrapper<TbUserRole>();
        userRoleQueryWrapper.eq("user_id", tbUser.getId());
        userRoleQueryWrapper.eq("status", "1"); //状态必须可用

        // 该user的所有userRole信息
        List<TbUserRole> userRoleList = userRoleDao.selectList(userRoleQueryWrapper);

        if (userRoleList == null || userRoleList.size() == 0) {
            tbUser.setRoleList(null);
            return tbUser;
        }

        QueryWrapper<TbRole> roleQueryWrapper = new QueryWrapper<TbRole>();

        for (int i = 0; i < userRoleList.size(); i++) {

            if (i == userRoleList.size() - 1) {
                roleQueryWrapper.eq("id", userRoleList.get(i).getRoleId());
            } else {
                roleQueryWrapper.eq("id", userRoleList.get(i).getRoleId()).or();
            }

        }

        List<TbRole> roleList = roleDao.selectList(roleQueryWrapper);

        tbUser.setRoleList(roleList);

        return tbUser;
    }
}
