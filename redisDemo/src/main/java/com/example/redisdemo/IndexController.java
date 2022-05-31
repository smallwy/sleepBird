package com.example.redisdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/test_sentinel")
    public void testSentinel() {
        int i = 1;
        while (true) {
            stringRedisTemplate.opsForValue().set("stone" + i, i + "");
            System.out.println("设置key:" + "stone" + i);
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("错误", e);
            }
        }
    }

    @RequestMapping("/test_cluster")
    public void testCluster() {
        stringRedisTemplate.opsForValue().set("hello", "world");
        System.out.println(stringRedisTemplate.opsForValue().get("hello"));
    }
}
