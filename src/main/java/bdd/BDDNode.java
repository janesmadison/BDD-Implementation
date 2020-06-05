package bdd;

public class BDDNode
{
  BDDNode high;
  BDDNode low;
  int nodeID;

//constructor
BDDNode(BDDNode high, BDDNode low, int v){
    this.high = high;
    this.low =low;
    this.nodeID = v;
}

Boolean isLowLeaf(){
  if(this.low instanceof BDDNode) {
    if(this.low.nodeID == 0 || this.low.nodeID == 1)
      return true;
  }
  return false;
}

Boolean isHighLeaf(){
  if(this.high instanceof BDDNode) {
    if(this.high.nodeID == 0 || this.high.nodeID == 1)
      return true;
  }
  return false;
}

}
