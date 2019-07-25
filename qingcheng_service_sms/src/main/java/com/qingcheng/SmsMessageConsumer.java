package com.qingcheng;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonResponse;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/17 15:03
 * @Description:
 */
public class SmsMessageConsumer implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    @Value("${smsCode}")
    private String smsCode; //短信模板编号

    @Value("${param}")
    private String param;
    @Override
    public void onMessage(Message message) {
        String msgStr = new String(message.getBody());
        Map<String, String> map = (Map<String, String>) JSON.parse(msgStr);
        System.out.print("phone:" + map.get("phone"));
        System.out.println("code:" + map.get("code"));
        if (map.get("phone") == null) {
            return;
        }
        CommonResponse commonResponse = smsUtil.sendSms(map.get("phone"), smsCode, param.replace("[value]", map.get("code")));
    }

    public static void main(String[] args) {
        Integer i = new Integer(123456);

        String s = i+"";
    }
}
