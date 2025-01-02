package com.css.coupon_sale.controller;

import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.service.SaleCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accept")
public class SaleCouponController {
    @Autowired
    private SaleCouponService saleCouponService;

    @PostMapping
    public String saveCoupon(@ModelAttribute OrderEntity orderEntity, Model model) {
        boolean isSaved = saleCouponService.saveSaleCouponFromOrder(orderEntity);

        if (isSaved) {
            model.addAttribute("message", "Sale coupon saved successfully.");
            return "successPage"; // Redirect or render a success page
        } else {
            model.addAttribute("error", "Failed to save sale coupon.");
            return "errorPage"; // Redirect or render an error page
        }
    }
}
