package org.example.likeshoppush.service;

import org.example.likeshoppush.domain.dto.WxDeliverGoodsDto;
import org.example.likeshoppush.domain.dto.WxUploadShippingResponseDto;
import org.springframework.http.ResponseEntity;

public interface IWxApiService {
    /**
     * 获取令牌
     *
     * @return 令牌
     */
    String getToken();

    /**
     * 上传订单到微信
     *
     * @param token 令牌
     * @param dto   订单发货信息
     * @return 结果
     */
    ResponseEntity<WxUploadShippingResponseDto> uploadShippingInfo(String token, WxDeliverGoodsDto dto);
}
