package org.example.likeshoppush.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 微信商品发货订单信息
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "微信商品发货订单信息实体", description = "微信商品发货订单信息")
public class WxOrderKeyDto implements Serializable {
    /**
     * 订单单号类型
     */
    @Schema(description = "订单单号类型")
    @NotNull(message = "订单单号类型不能为空")
    private Integer order_number_type;
    /**
     * 订单单号类型
     */
    @Schema(description = "微信订单号")
    private String transaction_id;
    /**
     * 商户号
     */
    @Schema(description = "商户号")
    private String mchid;
    /**
     * 商户系统内部订单号
     */
    @Schema(description = "商户系统内部订单号")
    private String out_trade_no;
}
