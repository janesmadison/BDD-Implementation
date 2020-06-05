package bdd;

import java.util.HashMap;
import java.util.Map;

public class BDD
{

  Map<BDDNode, String> mapNodes;
  Map<String,BDDNode> mapAttributes;

//constructor
  public BDD() {
    mapNodes = new HashMap<BDDNode, String>();      //these are wrong.. will need to convert these to something else to hold multiple types in the key and values
    mapAttributes = new HashMap<String,BDDNode>();
  }
  
 
 public BDDNode mk(String v, BDDNode low, BDDNode high) {
	 if(low.isEquivalent(high)) {
		 return low;
	 } //else if(member(H,i,l,h)) {
	 // return lookUp(H,i,l,h)
	 // } else {
	 // create a new node in T and insert it also into H
	return null;	 
 }
 
// private Boolean nodeExists(String v, BDDNode low, BDDNode high) {
//	 
// }

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
