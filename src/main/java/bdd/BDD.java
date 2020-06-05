package bdd;

import java.util.HashMap;
import java.util.Map;

public class BDD
{

  Map<BDDNode, String> map;
  Map<String,BDDNode> inverseMap;

//constructor
  public BDD() {
    map = new HashMap<BDDNode, String>();
    inverseMap = new HashMap<String,BDDNode>();
  }

//Boolean Functions
static Boolean and(Boolean x1, Boolean x2) {
  return x1 && x2;
}
static Boolean or(Boolean x1, Boolean x2) {
  return x1 || x2;
}
static Boolean xor(Boolean x1, Boolean x2) {
  return (x1 && !x2) || (!x1 && x2);
}

Boolean isEquivalent(BDD other) {
  // if()
  return true;
}

}
