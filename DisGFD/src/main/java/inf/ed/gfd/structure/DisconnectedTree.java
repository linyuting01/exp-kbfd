package inf.ed.gfd.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.event.ConnectedComponentTraversalEvent;

import inf.ed.gfd.util.Params;
import inf.ed.graph.structure.adaptor.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class DisconnectedTree {
	static Logger log = LogManager.getLogger(DisconnectedTree.class);
	
	public DisConnectedNode root;
	public HashMap<Integer, Integer> connectdPatternIndex;
	public HashMap<String, DisConnectedNode> disConnectedPatternIndex;
	public int preConnectedPNum = 0;
	public int flag = Integer.MAX_VALUE;
	//public Set<String> dom; //zhuyifuzhi
	public int nodeNum;
	
	public Set<GFD2> disConnectedGfds = new HashSet<GFD2>();
	
	//public WorkUnit disws ;

	
	//public int connectPatternNum = 0;
	
	

	public DisconnectedTree() {
		// TODO Auto-generated constructor stub
		this.root = new DisConnectedNode();
		this.disConnectedPatternIndex = new HashMap<String, DisConnectedNode>();
		this.connectdPatternIndex = new HashMap<Integer, Integer>();
		//this.dom = new HashSet<String>();
		//this.disws = new WorkUnit();
		
	}
	
	/**
	 * here extendPatterns has ordered by support; from large to small
	 * @param t
	 */
	private Queue<DisConnectedNode>  extendTree(List<SimP> extendPatterns){
		Queue<DisConnectedNode> disQue = new LinkedList<DisConnectedNode>();
		int begin = connectdPatternIndex.size()+1;
		int end = begin+extendPatterns.size()-1;
		preConnectedPNum = begin-1;
		int index =0;
		for(SimP pa : extendPatterns){
			addRootChildren(pa);
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
							int supp2 = extendPatterns.get(index-begin).supp;
							if(t.supp* supp2 < Params.VAR_SUPP){
								t.flag = index;
								this.flag = index;
							}
							else{
								SimP patt = extendPatterns.get(index-begin);
								if(t.pNodeNum +patt.nodeNum <= Params.var_K){
								    DisConnectedNode g = addNode(t, pattern, patt);
								    disQue.add(g);
								}
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
		return disQue;
}

	
	private int filters(DisConnectedNode parent, int pattern){
		if(this.flag <= pattern){
			return 1;//parent's neighbour filter;
		}
		if(parent.flag <= pattern){
			return 2; // begin to end filter
		}
		return 0;
		
	}
	
	private void addRootChildren(SimP pattern){
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
		p.ltree.dNode = p;
	}

		
	/**
	 * add node to compose disconnected pattern	
	 * @param parent
	 * @param pattern
	 * @return 
	 */

	private DisConnectedNode addNode(DisConnectedNode parent, int padd, SimP pattern){
		
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
			p.ltree.dNode = p;
			return p;
			/*
			p.ltree.dom = dom;
			p.ltree.extendSpace(p,this);
			p.ltree.disExtendRoot();
			List<String> ps = new ArrayList<String>();
			for(int patt : p.patterns){
				String s = connectdPatternIndex.get(patt);
				ps.add(s);
			}
			*/
			
			
		
	}
	/***
	 * validation
	 */
	private void intialVerifyPivotMatch(LiterNode t){
		//first partition dependency
		Condition c = t.dependency;
		Pair<Integer,Integer> yv = c.YEqualsVariable;
		
		
	}
	private void addNode(DisConnectedNode t, int pattern){
		DisConnectedNode p = new DisConnectedNode();
		p.parent = t;
		t.children.add(p);
		p.layer = t.layer+1;
		p.patterns.add(pattern);
		String newId = p.getpId();
		disConnectedPatternIndex.put(newId, p);
		t.ltree.dNode = t;
	}

	
	
	
	
	private void updateTree(DisWorkUnit w){
		for(Pair<Integer,Integer> p :w.disPatterns){
			if(!this.connectdPatternIndex.containsKey(p.x)){
				addNode(this.root,p.x);
			}
			if(!this.connectdPatternIndex.containsKey(p.y)){
				addNode(this.root,p.y);
			}
			addNode(this.disConnectedPatternIndex.get(p.x), p.y);	
		}
	}
	
	private void updateTreeRootChild(DisConnectedNode dn ,GfdTree gfdtree){
			int patternId = dn.patterns.get(0);
			int pId = this.connectdPatternIndex.get(patternId);
			GfdNode g = gfdtree.patterns_Map.get(pId);
			for(LiterNode ln : g.ltree.getRoot().children){
				if(ln.dependency.isLiteral){
					Pair<Integer,String> yl = ln.dependency.YEqualsLiteral;
					if(dn.stringCands == null){
						dn.stringCands = new HashMap<String,HashMap<Integer,IntSet>>();
					}
					if(!dn.stringCands.containsKey(yl.y)){
						dn.stringCands.put(yl.y, new HashMap<Integer,IntSet>());
					}
					dn.stringCands.get(yl.y).put(yl.x, ln.pivotMatch);
					if(ln.supp >= Params.VAR_SUPP){
						if(dn.allLiterCands == null){
							dn.allLiterCands = new HashMap<Integer,HashMap<String,IntSet>>();
						}
						if(!dn.allLiterCands.containsKey(yl.x)){
							dn.allLiterCands.put(yl.x, new HashMap<String,IntSet>());
						}
						dn.allLiterCands.get(yl.x).put(yl.y, ln.pivotMatch);
						
					}
					
				}
			}
		}
			
	
		
	private void updateTree(DisConnectedNode t, GfdTree gfdtree){
		HashMap<Integer,HashMap<Integer,Set<String>>> varCands = new HashMap<Integer,HashMap<Integer,Set<String>>>();
		//for disconnected pattens;
		int pId1 = t.patterns.get(0);
		int pId2 = t.patterns.get(1);
		String key1 = ""+pId1;
		String key2 = ""+pId2;
		DisConnectedNode d1 = this.disConnectedPatternIndex.get(key1);
		DisConnectedNode d2 = this.disConnectedPatternIndex.get(key2);
		HashMap<String, HashMap<Integer,IntSet>> tmpt1 = d1.stringCands;
		HashMap<String, HashMap<Integer,IntSet>> tmpt2 = d2.stringCands;
		for(String s1 : tmpt1.keySet()){
			for(String s2 : tmpt2.keySet()){
				if(s1.equals(s2)){
					for(int i: tmpt1.get(s1).keySet()){
						for(int j : tmpt2.get(s2).keySet()){
							if(!varCands.containsKey(i)){
								varCands.put(i, new HashMap<Integer,Set<String>>());	
							}
							if(!varCands.get(i).containsKey(j)){
								varCands.get(i).put(j, new HashSet<String>());
							}
							varCands.get(i).get(j).add(s1);	
						}
					}
				}
			}
		}
		for(int fId : varCands.keySet()){
			for(int tId: varCands.get(fId).keySet()){
				IntSet a = new IntOpenHashSet();
				IntSet b = new IntOpenHashSet();
				for(String s:varCands.get(fId).get(tId)){
					a.addAll(tmpt1.get(s).get(fId));
					b.addAll(tmpt2.get(s).get(tId));
				}
				int supp = a.size()*b.size();
				if(supp >= Params.VAR_SUPP){
					
					if(t.allVarCands == null){
						t.allVarCands = new HashMap<Integer,HashMap<Integer,List<IntSet>>>();
					}
					if(!t.allVarCands.containsKey(fId)){
						t.allVarCands.put(fId, new HashMap<Integer,List<IntSet>>());
					}
				    if(!t.allVarCands.get(fId).containsKey(tId)){
				    	t.allVarCands.get(fId).put(tId, new ArrayList<IntSet>());
				    }
				    t.allVarCands.get(fId).get(tId).add(a);
				    t.allVarCands.get(fId).get(tId).add(b);		    
				}
			}
		}
	}
		
		public void disConnectedGFD(List<SimP> extendPatterns, GfdTree gtree){
			
			Queue<DisConnectedNode> disQue = extendTree(extendPatterns);
			while(!disQue.isEmpty()){
				DisConnectedNode t = disQue.poll();
				t.ltree.extendDisConnected(this);
				if(t.conditions !=null){
					for(Condition c:t.conditions){
						GFD2  gfd1 = new GFD2();
						for(int p : t.patterns){
							int gp = this.connectdPatternIndex.get(p);
							GfdNode g = gtree.patterns_Map.get(gp);	
							gfd1.patterns.add(gfd1.getPattern());
						}
						gfd1.condition = c;
						this.disConnectedGfds.add(gfd1);
					}
				}
			}
				
			
			
			
		}
	
	
}
		
		
	

