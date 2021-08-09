# LogicWebNodes
Import java project in Eclipse
`File -> Import -> Maven -> Existing Maven Projects`

For lunch:

## HTTP prototype
1. Lunch Server `src/prototype/http/Main.java`
2. Open a web page `http://localhost:8080/api/nodes/id` where `ìd` is a number from 0 to 4
3. Open a web page `http://localhost:8080/api/nodes`
4. Refresh web pages for get updated data


## WebSocket prototype
1. Lunch Server `src/prototype/websocket/MainWebSocket.java`
2. Open 2 or more web pages `..../res/WebSocketClient/index.html` in browser(Tested only in Chrome)
3. Click "Increment" for ask server to increment counter
4. Click "Increment All" for ask server to increment all counters

## WebRTC prototype
1. Lunch Server `src/prototype/webrtc/MainWebRTC.java`
2. Open 2 or more web pages `..../res/WebRTCClient/index.html` in browser(Tested only in Chrome).
  Each new web page will connect automatically with all peers
3. Click "Increment" for ask server to increment counter
4. Click "Increment All" for increment counters of all connected peers 

P.S. server side code must be rewrited
