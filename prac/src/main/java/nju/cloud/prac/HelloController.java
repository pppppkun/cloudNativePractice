package nju.cloud.prac;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: pkun
 * @CreateTime: 2020-07-25 10:06
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    @RateLimit(limitNum = 100)
    public Object Hello(){
        Map<String, String> res = new HashMap<>();
        res.put("Time", new Date(System.currentTimeMillis()).toString());
        res.put("Sentence", "Hello");
        res.put("Check", "1234");
        return res;
    }

}
