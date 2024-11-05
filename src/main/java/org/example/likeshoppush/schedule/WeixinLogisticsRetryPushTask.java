package org.example.likeshoppush.schedule;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.likeshoppush.domain.entity.LsOrderPush;
import org.example.likeshoppush.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class WeixinLogisticsRetryPushTask {
    @Resource
    private ILsOrderPushService lsOrderPushService;

   @Scheduled(fixedRate = 10000)
    public void retryFailedPushes() {
        log.info("定时任务【推送重试】开始执行");
        List<LsOrderPush> lsOrderPushes = lsOrderPushService.lambdaQuery()
                .select(LsOrderPush::getOrderId)
                .eq(LsOrderPush::getIsSuccess, false)
                .lt(LsOrderPush::getCount, 3)
                .list();
        List<Integer> orders = lsOrderPushes.stream().filter(e -> e.getType() == 0).map(LsOrderPush::getOrderId).distinct().toList();
        List<Integer> rechargeOrders = lsOrderPushes.stream().filter(e -> e.getType() == 1).map(LsOrderPush::getOrderId).distinct().toList();
        if (CollectionUtil.isNotEmpty(orders)) {
            lsOrderPushService.pushOrderList(orders);
        }
        if (CollectionUtil.isNotEmpty(rechargeOrders)) {
            lsOrderPushService.pushRechargeOrderList(rechargeOrders);
        }
        log.info("定时任务【推送重试】执行完毕");
    }
}
