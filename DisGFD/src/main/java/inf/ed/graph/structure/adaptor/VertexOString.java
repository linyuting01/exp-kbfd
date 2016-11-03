package inf.ed.graph.structure.adaptor;

import inf.ed.gfd.util.KV;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.OrthogonalVertex;

import java.io.Serializable;

/**
 * Orthogonal Vertex with String label.
 * 
 * @author yecol
 *
 */
public class VertexOString extends OrthogonalVertex implements Serializable {

	private static final long serialVersionUID = 1L;
	int attr;
	String value;

	public VertexOString(int id) {
		this.id = id;
	}

	public VertexOString(VertexOString copy, boolean copyEdge) {
		this.id = copy.id;
		this.attr = copy.attr;
		if (copyEdge) {
			this.firstin = copy.firstin;
			this.firstout = copy.firstout;
		}
	}

	public VertexOString(String line) {
		String tmpt[] = line.split("\t");

		this.id = Integer.parseInt(tmpt[0].trim());

			this.attr =  Integer.parseInt(tmpt[1].trim());
			this.value = tmpt[2].trim();
		}
		
		
	

	public VertexOString(int id, int attr, String value ) {
		this.id = id;
		this.attr = attr;
		this.value = value;;
	}

	public VertexOString(int id, int attr, String value, OrthogonalEdge firstin, OrthogonalEdge firstout) {
		this.id = id;
		this.attr = attr;
		this.value = value;
		this.firstin = firstin;
		this.firstout = firstout;
	}

	public VertexOString(int vertexID, int i) {
		// TODO Auto-generated constructor stub
	}

	public VertexOString(int x, String val1) {
		// TODO Auto-generated constructor stub
		this.id = x;
		this.value = val1;
	}

	public int getAttr() {
		return this.attr;
	}

	public void setAttr(int attr) {
		this.attr = attr;
	}
	public String getValue(){
		return this.value;
	}
	public void setValue(String val) {
		this.value = val;;
	}

	@Override
	public int hashCode() {
		int result = String.valueOf(this.getID()).hashCode();
		result = 29 * result + attr + value.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object other) {
		final VertexOString v = (VertexOString) other;
		return v.getID() == this.getID();
	}

	public boolean match(Object other) {
		final VertexOString o = (VertexOString) other;
		return o.attr == this.attr;
	}

	@Override
	public String toString() {
		return "VOStr [ID=" + this.id + ", attr=" + this.attr + "]";
	}

	@Override
	public OrthogonalVertex copyWithoutEdge() {
		return new VertexOString(this, false);
	}
}
