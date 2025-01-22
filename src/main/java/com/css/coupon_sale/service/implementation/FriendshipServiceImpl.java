package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.FriendshipRequest;
import com.css.coupon_sale.dto.response.FriendshipResponse;
import com.css.coupon_sale.dto.response.UserResponse;
import com.css.coupon_sale.entity.FriendShipEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.FriendshipRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.FriendshipService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipRepository repo;

    @Autowired
    private UserRepository uRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public FriendshipResponse sendFriendRequest(FriendshipRequest request) {

        UserEntity sender = uRepo.findById((long) request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        UserEntity accepter = uRepo.findById((long) request.getAccepterId())
                .orElseThrow(() -> new RuntimeException("Accepter not found"));

        // Prevent duplicate or conflicting requests
        if (repo.existsBySenderAndAccepter(sender, accepter)) {
            throw new RuntimeException("Friend request already exists");
        }

        // Create friend request
        FriendShipEntity friendRequest = new FriendShipEntity();
        friendRequest.setSender(sender);
        friendRequest.setAccepter(accepter);
        friendRequest.setStatus(0); // Pending status
        friendRequest.setCreatedAt(LocalDateTime.now());

        FriendShipEntity savedRequest = repo.save(friendRequest);
        return mapToResponse(savedRequest, null);
    }

    @Override
    public FriendshipResponse acceptFriendRequest(int id) {
        FriendShipEntity friendRequest = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (friendRequest.getStatus() != 0) {
            throw new RuntimeException("Request is not in pending state");
        }

        // Accept the request
        friendRequest.setStatus(1); // Accepted
        friendRequest.setAcceptedDate(LocalDateTime.now());
        FriendShipEntity updatedRequest = repo.save(friendRequest);

        return mapToResponse(updatedRequest, null);
    }

    @Override
    public FriendshipResponse deleteFriendRequest(int id) {
        FriendShipEntity friendRequest = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // Delete the friendship record
        repo.delete(friendRequest);

        // Return a response with the details of the deleted friend request
        return mapToResponse(friendRequest, null);
    }

    @Override
    public List<FriendshipResponse> getFriends(int userId) {
        UserEntity user = uRepo.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendShipEntity> friends = repo.findAllBySenderOrAccepterAndStatus(user, 1);

        return friends.stream()
                .map(friendship -> mapToResponse(friendship, user))
                .collect(Collectors.toList());    }

    @Override
    public List<FriendshipResponse> getPendingRequests(int userId) {
        UserEntity user = uRepo.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendShipEntity> pendingRequests = repo.findAllByAccepterAndStatus(user, 0);

        return pendingRequests.stream()
                .map(friendship -> mapToResponse(friendship, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendshipResponse> getSentPendingRequests(int userId) {
        UserEntity user = uRepo.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        List<FriendShipEntity> sentPendingRequests = repo.findAllBySenderAndStatus(user, 0);

        return sentPendingRequests.stream()
                .map(friendship -> mapToResponse(friendship, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> searchUsersByEmail(String email, int loggedInUserId) {
        // Fetch eligible users from the repository
        List<UserEntity> users = repo.searchEligibleUsersByEmail(email, loggedInUserId);

        // Map the user entities to user responses
        return users.stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }


    @Override
    public void unfriend(int userId, int friendId) {
        UserEntity user = uRepo.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserEntity friend = uRepo.findById((long) friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        List<FriendShipEntity> friendships = repo.findByUsers(user, friend);
        if (friendships.isEmpty()) {
            throw new RuntimeException("Friendship does not exist");
        }
        if (friendships.size() > 1) {
            throw new RuntimeException("Duplicate friendships detected!");
        }

        repo.delete(friendships.get(0));
    }

    @Override
    public UserResponse getFriendDetailById(Long friendId) {
        UserEntity friend = uRepo.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found with ID: " + friendId));

        List<FriendShipEntity> friendships = repo.findByAccepterOrSenderAndStatus(friend, 1);
        if (friendships.isEmpty()) {
            throw new EntityNotFoundException("Friendship not found for friend ID: " + friendId);
        }

        FriendShipEntity friendship = friendships.get(0); // Get the first record

        UserResponse response = mapper.map(friend, UserResponse.class);
        response.setAcceptedDate(friendship.getAcceptedDate());
        return response;
    }




    private FriendshipResponse mapToResponse(FriendShipEntity friendship, UserEntity loggedInUser) {
        FriendshipResponse response = new FriendshipResponse();
        if (loggedInUser != null) {
            if (friendship.getSender().equals(loggedInUser)) {
                response.setFriendName(friendship.getAccepter().getName());
            } else {
                response.setFriendName(friendship.getSender().getName());
            }
        } else {
            response.setSenderName(friendship.getSender().getName());
            response.setAccepterName(friendship.getAccepter().getName());
        }
        response.setId(friendship.getId());
        response.setFriendId(friendship.getSender().equals(loggedInUser) ? friendship.getAccepter().getId() : friendship.getSender().getId()
        );

        response.setStatus(friendship.getStatus());
        response.setCreatedAt(friendship.getCreatedAt());
        response.setAcceptedDate(friendship.getAcceptedDate());

        return response;
    }
}
