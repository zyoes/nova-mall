package ${groupId}.${module}.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import ${groupId}.common.core.UserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyBatisPlusHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createdBy", UserContext.get(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updatedBy", UserContext.get(), metaObject);
    }
}
