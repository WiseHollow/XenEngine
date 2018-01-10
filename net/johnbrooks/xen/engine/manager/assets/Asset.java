package net.johnbrooks.xen.engine.manager.assets;

public class Asset {

    protected String name, path;
    protected Class type;

    public Asset(String name, String path, Class type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " | " + path + " | " + type.toString();
    }
}
