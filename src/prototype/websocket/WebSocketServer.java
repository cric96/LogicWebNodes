package prototype.websocket;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.HSTSHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class WebSocketServer extends AbstractVerticle {
	private final static int HTTP_OK = 200; // http ok code
	
	private final int port;
	
	public WebSocketServer(final int port) {
		this.port = port;
	}
	
	@Override 
	public void start() {
		startServer(vertx);
	}

	private void startServer(Vertx vertx) {
		
		HttpServer server = vertx.createHttpServer();
	
		server.webSocketHandler(webSocket -> {
			
			webSocket.handler(msg -> {
				System.out.println("The msg is: " + msg);
				if(msg.toString().equals("ping")) {
					webSocket.writeTextMessage("pong");
					System.out.println("Send Pong");
				}
				
			});
		}).listen(port);
		
		
//		Router router = Router.router(vertx);
//		router.route().handler(HSTSHandler.create());
//		//router.route().handler(HSTSHandler.create().allowedHeader("Access-Control-Allow-Origin"));
//		router.route("/static/*").handler(StaticHandler.create("res/WebSocketClient/"));
//		server.requestHandler(router).listen(port);
		
	}

}
