package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.TransferRequest;
import com.css.coupon_sale.dto.response.TransferResponse;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.entity.TransferEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.FriendshipRepository;
import com.css.coupon_sale.repository.SaleCouponRepository;
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

    @Autowired
    private SaleCouponRepository saleCouponRepository;

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

    @Override
    public List<TransferResponse> getCouponsForAcceptor(Long acceptorId) {
        List<TransferEntity> transfers = transferRepository.findByAccepter_Id(acceptorId);
        return transfers.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean transferCoupon(int saleCouponId, Long acceptorId) {
        try {

            SaleCouponEntity saleCoupon = saleCouponRepository.findById(saleCouponId)
                    .orElseThrow(() -> new RuntimeException("Sale Coupon ID not found: " + saleCouponId));


            if (saleCoupon.getStatus() == 2) {
                System.out.println("Sale Coupon has already been transferred. Transfer not allowed.");
                return false;
            }


            saleCoupon.setStatus(2); // Set status to 2 (transferred)
            saleCouponRepository.save(saleCoupon); // Save the updated SaleCoupon

            System.out.println("Sender: " + saleCoupon.getUser().getId());
            System.out.println("Acceptor: " + acceptorId);

            // Create a TransferEntity and set its properties
            UserEntity acceptor = new UserEntity();
            acceptor.setId(acceptorId);

            TransferEntity transferEntity = new TransferEntity();
            transferEntity.setSaleCoupon(saleCoupon);
            transferEntity.setSender(saleCoupon.getUser()); // Assuming sender is stored in SaleCoupon
            transferEntity.setAccepter(acceptor); // Assuming accepter is stored in SaleCoupon
            transferEntity.setStatus(0); // Status of transfer, can be adjusted (e.g., 1 = successful)
            transferEntity.setTransferAt(LocalDateTime.now()); // Set the timestamp of the transfer

            // Save the transfer data
            transferRepository.save(transferEntity);

            return true; // Return true if transfer is successful
        } catch (RuntimeException e) {
            System.out.println("Error transferring Sale Coupon: " + e.getMessage());
            return false; // Return false in case of an error
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            return false; // Return false for unexpected errors
        }
    }

    @Override
    public List<TransferResponse> getCouponsForSender(Long senderId) {
        List<TransferEntity> transfers = transferRepository.findBySender_Id(senderId);
        return transfers.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    private TransferResponse mapToResponseDTO(TransferEntity transfer) {
        TransferResponse dto = new TransferResponse();
        dto.setTransferId(transfer.getId());
        dto.setSenderId(transfer.getSender().getId());
        dto.setSenderName(transfer.getSender().getName());
        dto.setAccepterName(transfer.getAccepter().getName());
        dto.setAccepterId(transfer.getAccepter().getId());
        dto.setSaleCouponId(transfer.getSaleCoupon().getId());
        dto.setStatus(transfer.getStatus());
        dto.setTransferAt(transfer.getTransferAt());
        return dto;
    }
}
