package cn.boom.service.user.controller;

import cn.boom.framework.common.response.R;
import cn.boom.framework.model.entity.TbUserRole;
import cn.boom.framework.model.entity.union.UserRoleEntity;
import cn.boom.service.user.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/user-role")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/findOneById/{id}")
    public R findOneById(@PathVariable("id") Long id) {
        return R.ok().put("data", userRoleService.findOneById(id));
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/search/{pageNum}/{pageSize}")
    public R search(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize,
                    @RequestBody TbUserRole tbUserRole) {
        return R.ok().put("data", userRoleService.search(pageNum, pageSize, tbUserRole));
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/add")
    public R add(@RequestBody TbUserRole tbUserRole) {
        return R.ok().put("data", userRoleService.add(tbUserRole));
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/addUserRole")
    public R addUserRole(@RequestBody UserRoleEntity userRoleEntity) {
        return R.ok().put("data", userRoleService.add(userRoleEntity));
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/updateStatus/{id}/{status}")
    public R updateStatus(@PathVariable("id") Long id,@PathVariable("status") String status) {
        return R.ok().put("data", userRoleService.updateStatus(id, status));
    }
}
