package node;

import org.json.*;

public interface Node {
	
	/**
	 * Return a json that contains a state of node
	 */
	JSONObject getState();

}
