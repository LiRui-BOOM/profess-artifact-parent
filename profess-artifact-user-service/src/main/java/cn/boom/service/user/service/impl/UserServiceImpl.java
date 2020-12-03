package cn.boom.service.user.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.common.utils.*;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.entity.TbUserRole;
import cn.boom.framework.model.enums.TencentSignEnum;
import cn.boom.framework.model.enums.TencentTemplateEnum;
import cn.boom.framework.model.model.BaiduPhoneEntity;
import cn.boom.framework.model.model.EmailMessage;
import cn.boom.framework.model.model.Payload;
import cn.boom.framework.model.model.SMSParameter;
import cn.boom.framework.model.vo.BaiduCryptVo;
import cn.boom.framework.model.vo.BaiduLoginCallBackVo;
import cn.boom.framework.model.vo.UpdatePasswordByEmailVo;
import cn.boom.framework.model.vo.UpdatePasswordByOldPasswordVo;
import cn.boom.service.user.config.RsaConfiguration;
import cn.boom.service.user.dao.UserDao;
import cn.boom.service.user.dao.UserRoleDao;
import cn.boom.service.user.service.ProfessRecordService;
import cn.boom.service.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.encrypt.RsaProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@PropertySource(value = {"classpath:rabbitmq.properties", "classpath:user.properties"}, encoding = "UTF-8")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserRoleDao userRoleDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RsaConfiguration rsaConfiguration;

    @Autowired
    private ProfessRecordService professRecordService;

    @Override
    public TbUser findOneById(Long id) {
        return userDao.selectById(id);
    }

    @Override
    public List<TbUser> findByIds(Long[] ids) {

        if (ids == null || ids.length == 0) {
            return null;
        }

        // 设置查询条件
        QueryWrapper<TbUser> wrapper = new QueryWrapper<TbUser>();

        for (int i = 0; i < ids.length; i++) {

            if (ids[i] == null) {
                ExceptionCast.cast("ids数组中包含null元素！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
            }

            if (i == ids.length - 1) {
                wrapper.eq("id", ids[i]);
            } else {
                wrapper.eq("id", ids[i]).or();
            }
        }

        List<TbUser> userList = userDao.selectList(wrapper);

        // 校验查出的 userList 必须包含 所有的 ids
        if (userList.size() == ids.length) {
            return userList;
        }

        // ids 中包含 数据库中不存在的 用户id
        List<Long> userIds = new ArrayList<Long>();

        for (TbUser user : userList) {
            userIds.add(user.getId());
        }

        // 非法Id集合
        List<Long> illegalUserIds = new ArrayList<Long>();

        for (Long uid : ids) {
            if (!userIds.contains(uid)) {
                illegalUserIds.add(uid);
            }
        }

        ExceptionCast.cast("ids数组包含数据库中不存在的用户id：" + illegalUserIds, ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        return null;
    }

    @Override
    public TbUser findOneByUsername(String username) {

        if (StringUtils.isEmpty(username)) {
            ExceptionCast.cast("username 不能为空！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

        List<TbUser> userList = userDao.selectList(queryWrapper);

        if (userList == null || userList.size() == 0) {
            return null;
        }

        return userList.get(0);
    }

    @Override
    public TbUser findOneByEmail(String email) {

        if (email == null) {
            return null;
        }

        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email.toLowerCase());

        List<TbUser> userList = userDao.selectList(queryWrapper);

        if (userList == null || userList.size() == 0) {
            return null;
        }

        return userList.get(0);
    }

    @Override
    public TbUser findOneByPhone(String phone) {

        if (!RegexUtils.validateMobilePhone(phone)) {
            ExceptionCast.cast("手机号格式非法！",ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);

        List<TbUser> userList = userDao.selectList(queryWrapper);

        if (userList == null || userList.size() == 0) {
            return null;
        }

        return userList.get(0);
    }

    @Override
    public IPage<TbUser> search(int pageNum, int pageSize, TbUser tbUser) {

        if (pageNum < 1) {
            pageNum = 1;
        }

        if (pageSize < 1) {
            pageSize = 5;
        }

        Page<TbUser> page = new Page<>(pageNum, pageSize);

        if (tbUser == null) {
            return userDao.selectPage(page, null);
        }

        QueryWrapper<TbUser> wrapper = new QueryWrapper<>();

        // id 精确匹配
        if (tbUser.getId() != null) {
            wrapper.eq("id", tbUser.getId());
        }

        // username 精确匹配
        if (tbUser.getUsername() != null) {
            wrapper.eq("username", tbUser.getUsername());
        }

        // phone 手机号 精确匹配
        if (!StringUtils.isEmpty(tbUser.getPhone())) {
            wrapper.eq("phone", tbUser.getPhone());
        }

        // ....

        return userDao.selectPage(page, wrapper);
    }


    @Value("${DEFAULT_USER_PIC_URL}")
    private String DEFAULT_USER_PIC_URL;

    @Override
    public TbUser add(TbUser user) {

        if (user == null) {
            ExceptionCast.cast("user is null ！");
        }

        if (StringUtils.isEmpty(user.getUsername())) {
            ExceptionCast.cast("username is null！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (isUsernameUsed(user.getUsername())) {
            ExceptionCast.cast("用户名已被使用！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        // 数据校验 end
        user.setStatus("1");
        user.setNickName("游客" + UUID.randomUUID().toString().substring(0, 6));
        user.setUserPic(DEFAULT_USER_PIC_URL);
        userDao.insert(user);

        //为其添加角色...USER
        TbUserRole userRole = new TbUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2L);
        userRole.setStatus("1");
        userRole.setCreated(new Date());
        userRoleDao.insert(userRole);

        // ADMIN
        userRole.setUserId(user.getId());
        userRole.setRoleId(1L);
        userRole.setStatus("0");
        userRole.setCreated(new Date());
        userRoleDao.insert(userRole);

        return user;
    }

    @Override
    public boolean isUsernameUsed(String username) {

        QueryWrapper<TbUser> wrapper = new QueryWrapper<>();

        wrapper.eq("username", username);

        List<TbUser> users = userDao.selectList(wrapper);

        if (users == null || users.size() == 0) {
            return false;
        }

        return true;
    }


    @Override
    public boolean isPhoneUsed(String phone) {

        if (!RegexUtils.validateMobilePhone(phone)) {
            ExceptionCast.cast("手机号不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        QueryWrapper<TbUser> wrapper = new QueryWrapper<>();

        wrapper.eq("phone", phone);

        List<TbUser> users = userDao.selectList(wrapper);

        return users.size() != 0;
    }

    @Override
    public TbUser update(TbUser tbUser) {

        if (tbUser == null || tbUser.getId() == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbUser oldUser = findOneById(tbUser.getId());

        if (oldUser == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        // 不允许修改的内容
        tbUser.setUsername(oldUser.getUsername());
        tbUser.setPassword(oldUser.getPassword());
        tbUser.setPhone(oldUser.getPhone());
        tbUser.setStatus(oldUser.getStatus());
        userDao.updateById(tbUser);
        return tbUser;
    }

    @Override
    public TbUser updateStatusById(Long id, String status) {

        if (status == null || !status.equals("0") && !status.equals("1")) {
            ExceptionCast.cast("状态不合法！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }


        TbUser tbUser = userDao.selectById(id);

        if (tbUser == null) {
            ExceptionCast.cast("该用户不存在！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        tbUser.setStatus(status);
        userDao.updateById(tbUser);

        return tbUser;
    }

    @Value("${EMAIL_EXCHANGE}")
    private String EMAIL_EXCHANGE;

    @Value("${EMAIL_ROUTINGKEY}")
    private String EMAIL_ROUTINGKEY;

    @Override
    public void sendMessageTest() {

        EmailMessage message = new EmailMessage();
        message.setToEmail("2495399053@qq.com");
        message.setText("ok");
        message.setTitle("ok");

        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTINGKEY, JsonUtils.toString(message));
    }

    @Override
    public TbUser getPhoneNumber(BaiduCryptVo baiduCryptVo, String token) {

        BaiduCryptUtils cryptUtils = new BaiduCryptUtils();

        System.out.println(baiduCryptVo);

        Payload<TbUser> payload = JwtUtils.getInfoFromToken(token, rsaConfiguration.getPublicKey(), TbUser.class);
        TbUser tbUser = payload.getUserInfo();

        if (tbUser == null) {
            ExceptionCast.cast("不合法的Authorization!", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        String sessionKey = tbUser.getSessionKey();

        String s = null;
        try {
            s = cryptUtils.decrypt(baiduCryptVo.getEncryptedData(), sessionKey, baiduCryptVo.getIv());
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast("数据解密失败", 500);
        }
        BaiduPhoneEntity phoneEntity = JsonUtils.toBean(s, BaiduPhoneEntity.class);
        tbUser.setPhone(phoneEntity.getMobile());

        userDao.updateById(tbUser);
        professRecordService.matching(tbUser.getId());

        //屏蔽sessionKey
        tbUser.setSessionKey(null);
        tbUser.setRoleList(null);

        return tbUser;
    }

    private boolean isEmailUsed(String email) {
        QueryWrapper<TbUser> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        List<TbUser> users = userDao.selectList(wrapper);
        return users.size() != 0;
    }

    @Override
    public void sendBindingCheckCodeEmail(Long id, String email) {
        /*参数校验开始*/
        if (id == null || !RegexUtils.validateEmail(email)) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        email = email.toLowerCase(); //全小写

        TbUser user = findOneById(id);

        if (user == null) {
            ExceptionCast.cast("该数据在数据库中不存在！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        if (!StringUtils.isEmpty(user.getEmail())) {
            ExceptionCast.cast("该用户已绑定过邮箱！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (isEmailUsed(email)) {
            ExceptionCast.cast("该邮箱已绑定过账号！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        /*参数校验结束*/

        // 生成token，设置过期时间
        String token = TokenUtils.getToken();

        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps("USER_EMAIL_BINDING_CHECK_TOKEN-" + user.getId());
        boundHashOperations.put(user.getId(), email + "-" + token);
        // 10分钟过期
        redisTemplate.expire("USER_EMAIL_BINDING_CHECK_TOKEN-" + user.getId(), 10 * 60, TimeUnit.SECONDS);

        // 发送邮箱验证码
        sendBindingCheckCodeEmail(user.getNickName(), email, token);
    }

    @Value("${EMAIL_BINDING_CHECK_CODE_CONTENT}")
    private String EMAIL_BINDING_CHECK_CODE_CONTENT;
    @Value("${EMAIL_BINDING_CHECK_CODE_TITLE}")
    private String EMAIL_BINDING_CHECK_CODE_TITLE;

    private void sendBindingCheckCodeEmail(String nickName, String email, String token) {

        String content = EMAIL_BINDING_CHECK_CODE_CONTENT;
        content = content.replace("{{name}}", nickName);
        content = content.replace("{{code}}", token);
        content = content.replace("{{time}}", DateUtil.getNowTime());

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setToEmail(email);
        emailMessage.setText(content);
        emailMessage.setTitle(EMAIL_BINDING_CHECK_CODE_TITLE);

        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTINGKEY, JsonUtils.toString(emailMessage));
    }

    @Override
    public TbUser bindingEmailByToken(Long id, String token) {
        if (id == null || StringUtils.isEmpty(token)) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbUser user = findOneById(id);

        if (user == null) {
            ExceptionCast.cast("该数据在数据库中不存在！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        //开始检验验证码
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps("USER_EMAIL_BINDING_CHECK_TOKEN-" + user.getId());

        Object o = boundHashOperations.get(user.getId());

        String tokenInfo = (String) o;

        if (StringUtils.isEmpty(tokenInfo)) {
            ExceptionCast.cast("验证码过期！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        String[] infos = tokenInfo.split("-");

        String email = infos[0];
        String realToken = infos[1];

        if (!realToken.equals(token)) {
            ExceptionCast.cast("验证码错误！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        //保存信息 email 均为小写
        user.setEmail(email);
        userDao.updateById(user);

        //删除验证码
        boundHashOperations.delete(user.getId());

        // 绑定邮箱后匹配表白信息
        professRecordService.matching(user.getId());

        return user;
    }

    @Override
    public TbUser sendUpdatePasswordCheckCodeEmail(String email) {
        ExceptionCast.cast("未实现！");
        return null;
    }

    @Override
    public void updatePasswordByEmailCheckCode(UpdatePasswordByEmailVo vo) {
        ExceptionCast.cast("未实现！");
    }

    @Override
    public void updatePasswordByOldPassword(UpdatePasswordByOldPasswordVo vo) {
        if (vo == null || vo.getUserId() == null || StringUtils.isEmpty(vo.getOldPassword())
                || StringUtils.isEmpty(vo.getNewPassword())) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbUser user = findOneById(vo.getUserId());

        if (user == null) {
            ExceptionCast.cast("该用户不存在！", ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        // 密码校验

        if (!BCryptUtil.matches(vo.getOldPassword(),user.getPassword())) {
            ExceptionCast.cast("旧密码错误！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        user.setPassword(BCryptUtil.encode(vo.getNewPassword()));
        userDao.updateById(user);
    }
}

