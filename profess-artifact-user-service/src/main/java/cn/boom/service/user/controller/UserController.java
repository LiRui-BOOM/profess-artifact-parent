package cn.boom.service.user.controller;

import cn.boom.framework.common.response.R;
import cn.boom.framework.common.utils.BaiduCryptUtils;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.vo.BaiduCryptVo;
import cn.boom.framework.model.vo.UpdatePasswordByEmailVo;
import cn.boom.framework.model.vo.UpdatePasswordByOldPasswordVo;
import cn.boom.service.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"User接口"})
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/findOneById/{id}")
    public R findUserById(@PathVariable("id") Long id) {
        return R.ok().put("data",userService.findOneById(id));
    }

    @GetMapping("/findOneByUsername/{username}")
    public R findOneByUsername(@PathVariable("username") String username) {
        return R.ok().put("data",userService.findOneByUsername(username));
    }

    @PostMapping("/findByIds")
    public R findByIds(Long[] ids) {
        return R.ok().put("data",userService.findByIds(ids));
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/search/{pageNum}/{pageSize}")
    public R search(@PathVariable("pageNum") int pageNum,@PathVariable("pageSize")int pageSize,@RequestBody TbUser tbUser) {
        return R.ok().put("data",userService.search(pageNum, pageSize, tbUser));
    }

    @PostMapping("/add")
    public R add(@RequestBody TbUser tbUser) {
        TbUser user;
        synchronized (this) {
            user = userService.add(tbUser);
        }
        return R.ok().put("data",user);
    }

    @PostMapping("/update")
    public R update(@RequestBody TbUser tbUser) {
        return R.ok().put("data",userService.update(tbUser));
    }


    @ApiOperation("用户名是否已被占用")
    @GetMapping("/isUsernameUsed/{username}")
    public R isUsernameUsed(@PathVariable("username") String username) {
        return R.ok().put("data", userService.isUsernameUsed(username));
    }

    @Secured({"ROLE_ADMIN"})
    @ApiOperation("修改账号状态")
    @GetMapping("/updateStatusById/{id}/{status}")
    public R updateStatusByUserId(@PathVariable("id") Long id, @PathVariable("status") String status) {
        return R.ok().put("data",userService.updateStatusById(id,status));
    }

    @GetMapping("/sendMessageTest")
    public R sendMessageTest() {
        userService.sendMessageTest();
        return R.ok();
    }

    @PostMapping("/getPhoneNumber")
    public R getPhoneNumber(@RequestBody BaiduCryptVo baiduCryptVo,HttpServletRequest request){

        String auth = request.getHeader("Authorization");
        String token = auth.replace("Bearer ", "");
        return R.ok().put("data",userService.getPhoneNumber(baiduCryptVo,token));
    }

    @GetMapping("/sendBindingCheckCodeEmail/{id}/{email}")
    public R sendBindingCheckCodeEmail(@PathVariable("id") Long id, @PathVariable("email") String email) {
        userService.sendBindingCheckCodeEmail(id, email);
        return R.ok();
    }

    @PutMapping("/bindingEmailByToken/{id}/{token}")
    public R bindingEmailByToken(@PathVariable("id") Long id, @PathVariable("token") String token) {
        return R.ok().put("data", userService.bindingEmailByToken(id, token));
    }

    @GetMapping("/sendUpdatePasswordCheckCodeEmail/{email}")
    public R sendUpdatePasswordCheckCodeEmail(@PathVariable("email") String email) {
        return R.ok().put("data", userService.sendUpdatePasswordCheckCodeEmail(email));
    }

    @PutMapping("/updatePasswordByEmailCheckCode")
    public R updatePasswordByEmailCheckCode(@RequestBody UpdatePasswordByEmailVo vo) {
        userService.updatePasswordByEmailCheckCode(vo);
        return R.ok();
    }

    @PutMapping("/updatePasswordByOldPassword")
    public R updatePasswordByOldPassword(@RequestBody UpdatePasswordByOldPasswordVo vo) {
        userService.updatePasswordByOldPassword(vo);
        return R.ok();
    }
}
