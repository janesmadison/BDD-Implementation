package bdd;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BDDManager
{

private WeakHashMap<BDDInternal, WeakReference<BDDInternal>> internalMap = new WeakHashMap<>();
private WeakHashMap<BDDTerminal, WeakReference<BDDTerminal>> terminalMap = new WeakHashMap(); //from terminal to terminal 


//constructor
  public BDDManager() {
  }
  
  public <T> BDDInternal<T> choice(String cond, T left, T right) {
	  return mk(cond, mkTerminal(left), mkTerminal(right));
  }
 
 private <T> BDDInternal<T> mk(String v, BDD<T> low, BDD<T> high) {
	 if(low.isEquivalent(high)) {
		 return (BDDInternal<T>) low;
	 } 
	 BDDInternal<T> newNode = new BDDInternal<T>(v, low, high);
	return lookupCache(internalMap, newNode, () -> newNode);
 }
 
 private <T> BDD<T> mkTerminal(T value) {
	 BDDTerminal<T> newNode = new BDDTerminal<T>(value);
	return lookupCache(terminalMap, newNode, () -> newNode);
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
 
 <T, R> BDD<R> apply(BiFunction<T,T,R> op, BDD<T> one, BDD<T> two) {
	 
	  HashMap<BDDPair<T>,BDD<R>> pairCache = new HashMap<>();
	 	 BDDPair<T> pair = new BDDPair<T>(one, two);
	 	 BDD<R> found = pairCache.get(pair);
	     if (found != null)
	         return found;
	     BDD<R> newNode;
	     if (one.isLeafNode() && two.isLeafNode()) {
	    	 newNode = mkTerminal(op.apply(((BDDTerminal<T>)one).v, ((BDDTerminal<T>)two).v)); //creates a terminal node
	     }
	     else if (one.getOption().equals(two.getOption()))
	         newNode = mk(one.getOption(), apply(op, one.getLow(), two.getLow()), apply(op, one.getHigh(), two.getLow()));
	     else if (two.isLeafNode() || (one.getOption().compareTo(two.getOption()) > 0))
	         newNode = mk(one.getOption(), apply(op, one.getLow(), two), apply(op, one.getHigh(), two));
	     else
	         newNode = mk(two.getOption(), apply(op, one, two.getLow()), apply(op, one, two.getHigh()));

	     pairCache.put(pair, newNode);
	     return newNode;
	 }
 
 
//Written by Christian Kästner (modified)
//Can be used in conjunction with http://www.webgraphviz.com/ to print BDD
 public <T> void printDot(BDD<T> bdd) {

     System.out.println("digraph G {");
     //if leaf node getId() [shape=box, ....
     System.out.println("0 [shape=box, label=\"FALSE\", style=filled, shape=box, height=0.3, width=0.3];");
     System.out.println("1 [shape=box, label=\"TRUE\", style=filled, shape=box, height=0.3, width=0.3];");
     Set<BDD<T>> seen = new HashSet<>();
     LinkedList<BDD<T>> queue = new LinkedList<>();
     queue.add(bdd);

     while (!queue.isEmpty()) {
    	 BDD<T> b = queue.remove();
         if (!(seen.contains(b)) && (!b.isLeafNode())) {
             seen.add(b);
             System.out.println(b.getID() + " [label=\"" + b.getOption() + "\"];");
             System.out.println(b.getID() + " -> " + b.getLow().getID() + " [style=dotted];");
             System.out.println(b.getID() + " -> " + b.getHigh().getID() + " [style=filled];");
             queue.add(b.getLow());
             queue.add(b.getHigh());
         }

     }

     System.out.println("}");
 }
//=============================================================================================================================================
 public interface BDD<T> {
	 BDD<T> getHigh();
	 int getID();
	 BDD<T> getLow();
	 String getOption(); //v and if terminal "" or null (whatever is lowest / last in ordering)
	 public int getHash(); 
     public boolean isEquivalent(BDD<T> other);
     boolean isLeafNode();
 }
 //=============================================================================================================================================
 public class BDDInternal<T> implements BDD<T>
 {
   BDD<T> low;
   BDD<T> high;
   String v; //variable name ex. x1
   int hash;

 //constructor
 BDDInternal(String v, BDD<T> low, BDD<T> high){
     this.high = high;
     this.low =low;
     this.v = v;
     this.hash = v.hashCode() + 31 * (low == null ? 0 : low.getHash()) + 27 * (high == null ? 0 : high.getHash());
 }
 
 public int getHash() {
	 return hash;
 }

 @Override
 public boolean isEquivalent(BDD<T> other) { 
	 if(this == other) {
		 return true;
	 }
	 if(other instanceof BDDInternal) {
		 BDDInternal<T> otherNode = (BDDInternal<T>) other;
		 if((this.high == other.getHigh() && this.low == other.getLow() && this.v.equals(otherNode.v))) {
		 		return true;  
	 	  }
	 }
	return false;
 }

 @Override
public boolean isLeafNode(){
     return false;   
 }

 @Override
 public int getID() {
     return Math.abs(this.hash);
 }

@Override
public BDD<T> getHigh() {
	return this.high;
}

@Override
public BDD<T> getLow() {
	return this.low;
}

@Override
public String getOption() {
	return v;
}

 }
//=======================================================================================================================================
//=============================================================================================================================================
public class BDDTerminal<T> implements BDD<T>
{
  T v;
  int hash;

//constructor
BDDTerminal(T v){
    this.v = v;
    this.hash = v.hashCode();
}

public int getHash() {
	 return hash;
}

@Override
public boolean isEquivalent(BDD<T> other) {
	if(this == other) {
		 return true;
	 }
	 if(other instanceof BDDTerminal) {
		 BDDTerminal<T> otherNode = (BDDTerminal<T>) other;
		 if((this.v == otherNode.v)) {
		 		return true;  
	 	  }
	 }
		 return false;
	}

@Override
public boolean isLeafNode(){
    return true;   
}

@Override
public int getID() {
	 return Math.abs(this.hash);
}

@Override
public BDD<T> getHigh() {
	return null;
}

@Override
public BDD<T> getLow() {
	return null;
}

@Override
public String getOption() {
	return v.toString();
}

}
//=======================================================================================================================================
 
 private class BDDPair<T> {
	    private final BDD<T> low, high;

	    BDDPair(BDD<T> low, BDD<T> high) {
	        this.low = low;
	        this.high = high;
	    }

	    public int hashCode() {
	        return low.getHash() + high.getHash();
	    }

	    @Override
	    public boolean equals(Object t) {
	        if (t instanceof BDDPair) {
				BDDPair<T> that = (BDDPair<T>) t;
	            return this.high.isEquivalent(that.high) && this.low.isEquivalent(that.low);
	        }
	        return false;
	    }
	}
 
 //===========================================================================================================================================
}
