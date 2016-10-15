package inf.ed.gfd.algorithm.sequential;
import inf.ed.gfd.structure.Condition;
import inf.ed.gfd.structure.DFS;
import inf.ed.gfd.structure.GFD2;
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
import java.util.Set;
import java.util.TreeSet;

public class EdgePattern {
	  // here String denotes the pattern string P previous, N id the extended 
	  HashMap<String, List<Int2IntMap> > patternNodeMatchesP =  new HashMap<String, List<Int2IntMap>>();
	  //the ith layer , now ;
	  HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();
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
	         if(!patternNodeMatchesN.containsKey(pId)){
	           patternNodeMatchesN.put(pId, new ArrayList<Int2IntMap>());  
	         }
	         Int2IntMap e = new Int2IntOpenHashMap();
	         e.put(fId, fID);
	         e.put(tId, tID);
	         patternNodeMatchesN.get(pId).add(e);
	     }
	   }
	   return dfs;
	 }  
	 int partitionId;
	 public Set<WorkUnit> InitialWorkUnit(HashMap<String, Integer> labelsId, Graph<VertexOString, OrthogonalEdge> KB){
		 Set<WorkUnit> ws  = new HashSet<WorkUnit>();
		 List<DFS> dfss = edgePattern(labelsId,KB);
		 for(DFS dfs : dfss){
			 String pId = dfs.toString();
			 int supp = patternNodeMatchesN.get(pId).size();
			 WorkUnit w = new WorkUnit(pId,"", supp, true, partitionId, true );
			 ws.add(w);
		 }
		 return ws;
	 }
	 
	public void IncrementalCompute(Set<WorkUnit> wsc){
	    Set<WorkUnit> ws  = new HashSet<WorkUnit>();
	    for(WorkUnit w : wsc){
	    	//for connected patterns
	    	if(w.isConnected){
	    		if(w.conditionId == null){
	    			//extend pattern,
	    		}
	    			
	    		}
	    	
	    		String pId = w.oriPatternId;
	    		String eId = w.edgeId;
	    		
	    		if(w.conditionId == null){
	    		    
	    	}
	    }
		
	}
}
}
	   

	 

	
