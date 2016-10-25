package inf.ed.gfd.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DCPartialCond {
	
	HashMap<Integer,String> xl;
	Set<String> freeX;
	Set<String> freeY;
	

	public DCPartialCond() {
		// TODO Auto-generated constructor stub
		this.xl = new HashMap<Integer,String>();
		this.freeX = new HashSet<String>();
		this.freeY = new HashSet<String>();
	}

}
