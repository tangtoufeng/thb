package demo.frontend;

import demo.common.DefaultConfig;

public class FrontLoader {
    public static void main(String[] args) {
        DefaultConfig.getConfig().load();
        FrontServer server = new FrontServer();
        server.serve();
    }
}
