package prototype.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.json.JSONArray;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import node.*;

public class Server extends AbstractVerticle {
	private final static int HTTP_OK = 200; // http ok code
	private final static int HTTP_NOT_FOUND = 404;
	private final int port;
	
	private final Map<Integer, CounterNode> nodes;
	
	
	
	public Server(final int port) {
		this.port = port;
		Iterator<Integer> idGenerator = Stream.iterate(0, i -> i + 1).iterator(); 
		
		nodes = new HashMap<>();
		for(int i = 0; i < 5; i++) {
			int id = idGenerator.next();
			nodes.put(id, new CounterNodeImpl(id));
		}
		
		new Thread(() -> {
			while(true) {
				try {
					nodes.entrySet().stream().forEach(n -> n.getValue().incrementCounter());
					Thread.sleep(5000);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	
	@Override 
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.get("/api/nodes/:id").handler(this::handleGetNode);
		router.get("/api/nodes").handler(this::handleGetNodes);
		router.put("/api/nodes/:id/counter").handler(this::handlerIncrementCounter);
		//router.route("/static/*").handler(StaticHandler.create("res/client/"));  // for js client
		vertx.createHttpServer()
				.requestHandler(router)
				.listen(port);
	}
		
	/*
	 * Return state of the node 
	 */
	private void handleGetNode(RoutingContext routingContext) {
		int id = Integer.valueOf(routingContext.request().getParam("id"));
		
		HttpServerResponse response = routingContext.response();
		if(nodes.containsKey(id)) {
			response.setStatusCode(HTTP_OK)
			.putHeader("content-type", "application/json")
			.end(nodes.get(id).getState().toString());
		}else {
			response.setStatusCode(HTTP_NOT_FOUND).end();
		}
	}
	
	/*
	 * Return state of all nodes
	 */
	private void handleGetNodes(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		
		JSONArray arr = new JSONArray();
		nodes.entrySet().stream().map(m -> m.getValue().getState()).forEach(j -> arr.put(j));
		
		response.setStatusCode(HTTP_OK)
		.putHeader("content-type", "application/json")
		.end(arr.toString());
	}
	
	/*
	 * Use id for searching a node and increment its counter
	 */
	private void handlerIncrementCounter(RoutingContext routingContext) {
		int id = Integer.valueOf(routingContext.request().getParam("id"));
		
		HttpServerResponse response = routingContext.response();
		if(nodes.containsKey(id)) {
			nodes.get(id).incrementCounter();
			response.setStatusCode(HTTP_OK).end();
		}else {
			response.setStatusCode(HTTP_NOT_FOUND).end();
		}
	}
}
