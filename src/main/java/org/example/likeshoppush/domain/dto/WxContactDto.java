package org.example.likeshoppush.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 微信商品发货联系方式信息
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "微信商品发货联系方式信息实体", description = "微信商品发货联系方式信息")
public class WxContactDto implements Serializable {
    /**
     * 寄件人联系方式
     */
    @Schema(description = "寄件人联系方式")
    private String consignor_contact;
    /**
     * 收件人联系方式
     */
    @Schema(description = "收件人联系方式")
    private String receiver_contact;
}
