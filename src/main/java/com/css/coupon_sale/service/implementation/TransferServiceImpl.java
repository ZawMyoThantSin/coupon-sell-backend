package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.TransferRequest;
import com.css.coupon_sale.dto.response.TransferResponse;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.entity.TransferEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.FriendshipRepository;
import com.css.coupon_sale.repository.TransferRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private TransferRepository transferRepository;

//    @Autowired
//    private SaleCouponRepository saleCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public TransferResponse transferCoupon(TransferRequest requestDTO) {

        UserEntity sender = userRepository.findById(requestDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        UserEntity accepter = userRepository.findById(requestDTO.getAccepterId())
                .orElseThrow(() -> new RuntimeException("Accepter not found"));

//        boolean areFriends = friendshipRepository.findByUsers(sender, accepter)
//                .get(friendship -> friendship.getStatus() == 1) // 1 means Accepted
//                .orElse(false);
//        if (!areFriends) {
//            throw new RuntimeException("Transfer can only occur between friends");
//        }

//        SaleCouponEntity saleCoupon = saleCouponRepository.findById(requestDTO.getSaleCouponId())
//                .orElseThrow(() -> new RuntimeException("Sale Coupon not found"));
//
//        if (!saleCoupon.getUser().getId().equals(sender.getId())) {
//            throw new RuntimeException("Sender does not own the coupon");
//        }
//
//        if (saleCoupon.getExpiredDate().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("Coupon is expired and cannot be transferred");
//        }
//
//        boolean existsTransfer = transferRepository.existsBySenderAndAccepterAndSaleCouponAndStatus(
//                sender, accepter, saleCoupon, 0); // 0 is for Pending
//        if (existsTransfer) {
//            throw new RuntimeException("A pending transfer already exists for this coupon");
//        }

        TransferEntity transfer = new TransferEntity();
        transfer.setSender(sender);
        transfer.setAccepter(accepter);
//        transfer.setSaleCoupon(saleCoupon);
        transfer.setStatus(0); // Pending
        transfer.setTransferAt(LocalDateTime.now());

        TransferEntity savedTransfer = transferRepository.save(transfer);

        return mapToResponseDTO(savedTransfer);
    }

    @Override
    public TransferResponse acceptTransfer(int transferId) {
        TransferEntity transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        transfer.setStatus(1); // Accepted
        transferRepository.save(transfer);

        return mapToResponseDTO(transfer);
    }

    @Override
    public TransferResponse denyTransfer(int transferId) {
        TransferEntity transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        transfer.setStatus(-1); // Denied
        transferRepository.save(transfer);

        return mapToResponseDTO(transfer);
    }

    @Override
    public List<TransferResponse> getTransferHistory(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TransferEntity> transfers = transferRepository.findBySenderOrAccepter(user, user);

        return transfers.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    private TransferResponse mapToResponseDTO(TransferEntity transfer) {
        TransferResponse dto = new TransferResponse();
        dto.setTransferId(transfer.getId());
        dto.setSenderId(transfer.getSender().getId());
        dto.setAccepterId(transfer.getAccepter().getId());
        dto.setSaleCouponId(transfer.getSaleCoupon().getId());
        dto.setStatus(transfer.getStatus());
        dto.setTransferAt(transfer.getTransferAt());
        return dto;
    }
}
