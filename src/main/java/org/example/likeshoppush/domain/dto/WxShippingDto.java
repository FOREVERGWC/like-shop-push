package org.example.likeshoppush.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 微信商品发货物流信息
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "微信商品发货物流信息实体", description = "微信商品发货物流信息")
public class WxShippingDto implements Serializable {
    /**
     * 物流单号
     */
    @Schema(description = "物流单号")
    private String tracking_no;
    /**
     * 物流公司编码
     */
    @Schema(description = "物流公司编码")
    private String express_company;
    /**
     * 商品信息
     */
    @Schema(description = "商品信息")
    @NotBlank(message = "商品信息不能为空")
    private String item_desc;
    /**
     * 联系方式
     */
    @Schema(description = "联系方式")
    private WxContactDto contact;
}
