package bdd;

import java.lang.ref.WeakReference;
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
     System.out.println(key.toString());
     cache.put(key, new WeakReference<V>(val));
     return val;
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
