
window.onload = function(){
	let sock = new WebSocket('ws://localhost:9090');

	
	sock.onopen = function() {
		console.log('open');
	};

	sock.onmessage = function(e) {
		console.log('message', e.data);
		//sock.close();
	};

	sock.onclose = function() {
		console.log('close');
	};

	sock.onerror = function(error) {
		console.log('error', error.message);
	};
	
	document.getElementById("button").onclick = function(){
		sock.send('ping');
	}
	
}