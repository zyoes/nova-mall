package ${groupId}.${module};

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("${groupId}")
@MapperScan("${groupId}.${module}.mapper")
public class ${module?capitalize}Application {
    public static void main(String[] args) {
        SpringApplication.run(${module?capitalize}Application.class, args);
    }
}
