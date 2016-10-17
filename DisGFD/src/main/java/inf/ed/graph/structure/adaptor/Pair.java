package inf.ed.graph.structure.adaptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.structure.DFS;
import inf.ed.gfd.structure.GfdNode;
import inf.ed.gfd.structure.GfdTree;


public class Pair<T,T1> implements Comparable<Pair<Integer,Integer>>, Serializable {

	private static final long serialVersionUID = 1L;
	
	 static Logger log = LogManager.getLogger(Pair.class);

	/**
	 * The first object in the pair
	 */
	public T x;

	/**
	 * The second object in the pair
	 */
	public T1 y;

	/**
	 * Creates a pair out of {@code x} and {@code y}
	 */
	public Pair() {
	}
	
	public Pair(T x, T1 y) {
		this.x = x;
		this.y = y;
	}

	public Pair(Pair<T,T1> a) {
		// TODO Auto-generated constructor stub
		this.x = a.x;
		this.y = a.y;
	}

	/**
	 * Returns {@code true} if {@code o} is a {@link Pair} and its {@code x} and
	 * {@code y} elements are equal to those of this pair. Note that equality is
	 * specific to the ordering of {@code x} and {@code y}.
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Pair))
			return false;
		Pair<T,T1> p = ((Pair<T,T1>) o);
		return (x == p.x || (x != null && x.equals(p.x)))
				&& (y == p.y || (y != null && y.equals(p.y)));
	}

	public int hashCode() {
		return ((x == null) ? 0 : x.hashCode()) ^ ((y == null) ? 0 : y.hashCode());
	}

	public String toString() {
		return x + "," + y ;
	}


	@Override
	public int compareTo(Pair<Integer,Integer> o) {
		// TODO Auto-generated method stub
		Integer x1 = (Integer)x;
		Integer y1 = (Integer)y;
		if(x1 != o.x){
			return x1-o.x;
		}
		else{	
			return y1-o.y;
		}
		
	}
	public static void main(String args[]) {  
		Pair<Integer,Integer> p = new Pair<Integer,Integer>(3,5);
		
	
		log.debug(p.toString());
		
	    
	}


}