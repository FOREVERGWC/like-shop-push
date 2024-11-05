package org.example.likeshoppush.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.example.likeshoppush.common.enums.OrderStatus;
import org.example.likeshoppush.common.enums.OrderType;
import org.example.likeshoppush.common.enums.PayStatus;

import java.io.Serializable;

/**
 * <p>
 * 店铺
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "店铺实体", description = "店铺")
public class LsShop implements Serializable {
    /**
     * 店铺ID
     */
    @Schema(description = "店铺ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 店铺编号
     */
    @Schema(description = "店铺编号")
    private Integer shopSn;
    /**
     * 删除标识
     */
    @Schema(description = "删除标识")
    private Boolean del;
}
