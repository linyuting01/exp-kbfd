package inf.ed.gfd.structure;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;

public class GfdTree {
	
    private GfdNode root;
    
    public GfdTree(GfdNode root){
        this.setRoot(new GfdNode());
    }

	public GfdNode getRoot() {
		return root;
	}

	public void setRoot(GfdNode root) {
		this.root = root;
	}
	
	

}
