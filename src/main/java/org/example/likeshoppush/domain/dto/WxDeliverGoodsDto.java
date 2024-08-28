package org.example.likeshoppush.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 微信商品发货信息
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "微信商品发货信息实体", description = "微信商品发货信息")
public class WxDeliverGoodsDto implements Serializable {
    /**
     * 订单
     */
    @Schema(description = "订单")
    @NotNull(message = "订单不能为空")
    private WxOrderKeyDto order_key;
    /**
     * 物流模式
     */
    @Schema(description = "物流模式")
    @NotNull(message = "物流模式不能为空")
    private Integer logistics_type;
    /**
     * 发货模式
     */
    @Schema(description = "发货模式")
    @NotNull(message = "发货模式不能为空")
    private Integer delivery_mode;
    /**
     * 是否均已发货
     */
    @Schema(description = "是否均已发货")
    private Boolean is_all_delivered;
    /**
     * 物流信息列表
     */
    @Schema(description = "物流信息列表")
    @NotEmpty(message = "物流信息列表不能为空")
    private List<WxShippingDto> shipping_list;
    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    @NotBlank(message = "上传时间不能为空")
    private String upload_time;
    /**
     * 支付者
     */
    @Schema(description = "支付者")
    @NotNull(message = "支付者不能为空")
    private WxPayerDto payer;
}
