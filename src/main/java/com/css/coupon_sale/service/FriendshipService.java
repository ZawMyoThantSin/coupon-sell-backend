package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.FriendshipRequest;
import com.css.coupon_sale.dto.response.FriendshipResponse;
import com.css.coupon_sale.dto.response.UserResponse;

import java.util.List;

public interface FriendshipService {

    FriendshipResponse sendFriendRequest(FriendshipRequest request);
    FriendshipResponse acceptFriendRequest(int id);
    FriendshipResponse deleteFriendRequest(int id);
    List<FriendshipResponse> getFriends(int userId);
    List<FriendshipResponse> getPendingRequests(int userId);
    List<FriendshipResponse> getSentPendingRequests(int userId);
    List<UserResponse> searchUsersByEmail(String email);
    void unfriend(int userId, int friendId);
    UserResponse getFriendDetailById(Long friendId);
}
