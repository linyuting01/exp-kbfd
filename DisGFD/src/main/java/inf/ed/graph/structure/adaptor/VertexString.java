package inf.ed.graph.structure.adaptor;

import inf.ed.gfd.util.KV;
import inf.ed.graph.structure.Vertex;

import java.io.Serializable;

public class VertexString implements Vertex, Serializable {

	private static final long serialVersionUID = 1L;
	int ID;
	int attr;

	public VertexString(int ID, int attr) {
		this.ID = ID;
		this.attr = attr;
	}

	public VertexString(String line) {
		if (line.startsWith("v")) {
			String[] eles = line.split("\t");
			this.ID = Integer.parseInt(eles[1].trim());
			if (eles.length == 3) {
				this.attr = Integer.parseInt(eles[2].trim());
			} else {
				this.attr = KV.ANY;
			}
		}
	}

	public int getID() {
		return this.ID;
	}

	public int getAttr() {
		return attr;
	}

	public boolean match(Object other) {
		if (this.attr == KV.ANY) {
			return true;
		} else if (other instanceof VertexOString) {
			VertexOString ov = (VertexOString) other;
			return this.attr == ov.getAttr();
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = String.valueOf(this.getID()).hashCode();
		result = 29 * result + String.valueOf(attr).hashCode();
		return result;
	}

	@Override
	public boolean equals(Object other) {
		final VertexString v = (VertexString) other;
		return v.getID() == this.getID();
	}

	@Override
	public String toString() {
		return "VertexStringL [ID=" + ID + ", attr=" + attr + "]";
	}
	
	public String tofile(){
		return "v\t"+ ID+"\t"+attr+"\n";
	}

}
