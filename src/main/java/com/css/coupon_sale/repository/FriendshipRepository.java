package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.FriendShipEntity;
import com.css.coupon_sale.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendShipEntity, Integer> {

    boolean existsBySenderAndAccepter(UserEntity sender, UserEntity accepter);

    @Query("SELECT f FROM FriendShipEntity f WHERE (f.sender = :user OR f.accepter = :user) AND f.status = :status")
    List<FriendShipEntity> findAllBySenderOrAccepterAndStatus(@Param("user") UserEntity user, @Param("status") int status);

    List<FriendShipEntity> findAllByAccepterAndStatus(UserEntity user, int status);

    List<FriendShipEntity> findAllBySenderAndStatus(UserEntity user, int status);

    // Search users by email with partial match
    @Query("SELECT u FROM UserEntity u WHERE u.email LIKE %:email%")
    List<UserEntity> findUsersByEmail(@Param("email") String email);

    @Query("SELECT f FROM FriendShipEntity f WHERE " +
            "((f.sender = :user1 AND f.accepter = :user2) OR " +
            "(f.sender = :user2 AND f.accepter = :user1)) " +
            "AND f.status = 1")
    List<FriendShipEntity> findByUsers(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);

    @Query("SELECT u FROM UserEntity u WHERE u.email LIKE %:email% " +
            "AND u.id <> :loggedInUserId " + // Exclude logged-in user
            "AND u.id NOT IN (SELECT f.sender.id FROM FriendShipEntity f WHERE f.accepter.id = :loggedInUserId AND f.status = 1) " + // Exclude friends
            "AND u.id NOT IN (SELECT f.accepter.id FROM FriendShipEntity f WHERE f.sender.id = :loggedInUserId AND f.status = 1) " + // Exclude friends
            "AND u.id NOT IN (SELECT f.sender.id FROM FriendShipEntity f WHERE f.accepter.id = :loggedInUserId AND f.status = 0) " + // Exclude pending sent requests
            "AND u.id NOT IN (SELECT f.accepter.id FROM FriendShipEntity f WHERE f.sender.id = :loggedInUserId AND f.status = 0)") // Exclude pending received requests
    List<UserEntity> searchEligibleUsersByEmail(@Param("email") String email, @Param("loggedInUserId") int loggedInUserId);

    @Query("SELECT f FROM FriendShipEntity f " +
            "WHERE (f.sender = :friend OR f.accepter = :friend) " +
            "AND f.status = :status")
    List<FriendShipEntity> findByAccepterOrSenderAndStatus(@Param("friend") UserEntity friend, @Param("status") int status);

}
