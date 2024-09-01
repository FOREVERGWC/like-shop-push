package org.example.likeshoppush.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单推送
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "订单推送实体", description = "订单推送")
public class LsOrderPush implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Integer orderId;
    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderSn;
    /**
     * 推送成功
     */
    @Schema(description = "推送成功")
    private Boolean isSuccess;
    /**
     * 重试次数
     */
    @Schema(description = "重试次数")
    private Integer count;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
