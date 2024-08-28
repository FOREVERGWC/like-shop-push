package org.example.likeshoppush.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 微信发货响应
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "微信发货响应实体", description = "微信发货响应")
public class WxUploadShippingResponseDto implements Serializable {
    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private Long errcode;
    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errmsg;
}
