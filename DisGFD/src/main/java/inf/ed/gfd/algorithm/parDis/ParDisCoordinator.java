package inf.ed.gfd.algorithm.parDis;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.structure.CrossingEdge;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public class ParDisCoordinator {
	
	static Logger log = LogManager.getLogger(ParDisCoordinator.class);
	
	/** for project DisGfd **/
	//private Set<WorkUnit> workunits = new HashSet<WorkUnit>();
	private Int2ObjectMap<IntSet> allBorderVertices = new Int2ObjectOpenHashMap<IntSet>();
	private Int2ObjectMap<String> allVertices = new Int2ObjectOpenHashMap<String>();
	private Int2ObjectMap<Set<CrossingEdge>> mapBorderNodesAsSource = new Int2ObjectOpenHashMap<Set<CrossingEdge>>();
	private Int2ObjectMap<Set<CrossingEdge>> mapBorderNodesAsTarget = new Int2ObjectOpenHashMap<Set<CrossingEdge>>();
	
	
	


	


}
