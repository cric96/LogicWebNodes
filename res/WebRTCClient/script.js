let counter = 0;
let label;

let sock;

let connection;

let channel;

window.onload = function(){
	sock = new WebSocket('ws://localhost:8080');
	label = document.getElementById("h1");

	connection = new RTCPeerConnection();
	connection.ondatachannel = receiveChannelCallback;
	setUpSocket(sock);
	label.innerHTML = counter;




	document.getElementById("inc").onclick = () => sock.send('inc');
	document.getElementById("incAll").onclick = () => channel.send('{"msg": "ackInc"}');//sock.send('incAll');
	
	document.getElementById("connect").onclick = () =>  connectPeers();
	
}


function setUpSocket(sock){

	sock.onopen = function() {
		console.log('open');
	};

	sock.onmessage = async ({data}) => {
		try{
			let json = JSON.parse(data);
			if(json.msg){
				console.log('message', json.msg);
				//obj = JSON.parse(data);
				if(json.msg === "ackInc"){
					label.innerHTML = ++counter;
				}
			}else if(json.desc){
				if (json.desc.type === 'offer') {
					await connection.setRemoteDescription(json.desc);
					//const stream = await navigator.mediaDevices.getUserMedia(constraints);
					//stream.getTracks().forEach((track) => connection.addTrack(track, stream));
					await connection.setLocalDescription(await connection.createAnswer());
					sock.send(JSON.stringify({desc: connection.localDescription}));
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

	sock.onclose = function() {
		console.log('close');
	};

	sock.onerror = function(error) {
		console.log('error', error.message);
	};
}


function connectPeers(){
	

	channel = connection.createDataChannel("Channel");
    channel.onopen = () => console.log("Opend p2p");//handleChannelOpen;
	channel.onmessage = (m) => {
		if(JSON.parse(m.data).msg === "ackInc"){
			label.innerHTML = ++counter;
		}
		console.log("msg p2p:", m);// handleChannelOnMessage;
	}
	channel.onclose = () => console.log("close p2p"); //handleChannelClose;

	// Send any ice candidates to the other peer.
	connection.onicecandidate = e => {if(e.candidate != null) sock.send(JSON.stringify(e.candidate))}

	// Let the "negotiationneeded" event trigger offer generation.
	connection.onnegotiationneeded = async () => {
		try {
			await connection.setLocalDescription(await connection.createOffer());
			// Send the offer to the other peer.
			obj = {desc: connection.localDescription};
			sock.send(JSON.stringify(obj));
		} catch (err) {
			console.error(err);
		}
	};

	// Once remote data arrives
	connection.ontrack = (event) => {
		label.innerHTML = event.msg;
	};
}

 function receiveChannelCallback(event) {
    channel = event.channel;
    channel.onopen = () => console.log("Opend p2p");//handleChannelOpen;
	channel.onmessage = (m) => {
		console.log("M.data.msg:", JSON.parse(m.data).msg);
		if(JSON.parse(m.data).msg === "ackInc"){
			label.innerHTML = ++counter;
		}
		console.log("msg p2p:", m);// handleChannelOnMessage;
	}
	channel.onclose = () => console.log("close p2p"); //handleChannelClose;
  }


// Call start() to initiate.
async function start() {
	try {
		// Get local stream, show it in self-view, and add it to be sent.
		const stream = await navigator.mediaDevices.getUserMedia(constraints);
		stream.getTracks().forEach((track) => pc.addTrack(track, stream));
		selfView.srcObject = stream;
	} catch (err) {
		console.error(err);
	}
}

// Handle an error that occurs during addition of ICE candidate.
function handleAddCandidateError() {
	console.log("Oh noes! addICECandidate failed!");
}