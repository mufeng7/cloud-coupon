package com.sztu.coupon.schedule;

import com.sztu.coupon.dao.CouponTemplateDao;
import com.sztu.coupon.entity.CouponTemplate;
import com.sztu.coupon.vo.TemplateRule;;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 定时清理已过期的模板
 */
@Slf4j
@Component
public class ScheduledTask {

    private final CouponTemplateDao templateDao;

    public ScheduledTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * 下线过期优惠券
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate(){
        log.info("Start to Expire CouponTemplate");
        List<CouponTemplate> templates = templateDao.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templates)){
            log.info("Done To Expire CouponTemplate");
            return;
        }
        Date cur = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());
        templates.forEach(           //根据过期规则，判断模板是否过期
                t -> {
                    TemplateRule rule = t.getRule();
                    if (rule.getExpiration().getDeadline() < cur.getTime()){
                        t.setExpired(true);
                        expiredTemplates.add(t);
                    }
                }
        );
        if (CollectionUtils.isNotEmpty(expiredTemplates)){
            log.info("Expired CouponTemplate Num: {}",templateDao.saveAll(expiredTemplates));
        }
        log.info("Done To Expire CouponTemplate");
    }
}
