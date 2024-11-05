package org.example.likeshoppush.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import org.example.likeshoppush.common.exception.AccessTokenInvalid;
import org.example.likeshoppush.domain.dto.*;
import org.example.likeshoppush.domain.entity.LsOrder;
import org.example.likeshoppush.domain.entity.LsOrderGoods;
import org.example.likeshoppush.domain.entity.LsShop;
import org.example.likeshoppush.service.ILsOrderGoodsService;
import org.example.likeshoppush.service.ILsShopService;
import org.example.likeshoppush.service.IWecomApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WeComApiServiceImpl implements IWecomApiService {
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ILsOrderGoodsService lsOrderGoodsService;
    @Resource
    private ILsShopService lsShopService;

    @Value("${wecom.corpid}")
    private String corpid;
    @Value("${wecom.corpsecret}")
    private String secret;
    @Value("${wecom.agentid}")
    private String agentid;

    @Override
    public String getToken() {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("corpid", corpid)
                .queryParam("corpsecret", secret);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<WecomTokenResponseDto> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, WecomTokenResponseDto.class);
            WecomTokenResponseDto dto = response.getBody();
            if (dto == null || dto.getErrcode() != 0) {
                return "";
            }
            return dto.getAccessToken();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public List<WecomDepartmentResponseDto.Department> departmentList(String token) throws AccessTokenInvalid {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("access_token", token);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<WecomDepartmentResponseDto> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, WecomDepartmentResponseDto.class);
            WecomDepartmentResponseDto dto = response.getBody();
            // accessToken 失效
            if (dto == null || dto.getErrcode() == 40014 || dto.getErrcode() == 42001) {
                throw new AccessTokenInvalid();
            }
            if (dto == null || dto.getErrcode() != 0) {
                return null;
            }
            return dto.getDepartment();
        } catch (AccessTokenInvalid e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    private String buildTemplate(String toparty, String dateTime, String orderType, String orderSn, String consignee, String phone, String itemDesc, String remark) {
        return String.format("{" +
                "    \"toparty\": \"%s\"," +
                "    \"msgtype\": \"textcard\"," +
                "    \"agentid\": %s," +
                "    \"textcard\": {" +
                "        \"title\": \"下单通知\"," +
                "        \"description\": \"<div class=\\\"gray\\\">%s</div><div class=\\\"normal\\\">订单类型：%s</div><div class=\\\"normal\\\">订单编号：%s</div><div class=\\\"normal\\\">收货人：%s</div><div class=\\\"normal\\\">手机号：%s</div><div class=\\\"highlight\\\">%s</div><div class=\\\"normal\\\">备注：%s</div>\"," +
                "        \"url\": \"https://puppy-snack.suxitech.cn/shop/index/index.html\"," +
                "        \"btntxt\": \"更多\"" +
                "    }," +
                "    \"enable_id_trans\": 0," +
                "    \"enable_duplicate_check\": 0," +
                "    \"duplicate_check_interval\": 1800" +
                "}", toparty, agentid, dateTime, orderType, orderSn, consignee, phone, itemDesc, remark);
    }

    @Override
    public void sendMsg(String token, List<LsOrder> orders, List<WecomDepartmentResponseDto.Department> departments) {
        // 订单商品
        List<LsOrderGoods> orderGoodsList = lsOrderGoodsService.lambdaQuery()
                .in(LsOrderGoods::getOrderId, orders.stream().map(LsOrder::getId).toList())
                .list();
        Map<Integer, List<LsOrderGoods>> orderGoodsMap = orderGoodsList.stream().collect(Collectors.groupingBy(LsOrderGoods::getOrderId));

        HashMap<String, String> departmentIds = new HashMap<>();
        departments.forEach(e -> departmentIds.put(e.getName(), e.getId().toString()));
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        orders.forEach(e -> {
            LsShop shop = lsShopService.getById(e.getShopId());
            if (!departmentIds.containsKey(shop.getShopSn().toString())) {
                return;
            }
            List<LsOrderGoods> itemOrderGoodsList = orderGoodsMap.getOrDefault(e.getId(), List.of());
            String itemDesc = itemOrderGoodsList.stream().map(i -> i.getGoodsName() + "*" + i.getGoodsNum()).collect(Collectors.joining("\n"));
            String requestBody = buildTemplate(departmentIds.get(shop.getShopSn().toString()),
                    DateUtil.formatDateTime(DateUtil.date(e.getUpdateTime().longValue() * 1000)),
                    e.getOrderType().getMsg(),
                    e.getOrderSn(),
                    e.getConsignee(),
                    e.getMobile(), itemDesc,
                    e.getUserRemark()
            );
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            try {
                restTemplate.exchange(url, HttpMethod.POST, request, WecomNotifyResponseDto.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
