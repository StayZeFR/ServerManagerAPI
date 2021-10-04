package fr.stayze.commands;

import fr.stayze.ServerManagerAPI;
import fr.stayze.serverSystem.ServerBuilder;
import fr.stayze.serverSystem.ServerCreator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ServerManagerCMD extends Command {

    private static ServerManagerAPI api = ServerManagerAPI.getInstance();

    public ServerManagerCMD() {
        super("servermanager");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("servermanager.use")){
            if (args.length > 0){
                if (args[0].equalsIgnoreCase("create")){
                    if (args[1].length() > 0){
                        ServerBuilder sb = ServerCreator.getInstance().create(args[1]);
                        sender.sendMessage("Server create -> " + sb.getID());
                    }else {
                        sender.sendMessage("Command -> /servermanager create <template>");
                    }
                }else if (args[0].equalsIgnoreCase("start")) {
                    if (args[1].length() > 0) {
                        if (api.serversList.containsKey(args[1].toUpperCase())) {
                            ServerBuilder sb = api.serversList.get(args[1].toUpperCase());
                            sb.start();
                            sender.sendMessage("Server start -> " + sb.getID());
                        } else {
                            sender.sendMessage("Command -> /servermanager start <id>");
                        }
                    } else {
                        sender.sendMessage("Command -> /servermanager start <id>");
                    }
                }else if (args[0].equalsIgnoreCase("stop")) {
                    if (args[1].length() > 0) {
                        if (api.serversList.containsKey(args[1].toUpperCase())) {
                            ServerBuilder sb = api.serversList.get(args[1].toUpperCase());
                            sb.stop();
                            sender.sendMessage("Server stop -> " + sb.getID());
                        } else {
                            sender.sendMessage("Command -> /servermanager stop <id>");
                        }
                    } else {
                        sender.sendMessage("Command -> /servermanager stop <id>");
                    }
                }else if (args[0].equalsIgnoreCase("delete")) {
                    if (args[1].length() > 0) {
                        if (api.serversList.containsKey(args[1].toUpperCase())) {
                            ServerBuilder sb = api.serversList.get(args[1].toUpperCase());
                            sb.delete();
                            sender.sendMessage("Server delete -> " + sb.getID());
                        } else {
                            sender.sendMessage("Command -> /servermanager delete <id>");
                        }
                    } else {
                        sender.sendMessage("Command -> /servermanager delete <id>");
                    }
                }
            }else {
                sender.sendMessage("Command -> /servermanager create <template>");
                sender.sendMessage("Command -> /servermanager delete <id>");
                sender.sendMessage("Command -> /servermanager start <id>");
                sender.sendMessage("Command -> /servermanager stop <id>");
            }
        }
    }
}
