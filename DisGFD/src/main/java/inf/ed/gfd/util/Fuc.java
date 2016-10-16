package inf.ed.gfd.util;

import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.algorithm.sequential.EdgePattern;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class Fuc {

	static Logger log = LogManager.getLogger(Fuc.class);
	
		  public static IntSet Intersection (IntSet key1, IntSet key2){
		    IntSet a = new IntOpenHashSet();
		    a.addAll(key1);
		    a.retainAll(key2);
		    return a;
		  }

		  

		public static BitSet bitSet(IntSet a){
		  BitSet b = new BitSet();
		  for(int i :a ){
		    b.set(i);
		   
		  }
		  return b;
		}	
		
		public static void main(String args[]) {
			IntSet a = new IntOpenHashSet();
			IntSet b = new IntOpenHashSet();
			IntSet c = new IntOpenHashSet();
			
			a.add(1);
			a.add(2);
			a.add(3);
			a.add(4);
			b.add(1);
			b.add(2);
			b.add(3);
			c= Intersection(a,b);
			log.debug(a.size());
			log.debug(c.size());
			log.debug(b.size());
			
			
			
		}

}
