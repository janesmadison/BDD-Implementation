package bdd;

import org.junit.jupiter.api.Test;

import bddImpl.bdd.BDDManager;
import bddImpl.bdd.BDDManager.BDDNode;
import bddImpl.vbdd.VBDDManager;
import bddImpl.vbdd.VBDDManager.VBDD;

public class BDDTest {


    @Test
    public void testBDD() {

    BDDManager tree = new BDDManager();
    BDDNode x =  tree.addVariable("x");
    BDDNode y =  tree.addVariable("y");
    BDDNode z = tree.addVariable("z");
    tree.printDot(x.and(y).or(z).not());
    tree.printDot(x.not());
    }
    
    @Test
    public void testVBDD() {

    VBDDManager tree = new VBDDManager();
    VBDD<Integer> x =  tree.choice("x", 1, 0);
    VBDD<Integer> y =  tree.choice("y", 2, 0);
    
    //assertEquals(x.flatMap(a -> y.map(b -> a + b)), tree.choice("x", tree.choice("y", 3, 1), tree.choice("y", 2, 0)));   
    tree.printDot(tree.choice("x", tree.choice("y", 3, 1), tree.choice("y", 2, 0)));
    y.map(p -> p + 1);
    }

}