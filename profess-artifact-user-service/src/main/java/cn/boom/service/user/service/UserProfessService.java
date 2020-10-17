package cn.boom.service.user.service;

import cn.boom.framework.model.entity.TbUserProfess;

public interface UserProfessService {

    public abstract TbUserProfess findOneById(Long id);

    public abstract TbUserProfess add(TbUserProfess tbUserProfess);

    public abstract TbUserProfess update(TbUserProfess tbUserProfess);

    public abstract TbUserProfess updateSendStatusById(Long id, String status);
}
