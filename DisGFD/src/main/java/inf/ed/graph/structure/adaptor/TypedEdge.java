package inf.ed.graph.structure.adaptor;

import inf.ed.gfd.util.KV;
import inf.ed.graph.structure.Edge;
import inf.ed.graph.structure.Vertex;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.io.Serializable;

public class TypedEdge implements Edge, Serializable {

	/**
	 * Directed edge.
	 */
	private static final long serialVersionUID = 1L;
	Vertex from;
	Vertex to;
	private IntSet attrs = new IntOpenHashSet();
	public int attrCount = 0;

	public TypedEdge(Object from, Object to) {
		this.from = (Vertex) from;
		this.to = (Vertex) to;
	}

	public TypedEdge(Vertex from, Vertex to) {
		this.from = from;
		this.to = to;
	}

	public boolean match(Object o) {
		System.out.println("need to check");
		return true;
	}

	public Vertex from() {
		return from;
	}

	public Vertex to() {
		return to;
	}

	public IntSet getAttr() {
		return this.attrs;
	}

	public String getAttrString() {
		String s = "[";
		for (int i: attrs) {
			s += i+ ",";
		}
		return s + "]";
	}

	public void setAttr(int attr) {
		 
			attrs.add(attr);
			attrCount++;
		
	}

	@Override
	public String toString() {
		return "dEdge [f=" + from.getID() + ", t=" + to.getID() + ", attr=" + getAttrString()
				+ " ]";
	}
	public String tofile(){
		return "e\t"+from.getID() + "\t"+ to.getID() +  "\t"  + getAttrString() +"\n";
	}

}