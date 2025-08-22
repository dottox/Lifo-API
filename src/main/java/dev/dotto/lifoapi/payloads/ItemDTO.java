package dev.dotto.lifoapi.payloads;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long itemId;
    private String name;
    private String description;
    private String image;
    private String type;
    private Long goldValue;
    private Long findExperience;
    private int protection;
    private int attack;
    private int health;
    private int uses;
    private int set;
}
