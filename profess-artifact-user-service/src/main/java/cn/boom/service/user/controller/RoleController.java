package cn.boom.service.user.controller;

import cn.boom.framework.common.response.R;
import cn.boom.framework.model.entity.TbRole;
import cn.boom.service.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/findOneById/{id}")
    public R findOneById(@PathVariable("id") Long id) {
        return R.ok().put("data", roleService.findOneById(id));
    }

    @GetMapping("/findAll")
    public R findAll() {
        return R.ok().put("data", roleService.findAll());
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/search/{pageNum}/{pageSize}")
    public R search(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize,
                    @RequestBody TbRole tbRole) {
        return R.ok().put("data", roleService.search(pageNum, pageSize, tbRole));
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/update")
    public R update(@RequestBody TbRole role) {
        return R.ok().put("data", roleService.update(role));
    }
}
