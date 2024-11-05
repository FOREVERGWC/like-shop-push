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
 * 订单类型
 */
@Getter
@AllArgsConstructor
public enum OrderType {
    /**
     * 到店订单
     */
    IN_STORE_ORDER(1, "到店订单"),
    /**
     * 外卖订单
     */
    TAKEOUT_ORDER(2, "外卖订单");

    private static final Map<Integer, OrderType> map = new HashMap<>();

    static {
        for (OrderType item : OrderType.values()) {
            map.put(item.getCode(), item);
        }
    }

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;

    @JsonCreator
    private static OrderType jacksonInstance(final JsonNode jsonNode) {
        Integer code = Convert.convert(Integer.class, jsonNode.asText());
        return map.get(code);
    }

    /**
     * 根据键获取枚举
     *
     * @param code 键
     * @return 结果
     */
    public static OrderType getByCode(Integer code) {
        return map.get(code);
    }
}
