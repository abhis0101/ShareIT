package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDtoShort {

    private Long id;
    @NotBlank
    private String name;
}
