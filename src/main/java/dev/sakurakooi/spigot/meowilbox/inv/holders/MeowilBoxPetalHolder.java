package dev.sakurakooi.spigot.meowilbox.inv.holders;

import com.saicone.rtag.RtagItem;
import dev.sakurakooi.spigot.meowilbox.inv.MeowilBoxHolder;
import dev.sakurakooi.spigot.meowilbox.utils.InventoryUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.stream.IntStream;

import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class MeowilBoxPetalHolder implements MeowilBoxHolder {
    @Getter
    private Inventory inventory;

    private final Player player;
    private final int heldItemSlot;
    @Getter
    @Setter
    private ItemStack petalItem;

    public MeowilBoxPetalHolder(Player player, int heldItemSlot, ItemStack petalItem) {
        this.player = player;
        this.heldItemSlot = heldItemSlot;
        this.petalItem = petalItem;
        this.inventory = Bukkit.createInventory(this, 27, Objects.requireNonNullElse(petalItem.getItemMeta().displayName(),
                Component.text("樱花手袋").color(TextColor.color(0xff4081)).decoration(ITALIC, false)));
        RtagItem tag = RtagItem.of(petalItem);
        var items = InventoryUtils.getInventory(tag);
        items.forEach(inventory::setItem);
    }

    @Override
    public void saveData() {
        RtagItem.edit(petalItem, tag -> {
            InventoryUtils.setInventory(tag, IntStream.range(0, inventory.getSize()).boxed()
                    .filter(index -> inventory.getItem(index) != null)
                    .collect(HashMap::new, (map, index) -> map.put(index, inventory.getItem(index)), HashMap::putAll)
            );
        });
        player.getInventory().setItem(heldItemSlot, petalItem);
    }
}
