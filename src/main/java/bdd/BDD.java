package bdd;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class BDD
{

private WeakHashMap<BDDNode, WeakReference<BDDNode>> bddMap = new WeakHashMap<>();

//constructor
  public BDD() {
  }
  
 public BDDNode addVariable(String v) {
	 BDDNode falseLeaf = new BDDNode("false", null, null);
	 BDDNode trueLeaf = new BDDNode("true", null, null);
	 BDDNode n = new BDDNode(v, falseLeaf, trueLeaf);
	 n = this.mk(v, n.low, n.high);
	 return n;
 }
 
 private BDDNode mk(String v, BDDNode low, BDDNode high) {
	 if(low.isEquivalent(high)) {
		 System.out.println(low + "is a repeat");
		 return low;
	 } 
	BDDNode newNode = new BDDNode(v, low, high);
	return lookupCache(bddMap, newNode, () -> newNode);
 }
 
 //Written by Christian Kästner
 private <K, V> V lookupCache(WeakHashMap<K, WeakReference<V>> cache, K key, Supplier<V> newValue) {
     WeakReference<V> v = cache.get(key);
     V val = null;
     if (v != null) {
         val = v.get();
     }
     if (val != null)
         return val;

     val = newValue.get();
     cache.put(key, new WeakReference<V>(val));
     return val;
 }
 
//Written by Christian Kästner (modified)
//Can be used in conjunction with http://www.webgraphviz.com/ to print BDD
 public void printDot(BDDNode bdd) {


     System.out.println("digraph G {");
     System.out.println("0 [shape=box, label=\"FALSE\", style=filled, shape=box, height=0.3, width=0.3];");
     System.out.println("1 [shape=box, label=\"TRUE\", style=filled, shape=box, height=0.3, width=0.3];");
     Set<BDDNode> seen = new HashSet<>();
     LinkedList<BDDNode> queue = new LinkedList<>();
     queue.add(bdd);

     while (!queue.isEmpty()) {
         BDDNode b = queue.remove();
         if (!(seen.contains(b)) && (!b.isLeafNode())) {
             seen.add(b);
             System.out.println(b.getID() + " [label=\"" + b.v + "\"];");
             System.out.println(b.getID() + " -> " + b.low.getID() + " [style=dotted];");
             System.out.println(b.getID() + " -> " + b.high.getID() + " [style=filled];");
             queue.add(b.low);
             queue.add(b.high);
         }

     }

     System.out.println("}");
 }
 
 
//Boolean Functions
Boolean and(Boolean x1, Boolean x2) {
  return x1 && x2;
}
Boolean or(Boolean x1, Boolean x2) {
  return x1 || x2;
}
Boolean xor(Boolean x1, Boolean x2) {
  return (x1 && !x2) || (!x1 && x2);
}

}
