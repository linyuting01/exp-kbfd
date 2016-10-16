package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.SimpleGraph;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class LiterNode implements Serializable {
	
	private static final long serialVersionUID = 1L;

	static Logger log = LogManager.getLogger(LiterNode.class);
	public Condition dependency;
	public LiterNode parent; 
	public List<LiterNode>  children;
	public String key;
	
	public LiterNode() {
		this.children = new ArrayList<LiterNode>();
		this.dependency = new Condition();
		// TODO Auto-generated constructor stub
	}

	public Condition getDependency() {
		return dependency;
	}

	public void setDependency(Condition dependency) {
		this.dependency = dependency;
	}

	public LiterNode getParent() {
		return parent;
	}

	public void setParent(LiterNode parent) {
		this.parent = parent;
	}

	public List<LiterNode> getChildren() {
		return children;
	}

	public void setChildren(List<LiterNode> children) {
		this.children = children;
	}
	
	
}
