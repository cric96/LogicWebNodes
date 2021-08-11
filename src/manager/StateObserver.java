package manager;

import org.json.JSONObject;

public interface StateObserver {
	
	void notifyState(JSONObject obj);

}
