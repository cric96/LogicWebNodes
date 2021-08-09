package node;

import org.json.JSONObject;

public class CounterNodeImpl implements CounterNode {
	private int counter = 0;

	@Override
	public JSONObject getState() {
		return new JSONObject().put("counter", counter);
	}

	@Override
	public void incrementCounter() {
		this.counter++;
	}
	
}
