public class BPlusTree {
    public int treeOrder;   //--------------------------------------------------------BPlusNode must have the same value
    BPlusNode root;
    BPlusNode soughtNode = null;
    public Entry operationEntry;
    public DoubleIntEntry nodeMergeEntry = null;
    public boolean display = false;

    public BPlusTree(int treeOrder) {
        this.treeOrder = treeOrder;
        root = new BPlusNode(treeOrder, null);
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean isDisplay() {
        return display;
    }

    public BPlusNode getSoughtNode() {
        return soughtNode;
    }

    /**
     * Climbs down the tree by key values till reaches leaf node and checks if the sought value is there
     * There was supposed to be an additional text here but no time left
     * @param node
     * @param soughtValue
     * @return
     */
    public void find(BPlusNode node, int soughtValue) {
        //HERE Climbing down the tree
        if (!node.isLeaf) {
            if (soughtValue < node.getKeyOf(0)) {      //-------------------------------------------extreme left pointer
                if (display) System.out.println("Searching in extreme left subtree...");
                find(node.getSonOf(0), soughtValue);
            } else if (soughtValue >= node.getKeyOf(node.keysAmount - 1)) {     //-------------------------extreme right
                if (display) System.out.println("Searching in extreme right subtree...");
                find(node.getSonOf(node.keysAmount), soughtValue);
            } else {        //-----------------------------------------------------------------------pointers in between
                if (display) System.out.print("Searching in subtree between ");
                for (int i = 0; i < node.keysAmount -1; i++) {
                    if (soughtValue >= node.getKeyOf(i) && soughtValue < node.getKeyOf(i + 1)) {
                        if (display) System.out.println(node.getKeyOf(i) + " and " + node.getKeyOf(i + 1));
                        find(node.getSonOf(i+1), soughtValue);
                    }
                }
            }
        }
        else {      //----------------------------------------------------------simple browsing through leaf node's keys
            this.soughtNode = null;
            for (int i = 0; i < node.keysAmount; i++) {
                if (soughtValue == node.getKeyOf(i)) {
                    this.soughtNode = node;
                    return;
                } else {
                    continue;
                }
            }
        }
    }

    public void printNode(BPlusNode node) {
        if (node.isLeaf) {
            System.out.print("#");      //----------------------------------------------------leaf nodes are hash-tagged
        }
        System.out.print("[");
        for (int i = 0; i < node.keysAmount; i++) {
            System.out.print( node.getKeyOf(i) + " ");
        }
        System.out.print("]");
        System.out.println("\t\tnode's keyAmount: " + node.keysAmount);

        if (!node.isLeaf) {
            for (int j = 0; j <= node.keysAmount; j++) {
                try {       //------------------------------------------------extreme left index nodes have one more son
                    printNode(node.getSonOf(j));
                } catch (NullPointerException e) {   continue;  }
            }

        } else {  return;  }
    }

    public void printJustNode(BPlusNode node) {
        if (node == null) {
            System.out.println("#[]");
            return;
        }
        if (node.isLeaf) {
            System.out.print("#");
        }
        System.out.print("[");
        for (int i = 0; i < node.keysAmount; i++) {
            System.out.print( node.getKeyOf(i) + " ");
        }
        System.out.print("]");
        System.out.println("\t\tnode's keyAmount: " + node.keysAmount);
        return;
    }

    /**
     * Insterting function
     * @param node
     * @param newKey
     * @param
     */
    public void insert(BPlusNode node, int newKey, Entry newChildEntry) {   //----------------------call for node = root
                                                        //^--------------------------newChildEntry should be new Entry()
        //HERE INDEX NODE
        if (!node.isLeaf) {     //--------------------------------------------------climb down till we reach a leaf node
            if (display) System.out.println("Handling Index Node...");
            if (newKey < node.getKeyOf(0)) {
                if (display) System.out.println("Climbing down the extreme left subtree...");
                insert(node.getSonOf(0), newKey, newChildEntry);
            } else if (newKey > node.getKeyOf(node.keysAmount - 1)) {
                if (display) System.out.println("Climbing down the extreme right subtree...");
                insert(node.getSonOf(node.keysAmount), newKey, newChildEntry);
            } else {
                for (int i = 0; i < node.keysAmount; i++) {
                    if (display) System.out.println("Climbing down some subtree in between...");
                    if (newKey > node.getKeyOf(i) && newKey <= node.getKeyOf(i + 1)) {
                        insert(node.getSonOf(i+1), newKey, newChildEntry);
                        break;
                    }
                }
            }
            newChildEntry = operationEntry;     //------------------------------so recursion works (needs outside value)
            if (newChildEntry == null) {     //-------------------------------------handles usual case without splitting
                if (display) System.out.println("Done without touching any Index Node.");
                return;
            } else {        //----------------------------splits son, must insert pointer to newChildEntry in index node
                if (node.getKeysAmount() < 2 * treeOrder) {  //-------------------usual case, there's room in index node
                    if (display) System.out.println("Simply adding entry to the Index Node...");
                    TwoInts tmpTwoInts = simpleInsertIndex(node, newChildEntry.getKey());
                    int j = tmpTwoInts.getKeyInt();
                    int m = tmpTwoInts.getSonInt();

                    node.setKey(j, newChildEntry.getKey());     //----------------adds new key and new son to index node
                    node.setSon(m, newChildEntry.getNode());
                    node.getSonOf(m).setDaddy(node);

                    operationEntry = null;
                    return;

                } else {        //------------------------------------------------------------------SPLITTING INDEX NODE
                    if (display) System.out.println("Splitting Index Node...");

                    //HERE Splitting: creating, coping, deleting
                    BPlusNode newNode = new BPlusNode(treeOrder, node.daddy);
                    newNode.setLeaf(false);

                    if (display) {
                        System.out.print("Index Node before splitting:\t");
                        printJustNode(node);
                    }
                    for (int i = 0; i < treeOrder; i++) {   //-----------------------copies entries from d+1 to the last
                        newNode.setKey(i, node.getKeyOf(i + treeOrder));
                        node.setKey(i + treeOrder, 0);      //-------------------------deletes previously copied entries
                    }

                    for (int i = 1; i <= treeOrder; i++) {   //----same for pointers from d +2 (lef nodes have +1 value)
                        newNode.setSon(i, node.getSonOf(i + treeOrder));
                        node.getSonOf(i + treeOrder).setDaddy(newNode);
                        node.setSon(i + treeOrder, null);
                    }

                    node.setKeysAmount(treeOrder);  //--------------------------number of keys has shrank after deletion
                    newNode.setKeysAmount(treeOrder);

                    //HERE Splitting: adding
                    TwoInts tmpTwoInts;
                    int j;
                    int m;

                    if (newChildEntry.getKey() < node.getKeyOf(node.keysAmount - 1)) {     //----key belongs to old node
                        tmpTwoInts = simpleInsertIndex(node, newChildEntry.getKey());
                        j = tmpTwoInts.getKeyInt();
                        m = tmpTwoInts.getSonInt();
                        node.setKey(j, newChildEntry.getKey());     //------------adds new key and new son to index node
                        node.setSon(m, newChildEntry.getNode());
                        node.getSonOf(m).setDaddy(node);
                        if (display) {
                            System.out.print("Index Node after splitting:\t");
                            printJustNode(node);
                        }

                    } else {        //---------------------------------------------------if it belongs to newNode's team
                        tmpTwoInts = simpleInsertIndex(newNode, newChildEntry.getKey());
                        j = tmpTwoInts.getKeyInt();
                        m = tmpTwoInts.getSonInt();
                        newNode.setKey(j, newChildEntry.getKey());     //---------adds new key and new son to index node
                        newNode.setSon(m, newChildEntry.getNode());
                        newNode.getSonOf(m).setDaddy(newNode);
                        if (display) {
                            System.out.print("Index Node before splitting:\t");
                            printJustNode(newNode);
                        }
                    }

                    Entry tmpEntry = new Entry();
                    tmpEntry.setNode(newNode);   //-----------newChildEntry = &(min. key in newNode, pointer to newNode)
                    tmpEntry.setKey(newNode.getKeyOf(0));

                    if (node.getDaddy() == null) {      //--------------------------------------if the node was the root
                        if (display) System.out.println("Creating new Root Node (above two Index Nodes)...");
                        BPlusNode newRoot = new BPlusNode(treeOrder, null);
                        newRoot.setLeaf(false);

                        newRoot.setKey(0, newNode.getKeyOf(0));     //--------------------------newRoot has only one key
                        newRoot.setSon(0, node);        //-------------------------------oldNode one the left of the key
                        newRoot.setSon(1, newNode);     //------------------------------------------newNode on the right

                        node.setDaddy(newRoot);
                        newNode.setDaddy(newRoot);

                        this.root = newRoot;        //------------------update new root, insert(node) is called for root
                        operationEntry = null;
                        return;
                    } else {
                        operationEntry = tmpEntry;
                        return;
                    }
                }
            }

        //HERE LEAF NODE
        } else {    //--------------------------------------------------------------------------- if node is a leaf node
            if (node.keysAmount < 2 * treeOrder) {  //-----------------------------usual case, there's room in leaf node
                if (display) System.out.println("Simply adding key the to Leaf Node...");
                int j = simpleInsertLeaf(node, newKey);
                node.setKey(j, newKey);     //-------------------------------------adds new key and new son to leaf node
                operationEntry = null;
                return;

            } else {    //---------------------------------------------------------------------------SPLITTING LEAF NODE
                if (display) System.out.println("Splitting Leaf Node...");

                //HERE Splitting: creating, coping, deleting
                BPlusNode newLeaf = new BPlusNode(treeOrder, node.daddy);

                if (display) {
                    System.out.print("Leaf Node before splitting:\t");
                    printJustNode(node);
                }
                for (int i = 0; i < treeOrder; i++) {   //---------------------------copies entries from d+1 to the last
                    newLeaf.setKey(i, node.getKeyOf(i + treeOrder));
                    node.setKey(i + treeOrder, 0);      //-----------------------------deletes previously copied entries
                }

                node.setKeysAmount(treeOrder);      //--------------------------number of keys has shrank after deletion
                newLeaf.setKeysAmount(treeOrder);

                //HERE Splitting: adding
                if (newKey < node.getKeyOf(node.keysAmount - 1)) {    //--------------------if it belongs to node's team
                    int j = simpleInsertLeaf(node, newKey);
                    node.setKey(j, newKey);
                    if (display) {
                        System.out.print("Leaf Node after splitting:\t");
                        printJustNode(node);
                    }
                }
                else {        //---------------------------------------------------------if it belongs to newLeaf's team
                    int j = simpleInsertLeaf(newLeaf, newKey);
                    newLeaf.setKey(j, newKey);
                    if (display) {
                        System.out.print("Leaf Node after splitting:\t");
                        printJustNode(newLeaf);
                    }
                }

                Entry tmpEntry = new Entry();
                tmpEntry.setNode(newLeaf);       //------------newChildEntry = &(min. key in newLeaf, pointer to newLef)
                tmpEntry.setKey(newLeaf.getKeyOf(0));

                operationEntry = tmpEntry;

                newLeaf.setBrother(node.getBrother());
                if (node.getBrother() != null) {
                    node.getBrother().setSister(newLeaf);
                }
                node.setBrother(newLeaf);
                newLeaf.setSister(node);

                if (node.getDaddy() == null) {      //------------------------------------------if the node was the root
                    if (display) System.out.println("Creating new Root Node (above two Leaves)...");
                    BPlusNode newRoot = new BPlusNode(treeOrder, null);
                    newRoot.setLeaf(false);

                    newRoot.setKey(0, newLeaf.getKeyOf(0));     //------------------------------newRoot has only one key
                    newRoot.setSon(0, node);        //-----------------------------------oldNode one the left of the key
                    newRoot.setSon(1, newLeaf);     //----------------------------------------------newNode on the right

                    node.setDaddy(newRoot);
                    newLeaf.setDaddy(newRoot);

                    this.root = newRoot;        //----------------------update new root, insert(node) is called for root
                    operationEntry = null;
                    return;
                } else {
                    return;
                }
            }
        }
    }

    public TwoInts simpleInsertIndex(BPlusNode node, int key) {
        TwoInts twoInts = new TwoInts();
        int j = node.keysAmount - 1;

        while (node.getKeyOf(j) > key && j > 0) {       //--------checks where to put a newKey & moves bigger keys right
            node.setKey(j + 1, node.getKeyOf(j));
            node.setSon(j + 2, node.getSonOf(j+1));
            node.setSon(j + 1, node.getSonOf(j));
            j--;
        }

        if (j == 0 && node.getKeyOf(0) > key) {           //-----------------------------newKey smaller than any element
            node.setKey(j + 1, node.getKeyOf(j));       //---------now we have to move first element and its sons (two!)
            node.setSon(j + 2, node.getSonOf(j+1));
            node.setSon(j + 1, node.getSonOf(j));
            twoInts.setKeyInt(j);
            twoInts.setSonInt(j+1);         //---------j+1 cause it's an index node, j-son belongs to another index node
            return twoInts;
        } else {
            twoInts.setKeyInt(j + 1);
            twoInts.setSonInt(j + 2);
            return twoInts;
        }
    }

    public int simpleInsertLeaf(BPlusNode node, int key) {
        if (node.keysAmount == 0) {     //---------------------------------------------------------if it's an empty leaf
            return 0;
        }
        else {
            int j = node.keysAmount - 1;

            while (node.getKeyOf(j) > key && j > 0) {       //----checks where to put a newKey & moves bigger keys right
                node.setKey(j + 1, node.getKeyOf(j));
                j--;
            }

            if (j == 0 && node.getKeyOf(0) > key) {           //-------------------------newKey smaller than any element
                node.setKey(j + 1, node.getKeyOf(j));       //-------------------------now we have to move first element
                return j;
            } else {
                return j + 1;
            }
        }
    }
/*--------------------------------------------------------------------------------------------------------------------*/
/*---------------------------------------------------END OF INSERTING-------------------------------------------------*/
/*--------------------------------------------------------------------------------------------------------------------*/

    public void delete(BPlusNode node, int oldKey, Entry oldChildEntry) {
        //HERE INDEX NODE
        if (!node.isLeaf) {     //--------------------------------------------------climb down till we reach a leaf node
            if (display) System.out.println("Handling Index Node...");
            if (oldKey < node.getKeyOf(0)) {
                if (display) System.out.println("Climbing down the extreme left subtree...");
                delete(node.getSonOf(0),oldKey, oldChildEntry);
            } else if (oldKey >= node.getKeyOf(node.keysAmount - 1)) {
                if (display) System.out.println("Climbing down the extreme right subtree...");
                delete(node.getSonOf(node.keysAmount), oldKey, oldChildEntry);
            } else {
                for (int i = 0; i < node.keysAmount; i++) {
                    if (display) System.out.print("Climbing down some subtree in between ");
                    if (oldKey >= node.getKeyOf(i) && oldKey < node.getKeyOf(i + 1)) {
                        if (display) System.out.println(node.getKeyOf(i) + " and " + node.getKeyOf(i + 1));
                        delete(node.getSonOf(i+1), oldKey, oldChildEntry);
                        break;
                    }
                    if (display) System.out.println();
                }
            }
            oldChildEntry = operationEntry;     //------------------------------so recursion works (needs outside value)
            if (oldChildEntry == null) {     //----------------------------occurs when simple deletion or redistribution
                if (display) System.out.println("Done without redistributing or merging any Index Node.");
                return;
            } else {        //-----------------------------------------------------------------------occurs when merging
                if (node.getKeysAmount() > treeOrder) {     //-------------------------simple deletion for an index node
                    if (display) System.out.println("Simply deleting entry from the Index Node...");
                    int j = simpleDeleteIndex(node, oldChildEntry.getKey());
                    node.setSon(j, oldChildEntry.getNode());
                    nodeMergeEntry = null;
                    operationEntry = null;
                    return;
                }
                else {      //---------------------------------------------------------------------and the fun begins...
                    BPlusNode tmpBro = null, tmpSis = null, tmpDad = node.getDaddy();

                    if (tmpDad == null) {
                        if (node.getKeysAmount() == 1) {
                            if(display) System.out.println("Height of the tree decreased by 1.");
                            this.root = oldChildEntry.getNode();
                            root.setDaddy(null);
                            operationEntry = null;
                            return;
                        }
                        else {
                            if (display) System.out.println("Simply deleting entry from the Index-Root Node (undersized)...");

                            int j = simpleDeleteIndex(node, oldChildEntry.getKey());
                            node.setSon(j, oldChildEntry.getNode());
                            operationEntry = null;
                            return;
                        }
                    }
                    else { //FIXME
                        int j = tmpDad.keysAmount - 1;
                        while (tmpDad.getKeyOf(j) > node.getKeyOf(0) && j > 0) {       //---look for itself in daddy
                            j--;
                        }

                        if (j == 0 && tmpDad.getKeyOf(j) > node.getKeyOf(0)) {
                            tmpBro = node.getDaddy().getSonOf(j + 1);
                        } else if (j == 0) {
                            tmpSis = node.getDaddy().getSonOf(j);
                            if (tmpDad.keysAmount >= 2) {
                                tmpBro = node.getDaddy().getSonOf(j + 2);
                            }
                        } else if (j == tmpDad.keysAmount - 1) {       //------------------------node has no "sister" and its position is 1!
                            tmpSis = node.getDaddy().getSonOf(j);
                            if (tmpDad.keysAmount > 2) {
                                tmpBro = node.getDaddy().getSonOf(j + 2);
                            }
                        } else {
                            tmpSis = node.getDaddy().getSonOf(j);
                            tmpBro = node.getDaddy().getSonOf(j + 2);
                        }
                    }

                    //HERE Redistribution
                    if (tmpBro != null) {       //----------------------------------------------------leaf has a brother
                        if (tmpBro.getKeysAmount() > treeOrder) {
                            if(display) System.out.println("Starting redistribution with Brother-Index Node...");
                            int tmpKey, steeringInt;
                            BPlusNode tmpSon;
                            TwoInts tmpTwoInts;

                            do {
                                tmpKey = tmpBro.getKeyOf(0);
                                tmpSon = tmpBro.getSonOf(1);
                                simpleDeleteIndex(tmpBro, tmpBro.getKeyOf(0));
                                tmpTwoInts = simpleInsertIndex(node, tmpKey);

                                node.setKey(tmpTwoInts.getKeyInt(), tmpKey);
                                node.setSon(tmpTwoInts.getSonInt(), tmpSon);

                                steeringInt = (tmpBro.getKeysAmount() - treeOrder) / 2;     //-------even redistribution
                            } while (steeringInt > 0);

                            int k = simpleDeleteIndex(node, oldChildEntry.getKey());
                            node.setSon(k, oldChildEntry.getNode());
                            operationEntry = null;
                            return;
                        }
                    }
                    if (tmpSis != null) {       //----------------------------------------------------leaf has a brother
                        if (tmpSis.getKeysAmount() > treeOrder) {
                            if(display) System.out.println("Starting redistribution with Sister-Index Node...");
                            int tmpKey, steeringInt;
                            BPlusNode tmpSon;
                            TwoInts tmpTwoInts;

                            do {
                                tmpKey = tmpSis.getKeyOf(tmpSis.getKeysAmount() - 1);
                                tmpSon = tmpSis.getSonOf(tmpSis.getKeysAmount());
                                simpleDeleteIndex(tmpSis, tmpSis.getKeyOf(tmpSis.getKeysAmount() - 1));
                                tmpTwoInts = simpleInsertIndex(node, tmpKey);

                                node.setKey(tmpTwoInts.getKeyInt(), tmpKey);
                                node.setSon(tmpTwoInts.getSonInt(), tmpSon);

                                steeringInt = (tmpSis.getKeysAmount() - treeOrder) / 2;     //-------even redistribution
                            } while (steeringInt > 0);

                            simpleDeleteIndex(node, oldChildEntry.getKey());
                            operationEntry = null;
                            return;
                        }
                    }

                    //HERE Merging
                    if(tmpSis != null) {
                        if (tmpSis.getKeysAmount() == treeOrder) {
                            if(display) System.out.println("Starting merging with Sister-Index Node...");
                            TwoInts tmpTwoTwins;

                            Entry tmpEntry = new Entry();
                            tmpEntry.setNode(tmpSis);       //------------------oldChildEntry = &(current entry in parent)
                            tmpEntry.setKey(node.getKeyOf(0));
                            operationEntry = tmpEntry;

                            for (int i = 0; i < node.keysAmount; i++) {     //--moves all elements but oldKey to the Sis
                                if (node.getKeyOf(i) != oldKey) {
                                    tmpTwoTwins = simpleInsertIndex(tmpSis, node.getKeyOf(i));
                                    tmpSis.setKey(tmpTwoTwins.getKeyInt(), node.getKeyOf(i));
                                    tmpSis.setSon(tmpTwoTwins.getSonInt(), node.getSonOf(i + 1));
                                    node.getSonOf(i + 1).setDaddy(tmpSis);
                                    node.setKey(i, 0);
                                    node.setSon(i + 1, null);
                                }
                            }

                            node.setKeysAmount(0);
                            return;
                        }
                    }

                    if(tmpBro != null) {
                        if (tmpBro.getKeysAmount() == treeOrder) {
                            if(display) System.out.println("Starting merging with Brother-Index Node...");
                            TwoInts tmpTwoTwins;

                            Entry tmpEntry = new Entry();
                            tmpEntry.setNode(tmpBro);       //------------------oldChildEntry = &(current entry in parent)
                            if (oldChildEntry.getKey() < node.getDaddy().getKeyOf(0)) {
                                tmpEntry.setKey(tmpBro.getKeyOf(0));
                            } else {
                                tmpEntry.setKey(node.getKeyOf(0));
                            }
                            operationEntry = tmpEntry;

                            for (int i = 0; i < node.keysAmount; i++) {     //--moves all elements but oldKey to the Sis
                                if (node.getKeyOf(i) != oldKey) {
                                    tmpTwoTwins = simpleInsertIndex(tmpBro, node.getKeyOf(i));
                                    tmpBro.setKey(tmpTwoTwins.getKeyInt(), node.getKeyOf(i));
                                    tmpBro.setSon(tmpTwoTwins.getSonInt(), node.getSonOf(i + 1));
                                    node.getSonOf(i + 1).setDaddy(tmpBro);
                                    node.setKey(i, 0);
                                    node.setSon(i + 1, null);
                                }
                            }

                            node.setKeysAmount(0);
                            return;
                        }
                    }
                }
            }
            System.out.println("Non of index conditions met - system exit");
            System.exit(0);
        }
        //HERE LEAF NODE
        else {
            if (node.getDaddy() == null) {      //-----------------------------------------leaf node is also a root node
                if (display) System.out.println("Deleting key form the Leaf-Root Node (will be undersized)...");
                simpleDeleteLeaf(node, oldKey);
                operationEntry = null;
                return;
            }

            else if (node.keysAmount > treeOrder) {  //---------------------usual case, leaf node is more than half-full
                if (display) System.out.println("Simply deleting key form the Leaf Node...");
                simpleDeleteLeaf(node, oldKey);
                operationEntry = null;
                return;
            }

            else {      //------------------------------------------------------------------------leaf node is half-full
                BPlusNode tmpBro = node.getBrother();
                BPlusNode tmpSis = node.getSister();

                //HERE Redistribution
                if (tmpBro != null) {       //--------------------------------------------------------leaf has a brother
                    if (tmpBro.getKeysAmount() > treeOrder) {
                        if(display) System.out.println("Starting redistribution with Brother-Leaf Node...");
                        int tmpKey, steeringInt, place;

                        do {
                            tmpKey = tmpBro.getKeyOf(0);
                            simpleDeleteLeaf(tmpBro, tmpBro.getKeyOf(0));  //----------------------------simple deletion
                            place = simpleInsertLeaf(node, tmpKey);     //-----------should assign place=node.keysAmount
                            node.setKey(place, tmpKey);

                            steeringInt = (tmpBro.getKeysAmount() - treeOrder) / 2;     //-----------even redistribution
                        } while (steeringInt > 0);

                        simpleDeleteLeaf(node, oldKey);        //---------------------------now it'll be simple deletion
                        operationEntry = null;
                        return;
                    }
                }
                if (tmpSis != null) {       //--------------------------------------------------------leaf has a brother
                    if (tmpSis.getKeysAmount() > treeOrder) {
                        if(display) System.out.println("Starting redistribution with Sister-Leaf Node...");
                        int tmpKey, steeringInt, place, tmpEntry = node.getKeyOf(0);

                        do {
                            tmpKey = tmpSis.getKeyOf(tmpSis.getKeysAmount() - 1);
                            simpleDeleteLeaf(tmpSis, tmpSis.getKeyOf(tmpSis.getKeysAmount() - 1));  //-------------
                            place = simpleInsertLeaf(node, tmpKey);     //------------should assign place=0 'n move keys
                            node.setKey(place, tmpKey);

                            updateEntry(node, node.getKeyOf(0), tmpEntry, node.getKeyOf(0));

                            steeringInt = (tmpSis.getKeysAmount() - treeOrder) / 2;     //-----------even redistribution
                        } while (steeringInt > 0);

                        simpleDeleteLeaf(node, oldKey);        //---------------------------now it'll be simple deletion
                        operationEntry = null;
                        return;
                    }
                }
                //HERE Merging
                if(tmpSis != null /*&& node.getDaddy().getKeyOf(0) != oldKey*/) {
                    if (tmpSis.getKeysAmount() == treeOrder) {
                        if(display) System.out.println("Starting merging with Sister-Leaf Node...");

                        int place;
                        Entry tmpEntry = new Entry();
                        tmpEntry.setNode(tmpSis);       //--------------------oldChildEntry = &(current entry in parent)
                        tmpEntry.setKey(node.getKeyOf(0));
                        operationEntry = tmpEntry;

                        DoubleIntEntry tmpDoubleEntry = new DoubleIntEntry();
                        tmpDoubleEntry.setNode(tmpSis);
                        tmpDoubleEntry.setOldKey(node.getKeyOf(0));   //--------------old key(0), update after merging

                        for (int i = 0; i < node.keysAmount; i++) {     //-moves all elements but oldKey to the Sis node
                            if (node.getKeyOf(i) != oldKey) {
                                place = simpleInsertLeaf(tmpSis, node.getKeyOf(i));
                                tmpSis.setKey(place, node.getKeyOf(i));
                                node.setKey(i, 0);
                            }
                        }

                        tmpDoubleEntry.setNewKey(tmpSis.getKeyOf(0));
                        node.setKeysAmount(0);

                        if (tmpBro != null) {
                            tmpSis.setBrother(tmpBro);
                            tmpBro.setSister(tmpSis);
                        } else {
                            tmpSis.setBrother(null);
                            node = tmpSis;
                        }
                        return;
                    }
                }
                if(tmpBro != null /*&& node.getDaddy().getKeyOf(node.getDaddy().getKeysAmount() - 1) != oldKey*/) {
                    if (tmpBro.getKeysAmount() == treeOrder) {
                        if(display) System.out.println("Starting merging with Brother-Leaf Node...");

                        int place;
                        Entry tmpEntry = new Entry();
                        tmpEntry.setNode(tmpBro);       //--------------------oldChildEntry = &(current entry in parent)
                        if (oldKey < node.getDaddy().getKeyOf(0)) {
                            tmpEntry.setKey(tmpBro.getKeyOf(0));
                        } else {
                            tmpEntry.setKey(node.getKeyOf(0));    //tu pozamienialem
                        }
                        operationEntry = tmpEntry;

                        DoubleIntEntry tmpDoubleEntry = new DoubleIntEntry();
                        tmpDoubleEntry.setNode(tmpBro);
                        tmpDoubleEntry.setOldKey(tmpBro.getKeyOf(0));   //--------------old key(0), update after merging

                        for (int i = 0; i < node.keysAmount; i++) {     //-moves all elements but oldKey to the Sis node
                            if (node.getKeyOf(i) != oldKey) {
                                place = simpleInsertLeaf(tmpBro, node.getKeyOf(i));
                                tmpBro.setKey(place, node.getKeyOf(i));
                                node.setKey(i, 0);
                            }
                        }

                        tmpDoubleEntry.setNewKey(tmpBro.getKeyOf(0));
                        node.setKeysAmount(0);          //------------------------------new key(0), update after merging

                        if (tmpSis != null) {
                            tmpSis.setBrother(tmpBro);
                            tmpBro.setSister(tmpSis);
                        } else {
                            tmpBro.setSister(null);

                        }
                        return;
                    }
                }
                System.out.println("Non of leaf conditions met - system exit");
                System.exit(0);

            }
        }
    }

    public int simpleDeleteLeaf(BPlusNode node, int key) {
        int j = node.keysAmount - 1;
        int tmpNewEntry, tmpOldEntry = node.getKeyOf(0);
        while (node.getKeyOf(j) > key && j > 0) {       //------------------------checks where to start moving keys left
            j--;
        }
        for (int i = j; i <= node.keysAmount - 1; i++) {         //---------------moves keys left overriding the old key
            if (i == node.keysAmount - 1) {
                node.setKey(i, 0);
            }
            else{
                node.setKey(i, node.getKeyOf(i + 1));
            }
        }

        node.setKeysAmount(node.keysAmount - 1);
        tmpNewEntry = node.getKeyOf(0);

        if (node.getDaddy() == null) {
            return j;
        }
        else {
            if (j != 0) {       //------------------------------just deleted value wasn't the first of leaf node's daddy
                return j;
            } else {
                updateEntry(node, key, tmpOldEntry, tmpNewEntry);
                return j;
            }
        }
    }

    public int simpleDeleteIndex(BPlusNode node, int key) {
        int j = node.keysAmount - 1;
        int tmpNewEntry, tmpOldEntry = node.getKeyOf(0);

        while (node.getKeyOf(j) > key && j > 0) {       //------------------------checks where to start moving keys left
            j--;
        }
        for (int i = j; i <= node.keysAmount - 1; i++) {

            if (i == node.keysAmount - 1) {
                node.setKey(i, 0);
                node.setSon(i + 1, null);

            } else {
                node.setKey(i, node.getKeyOf(i + 1));           //----------------moves keys left overriding the old key
                node.setSon(i+1, node.getSonOf(i + 2));           //--------------same for sons, we have to delete 0-son

            }
        }
        node.setKeysAmount(node.keysAmount - 1);
        tmpNewEntry = node.getKeyOf(0);
        updateEntry(node, key, tmpOldEntry, tmpNewEntry);

        if (j == 0 && key < node.getKeyOf(0)) {
            return j;
        } else {
            return j + 1;
        }
    }

    public int simpleUpdateEntry(BPlusNode node, int tmpOldEntry, int tmpNewEntry) {
        int jj = node.getDaddy().keysAmount - 1;

        while (node.getDaddy().getKeyOf(jj) > tmpOldEntry && jj > 0) {       //----------searches old key to be replaced
            jj--;
        }
        node.getDaddy().setKey(jj, tmpNewEntry);        //----------substitutes oldKey with newKey at the right position
        return jj;
    }

    public void updateEntry(BPlusNode node, int key, int tmpOldEntry, int tmpNewEntry) {
        int jj = 0;

        while (jj == 0 && node.getDaddy() != null /*&& node.getDaddy().getKeyOf(0) >= tmpOldEntry*/) {

            if (node.getDaddy() == null && node.getKeysAmount() == 1 && node.getKeyOf(0) == tmpOldEntry) {
                if (display) System.out.println("Have to substitute key entry in Parent Node ...");
                node.setKey(0, tmpNewEntry);
            }

            if (key != tmpNewEntry) {

                if (node.getDaddy() == null && node.getKeyOf(0) > key) {    //----------------------node is the root
                    return;
                }
                else if ( node.getDaddy().getKeyOf(0) > key) {
                    return;
                }

                if (display) System.out.println("Have to substitute key entry in Parent Node ...UP");
                jj = simpleUpdateEntry(node, tmpOldEntry, tmpNewEntry);       //----------ala old key vanish from the tree
                node = node.getDaddy();
            }
            else {
                if (node.getDaddy() == null && node.getKeyOf(0) > key) {    //----------------------node is the root
                    return;
                }
                if (display) System.out.println("Have to substitute key entry in Parent Node ...DOWN");
                jj = simpleUpdateEntry(node, tmpOldEntry, tmpNewEntry);       //----------ala old key vanish from the tree
                node = node.getDaddy();
            }

        }

        return;
    }




/*--------------------------------------------------------------------------------------------------------------------*/
/*---------------------------------------------------END OF DELETING--------------------------------------------------*/
/*--------------------------------------------------------------------------------------------------------------------*/

}


