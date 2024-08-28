package org.example.likeshoppush.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 微信令牌响应
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "微信令牌响应实体", description = "微信令牌响应")
public class WxTokenResponseDto implements Serializable {
    /**
     * 令牌
     */
    @Schema(description = "令牌")
    private String access_token;
    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private String expires_in;
}
