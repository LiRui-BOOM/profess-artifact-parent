package cn.boom.framework.model.entity.union;

import cn.boom.framework.model.entity.TbProfess;
import cn.boom.framework.model.entity.TbProfessRecord;
import cn.boom.framework.model.entity.TbUser;
import lombok.Data;

@Data
public class ProfessRecordEntity {
    private TbProfessRecord professRecord;
    private TbProfess profess;
    private TbUser from;
    private TbUser to;
}
