package cn.boom.service.user.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.entity.TbUserProfess;
import cn.boom.service.user.dao.UserProfessDao;
import cn.boom.service.user.service.UserProfessService;
import cn.boom.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserProfessServiceImpl implements UserProfessService {

    @Resource
    private UserProfessDao userProfessDao;

    @Autowired
    private UserService userService;

    @Override
    public TbUserProfess findOneById(Long id) {
        return userProfessDao.selectById(id);
    }

    @Override
    public TbUserProfess add(TbUserProfess tbUserProfess) {

        if (tbUserProfess == null || tbUserProfess.getConfessorId() == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        // 检查数据合法性
        if (tbUserProfess.getConfessedId() != null) {
            TbUser one = userService.findOneById(tbUserProfess.getConfessedId());

            if (one == null) {
                ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
            }
        }

        tbUserProfess.setCreated(new Date());
        tbUserProfess.setUpdated(new Date());
        tbUserProfess.setId(null);
        tbUserProfess.setIsSend("0");

        userProfessDao.insert(tbUserProfess);

        return tbUserProfess;
    }

    @Override
    public TbUserProfess update(TbUserProfess tbUserProfess) {

        if (tbUserProfess == null || tbUserProfess.getId() == null
                || tbUserProfess.getConfessorId() == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbUserProfess oldProfess = userProfessDao.selectById(tbUserProfess.getId());

        if (oldProfess == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        // 检查数据合法性
        if (!tbUserProfess.getConfessedId().equals(oldProfess.getConfessedId())) {
            TbUser one = userService.findOneById(tbUserProfess.getConfessedId());

            if (one == null) {
                ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
            }
        }

        // 不允许修改发送状态
        if (!oldProfess.getIsSend().equals(tbUserProfess.getIsSend())) {
            ExceptionCast.cast("不允许修改发送状态！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (tbUserProfess.getIsSend().equals("1")) {
            ExceptionCast.cast("已发送，不可修改！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        oldProfess.setUpdated(new Date());
        oldProfess.setConfessedId(tbUserProfess.getConfessedId());
        oldProfess.setTitle(tbUserProfess.getTitle());
        oldProfess.setContext(tbUserProfess.getContext());

        userProfessDao.updateById(oldProfess);

        return oldProfess;
    }

    @Override
    public TbUserProfess updateSendStatusById(Long id, String status) {

        if (id == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbUserProfess oldProfess = userProfessDao.selectById(id);

        if (oldProfess == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        oldProfess.setIsSend(status);
        oldProfess.setUpdated(new Date());

        userProfessDao.updateById(oldProfess);

        return oldProfess;
    }
}
