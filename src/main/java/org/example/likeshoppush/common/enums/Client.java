package org.example.likeshoppush.common.enums;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端类型
 */
@Getter
@AllArgsConstructor
public enum Client {
    /**
     * 微信小程序
     */
    WECHAT(1, "微信小程序"),
    /**
     * H5
     */
    H5(2, "H5"),
    /**
     * ios
     */
    IOS(3, "ios"),
    /**
     * Android
     */
    ANDROID(4, "Android");

    private static final Map<Integer, Client> map = new HashMap<>();

    static {
        for (Client item : Client.values()) {
            map.put(item.getCode(), item);
        }
    }

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;

    @JsonCreator
    private static Client jacksonInstance(final JsonNode jsonNode) {
        Integer code = Convert.convert(Integer.class, jsonNode.asText());
        return map.get(code);
    }

    /**
     * 根据键获取枚举
     *
     * @param code 键
     * @return 结果
     */
    public static Client getByCode(Integer code) {
        return map.get(code);
    }
}
