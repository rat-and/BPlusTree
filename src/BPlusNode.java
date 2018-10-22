
public class BPlusNode {
    public int order;       //-------------------------------------------------------------------------same as treeOrder
    public int keysAmount;
    public int keys[];
    public BPlusNode sons[];
    public boolean isLeaf;
    public BPlusNode daddy;
    public BPlusNode brother;       //---------------------------------------only for leaf nodes: leaf node in the right
    public BPlusNode sister;        //----------------------------------------only for leaf nodes: leaf node in the left

    public BPlusNode() {}

    public BPlusNode(int order, BPlusNode daddy) {
        this.order = order;
        this.daddy = daddy;

        keysAmount = 0;         //------------------------------------------------------------------new node has no keys
        sons = new BPlusNode[2*order +1];        //------------------------------------------in order to satisfy 50%-rule
        keys = new int[2*order];
        isLeaf = true;      //--------------------------------------------------------new node is created as a leaf node

    }


    public void setDaddy(BPlusNode daddy) {
        this.daddy = daddy;
    }

    public BPlusNode getDaddy() {
        return daddy;
    }

    public void setBrother(BPlusNode brother) {
        this.brother = brother;
    }

    public BPlusNode getBrother() {
        return brother;
    }

    public void setSister(BPlusNode sister) { this.sister = sister; }

    public BPlusNode getSister() {  return sister;  }

    public int getKeyOf(int index) {
        return keys[index];
    }

    public void setKey(int index, int value) {
        keys[index] = value;
        if (index > keysAmount - 1) {       //------------------------------------in order to keep keysAmount up to date
            keysAmount++;
        }
    }


    public BPlusNode getSonOf(int index) {
        return sons[index];
    }

    public void setSon(int index, BPlusNode node) {
        sons[index] = node;
    }

    public void setKeysAmount(int keysAmount) {
        this.keysAmount = keysAmount;
    }

    public int getKeysAmount() {
        return keysAmount;
    }

    public void setLeaf(boolean isIt) {
        this.isLeaf = isIt;
    }
}