package demo.frontend;

import demo.common.DefaultConfig;

public class FrontLoader {
    public static void main(String[] args) {
        DefaultConfig.getConfig().load();
        FrontServerThread f1= new FrontServerThread();
        f1.init(8082);
        FrontServerThread f2= new FrontServerThread();
        f2.init(8083);
    /*    FrontServer server1 = new FrontServer();
        server1.serve(8083);*/
    }
}
