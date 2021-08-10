const serverURL = 'ws://localhost:8080';

let counter = 0;
let label;

let sock;

window.onload = function(){
	setUpSocket();
	
	label = document.getElementById("h1");
	label.innerHTML = counter;

	document.getElementById("button").onclick = () => sock.send('inc');
	document.getElementById("buttonAll").onclick = () => sock.send('incAll');
}


function setUpSocket(){
	sock = new WebSocket(serverURL);

	sock.onopen = () => console.log('open');
	sock.onclose = () => console.log('close');
	sock.onerror = () => console.log('error', error.message);

	sock.onmessage = function(msg) {
		console.log('message', msg.data);
		if(msg.data === 'ackInc'){
			label.innerHTML = ++counter;
		}
	};
}