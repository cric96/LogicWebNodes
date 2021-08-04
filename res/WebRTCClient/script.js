const serverURL = 'ws://localhost:8080';

let counter = 0;
let label;

let webSocket;

let connection;
let channel;

window.onload = function(){
	man = new WebrtcManager();
	setUpDOM();
	setUpWebSocket();
}

// Set things up, connect event listeners
function setUpDOM() {
	label = document.getElementById("h1");
	label.innerHTML = counter;

    // Set event listeners for user interface widgets

   	document.getElementById("inc").onclick = () => webSocket.send('inc');
	document.getElementById("incAll").onclick = () => channel?.send('{"msg": "ackInc"}');
	document.getElementById("connect").onclick = () =>  connectPeer();
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
			}else if(json.desc){
				if (json.desc.type === 'offer') {
					connection = createConnection();
					await connection.setRemoteDescription(json.desc);
					await connection.setLocalDescription(await connection.createAnswer());
					webSocket.send(JSON.stringify({desc: connection.localDescription}));
				} else if (json.desc.type === 'answer') {
					await connection.setRemoteDescription(json.desc);
				} else {
					console.log('Unsupported SDP type.');
				}

			}else if(json.candidate){
				console.log("received candidate: ", json);
				await connection.addIceCandidate(json);
			}else{
				console.log("the received msg is not recognized: " + data);
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


function connectPeer(){

	connection = createConnection();
	
	
	channel = connection.createDataChannel("Channel");
    channel.onopen = () => console.log("Opend p2p");//handleChannelOpen;
	channel.onmessage = handleReceiveMessage;
	channel.onclose = () => console.log("close p2p"); //handleChannelClose;

	// Send any ice candidates to the other peer.
	connection.onicecandidate = e => {if(e.candidate != null) webSocket.send(JSON.stringify(e.candidate))}

	// Let the "negotiationneeded" event trigger offer generation.
	connection.onnegotiationneeded = async () => {
		try {
			await connection.setLocalDescription(await connection.createOffer());
			// Send the offer to the other peer.
			obj = {desc: connection.localDescription};
			webSocket.send(JSON.stringify(obj));
		} catch (err) {
			console.error(err);
		}
	};

	// Once remote data arrives
	connection.ontrack = (event) => {
		label.innerHTML = event.msg;
	};
}

function createConnection(){
	let conn = new RTCPeerConnection();
	conn.ondatachannel = receiveChannelCallback;
	conn.oniceconnectionstatechange = handleReceiveChannelStatusChange;
	return conn; 
}

 function receiveChannelCallback(event) {
    channel = event.channel;
    channel.onopen = () => console.log("Opend p2p");//handleChannelOpen;
	channel.onmessage = handleReceiveMessage;
	channel.onclose = () => console.log("close p2p"); //handleChannelClose;
  }


// Handle an error that occurs during addition of ICE candidate.
function handleAddCandidateError() {
	console.log("Oh noes! addICECandidate failed!");
}

function handleReceiveMessage(m){
	console.log("M.data.msg:", JSON.parse(m.data).msg);
	if(JSON.parse(m.data).msg === "ackInc"){
		label.innerHTML = ++counter;
	}
	//console.log("msg p2p:", m);// handleChannelOnMessage;
}

function handleReceiveChannelStatusChange(event){
	if(connection.iceConnectionState == 'disconnected'){
			console.log("disconnected");
	}
}