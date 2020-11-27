package com.leyou.sms.mq;

import com.leyou.common.utils.RegexUtils;
import com.leyou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static com.leyou.common.constants.MQConstants.*;


@Slf4j
@Component
public class MessageListener {

    private final SmsUtils smsUtils;

    public MessageListener(SmsUtils smsUtils) {
        this.smsUtils = smsUtils;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = QueueConstants.SMS_VERIFY_CODE_QUEUE, durable = "true"),
            exchange = @Exchange(name = ExchangeConstants.SMS_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = RoutingKeyConstants.VERIFY_CODE_KEY
    ))
    public void listenVerifyCodeMessage(Map<String,String> msg){
        // 获取参数
        if(CollectionUtils.isEmpty(msg)){
            // 如果消息为空，不处理
            return;
        }
        // 手机号
        String phone = msg.get("phone");
        if (!RegexUtils.isPhone(phone)) {
            // 手机号有误，不处理
            return;
        }
        // 验证码
        String code = msg.get("code");
        if (!RegexUtils.isCodeValid(code)) {
            // 验证码有误，不处理
            return;
        }
        // 发送短信
        try {
            smsUtils.sendVerifyCode(phone, code);
        } catch (Exception e) {
            // 短信发送失败，我不想重试，异常捕获
            log.error("【SMS服务】短信验证码发送失败", e);
        }
    }
}