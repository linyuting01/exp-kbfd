package inf.ed.gfd.structure;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.OMGVMCID;

public class SimP implements Comparable<SimP>, Serializable {
	
	static Logger log = LogManager.getLogger(DFS.class);
	  
	private static final long serialVersionUID = 1L;
	 
	int patternId;
	int supp;
	int nodeNum;

	public SimP() {
		// TODO Auto-generated constructor stub
	}

	public SimP(int pId, int supp2, int nodeNum2) {
		// TODO Auto-generated constructor stub
		this.patternId = pId;
		this.supp = supp2;
		this.nodeNum = nodeNum2;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public int compareTo(SimP o) {
		// TODO Auto-generated method stub
		if(this.supp> o.supp){
			return 1;
		}
		if(this.supp < o.supp){
			return -1;
		}
		else{
			return 0;
		}
	}
	
	  @Override
	  public boolean equals(Object o) {
		    if (!(o instanceof SimP)) return false;
		    SimP other = (SimP) o;
		    if(this.supp == other.supp){
		    	return true;
		    }
		    return false;
		  }
	  
	  @Override
	  public int hashCode() { 
		  return  this.patternId; 
	  }

}
