package cn.boom.service.user.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.common.utils.DateUtil;
import cn.boom.framework.common.utils.JsonUtils;
import cn.boom.framework.common.utils.RegexUtils;
import cn.boom.framework.model.entity.TbProfess;
import cn.boom.framework.model.entity.TbProfessRecord;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.entity.union.ProfessRecordEntity;
import cn.boom.framework.model.model.EmailMessage;
import cn.boom.framework.model.vo.ProfessRecordVo;
import cn.boom.service.user.dao.ProfessRecordDao;
import cn.boom.service.user.service.ProfessRecordService;
import cn.boom.service.user.service.ProfessService;
import cn.boom.service.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@PropertySource(value = {"classpath:rabbitmq.properties", "classpath:email.properties"}, encoding = "UTF-8")
@Service
public class ProfessRecordServiceImpl implements ProfessRecordService {

    @Resource
    private ProfessRecordDao professRecordDao;

    @Autowired
    private ProfessService professService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${EMAIL_EXCHANGE}")
    private String EMAIL_EXCHANGE;

    @Value("${EMAIL_ROUTINGKEY}")
    private String EMAIL_ROUTINGKEY;

    @Override
    public TbProfessRecord findOneById(Long id) {
        return professRecordDao.selectById(id);
    }

    @Override
    public ProfessRecordEntity findEntityById(Long id) {

        TbProfessRecord tbProfessRecord = findOneById(id);

        if (tbProfessRecord == null) {
            return null;
        }

        ProfessRecordEntity entity = new ProfessRecordEntity();
        entity.setTo(userService.findOneById(tbProfessRecord.getToId()));
        entity.setFrom(userService.findOneById(tbProfessRecord.getFromId()));
        entity.setProfessRecord(tbProfessRecord);
        entity.setProfess(professService.findOneById(tbProfessRecord.getProfessId()));

        return entity;
    }

    @Override
    public ProfessRecordEntity findEntityByFromTo(Long from, Long to) {

        if (from == null || to == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TbUser fromUser = userService.findOneById(from);
        TbUser toUser = userService.findOneById(to);

        QueryWrapper<TbProfessRecord> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("from_id", from);
        queryWrapper.eq("to_id", to);

        List<TbProfessRecord> professRecords = professRecordDao.selectList(queryWrapper);

        ProfessRecordEntity entity = new ProfessRecordEntity();

        entity.setFrom(fromUser);
        entity.setTo(toUser);

        if (professRecords == null || professRecords.size() == 0) {
            return entity;
        }

        entity.setProfessRecord(professRecords.get(0));
        TbProfess profess = professService.findOneById(professRecords.get(0).getProfessId());
        entity.setProfess(profess);

        return entity;
    }

    @Override
    public List<TbProfessRecord> findListByFromId(Long from) {

        if (from == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        QueryWrapper<TbProfessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("from_id", from);

        return professRecordDao.selectList(wrapper);
    }

    @Override
    public List<ProfessRecordEntity> findEntityListByFromId(Long from) {

        List<ProfessRecordEntity> res = new ArrayList<>();

        List<TbProfessRecord> professRecordList = findListByFromId(from);

        for (TbProfessRecord tbProfessRecord : professRecordList) {
            ProfessRecordEntity entity = findEntityById(tbProfessRecord.getId());
            res.add(entity);
        }

        return res;
    }

    @Override
    public List<TbProfessRecord> findListByToId(Long to) {

        if (to == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        QueryWrapper<TbProfessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("to_id", to);

        return professRecordDao.selectList(wrapper);
    }

    @Override
    public List<ProfessRecordEntity> findEntityListByToId(Long to) {

        List<ProfessRecordEntity> res = new ArrayList<>();

        List<TbProfessRecord> toProfessRecords = findListByToId(to);

        for (TbProfessRecord tbProfessRecord : toProfessRecords) {
            ProfessRecordEntity entity = findEntityById(tbProfessRecord.getId());
            res.add(entity);
        }

        return res;
    }

    @Value("${EMAIL_SEND_PROFESS_NOTE_TITLE}")
    private String EMAIL_SEND_PROFESS_NOTE_TITLE;

    @Value("${EMAIL_SEND_NOT_SURE_PROFESS_NOTE_CONTENT}")
    private String EMAIL_SEND_NOT_SURE_PROFESS_NOTE_CONTENT;

    @Value("${EMAIL_SEND_SURE_PROFESS_NOTE_CONTENT}")
    private String EMAIL_SEND_SURE_PROFESS_NOTE_CONTENT;

    @Value("${EMAIL_SEND_PROFESS_NOTE_URL}")
    private String EMAIL_SEND_PROFESS_NOTE_URL;

    private String genSendProfessNoteContent(String name, String content) {
        content = content.replace("{{name}}", name);
        content = content.replace("{{url}}", EMAIL_SEND_PROFESS_NOTE_URL);
        return content.replace("{{time}}", DateUtil.getNowTime());
    }

    @Override
    public ProfessRecordEntity add(ProfessRecordVo professRecordVo) {

        if (professRecordVo == null || professRecordVo.getFromId() == null
                || StringUtils.isEmpty(professRecordVo.getToName())) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        // 检查数据合法性
        TbUser from = userService.findOneById(professRecordVo.getFromId());
        if (from == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        // 邮箱必填
        if (!RegexUtils.validateEmail(professRecordVo.getToEmail())) {
            ExceptionCast.cast("邮箱不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (!RegexUtils.validateMobilePhone(professRecordVo.getToPhone())) {
            ExceptionCast.cast("手机号不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (professRecordVo.getToPhone().equals(from.getPhone())) {
            ExceptionCast.cast("不能表白自己哟~~",ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        professRecordVo.setToEmail(professRecordVo.getToEmail().toLowerCase());

        //校验是否存在重复表白
        //所有表白记录
        List<TbProfessRecord> historyRecords = findListByFromId(professRecordVo.getFromId());
        for (TbProfessRecord record : historyRecords) {
            if (!record.getStatus().equals("4") && !record.getStatus().equals("5")) {
                ExceptionCast.cast("表白正在进行中，不可以重复发起表白~~", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
            }
        }

        // 校验被表白者是否已完成注册，未注册则将表白记录存入redis

        TbUser toUser = userService.findOneByPhone(professRecordVo.getToPhone());

        TbProfessRecord insertProfessRecord = new TbProfessRecord();

        List<ProfessRecordVo> toUserProfessRecords = (List<ProfessRecordVo>) redisTemplate.opsForValue().get("SEND_PROFESS_NOTE_TO_USER_PHONE_" + professRecordVo.getToPhone());

        if (toUserProfessRecords == null) {
            toUserProfessRecords = new ArrayList<ProfessRecordVo>();
        }

        if (toUser != null) {
            insertProfessRecord.setToId(toUser.getId());
        }

        // insert ProfessRecord
        insertProfessRecord.setFromId(professRecordVo.getFromId());
        insertProfessRecord.setCreated(new Date());
        insertProfessRecord.setId(null);
        insertProfessRecord.setStatus("1");
        insertProfessRecord.setProfessId(professService.add(new TbProfess()).getId());
        professRecordDao.insert(insertProfessRecord);

        if (toUser == null) {
            //将待匹配用户toUser放入Redis
            professRecordVo.setProfessRecordId(insertProfessRecord.getId());
            toUserProfessRecords.add(professRecordVo);
            redisTemplate.opsForValue().set("SEND_PROFESS_NOTE_TO_USER_PHONE_" + professRecordVo.getToPhone(), toUserProfessRecords);
        }

        //发送邮件通知
        EmailMessage message;
        if (toUser == null) {
            message = new EmailMessage(professRecordVo.getToEmail(), EMAIL_SEND_PROFESS_NOTE_TITLE, genSendProfessNoteContent(professRecordVo.getToName(), EMAIL_SEND_NOT_SURE_PROFESS_NOTE_CONTENT));
        } else {
            message = new EmailMessage(professRecordVo.getToEmail(), EMAIL_SEND_PROFESS_NOTE_TITLE, genSendProfessNoteContent(toUser.getNickName(), EMAIL_SEND_SURE_PROFESS_NOTE_CONTENT));
        }

        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTINGKEY, JsonUtils.toString(message));

        return findEntityById(insertProfessRecord.getId());
    }


    @Override
    public List<ProfessRecordEntity> matching(Long toId) {

        TbUser toUser = userService.findOneById(toId);

        if (toUser == null || !RegexUtils.validateMobilePhone(toUser.getPhone())) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        //开始匹配
        //toUser所有的表白
        List<ProfessRecordVo> list = (List<ProfessRecordVo>) redisTemplate.opsForValue().get("SEND_PROFESS_NOTE_TO_USER_PHONE_" + toUser.getPhone());

        if (list == null) {
            return null;
        }

        List<ProfessRecordEntity> recordEntityList = new ArrayList<>();

        for (ProfessRecordVo vo : list) {
            //修改表白记录
            ProfessRecordEntity entity = updateToIdStatus(vo.getProfessRecordId(), toUser.getId(), "1");//响应
            recordEntityList.add(entity);
        }

        //清空被表白redis记录
        redisTemplate.delete("SEND_PROFESS_NOTE_TO_USER_PHONE_" + toUser.getPhone());

        return recordEntityList;
    }


    @Override
    public ProfessRecordEntity updateToIdStatus(Long id, Long toId, String status) {

        TbProfessRecord one = findOneById(id);

        if (one == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        TbUser user = userService.findOneById(toId);

        if (user == null) {
            ExceptionCast.cast("toId参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        one.setToId(toId);
        one.setStatus(status);
        professRecordDao.updateById(one);
        return findEntityById(one.getId());
    }

    @Override
    public ProfessRecordEntity updateStatusById(Long id, String status) {

        TbProfessRecord professRecord = findOneById(id);

        if (professRecord == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        professRecord.setStatus(status);
        professRecordDao.updateById(professRecord);
        return findEntityById(professRecord.getId());
    }

    @Override
    public void deleteIds(Long[] ids) {

        if (ids == null) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        for (Long id : ids) {

            TbProfessRecord one = findOneById(id);
            if (one != null) {
                // 删除 表白记录
                // 并且 删除 表白
                professService.deleteById(one.getProfessId());
                professRecordDao.deleteById(id);
            }
        }
    }
}
