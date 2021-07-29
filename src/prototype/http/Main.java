package prototype.http;

import io.vertx.core.Vertx;

public class Main {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		Server server = new Server(8080);
		vertx.deployVerticle(server);
	}
}
