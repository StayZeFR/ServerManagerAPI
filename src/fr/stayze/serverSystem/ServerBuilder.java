package fr.stayze.serverSystem;

import fr.stayze.ServerManagerAPI;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerBuilder {

    private static final Configuration bungeeConfig = ServerManagerAPI.getInstance().bungeeConfig;
    private static ServerManagerAPI api = ServerManagerAPI.getInstance();

    private String serverID;
    private Integer serverPort;
    private String serverPath;
    private String serverRam;


    public ServerBuilder(String serverID, Integer serverPort, String serverPath, String serverRam){

        this.serverID = serverID;
        this.serverPort = serverPort;
        this.serverPath = serverPath;
        this.serverRam = serverRam;

    }

    public String getID(){return serverID;}

    public Integer getPort(){return serverPort;}

    public String getPath(){return serverPath;}

    public String getRam(){return serverRam;}

    public void setRam(String ram){this.serverRam = ram;}

    //PATH -> Error to get list type !

    public void setDefaultServer(){
        //ProxyServer.getInstance().getPluginManager().;
    }

    public void addToDefaultServer(){
        //bungeeConfig.getStringList("listeners.priorities").add(serverID);
        System.out.println(bungeeConfig.get("listeners.priorities"));
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(api.bungeeConfig, new File("config.yml"));
        } catch (IOException e) {e.printStackTrace();}
    }

    //PATH -> Error to get list type !

    public void stop(){

        stopExecution();
        ProxyServer.getInstance().getConfig().getServers().remove(serverID);

        api.serversOnline.remove(serverID);

    }

    public void delete(){
        if (api.serversOnline.contains(serverID)) stop();
        api.serversList.remove(serverID);
        api.serversListener.set(serverID, null);
        try {
            FileUtils.deleteDirectory(serverPath);
        } catch (IOException e) {e.printStackTrace();}
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(api.serversListener, new File(api.pathServersListener));
        } catch (IOException e) {e.printStackTrace();}
    }

    private void stopExecution(){
        Runtime runtime = Runtime.getRuntime();
        String cmd = "screen -X -S %id stuff stop\\n\"".replaceAll("%id", serverID);
        try{
            Process process = runtime.exec(cmd);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start(){

        executeJar();
        reloadConfig();

        api.serversOnline.add(serverID);

    }

    private void executeJar(){
        Runtime runtime = Runtime.getRuntime();
        String cmd = "screen -dmS %id java -Xms%ram -Xmx%ram -jar server.jar".replaceAll("%id", serverID).replaceAll("%ram", serverRam);
        try {
            Process process = runtime.exec(cmd, null, new File(serverPath));
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void reloadConfig(){
        ServerInfo si = new BungeeServerInfo(serverID, new InetSocketAddress(serverPort), "motd", false);
        ProxyServer.getInstance().getConfig().getServers().putIfAbsent(serverID, si);
    }

}
