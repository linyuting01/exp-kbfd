package inf.ed.gfd.structure;

import java.io.Serializable;

public class TransAttr implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int attr = -1;
	String val = "";
	
	public TransAttr(int attr, String val){
		this.attr = attr;
		this.val = val;
	}
	
	  
	 
	  @Override
	  public boolean equals(Object o) {
		    if (!(o instanceof TransAttr)) return false;
		    TransAttr  other = (TransAttr) o;
		    return (this.attr == other.attr) 
		    		&& this.val.equals(other.val);
		  }
	  
	  @Override
	  public int hashCode() { 
		  return  attr ^ ((val == null) ? 0 : val.hashCode());
	  }
}