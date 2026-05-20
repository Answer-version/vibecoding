package com.vibecoding.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vibecoding.user.entity.VerificationCode;
import com.vibecoding.user.mapper.VerificationCodeMapper;
import com.vibecoding.user.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeMapper verificationCodeMapper;

    // 验证码有效期：5分钟
    private static final int EXPIRE_MINUTES = 5;

    @Override
    public String sendCode(String phone, String type) {
        // 生成6位数字验证码
        String code = String.format("%06d", (int) (Math.random() * 1000000));

        // 先标记之前的验证码为已使用
        VerificationCode oldCode = verificationCodeMapper.selectOne(
            new LambdaQueryWrapper<VerificationCode>()
                .eq(VerificationCode::getPhone, phone)
                .eq(VerificationCode::getType, type)
                .eq(VerificationCode::getUsed, 0)
        );

        if (oldCode != null) {
            oldCode.setUsed(1);
            verificationCodeMapper.updateById(oldCode);
        }

        // 创建新验证码
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPhone(phone);
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
        verificationCode.setUsed(0);
        verificationCode.setCreateTime(LocalDateTime.now());
        verificationCode.setUpdateTime(LocalDateTime.now());

        verificationCodeMapper.insert(verificationCode);

        // TODO: 实际发送短信 (集成短信服务商)
        // 这里仅返回验证码用于测试
        System.out.println("【VibeCommerce】验证码：" + code + "，有效期" + EXPIRE_MINUTES + "分钟");

        return code;
    }

    @Override
    public boolean verifyCode(String phone, String code, String type) {
        VerificationCode verificationCode = verificationCodeMapper.selectOne(
            new LambdaQueryWrapper<VerificationCode>()
                .eq(VerificationCode::getPhone, phone)
                .eq(VerificationCode::getCode, code)
                .eq(VerificationCode::getType, type)
                .eq(VerificationCode::getUsed, 0)
                .gt(VerificationCode::getExpireTime, LocalDateTime.now())
        );

        if (verificationCode != null) {
            // 标记为已使用
            verificationCode.setUsed(1);
            verificationCodeMapper.updateById(verificationCode);
            return true;
        }

        return false;
    }
}
