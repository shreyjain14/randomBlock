package me.shreyjain.randomBlock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RandomBlockListener implements Listener {

    private final RandomBlock plugin;
    private final Map<String, Material> placedBlocks; // Store location -> random block type
    private final List<Material> validBlocks;
    private final Random random;

    public RandomBlockListener(RandomBlock plugin) {
        this.plugin = plugin;
        this.placedBlocks = new HashMap<>();
        this.random = new Random();
        this.validBlocks = new ArrayList<>();

        // Populate list with all placeable blocks
        for (Material material : Material.values()) {
            if (material.isBlock() && !material.isAir() && material.isSolid()) {
                validBlocks.add(material);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = event.getItemInHand();

        // Only transform if it's the special Random Block item
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            if (!displayName.equals("§d§lRandom Block")) {
                return; // Not our special item, don't transform
            }
        } else {
            return; // Regular block, don't transform
        }

        // Choose a random block from all valid blocks
        Material randomMaterial = validBlocks.get(random.nextInt(validBlocks.size()));

        // Store the location and the random block type
        String locationKey = getLocationKey(block);
        placedBlocks.put(locationKey, randomMaterial);

        // Change the block to the random material
        block.setType(randomMaterial);

        player.sendMessage("§aYour block transformed into: §e" + randomMaterial.name());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        String locationKey = getLocationKey(block);

        // Check if this block was placed by our plugin
        if (placedBlocks.containsKey(locationKey)) {
            Material randomMaterial = placedBlocks.get(locationKey);
            Player player = event.getPlayer();

            // Cancel normal drops
            event.setDropItems(false);

            // Create the special Random Block item to drop
            ItemStack randomBlockItem = new ItemStack(Material.DIAMOND_BLOCK, 1);
            ItemMeta meta = randomBlockItem.getItemMeta();

            if (meta != null) {
                meta.setDisplayName("§d§lRandom Block");
                List<String> lore = new ArrayList<>();
                lore.add("§7Place this block to transform");
                lore.add("§7it into a random Minecraft block!");
                lore.add("");
                lore.add("§6✨ Break it to get the random block back!");
                meta.setLore(lore);

                // Add enchant glow
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                randomBlockItem.setItemMeta(meta);
            }

            // Drop the special Random Block item instead
            block.getWorld().dropItemNaturally(block.getLocation(), randomBlockItem);

            // Remove from tracking
            placedBlocks.remove(locationKey);

            player.sendMessage("§aYou received your §d§lRandom Block §aback! (It was: §e" + randomMaterial.name() + "§a)");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled()) return;

        // Check all blocks being pushed
        for (Block block : event.getBlocks()) {
            String oldLocationKey = getLocationKey(block);

            // If this is one of our tracked blocks
            if (placedBlocks.containsKey(oldLocationKey)) {
                Material material = placedBlocks.remove(oldLocationKey);

                // Calculate new location after push
                Block newBlock = block.getRelative(event.getDirection());
                String newLocationKey = getLocationKey(newBlock);

                // Re-track at new location
                placedBlocks.put(newLocationKey, material);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled()) return;

        // Check all blocks being pulled
        for (Block block : event.getBlocks()) {
            String oldLocationKey = getLocationKey(block);

            // If this is one of our tracked blocks
            if (placedBlocks.containsKey(oldLocationKey)) {
                Material material = placedBlocks.remove(oldLocationKey);

                // Calculate new location after pull
                Block newBlock = block.getRelative(event.getDirection());
                String newLocationKey = getLocationKey(newBlock);

                // Re-track at new location
                placedBlocks.put(newLocationKey, material);
            }
        }
    }

    private String getLocationKey(Block block) {
        return block.getWorld().getName() + "," +
               block.getX() + "," +
               block.getY() + "," +
               block.getZ();
    }

    public Map<String, Material> getPlacedBlocks() {
        return placedBlocks;
    }
}
