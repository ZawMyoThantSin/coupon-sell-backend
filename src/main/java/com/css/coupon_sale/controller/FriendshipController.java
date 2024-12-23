package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.FriendshipRequest;
import com.css.coupon_sale.dto.response.FriendshipResponse;
import com.css.coupon_sale.dto.response.UserResponse;
import com.css.coupon_sale.service.FriendshipService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {

    @Autowired
    private FriendshipService service;


    @PostMapping
    public ResponseEntity<FriendshipResponse> sendFriendRequest(@RequestBody FriendshipRequest request) {
        System.out.println("Received Friend Request: " + request);
        FriendshipResponse response = service.sendFriendRequest(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponse> acceptFriendRequest(@PathVariable int id) {
        FriendshipResponse response = service.acceptFriendRequest(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deny")
    public ResponseEntity<FriendshipResponse> denyFriendRequest(@PathVariable int id) {
        FriendshipResponse response = service.denyFriendRequest(id);
        return ResponseEntity.ok(response);
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
        service.unfriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
