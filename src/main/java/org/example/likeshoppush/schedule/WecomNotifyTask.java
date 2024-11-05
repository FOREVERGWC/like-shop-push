package org.example.likeshoppush.schedule;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.likeshoppush.common.enums.OrderStatus;
import org.example.likeshoppush.common.exception.AccessTokenInvalid;
import org.example.likeshoppush.domain.dto.WecomDepartmentResponseDto;
import org.example.likeshoppush.domain.entity.LsOrder;
import org.example.likeshoppush.service.ILsOrderService;
import org.example.likeshoppush.service.IWecomApiService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WecomNotifyTask {
    @Resource
    private ILsOrderService lsOrderService;
    @Resource
    private IWecomApiService wecomApiService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LAST_EXEC = "notify_last_execution_time";

    private static final String TOKEN = "wecom_token";

    @Scheduled(fixedRate = 60000)
    public void execute() {
        Long currentTimeSeconds = System.currentTimeMillis() / 1000;
        Object redisValue = redisTemplate.opsForValue().get(LAST_EXEC);
        Long lastExecutionTimeSeconds = null;
        if (redisValue instanceof Integer) {
            lastExecutionTimeSeconds = ((Integer) redisValue).longValue();
        } else if (redisValue instanceof Long) {
            lastExecutionTimeSeconds = (Long) redisValue;
        }
        if (lastExecutionTimeSeconds == null) {
            redisTemplate.opsForValue().set(LAST_EXEC, currentTimeSeconds);
            return;
        }
        String token = (String) redisTemplate.opsForValue().get(TOKEN);

        log.info("定时任务【企微通知】开始执行");
        if (StrUtil.isBlank(token)) {
            token = wecomApiService.getToken();
            redisTemplate.opsForValue().set(TOKEN, token, 6500, TimeUnit.SECONDS);
        }
        if (StrUtil.isBlank(token)) {
            log.error("定时任务【企微通知】token 获取失败");
            return;
        }
        List<WecomDepartmentResponseDto.Department> departments;
        try {
            departments = wecomApiService.departmentList(token);
        } catch (AccessTokenInvalid e) {
            token = wecomApiService.getToken();
            redisTemplate.opsForValue().set(TOKEN, token, 6500, TimeUnit.SECONDS);
            if (StrUtil.isBlank(token)) {
                log.error("定时任务【企微通知】token 获取失败");
                return;
            }
            try {
                departments = wecomApiService.departmentList(token);
            } catch (Exception ex) {
                log.error("定时任务【企微通知】department 获取失败");
                return;
            }
        }
        if (CollectionUtil.isEmpty(departments)) {
            redisTemplate.opsForValue().set(LAST_EXEC, currentTimeSeconds);
            log.info("定时任务【企微通知】departments 为空");
            return;
        }

        List<LsOrder> orders = lsOrderService.lambdaQuery()
                .eq(LsOrder::getOrderStatus, OrderStatus.ON_MAKING)
                .ge(LsOrder::getUpdateTime, lastExecutionTimeSeconds)
                .lt(LsOrder::getUpdateTime, currentTimeSeconds)
                .eq(LsOrder::getDel, false)
                .list();
        if (CollectionUtil.isNotEmpty(orders)) {
            try {
                wecomApiService.sendMsg(token, orders, departments);
                redisTemplate.opsForValue().set(LAST_EXEC, currentTimeSeconds);
            } catch (Exception ex) {
                log.info("定时任务【企微通知】发送企业通知失败");
                return;
            }
        }
        log.info("定时任务【企微通知】执行成功");
    }
}
