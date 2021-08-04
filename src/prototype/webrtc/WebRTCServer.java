package prototype.webrtc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.*;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class WebRTCServer extends AbstractVerticle {
	private final static String BROADCAST = "Broadcast";
	private final int port;
	private final Map<String, List<String>> clients;
	
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
			
			clients.entrySet().forEach(e -> e.getValue().add(webSocket.textHandlerID()));
			clients.put(webSocket.textHandlerID(), new LinkedList<>(clients.keySet()));
			
			System.out.println("Clients: \n" + clients.entrySet().toString());
			
			
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
						
						if(json.has("desc")) {
							System.out.println("This is desc");
							String type = json.getJSONObject("desc").get("type").toString();
							if(type.equals("offer")) {
								vertx.eventBus().publish(clients.get(webSocket.textHandlerID()).get(0), json.toString());
							}else if(type.equals("answer")) {
								vertx.eventBus().publish(clients.get(webSocket.textHandlerID()).get(0), json.toString());
							}else {
								System.out.println("Wrong type of msg");
							}
							
							
						}else if(json.has("candidate")) {
							System.out.println("This is candidate");
							vertx.eventBus().publish(clients.get(webSocket.textHandlerID()).get(0),  json.toString());
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
                clients.remove(webSocket.textHandlerID());
                clients.entrySet().forEach(e -> e.getValue().remove(webSocket.textHandlerID()));
            });
			
		}).listen(port);	
		
	}
	
	private String getIncrementCounterMsg() {
		JSONObject ackMsg = new JSONObject().put("msg", "ackInc");
		return ackMsg.toString();
	}
}
