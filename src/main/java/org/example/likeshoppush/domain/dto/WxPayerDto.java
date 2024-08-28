package org.example.likeshoppush.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 微信商品发货支付者信息
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "微信商品发货支付者信息实体", description = "微信商品发货支付者信息")
public class WxPayerDto implements Serializable {
    /**
     * 用户标识
     */
    @Schema(description = "用户标识")
    @NotBlank(message = "用户标示不能为空")
    private String openid;
}
