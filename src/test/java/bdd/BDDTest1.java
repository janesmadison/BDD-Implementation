package bdd;

import org.junit.jupiter.api.Test;

public class BDDTest1 {


    @Test
    public void test() {

    BDD tree = new BDD();
    BDDNode x =  tree.addVariable("x");
    BDDNode x2 =  tree.addVariable("y");
    BDDNode z =  tree.addVariable("z");
    
    tree.printDot(x);
    }

}