package prototype.websocket;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class WebSocketServer extends AbstractVerticle {
	private final static String BROADCAST = "Broadcast";
	private final int port;
	private final Map<Integer, String> clients;
	
	private int idCounter = 1;
	
	public WebSocketServer(final int port) {
		this.port = port;
		clients = new HashMap<>();
	}
	
	@Override 
	public void start() {
		startServer(vertx);
	}

	private void startServer(Vertx vertx) {
		HttpServer server = vertx.createHttpServer();
	
		server.webSocketHandler(webSocket -> {
			
			System.out.println("client Connected "+webSocket.textHandlerID());
			//salvo un nuovo client
			clients.put(this.idCounter++, webSocket.textHandlerID());
			System.out.println("Clients \n" + clients.toString());
			
			
			//Incremente periodiacamente il contatore del primo client
			if(idCounter == 2) {
				vertx.eventBus().localConsumer(webSocket.textHandlerID());
				vertx.setPeriodic(3000, msg -> vertx.eventBus().publish(clients.get(1), "ackInc"));
			}
			
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
				}else {
					System.out.println("The message is not recognized");
				}
				System.out.println("The msg is: " + message + "| id: " + webSocket.textHandlerID());
              
            });
			
			//Se un client è disconesso
			webSocket.closeHandler(message ->{
                System.out.println("client disconnected "+webSocket.textHandlerID());
                clients.remove(clients.entrySet().stream().filter(f -> f.getValue().equals(webSocket.textHandlerID())).map(m -> m.getKey()).findFirst().get());
            });
			
		}).listen(port);	
		
	}

}
