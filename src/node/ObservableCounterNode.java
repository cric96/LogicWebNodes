package node;

import manager.StateObserver;

public class ObservableCounterNode extends CounterNodeImpl{
	private final StateObserver observer;
	
	public ObservableCounterNode(int id, StateObserver observer) {
		super(id);
		this.observer = observer;
	}
	

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(getSleepDelay());
				incrementCounter();
				observer.notifyState(getState());
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}

}
