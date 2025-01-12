package com.css.coupon_sale.controller;

import com.css.coupon_sale.config.CustomWebSocketHandler;
import com.css.coupon_sale.dto.request.FriendshipRequest;
import com.css.coupon_sale.dto.response.FriendshipResponse;
import com.css.coupon_sale.dto.response.UserResponse;
import com.css.coupon_sale.entity.FriendShipEntity;
import com.css.coupon_sale.repository.FriendshipRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.FriendshipService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/friendship")
public class FriendshipController {

    private final FriendshipService service;
    private final CustomWebSocketHandler webSocketHandler;
    private final UserRepository URepository;
    private final FriendshipRepository friendshipRepository;

    @Autowired
    public FriendshipController(FriendshipService service, CustomWebSocketHandler webSocketHandler, UserRepository uRepository, FriendshipRepository friendshipRepository) {
        this.service = service;
        this.webSocketHandler = webSocketHandler;
        URepository = uRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @PostMapping
    public ResponseEntity<FriendshipResponse> sendFriendRequest(@RequestBody FriendshipRequest request) {
        try {
            System.out.println("Received Friend Request Id: " + request.getAccepterId());
            FriendshipResponse response = service.sendFriendRequest(request);
            String message = "FRIEND_REQUEST_RECEIVED";
            webSocketHandler.sendToUser((long) request.getAccepterId(), message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FriendshipResponse());
        }
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponse> acceptFriendRequest(@PathVariable int id) {
        try{
            FriendshipResponse response = service.acceptFriendRequest(id);
            String message = "FRIEND_REQUEST_ACCEPTED";
            webSocketHandler.sendToUser((long) response.getFriendId(), message);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/deny")
    public ResponseEntity<FriendshipResponse> denyFriendRequest(@PathVariable int id) {
        try {
            FriendshipResponse response = service.deleteFriendRequest(id);
            String message = "FRIEND_REQUEST_DENIED";
            System.out.println("Denied Friend ID : " + response.getFriendId());
            webSocketHandler.sendToUser(response.getFriendId(), message);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<FriendshipResponse> cancelFriendRequest(@PathVariable int id) {
        try {
            System.out.println("Canceled id " + id);

            // Fetch the FriendShipEntity using the ID
            Optional<FriendShipEntity> friendshipOptional = friendshipRepository.findById(id);

            if (friendshipOptional.isEmpty()) {
                // Return 404 if the friendship entity is not found
                System.err.println("Friendship entity not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            FriendShipEntity friendshipEntity = friendshipOptional.get();

            // Extract the accepter's ID directly from the FriendShipEntity
            Long accepterId = friendshipEntity.getAccepter().getId();
            System.out.println("Accepter ID: " + accepterId);
            FriendshipResponse response = service.deleteFriendRequest(id);
            System.out.println("Friend Id " + response.getFriendId());
            String message = "FRIEND_REQUEST_CANCELLED";
            webSocketHandler.sendToUser(accepterId, message);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<FriendshipResponse>> getFriends(@PathVariable int userId) {
        List<FriendshipResponse> friends = service.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{userId}/pending")
    public ResponseEntity<List<FriendshipResponse>> getPendingRequests(@PathVariable int userId) {
        List<FriendshipResponse> pendingRequests = service.getPendingRequests(userId);
        return ResponseEntity.ok(pendingRequests);
    }

    @GetMapping("/{userId}/sent-pending")
    public ResponseEntity<List<FriendshipResponse>> getSentPendingRequests(@PathVariable int userId) {
        List<FriendshipResponse> sentPendingRequests = service.getSentPendingRequests(userId);
        return ResponseEntity.ok(sentPendingRequests);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsersByEmail(
            @RequestParam("email") String email) {
        List<UserResponse> responses = service.searchUsersByEmail(email);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{userId}/unfriend/{friendId}")
    public ResponseEntity<Void> unfriend(
            @PathVariable int userId,
            @PathVariable int friendId) {
        try {
            service.unfriend(userId, friendId);
            String message = "UNFRIENDED";
            webSocketHandler.sendToUser((long) friendId, message);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/friend/{friendId}")
    public ResponseEntity<UserResponse> getFriendDetailById(@PathVariable Long friendId) {
        try {
            UserResponse response = service.getFriendDetailById(friendId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
