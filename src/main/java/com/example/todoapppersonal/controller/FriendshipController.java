package com.example.todoapppersonal.controller;

import com.example.todoapppersonal.dto.FriendRequestDto;
import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.repository.IAppUserRepository;
import com.example.todoapppersonal.service.FriendshipService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/friendships")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final IAppUserRepository appUserRepository;

    @PostMapping("/send/{friendId}")
    public ResponseEntity<Void> sendFriendRequest(@PathVariable Long friendId,
                                                  @AuthenticationPrincipal AppUser sender) {
        // Najde uživatele podle ID
        AppUser recipient = appUserRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        // Odeslání žádosti o přátelství
        friendshipService.sendFriendRequest(sender, recipient);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/accept/{friendshipId}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable Long friendshipId) {
        friendshipService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reject/{friendshipId}")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable Long friendshipId) {
        friendshipService.rejectFriendRequest(friendshipId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/send/{friendshipId}")
    public ResponseEntity<Void> removeFriendFromFriendList(@PathVariable Long friendshipId) {
        try {
            friendshipService.removeFriendFromFriendlist(friendshipId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestDto>> getPendingFriendRequests(@AuthenticationPrincipal AppUser currentUser) {
        List<FriendRequestDto> requests = friendshipService.getPendingFriendRequests(currentUser);
        return ResponseEntity.ok(requests);
    }


    @GetMapping("/my-friends")
    public ResponseEntity<List<AppUserDto>> getFriends(@AuthenticationPrincipal AppUser currentUser) {
        List<AppUser> friends = friendshipService.getFriends(currentUser);

        // Mapping List<AppUser> to List<AppUserDto>
        List<AppUserDto> friendsDtos = friends.stream()
                .map(friend -> new AppUserDto(friend.getUsername())) // Map to AppUserDto
                .collect(Collectors.toList()); // Collect results into a List

        return ResponseEntity.ok(friendsDtos);
    }
}
