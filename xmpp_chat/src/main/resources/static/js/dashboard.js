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

//handling of a web socket connection
var socket = new WebSocket("ws://localhost:8080/chat");

    socket.onmessage = function(event) {
        var messageData = JSON.parse(event.data);
        var fromUser = messageData.from;
        var messageBody = messageData.body;

        // Check if the chat from this user is already open
        var chatWrapper = document.querySelector('.chat-wrapper');
        var chatHeader = document.querySelector('.chat-header p');
        if (chatHeader.textContent === fromUser) {
            // Append the message to the chat wrapper
            var p = document.createElement('p');
            p.textContent = messageBody;
            chatWrapper.appendChild(p);
        }

        // Check if the user is already in the sidebar; if not, add them
        var chatList = document.querySelector('.chats ul');
        var existingUserLink = chatList.querySelector('a[onclick*="' + fromUser + '"]');
        if (!existingUserLink) {
            var li = document.createElement('li');
            var a = document.createElement('a');
            a.href = "#";
            a.textContent = fromUser;
            a.setAttribute("onclick", "showChat('" + fromUser + "')");
            li.appendChild(a);
            chatList.appendChild(li);
        }
    };