package inf.ed.gfd.structure;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Condition implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger log = LogManager.getLogger(Condition.class);

	//public HashMap<Integer, String> XEqualsLiteral;
	//public HashMap<Integer, IntSet> XEqualsVariable;
    
	public Int2ObjectMap<Pair<Integer,String>> XEqualsLiteral;
	public List<Pair<Integer,Integer>> XEVvId;
	public List<Pair<Integer,Integer>> XEVattrId  = new ArrayList<Pair<Integer,Integer>>();
	
	public Pair<Integer,Pair<Integer, String>> YEqualsLiteral;
	
	public Pair<Integer,Integer> YEVvId = new Pair<Integer,Integer>();
	public Pair<Integer,Integer> YEVattrId  = new Pair<Integer,Integer>();


	

	//public HashMap<Integer, S tring> YEqualsLiteral;
	//public HashMap<Integer, IntSet> YEqualsVariable;
	
	public String conditionId;

	public boolean isLiteral = false; 

	public Condition() {

		XEqualsLiteral = new Int2ObjectOpenHashMap<Pair<Integer,String>>() ;
		XEVvId = new ArrayList<Pair<Integer,Integer>>();
		XEVattrId  = new ArrayList<Pair<Integer,Integer>>();
		YEVvId = new Pair<Integer,Integer>();
		YEVattrId  = new Pair<Integer,Integer>();

		YEqualsLiteral = new  Pair<Integer,Pair<Integer, String>>() ;
	}
	public void setYEqualsLiteral(int x, int attr,String y){
		this.YEqualsLiteral.x = x;
		this.YEqualsLiteral.y = new Pair<Integer,String>(attr,y);
	}
	
	public void setYEqualsVariable(int x, int attrx,int y,int attry){
		this.YEVvId = new Pair<Integer,Integer>(x,y);;
		this.YEVattrId =  new Pair<Integer,Integer>(attrx,attry);;
	}
	
 
	/*
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
    } */
	/*
	 public static void main(String args[]) {  
		 Condition cond = new Condition();
		 cond.XEqualsLiteral.put(1,"asd");
		 cond.XEqualsLiteral.put(2,"asd");
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
		 log.debug(conds1);
		 
		
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
			if (!KB.getVertex(vertexID).getValue().equals(XEqualsLiteral.get(u))) {
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
			    if (!KB.getVertex(vertexID1).getValue().equals(KB.getVertex(vertexID2).getValue())) {
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
	        if (!KB.getVertex(vertexID).getValue().equals(YEqualsLiteral.y)) {
	        	return false;
	        }
	    }
		else{
			int vertexID1 = YEqualsVariable.x;
			int vertexID2 = YEqualsVariable.y;
			 if (!KB.getVertex(vertexID1).getValue().equals(KB.getVertex(vertexID2).getValue())) {
	              return false;
			 }
			
		}
      matchy.add(count);
      return true;
  }    
	/////////////////////////////////////////////
	*/
	public boolean verifyAttrLiter(Graph<VertexOString, OrthogonalEdge> KB,
			int vertexID, int attr, String value){
		OrthogonalEdge e1 = KB.getVertex(vertexID).GetFirstOut();
		for (OrthogonalEdge e = e1; e != null; e = e.GetTLink()) {
			if(e.getAttr().contains(attr)){
				VertexOString t = (VertexOString) e.to();
				if(t.getValue().equals(value)){
					return true;
				}
			}
		
		}
		return false;
	}
	
	public boolean verifyAttrVar(Graph<VertexOString, OrthogonalEdge> KB,
			int vertexID,int vertexID2, int attr, int attr2){
		Set<String> a = getValue(KB,vertexID,attr);
		Set<String> b = getValue(KB,vertexID2,attr2);
		
		
		return false;
	}
	public Set<String> getValue(Graph<VertexOString, OrthogonalEdge> KB,
			int vertexID,int attr){
		Set<String> a = new HashSet<String>();
		OrthogonalEdge e1 = KB.getVertex(vertexID).GetFirstOut();
		for (OrthogonalEdge e = e1; e != null; e = e.GetTLink()) {
			if(e.getAttr().contains(attr)){
				VertexOString t = (VertexOString) e.to();
				a.add(t.getValue());
			}
		
		}
		return  a;
	}

	public boolean verify(Int2IntMap match, Graph<VertexOString, OrthogonalEdge> KB) {

      // log.debug("begin verify: " + match.toString());

      // check for X
      for (int u : XEqualsLiteral.keySet()) {
    	  Pair<Integer,String> pair = XEqualsLiteral.get(u);
          int vertexID = match.get(u);
          boolean flag = verifyAttrLiter(KB,vertexID,pair.x, pair.y);
          if(!flag){
        	  return false;
          }
      }

      for (Pair<Integer,Integer> vId :XEVvId) {
          int vertexID1 = match.get(vId.x);
          int vertexID2 = match.get(vId.y);
          
          for(int value: X.g){
			    int vertexID2 = match.get(value);
          // log.debug(vertexID1 + ":" + KB.getVertex(vertexID1).getAttr() +
          // "|" + +vertexID2 + ":"
          // + KB.getVertex(vertexID2).getAttr());
                if (!KB.getVertex(vertexID1).getValue().equals(KB.getVertex(vertexID2).getValue())) {
                         return true;
          }
          }
      }

      // log.debug("passed X match");

      // check for Y. it valid only satisfy the condition
      if(this.isLiteral == true){
			int u = YEqualsLiteral.x;
	        int vertexID = match.get(u);
	        if (!KB.getVertex(vertexID).getValue().equals(YEqualsLiteral.y)) {
	        	return false;
	        }
	    }
		else{
			int vertexID1 = match.get(YEqualsVariable.x);
			int vertexID2 = match.get(YEqualsVariable.y);
			 if (!KB.getVertex(vertexID1).getValue().equals(KB.getVertex(vertexID2).getValue())) {
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
	      StringBuffer sb = new StringBuffer();
	      sb.append("%x\n");
	      transferIStringLit(this.XEqualsLiteral,sb);
	      transferIStringVar(this.XEqualsVariable,sb);
	      sb.append("%y\n");
	      if(this.isLiteral){
	    	  transferIString(this.YEqualsLiteral,sb,true);
	      }
	      else{
	    	  transferIString(this.YEqualsVariable,sb,false);
	      }
	     // Collections.sort(ts);
	     
	
	      return sb.toString();
    }
	 

   
   
      public <T> void transferIStringVar(HashMap<Integer, IntSet> literal,  StringBuffer sb) {
       // StringBuffer sb = new StringBuffer();
        for(Entry<Integer, IntSet> entry : literal.entrySet()){
        	for(int nodeId2 : entry.getValue()){
        		 int i1 = entry.getKey();
                 sb.append(i1);
                 sb.append("\t");
              	  sb.append("eq-var");
                 	sb.append("\t");
                  sb.append(nodeId2);
                  sb.append("\n");
        	}
        }
                  
      
      }
      public <T> void transferIStringLit(HashMap<Integer, String> literal,  StringBuffer sb) {
          // StringBuffer sb = new StringBuffer();
           for(Entry<Integer, String> entry : literal.entrySet()){
              int i1 = entry.getKey();
              sb.append(i1);
              sb.append("\t");
             
           	  sb.append("eq-let"); 
           
              	sb.append("\t");
               sb.append(entry.getValue());
               sb.append("\n");
               //sb.append("\n");
              }
              //sb.append(" ");
   	  
         }
      public <T> void transferIString( Pair<Integer,T> p,  StringBuffer sb, boolean isLiteral) {
	     // StringBuffer sb = new StringBuffer();
	      sb.append(p.x);
	      sb.append("\t");
	      if(isLiteral == true){
	        sb.append("eq-let");
	      }
	      else{
	        sb.append("eq-var");
	       // sb.append(";");
	      }
	      sb.append("\t");
		   sb.append(p.y);
		//ts.add(sb.toString());
  	  
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