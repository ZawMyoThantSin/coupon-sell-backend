package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.PayOwnerRequest;
import com.css.coupon_sale.dto.response.PayOwnerResponse;
import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.PaidHistoryEntity;
import com.css.coupon_sale.repository.BusinessRepository;
import com.css.coupon_sale.repository.PaidHistoryRepository;
import com.css.coupon_sale.repository.SaleCouponRepository;
import com.css.coupon_sale.service.PaidHistoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaidHistoryServiceImpl implements PaidHistoryService {

    @Autowired
    private PaidHistoryRepository paidHistoryRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private SaleCouponRepository saleCouponRepository;

    @Override
    public PayOwnerResponse payOwner(PayOwnerRequest requestDto) {
        // Retrieve the business entity
        BusinessEntity business = businessRepository.findById(requestDto.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        // Validate owner percentage
        if (requestDto.getDesiredPercentage() < 1 || requestDto.getDesiredPercentage() > 100) {
            throw new IllegalArgumentException("Owner percentage must be between 1 and 100");
        }

        // Check if this is the first payment
        boolean isFirstPayment = (business.getLastPaidAmount() == null || business.getLastPaidAmount() == 0);

        if (!isFirstPayment && !business.isIncomeIncreased()) {
            throw new IllegalStateException("Total income has not increased since the last payment");
        }


        Double increment = business.getTotalIncome() - business.getLastPaidAmount();
//        Double ownerShare = increment * requestDto.getDesiredPercentage() / 100;
//        Double adminShare = increment - ownerShare;
        Double adminShare = increment * requestDto.getDesiredPercentage() / 100;
        Double ownerShare = increment - adminShare;

        // Update the business entity
        business.setLastPaidAmount(business.getTotalIncome());
        business.setIncomeIncreased(false);
        // Update payment status to PAID
        if (business.isIncomeIncreased()) {
            business.setPaymentStatus("PAID");
        } else {
            business.setPaymentStatus("PENDING"); // or whatever status you want to set
        }
        businessRepository.save(business);

        // Save to payment history
        PaidHistoryEntity paymentHistory = new PaidHistoryEntity();
        paymentHistory.setBusiness(business);
        paymentHistory.setPaymentDate(LocalDateTime.now());
        paymentHistory.setPaidAmount(ownerShare);
        paymentHistory.setDesiredPercentage(requestDto.getDesiredPercentage());
        paidHistoryRepository.save(paymentHistory);

        // Return the response DTO
        return new PayOwnerResponse(
                business.getId(),
                ownerShare,
                adminShare,
                ownerShare,
                paymentHistory.getPaymentDate()
        );
    }

    @Override
    public List<PaidHistoryEntity> getPaidHistory(int businessId) {
        return paidHistoryRepository.findByBusinessIdOrderByPaymentDateDesc(businessId);
    }
}
