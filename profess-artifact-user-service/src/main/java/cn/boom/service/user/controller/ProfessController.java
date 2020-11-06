package cn.boom.service.user.controller;

import cn.boom.framework.common.response.R;
import cn.boom.framework.model.entity.TbProfess;
import cn.boom.service.user.service.ProfessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profess")
public class ProfessController {

    @Autowired
    private ProfessService professService;

    @GetMapping("/findOneById/{id}")
    public  R  findOneById(@PathVariable("id") Long id){
        return R.ok().put("data", professService.findOneById(id));
    }

    @PostMapping("/add")
    public R add(@RequestBody TbProfess tbProfess){
        return R.ok().put("data", professService.add(tbProfess));
    }

    @PutMapping("/update")
    public R  update(@RequestBody TbProfess tbProfess){
        return R.ok().put("data", professService.update(tbProfess));
    }

    @PutMapping("/updateSend/{id}")
    public R  updateSend(@PathVariable("id") Long id){
        return R.ok().put("data", professService.updateSend(id));
    }
}
