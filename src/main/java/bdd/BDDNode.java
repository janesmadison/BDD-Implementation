package bdd;

public class BDDNode
{
  BDDNode high;
  BDDNode low;
  String v; //variable name ex. x1
  int hash;

//constructor
BDDNode(String v, BDDNode high, BDDNode low){
    this.high = high;
    this.low =low;
    this.v = v;
    this.hash = v.hashCode() + 31 * (low == null ? 0 : low.hash) + 27 * (high == null ? 0 : high.hash);
}

Boolean isEquivalent(BDDNode other) {
	  if((this.high == other.high && this.low == other.low && this.v.equals(other.v)) || this == other) {
		return true;  
	  }
	  return false;
	}

Boolean isLeafNode(){
    if(this.v.equals("true") || this.v.equals("false")) {
    	return true;
    } else {
    	return false;
    }    
}

 int getID() {
    if (this.v.equals("true")) return 1;
    else if (this.v.equals("false")) return 0;
    else return Math.abs(this.hash);
}

}
