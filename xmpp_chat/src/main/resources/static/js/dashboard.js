'use strict';
//handling of a web socket connection
const nickname = document.querySelector('#profile-name').textContent.trim();


console.log(nickname);

const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);


if (nickname) {
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
}

function onError() {
    console.log('socket was not able to be used')
}

document.getElementById("send-button").addEventListener("click", function() {
    let message = document.getElementById("message").value;

    fetch('/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({message: message , destination:"arg21527"})
    })
    .then(response => response.text())
    .then(data => {
        console.log("Message sent: ", data);
        document.getElementById("message").value = "";  // Clear the input field
    })
    .catch(error => {
        console.error("Error sending message: ", error);
    });
});

function onMessageReceived(payload){
    console.log("Recevied a meessage")
    const message = JSON.parse(payload.body);
    console.log(message);
    console.log(message.from);
    console.log(message.body);
    var chatHeader = document.querySelector('#current-chat-username');
    var chatWrapper = document.querySelector('.chat-wrapper');
    if (chatHeader.textContent === message.from) {
        // Append the message to the chat wrapper
        var p = document.createElement('p');
        p.textContent = message.body;
        chatWrapper.appendChild(p);
    }

    // Check if the user is already in the sidebar; if not, add them
    var chatList = document.querySelector('.chats ul');
    var existingUserLink = chatList.querySelector('a[onclick*="' + message.from + '"]');
    if (!existingUserLink) {
        var li = document.createElement('li');
        var a = document.createElement('a');
        a.href = "#";
        a.textContent = message.from;
        a.setAttribute("onclick", "showChat('" + message.from + "')");
        li.appendChild(a);
        chatList.appendChild(li);
    }
}


// rendering the messages 
function showChat(username) {
    console.log("Fetching messages for chat with: ", username);

    // Fetch messages for the clicked username from the server
    fetch('/chat/' + username)
        .then(response => response.json())
        .then(messages => {
            var chatWrapper = document.querySelector('.chat-wrapper');
            chatWrapper.innerHTML = '';

            messages.forEach(function(message) {
                var p = document.createElement('p');
                p.textContent = message;
                chatWrapper.appendChild(p);
            });

            // Update the header
            var chatHeader = document.querySelector('.chat-header p');
            chatHeader.textContent = username;
        })
        .catch(error => console.error('Error fetching chat messages:', error));
}