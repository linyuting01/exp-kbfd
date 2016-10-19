package inf.ed.gfd.structure;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Condition implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger log = LogManager.getLogger(Condition.class);

	public HashMap<Integer, String> XEqualsLiteral;
	public HashMap<Integer, IntSet> XEqualsVariable;
    
	
	public Pair<Integer, String> YEqualsLiteral;
	public Pair<Integer,Integer> YEqualsVariable;
	

	//public HashMap<Integer, String> YEqualsLiteral;
	//public HashMap<Integer, IntSet> YEqualsVariable;
	
	public String conditionId;

	public boolean isLiteral = false; 

	public Condition() {

		XEqualsLiteral = new HashMap<Integer, String>();
		XEqualsVariable = new HashMap<Integer, IntSet>();

		YEqualsLiteral = new Pair<Integer, String>();
		YEqualsVariable = new Pair<Integer, Integer>();
	}
	public void setYEqualsLiteral(int x, String y){
		this.YEqualsLiteral.x = x;
		this.YEqualsLiteral.y = y;
	}
	
	public void setYEqualsVariable(int x, int y){
		this.YEqualsVariable.x = x;
		this.YEqualsVariable.y = y;
	}
	
	@Override  
    public Object clone() {  
      Condition cond = null;  
        try{  
            cond = (Condition)super.clone();   //浅复制  
        }catch(CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        cond.XEqualsLiteral = new HashMap<Integer, String>(this.XEqualsLiteral);
        cond.XEqualsVariable = new HashMap<Integer, IntSet>();
        for(Entry<Integer, IntSet> entry: this.XEqualsVariable.entrySet()){
        	cond.XEqualsVariable.put(entry.getKey(), new IntOpenHashSet(entry.getValue()));
        }
      
        cond.YEqualsLiteral = new Pair<Integer, String>(this.YEqualsLiteral);
        
        return cond;  
    } 
	
	 public static void main(String args[]) {  
		 Condition cond = new Condition();
		 cond.XEqualsLiteral.put(1,"asd");
		 cond.XEqualsVariable.put(1, new IntOpenHashSet());
		 cond.XEqualsVariable.get(1).add(2);
		 cond.XEqualsVariable.get(1).add(3);
		 String x = cond.toString();
		 log.debug(x);
		 
		 Condition cond2 = (Condition) cond.clone();
		 
		 cond2.XEqualsLiteral.put(2, "234"); 
		 cond2.XEqualsVariable.get(1).add(4);
		 String conds1 = cond.toString();
		 String conds2 = cond2.toString();
		 log.debug(conds1 + "\t"+conds2);
		 
		
	}

	public boolean verifyX(Int2IntMap match, Graph<VertexOString, OrthogonalEdge> KB,IntSet matchx, int count) {

		// log.debug("begin verify: " + match.toString());

		// check for X
		for (int u : XEqualsLiteral.keySet()) {
			// for literal equation, if not equals then it valid.
			int vertexID = match.get(u);
			// log.debug(vertexID + ":" + KB.getVertex(vertexID).getAttr() +
			// ", expt = "
			// + XEqualsLiteral.get(u));
			if (!KB.getVertex(vertexID).getAttr().equals(XEqualsLiteral.get(u))) {
				return true;
			}
		}

		for (int u : XEqualsVariable.keySet()) {
			// for variable equation, if not equals then it valid
			int vertexID1 = match.get(u);
			for(int value: XEqualsVariable.get(u)){
			    int vertexID2 = match.get(value);
			// log.debug(vertexID1 + ":" + KB.getVertex(vertexID1).getAttr() +
			// "|" + +vertexID2 + ":"
			// + KB.getVertex(vertexID2).getAttr());
			    if (!KB.getVertex(vertexID1).getAttr().equals(KB.getVertex(vertexID2).getAttr())) {
				return false;
			}
		}
		}

		// log.debug("passed X match");
		matchx.add(count);
		return true;
	}
	
	
	
	public boolean verifyY(Int2IntMap match, Graph<VertexOString, OrthogonalEdge> KB, IntSet matchy, int count) {

      // log.debug("begin verify: " + match.toString());

      // check for X

      // check for Y. it valid only satisfy the condition
		if(this.isLiteral == true){
			int u = YEqualsLiteral.x;
	        int vertexID = match.get(u);
	        if (!KB.getVertex(vertexID).getAttr().equals(YEqualsLiteral.y)) {
	        	return false;
	        }
	    }
		else{
			int vertexID1 = YEqualsVariable.x;
			int vertexID2 = YEqualsVariable.y;
			 if (!KB.getVertex(vertexID1).getAttr().equals(KB.getVertex(vertexID2).getAttr())) {
	              return false;
			 }
			
		}
      matchy.add(count);
      return true;
  }    
	/////////////////////////////////////////////
	
	

	public boolean verify(Int2IntMap match, Graph<VertexOString, OrthogonalEdge> KB) {

      // log.debug("begin verify: " + match.toString());

      // check for X
      for (int u : XEqualsLiteral.keySet()) {
          // for literal equation, if not equals then it valid.
          int vertexID = match.get(u);
          // log.debug(vertexID + ":" + KB.getVertex(vertexID).getAttr() +
          // ", expt = "
          // + XEqualsLiteral.get(u));
          if (!KB.getVertex(vertexID).getAttr().equals(XEqualsLiteral.get(u))) {
              return true;
          }
      }

      for (int u : XEqualsVariable.keySet()) {
          // for variable equation, if not equals then it valid
          int vertexID1 = match.get(u);
          for(int value: XEqualsVariable.get(u)){
			    int vertexID2 = match.get(value);
          // log.debug(vertexID1 + ":" + KB.getVertex(vertexID1).getAttr() +
          // "|" + +vertexID2 + ":"
          // + KB.getVertex(vertexID2).getAttr());
                if (!KB.getVertex(vertexID1).getAttr().equals(KB.getVertex(vertexID2).getAttr())) {
                         return true;
          }
          }
      }

      // log.debug("passed X match");

      // check for Y. it valid only satisfy the condition
      if(this.isLiteral == true){
			int u = YEqualsLiteral.x;
	        int vertexID = match.get(u);
	        if (!KB.getVertex(vertexID).getAttr().equals(YEqualsLiteral.y)) {
	        	return false;
	        }
	    }
		else{
			int vertexID1 = YEqualsVariable.x;
			int vertexID2 = YEqualsVariable.y;
			 if (!KB.getVertex(vertexID1).getAttr().equals(KB.getVertex(vertexID2).getAttr())) {
	              return false;
			 }
		}
      return true;
  }


  

    
    
    
    public void clear(){
      this.XEqualsLiteral.clear();
      this.XEqualsVariable.clear();
    }
    
    
    public void combineCondition(Condition c1 , Condition c2){
      this.XEqualsLiteral.putAll(c1.XEqualsLiteral);
      this.XEqualsLiteral.putAll(c2.XEqualsLiteral);
      this.XEqualsVariable.putAll(c1.XEqualsVariable);
      this.XEqualsLiteral.putAll(c2.XEqualsLiteral);
    }

	////////////////////////////////////////
	  public void combineCondition(Condition c2){
	    this.XEqualsLiteral.putAll(c2.XEqualsLiteral);
	    this.XEqualsVariable.putAll(c2.XEqualsVariable);
	  }
	    @Override
	    public boolean equals(Object obj) {
	       Condition other = (Condition) obj;
	       return this.XEqualsLiteral == other.XEqualsLiteral && this.XEqualsVariable == other.XEqualsVariable
	           && this.YEqualsLiteral == other.YEqualsLiteral && this.YEqualsVariable == other.YEqualsVariable;
	          
	    }

	    @Override
	    public int hashCode() {
	        return this.toString().hashCode();
	    }
	    
	    @Override
	    
	    public String toString(){
	      List<String> ts = new ArrayList<String>();
	      transferIString(this.XEqualsLiteral,ts,0);
	      transferIString(this.XEqualsVariable,ts,1);
	      if(this.isLiteral){
	    	  transferIString(this.YEqualsLiteral,ts,true);
	      }
	      else{
	    	  transferIString(this.YEqualsVariable,ts,false);
	      }
	      Collections.sort(ts);
	      StringBuffer sb = new StringBuffer();
	      for(String s :ts)
	      {
	        sb.append(s);
	      }
	      return sb.toString();
    }
	 

   
   
      public <T> void transferIString(HashMap<Integer, T> literal, List<String> ts, int i) {
        StringBuffer sb = new StringBuffer();
        if(i==0){
         sb.append("XEQ");
        }
        if(i==1){
          sb.append("XEV");
         }
        for(Entry<Integer, T> entry : literal.entrySet()){
           int i1 = entry.getKey();
           sb.append(i1);
           sb.append(":");
           sb.append(entry.getValue());
        }
        
        ts.add(sb.toString());
	  
      }
      public <T> void transferIString( Pair<Integer,T> p, List<String> ts, boolean isLiteral) {
	      StringBuffer sb = new StringBuffer();
	      if(isLiteral == true){
	        sb.append("YEQ");
	      }
	      else{
	        sb.append("YEV");
	      }
		sb.append(p.x);
		sb.append(":");
		sb.append(p.y);
		ts.add(sb.toString());
  	  
  }
    
      public boolean isXEmpty(){
        if(this.XEqualsLiteral.isEmpty() && this.XEqualsVariable.isEmpty() ){
          return true;
        }
        return false;
      }
 
   /*   
      public boolean trival(){
        HashMap<String, IntSet> lter = new HashMap<String, IntSet>();
        Int2ObjectMap<IntSet> lvar = new Int2ObjectOpenHashMap<IntSet>();
        for(Map.Entry<Integer, String>  entry: this.XEqualsLiteral.entrySet()){
          String value = entry.getValue();
          if(lter.containsKey(value)){
            lter.get(value).add(entry.getKey());
          }
          else{
            lter.put(value, new IntOpenHashSet());
            lter.get(value).add(entry.getKey());
          }
        }
        
        for(Map.Entry<Integer, Integer>  entry: this.XEqualsVariable.entrySet()){
          int node1 = entry.getKey();
          int node2 = entry.getValue();
          if(lvar.containsKey(node1)){
            lvar.get(node1).add(entry.getKey());
          }
          else{
            lvar.put(node1, new IntOpenHashSet());
            lvar.get(node1).add(node2);
          }
          while(this.XEqualsVariable.containsKey(node2)){
            lvar.get(node1).add(this.XEqualsVariable.get(node2));
            node2 = this.XEqualsVariable.get(node2);    
          }  
        }
        
        for(Map.Entry<Integer, String>  entry: this.YEqualsLiteral.entrySet()){
          int id = entry.getKey();
          String v1 = entry.getValue();
          if(lter.containsKey(v1)){
            for(int i : lter.get(v1)){
              if(lvar.containsKey(i)){
                if(lvar.get(i).contains(id)){
                  return true;
                }
              }
              if(lvar.containsKey(id)){
                if(lvar.get(id).contains(i)){
                  return true;
                }
              }
            }
          }
        }
        for(Map.Entry<Integer, Integer>  entry: this.YEqualsVariable.entrySet()){
          int key = entry.getKey();
          int value = entry.getValue();
          if(this.XEqualsLiteral.containsKey(key))
          {
            if(this.XEqualsLiteral.containsKey(value)){
              if(this.XEqualsLiteral.get(key) == this.XEqualsLiteral.get(value)){
                return true;
              }
            }
          }
        }
        return false;
     }
      
   /*   
      public boolean leftReduced(TrieNode t){
        TrieNode parent = t.getParent();
        List<TrieNode> children = t.getChildList();
        for(TrieNode n : children){
           if(n.getGFDs().getID() == t.getGFDs().getID());
           {
             if(n.getGFDs().getCondition().isXEmpty()){
               return true;
             }
             
           }
           
        }
        return false;  
      }
      
      public boolean patternReduced(TrieNode t, TrieIndex index){
        Condition tc = t.getGFDs().getCondition();
        while (t.forward != -1){
         TrieNode n =  index.trieNodeIndex.get(t.forward);
            if(n.getGFDs().getCondition().equals(tc)){
              return true;
            }
            else{
              t = n;
            }
        }
        return false;
      }
      */
}