function shoot(yValue, xValue){
  var xhr = new XMLHttpRequest();
    var url = '/battle-ships/games/' + document.getElementById("gameId").value + '/shoot';
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function () { };

    let shootObj = {x:xValue, y:yValue};
    var data = JSON.stringify(shootObj);
    xhr.send(data);
}

var stompClient = null;
var privateStompClient = null;

socket = new SockJS('/ws');
privateStompClient = Stomp.over(socket);
privateStompClient.connect({}, function (frame) {
    var gameId = document.getElementById('gameId').value;
    privateStompClient.subscribe('/reload-board/' + gameId, function (result) {
        updateBoard();
    });
});

stompClient = Stomp.over(socket);

function updateBoard() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            $("#board").replaceWith(xhr.responseText);
        }
    }
    xhr.open('GET', "/battle-ships/games/" + document.getElementById("gameId").value + "/board/reload", true);
    xhr.send(null);
}