package cn.boom.service.user.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.model.entity.TbProfess;
import cn.boom.service.user.dao.ProfessDao;
import cn.boom.service.user.service.ProfessService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class ProfessServiceImpl implements ProfessService {

    @Resource
    private ProfessDao professDao;

    @Override
    public TbProfess findOneById(Long id) {
        return professDao.selectById(id);
    }

    @Override
    public TbProfess add(TbProfess tbProfess) {

        if (tbProfess == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        Date date = new Date();
        tbProfess.setCreated(date);
        tbProfess.setUpdated(date);
        tbProfess.setIsSend("0");

        professDao.insert(tbProfess);

        return tbProfess;
    }

    @Override
    public TbProfess update(TbProfess tbProfess) {

        if (tbProfess == null || tbProfess.getId() == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbProfess oldProfess = findOneById(tbProfess.getId());

        if (oldProfess.getIsSend().equals("1")) {
            ExceptionCast.cast("已发送，不可修改！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        oldProfess.setUpdated(new Date());
        oldProfess.setTitle(tbProfess.getTitle());
        oldProfess.setContext(tbProfess.getContext());

        professDao.updateById(oldProfess);

        return oldProfess;
    }

    @Override
    public TbProfess updateSend(Long id) {

        TbProfess profess = findOneById(id);

        if (profess == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        if (profess.getIsSend().equals("1")) {
            ExceptionCast.cast("已发送，无需再次发送！");
        }

        profess.setIsSend("1");
        professDao.updateById(profess);

        return profess;
    }

    @Override
    public TbProfess deleteById(Long id) {

        if (id == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbProfess tbProfess = findOneById(id);

        if (tbProfess == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        professDao.deleteById(tbProfess.getId());
        return tbProfess;
    }
}
