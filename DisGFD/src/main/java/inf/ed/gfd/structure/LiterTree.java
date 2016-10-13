/**
 * 
 */
package inf.ed.gfd.structure;


/**
 * @author v1xliu33
 *
 */
public class LiterTree {

	/**
	 * 
	 */
	
	private LiterNode root;
	

	public LiterTree() {
		// TODO Auto-generated constructor stub
	}
	
	public LiterTree(LiterNode root){
		this.setRoot(root);
	}

	public LiterNode getRoot() {
		return root;
	}

	public void setRoot(LiterNode root) {
		this.root = root;
	} 
	
	/*
	public Condition getCondition(LiterNode t){
	 LiterNode node = t.getParent();
	 Condition cond = new Condition();
	 while(node!= root){
		 cond.combineCondition(t.getDependecy());	 
		 node = node.getParent();
	 }
	 return cond;	 
	}
	*/


}
