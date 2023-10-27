package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ItemDtoShort {
    private Long id;
    @NotBlank
    private String name;

}
