package dev.dotto.lifoapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long itemId;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 512)
    private String description;

    private String image;

    @NotBlank
    private String type;

    @NotNull
    @Column(name = "buy_level")
    private int buyLevel;

    @NotNull
    @Column(name = "find_level")
    private int findLevel;

    @NotNull
    @Column(name = "use_level")
    private int useLevel;

    @NotNull
    @Column(name = "gold_value")
    private Long goldValue;

    @NotNull
    private int protection;

    @NotNull
    private int attack;

    @NotNull
    private int health;

    @NotNull
    @Column(name = "find_possibility")
    private int findPossibility;

    @NotNull
    @Column(name = "find_experience")
    private Long findExperience;

    // 0: Armor/Weapon
    // 1: One use item (ethereal items or potions)
    // 2: Food
    // 3: Potion
    // 4: Resource
    @NotNull
    private int uses;

    @NotNull
    private int set; // Don't know what this is for

}
