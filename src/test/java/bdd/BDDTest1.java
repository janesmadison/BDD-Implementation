package bdd;

import org.junit.jupiter.api.Test;

import bdd.BDDManager.BDDNode;

public class BDDTest1 {


    @Test
    public void test() {

    BDDManager tree = new BDDManager();
    BDDNode x =  tree.addVariable("x");
    BDDNode y =  tree.addVariable("y");
    tree.printDot(x.and(y));
    }

}