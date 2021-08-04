class WebrtcManager{

	addConnection(){
		this.peer = new ConnectedPeer(); 
		this.peer.newChannel();
	}

	sendToAll(msg){
		this.peer.channel.send(msg);
	}

	async elaborateMsg(json){
		if(json.desc){
			if (json.desc.type === 'offer') {
				this.peer = new ConnectedPeer();
				await this.peer.peerConnection.setRemoteDescription(json.desc);
				await this.peer.peerConnection.setLocalDescription(await this.peer.peerConnection.createAnswer());
				webSocket.send(JSON.stringify({desc: this.peer.peerConnection.localDescription}));
			} else if (json.desc.type === 'answer') {
				await this.peer.peerConnection.setRemoteDescription(json.desc);
			} else {
				console.log('Unsupported SDP type.');
			}

		}else if(json.candidate){
			console.log("received candidate: ", json);
			await this.peer.peerConnection.addIceCandidate(json);
		}else{
			console.log("the received msg is not recognized: " + data);
		}
	}
}

class ConnectedPeer{

	constructor(){
		this.peerConnection =  new RTCPeerConnection();
		this.peerConnection.ondatachannel = (event) =>  this.#setUpChannel(event.channel);
		this.peerConnection.oniceconnectionstatechange = (e) => {
			if(this.peerConnection.iceConnectionState == 'disconnected'){
				console.log("disconnected");
			}
		};
	}
	
	newChannel(){
		this.#setUpChannel(this.peerConnection.createDataChannel("Channel"));

		// Send any ice candidates to the other peer.
		this.peerConnection.onicecandidate = e => {if(e.candidate != null) webSocket.send(JSON.stringify(e.candidate))}

		// Let the "negotiationneeded" event trigger offer generation.
		this.peerConnection.onnegotiationneeded = async () => {
			try {
				await this.peerConnection.setLocalDescription(await this.peerConnection.createOffer());
				// Send the offer to the other peer.
				let obj = {desc: this.peerConnection.localDescription};
				webSocket.send(JSON.stringify(obj));
			} catch (err) {
				console.error(err);
			}
		};
	}

	#setUpChannel(newChannel) {
		this.channel = newChannel;
		this.channel.onopen = () => console.log("Open a new P2P connection");//handleChannelOpen;
		this.channel.onmessage = handleReceiveMessage;
		this.channel.onclose = () => console.log("Close a P2P connection"); //handleChannelClose;
	}

}

function handleReceiveMessage(m){
	console.log("M.data.msg:", JSON.parse(m.data).msg);
	if(JSON.parse(m.data).msg === "ackInc"){
		label.innerHTML = ++counter;
	}
}
