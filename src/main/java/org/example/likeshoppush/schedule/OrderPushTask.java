package org.example.likeshoppush.schedule;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.likeshoppush.common.enums.OrderStatus;
import org.example.likeshoppush.domain.entity.LsOrder;
import org.example.likeshoppush.domain.entity.LsOrderPush;
import org.example.likeshoppush.service.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderPushTask {
    @Resource
    private ILsOrderService lsOrderService;
    @Resource
    private ILsOrderPushService lsOrderPushService;
    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    private final String key = "last_execution_time";

    @Scheduled(fixedRate = 60000)
    public void pushOrders() {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;

        Object redisValue = redisTemplate.opsForValue().get(key);
        long lastExecutionTimeSeconds;
        if (redisValue instanceof Integer) {
            lastExecutionTimeSeconds = ((Integer) redisValue).longValue();
        } else if (redisValue instanceof Long) {
            lastExecutionTimeSeconds = (Long) redisValue;
        } else {
            lastExecutionTimeSeconds = currentTimeSeconds - 1;
        }

        redisTemplate.opsForValue().set(key, currentTimeSeconds);
        log.info("定时任务【订单推送】开始执行");
        List<Integer> orderIdList = lsOrderService.lambdaQuery()
                .select(LsOrder::getId)
                .in(LsOrder::getOrderStatus, Arrays.asList(OrderStatus.ON_SENDING, OrderStatus.HAS_FINISHED))
                .ge(LsOrder::getUpdateTime, (int) lastExecutionTimeSeconds)
                .eq(LsOrder::getDel, false)
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
