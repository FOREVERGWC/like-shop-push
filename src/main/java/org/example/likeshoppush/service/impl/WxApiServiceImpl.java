package org.example.likeshoppush.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.example.likeshoppush.domain.dto.WxDeliverGoodsDto;
import org.example.likeshoppush.domain.dto.WxTokenResponseDto;
import org.example.likeshoppush.domain.dto.WxUploadShippingResponseDto;
import org.example.likeshoppush.service.IWxApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WxApiServiceImpl implements IWxApiService {
    @Resource
    private RestTemplate restTemplate;

    @Value("${wechat.appid}")
    private String appId;
    @Value("${wechat.secret}")
    private String secret;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("grant_type", "client_credential")
                .queryParam("appid", appId)
                .queryParam("secret", secret);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<WxTokenResponseDto> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, WxTokenResponseDto.class);
            WxTokenResponseDto dto = response.getBody();
            if (dto == null || StrUtil.isBlank(dto.getAccess_token())) {
                return "";
            }
            return dto.getAccess_token();
        } catch (Exception e) {
            return "";
        }
    }

    @SneakyThrows
    @Override
    public ResponseEntity<WxUploadShippingResponseDto> uploadShippingInfo(String token, WxDeliverGoodsDto dto) {
        String url = "https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(dto);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, WxUploadShippingResponseDto.class);
    }
}
