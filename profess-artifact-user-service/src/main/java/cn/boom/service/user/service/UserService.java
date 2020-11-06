package cn.boom.service.user.service;


import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.vo.BaiduCryptVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface UserService {

    public TbUser findOneById(Long id);

    public List<TbUser> findByIds(Long[] ids);

    public TbUser findOneByUsername(String username);

    public IPage<TbUser> search(int pageNum, int pageSize, TbUser tbUser);

    public TbUser add(TbUser tbUser);

    public boolean isUsernameUsed(String username);

    public boolean isPhoneUsed(String phone);

    public TbUser update(TbUser tbUser);

    public TbUser updateStatusById(Long id, String status);

    public void sendMessageTest();

    public TbUser getPhoneNumber(BaiduCryptVo baiduCryptVo, String token);
}
