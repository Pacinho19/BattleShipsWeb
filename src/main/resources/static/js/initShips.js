var stompClient = null;
var privateStompClient = null;

socket = new SockJS('/ws');
privateStompClient = Stomp.over(socket);
privateStompClient.connect({}, function (frame) {
    var gameId = document.getElementById('gameId').value;
    privateStompClient.subscribe('/start-game/' + gameId, function (result) {
         window.location.href = '/battle-ships/games/' + gameId
    });
});

stompClient = Stomp.over(socket);

function generateRandomShipsBoard(){
   reloadBoard("/battle-ships/games/" + document.getElementById("gameId").value + "/init-ships/generate-random-board");
}

function startGame(){
   reloadBoard("/battle-ships/games/"+ document.getElementById("gameId").value +"/start-game");
}

function reloadBoard(url){
 var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            $("#board").replaceWith(xhr.responseText);
        }
    }
    xhr.open('POST', url, true);
    xhr.send(null);
}

function manuallyInit(){
    console.log("Manual initialization");
}