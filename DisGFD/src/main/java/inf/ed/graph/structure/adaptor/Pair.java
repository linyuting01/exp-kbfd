package inf.ed.graph.structure.adaptor;

import java.io.Serializable;


public class Pair<T> implements Comparable<Pair<Integer>>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The first object in the pair
	 */
	public T x;

	/**
	 * The second object in the pair
	 */
	public T y;

	/**
	 * Creates a pair out of {@code x} and {@code y}
	 */
	public Pair(T x, T y) {
		this.x = x;
		this.y = y;
	}

	public Pair(Pair<T> a) {
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
		Pair<T> p = ((Pair<T>) o);
		return (x == p.x || (x != null && x.equals(p.x)))
				&& (y == p.y || (y != null && y.equals(p.y)));
	}

	public int hashCode() {
		return ((x == null) ? 0 : x.hashCode()) ^ ((y == null) ? 0 : y.hashCode());
	}

	public String toString() {
		return "{" + x + ", " + y + "}";
	}


	@Override
	public int compareTo(Pair<Integer> o) {
		// TODO Auto-generated method stub
		int x1 = (int)x;
		int y1 = (int)y;
		if(x1 != o.x){
			return x1-o.x;
		}
		else{	
			return y1-o.y;
		}
		
	}


}