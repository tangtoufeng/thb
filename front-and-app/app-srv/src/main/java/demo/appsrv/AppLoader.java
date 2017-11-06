package demo.appsrv;

import demo.common.DefaultConfig;

public class AppLoader {
    public static void main(String[] args){
        DefaultConfig.getConfig().load();
        AppServer server = new AppServer();
        server.serve();
    }
}
