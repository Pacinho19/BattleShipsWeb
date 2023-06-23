var manuallyInitialization = false;
var nextShip;
var selectedCells = [];
var shipType = 'VERTICAL';

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

function checkMode(){
    if(document.getElementById('shipsManuallyInit'))
         manuallyInitPart2();
}
function generateRandomShipsBoard(){
   clearManuallyInitialization();
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
     manuallyInitialization = true;
     reloadBoardManuallyInit();
}

function reloadBoardManuallyInit(){
 var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            $("#board").replaceWith(xhr.responseText);
            manuallyInitPart2();
        }
    }
    xhr.open('POST', "/battle-ships/games/"+ document.getElementById("gameId").value +"/init-ships/clear-board", true);
    xhr.send(null);
}

function manuallyInitPart2(){
    selectedCells = [];
    nextShip = document.getElementById("nextShipManuallyInit").value;

     if(nextShip==0)
        return;

    console.log("To place: " + nextShip);
    addClickListeners();
}

function clearManuallyInitialization(){
    manuallyInitialization = false;
}

var boardCellClickFunction = function() {
 var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            $("#board").replaceWith(xhr.responseText);
            manuallyInitPart2();
        }
    }
    xhr.open('POST', "/battle-ships/games/"+ document.getElementById("gameId").value +"/init-ships/place", true);
    xhr.setRequestHeader("Content-Type", "application/json");

    pos = selectedCells[0].id.split('_');
    let shipObj = {x:pos[0], y:pos[1], mastsCount:nextShip, shipType: shipType};
    var data = JSON.stringify(shipObj);
    xhr.send(data);
};

var boarCellMouseoverFunction = function(){
    if(selectedCells.length>0){
        selectedCells.forEach(lastCellObj => {
          lastCellElement = document.getElementById(lastCellObj.id);
                lastCellElement.style.backgroundColor =  lastCellObj.backColor;
        });
    }

    selectedCells = [];

    toSelect = [];
    for(let i=0;i<nextShip;i++){
        element = getNextCellElement(i, this.id);
        if(element==null)
            return;

        isFreeCell = element.getAttribute("data-free-cell")  == 'true';
        if(!isFreeCell) return;

        toSelect.push(element);
    }

    if(toSelect.length!=nextShip)
        return;

    toSelect.forEach(element => {
        selectedCells.push({id: element.id, backColor: element.style.backgroundColor})
        element.style.backgroundColor = 'yellow';
    });
};

function getNextCellElement(offset, baseId){
    offsetX = shipType=='HORIZONTAL' ? offset : 0;
    offsetY = shipType=='VERTICAL' ? offset : 0;

    pos = baseId.split('_');
    newId = (parseInt(pos[0]) + offsetX) + '_' + (parseInt(pos[1]) + offsetY);
    return document.getElementById(newId);
}

function addClickListeners(){
    elements = document.getElementsByClassName("boardCell");
       Array.from(elements).forEach(function(element) {

               isFreeCell = element.getAttribute("data-free-cell") == 'true';
               if(!isFreeCell){
                    console.log("element " + element + " is not free");
                    return;
               }

            element.addEventListener("click", boardCellClickFunction);
            element.addEventListener("mouseover", boarCellMouseoverFunction);
      });
}