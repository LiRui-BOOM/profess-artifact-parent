package cn.boom.service.user.controller;

import cn.boom.framework.common.response.R;
import cn.boom.framework.model.entity.TbProfessRecord;
import cn.boom.framework.model.vo.ProfessRecordVo;
import cn.boom.service.user.service.ProfessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profess-record")
public class ProfessRecordController {

    @Autowired
    private ProfessRecordService professRecordService;

    @GetMapping("findEntityById/{id}")
    public R  findEntityById(@PathVariable("id") Long id) {
        return R.ok().put("data",professRecordService.findEntityById(id));
    }

    @GetMapping("findEntityListByFromId/{from}")
    public R  findEntityListByFromId(@PathVariable("from") Long from) {
        return R.ok().put("data",professRecordService.findEntityListByFromId(from));
    }

    @GetMapping("findEntityListByToId/{to}")
    public R  findEntityListByToId(@PathVariable("to") Long to) {
        return R.ok().put("data",professRecordService.findEntityListByToId(to));
    }

    @PostMapping("add")
    public R  add(@RequestBody ProfessRecordVo professRecordVo) {
        return R.ok().put("data",professRecordService.add(professRecordVo));
    }

    @PutMapping("updateStatusById/{id}/{status}")
    public R  updateStatusById(@PathVariable("id") Long id, @PathVariable("status") String status) {
        return R.ok().put("data",professRecordService.updateStatusById(id,status));
    }

    @DeleteMapping("deleteIds")
    public R deleteIds(Long[] ids) {
        professRecordService.deleteIds(ids);
        return R.ok();
    }
}
