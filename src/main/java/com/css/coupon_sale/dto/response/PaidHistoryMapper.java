package com.css.coupon_sale.dto.response;

import com.css.coupon_sale.entity.PaidHistoryEntity;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PaidHistoryMapper {

    // DTO class
    public static class PaidHistoryDTO {
        private String businessName;
        private Double adminProfit;
        private Double percentage;
        private Double paidAmount;
        private Date paidAt;

        public PaidHistoryDTO(String businessName, Double adminProfit, Double percentage, Double paidAmount, Date paidAt) {
            this.businessName = businessName;
            this.adminProfit = adminProfit;
            this.percentage = percentage;
            this.paidAmount = paidAmount;
            this.paidAt = paidAt;
        }

        // Getters and Setters (Optional)
        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public Double getAdminProfit() {
            return adminProfit;
        }

        public void setAdminProfit(Double adminProfit) {
            this.adminProfit = adminProfit;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }

        public Double getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(Double paidAmount) {
            this.paidAmount = paidAmount;
        }

        public Date getPaidAt() {
            return paidAt;
        }

        public void setPaidAt(Date paidAt) {
            this.paidAt = paidAt;
        }
    }

    // Mapper method
    public List<PaidHistoryDTO> mapToDTO(List<PaidHistoryEntity> paidHistoryEntities) {
        return paidHistoryEntities.stream()
                .map(entity -> new PaidHistoryDTO(
                        entity.getBusiness().getName(), // Assuming the entity has a businessName field
                        entity.getPaidAmount(), // Assuming the entity has an adminProfit field
                        entity.getDesiredPercentage(),  // Assuming the entity has a percentage field
                        entity.getPaidAmount(),  // Assuming the entity has a paidAmount field
                        entity.getPaymentDate() != null
                                ? Date.from(entity.getPaymentDate().atZone(ZoneId.systemDefault()).toInstant())
                                : null // Convert LocalDateTime to Date
                ))
                .collect(Collectors.toList());
    }
}

