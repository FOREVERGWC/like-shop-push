package org.example.likeshoppush.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.example.likeshoppush.common.enums.PayStatus;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 充值订单
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "充值实体", description = "充值")
public class LsRechargeOrder implements Serializable {
    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
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
     * 订单来源
     */
    @Schema(description = "订单来源")
    private Integer orderSource;
    /**
     * 支付方式
     */
    @Schema(description = "支付方式")
    private Integer payWay;
    /**
     * 支付状态
     */
    @Schema(description = "支付状态")
    private PayStatus payStatus;
    /**
     * 支付时间
     */
    @Schema(description = "更新时间")
    private Long payTime;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;
    /**
     * 规则ID
     */
    @Schema(description = "规则ID")
    private Integer ruleId;
    /**
     * 充值金额
     */
    @Schema(description = "充值金额")
    private BigDecimal orderAmount;
    /**
     * 赠送金额
     */
    @Schema(description = "赠送金额")
    private BigDecimal giveMoney;
    /**
     * 更新时间
     */
    @Schema(description = "创建时间")
    private Long createTime;
}
