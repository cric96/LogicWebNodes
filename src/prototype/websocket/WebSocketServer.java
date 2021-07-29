package prototype.websocket;

import io.vertx.core.AbstractVerticle;

public class WebSocketServer extends AbstractVerticle {
	private final static int HTTP_OK = 200; // http ok code
	
	private final int port;
	
	public WebSocketServer(final int port) {
		this.port = port;
	}

}
