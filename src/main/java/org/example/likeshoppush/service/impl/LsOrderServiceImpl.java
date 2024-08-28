package org.example.likeshoppush.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.likeshoppush.domain.entity.LsOrder;
import org.example.likeshoppush.mapper.LsOrderMapper;
import org.example.likeshoppush.service.ILsOrderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单服务实现类
 * </p>
 */
@Service
public class LsOrderServiceImpl extends ServiceImpl<LsOrderMapper, LsOrder> implements ILsOrderService {

}
