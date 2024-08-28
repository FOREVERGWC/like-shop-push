package org.example.likeshoppush.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime date = LocalDateTime.now();
        metaObject.setValue("createTime", date);
        metaObject.setValue("updateTime", date);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime date = LocalDateTime.now();
        metaObject.setValue("updateTime", date);
    }
}
