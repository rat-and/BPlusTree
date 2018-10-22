import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void add(Scanner input, BPlusTree tree) {
        int num, tmp;
        BPlusNode soughtNode;

        System.out.println("How many values (keys) do you want to enter?:");
        try {       //-------------------------------------------------------------------------in case of invalid format
            num = input.nextInt();
        } catch (InputMismatchException e) {
            num = 0;       //---------------------------------------------just to enter while loop when exception occurs
        }
        while (num < 1) {
            System.out.println("Number of values (keys) must be an integer greater or equal to 1. Try again.");
            input.nextLine();

            try {
                num = input.nextInt();
            } catch (InputMismatchException e) {
                continue;
            }
        }

        for (int i = 0; i < num; i++) {

            System.out.print("\nEnter value (key)");
            System.out.println(i + 1 + ":\t");

            try {       //---------------------------------------------------------------------in case of invalid format
                tmp = input.nextInt();
            } catch (InputMismatchException e) {
                tmp = 0;       //-----------------------------------------just to enter while loop when exception occurs
            }
            while (tmp < 1) {
                System.out.println("Supported values (keys) are integers greater or equal to 1. Try again.");
                input.nextLine();

                try {
                    tmp = input.nextInt();
                } catch (InputMismatchException e) {
                    continue;
                }
            }
            tree.find(tree.root, tmp);
            soughtNode = tree.getSoughtNode();

            if (soughtNode == null) {        //--------------------------------checks if value has been already inserted
                tree.insert(tree.root, tmp, new Entry());
            } else {
                System.out.println("Implementation supports only pairwise different values (keys). Value skipped.");
                continue;
            }
        }
    }

    public static void main(String[] args) {

        Scanner input = new Scanner( System.in );
        int ord;
        int choice;          //-------------------------------------------------------------------control menu variables
        boolean halt = true;
        System.out.println("Welcome. Program 'BPlusTree' is still under test...");
        System.out.println("Enter the order 'd' of the Tree - nodes will store form d to 2d values (floor):");

        try {       //-------------------------------------------------------------------------in case of invalid format
            ord = input.nextInt();
        } catch (InputMismatchException e) {
            ord = 0;       //---------------------------------------------just to enter while loop when exception occurs
        }
        while (ord < 2) {
            System.out.println("Order must be an integer greater than 1. Try again.");
            input.nextLine();

            try {
                ord = input.nextInt();
            } catch (InputMismatchException e) {
                continue;
            }

        }
        BPlusTree tree = new BPlusTree(ord);
        add(input, tree);

        while (halt) {      //---------------------------------------------------------break only when stopped by a user

            System.out.println("\n\tMENU. Insert one of the following numbers");
            System.out.println("1 to enter more values in a tree");
            System.out.println("2 to print the whole tree in preorder (I guess...)");
            System.out.println("3 to search for a Key and print the Node it belongs to");
            System.out.println("4 to draw random key values and insert them");
            System.out.println("5 to display operation comments");
            System.out.println("6 to delete a key (it's being tested!)");
            System.out.println("7 to exit");

            try {       //---------------------------------------------------------------------in case of invalid format
                choice = input.nextInt();
            } catch (InputMismatchException e) {
                choice = 0;       //--------------------------------------just to enter while loop when exception occurs
            }
            while (choice < 1 || choice > 7) {
                System.out.println("Choose one from given values. Try again.");
                input.nextLine();
                try {
                    choice = input.nextInt();
                } catch (InputMismatchException e) {
                    continue;
                }
            }

            if ( choice == 7) {
                System.out.printf("See you!");
                halt = false;
                System.exit(0);
            }

            else
            {
                switch(choice)
                {
                    case 1:
                        add(input, tree);
                        break;

                    case 2:
                        tree.printNode(tree.root);
                        System.out.println();
                        break;

                    case 3:
                        System.out.println("Value of sought key: ");
                        int key2;

                        try {       //---------------------------------------------------------in case of invalid format
                            key2 = input.nextInt();
                        } catch (InputMismatchException e) {
                            key2 = 0;       //----------------------------just to enter while loop when exception occurs
                        }
                        while (key2 < 1) {
                            System.out.println("Supported values (keys) are integers greater or equal to 1. Try again.");
                            input.nextLine();

                            try {
                                key2 = input.nextInt();
                            } catch (InputMismatchException ex) {
                                continue;
                            }
                        }

                        tree.find(tree.root, key2);
                        BPlusNode soughtNode = tree.soughtNode;
                        if (soughtNode == null) {
                            System.out.println("There's no such key value in the tree");
                        } else {
                            System.out.println("Leaf Node for the sought key");
                            tree.printJustNode(soughtNode);
                            System.out.println("Its brother:");
                            tree.printJustNode(soughtNode.getBrother());
                            System.out.println("Its sister:");
                            tree.printJustNode(soughtNode.getSister());
                            System.out.println("Its daddy:");
                            tree.printJustNode(soughtNode.getDaddy());
                        }
                        break;

                    case 4:
                        System.out.println("How many random values ranging from 1 to 100 you want to input: ");
                        int randoms, tmp, nonUnique = 0;


                        try {       //---------------------------------------------------------in case of invalid format
                            randoms = input.nextInt();
                        } catch (InputMismatchException e) {
                            randoms = 0;       //-------------------------just to enter while loop when exception occurs
                        }
                        while (randoms < 1) {
                            System.out.println("Come on... Try again.");
                            input.nextLine();

                            try {
                                randoms = input.nextInt();
                            } catch (InputMismatchException ex) {
                                continue;
                            }
                        }

                        Random randomGen = new Random();
                        for (int i = 0; i < randoms; i++) {
                            tmp = randomGen.nextInt(100) + 1;
                            tree.find(tree.root, tmp);
                            soughtNode = tree.soughtNode;

                            if (soughtNode == null) {        //----------------checks if value has been already inserted
                                tree.insert(tree.root, tmp, new Entry());
                            } else {
                                nonUnique += 1;
                                continue;
                            }
                        }
                        System.out.println();
                        System.out.println(nonUnique + " values repeated, " + (randoms - nonUnique) + " added.");
                        break;

                    case 5:
                        if (tree.isDisplay()) {
                            tree.setDisplay(false);
                        } else {
                            tree.setDisplay(true);
                        }
                        System.out.println("Comments display set to: " + tree.isDisplay());
                        break;

                    case 6:
                        System.out.println("Enter value (key) you want to delete:");

                        try {       //---------------------------------------------------------------------in case of invalid format
                            tmp = input.nextInt();
                        } catch (InputMismatchException e) {
                            tmp = 0;       //-----------------------------------------just to enter while loop when exception occurs
                        }
                        while (tmp < 1) {
                            System.out.println("Supported values (keys) that are stored are integers equal to or greater than 1. Try again.");
                            input.nextLine();

                            try {
                                tmp = input.nextInt();
                            } catch (InputMismatchException e) {
                                continue;
                            }
                        }
                        tree.find(tree.root, tmp);
                        soughtNode = tree.getSoughtNode();

                        if (soughtNode != null) {        //--------------------------------checks if value has been already inserted
                            tree.delete(tree.root, tmp, new Entry());
                        } else {
                            System.out.println("Couldn't find inserted value. Nothing to be deleted.");
                            continue;
                        }
                }
            }
        }
        System.exit(0);

    }
}
