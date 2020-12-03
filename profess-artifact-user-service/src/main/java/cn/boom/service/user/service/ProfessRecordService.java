package cn.boom.service.user.service;

import cn.boom.framework.model.entity.TbProfessRecord;
import cn.boom.framework.model.entity.union.ProfessRecordEntity;
import cn.boom.framework.model.vo.ProfessRecordVo;

import java.util.List;

public interface ProfessRecordService {

    public abstract TbProfessRecord findOneById(Long id);

    public abstract ProfessRecordEntity findEntityById(Long id);

    public abstract ProfessRecordEntity findEntityByFromTo(Long from,Long to);

    public abstract List<TbProfessRecord> findListByFromId(Long from);

    public abstract List<ProfessRecordEntity> findEntityListByFromId(Long from);

    public abstract List<TbProfessRecord> findListByToId(Long to);

    public abstract List<ProfessRecordEntity> findEntityListByToId(Long to);

    public abstract ProfessRecordEntity add(ProfessRecordVo professRecordVo);

    public abstract List<ProfessRecordEntity> matching(Long toId);

    public abstract ProfessRecordEntity updateToIdStatus(Long id,Long toId,String status);

    public abstract ProfessRecordEntity updateStatusById(Long id, String status);

    public abstract void deleteIds(Long[] ids);
}
