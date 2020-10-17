package cn.boom.service.user.controller;

import cn.boom.framework.common.response.R;
import cn.boom.framework.model.entity.TbUserProfess;
import cn.boom.service.user.service.UserProfessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profess")
public class UserProfessController {

    @Autowired
    private UserProfessService userProfessService;

    @GetMapping("findOneById/{id}")
    public R  findOneById(@PathVariable("id") Long id) {
        return R.ok().put("data",userProfessService.findOneById(id));
    }

    @PostMapping("add")
    public R  add(@RequestBody TbUserProfess tbUserProfess) {
        return R.ok().put("data",userProfessService.add(tbUserProfess));
    }
    @PostMapping("update")
    public R  update(@RequestBody TbUserProfess tbUserProfess) {
        return R.ok().put("data",userProfessService.update(tbUserProfess));
    }

    @GetMapping("updateSendStatusById/{id}/{status}")
    public R  updateSendStatusById(@PathVariable("id") Long id, @PathVariable("status") String status) {
        return R.ok().put("data",userProfessService.updateSendStatusById(id,status));
    }
}
