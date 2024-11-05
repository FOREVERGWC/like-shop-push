package org.example.likeshoppush.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.likeshoppush.domain.entity.LsOrderPush;

import java.util.List;

/**
 * <p>
 * 订单推送服务类
 * </p>
 */
public interface ILsOrderPushService extends IService<LsOrderPush> {
    /**
     * 推送订单到微信
     *
     * @param orderIds 订单ID列表
     */
    void pushOrderList(List<Integer> orderIds);
    /**
     * 推送充值订单到微信
     *
     * @param orderIds 订单ID列表
     */
    void pushRechargeOrderList(List<Integer> orderIds);
}
