class CounterNode{
    
    constructor(id, webSocket){
        this.id = id;
        this.counter = 0;
        this.webSocket = webSocket;
    }

    incrementCounter(){
        this.counter++;
        let obj = JSON.stringify({"id": this.id, "counter": this.counter});
        webSocket.send(obj)
    }

    getCounter(){
        return this.counter;
    }
}