'use strict';
// Handling of a WebSocket connection
const nickname = document.querySelector('#profile-name').textContent.trim();
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
var currentHistorical = {};

//ad an action to logout
document.getElementById("logout-icon").addEventListener('click',function(){
    currentHistorical = {};
    document.getElementById('logout-form').submit();
    
})

//handle the submision of the messages
document.getElementById("send-button").addEventListener("click", function() {
    let message = document.getElementById("message").value;
    let destination = document.getElementById('current-chat-username').textContent.trim(); // Fixed retrieval
    console.log("sending message", message);
    console.log("destination", destination);
    fetch('/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: message, destination: destination })
    })
    .then(response => response.text())
    .then(data => {
        console.log("Message sent: ", data);
        document.getElementById("message").value = "";  // Clear the input field
        
        // Ensure the destination key exists, then push the message
        currentHistorical[destination] = currentHistorical[destination] || [];
        currentHistorical[destination].push({ message: message, received: false });

        //render the message
        renderMessage(message,false);
    })
    .catch(error => {
        console.error("Error sending message: ", error);
    });
});


if (nickname) {
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
}

function onError() {
    console.log('socket was not able to be used');
}



function renderMessage(message, received) {
    var chatWrapper = document.querySelector('.chat-wrapper');
    var p = document.createElement('p');
    p.textContent = message;
    p.className = received ? 'received-message' : 'sent-message';
    chatWrapper.appendChild(p);
}

function onMessageReceived(payload){
    console.log("Received a message");
    const message = JSON.parse(payload.body);
    console.log(message);
    
    var chatHeader = document.querySelector('#current-chat-username');
    
    if (chatHeader.textContent === message.from) {
        // Append the message to the chat wrapper
        renderMessage(message.body, true);
    }    // Save the message otherwise it will be lost
    currentHistorical[message.from] = currentHistorical[message.from] || [];
    currentHistorical[message.from].push({ message: message.body, received: true });
    

    // Check if the user is already in the sidebar; if not, add them
    var chatList = document.querySelector('.chats ul');
    var existingUserLink = chatList.querySelector(`a[onclick*="${message.from}"]`);
    if (!existingUserLink) {
        var li = document.createElement('li');
        var a = document.createElement('a');
        a.href = "#";
        a.textContent = message.from;
        a.setAttribute("onclick", `showChat('${message.from}')`);
        li.appendChild(a);
        chatList.appendChild(li);
    }
}

// Rendering the messages from history
function showChat(username) {
    var chatWrapper = document.querySelector('.chat-wrapper');
    chatWrapper.innerHTML = '';
    console.log(currentHistorical);
    if (currentHistorical[username]) {
        currentHistorical[username].forEach((message) => {
            renderMessage(message.message, message.received); 
        });
    }

    var chatHeader = document.querySelector('.chat-header p');
    chatHeader.textContent = username;

    // Handle the "active" class
    var chatItems = document.querySelectorAll('.chat-item-container');
    console.log(chatItems);
    chatItems.forEach(function(item) {
        item.classList.remove('active');
        // Get the username from the chat item
        var chatUsername = item.querySelector('.chat-username-ref').textContent;
        console.log(chatUsername)
        if (chatUsername + "@alumchat.lol" === username) {
            item.classList.add('active');
        }
    });
}

