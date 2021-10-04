package fr.stayze;

import fr.stayze.channelManager.receiverRequest;
import fr.stayze.commands.ServerManagerCMD;
import fr.stayze.serverSystem.ServerBuilder;
import fr.stayze.serverSystem.ServerCreator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerManagerAPI extends Plugin {

    private static ServerManagerAPI instance;
    public String pathBasicConfig = "ServerManagerAPI/config.yml";
    public String pathServersListener = "ServerManagerAPI/servers.yml";

    public Configuration basicConfig;
    public Configuration serversListener;
    public Configuration bungeeConfig;

    public HashMap<String, ServerBuilder> serversList = new HashMap<>();
    public List<String> serversOnline = new ArrayList<>();

    @Override
    public void onEnable() {

        instance = this;

        setupFile();
        loadClass();
        initServersList();

        getProxy().registerChannel("ServerManagerAPI");
        getProxy().getPluginManager().registerListener(this, new receiverRequest());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ServerManagerCMD());

        stopServers();

        //ServerBuilder sb = ServerCreator.getInstance().create("tntrun");
        //sb.setRam("1G");
        //sb.addToDefaultServer();
        //sb.start();

    }

    private void stopServers(){
        if (serversOnline.size() > 0){
            for (int i = 0; i < serversOnline.size(); i++){
                ServerBuilder sb = serversList.get(serversOnline.get(i));
                sb.stop();
            }
        }
    }

    private void initServersList(){
        for (String id : serversListener.getKeys()){
            Integer port = serversListener.getInt(id + ".port");
            String path = serversListener.getString(id + ".path");
            String ram = serversListener.getString(id + ".ram");
            ServerBuilder sb = new ServerBuilder(id, port, path, ram);
            serversList.put(id, sb);
        }
    }

    private void loadClass(){
        new ServerCreator();
    }

    private void setupFile() {
        try {
            bungeeConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getProxy().getPluginsFolder().getParentFile(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File f = new File(pathBasicConfig);
            if (!f.exists()){
                f.getParentFile().mkdirs();
                f.createNewFile();
                basicConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);
                initBasicConfig();
            }else{
                basicConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);
            }
        }catch (IOException e){e.printStackTrace();}

        try {
            File f = new File(pathServersListener);
            if (!f.exists()){
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            serversListener = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);
        }catch (IOException e){e.printStackTrace();}
    }

    private void initBasicConfig(){

        basicConfig.set("pathTemplates", "../Templates/");
        basicConfig.set("pathDestination", "../Servers/");
        basicConfig.set("portStart", 25566);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(basicConfig, new File(pathBasicConfig));
        } catch (IOException e) {e.printStackTrace();}

    }

    @Override
    public void onDisable() {

    }

    public static ServerManagerAPI getInstance() {
        return instance;
    }

}
