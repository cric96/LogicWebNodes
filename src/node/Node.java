package node;

import org.json.JSONObject;

public interface Node {
	
	/**
	 * @return a json object that contains a state of node
	 */
	JSONObject getState();
	
	/**
	 * @return id of node
	 */
	int getId();

}
