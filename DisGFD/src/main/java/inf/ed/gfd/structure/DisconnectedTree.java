package inf.ed.gfd.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.event.ConnectedComponentTraversalEvent;

import inf.ed.gfd.util.Params;

public class DisconnectedTree {
	static Logger log = LogManager.getLogger(DisconnectedTree.class);
	
	public DisConnectedNode root;
	public HashMap<Integer, String> connectdPatternIndex;
	public HashMap<String, DisConnectedNode> disConnectedPatternIndex;
	public int preConnectedPNum = 0;
	public int flag = Integer.MAX_VALUE;

	
	//public int connectPatternNum = 0;
	
	

	public DisconnectedTree() {
		// TODO Auto-generated constructor stub
		this.root = new DisConnectedNode();
		this.disConnectedPatternIndex = new HashMap<String, DisConnectedNode>();
		this.connectdPatternIndex = new HashMap<Integer, String>();
		
	}
	
	/**
	 * here extendPatterns has ordered by support; from large to small
	 * @param t
	 */
	public void extendTree(List<SimP> extendPatterns){
		int begin = connectdPatternIndex.size()+1;
		int end = begin+extendPatterns.size()-1;
		preConnectedPNum = begin-1;
		int index =0;
		for(SimP pa : extendPatterns){
			addRoorChildren(pa);
		}
		List<DisConnectedNode> flist = this.root.children;
		
		boolean loopfor = true;
		boolean klayer = true;
		while(!flist.isEmpty() && klayer){
			this.flag = Integer.MAX_VALUE;
			for(DisConnectedNode t: flist){
				if(t.layer > Params.var_K){
					klayer = false;
	        		break;
					
				}
				if(loopfor){
					if(t.lastPId<= begin){
						index = begin;
					}
					else{
						index = t.lastPId;
					}
					for(;index<= end;index++){
						int pattern = this.preConnectedPNum + index-begin+1;
						int filter = filters(t,pattern);
						if(filter == 1){
							loopfor = false;
							break;
						}
						if(filter == 2){
							break;
						}
						if(filter == 0){
							double supp2 = extendPatterns.get(index-begin).supp;
							if(t.supp* supp2 < Params.VAR_SUPP){
								t.flag = index;
								this.flag = index;
							}
							else{
								addNode(t, pattern,extendPatterns.get(index-begin));
							}
						}
							
					}
				}
			}
			List<DisConnectedNode> tmptList = new ArrayList<DisConnectedNode>();
			for(DisConnectedNode x: flist){
				tmptList.addAll(x.children);
			}
			flist.clear();
			flist = tmptList;
		}
}

	
	public int filters(DisConnectedNode parent, int pattern){
		if(this.flag <= pattern){
			return 1;//parent's neighbour filter;
		}
		if(parent.flag <= pattern){
			return 2; // begin to end filter
		}
		return 0;
		
	}
	
	public void addRoorChildren(SimP pattern){
		DisConnectedNode p = new DisConnectedNode();
		p.parent = this.root;
		this.root.children.add(p);
		int num = connectdPatternIndex.size();
		connectdPatternIndex.put(num+1, pattern.patternId);
		p.patterns.add(num+1);
		String newId = p.getpId();
		disConnectedPatternIndex.put(newId, p);
		p.layer = 1;
		p.supp = pattern.supp;
		p.lastPId = num+1;
		p.flag = Integer.MAX_VALUE;
		p.pNodeNum = pattern.nodeNum;
	}
		
	/**
	 * add node to compose disconnected pattern	
	 * @param parent
	 * @param pattern
	 */

	public void addNode(DisConnectedNode parent, int padd, SimP pattern){
		if(parent.pNodeNum +pattern.nodeNum <= Params.var_K){
			DisConnectedNode p = new DisConnectedNode();
			p.parent = parent;
			parent.children.add(p);
			p.patterns = new ArrayList<Integer>(parent.patterns);
			p.patterns.add(padd);
			p.layer = parent.layer+1;
			p.supp = parent.supp* pattern.supp;
			String newId = p.getpId();
			disConnectedPatternIndex.put(newId, p);
			p.lastPId = padd;
			p.flag = Integer.MAX_VALUE;
			p.pNodeNum = parent.pNodeNum + pattern.nodeNum;
		}
	}
	


}
