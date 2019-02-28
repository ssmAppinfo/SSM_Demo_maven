package cn.smbms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ClassName: TestController <br/>
 * Description: <br/>
 * date: 2019/2/28 15:58<br/>
 *
 * @author LucyBoy<br />
 * @since JDK 1.8
 */
@Controller
public class TestController {
    //添加测试controller
    @RequestMapping("/test")
    public String test(){
        return "test";
    }
}
