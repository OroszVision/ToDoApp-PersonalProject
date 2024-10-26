package com.example.todoapppersonal.service;

import com.example.todoapppersonal.dto.FriendRequestDto;
import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.model.Friendship;
import com.example.todoapppersonal.model.FriendshipStatus;
import com.example.todoapppersonal.repository.IFriendshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final IFriendshipRepository friendshipRepository;

    public void sendFriendRequest(AppUser sender, AppUser recipient) {
        // Create friendship from sender to recipient
        Friendship friendship = new Friendship();
        friendship.setUser(sender);
        friendship.setFriend(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);

        // Create friendship from recipient to sender
        Friendship reverseFriendship = new Friendship();
        reverseFriendship.setUser(recipient);
        reverseFriendship.setFriend(sender);
        reverseFriendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(reverseFriendship);
    }


    public void acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        // Update the status of the original friendship
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        // Update the reverse friendship to ACCEPTED as well
        Friendship reverseFriendship = friendshipRepository.findByUserAndFriend(
                friendship.getFriend(), friendship.getUser())
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        if (reverseFriendship != null) {
            reverseFriendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipRepository.save(reverseFriendship);
        }
    }

    public void rejectFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));
        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);

        // Optionally handle the reverse friendship if needed
        Friendship reverseFriendship = friendshipRepository.findByUserAndFriend(
                        friendship.getFriend(), friendship.getUser())
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        if (reverseFriendship != null) {
            reverseFriendship.setStatus(FriendshipStatus.REJECTED);
            friendshipRepository.save(reverseFriendship);
        }
    }

    public void removeFriendFromFriendlist(Long friendshipId) {
        // Najdi přátelství podle ID
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Přátelství nenalezeno"));

        // Najdi reverzní přátelství
        Optional<Friendship> reverseFriendship = friendshipRepository.findByUserAndFriend(friendship.getFriend(), friendship.getUser());

        // Odstranění aktuálního přátelství
        friendshipRepository.delete(friendship);

        // Odstranění reverzního přátelství, pokud existuje
        reverseFriendship.ifPresent(friendshipRepository::delete);
    }

    public List<FriendRequestDto> getPendingFriendRequests(AppUser user) {
        List<Friendship> friendships = friendshipRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING);

        // Převedení na DTO s ID
        return friendships.stream()
                .map(friendship -> new FriendRequestDto(friendship.getId(), friendship.getUser().getUsername()))
                .collect(Collectors.toList());
    }





    public List<AppUser> getFriends(AppUser user) {
        return friendshipRepository.findAcceptedFriends(user.getId());
    }
}

