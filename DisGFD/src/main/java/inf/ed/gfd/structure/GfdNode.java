/**
 * 
 */
package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.SimpleGraph;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;

/**
 * @author v1xliu33
 *
 */
public class GfdNode implements Comparable<GfdNode> {

	/**
	 * 
	 */
	
	static Logger log = LogManager.getLogger(GfdNode.class);
	
	//public Graph<VertexString, TypedEdge> pattern;
	public LiterTree ltree; 
	//public Vector<DFS> patternCode;
	
	//do what? 
	public HashMap<Pair<String,Integer>,Integer> nodeSet;
	
	public GfdNode parent; 
	public List<GfdNode> children; 
	public String key;
	public DFS edgePattern;
	public String newAttr;
	public HashMap<String,Integer> attrs;
	public Set<DFS> extendDfss;
	public int pos;
	//public List<String> disConnectedP;
	//public int nodeNUm = 0;
	//private GfdNode rNeighbor;
	
	public boolean isConnected;
	//public boolean isNegative;
	public WorkUnit wC2Wp;
	public double supp;

	public int nodeNum = 0;
	
	
	public HashMap<Integer, Set<String>> patternDom; 

 /**
  * for disconnected pattern 
  * GfdNode keep : disConnectedP ;key; isConnected;
  */

	
	
	public GfdNode(){
		//this.pattern = new SimpleGraph<VertexString, TypedEdge>(VertexString.class,
			//	TypedEdge.class);
		this.ltree = new LiterTree();
		//this.parent =;
		this.children = new ArrayList<GfdNode>();
		this.nodeSet = new HashMap<Pair<String,Integer>,Integer>();
		//this.patternCode = new Vector<DFS>();
		this.wC2Wp = new WorkUnit();
		this.edgePattern = new DFS();
		this.patternDom = new HashMap<Integer, Set<String>>();
		this.attrs = new HashMap<String,Integer>();
		this.extendDfss = new HashSet<DFS>();
		
		//this.rNeighbor = null;
	}

	//public Graph<VertexString, TypedEdge> getPattern() {
	//	return pattern;
	//}

	//public void setPattern(SimpleGraph<VertexString, TypedEdge> pattern) {
	//	this.pattern = pattern;
	//}
 

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
	
	//public void setPattern(Graph<VertexString, TypedEdge>  pattern){
	//	this.pattern = pattern;
	//}
	

	//public Vector<DFS> getPatternCode(){
	//	return this.patternCode;
//	}
	//public void setPatternCode(Vector<DFS> patternCode){
	//	this.patternCode = patternCode;
	//}	
	public void getDom(){
		for(LiterNode t:this.ltree.getRoot().children){
			if(t.supp > 0){
				if(t.dependency.isLiteral){
					Pair<Integer,String> p = t.dependency.YEqualsLiteral;
					if(!patternDom.containsKey(p.x)){
						patternDom.put(p.x, new HashSet<String>());
					}
					patternDom.get(p.x).add(p.y);
				}
			}
		}
	}

	@Override
	public int compareTo(GfdNode arg0) {
		// TODO Auto-generated method stub
		
		return this.edgePattern.compareTo(arg0.edgePattern);
	}

}
