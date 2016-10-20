package inf.ed.gfd.util;

import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.algorithm.sequential.EdgePattern;
import inf.ed.gfd.structure.Condition;
import inf.ed.gfd.structure.DFS;
import inf.ed.graph.structure.adaptor.Pair;
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
		public static DFS getDfsFromString(String key){
			
			String tmpt[] = key.split(";");
			String s1[] = tmpt[0].split(",");
			String s2[] = tmpt[1].split(",");
			
			Pair<String,Integer> p1 = new Pair<String,Integer>(s1[0].trim(),Integer.parseInt(s1[1]));
			Pair<String,Integer> p2 = new Pair<String,Integer>(s2[0].trim(),Integer.parseInt(s2[1]));
			DFS dfs = new DFS(p1,p2, Integer.parseInt(tmpt[2]));
			
			return dfs;
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
			String a1 = "XEQ;;XEV;1:{2,3};YEV;1:4";
			String tempt[] = a1.split(";");
			for(int i=0; i<tempt.length;i++){
				log.debug(tempt[i]);
			}
			
			
			
		}

}
