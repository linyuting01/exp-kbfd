package inf.ed.gfd.structure;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
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

	
    
	public Set<EqLiteral> XEqualsLiteral;
	public Set<EqVarLiter> XEqualsVariable;
	
	public EqLiteral YEqualsLiteral = null ;
	public EqVarLiter YEqualsVariable = null;
	
	
	


	

	//public HashMap<Integer, S tring> YEqualsLiteral;
	//public HashMap<Integer, IntSet> YEqualsVariable;
	
	public String conditionId;

	public boolean isLiteral = false; 

	public Condition() {

		XEqualsLiteral = new HashSet<EqLiteral>();
		XEqualsVariable = new HashSet<EqVarLiter>();
		
	}
	public void setYEqualsLiteral(EqLiteral x){
		this.YEqualsLiteral = x;
		
	}
	
	public void setYEqualsVariable(EqVarLiter y){
		this.YEqualsVariable = y;
	}
	
 
	
	public boolean verifyAttrLiter(Graph<VertexOString, OrthogonalEdge> KB, EqLiteral xl,Int2ObjectMap<String[]> kbAttr_Map){
			
		int vertexID = xl.nId;
		int attr = xl.attrId;
		String value = xl.val;
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
	
	public boolean verifyAttrVar(Graph<VertexOString, OrthogonalEdge> KB,EqVarLiter xv){
		int vertexID = xv.fId;
		int vertexID2 = xv.tId;
		int attr = xv.fattr;
		int attr2 = xv.tattr;
		
		OrthogonalEdge e1 = KB.getVertex(vertexID).GetFirstOut();
		for (OrthogonalEdge e = e1; e != null; e = e.GetTLink()) {
			if(e.getAttr().contains(attr)){
				VertexOString t1 = (VertexOString) e.to();
				int tID = t1.getID();
				if(KB.contains(vertexID2, tID)){
					OrthogonalEdge e2 = KB.getEdge(vertexID2, tID);
					if(e.getAttr().contains(attr2)){
						return true;
					}
				}
			}
		
		}
		
		return false;
	
	
	}


		public boolean verifyX(Int2IntMap match, Int2ObjectMap<List<TransAttr>> kbAttr_Map) {

		      // log.debug("begin verify: " + match.toString());

		      // check for X
		      for (EqLiteral eql : XEqualsLiteral) {
		    	  int vertexID = match.get(eql.nId);
		    	  if(kbAttr_Map.containsKey(vertexID)){
		    		  TransAttr a = new TransAttr(eql.attrId,eql.val);
		    
			    	  if(!kbAttr_Map.get(vertexID).contains(a)){
			    		  return false;
			    	  }
			    	}
		      }

		      for(EqVarLiter eqv : XEqualsVariable){
		    	  int vertexID = match.get(eqv.fId);
		    	  int vertexID2 = match.get(eqv.tId);
		    	  Set<String> fval = new HashSet<String>();
		    	  Set<String> tval = new HashSet<String>();
		    	
		    	  if(kbAttr_Map.containsKey(vertexID) && kbAttr_Map.containsKey(vertexID2) ){
		    		  for(TransAttr a : kbAttr_Map.get(vertexID) ){
		    			  if(a.attr ==  eqv.fattr){
		    				  fval.add(a.val);
		    			  }
		    		
		    		  }
		    		  for(TransAttr a : kbAttr_Map.get(vertexID2) ){
		    			  if(a.attr ==  eqv.tattr){
		    				  tval.add(a.val);
		    			  }
		    		
		    		  }
		    		  fval.retainAll(tval);
		    		  if(fval.isEmpty()){
		    			  return false;
		    		  }
			    	 
		    	  }
		      }
		
		return true;
	 }
	public boolean verifyY(Int2IntMap match, Int2ObjectMap<List<TransAttr>> kbAttr_Map) {

      // check for Y. it valid only satisfy the condition
      if(this.isLiteral == true){
	    	  int vertexID = match.get(YEqualsLiteral.nId);
		    	  if(kbAttr_Map.containsKey(vertexID)){
		    		  TransAttr a = new TransAttr(YEqualsLiteral.attrId,YEqualsLiteral.val);
		    
			    	  if(!kbAttr_Map.get(vertexID).contains(a)){
			    		  return false;
			    	  }
			    	}
	
	     }
		else{
			  int vertexID = match.get(YEqualsVariable.fId);
	    	  int vertexID2 = match.get(YEqualsVariable.tId);
	    	  Set<String> fval = new HashSet<String>();
	    	  Set<String> tval = new HashSet<String>();
	    	
	    	  if(kbAttr_Map.containsKey(vertexID) && kbAttr_Map.containsKey(vertexID2) ){
	    		  for(TransAttr a : kbAttr_Map.get(vertexID) ){
	    			  if(a.attr ==  YEqualsVariable.fattr){
	    				  fval.add(a.val);
	    			  }
	    		
	    		  }
	    		  for(TransAttr a : kbAttr_Map.get(vertexID2) ){
	    			  if(a.attr ==  YEqualsVariable.tattr){
	    				  tval.add(a.val);
	    			  }
	    		
	    		  }
	    		  fval.retainAll(tval);
	    		  if(fval.isEmpty()){
	    			  return false;
	    		  }
		    	 
	    	  }
	    	  
		}
      return true;
  }
	 @Override
	    
	    public String toString(){
	      StringBuffer sb = new StringBuffer();
	      sb.append("%x\n");
	      for (EqLiteral eql : XEqualsLiteral){
	    	  sb.append(eql.toString());
	      }
	      for(EqVarLiter eqv:XEqualsVariable){
	    	  sb.append(eqv.toString());
	      }
	      
	      sb.append("%Y\n");
	      
	      if(this.isLiteral == true){
	    	  sb.append(YEqualsLiteral.toString());
		      }
			else{
				sb.append(YEqualsVariable.toString());
			}
	       return sb.toString();
	 }

  public  boolean isEmpty(){
	  if(this.YEqualsLiteral == null && this.YEqualsVariable == null){
		  return true;
	  }
	  return false;
   }

    
  
}