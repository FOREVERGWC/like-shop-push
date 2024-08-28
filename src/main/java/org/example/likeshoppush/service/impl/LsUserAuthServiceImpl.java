package org.example.likeshoppush.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.likeshoppush.domain.entity.LsUserAuth;
import org.example.likeshoppush.mapper.LsUserAuthMapper;
import org.example.likeshoppush.service.ILsUserAuthService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户权限服务实现类
 * </p>
 */
@Service
public class LsUserAuthServiceImpl extends ServiceImpl<LsUserAuthMapper, LsUserAuth> implements ILsUserAuthService {

}
