package cn.boom.service.auth.controller;

import cn.boom.framework.common.response.R;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class SpringSecurityController {

    @GetMapping("/success")
    public R success() {
        return R.ok();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/test_admin")
    public R test_admin() {
        System.out.println("test_admin");
        return R.ok().put("data", "test_admin");
    }

    @Secured("ROLE_HR")
    @GetMapping("/test_hr")
    public R test_hr() {
        System.out.println("test_hr");
        return R.ok().put("data", "test_hr");
    }

    @Secured("ROLE_STUDENT")
    @GetMapping("/test_stu")
    public R test_stu() {
        System.out.println("test_stu");
        return R.ok().put("data", "test_stu");
    }
}
