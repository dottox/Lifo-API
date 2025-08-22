package dev.dotto.lifoapi.util;

import dev.dotto.lifoapi.config.AppConstants;
import dev.dotto.lifoapi.models.Item;
import dev.dotto.lifoapi.models.User;
import dev.dotto.lifoapi.models.UserItem;
import dev.dotto.lifoapi.models.UserItemId;
import dev.dotto.lifoapi.repositories.ItemRepository;
import dev.dotto.lifoapi.repositories.UserItemRepository;
import dev.dotto.lifoapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GameUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    public void addGoldToPlayer(
            User player,
            Long goldGained
    ) {
        player.setGold(player.getGold() + goldGained);
        userRepository.save(player);
    }

    public void addExperienceToPlayer(
            User player,
            Long experienceGained
    ) {
        player.setCurrentLevelExperience(player.getCurrentLevelExperience() + experienceGained);
        player.setTotalExperience(player.getTotalExperience() + experienceGained);
        userRepository.save(player);;
    }

    // Function to calculate level ups, returns the number of levels gained
    @Transactional
    public Integer calculateLevelUps(
            User player
    ) {
        int levelUps = 0;

        while (_checkLevelUp(player)) {
            levelUps++;
        }

        return levelUps;
    }

    // Recursive function to level up the player, returns true if leveled up
    @Transactional
    public Boolean _checkLevelUp(
            User player
    ) {
        int currentLevel = player.getLevel();
        Long currentLevelExperience = player.getCurrentLevelExperience();

        // Calculate the experience required for the next level
        // Base experience is 1250, each level requires 1.2x more experience
        Long experienceToNextLevel = (long) (1250 * Math.pow(AppConstants.EXPERIENCE_MULTI, currentLevel - 1));
        player.setNextLevelExperience(experienceToNextLevel);

        // Check if the player has enough experience to level up
        if (currentLevelExperience >= experienceToNextLevel) {
                // Deduct the experience required for the next level and add to the current level experience
            player.setCurrentLevelExperience(player.getCurrentLevelExperience() - experienceToNextLevel);
            player.setLevel(currentLevel + 1);
            userRepository.save(player);

            return true;
        }

        return false;
    }


    @Transactional
    public List<Item> addItemsFromWorking(
            User player,
            Long workedTime // in seconds
    ) {
        // Get player details
        int level = player.getLevel();

        // Calculate the number of items based on worked time and percentage
        // Randomized between 0.5 and 1.5 items per hour
        int itemsGained = (int) Math.round(workedTime / 3600.0 * (0.5 + Math.random()));

        // Calculate items to give based on level and items gained
        List<Item> itemsToGive = _calculateItemsGained(level, itemsGained);

        // Add items to the player
        _addItemsToPlayer(player, itemsToGive);

        // Return the list of items given to the player
        return itemsToGive;
    }

    @Transactional
    public void _addItemsToPlayer(
            User player,
            List<Item> itemsToGive
    ) {

        // Map the player's existing items to a map for quick lookup
        Map<Long, UserItem> existingItems = player.getItems().stream()
                    .collect(Collectors.toMap(ui -> ui.getItem().getItemId(), ui -> ui));


        Long experienceGained = 0L;

        for (Item item : itemsToGive) {
            // Check if the item already exists in the player's items
            // If the item exists, increase the quantity, otherwise create a new UserItem
            UserItem userItem = existingItems.get(item.getItemId());
            if (userItem != null) {
                userItem.setQuantity(userItem.getQuantity() + 1);
            } else {
                UserItemId userItemId = new UserItemId();
                userItemId.setUser(player.getUserId());
                userItemId.setItem(item.getItemId());

                userItem = new UserItem();
                userItem.setItem(item);
                userItem.setQuantity(1);
                userItem.setUser(player);
                userItem.setUserItemId(userItemId);
                userItemRepository.save(userItem);

                player.getItems().add(userItem);
                existingItems.put(item.getItemId(), userItem);
            }
            // Sum the experience gained from the item
            experienceGained += item.getFindExperience();
        }

        userRepository.save(player);

        addExperienceToPlayer(player, experienceGained);
    }


    @Transactional
    public List<Item> _calculateItemsGained(
            int level,
            int itemsGained
    ) {
        if (AppConstants.DEV_MODE) {
            itemsGained += 50;
        }

        // Find items based on player level
        List<Item> possibleItems = itemRepository.findItemsByFindLevelLessThanEqual(level);
        Collections.shuffle(possibleItems);

        // Get a random selection of n=itemsGained items from the possible items
        List<Item> itemsToGive = new ArrayList<>(possibleItems.stream()
                .limit(itemsGained)
                .toList());

        // Add randoms items if the list is not completed
        if (itemsToGive.size() < itemsGained) {
            for (int i = itemsToGive.size(); i < itemsGained; i++) {
                Item randomItem = possibleItems.get((int) (Math.random() * possibleItems.size()));
                itemsToGive.add(randomItem);
            }
        }

        // Return the list of items to give
        return itemsToGive;
    }
}
