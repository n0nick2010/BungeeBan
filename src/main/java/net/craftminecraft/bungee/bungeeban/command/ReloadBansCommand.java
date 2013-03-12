package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadBansCommand extends Command {

	public ReloadBansCommand() {
		super("reloadbans");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!Utils.hasPermission(sender, "reloadbans", "")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
			return;
		}
		BanManager.reload();
		sender.sendMessage(ChatColor.RED + "Reloaded banlist.");
	}
}