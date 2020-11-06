package cn.boom.service.user.service;

import cn.boom.framework.model.entity.TbProfess;

public interface ProfessService {

    public abstract TbProfess findOneById(Long id);

    public abstract TbProfess add(TbProfess tbProfess);

    public abstract TbProfess update(TbProfess tbProfess);

    public abstract TbProfess updateSend(Long id);

    public abstract TbProfess deleteById(Long id);
}
