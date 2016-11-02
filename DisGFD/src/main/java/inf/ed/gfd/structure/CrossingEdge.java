package inf.ed.gfd.structure;

import java.io.Serializable;

public class CrossingEdge implements Serializable {

	// TODO: add source and target vertex label
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int source;
	public int target;
	public int edgetype;
	public int sourceAttr;
	public int targetAttr;


	public String sourceLabel;
	public String targetLabel;

	public CrossingEdge(int source, int edgetype, int target) {
		this.source = source;
		this.edgetype = edgetype;
		this.target = target;
	}
	
	public CrossingEdge(int source, int edgetype, int target, int attr, String val) {
		this.source = source;
		this.edgetype = edgetype;
		this.target = target;
		this.targetAttr = attr;
		this.targetLabel = val;
	}

	public String toString() {
		return source + "-" + edgetype + "->" + target;
	}
}
