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
 * 订单状态
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {
    /**
     * 待付款
     */
    NO_PAY(0, "待付款"),
    /**
     * 制作中
     */
    ON_MAKING(1, "制作中"),
    /**
     * 待取餐/配送中
     */
    ON_SENDING(2, "待取餐/配送中"),
    /**
     * 已完成
     */
    HAS_FINISHED(3, "已完成"),
    /**
     * 已关闭
     */
    HAS_CLOSED(4, "已关闭");

    private static final Map<Integer, OrderStatus> map = new HashMap<>();

    static {
        for (OrderStatus item : OrderStatus.values()) {
            map.put(item.getCode(), item);
        }
    }

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;

    @JsonCreator
    private static OrderStatus jacksonInstance(final JsonNode jsonNode) {
        Integer code = Convert.convert(Integer.class, jsonNode.asText());
        return map.get(code);
    }

    /**
     * 根据键获取枚举
     *
     * @param code 键
     * @return 结果
     */
    public static OrderStatus getByCode(Integer code) {
        return map.get(code);
    }
}
