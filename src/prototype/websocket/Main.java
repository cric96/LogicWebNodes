package prototype.websocket;

import io.vertx.core.Vertx;

public class Main {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		WebSocketServer server = new WebSocketServer(8080);
		vertx.deployVerticle(server);
	}
}
