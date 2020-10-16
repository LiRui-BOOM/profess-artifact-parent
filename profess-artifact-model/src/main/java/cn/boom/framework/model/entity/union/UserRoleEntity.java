package cn.boom.framework.model.entity.union;

import cn.boom.framework.model.entity.TbRole;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.entity.TbUserRole;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRoleEntity implements Serializable {

    private TbUser tbUser;
    private TbRole tbRole;
    private TbUserRole tbUserRole;
}
