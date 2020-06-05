package bdd;

public class BDDNode
{
  BDDNode high;
  BDDNode low;
  String v; //variable name ex. x1

//constructor
BDDNode(String v, BDDNode high, BDDNode low){
    this.high = high;
    this.low =low;
    this.v = v;
}

Boolean isEquivalent(BDDNode other) {
	  if((this.high == other.high && this.low == other.low && this.v.equals(other.v)) || this == other) {
		return true;  
	  }
	  return false;
	}

//Boolean isLowLeaf(){
//  if(this.low instanceof BDDNode) {
//    if(this.low.nodeID == 0 || this.low.nodeID == 1)
//      return true;
//  }
//  return false;
//}
//
//Boolean isHighLeaf(){
//  if(this.high instanceof BDDNode) {
//    if(this.high.nodeID == 0 || this.high.nodeID == 1)
//      return true;
//  }
//  return false;
//}

}
