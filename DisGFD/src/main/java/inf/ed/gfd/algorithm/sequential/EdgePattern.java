package inf.ed.gfd.algorithm.sequential;
import inf.ed.gfd.structure.DFS;
import inf.ed.gfd.structure.DFS;
import inf.ed.gfd.structure.GFD2;
import inf.ed.gfd.structure.GfdMsg;
import inf.ed.gfd.structure.GfdNode;
import inf.ed.gfd.structure.GfdTree;
import inf.ed.gfd.structure.LiterNode;
import inf.ed.gfd.structure.LiterTree;
import inf.ed.gfd.structure.SuppResult;
import inf.ed.gfd.structure.WorkUnit;
import inf.ed.gfd.util.Params;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.OrthogonalGraph;
import inf.ed.graph.structure.OrthogonalVertex;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgePattern {
	
	

	static Logger log = LogManager.getLogger(EdgePattern.class);
	  // here String denotes the pattern string P previous, N id the extended 
	  public HashMap<String, List<Int2IntMap> > patternNodeMatchesP =  new HashMap<String, List<Int2IntMap>>();
	  //the ith layer , now ;
	  public HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();
	  
	  
	//  public HashMap<String, List<Pair<Integer,Integer>>> edgePatternNodeMatch = new HashMap<String, List<Pair<Integer,Integer>>>();
	  
	  public GfdMsg gfdMsg = new GfdMsg();
	  
	  //HashMap<String,List<Int2IntMap>> boderMatch = new HashMap<String,List<Int2IntMap>> ();
	  
	//  public HashMap<String,IntSet> pivotPMatch  = new HashMap<String,IntSet>();
	  
	  IntSet borderNodes = new IntOpenHashSet();
	  
	  //the ith layer , now ;
	 // HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();

	  
	  
	  
	  
	  /**
	   *  edge pattern;
	   * @param KB
	   * @param nodeMatch
	   * @param edgeMatch
	   * @return
	   */
	// public HashMap<String, Integer> labelId = new HashMap<String, Integer>();//
	  
	 public List<DFS> edgePattern( Graph<VertexOString, OrthogonalEdge> KB, HashMap<String,IntSet> pivotPMatch, 
			 HashMap<String, List<Pair<Integer,Integer>>> edgePatternNodeMatch,  
			 HashMap<String, List<Int2IntMap> > patternNodeMatchesN, Set<String> dom){
	   OrthogonalVertex fNode, tNode;
	   VertexOString fVertex , tVertex;
	   List<DFS> DFS = new ArrayList<DFS>(); 
      // get all edge patterns and literals for nodes ;
	   for(OrthogonalEdge edge: KB.getAllEdges()){ 
	     //System.out.print("success");
	     
	     fNode = edge.from();
	     tNode = edge.to();
	     int fID = fNode.getID();
	     int tID = tNode.getID();
	     fVertex = KB.getVertex(fID);
	     tVertex = KB.getVertex(tID);
	     String fLabel = fVertex.getAttr();
	     String tLabel = tVertex.getAttr();
	     dom.add(fLabel);
	     dom.add(tLabel);
	     
	     int[] eLabel = edge.getAttr();
	     int elNum = edge.attrCount;
	     
	     Pair<String,Integer> f = new Pair<String,Integer>(fLabel,0);
    	 Pair<String,Integer> t = new Pair<String,Integer>(tLabel,0);
    	 if(fLabel.equals(tLabel)){
    		 t.y = 1;
    	 }
	   
	     for(int i = 0; i< elNum; i++){
	    	 
	        DFS code = new DFS( f, t, eLabel[i]);

	      
	         String pId = code.toString();
	         //log.debug(pId);
	         DFS.add(code);
	         
	         if(!pivotPMatch.containsKey(pId)){
	        	 pivotPMatch.put(pId, new IntOpenHashSet());  
	         }
	         pivotPMatch.get(pId).add(fID);
	         
	         if(!edgePatternNodeMatch.containsKey(pId)){
	        	 edgePatternNodeMatch.put(pId, new ArrayList<Pair<Integer,Integer>>()); 
	        	 edgePatternNodeMatch.get(pId).add(new Pair<Integer,Integer>(fID,tID)); 
	         }
	         if(! patternNodeMatchesN.containsKey(pId)){
	          	 patternNodeMatchesN.put(pId, new ArrayList<Int2IntMap>());  
	           }
	           
	           Int2IntMap e = new Int2IntOpenHashMap();
	           e.put(1, fID);
	           e.put(2, tID);
	           
	           
	           patternNodeMatchesN.get(pId).add(e);
	           /*
	           VertexOString f1 = new VertexOString(fID,fLabel);
		         VertexOString t1 = new VertexOString(tID,tLabel);
		         
		         if(!gfdMsg.transferingEdgeMatch.containsKey(pId)){
		        	 gfdMsg.transferingEdgeMatch.put(pId, new ArrayList<Pair<VertexOString,VertexOString>>());
		         }
		         gfdMsg.transferingEdgeMatch.get(pId).add(new Pair<VertexOString,VertexOString>(f1,t1));
		         */
	     }
	   }
	  
      
	return DFS;
 }
	         //Initialize matchesP;
	         /*
	         if(! patternNodeMatchesN.containsKey(pId)){
	        	 patternNodeMatchesN.put(pId, new ArrayList<Int2IntMap>());  
	         }
	         
	         Int2IntMap e = new Int2IntOpenHashMap();
	         e.put(fId, fID);
	         e.put(tId, tID);
	         
	         
	         patternNodeMatchesN.get(pId).add(e);
	        
	         
	         
	         VertexOString f1 = new VertexOString(fID,fLabel);
	         VertexOString t1 = new VertexOString(tID,tLabel);
	         
	         if(!gfdMsg.transferingEdgeMatch.containsKey(pId)){
	        	 gfdMsg.transferingEdgeMatch.put(pId, new ArrayList<Pair<VertexOString,VertexOString>>());
	         }
	         gfdMsg.transferingEdgeMatch.get(pId).add(new Pair<VertexOString,VertexOString>(f1,t1));
	         
	         */
	         
	         /*
	         //send to SC
	         if(fVertex.isBorder == true || tVertex.isBorder == true){
	        	 if(! boderMatch.containsKey(pId)){
	        		 boderMatch.put(pId, new ArrayList<Int2IntMap>());  
		         }
	        	 boderMatch.get(pId).add(e);   	 
	         } 
	         */    
	   
	   
	 
	   
	   
	// int partitionId;
	 
   
	 
	 //receive workunit wsc and create message w2c
	public static void IncrePattern(HashMap<String,List<WorkUnit>> wsc, 
			HashMap<String, List<Pair<Integer,Integer>>> edgePatternNodeMatch, 
			HashMap<String,IntSet> pivotPMatch,
			Graph<VertexOString, OrthogonalEdge> KB, 
			 HashMap<String, List<Int2IntMap> > patternNodeMatchesN,
			 HashMap<String, List<Int2IntMap> > patternNodeMatchesP){
		//pivotPMatch.clear();
		patternNodeMatchesP.clear();;
	    patternNodeMatchesP = (HashMap<String, List<Int2IntMap>>)patternNodeMatchesN.clone();
	    patternNodeMatchesN. clear();
	    for(Entry<String,List<WorkUnit>> entry1: wsc.entrySet()){
		    for(WorkUnit w : entry1.getValue()){
		    	IncPattern( w, edgePatternNodeMatch, pivotPMatch,KB,  
		    			patternNodeMatchesN,patternNodeMatchesP);
		    }
			   
	    }
}
public static void IncPattern(WorkUnit w,
			HashMap<String, List<Pair<Integer,Integer>>> edgePatternNodeMatch, 
			HashMap<String,IntSet> pivotPMatch,
			Graph<VertexOString, OrthogonalEdge> KB, 
			 HashMap<String, List<Int2IntMap> > patternNodeMatchesN,
			 HashMap<String, List<Int2IntMap> > patternNodeMatchesP){
	
	String ppId = w.oriPatternId;
	List<Int2IntMap> pmatches = patternNodeMatchesP.get(ppId);
	
	//for each match of previous pattern ppId
	for(Int2IntMap match : pmatches){
		//for each edge wait to added into ppId
		for(Entry<DFS, Pair<Integer,Integer>> entry : w.edgeIds.entrySet()){
			DFS dfs = entry.getKey();
			String opId = w.oriPatternId;
			Pair<Integer,Integer> pair = entry.getValue();
			
			IncPatterMatchEdge(match,opId,dfs, pair, edgePatternNodeMatch, 
					pivotPMatch,KB, patternNodeMatchesN);
		}	
		
		
	}
}

	public static void IncPatterMatchEdge(Int2IntMap match, String opId,DFS dfs, Pair<Integer,Integer> pair,
			HashMap<String, List<Pair<Integer,Integer>>> edgePatternNodeMatch, 
			HashMap<String,IntSet> pivotPMatch,
			Graph<VertexOString, OrthogonalEdge> KB, 
			 HashMap<String, List<Int2IntMap> > patternNodeMatchesN){
//for each edge wait to added into ppId
				
					
					String edgeId1 = dfs.toString();
					String pId = opId + edgeId1;
					
					String edgeId = dfs.findDFS().toString();	
					List<Pair<Integer,Integer>> pairL = edgePatternNodeMatch.get(edgeId);
					
				
					//edge (fId,tId,eLabel)
					int fId = pair.x;
					int tId = pair.y;
					int eLabel = dfs.eLabel;
					
					if(match.containsKey(fId)){//add the node AB A is in ppId
						if(match.containsKey(tId)){// add AB AB is in ppId
							Pair<Integer,Integer> p = new Pair<Integer,Integer>(match.get(fId),match.get(tId));
							if(pairL.contains(p)){
								if(matchKB(p.x,p.y,eLabel,KB)){
									addMatch(pivotPMatch,match, pId, fId, tId, 0,0, patternNodeMatchesN);
								}
							}
						}
						else{
							for(Pair<Integer,Integer> p: pairL){
								if(p.x == match.get(fId)){
									if(matchKB(p.x,p.y,eLabel,KB)){
										addMatch(pivotPMatch,match, pId, fId, tId, 1,(int)p.y, patternNodeMatchesN);
									}
									
									
								}
							}
						}
					}
					if(match.containsKey(tId)){
						for(Pair<Integer,Integer> p: pairL){
							if(p.y == match.get(fId)){
								if(matchKB(p.x,p.y,eLabel,KB)){
									addMatch(pivotPMatch,match, pId, fId, tId, 2,(int)p.x,patternNodeMatchesN);
								}
							}
						}
					}
	
	}




	
	public static void addMatch(HashMap<String,IntSet>  pivotPMatch, Int2IntMap match, String pId, int fId, int tId, int flag, int x,
					 HashMap<String, List<Int2IntMap> > patternNodeMatchesN){
		
		Int2IntMap tmpt = new Int2IntOpenHashMap(match);
		if(flag == 1){
			tmpt.put(tId,x);
		}
		if(flag==2)
		{
		    tmpt.put(fId, x);
		}
		if(!patternNodeMatchesN.containsKey(pId)){
			patternNodeMatchesN.put(pId, new ArrayList<Int2IntMap>());
		}
		patternNodeMatchesN.get(pId).add(tmpt);
		if(!pivotPMatch.containsKey(pId)){
			pivotPMatch.put(pId, new IntOpenHashSet());
		}
		pivotPMatch.get(pId).add(tmpt.get(1));
	}
	
	public static boolean matchKB(int fId, int tId, int elabel,Graph<VertexOString, OrthogonalEdge> KB){
		OrthogonalEdge e = KB.getEdge(fId, tId);
		int[] attrs= e.getAttr();
		for(int m: attrs){
			if(m == elabel) {
			 return true;
			}			
		}
		return false;		
	
	}
	
	
/*
		
	public static void main(String args[]) {
		
		
		Set<WorkUnit> ws = new HashSet<WorkUnit>();
		List<Int2IntMap> l = new ArrayList<Int2IntMap>();
		Int2IntMap m = new Int2IntOpenHashMap();
		HashMap<Integer,String> attr_Map = new HashMap<Integer,String>();
		m.put(1, 2);
		m.put(2, 1);
		m.put(3, 3);
		m.put(4, 4);
		l.add(m);
		Int2IntMap n = new Int2IntOpenHashMap(m);
		n.put(4, 2);
		l.add(n);
		
		EdgePattern eP = new EdgePattern();
		eP.KB.loadGraphFromVEFile("data/test", true);
		Set<String> literals = new HashSet<String>();
		eP.getLabelIds(eP.labelId,literals, eP.KB);
		List<DFS> edgePattern = eP.edgePattern(eP.labelId, eP.KB);
		log.debug(eP.KB.edgeSize() +"success");
		for(Entry<String, Integer> entry :eP.labelId.entrySet()){
			attr_Map.put(entry.getValue(), entry.getKey());
		}
		
		GfdTree tree = new GfdTree();
		tree.initialExtend(edgePattern,attr_Map);
		
		//eP.IncrePattern(ws);
		for(GfdNode t: tree.getRoot().children){
			t.ltree = new LiterTree(t);
			t.ltree.extendNode(literals, t.ltree.getRoot());
			for(LiterNode nl : t.ltree.getRoot().children){
				t.ltree.extendNode(literals, nl);
			}
			
		}
		
		
		
		
		
		log.debug("success");
		
		
		
		
		//Set<WorkUnitC2WEp> wsc = new HashSet<WorkUnitC2WEp>();
		
	}
	*/
	
	
	
	/*******************************************************************
	 * Sc : BorderNode, Crossing edge. 
	 * in preprocee, add the croccsing edge to each partition graph.
	 * recored the border node in each worker. 
	 * If a pattern match has the boder node, then transfering border node's match to SC
	 */
	
	//for worker
	
	
	
	//public void JoinPEdge(GfdNode g, DFS edgeId){
		//plan1 suppose that g has the matches of border patterns. from combination. 
		//update delete the matches when extend from g;
		//If AB has boder node match , then return bmatch(AB) to SC;//just send one time;
		// need workload balance; 
		
		
		
	//}
	
	
	
	
	
	
	/*
	public void writeToFile(String filename, List<GFD2> gfds) {

		System.out.println("Write final result file to:" + filename);

		PrintWriter writer;
		try {
			writer = new PrintWriter(filename);
			for (GFD2 gfd : gfds) {
				if(gfd.isConnected()){
					writer.println(gfd.getPattern().display()  + gfd.condition.toString());
				}
				writer.println(gfd.getPatterns());
			}
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	*/
}
	/*for(Entry<DFS, Pair<Integer,Integer>> entry : edgeIds.entrySet()){
			IntSet pivotMatch = new IntOpenHashSet();
			String edgeId1 = entry.getKey().toString();
			String pId = ppId + edgeId1.toString();
			String edgeId = entry.getKey().findDFS().toString();	
			List<Pair<Integer,Integer>> pairL = edgePatternNodeMatch.get(edgeId);
			
		
			//edge (fId,tId,eLabel)
			int fId = entry.getValue().x;
			int tId = entry.getValue().y;
			int eLabel = entry.getKey().eLabel;
			
			if(match.containsKey(fId)){//add the node AB A is in ppId
				if(match.containsKey(tId)){// add AB AB is in ppId
					Pair<Integer,Integer> p = new Pair<Integer,Integer>(match.get(fId),match.get(tId));
					if(pairL.contains(p)){
						if(matchKB(p.x,p.y,eLabel,KB)){
							addMatch(pivotMatch,match, pId, fId, tId, 0,0, patternNodeMatchesN);
						}
					}
				}
				else{
					for(Pair<Integer,Integer> p: pairL){
						if(p.x == match.get(fId)){
							if(matchKB(p.x,p.y,eLabel,KB)){
								addMatch(pivotMatch,match, pId, fId, tId, 1,(int)p.y, patternNodeMatchesN);
							}
							
							
						}
					}
				}
			}
			if(match.containsKey(tId)){
				for(Pair<Integer,Integer> p: pairL){
					if(p.y == match.get(fId)){
						if(matchKB(p.x,p.y,eLabel,KB)){
							addMatch(pivotMatch,match, pId, fId, tId, 2,(int)p.x,patternNodeMatchesN);
						}
					}
				}
			}
			
//				  SuppResult w1 = new SuppResult(pId,pivotMatch.size(),partitionId);
//				  w2c.add(w1);
			
			if(!pivotPMatch.containsKey(pId)){
				pivotPMatch.put(pId, pivotMatch);
			}else{
				pivotMatch.retainAll(pivotPMatch.get(pId));
			}

			
		}
		*/   

	 

	
