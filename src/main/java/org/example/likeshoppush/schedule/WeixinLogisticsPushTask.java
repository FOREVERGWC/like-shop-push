package org.example.likeshoppush.schedule;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.likeshoppush.common.enums.OrderStatus;
import org.example.likeshoppush.common.enums.PayStatus;
import org.example.likeshoppush.domain.entity.LsOrder;
import org.example.likeshoppush.domain.entity.LsOrderPush;
import org.example.likeshoppush.domain.entity.LsRechargeOrder;
import org.example.likeshoppush.service.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class WeixinLogisticsPushTask {
    @Resource
    private ILsOrderService lsOrderService;
    @Resource
    private ILsRechargeService lsRechargeService;
    @Resource
    private ILsOrderPushService lsOrderPushService;
    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    private final String key = "last_execution_time";

   @Scheduled(fixedRate = 60000)
    public void execute() {
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

        log.info("定时任务【订单推送】开始执行");
        List<Integer> orderIdList = lsOrderService.lambdaQuery()
                .select(LsOrder::getId)
                .in(LsOrder::getOrderStatus, Arrays.asList(OrderStatus.ON_SENDING, OrderStatus.HAS_FINISHED))
                .ge(LsOrder::getUpdateTime, (int) lastExecutionTimeSeconds)
                .ne(LsOrder::getPayWay, 2)
                .eq(LsOrder::getDel, false)
                .list()
                .stream()
                .map(LsOrder::getId)
                .toList();
        if (CollectionUtil.isNotEmpty(orderIdList)) {
            List<Integer> pushedOrderIds = lsOrderPushService.lambdaQuery()
                    .eq(LsOrderPush::getIsSuccess, true)
                    .eq(LsOrderPush::getType, 0)
                    .in(LsOrderPush::getOrderId, orderIdList)
                    .select(LsOrderPush::getOrderId)
                    .list()
                    .stream()
                    .map(LsOrderPush::getOrderId)
                    .toList();
            List<Integer> idsToPush = orderIdList.stream()
                    .filter(orderId -> !pushedOrderIds.contains(orderId))
                    .toList();
            if (CollectionUtil.isNotEmpty(idsToPush)) {
                lsOrderPushService.pushOrderList(idsToPush);
            }
        }

        List<Integer> rechargeIdList = lsRechargeService.lambdaQuery()
                .select(LsRechargeOrder::getId)
                .ge(LsRechargeOrder::getPayTime, (int) lastExecutionTimeSeconds)
                .eq(LsRechargeOrder::getPayStatus, PayStatus.HAS_PAYED)
                .list()
                .stream()
                .map(LsRechargeOrder::getId)
                .toList();
        if (CollectionUtil.isNotEmpty(rechargeIdList)) {
            List<Integer> pushedRechargeOrderIds = lsOrderPushService.lambdaQuery()
                    .eq(LsOrderPush::getIsSuccess, true)
                    .eq(LsOrderPush::getType, 1)
                    .in(LsOrderPush::getOrderId, rechargeIdList)
                    .select(LsOrderPush::getOrderId)
                    .list()
                    .stream()
                    .map(LsOrderPush::getOrderId)
                    .toList();
            List<Integer> rechargeIdsToPush = rechargeIdList.stream()
                    .filter(orderId -> !pushedRechargeOrderIds.contains(orderId))
                    .toList();
            if (CollectionUtil.isNotEmpty(rechargeIdsToPush)) {
                lsOrderPushService.pushRechargeOrderList(rechargeIdsToPush);
            }
        }
        redisTemplate.opsForValue().set(key, currentTimeSeconds);
        log.info("定时任务【订单推送】执行完毕");
    }
}
