package com.sztu.coupon.service.impl;

import com.sztu.coupon.dao.CouponTemplateDao;
import com.sztu.coupon.entity.CouponTemplate;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.service.IAsyncService;
import com.sztu.coupon.service.IBuildTemplateService;
import com.sztu.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    private final IAsyncService asyncService;

    private final CouponTemplateDao templateDao;

    public BuildTemplateServiceImpl(IAsyncService asyncService, CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {
        if (!request.validate()){
            throw new CouponException("BuildTemplate Param Is not Valid");
        }
        if (null != templateDao.findByName(request.getName())){
            throw new CouponException("Exist Sname Name Template");
        }
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);
        asyncService.asyncConstructCouponByATemplate(template);
        return template;
    }


    private CouponTemplate requestToTemplate(TemplateRequest request){
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
