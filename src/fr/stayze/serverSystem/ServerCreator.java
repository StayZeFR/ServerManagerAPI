package fr.stayze.serverSystem;

import fr.stayze.ServerManagerAPI;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ServerCreator {

    private static ServerCreator instance;

    private ServerManagerAPI api = ServerManagerAPI.getInstance();
    private final String templatesDir = api.basicConfig.getString("pathTemplates");
    private final String destinationDir = api.basicConfig.getString("pathDestination");

    public ServerCreator(){
        instance = this;
    }

    public ServerBuilder create(String template) {
        File f = new File(templatesDir + template);
        if (f.isDirectory()){
            Integer port = definedPort();
            String id = template.toUpperCase() + "-" + port;
            String ram = "500M";
            String dest = destinationDir + id + "/";
            duplicateDirectory(dest, f.getPath());
            ServerBuilder sb = new ServerBuilder(id, port, dest + "/", ram);
            saveIntoFile(sb);
            //addServerToConfig(sb);
            modifyProperties(dest, port);
            api.serversList.put(sb.getID(), sb);
            return sb;
        }else {api.getLogger().info("Template doesn't exist !");}
        return null;
    }

    private void modifyProperties(String path, Integer port){
        String pathProp = path + "server.properties";
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(pathProp));
            prop.setProperty("server-port", String.valueOf(port));
            prop.store(new FileOutputStream(pathProp), null);
        }catch (IOException e){e.printStackTrace();}
    }

    private void saveIntoFile(ServerBuilder sb){
        api.serversListener.set(sb.getID() + ".port", sb.getPort());
        api.serversListener.set(sb.getID() + ".ram", sb.getRam());
        api.serversListener.set(sb.getID() + ".path", sb.getPath());
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(api.serversListener, new File(api.pathServersListener));
        } catch (IOException e) {e.printStackTrace();}
    }

    private void duplicateDirectory(String target, String source) {
        try {
            final Path targetPath = Paths.get(target);
            final Path sourcePath = Paths.get(source);
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (IOException e){e.printStackTrace();}
    }

    private Integer definedPort(){
        Integer port = api.basicConfig.getInt("portStart");
        List<Integer> list = new ArrayList<>();
        for (String id : api.serversListener.getKeys()){
            String[] split = id.split("-");
            Integer getPort = Integer.valueOf(split[1]);
            list.add(getPort);
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (!list.contains(port)) {
                    return port;
                }
                port++;
            }
        }
        return port;
    }

    public static ServerCreator getInstance() {
        return instance;
    }

}
