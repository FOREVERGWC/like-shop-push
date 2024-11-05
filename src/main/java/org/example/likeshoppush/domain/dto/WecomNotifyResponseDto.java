package org.example.likeshoppush.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "企业微信推送消息响应实体", description = "企业微信推送消息响应")
public class WecomNotifyResponseDto {

    private Integer errcode;
    private String errmsg;
}
