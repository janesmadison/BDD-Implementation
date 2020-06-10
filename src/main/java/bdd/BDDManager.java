package bdd;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class BDDManager
{

private WeakHashMap<BDDNode, WeakReference<BDDNode>> bddMap = new WeakHashMap<>();
private WeakHashMap<BDDPair,WeakReference<BDDNode>> andCache = new WeakHashMap<>();
private HashMap<BDDPair,BDDNode> pairCache = new HashMap<>();

private BDDNode TRUE = new BDDNode("true", null, null) {

    @Override
    public Boolean isLeafNode() {
        return true;
    }
};

private BDDNode FALSE = new BDDNode("false", null, null) {

    @Override
    public Boolean isLeafNode() {
        return true;
    }
};

//constructor
  public BDDManager() {
  }
  
 public BDDNode addVariable(String v) {
	 return mk(v, FALSE, TRUE);
 }
 
 private BDDNode mk(String v, BDDNode low, BDDNode high) {
	 if(low.isEquivalent(high)) {
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
 
 BDDNode applyAnd(BDDNode one, BDDNode two) {
	 
	 	 BDDPair pair = new BDDPair(one, two);
	     BDDNode found = pairCache.get(pair);
	     if (found != null)
	         return found;
	     BDDNode newNode;
	     if (one.isLeafNode() && two.isLeafNode())
	         newNode = logicAnd(one.isEquivalent(TRUE), two.isEquivalent(TRUE)) ? TRUE : FALSE;
	     else if (one.v.equals(two.v))
	         newNode = mk(one.v, applyAnd(one.low, two.low), applyAnd(one.high, two.high));
	     else if (two.isLeafNode() || (one.v.compareTo(two.v) > 0))
	         newNode = mk(one.v, applyAnd(one.low, two), applyAnd(one.high, two));
	     else
	         newNode = mk(one.v, applyAnd(one, two.low), applyAnd(one, two.high));

	     pairCache.put(pair, newNode);
	     return newNode;
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
 
 private Boolean logicAnd(Boolean one, Boolean two) {
	return one && two;
 }
 
 //=============================================================================================================================================
 public class BDDNode
 {
   BDDNode high;
   BDDNode low;
   String v; //variable name ex. x1
   int hash;

 //constructor
 BDDNode(String v, BDDNode low, BDDNode high){
     this.high = high;
     this.low =low;
     this.v = v;
     System.out.println("node constructed with:  " + v);
     this.hash = v.hashCode() + 31 * (low == null ? 0 : low.hash) + 27 * (high == null ? 0 : high.hash);
 }

 Boolean isEquivalent(BDDNode other) {
 	  if((this.high == other.high && this.low == other.low && this.v.equals(other.v)) || this == other) {
 		return true;  
 	  }
 	  return false;
 	}

 Boolean isLeafNode(){
     return false;   
 }

  int getID() {
     if (this.isEquivalent(TRUE)) return 1;
     else if (this.isEquivalent(FALSE)) return 0;
     else return Math.abs(this.hash);
 }
  
  public BDDNode and(BDDNode other) {
	  return lookupCache(andCache, new BDDPair(this, other), () -> applyAnd(this,other));
  }

 }
//=======================================================================================================================================
 
 private class BDDPair {
	    private final BDDNode low, high;

	    BDDPair(BDDNode low, BDDNode high) {
	        this.low = low;
	        this.high = high;
	    }

	    public int hashCode() {
	        return low.hash + high.hash;
	    }

	    public boolean equals(Object t) {
	        if (t instanceof BDDPair) {
	            BDDPair that = (BDDPair) t;
	            return this.high.isEquivalent(that.high) && this.low.isEquivalent(that.low);
	        }
	        return false;
	    }
	}
 
 //===========================================================================================================================================
}
