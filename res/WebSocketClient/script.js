let counter = 0;
let label;

window.onload = function(){
	let sock = new WebSocket('ws://localhost:9090');
	label = document.getElementById("h1");

	setUpSocket(sock);
	label.innerHTML = counter;


	document.getElementById("button").onclick = function(){
		sock.send('inc');
	}

	document.getElementById("buttonAll").onclick = function(){
		sock.send('incAll');
	}
	
}


function setUpSocket(sock){

	sock.onopen = function() {
		console.log('open');
	};

	sock.onmessage = function(msg) {
		console.log('message', msg.data);
		if(msg.data === 'ackInc'){
			label.innerHTML = ++counter;
		}
	};

	sock.onclose = function() {
		console.log('close');
	};

	sock.onerror = function(error) {
		console.log('error', error.message);
	};
}