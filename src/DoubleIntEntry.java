public class DoubleIntEntry {
    public BPlusNode node;
    public int oldKey, newKey;

    public DoubleIntEntry() {
    }

    public void setOldKey(int oldKey) {
        this.oldKey = oldKey;
    }

    public int getOldKey() {
        return oldKey;
    }

    public void setNewKey(int newKey) {
        this.newKey = newKey;
    }

    public int getNewKey() {
        return newKey;
    }

    public void setNode(BPlusNode node) {
        this.node = node;
    }

    public BPlusNode getNode() {
        return node;
    }
}

