/**
 * 
 */
package inf.ed.gfd.structure;

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
public class GfdNode {

	/**
	 * 
	 */
	
	static Logger log = LogManager.getLogger(GfdNode.class);
	
	public Graph<VertexString, TypedEdge> pattern;
	public LiterTree ltree; 
	public Vector<DFS> patternCode;
	public HashMap<Pair<Integer>,Integer> nodeSet;
	
	public GfdNode parent; 
	public List<GfdNode> children; 
	public String key;
	//private GfdNode rNeighbor;
	
	
	public WorkUnitC2WEp wC2Wp;

	
	
	public GfdNode(){
		this.pattern = new SimpleGraph<VertexString, TypedEdge>(VertexString.class,
				TypedEdge.class);
		this.ltree = null;
		this.parent = null;
		this.children = new ArrayList<GfdNode>();
		this.nodeSet = new HashMap<Pair<Integer>,Integer>();
		this.patternCode = new Vector<DFS>();
		this.wC2Wp = new WorkUnitC2WEp();
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

}
