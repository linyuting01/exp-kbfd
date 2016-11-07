package inf.ed.gfd.structure;

import java.io.Serializable;

public class EqLiteral implements Comparable<EqLiteral>, Serializable{
	
	private static final long serialVersionUID = 8852252918165328207L;

	int patterId = 1;//for disconnected
	int nId;
	int attrId;
	String val;

	public EqLiteral(int x, int y,String s) {
		// TODO Auto-generated constructor stub
		nId = x;
		attrId = y;
		val = s;
	}

	public EqLiteral() {
		// TODO Auto-generated constructor stub
	}

	public EqLiteral(int p, int x, int y,String s) {
		// TODO Auto-generated constructor stub
		this.patterId = p;
		nId = x;
		attrId = y;
		val = s;
	}

	public EqLiteral(EqLiteral eql) {
		// TODO Auto-generated constructor stub
		nId = eql.nId;
		attrId = eql.attrId;
		val =eql.val ;
		patterId = eql.patterId;
	}
	 @Override
	    
	    public String toString(){
		 return ""+patterId +"\t"+ nId+"\t"+attrId+"\t"+"eq-let\t"+val+"\n";
	 }

	@Override
	public int compareTo(EqLiteral o) {
		// TODO Auto-generated method stub
		if(this.nId != o.nId){
			return this.nId - o.nId;
		}
		if(this.attrId!= o.attrId){
			return this.attrId - o.attrId;
		}
		return this.val.compareTo(o.val);
	}
	
	  @Override
	  public boolean equals(Object o) {
		    if (!(o instanceof EqLiteral)) return false;
		    EqLiteral  other = (EqLiteral) o;
		    return (this.nId == other.nId
		    		&& this.attrId == other.attrId &&
		    	     this.val.equals(other.val));
		  }
	  
	  @Override
	  public int hashCode() { 
		  return  this.toString().hashCode(); 
	  }

}
