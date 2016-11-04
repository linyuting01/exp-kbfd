package inf.ed.gfd.structure;

import java.io.Serializable;

public class EqVarLiter implements Comparable<EqVarLiter>, Serializable {
	private static final long serialVersionUID = -2264345127650602526L;

	int fId;
	int tId;
	int fattr;
	int tattr;

	public EqVarLiter(int f, int t, int fa, int ta) {
		// TODO Auto-generated constructor stub
		fId  = f;
		tId =  t;
		fattr = fa;
		tattr = ta;
	}

	public EqVarLiter() {
		// TODO Auto-generated constructor stub
	}

	 @Override
	    
	    public String toString(){
		 return ""+fId+"\t"+fattr+"\t"+"eq-var\t"+tId+"\t"+tattr+"\n";
	 }

	@Override
	public int compareTo(EqVarLiter o) {
		// TODO Auto-generated method stub
		if(this.fId != o.fId){
			return this.fId - o.fId;
		}
		if(this.tId != o.tId){
			return this.tId - o.tId;
		}
		if(this.fattr!= o.fattr){
			return this.fattr - o.fattr;
		}
		if(this.tattr!= o.tattr){
			return this.tattr - o.tattr;
		}
		return 0;
		

	}
	  @Override
	  public boolean equals(Object o) {
		    if (!(o instanceof EqVarLiter)) return false;
		    EqVarLiter  other = (EqVarLiter) o;
		    return (this.fId == other.fId
		    		&& this.fId == other.fId
		    		&& this.fattr == other.fattr 
		    		&& this.tattr == other.tattr);
		  }
	  
	  @Override
	  public int hashCode() { 
		  return  this.toString().hashCode(); 
	  }

}
