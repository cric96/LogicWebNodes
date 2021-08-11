const serverURL = 'ws://localhost:8080';
let list;

window.onload = function(){
	setUpWebSocket();
	
	list = document.getElementById("list");	
	
}

function setUpWebSocket(){
	webSocket = new WebSocket(serverURL);

	webSocket.onopen = () => {
		console.log('Open WebSocket connection with Server');
		webSocket.send("subscribe");
		
	};
	webSocket.onclose = () => console.log('Close WebSocket connection with Server');
	webSocket.onerror =  (error) => console.log('error', error.message);

	webSocket.onmessage = (msg) => {
		console.log('Message', msg.data);

		try{	
			let json = JSON.parse(msg.data);
			let listItem = document.getElementById(json.id);
			console.log("listitem", listItem);
			if(listItem != null){
				listItem.lastChild.textContent = " | Counter: " + json.counter;

			}else{
				let li = document.createElement('li');
				li.id = json.id;
				let idLabel = document.createTextNode("Node Id: " + json.id);
				let counterLabel = document.createTextNode(" |  Counter: " + json.counter);
				li.appendChild(idLabel);
				li.appendChild(counterLabel);

				let children = list.children;

				list.appendChild(li);
			}	
		}catch(err){
			console.error(err);
		}
	}
}
