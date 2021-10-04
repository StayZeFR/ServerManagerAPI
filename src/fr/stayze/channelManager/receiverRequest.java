package fr.stayze.channelManager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.stayze.ServerManagerAPI;
import fr.stayze.serverSystem.ServerBuilder;
import fr.stayze.serverSystem.ServerCreator;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class receiverRequest implements Listener {

    private static ServerManagerAPI api = ServerManagerAPI.getInstance();

    @EventHandler
    public void onRequest(PluginMessageEvent e){

        System.out.println(e.getTag());

        if (e.getTag().equalsIgnoreCase("ServerManagerAPI")) {

            final ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
            final String sub = in.readUTF();

            System.out.println("sub -> " + sub);

            if (sub.equalsIgnoreCase("create")){
                final String template = in.readUTF();
                ServerCreator.getInstance().create(template);
            }else if (sub.equalsIgnoreCase("start")){
                final String id = in.readUTF();
                if (api.serversList.containsKey(id)){
                    ServerBuilder sb = api.serversList.get(id);
                    sb.start();
                }
            }else if (sub.equalsIgnoreCase("stop")){
                final String id = in.readUTF();
                if (api.serversList.containsKey(id)){
                    ServerBuilder sb = api.serversList.get(id);
                    sb.stop();
                }
            }else if (sub.equalsIgnoreCase("delete")){
                final String id = in.readUTF();
                if (api.serversList.containsKey(id)){
                    ServerBuilder sb = api.serversList.get(id);
                    sb.delete();
                }
            }

        }

    }

}
