import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class FlameEngineEnchantment extends Enchantment implements Listener, CommandExecutor {

    private static final FlameEngineEnchantment INSTANCE = new FlameEngineEnchantment(255);

    private FlameEngineEnchantment(int id) {
        super(id);
    }

    public static FlameEngineEnchantment getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.IRON_SWORD;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public String getName() {
        return "FlameEngine";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            ItemStack weapon = player.getItemInHand();
            if (weapon != null && weapon.containsEnchantment(this)) {
                player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                Entity target = event.getEntity();
                double damage = 2.0;
                double fireDamage = 1.0;
                target.damage(damage);
                target.setFireTicks(20);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみが実行できます。");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "使用法: /besenceenchant <エンチャントレベル>");
            return true;
        }

        int enchantLevel;
        try {
            enchantLevel = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "エンチャントレベルは整数で指定してください。");
            return true;
        }

        ItemStack enchantBook = createEnchantBook(enchantLevel);
        if (enchantBook == null) {
            player.sendMessage(ChatColor.RED + "エンチャントの作成に失敗しました。");
            return true;
        }

        player.getInventory().addItem(enchantBook);
        player.sendMessage(ChatColor.GREEN + "エンチャント本を手に入れました。");

        return true;
    }

    private ItemStack createEnchantBook(int enchantLevel) {
        ItemStack enchantBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) enchantBook.getItemMeta();

        // エンチャントを追加
        enchantMeta.addStoredEnchant(this, enchantLevel, true);

        // アイテムのメタデータを更新
        enchantBook.setItemMeta(enchantMeta);

        return enchantBook;
    }
}
