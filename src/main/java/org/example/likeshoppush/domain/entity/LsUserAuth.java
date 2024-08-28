package org.example.likeshoppush.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.example.likeshoppush.common.enums.Client;

import java.io.Serializable;

/**
 * 用户权限
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户权限实体", description = "用户权限")
public class LsUserAuth implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;
    /**
     * 微信唯一标识
     */
    @Schema(description = "微信唯一标识")
    private String openid;
    /**
     * 微信unionid
     */
    @Schema(description = "微信unionid")
    private String unionid;
    /**
     * 客户端类型
     */
    private Client client;
}
