package inf.ed.gfd.structure;

import java.io.Serializable;

public class Triple implements Comparable<Triple>, Serializable{

	  private static final long serialVersionUID = 1L;
	  private int first;
	  private int second;
	  private int third;

	  public Triple(int first, int second, int third){
	    this.first = first;
	    this.second = second;
	    this.third = third;
	  }


	  public Triple(Triple t) {
		// TODO Auto-generated constructor stub
		  this.first = t.getFirst();
		  this.second = t.getSecond();
		  this.third = t.getThird();
	}


	public Triple() {
		// TODO Auto-generated constructor stub
	}


	@Override
	  public int hashCode() { return first ^ second ^third; }

	  @Override
	  public boolean equals(Object o) {
	    if (!(o instanceof Triple)) return false;
	    Triple tripleo = (Triple) o;
	    return this.first == tripleo.getFirst() &&
	           this.second == tripleo.getSecond() &&
	           this.third == tripleo.getThird();
	  }

	@Override
	public int compareTo(Triple o) {
		// TODO Auto-generated method stub
		if(this.first != o.getFirst()){
			return this.first-o.getFirst();
		}
		else if(this.second != o.getSecond()){
			return this.second-o.getSecond();
		}
		else{
			return this.third - o.getThird();
		}
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getThird() {
		return third;
	}

	public void setThird(int third) {
		this.third = third;
	}

}

