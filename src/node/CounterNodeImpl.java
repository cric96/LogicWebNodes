package node;

import org.json.JSONObject;

public class CounterNodeImpl implements CounterNode, Runnable {
	private final int id;
	volatile private int counter = 0;
	
	public CounterNodeImpl(int id) {
		this.id = id;
	}

	@Override
	public JSONObject getState() {
		return new JSONObject()
				.put("id", id)
				.put("counter", counter);
	}

	@Override
	public void incrementCounter() {
		this.counter++;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void run() {
		while(true) {
			try {
				incrementCounter();
				Thread.sleep(getSleepDelay());
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	protected int getSleepDelay() {
		return (id+1) * 1000;
	}
}
