const serverURL = 'ws://localhost:8080';

let label;

let webSocket;

let connectionsManager;

let node;

window.onload = function(){
	connectionsManager = new WebrtcManager();
	setUpDOM();
	setUpWebSocket();	
}

// Set things up, connect event listeners
function setUpDOM() {
	label = document.getElementById("h1");
    // Set event listeners for user interface widgets

   	document.getElementById("inc").onclick = () => webSocket.send('inc');
	document.getElementById("incAll").onclick = () => connectionsManager.sendToAll('{"msg": "ackInc"}');
  }


function setUpWebSocket(){
	webSocket = new WebSocket(serverURL);

	webSocket.onopen = () => console.log('Open WebSocket connection with Server');
	webSocket.onclose = () => console.log('Close WebSocket connection with Server');
	webSocket.onerror =  (error) => console.log('error', error.message);

	webSocket.onmessage = async ({data}) => {
		try{
			let json = JSON.parse(data);
			//console.log('JSON: ', json);
			if(json.msg){
				console.log('message', json.msg);
				if(json.msg === "ackInc"){
					node.incrementCounter();
					label.innerHTML = node.getCounter();
				}else if(json.msg === "connectionsAvailable"){
					connectionsManager.connectionAvailable();
				}
			}else if(json.yourid != null){
				node = new CounterNode(json.yourid, webSocket);
				label.innerHTML = node.getCounter();
			}else{
				connectionsManager.elaborateMsg(json);
			} 
		}catch(err){
			console.error(err);
		}
	};
}
