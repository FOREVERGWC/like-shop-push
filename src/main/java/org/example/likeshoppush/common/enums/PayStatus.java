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
 * 支付状态
 */
@Getter
@AllArgsConstructor
public enum PayStatus {
    /**
     * 待支付
     */
    NO_PAY(0, "待支付"),
    /**
     * 已支付
     */
    HAS_PAYED(1, "已支付");

    private static final Map<Integer, PayStatus> map = new HashMap<>();

    static {
        for (PayStatus item : PayStatus.values()) {
            map.put(item.getCode(), item);
        }
    }

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;

    @JsonCreator
    private static PayStatus jacksonInstance(final JsonNode jsonNode) {
        Integer code = Convert.convert(Integer.class, jsonNode.asText());
        return map.get(code);
    }

    /**
     * 根据键获取枚举
     *
     * @param code 键
     * @return 结果
     */
    public static PayStatus getByCode(Integer code) {
        return map.get(code);
    }
}
