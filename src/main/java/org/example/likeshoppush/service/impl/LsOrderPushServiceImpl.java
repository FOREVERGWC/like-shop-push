package org.example.likeshoppush.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.likeshoppush.common.constant.Common;
import org.example.likeshoppush.common.enums.Client;
import org.example.likeshoppush.common.enums.OrderType;
import org.example.likeshoppush.domain.dto.*;
import org.example.likeshoppush.domain.entity.*;
import org.example.likeshoppush.mapper.LsOrderPushMapper;
import org.example.likeshoppush.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单推送服务实现类
 * </p>
 */
@Slf4j
@Service
public class LsOrderPushServiceImpl extends ServiceImpl<LsOrderPushMapper, LsOrderPush> implements ILsOrderPushService {
    @Resource
    private ILsOrderService lsOrderService;
    @Resource
    private ILsOrderGoodsService lsOrderGoodsService;
    @Resource
    private ILsRechargeService lsRechargeService;
    @Resource
    private ILsUserAuthService lsUserAuthService;
    @Resource
    private IWxApiService wxApiService;

    @Value("${wechat.mchid}")
    private String mchId;

    @Override
    public void pushOrderList(List<Integer> orderIds) {
        String token = wxApiService.getToken();
        // 订单
        List<LsOrder> list = lsOrderService.lambdaQuery()
                .in(LsOrder::getId, orderIds)
                .list();
        // 订单商品
        List<LsOrderGoods> orderGoodsList = lsOrderGoodsService.lambdaQuery()
                .in(LsOrderGoods::getOrderId, orderIds)
                .list();
        Map<Integer, List<LsOrderGoods>> orderGoodsMap = orderGoodsList.stream().collect(Collectors.groupingBy(LsOrderGoods::getOrderId));
        // 下单用户
        List<Integer> userIdList = list.stream().map(LsOrder::getUserId).distinct().toList();
        List<LsUserAuth> userAuthList = lsUserAuthService.lambdaQuery()
                .in(LsUserAuth::getUserId, userIdList)
                .eq(LsUserAuth::getClient, Client.WECHAT)
                .list();
        Map<Integer, String> userAuthMap = userAuthList.stream().collect(Collectors.toMap(LsUserAuth::getUserId, LsUserAuth::getOpenid));
        // 调用微信发货接口
        list.forEach(item -> {
            List<LsOrderGoods> itemOrderGoodsList = orderGoodsMap.getOrDefault(item.getId(), List.of());
            String itemDesc = itemOrderGoodsList.stream().map(i -> i.getGoodsName() + "*" + i.getGoodsNum()).collect(Collectors.joining(","));
            String uploadTime = DateUtil.formatDateTime(DateUtil.date()).replace(" ", "T") + "+08:00";
            String openid = userAuthMap.getOrDefault(item.getUserId(), "");
            // 组装
            WxDeliverGoodsDto dto = WxDeliverGoodsDto.builder()
                    .order_key(WxOrderKeyDto.builder()
                            .order_number_type(Common.ORDER_NUMBER_TYPE_WECHAT)
                            .transaction_id(item.getTransactionId())
                            .mchid(mchId)
                            .out_trade_no(item.getOrderSn() + "JSAPI1")
                            .build())
                    .logistics_type(item.getOrderType() == OrderType.IN_STORE_ORDER ? Common.LOGISTICS_TYPE_YHZT : Common.LOGISTICS_TYPE_TCPS)
                    .delivery_mode(Common.DELIVERY_MODE_UNIFIED_DELIVERY)
                    .is_all_delivered(true)
                    .shipping_list(List.of(WxShippingDto.builder()
                            .item_desc(itemDesc)
                            .build()))
                    .upload_time(uploadTime)
                    .payer(WxPayerDto.builder()
                            .openid(openid)
                            .build())
                    .build();
            // 调用微信推送接口
            LsOrderPush orderPush = Optional.ofNullable(lambdaQuery()
                            .eq(LsOrderPush::getOrderId, item.getId())
                            .eq(LsOrderPush::getType, 0)
                            .one())
                    .orElse(LsOrderPush.builder()
                            .orderId(item.getId())
                            .orderSn(item.getOrderSn())
                            .isSuccess(false)
                            .count(0)
                            .remark("")
                            .build());
            String remark = JSONUtil.toJsonStr(dto) + "===";
            try {
                ResponseEntity<WxUploadShippingResponseDto> response = wxApiService.uploadShippingInfo(token, dto);
                boolean isSuccess = response.getStatusCode().is2xxSuccessful() && response.getBody() != null && Objects.equals(response.getBody().getErrcode(), 0L);
                remark += JSONUtil.toJsonStr(response.getBody());

                orderPush.setIsSuccess(isSuccess)
                        .setCount((isSuccess || orderPush.getId() == null) ? 0 : orderPush.getCount() + 1)
                        .setRemark(remark);
            } catch (Exception e) {
                remark += JSONUtil.toJsonStr(e.getMessage());
                orderPush.setIsSuccess(false)
                        .setCount(orderPush.getId() == null ? 0 : orderPush.getCount() + 1)
                        .setRemark(remark);
            }
            saveOrUpdate(orderPush);
            log.info("订单{}推送{}：{}", item.getId(), orderPush.getIsSuccess() ? "成功" : "失败", orderPush);
        });
    }

    @Override
    public void pushRechargeOrderList(List<Integer> orderIds) {
        String token = wxApiService.getToken();
        List<LsRechargeOrder> list = lsRechargeService.lambdaQuery()
                .in(LsRechargeOrder::getId, orderIds)
                .list();
        // 下单用户
        List<Integer> userIdList = list.stream().map(LsRechargeOrder::getUserId).distinct().toList();
        List<LsUserAuth> userAuthList = lsUserAuthService.lambdaQuery()
                .in(LsUserAuth::getUserId, userIdList)
                .eq(LsUserAuth::getClient, Client.WECHAT)
                .list();
        Map<Integer, String> userAuthMap = userAuthList.stream().collect(Collectors.toMap(LsUserAuth::getUserId, LsUserAuth::getOpenid));
        String uploadTime = DateUtil.formatDateTime(DateUtil.date()).replace(" ", "T") + "+08:00";
        list.forEach(item -> {
            String openid = userAuthMap.getOrDefault(item.getUserId(), "");
            // 组装
            WxDeliverGoodsDto dto = WxDeliverGoodsDto.builder()
                    .order_key(WxOrderKeyDto.builder()
                            .order_number_type(Common.ORDER_NUMBER_TYPE_WECHAT)
                            .transaction_id(item.getTransactionId())
                            .mchid(mchId)
                            .out_trade_no(item.getOrderSn() + "JSAPI1")
                            .build())
                    .logistics_type(Common.LOGISTICS_TYPE_YHZT)
                    .delivery_mode(Common.DELIVERY_MODE_UNIFIED_DELIVERY)
                    .is_all_delivered(true)
                    .shipping_list(List.of(WxShippingDto.builder()
                            .item_desc("余额充值:" + item.getOrderAmount())
                            .build()))
                    .upload_time(uploadTime)
                    .payer(WxPayerDto.builder()
                            .openid(openid)
                            .build())
                    .build();
            // 调用微信推送接口
            LsOrderPush orderPush = Optional.ofNullable(lambdaQuery()
                            .eq(LsOrderPush::getOrderId, item.getId())
                            .eq(LsOrderPush::getType, 1)
                            .one())
                    .orElse(LsOrderPush.builder()
                            .orderId(item.getId())
                            .orderSn(item.getOrderSn())
                            .isSuccess(false)
                            .count(0)
                            .remark("")
                            .build());
            String remark = JSONUtil.toJsonStr(dto) + "===";
            try {
                ResponseEntity<WxUploadShippingResponseDto> response = wxApiService.uploadShippingInfo(token, dto);
                boolean isSuccess = response.getStatusCode().is2xxSuccessful() && response.getBody() != null && Objects.equals(response.getBody().getErrcode(), 0L);
                remark += JSONUtil.toJsonStr(response.getBody());
                orderPush.setIsSuccess(isSuccess)
                        .setCount((isSuccess || orderPush.getId() == null) ? 0 : orderPush.getCount() + 1)
                        .setRemark(remark);
            } catch (Exception e) {
                remark += JSONUtil.toJsonStr(e.getMessage());
                orderPush.setIsSuccess(false)
                        .setCount(orderPush.getId() == null ? 0 : orderPush.getCount() + 1)
                        .setRemark(remark);
            }
            saveOrUpdate(orderPush);
            log.info("充值订单{}推送{}：{}", item.getId(), orderPush.getIsSuccess() ? "成功" : "失败", orderPush);
        });
    }
}
