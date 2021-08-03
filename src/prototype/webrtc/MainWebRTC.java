package prototype.webrtc;

import io.vertx.core.Vertx;
import prototype.websocket.WebSocketServer;

public class MainWebRTC {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		WebRTCServer server = new WebRTCServer(8080);
		vertx.deployVerticle(server);
	}

}
