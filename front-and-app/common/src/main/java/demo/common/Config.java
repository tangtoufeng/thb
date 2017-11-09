package demo.common;

public interface Config {
	
    void load();

    String getStrByKey(ConfigEntry key);

    int getIntByKey(ConfigEntry key);
}
