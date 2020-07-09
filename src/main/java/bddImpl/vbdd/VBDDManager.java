package bddImpl.vbdd;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import bddImpl.V;

public class VBDDManager
{

private WeakHashMap<VBDDInternal, WeakReference<VBDDInternal>> internalMap = new WeakHashMap<>();
private WeakHashMap<VBDDTerminal, WeakReference<VBDDTerminal>> terminalMap = new WeakHashMap(); //from terminal to terminal 
private HashMap<String, Integer> symbolMap = new HashMap();


//constructor
  public VBDDManager() {
  }
  
  private static int symbolCounter = 0;
  
  public <T> VBDD<T> choice(String cond, T left, T right) {
	  if (!symbolMap.containsKey(cond)) {      //Keeps track of the order the variables are added in
		  symbolCounter++;
		  symbolMap.put(cond, symbolCounter);
	  }
	  return mk(cond, mkTerminal(left), mkTerminal(right));
  }
  
  public <T> VBDD<T> one(T value) {
	  return mkTerminal(value);
  }
 
 private <T> VBDD<T> mk(String v, VBDD<T> low, VBDD<T> high) {
	 if(low.isEquivalent(high)) {
		 return (VBDD<T>) low;
	 } 
	 VBDDInternal<T> newNode = new VBDDInternal<T>(v, low, high);
	return lookupCache(internalMap, newNode, () -> newNode);
 }
 
 private <T> VBDD<T> mkTerminal(T value) {
	 VBDDTerminal<T> newNode = new VBDDTerminal<T>(value);
	return lookupCache(terminalMap, newNode, () -> newNode);
 }
 
 //Written by Christian Kästner
 private <K, L> L lookupCache(WeakHashMap<K, WeakReference<L>> cache, K key, Supplier<L> newValue) {
     WeakReference<L> v = cache.get(key);
     L val = null;
     if (v != null) {
         val = v.get();
     }
     if (val != null)
         return val;

     val = newValue.get();
     cache.put(key, new WeakReference<L>(val));
     return val;
 }
 
 <T, R> VBDD<R> apply(BiFunction<T,T,R> op, VBDD<T> one, VBDD<T> two) {
	 
	  HashMap<VBDDPair<T>,VBDD<R>> pairCache = new HashMap<>();
	 	 VBDDPair<T> pair = new VBDDPair<T>(one, two);
	 	 VBDD<R> found = pairCache.get(pair);
	     if (found != null)
	         return found;
	     VBDD<R> newNode;
	     if (one.isLeafNode() && two.isLeafNode()) {
	    	 newNode = mkTerminal(op.apply(((VBDDTerminal<T>)one).v, ((VBDDTerminal<T>)two).v)); //creates a terminal node
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
 
 private String min(String first, String second) {
	 return symbolMap.get(first) < symbolMap.get(second) ? first : second;
 }
 

 public <T> V<T> ite_(V<Boolean> f, V<T> g, V<T> h) {
     return ite((VBDD<Boolean>) f, (VBDD<T>) g, (VBDD<T>) h);
 }

 public <T> VBDD<T> ite(VBDD<Boolean> f, VBDD<T> g, VBDD<T> h) {
     return _ite(f, g, h, new HashMap<>());
 }

 private <T> VBDD<T> _ite(VBDD<Boolean> f, VBDD<T> g, VBDD<T> h, Map<Triple<VBDD<Boolean>, VBDD<T>, VBDD<T>>, VBDD<T>> cache) {
     if (f.getOption().equals("true")) return g;
     if (f.getOption().equals("false")) return h;
     if (g == h) return h;

     Triple<VBDD<Boolean>, VBDD<T>, VBDD<T>> tr = new Triple<>(f, g, h);
     if (cache.containsKey(tr))
         return cache.get(tr);

     String v = min(h.getOption(), min(f.getOption(), g.getOption()));
     VBDD<T> t = _ite(v == f.getOption() ? f.getHigh() : f, v == g.getOption() ? g.getHigh() : g, v == h.getOption() ? h.getHigh() : h, cache);
     VBDD<T> e = _ite(v == f.getOption() ? f.getLow() : f, v == g.getOption() ? g.getLow() : g, v == h.getOption() ? h.getLow() : h, cache);
     if (t == e) return e;
     VBDD<T> result = mk(v, e, t);
     cache.put(tr, result);
     return result;
 }
 
 
//Written by Christian Kästner (modified)
//Can be used in conjunction with http://www.webgraphviz.com/ to print BDD
 public <T> void printDot(V<T> bdd) {

     System.out.println("digraph G {");
     Set<VBDD<T>> seen = new HashSet<>();
     LinkedList<VBDD<T>> queue = new LinkedList<>();
     queue.add((VBDD<T>) bdd);

     while (!queue.isEmpty()) {
    	 VBDD<T> b = queue.remove();
         if (!(seen.contains(b)) && (!b.isLeafNode())) {
             seen.add(b);
             System.out.println(b.getID() + " [label=\"" + b.getOption() + "\"];");
             System.out.println(b.getID() + " -> " + b.getLow().getID() + " [style=dotted];");
             System.out.println(b.getID() + " -> " + b.getHigh().getID() + " [style=filled];");
             queue.add(b.getLow());
             queue.add(b.getHigh());
         } else if(!(seen.contains(b)) && (b.isLeafNode())) {
        	 System.out.println(b.getID() + " [shape=box, label=\"" + b.getOption() + "\", style=filled, shape=box, height=0.3, width=0.3];");
         }

     }

     System.out.println("}");
 }
//=============================================================================================================================================
 public interface VBDD<T> extends V<T> {
	 VBDD<T> getHigh();
	 int getID();
	 VBDD<T> getLow();
	 String getOption(); //v and if terminal "" or null (whatever is lowest / last in ordering)
	 public int getHash(); 
     public boolean isEquivalent(VBDD<T> other);
     boolean isLeafNode();
 }
 //=============================================================================================================================================
 public class VBDDInternal<T> implements VBDD<T>
 {
   VBDD<T> low;
   VBDD<T> high;
   String v; //variable name ex. x1
   int hash;

 //constructor
 VBDDInternal(String v, VBDD<T> low, VBDD<T> high){
     this.high = high;
     this.low =low;
     this.v = v;
     this.hash = v.hashCode() + 31 * (low == null ? 0 : low.getHash()) + 27 * (high == null ? 0 : high.getHash());
 }
 
 public int getHash() {
	 return hash;
 }

 @Override
 public boolean isEquivalent(VBDD<T> other) { 
	 if(this == other) {
		 return true;
	 }
	 if(other instanceof VBDDInternal) {
		 VBDDInternal<T> otherNode = (VBDDInternal<T>) other;
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
public VBDD<T> getHigh() {
	return this.high;
}

@Override
public VBDD<T> getLow() {
	return this.low;
}

@Override
public String getOption() {
	return v;
}

@Override
public <U> VBDD<U> map(Function<T, U> fun) {
	//We had this returning type V<U> instead of type BDD<U> which caused some casting problems. After examing Christian's VBDDfactory,
	// I realized he returned VNode<U> which extended V<U>. Essentially his VNode is my BDD interface. Also, I need help fixing printDot() (maybe)
	VBDD<U> newBDD = (VBDD<U>) mk(this.getOption(), (VBDD<T>) this.getLow().map(fun), (VBDD<T>) this.getHigh().map(fun));
	
	return newBDD;
}

@Override
public <U> VBDD<U> flatMap(Function<T, V<U>> fun) {
	VBDD<U> newBDD = (VBDD<U>) mk(this.getOption(), (VBDD<T>) this.getLow().flatMap(fun), (VBDD<T>) this.getHigh().flatMap(fun));
	return newBDD;
}

 }
//=======================================================================================================================================
//=============================================================================================================================================
public class VBDDTerminal<T> implements VBDD<T>
{
  T v;
  int hash;

//constructor
VBDDTerminal(T v){
    this.v = v;
    this.hash = v.hashCode();
}

public int getHash() {
	 return hash;
}

@Override
public boolean isEquivalent(VBDD<T> other) {
	if(this == other) {
		 return true;
	 }
	 if(other instanceof VBDDTerminal) {
		 VBDDTerminal<T> otherNode = (VBDDTerminal<T>) other;
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
public VBDD<T> getHigh() {
	return null;
}

@Override
public VBDD<T> getLow() {
	return null;
}

@Override
public String getOption() {
	return v.toString();
}

@Override
public <U> VBDD<U> map(Function<T, U> fun) {
	//you already know it is a terminal node 
	return mkTerminal(fun.apply(this.v));
}

@Override
public <U> V<U> flatMap(Function<T, V<U>> fun) {	
	return fun.apply(this.v);
}

}
//=======================================================================================================================================
 
 private class VBDDPair<T> {
	    private final VBDD<T> low, high;

	    VBDDPair(VBDD<T> low, VBDD<T> high) {
	        this.low = low;
	        this.high = high;
	    }

	    public int hashCode() {
	        return low.getHash() + high.getHash();
	    }

	    @Override
	    public boolean equals(Object t) {
	        if (t instanceof VBDDPair) {
				VBDDPair<T> that = (VBDDPair<T>) t;
	            return this.high.isEquivalent(that.high) && this.low.isEquivalent(that.low);
	        }
	        return false;
	    }
	}
 
 //===========================================================================================================================================
 
 public static class Triple<A, B, C> {
     private final A a;
     private final B b;
     private final C c;

     public Triple(A a, B b, C c) {
         this.a = a;
         this.b = b;
         this.c = c;
     }

     public int hashCode() {
         return a.hashCode() + b.hashCode() + c.hashCode();
     }

     public boolean equals(Object t) {
         if (t instanceof Triple) {
             Triple<A, B, C> that = (Triple<A, B, C>) t;
             return this.a == that.a && this.b == that.b && this.c == that.c;
         }
         return false;
     }
 }
 //============================================================================================================================================
}
