//package org.example.likeshoppush;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.date.DateUtil;
//import jakarta.annotation.Resource;
//import org.example.likeshoppush.common.constant.Common;
//import org.example.likeshoppush.common.enums.Client;
//import org.example.likeshoppush.common.enums.OrderStatus;
//import org.example.likeshoppush.domain.dto.WxDeliverGoodsDto;
//import org.example.likeshoppush.domain.dto.WxOrderKeyDto;
//import org.example.likeshoppush.domain.dto.WxPayerDto;
//import org.example.likeshoppush.domain.dto.WxShippingDto;
//import org.example.likeshoppush.domain.entity.LsOrder;
//import org.example.likeshoppush.domain.entity.LsOrderGoods;
//import org.example.likeshoppush.domain.entity.LsOrderPush;
//import org.example.likeshoppush.domain.entity.LsUserAuth;
//import org.example.likeshoppush.service.*;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@SpringBootTest
//class LikeShopPushApplicationTests {
//    @Resource
//    private ILsOrderService lsOrderService;
//    @Resource
//    private ILsOrderGoodsService lsOrderGoodsService;
//    @Resource
//    private ILsUserAuthService lsUserAuthService;
//    @Resource
//    private ILsOrderPushService lsOrderPushService;
//    @Resource
//    private IWXApiService wxApiService;
//
//    @Test
//    void contextLoads() {
//        List<Integer> orderIdList = lsOrderService.lambdaQuery()
//                .eq(LsOrder::getOrderStatus, OrderStatus.ON_SENDING)
//                .select(LsOrder::getId)
//                .list()
//                .stream()
//                .map(LsOrder::getId)
//                .toList();
//        if (CollectionUtil.isEmpty(orderIdList)) {
//            return;
//        }
//        List<Integer> pushedOrderIds = lsOrderPushService.lambdaQuery()
//                .eq(LsOrderPush::getIsSuccess, true)
//                .in(LsOrderPush::getOrderId, orderIdList)
//                .select(LsOrderPush::getOrderId)
//                .list()
//                .stream()
//                .map(LsOrderPush::getOrderId)
//                .toList();
//        List<Integer> idsToPush = orderIdList.stream()
//                .filter(orderId -> !pushedOrderIds.contains(orderId))
//                .collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(idsToPush)) {
//            return;
//        }
//        // 订单
//        List<LsOrder> list = lsOrderService.lambdaQuery()
//                .in(LsOrder::getId, idsToPush)
//                .list();
//        // 订单商品
//        List<Integer> idList = list.stream().map(LsOrder::getId).toList();
//        List<LsOrderGoods> orderGoodsList = lsOrderGoodsService.lambdaQuery()
//                .in(LsOrderGoods::getOrderId, idList)
//                .list();
//        Map<Integer, List<LsOrderGoods>> orderGoodsMap = orderGoodsList.stream().collect(Collectors.groupingBy(LsOrderGoods::getOrderId));
//        // 下单用户
//        List<Integer> userIdList = list.stream().map(LsOrder::getUserId).distinct().toList();
//        List<LsUserAuth> userAuthList = lsUserAuthService.lambdaQuery()
//                .in(LsUserAuth::getUserId, userIdList)
//                .eq(LsUserAuth::getClient, Client.WECHAT)
//                .list();
//        Map<Integer, String> userAuthMap = userAuthList.stream().collect(Collectors.toMap(LsUserAuth::getUserId, LsUserAuth::getOpenid));
//        // 调用微信发货接口
//        list.forEach(item -> {
//            List<LsOrderGoods> itemOrderGoodsList = orderGoodsMap.getOrDefault(item.getId(), List.of());
//            String itemDesc = itemOrderGoodsList.stream().map(i -> i.getGoodsName() + "*" + i.getGoodsNum()).collect(Collectors.joining(","));
//            String uploadTime = DateUtil.formatDateTime(DateUtil.date()).replace(" ", "T") + "+08:00";
//            String openid = userAuthMap.getOrDefault(item.getUserId(), "");
//            // 组装
//            WxDeliverGoodsDto dto = WxDeliverGoodsDto.builder()
//                    .order_key(WxOrderKeyDto.builder()
//                            .order_number_type(Common.ORDER_NUMBER_TYPE_WECHAT)
//                            .transaction_id(item.getTransactionId())
//                            // TODO 获取商家号
//                            .mchid("")
//                            .out_trade_no(item.getOrderSn())
//                            .build())
//                    .logistics_type(Common.LOGISTICS_TYPE_TCPS)
//                    .delivery_mode(Common.DELIVERY_MODE_UNIFIED_DELIVERY)
//                    .is_all_delivered(true)
//                    .shipping_list(List.of(WxShippingDto.builder()
//                            .item_desc(itemDesc)
//                            .build()))
//                    .upload_time(uploadTime)
//                    .payer(WxPayerDto.builder()
//                            .openid(openid)
//                            .build())
//                    .build();
//            // 调用微信推送接口
//            try {
//                ResponseEntity<String> response = wxApiService.uploadShippingInfo("", dto);
//                LocalDateTime now = LocalDateTime.now();
//                boolean isSuccess = response.getStatusCode().is2xxSuccessful();
//                String remark = response.getBody();
//                lsOrderPushService.save(LsOrderPush.builder()
//                        .orderId(item.getId())
//                        .isSuccess(isSuccess)
//                        .createTime(now)
//                        .updateTime(now)
//                        .remark(remark)
//                        .build());
//            } catch (Exception e) {
//                LocalDateTime now = LocalDateTime.now();
//                lsOrderPushService.save(LsOrderPush.builder()
//                        .orderId(item.getId())
//                        .isSuccess(false)
//                        .createTime(now)
//                        .updateTime(now)
//                        .remark(e.getMessage())
//                        .build());
//            }
//        });
//    }
//
//    // TODO 两个定时任务，一个五分钟查一次订单表执行
//    // TODO 第二个定时任务五分钟查一次推送表，获取失败订单重新推送
//}
