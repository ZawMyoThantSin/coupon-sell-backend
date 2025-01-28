package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.PayOwnerRequest;
import com.css.coupon_sale.dto.response.PayOwnerResponse;
import com.css.coupon_sale.entity.PaidHistoryEntity;
import net.sf.jasperreports.engine.JRException;

import java.util.List;

public interface PaidHistoryService {

    PayOwnerResponse payOwner(PayOwnerRequest requestDto);
    List<PaidHistoryEntity> getPaidHistory(int businessId);
    byte[] generatePaidHistoryReport(Integer businessId, String reportType) throws JRException;
}
