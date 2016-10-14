/**
 * 
 */
package inf.ed.gfd.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.util.ColoneUtils;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.SimpleGraph;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;

/**
 * @author v1xliu33
 *
 */
public class GfdNode {

	/**
	 * 
	 */

	static Logger log = LogManager.getLogger(DFS.class);
	
	private Graph<VertexString, TypedEdge> pattern;
	private LiterTree ltree; 
	private Vector<DFS> patternCode;
	public HashMap<Triple,Integer> nodeSet;
	
	private GfdNode parent; 
	private List<GfdNode> children; 
	//private GfdNode rNeighbor;

	
	
	public GfdNode(){
		this.pattern = new SimpleGraph<VertexString, TypedEdge>(VertexString.class,
				TypedEdge.class);
		this.ltree = null;
		this.parent = null;
		this.children = new ArrayList<GfdNode>();
		this.nodeSet = new HashMap<Triple,Integer>();
		this.patternCode = new Vector<DFS>();
		//this.rNeighbor = null;
	}

	public Graph<VertexString, TypedEdge> getPattern() {
		return pattern;
	}

	public void setPattern(SimpleGraph<VertexString, TypedEdge> pattern) {
		this.pattern = pattern;
	}

	public LiterTree getLtree() {
		return ltree;
	}

	public void setLtree(LiterTree ltree) {
		this.ltree = ltree;
	}
	
	

	public GfdNode getParent() {
		return parent;
	}

	public void setParent(GfdNode parent) {
		this.parent = parent;
	}

	public List<GfdNode> getChildren() {
		return children;
	}

	public void setChildren(List<GfdNode> children) {
		this.children = children;
	}
	
	public void setPattern(Graph<VertexString, TypedEdge>  pattern){
		this.pattern = pattern;
	}
	

	public Vector<DFS> getPatternCode(){
		return this.patternCode;
	}
	public void setPatternCode(Vector<DFS> patternCode){
		this.patternCode = patternCode;
	}
	
	
	/*
	 * when this is root;	edgePattern has order		
	 */
	  public void initialExtend(List<DFS> edgePattern,HashMap<Integer,String> attr_Map ){
		  for(DFS dfs: edgePattern){
			    this.addNode(attr_Map, dfs);
		   }
		  //log.debug(this.children.size());
		  // extend root's children
		  for(GfdNode t: this.children){
			 // log.debug(t.getPatternCode().size());
			  DFS tdfs = t.getPatternCode().get(t.getPatternCode().size()-1);
			 // log.debug(tdfs.toString());
			  int index = edgePattern.indexOf(tdfs);
			 // log.debug(index);
			  index++;
			  //for AA_1 , add AA_2, A_1A, and A{C},{C}A  and A_1{C} and {C}A_1
			  if(isEqualL(tdfs)){
				  extendSpecial(true,tdfs,attr_Map,t);
				  for(; index < (edgePattern.size()-1); index++){
					  if(edgePattern.get(index).fLabel.getFirst() == tdfs.fLabel.getFirst()){
						  DFS dfs1 = new DFS(tdfs.tLabel,edgePattern.get(index).fLabel,edgePattern.get(index).eLabel);
						  t.addNode(attr_Map, dfs1);
						  t.addNode(attr_Map, edgePattern.get(index));
					  }
					  if(edgePattern.get(index).tLabel.getFirst() == tdfs.fLabel.getFirst()){
						  DFS dfs2 = new DFS(edgePattern.get(index).fLabel,tdfs.tLabel,edgePattern.get(index).eLabel);
						 t.addNode(attr_Map, dfs2);
						t.addNode(attr_Map, edgePattern.get(index));	  
					  }	  
				  }
			  }
			  //for AB: add AB_1 and A_1B and A{C},{C}A, B{D} and {D}B who are larger than AB.
			  else{
				  extendSpecial(false,tdfs,attr_Map,t);
				  for(; index < (edgePattern.size()-1); index++){  
					  if(edgePattern.get(index).fLabel.getFirst() == tdfs.fLabel.getFirst() || 
							  edgePattern.get(index).tLabel.getFirst() == tdfs.fLabel.getFirst() ||
									  edgePattern.get(index).fLabel.getFirst() == tdfs.tLabel.getFirst() || 
											  edgePattern.get(index).tLabel.getFirst() == tdfs.fLabel.getFirst()){
						  	t.addNode(attr_Map, edgePattern.get(index));
					  }		  
				  }
				  
			  }
			  log.debug(t.getChildren().size());//revise
			  
		  }
		  
	  }
	 
	
   public void extendNode(GfdNode root, List<DFS> edgePattern, HashMap<Integer,String> attr_Map){
	   
		
		DFS dfs = this.patternCode.get(this.patternCode.size()-1);
		log.debug(dfs.fLabel + " " + dfs.eLabel + " "+dfs.tLabel);
		int index = findIndex(dfs, edgePattern);
		DFS dfsn = edgePattern.get(index);
		List<GfdNode> gfds = root.getChildren().get(index).getChildren();
		if(gfds!=null){
			if(isEqualL(dfs)){
				extendSpecial(true, dfs,attr_Map, this);	
			}
			else{
				extendSpecial(false, dfs,attr_Map, this);	
			}
			extendGeneral(gfds,dfsn, dfs,attr_Map, this);	
		}
   }
   /*
    * extend AA_1 by AA_2, A_1A and AB by A_1B and AB_1
    */
    public void extendSpecial( boolean flag, DFS dfs,HashMap<Integer,String> attr_Map, GfdNode g){
        
 	   Triple b1 = addTriple(dfs.tLabel);
 	   Triple b2 = addTriple(dfs.fLabel);
 	   int i= dfs.fLabel.getThird();
 	   int j= dfs.tLabel.getThird();
 	   
 	   if(flag){//A_mA_n
 		   if(dfs.fLabel.compareTo(dfs.tLabel)<0){//m<n
 			   DFS dfs1 = new DFS(dfs.fLabel,b1,dfs.eLabel);
 			   DFS dfs2 = new DFS(dfs.tLabel,dfs.fLabel,dfs.eLabel);
 			   g.addNode(attr_Map, dfs1);
 			   g.addNode(attr_Map, dfs2);
 			   //A_1A_3 ; add A_1A_4,A_1A_2, A_2A_3, and A_3,A_1
 			   if(i<j-1){
 				   for(i=i+1;i<j;i++){
 					   Triple tp = new Triple(dfs.fLabel);
 					   tp.setThird(i);
 					   DFS dfs3 = new DFS(dfs.fLabel,tp,dfs.eLabel);
 					   DFS dfs4 = new DFS(tp,dfs.tLabel,dfs.eLabel);
 					   g.addNode(attr_Map, dfs3);
 					   g.addNode(attr_Map, dfs4);
 					} 
 			   }
 		   }
 		   else{//A_3A_1, A_3A_2, A_2A_1, A_3,A_4  ---m>n
 			   DFS dfs1 = new DFS(dfs.fLabel,b2,dfs.eLabel);
 			   g.addNode(attr_Map, dfs1);
 			   if(j<i-1){
 				   for(i=i-1;i>j;i--){
 					   Triple tp = new Triple(dfs.fLabel);
 					   tp.setThird(i);
 					   DFS dfs3 = new DFS(tp,dfs.tLabel,dfs.eLabel);
 					   DFS dfs4 = new DFS(dfs.fLabel,tp,dfs.eLabel);
 					   g.addNode(attr_Map, dfs3);
 					   g.addNode(attr_Map, dfs4);
 					} 
 			   }
 			   
 		   }
 	   }
 	   else{//A_mB_n 
 		   DFS dfs1 = new DFS(dfs.fLabel,b1,dfs.eLabel);
 		   DFS dfs2 = new DFS(b2,dfs.tLabel,dfs.eLabel);
 		   g.addNode(attr_Map, dfs1);
 		   g.addNode(attr_Map, dfs2);
 	   }  
    }
    
	
   /*
	 * @flag : AB or BA 
	 * @gfds : root->children->children;
	 * @dfsn : dsf in edgePattern; 
	 * @dfs  : t.dfs
	 * @attr_Map : attr ID to String
	 */
	public void extendGeneral(List<GfdNode> gfds, DFS dfsn, DFS dfs,HashMap<Integer,String> attr_Map, GfdNode t){
			for(GfdNode g:gfds){
				DFS dfs1 = g.getPatternCode().get(1);

				
				if(!isEqualL(dfs1) && !isEqualL(dfs1,dfsn)){//AB : AX	
					if(dfs1.fLabel.equals(dfsn.fLabel)){
						DFS dfs2 = new DFS(dfs.fLabel,dfs1.tLabel,dfs1.eLabel);
						t.addNode(attr_Map, dfs2);
					}
					if(dfs1.tLabel.equals(dfsn.fLabel)){//XA
						DFS dfs2 = new DFS(dfs1.fLabel,dfs.tLabel,dfs1.eLabel);
						t.addNode(attr_Map, dfs2);
					}
					if(dfs1.tLabel.equals(dfsn.tLabel)){//XB
						DFS dfs2 = new DFS(dfs1.fLabel,dfs.tLabel,dfs1.eLabel);
						t.addNode(attr_Map, dfs2);
					}
					if(dfs.fLabel.compareTo(dfs.tLabel)<0){
						if(dfs1.fLabel.equals(dfsn.tLabel)){//BX
							DFS dfs2 = new DFS(dfs.tLabel,dfs1.tLabel,dfs1.eLabel);
							t.addNode(attr_Map, dfs2);
						}
					}
			   }
		   }	
			
	}
	
   public DFS findDFS(DFS dfs){
	   Triple t1 = new Triple(0,0,0);
	   Triple t2 = new Triple(0,0,0);
	   
	   if(dfs.fLabel.getSecond() != 0){
		   t1.setFirst(dfs.fLabel.getSecond());
	   }
	   else{
		   t1.setFirst(dfs.fLabel.getFirst());
	   }
	   
	   if(dfs.tLabel.getSecond() != 0){
		   t2.setFirst(dfs.tLabel.getSecond());
	   }
	   else{
		   t2.setFirst(dfs.tLabel.getFirst());
	   }
	   if(isEqualL(dfs)){
		  t2.setSecond(t2.getFirst());
		  t2.setThird(1);
	   }
	   DFS dfsn = new DFS(t1,t2,dfs.eLabel);
	   return dfsn;
   }
   /* the index is decided by edgePattern.
    * 
    */
   public int findIndex(DFS dfs, List<DFS> edgePattern ){
	   
	   DFS dfsn =  findDFS(dfs);
	   
	   return edgePattern.indexOf(dfsn);
	   
   }
   
   /*
    * if it is AA_1
    */
   public boolean isEqualL(DFS dfs){
	   int fFirst = dfs.fLabel.getFirst();
	   int fSecond = dfs.fLabel.getSecond();
	   int tSecond = dfs.tLabel.getSecond();
	   if(fSecond == 0 && tSecond == fFirst){
		   return true;
	   }
	   if(fSecond!=0 &&fSecond == tSecond){
		   return true;
	   }
	   return false;
   }
   
   public boolean isEqualL(DFS dfs1, DFS dfs2){
	   DFS dfsn1 =  findDFS(dfs1);
	   DFS dfsn2 =  findDFS(dfs2);
	   if(dfsn1.equals(dfsn2)){
		   return true;
	   } 
	   return false;
   }
   

   /*
    * 
    */
  
   /*
    * for AA to get AA_1
    */
   public Triple addTriple(Triple a){
	   
	   if(a.getSecond() == 0){
		   Triple b = new Triple(a.getFirst(),a.getFirst(),1);
		   return b;
	   }
	   else{
		   Triple b = new Triple(a.getFirst(),a.getSecond(),a.getThird()+1);
		   return b;
	   }
   }
   
			
   /**
    * 
    * @param attr_Map attr id: attr 
    * @param dfs
    * @return
    */

	public GfdNode addNode(HashMap<Integer,String> attr_Map, DFS dfs){
		GfdNode g = new GfdNode();
		g.setParent(this);
		g.setPattern(ColoneUtils.clone((SimpleGraph<VertexString, TypedEdge>)this.pattern));
		g.setPatternCode(ColoneUtils.clone(this.patternCode));
		this.children.add(g);
		//how to create new node for pattern;
		Triple e1 = dfs.fLabel;
		Triple e2 = dfs.tLabel;
		g.getPatternCode().add(dfs);
		if(!g.nodeSet.containsKey(e1)){
			String attr1 = getAttr(attr_Map, dfs.fLabel);
			VertexString vertex1 = new VertexString(g.pattern.vertexSize()+1, attr1);
			g.getPattern().addVertex(vertex1);
			//log.debug(g.getPattern().vertexSize());
			g.nodeSet.put(e1,g.pattern.vertexSize());
		}
		if(!g.nodeSet.containsKey(e2)){
			String attr2 = getAttr(attr_Map, dfs.fLabel);
			VertexString vertex2 = new VertexString(g.pattern.vertexSize()+1, attr2);
			g.getPattern().addVertex(vertex2);
			g.nodeSet.put(e2,g.pattern.vertexSize());
		}
		int fId = g.nodeSet.get(e1);
		int tId = g.nodeSet.get(e2);
		log.debug(fId+ " "+ tId);
		//log.debug(g.getPattern().allVertices().size());
		/////////////////////////////////////////////////////notice need revise
		//if(fId!=tId){
		g.getPattern().addEdge(g.getPattern().allVertices().get(fId), 
				g.getPattern().allVertices().get(tId));
		
		return g;
	}
	
	/*
	 * get attr from attr Id
	 */
	public String getAttr(HashMap<Integer,String> attr_Map, Triple node){
		int id;
		if(node.getSecond() == 0){
			id = node.getFirst();
		}
		else{
			id = node.getSecond();
		}
		return attr_Map.get(id);
	}
		
	

	public static void main(String args[]) {  
		HashMap<Integer, String> attr_Map = new HashMap<Integer, String>();
		attr_Map.put(1, "a");
		attr_Map.put(2, "b");
		attr_Map.put(3, "c");
		List<DFS> edgePattern = new ArrayList<DFS>();
		Triple t1 = new Triple(1,0,0);
		Triple t2 = new Triple(2,0,0);
		Triple t3 = new Triple(3,0,0);
		
		DFS d1 = new DFS(t1,t2,1);
		DFS d2 = new DFS(t1,t2,2);
		DFS d3 = new DFS(t1,t3,2);
		DFS d4 = new DFS(t2,t3,1);
		DFS d5 = new DFS(t3,t1,1);
		DFS d6 = new DFS(t3,t1,2);
		edgePattern.add(d1);
		edgePattern.add(d2);
		edgePattern.add(d3);
		edgePattern.add(d4);
		edgePattern.add(d5);
		edgePattern.add(d6);
		
		GfdNode root = new GfdNode();
		root.initialExtend(edgePattern,attr_Map);
		for(GfdNode g1: root.getChildren()){
			for(GfdNode g:g1.getChildren()){
				g.extendNode(root, edgePattern, attr_Map);
			}
		}
	
		log.debug("sucess");
		
		
		
		
		    
	}
		    

}
