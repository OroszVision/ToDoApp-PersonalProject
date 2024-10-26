package com.example.todoapppersonal.repository;

import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.model.Friendship;
import com.example.todoapppersonal.model.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFriendshipRepository extends JpaRepository<Friendship,Long>{
        @Query("SELECT f.friend FROM Friendship f WHERE f.user.id = :userId AND f.status = 'ACCEPTED'")
        List<AppUser> findAcceptedFriends(@Param("userId") Long userId);

        @Query("SELECT f FROM Friendship f WHERE f.user = :user AND f.friend = :friend")
        Optional<Friendship> findByUserAndFriend(@Param("user") AppUser user, @Param("friend") AppUser friend);

        @Query("SELECT f FROM Friendship f WHERE f.friend = :user AND f.status = 'PENDING'")
        List<Friendship> findByFriendAndStatus(@Param("user") AppUser user, @Param("status") FriendshipStatus status);


}
