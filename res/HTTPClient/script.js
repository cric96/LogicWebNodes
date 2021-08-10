const target = {
	counter: 0
};

const handler = {
		set: function(target, prop, value){
			if(prop === 'counter' && (value - target.counter) === 1){
				target.counter += 1;
				label.innerHTML = target.counter;
				return true;
			}
		}
	};

const proxy = new Proxy(target, handler);

window.onload = function(){
	let counter = 0;
	label = document.getElementById("h1");

	label.innerHTML = target.counter;
	
	document.getElementById("button").onclick = function(){
		proxy.counter += 1;
	}

}

