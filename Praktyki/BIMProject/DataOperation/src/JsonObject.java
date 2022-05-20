import java.util.ArrayList;

public class JsonObject {
    private String id, name, type, objectType, tag, layer;
    private ArrayList<PropertySet> list;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }
}
