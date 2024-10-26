package com.example.todoapppersonal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private Long friendshipId;  // Přidání ID přátelství
    private String friendUsername;
}
