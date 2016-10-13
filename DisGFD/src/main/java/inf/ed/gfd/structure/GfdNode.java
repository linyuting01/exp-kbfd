/**
 * 
 */
package inf.ed.gfd.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.util.ColoneUtils;
import inf.ed.gfd.util.Params;
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
	private GfdNode rNeighbor;
	
	
	public GfdNode(){
		this.setPattern(new SimpleGraph<VertexString, TypedEdge>(VertexString.class,
				TypedEdge.class));
		this.ltree = null;
		this.parent = new GfdNode();
		this.children = new ArrayList<GfdNode>();
		this.rNeighbor = null;
	}

	public Graph<VertexString, TypedEdge> getPattern() {
		return pattern;
	}

	public void setPattern(Graph<VertexString, TypedEdge> pattern) {
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
	
	
	public GfdNode getRNeighbor(){
		return this.rNeighbor;
	}
	public void setRNeighbor(GfdNode rNeighbor){
		this.rNeighbor = rNeighbor;
	}
	public Vector<DFS> getPatternCode(){
		return this.patternCode;
	}
	public void setPatternCode(Vector<DFS> patternCode){
		this.patternCode = patternCode;
	}
	
	/*
	 * 
	 */
   public void extendNode(List<DFS> edgePattern, HashMap<Integer,String> attr_Map){
		//extend node that occurs in the children of the pattern node (> currnt node)
			GfdNode tmpt = this.rNeighbor;
			while(tmpt != null){
				Vector<DFS> dfsv = tmpt.getPatternCode();
				GfdNode g = this.addNode(attr_Map, dfsv.get(dfsv.size()-1));
				if(tmpt.getRNeighbor() != null){
					g.rNeighbor = tmpt.rNeighbor;
				}
				tmpt = tmpt.getRNeighbor();
			}
		// extend the last tnode ;
			DFS dfs = this.patternCode.get(this.patternCode.size()-1);
			if(dfs.fLabel.compareTo(dfs.tLabel)< 0){
				
				Triple t1 = new Triple(0,0,0);
				Triple t2 = new Triple(dfs.tLabel.)
				DFS dfs1 = new DFS(dfs.tLabel, t1, 0);
				Triple t2 = new Triple(Integer.MAX_VALUE,Integer.MAX_VALUE,Params.var_K);
				DFS dfs2 = new DFS(t2,dfs.tLabel,0);
				
				
			}
			
	}
   
   public void extendDFS(DFS dfs, List<DFS> edgePattern){
	   
	   // two situations;
	   //(1) introduce new node : right neighbours  + new node extend
	   //(i) new node 's label has occured.
	   //(2) no new node : right neighbours
	   int attr;
	   if(dfs.fLabel.compareTo(dfs.tLabel)< 0){
		   
		   if(dfs.tLabel.getSecond() != 0){
			   attr = dfs.tLabel.getSecond();
		   }
		   else{
			   attr = dfs.tLabel.getFirst();
		   }
		   //notice: for the replicated label we can process it in advance: 
		   //compute 
	   }
   }
				
				
			

	public GfdNode addNode(HashMap<Integer,String> attr_Map, DFS dfs){
		GfdNode g = new GfdNode();
		g.setParent(this);
		g.setPattern(ColoneUtils.clone((SimpleGraph)this.pattern));
		g.setPatternCode(ColoneUtils.clone(this.patternCode));
		this.children.add(g);
		//how to create new node for pattern;
		Triple e1 = dfs.fLabel;
		Triple e2 = dfs.tLabel;
		if(!this.nodeSet.containsKey(e1)){
			String attr1 = getAttr(attr_Map, dfs.fLabel);
			VertexString vertex1 = new VertexString(this.pattern.vertexSize()+1, attr1);
			g.getPattern().addVertex(vertex1);
		}
		if(!this.nodeSet.containsKey(e2)){
			String attr2 = getAttr(attr_Map, dfs.fLabel);
			VertexString vertex2 = new VertexString(this.pattern.vertexSize()+1, attr2);
			g.getPattern().addVertex(vertex2);
		}
		int fId = g.nodeSet.get(e1);
		int tId = g.nodeSet.get(e2);
		g.getPattern().addEdge(g.getPattern().allVertices().get(fId), 
				g.getPattern().allVertices().get(tId));
		return g;
	}
	
	
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
		    
			 Vector <Integer> a = new Vector<Integer>();
			 a.add(1);
			 a.add(2);
			 int c = a.size();
			 log.debug(c +" " + a.get(c-1));
		  }
		    

}
