package cn.boom.service.user.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
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

        List<TbProfessRecord> res = new ArrayList<>();

        List<TbProfessRecord> toProfessRecords = professRecordDao.selectList(wrapper);

        for (TbProfessRecord professRecord : toProfessRecords) {

            TbProfess profess = professService.findOneById(professRecord.getProfessId());

            if (profess.getIsSend().equals("1")) { //已发送的profess
                res.add(professRecord);
            }
        }

        return res;
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

    @Value("${EMAIL_SEND_PROFESS_NOTE_CONTENT}")
    private String EMAIL_SEND_PROFESS_NOTE_CONTENT;

    @Value("${EMAIL_SEND_PROFESS_NOTE_URL}")
    private String EMAIL_SEND_PROFESS_NOTE_URL;

    private String genSendProfessNoteContent(String name) {
        String content = EMAIL_SEND_PROFESS_NOTE_CONTENT.replace("{{name}}", name);
        content =  content.replace("{{url}}", EMAIL_SEND_PROFESS_NOTE_URL);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = sdf.format(new Date());
        return content.replace("{{time}}", s);
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

        //将待匹配用户toUser放入Redis
        redisTemplate.opsForValue().set("SEND_NOTE_TO_USER_FROM_" + from.getId(), professRecordVo);

        // insert ProfessRecord
        TbProfessRecord professRecord = new TbProfessRecord();
        professRecord.setFromId(professRecordVo.getFromId());
        professRecord.setCreated(new Date());
        professRecord.setId(null);
        professRecord.setStatus("1");
        professRecord.setProfessId(professService.add(new TbProfess()).getId());
        professRecordDao.insert(professRecord);

        //发送邮件通知
        EmailMessage message = new EmailMessage(professRecordVo.getToEmail(),EMAIL_SEND_PROFESS_NOTE_TITLE,genSendProfessNoteContent(professRecordVo.getToName()));
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE,EMAIL_ROUTINGKEY,JsonUtils.toString(message));

        return findEntityById(professRecord.getId());
    }


    @Override
    public ProfessRecordEntity updateToId(Long id,Long toId) {

        TbProfessRecord one = findOneById(id);

        if (one == null) {
            ExceptionCast.cast(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        TbUser user = userService.findOneById(toId);

        if (user == null) {
            ExceptionCast.cast("toId参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        one.setToId(toId);

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
