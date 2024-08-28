package org.example.likeshoppush.schedule;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.likeshoppush.common.enums.OrderStatus;
import org.example.likeshoppush.domain.entity.LsOrder;
import org.example.likeshoppush.domain.entity.LsOrderPush;
import org.example.likeshoppush.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderPushTask {
    @Resource
    private ILsOrderService lsOrderService;
    @Resource
    private ILsOrderPushService lsOrderPushService;

    @Scheduled(fixedRate = 300000)
    public void pushOrders() {
        log.info("定时任务【订单推送】开始执行");
        List<Integer> orderIdList = lsOrderService.lambdaQuery()
                .eq(LsOrder::getOrderStatus, OrderStatus.ON_SENDING)
                .select(LsOrder::getId)
                .list()
                .stream()
                .map(LsOrder::getId)
                .toList();
        if (CollectionUtil.isEmpty(orderIdList)) {
            return;
        }
        List<Integer> pushedOrderIds = lsOrderPushService.lambdaQuery()
                .eq(LsOrderPush::getIsSuccess, true)
                .in(LsOrderPush::getOrderId, orderIdList)
                .select(LsOrderPush::getOrderId)
                .list()
                .stream()
                .map(LsOrderPush::getOrderId)
                .toList();
        List<Integer> idsToPush = orderIdList.stream()
                .filter(orderId -> !pushedOrderIds.contains(orderId))
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idsToPush)) {
            return;
        }
        lsOrderPushService.pushOrderList(idsToPush);
        log.info("定时任务【订单推送】执行完毕");
    }
}
