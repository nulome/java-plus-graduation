package ru.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowingDto {
    private Long id;
    private String email;
    private String name;
    private Set<Long> following;
}
