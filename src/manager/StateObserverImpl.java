package manager;

import org.json.JSONObject;

import io.vertx.core.http.ServerWebSocket;

public class StateObserverImpl implements StateObserver{
	
	private final ServerWebSocket webSocket;

	public StateObserverImpl(ServerWebSocket ws) {
		this.webSocket = ws;
	}
	
	@Override
	public void notifyState(JSONObject obj) {
		webSocket.writeTextMessage(obj.toString());
		
	}

}
