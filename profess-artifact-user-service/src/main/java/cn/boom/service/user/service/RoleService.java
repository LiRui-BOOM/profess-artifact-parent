package cn.boom.service.user.service;

import cn.boom.framework.model.entity.TbRole;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface RoleService {

    public TbRole findOneById(Long id);


    public List<TbRole> findAll();

    public IPage<TbRole> search(int pageNum, int pageSize, TbRole role);

    public TbRole update(TbRole tbRole);
}
