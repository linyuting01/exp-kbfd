package inf.ed.gfd.structure;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.graph.structure.adaptor.Pair;


public class DFS implements Comparable<DFS>, Serializable {
  
  static Logger log = LogManager.getLogger(DFS.class);
  
private static final long serialVersionUID = 1L;
  /*
   * we use Triple to denote one node in the pattern, why a Triple is 
   * for the node whose label has already in the pattern
   */
  public Pair<String,Integer> fLabel;
  public Pair<String,Integer> tLabel;
  public int eLabel;

  public DFS(Pair<String,Integer> A, Pair<String,Integer> B, int e){
    this.fLabel = A;
    this.tLabel = B;
    this.eLabel = e;
  }


public DFS() {
	// TODO Auto-generated constructor stub
}

public DFS(DFS dfs) {
	// TODO Auto-generated constructor stub
	 
		    this.fLabel = dfs.fLabel;
		    this.tLabel = dfs.tLabel;
		    this.eLabel = dfs.eLabel; 
}

@Override
  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append(this.fLabel.toString());
    sb.append(";;");
    sb.append(this.tLabel.toString());
    sb.append(";;");
    sb.append(this.eLabel);
    return sb.toString();
    
  }
  @Override
  public int compareTo(DFS other) {
    if(!this.fLabel.equals(other.fLabel)){
      return this.fLabel.compareTo(other.fLabel);
    }
    if(!this.tLabel.equals(other.tLabel)){
      return this.tLabel.compareTo(other.tLabel);
    }
    if(this.eLabel != other.eLabel){
      return this.eLabel- other.eLabel;
    }
    return 0;
  }
  
  public static void main(String args[]) throws CloneNotSupportedException {  
    
	Pair<String,Integer> a = new Pair<String,Integer> ("a",5);
	Pair<String,Integer> b = new Pair<String,Integer> ("b",1);
	 int c = 2;
	DFS dfs = new DFS(a,b,c);
	// DFS dfsn = (DFS) dfs.clone();
	log.debug(dfs.toString());
  }
  @Override
  public boolean equals(Object o) {
	    if (!(o instanceof DFS)) return false;
	    DFS  other = (DFS) o;
	    return (this.fLabel.equals(other.fLabel) 
	    		&& this.tLabel.equals(other.tLabel) &&
	    	     this.eLabel == other.eLabel);
	  }
  
  @Override
  public int hashCode() { 
	  return  this.toString().hashCode(); 
  }
	
  public DFS findDFS(){
	   Pair<String,Integer> t1 = new Pair<String,Integer> ("",0);
	   Pair<String,Integer> t2 = new Pair<String,Integer> ("",0);
	   t1.x = this.fLabel.x;
	   t2.x = this.tLabel.x;
	   if(this.isEqualL()){
		  t2.y = 1;
	   }
	   DFS dfsn = new DFS(t1,t2,this.eLabel);
	   return dfsn;
  }
  public boolean isEqualL(){
	   if(this.fLabel.x.equals(this.tLabel.x)){
		   return true;
	   }
	   return false;
  }
  
  public boolean isEqualL(DFS dfs2){
	   if(this.fLabel.x.equals( dfs2.fLabel.x) && this.tLabel.x.equals(dfs2.tLabel.x)){
		   return true;
	   } 
	   return false;
  }
    
 /**     
void addDFSToPatterns(Graph<VertexString, TypedEdge> pattern, DFS dfsCode){
  int fLabel = dfsCode.fLabel;
  int tLabel = dfsCode.tLabel;
  VertexString f = pattern.allVertices().get(fLabel);
  VertexString t = pattern.allVertices().get(tLabel);
  if(f == null ){
    f = new VertexString(fLabel, dfsCode.fLabel);
    pattern.addVertex(f);
    pattern.allVertices().put(dfsCode.fLabel, f);
  }
    if(t == null){
      t = new VertexString(tLabel, dfsCode.tLabel);
      pattern.addVertex(t);
      pattern.allVertices().put(dfsCode.fLabel, t);
    }
    TypedEdge e = new TypedEdge(f,t, pattern.edgeSize()+1);
    pattern.addEdge(e);  
}

Graph<VertexString, TypedEdge> createPattern(ArrayList<DFS> dfsCode){
  Graph<VertexString, TypedEdge> pattern = new SimpleGraph<VertexString, TypedEdge>(VertexString.class, TypedEdge.class);
  for(int i=0; i<dfsCode.size(); i++){
    addDFSToPatterns(pattern,dfsCode.get(i));
  }
  return pattern;
  }

 */ 

}
