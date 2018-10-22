public class Entry {
    public BPlusNode node;
    public int key;

    public Entry() {
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setNode(BPlusNode node) {
        this.node = node;
    }

    public BPlusNode getNode() {
        return node;
    }
}
