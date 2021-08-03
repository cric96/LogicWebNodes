package prototype.webrtc;

import java.util.HashMap;
import java.util.Map;
import org.json.*;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class WebRTCServer extends AbstractVerticle {
	private final static String BROADCAST = "Broadcast";
	private final int port;
	private final Map<Integer, String> clients;
	
	private int idCounter = 1;
	
	
	public WebRTCServer(final int port) {
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
				//vertx.setPeriodic(3000, msg -> vertx.eventBus().publish(clients.get(1),  getIncrementCounterMsg()));
				
			}
			
			//registro broadcast sender
			vertx.eventBus().consumer(BROADCAST, message -> {
				webSocket.writeTextMessage(message.body().toString());
            });
			
			// gestisco la logica di elaborazione dei messaggi ricevuti
			webSocket.textMessageHandler(message -> {
				if(message.toString().equals("inc")) {
					webSocket.writeTextMessage(getIncrementCounterMsg());
					
				}else if(message.toString().equals("incAll")) {
					vertx.eventBus().publish(BROADCAST, getIncrementCounterMsg());
				}else {
					try {
						JSONObject json = new JSONObject(message);
						int receiver = clients.get(1).equals(webSocket.textHandlerID()) ? 2 : 1; 
						if(json.has("desc")) {
							System.out.println("This is desc");
							vertx.eventBus().publish(clients.get(receiver), json.toString());
						}else if(json.has("candidate")) {
							System.out.println("This is candidate");
							vertx.eventBus().publish(clients.get(receiver),  json.toString());
						}
						
					}catch (JSONException ex) {
						System.out.println("The message is not recognized");
					}
					
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
	
	private String getIncrementCounterMsg() {
		JSONObject ackMsg = new JSONObject().put("msg", "ackInc");
		return ackMsg.toString();
	}
}
