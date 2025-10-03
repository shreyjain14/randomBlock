package me.shreyjain.randomBlock;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RandomBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("randomblock.give") && !player.isOp()) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        // Determine amount
        int amount = 1;
        if (args.length > 0) {
            try {
                amount = Integer.parseInt(args[0]);
                if (amount <= 0 || amount > 64) {
                    player.sendMessage("§cAmount must be between 1 and 64!");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid amount! Usage: /randomblock [amount]");
                return true;
            }
        }

        // Create the special random block item
        ItemStack randomBlockItem = new ItemStack(Material.DIAMOND_BLOCK, amount);
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

        // Give the item to player
        player.getInventory().addItem(randomBlockItem);
        player.sendMessage("§aYou received §d" + amount + " Random Block" + (amount > 1 ? "s" : "") + "§a!");

        return true;
    }
}

