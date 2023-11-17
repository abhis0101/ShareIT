package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 300)
    private String name;

    @NotBlank
    @Size(min = 1, max = 1000)
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}
