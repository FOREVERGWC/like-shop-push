package org.example.likeshoppush.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 订单商品
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "订单商品实体", description = "订单商品")
public class LsOrderGoods implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 店铺ID
     */
    @Schema(description = "店铺ID")
    private Integer shopId;
    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Integer orderId;
    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Integer goodsId;
    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String goodsName;
    /**
     * 商品数量
     */
    @Schema(description = "商品数量")
    private Integer goodsNum;
}
