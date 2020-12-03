package cn.boom.service.user.service;


import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.vo.BaiduCryptVo;
import cn.boom.framework.model.vo.UpdatePasswordByEmailVo;
import cn.boom.framework.model.vo.UpdatePasswordByOldPasswordVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface UserService {

    public TbUser findOneById(Long id);

    public List<TbUser> findByIds(Long[] ids);

    public TbUser findOneByUsername(String username);

    public TbUser findOneByEmail(String email);

    public TbUser findOneByPhone(String phone);

    public IPage<TbUser> search(int pageNum, int pageSize, TbUser tbUser);

    public TbUser add(TbUser tbUser);

    public boolean isUsernameUsed(String username);

    public boolean isPhoneUsed(String phone);

    public TbUser update(TbUser tbUser);

    public TbUser updateStatusById(Long id, String status);

    public void sendMessageTest();

    public TbUser getPhoneNumber(BaiduCryptVo baiduCryptVo, String token);

    /**
     * 发送用于验证邮箱的验证码
     * @param id
     * @param email
     */
    public void sendBindingCheckCodeEmail(Long id, String email);

    /**
     * 验证邮箱，完成绑定
     * @param id
     * @param token
     */
    public TbUser bindingEmailByToken(Long id, String token);

    /**
     *  发送邮箱重置密码的验证邮箱
     * @param email
     */
    public TbUser sendUpdatePasswordCheckCodeEmail(String email);


    /**
     * 根据邮箱验证码，重置密码
     * @param vo
     */
    public void updatePasswordByEmailCheckCode(UpdatePasswordByEmailVo vo);

    /**
     * 根据oldpassword修改密码
     * @param vo
     */
    public void updatePasswordByOldPassword(UpdatePasswordByOldPasswordVo vo);
}
