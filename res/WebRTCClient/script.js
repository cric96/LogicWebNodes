const serverURL = 'ws://localhost:8080';

let counter = 0;
let label;

let webSocket;

let connection;
let channel;

let connectionsManager;

window.onload = function(){
	connectionsManager = new WebrtcManager();
	setUpDOM();
	setUpWebSocket();
}

// Set things up, connect event listeners
function setUpDOM() {
	label = document.getElementById("h1");
	label.innerHTML = counter;

    // Set event listeners for user interface widgets

   	document.getElementById("inc").onclick = () => webSocket.send('inc');
	document.getElementById("incAll").onclick = () => connectionsManager.sendToAll('{"msg": "ackInc"}');
	document.getElementById("connect").onclick = () =>  connectionsManager.addConnection();;
  }


function setUpWebSocket(){
	webSocket = new WebSocket(serverURL);

	webSocket.onopen = function() {
		console.log('open');
	};

	webSocket.onmessage = async ({data}) => {
		try{
			let json = JSON.parse(data);
			if(json.msg){
				console.log('message', json.msg);
				if(json.msg === "ackInc"){
					label.innerHTML = ++counter;
				}
			}else{
				connectionsManager.elaborateMsg(json);
			} 
		}catch(err){
			console.error(err);
		}
		
	};

	webSocket.onclose = function() {
		console.log('close');
	};

	webSocket.onerror = function(error) {
		console.log('error', error.message);
	};
}
