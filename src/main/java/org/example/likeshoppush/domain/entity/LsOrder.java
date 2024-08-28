package org.example.likeshoppush.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.example.likeshoppush.common.enums.OrderStatus;
import org.example.likeshoppush.common.enums.PayStatus;

import java.io.Serializable;

/**
 * <p>
 * 订单
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "订单实体", description = "订单")
public class LsOrder implements Serializable {
    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 店铺ID
     */
    @Schema(description = "店铺ID")
    private Integer shopId;
    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderSn;
    /**
     * 第三方平台交易流水号
     */
    @Schema(description = "第三方平台交易流水号")
    private String transactionId;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;
    /**
     * 支付状态
     */
    @Schema(description = "支付状态")
    private PayStatus payStatus;
    /**
     * 订单状态
     */
    @Schema(description = "订单状态")
    private OrderStatus orderStatus;
    /**
     * 收货人
     */
    @Schema(description = "收货人")
    private String consignee;
    /**
     * 手机
     */
    @Schema(description = "手机")
    private String mobile;
    /**
     * 地址快照
     */
    @Schema(description = "地址快照")
    private String addressSnap;
}
