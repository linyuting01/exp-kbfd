package inf.ed.gfd.structure;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DFS implements Comparable<DFS>, Serializable {
  
  static Logger log = LogManager.getLogger(DFS.class);
  
private static final long serialVersionUID = 1L;
  /*
   * we use Triple to denote one node in the pattern, why a Triple is 
   * for the node whose label has already in the pattern
   */
  public Triple fLabel;
  public Triple tLabel;
  public int eLabel;

  public DFS(Triple A, Triple B, int e){
    this.fLabel = A;
    this.tLabel = B;
    this.eLabel = e;
  }
  
  @Override
  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append(this.fLabel);
    sb.append(this.tLabel);
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
  
  public static void main(String args[]) {  
    
	 Triple a = new Triple(3,5,1);
	 Triple b = new Triple(4,1,2);
	 int c = 2;
	 DFS dfs = new DFS(a,b,c);
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
	  return  fLabel.hashCode() ^ tLabel.hashCode() ^ eLabel; 
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
