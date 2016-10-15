package inf.ed.gfd.algorithm.sequential;
import inf.ed.gfd.structure.Condition;
import inf.ed.gfd.structure.DFS;
import inf.ed.gfd.structure.GFD2;
import inf.ed.gfd.structure.LiterTree;
import inf.ed.gfd.structure.WorkUnitC2WEp;
import inf.ed.gfd.structure.WorkUnitW2C;
import inf.ed.gfd.util.Params;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.OrthogonalVertex;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgePattern {
	
	

	static Logger log = LogManager.getLogger(EdgePattern.class);
	  // here String denotes the pattern string P previous, N id the extended 
	  HashMap<String, List<Int2IntMap> > patternNodeMatchesP =  new HashMap<String, List<Int2IntMap>>();
	  //the ith layer , now ;
	  HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();
	  
	  
	  HashMap<String, List<Int2IntMap> > edgePatternNodeMatch =  new HashMap<String, List<Int2IntMap>>();
	  
	  HashMap<String,Int2ObjectMap<IntSet>> edgePatternNMatch = new HashMap<String,Int2ObjectMap<IntSet>> ();
	  
	  HashMap<String,Int2ObjectMap<IntSet>> patternNMatchesN = new HashMap<String,Int2ObjectMap<IntSet>> ();
	  
	  HashMap<String,Int2ObjectMap<IntSet>> patternNMatchesP = new HashMap<String,Int2ObjectMap<IntSet>> ();
	  
	  
	  //the ith layer , now ;
	 // HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();
	 
	  public Graph<VertexOString, OrthogonalEdge> KB;
	  
	  
	  
	  
	  /**
	   *  edge pattern;
	   * @param KB
	   * @param nodeMatch
	   * @param edgeMatch
	   * @return
	   */
	 HashMap<String, Integer> labelId = new HashMap<String, Integer>();
	  
	 public List<DFS> edgePattern(HashMap<String, Integer> labelsId, Graph<VertexOString, OrthogonalEdge> KB){
	   OrthogonalVertex fNode, tNode;
	   VertexOString fVertex , tVertex;
	   List<DFS> dfs = new ArrayList<DFS>(); 
      // get all edge patterns and literals for nodes ;
	   for(OrthogonalEdge edge: KB.allEdges()){ 
	     //System.out.print("success");
	     
	     fNode = edge.from();
	     tNode = edge.to();
	     int fID = fNode.getID();
	     int tID = tNode.getID();
	     fVertex = KB.getVertex(fID);
	     tVertex = KB.getVertex(tID);
	     String fLabel = fVertex.getAttr();
	     String tLabel = tVertex.getAttr();
	     int fId = labelId.get(fLabel);
	     int tId = labelId.get(tLabel);
	     
	     int[] eLabel = edge.getAttr();
	     int elNum = edge.attrCount;
	     
	     Pair<Integer> f = new Pair<Integer>(fId,0);
    	 Pair<Integer> t = new Pair<Integer>(tId,0);
    	 if(fId == tId){
    		 t.y = 1;
    	 }
	   
	     for(int i = 0; i< elNum; i++){
	    	 
	       DFS code = new DFS( f, t, eLabel[i]);

	      
	         String pId = code.toString();
	         dfs.add(code);
	         if(!edgePatternNodeMatch.containsKey(pId)){
	        	 edgePatternNodeMatch.put(pId, new ArrayList<Int2IntMap>());  
	         }
	         Int2IntMap e = new Int2IntOpenHashMap();
	         e.put(fId, fID);
	         e.put(tId, tID);
	         edgePatternNodeMatch.get(pId).add(e); 
	         
	         if(!edgePatternNMatch.containsKey(pId)){
	        	 edgePatternNMatch.put(pId, new Int2ObjectOpenHashMap<IntSet>());  
	         }
	         if(!edgePatternNMatch.get(pId).containsKey(fId)){
	        	 edgePatternNMatch.get(pId).put(fId, new IntOpenHashSet());
	         }
	         edgePatternNMatch.get(pId).get(fId).add(fID);
	         
	         if(!edgePatternNMatch.get(pId).containsKey(tId)){
	        	 edgePatternNMatch.get(pId).put(tId, new IntOpenHashSet());
	         }
	         edgePatternNMatch.get(pId).get(tId).add(fID);  	    	
	     }
	   }
	   return dfs;
	 }  
	 int partitionId;
	 
	 
	 @SuppressWarnings("unchecked")
	public Set<WorkUnitW2C> InitialWorkUnit(HashMap<String, Integer> labelsId, Graph<VertexOString, OrthogonalEdge> KB){
		 patternNodeMatchesN = (HashMap<String, List<Int2IntMap>>) edgePatternNodeMatch.clone();
		 patternNMatchesN = (HashMap<String, Int2ObjectMap<IntSet>>) edgePatternNMatch.clone();
		 Set<WorkUnitW2C> ws  = new HashSet<WorkUnitW2C>();
		 List<DFS> dfss = edgePattern(labelsId,KB);
		 for(DFS dfs : dfss){
			 String pId = dfs.toString();
			 int supp = edgePatternNodeMatch.get(pId).size();
			 WorkUnitW2C w = new WorkUnitW2C(pId, supp,partitionId);
			 ws.add(w);
		 }
		 return ws;
	 }
	 
	public void IncrementalCompute(Set<WorkUnitC2WEp> wsc){
	    Set<WorkUnitC2WEp> ws  = new HashSet<WorkUnitC2WEp>();
	    patternNodeMatchesP = (HashMap<String, List<Int2IntMap>>) patternNodeMatchesN.clone();
	    patternNMatchesP = (HashMap<String, Int2ObjectMap<IntSet>>) patternNMatchesN.clone();
	    patternNodeMatchesN.clear();
	    patternNMatchesN.clear();
	    for(WorkUnitC2WEp w : wsc){
	    //extends pattern,
	    	
		String ppId = w.oriPatternId;
		HashMap<String,Pair<Integer>> edgeIds = w.edgeIds;
		
		List<Int2IntMap> pmatches = patternNodeMatchesP.get(ppId);
		
		for(Entry<String, Pair<Integer>> entry : edgeIds.entrySet()){
			String edgeId = entry.getKey();
			int fId = entry.getValue().x;
			int tId = entry.getValue().y;
			IntSet fmatchs = edgePatternNMatch.get(edgeId).get(fId);
			IntSet tmatchs = edgePatternNMatch.get(edgeId).get(tId);
			
			//revise may be get information from edgeID;
			if(pmatches.get(0).containsKey(fId)){
			}
			if(pmatches.get(0).containsKey(tId)){
			}
			if(pmatches.get(0).containsKey(tId) && pmatches.get(0).containsKey(tId) ){
			}
			
			
			
		}
			
		
			
			
		
	    				
	    				
	
	    	
	    		
	    		
	    		
	    		    
	    	}
	    }
		
	}
}
	public static void main(String args[]) {
		HashMap<Integer,Integer> a = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> b = new HashMap<Integer,Integer>();
		a.put(0, 0);
		a.put(0, 1);
		a.put(1, 0);
		a.put(2, 3);
		for(Map.Entry<Integer, Integer> entry : a.entrySet()){
			log.debug(entry.getKey()+" "+entry.getValue());
		}
		b = (HashMap<Integer, Integer>) a.clone();
		
		a.clear();
		
		for(Map.Entry<Integer, Integer> entry : b.entrySet()){
			log.debug(entry.getKey()+" "+entry.getValue());
		}
		for(Map.Entry<Integer, Integer> entry : a.entrySet()){
			log.debug(entry.getKey()+" "+entry.getValue());
		}
	}
}
	   

	 

	
