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
			Pair<Integer,Integer> p1 = new Pair<Integer,Integer>(Integer.parseInt(s1[0].trim()),Integer.parseInt(s1[1].trim()));
			Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(Integer.parseInt(s2[0].trim()),Integer.parseInt(s2[1].trim()));
			DFS dfs = new DFS(p1,p2, Integer.parseInt(tmpt[2].trim()));
			
			return dfs;
		}
		
		
		public static void main(String args[]) {
			////Pair<Integer,Integer> a = new Pair<String,Integer> ("a",5);
		//	Pair<Integer,Integer> b = new Pair<String,Integer> ("b",1);
			 int c = 2;
		//	DFS dfs = new DFS(a,b,c);
		//	DFS dfsn = Fuc.getDfsFromString(dfs.toString());
			//log.debug(dfsn.toString());
			
			
			
		}

}
