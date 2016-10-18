package inf.ed.grape.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.ints.IntSet;

public abstract class Result implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	static Logger log = LogManager.getLogger(Result.class);

	/** a function how to assemble partial results to a final result. */
	public abstract void assemblePartialResults(
			Collection<Result> partialResults);

	/** a function how write file results to a final result. */
	public abstract void writeToFile(String filename);



		// TODO Auto-generated method stub





	public abstract void assemblePartialResults(Collection<Result> partialResults, HashMap<String, IntSet> pivotMatch,
			HashMap<String, HashMap<String, IntSet>> gfdPMatch, HashMap<String, Set<String>> cIds, boolean flagP);
}
		
	


