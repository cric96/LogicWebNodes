package prototype.websocket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import manager.StateObserver;
import manager.StateObserverImpl;
import node.CounterNode;
import node.CounterNodeImpl;
import node.ObservableCounterNode;

public class WebSocketServer extends AbstractVerticle {
	private final static String BROADCAST = "Broadcast";
	private final int port;
	
	private final Map<Integer, CounterNode> nodes;
	private StateObserver manager = null;
	
	public WebSocketServer(final int port) {
		this.port = port;
		
		nodes = new HashMap<>();

	}
	
	@Override 
	public void start() {
		startServer(vertx);
	}

	private void startServer(Vertx vertx) {
		HttpServer server = vertx.createHttpServer();
	
		server.webSocketHandler(webSocket -> {
			System.out.println("client Connected "+ webSocket.textHandlerID());	
			
			//registro broadcast sender
			vertx.eventBus().consumer(BROADCAST, message -> {
				webSocket.writeTextMessage(message.body().toString());
            });
			
			// gestisco la logica di elaborazione dei messaggi ricevuti
			webSocket.textMessageHandler(message -> {
				if(message.toString().equals("inc")) {
					webSocket.writeTextMessage("ackInc");
					
				}else if(message.toString().equals("incAll")) {
					vertx.eventBus().publish(BROADCAST, "ackInc");
				}else if(message.toString().equals("subscribe")) {
					manager = new StateObserverImpl(webSocket);
					for(int id = 0; id < 5; id++) {
						ObservableCounterNode newNode = new ObservableCounterNode(id, manager);
						new Thread(newNode).start();
						nodes.put(id, newNode);
					}
				
				}else {
					System.out.println("The message is not recognized");
				}
				System.out.println("The msg is: " + message + "| id: " + webSocket.textHandlerID());
              
            });
			
			//Se un client è disconesso
			webSocket.closeHandler(message -> {
                System.out.println("client disconnected "+webSocket.textHandlerID());
            });
			
		});	
		
		server.listen(port);
		
	}
}
