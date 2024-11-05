package org.example.likeshoppush.service;

import org.example.likeshoppush.common.exception.AccessTokenInvalid;
import org.example.likeshoppush.domain.dto.WecomDepartmentResponseDto;
import org.example.likeshoppush.domain.entity.LsOrder;

import java.util.List;

public interface IWecomApiService {

    /**
     * 获取令牌
     *
     * @return 令牌
     */
    String getToken();

    /**
     * 发送订单发货信息到企微
     *
     * @param token 令牌
     * @return 结果
     */
    List<WecomDepartmentResponseDto.Department> departmentList(String token) throws AccessTokenInvalid;

    /**
     * 发送订单发货信息到企微
     *
     * @param token       令牌
     * @param orders      订单
     * @param departments 部门
     * @return 结果
     */
    void sendMsg(String token, List<LsOrder> orders, List<WecomDepartmentResponseDto.Department> departments);
}
