package ${groupId}.${module}.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String hello(){
        return "hello ${module} module";
    }

    @GetMapping("hi")
    public String hi(){
        return "hi ${module} module";
    }
}
