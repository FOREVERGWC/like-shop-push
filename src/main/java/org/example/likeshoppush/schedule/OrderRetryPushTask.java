package org.example.likeshoppush.schedule;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.likeshoppush.domain.entity.LsOrderPush;
import org.example.likeshoppush.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderRetryPushTask {
    @Resource
    private ILsOrderPushService lsOrderPushService;

    @Scheduled(fixedRate = 10000)
    public void retryFailedPushes() {
        log.info("定时任务【推送重试】开始执行");
        List<Integer> failedOrderIds = lsOrderPushService.lambdaQuery()
                .eq(LsOrderPush::getIsSuccess, false)
                .select(LsOrderPush::getOrderId)
                .list()
                .stream()
                .map(LsOrderPush::getOrderId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(failedOrderIds)) {
            return;
        }
        lsOrderPushService.pushOrderList(failedOrderIds);
        log.info("定时任务【推送重试】执行完毕");
    }
}
