'use strict';
// Handling of a WebSocket connection
const nickname = document.querySelector('#profile-name').textContent.trim();
const socket = new SockJS('/ws');
const socketg = new SockJS('/ws/group');
const stompClient = Stomp.over(socket);
const stompClientGroups = Stomp.over(socketg);
var currentHistorical = {};
var currentHistoricalGroups = {};
let isConnected = false;
//ad an action to logout
document.getElementById("logout-icon").addEventListener('click',function(){
    currentHistorical = {};
    document.getElementById('logout-form').submit();
    
})

document.getElementById("delete-account-button").addEventListener('click',function (){
    Swal.fire({
        title: 'Delete Account',
        showDenyButton: true,
        text: 'Deleting the account is permanent, proceed?',
        confirmButtonText: "Delete Account",
        confirmButtonColor: "#eb4034",
        denyButtonColor: "#454747",

        
    }).then((result) => {
        /* Read more about isConfirmed, isDenied below */
        if (result.isConfirmed) {
            fetch('/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({})  // or any necessary data to send
            })
            .then(response => response.json())
            .then(data => {
                console.log(data);
                if (data["succes"] === "true") {
                    // Redirect to the login page if the deletion was successful
                    if (data["redirect"]) {
                        Swal.fire('Success', data.message, 'success');
                        window.location.href = data["redirect"];
                    } 
                } else {
                    Swal.fire('Error', data.message, 'error');
                }
            })
            .catch((error) => {
                console.error('Error:', error);
                Swal.fire('Error', 'An error occurred while processing the request.', 'error');
            });
        } 
      });
})

//handle the submision of the messages
document.getElementById("send-button").addEventListener("click", function() {
    let message = document.getElementById("message").value;
    let destination = document.getElementById('current-chat-username').textContent.trim(); // Fixed retrieval
    fetch('/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: message, destination: destination })
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);
        document.getElementById("message").value = "";  // Clear the input field
        console.log("Success value:", data["succes"]);
        if (data["succes"] === "true" || data["success"] === true)   {
                // Ensure the destination key exists, then push the message
            if (search(destination, FoundGroups) != ""){
                currentHistoricalGroups[destination] = currentHistoricalGroups[destination] || [];
                currentHistoricalGroups[destination].push({body :message, from : destination});
            }else{
                currentHistorical[destination] = currentHistorical[destination] || [];
                currentHistorical[destination].push({ message: message, received: false });
                //render the message
                renderMessage(message,false);
            }   
        }else{
            Swal.fire('error',data.message,'error');
        }
        
        
    })
    .catch(error => {
        Swal.fire({
            title: 'Error! Message could not be sent',
            text:error,
            icon: 'error',
            confirmButtonText: 'Okay'
          })
    });
    
});


if (nickname) {
    if(!isConnected) {
    stompClient.connect({}, onConnected, onError);
    stompClientGroups.connect({}, onConnectedGroups, onError);
}
}

function onConnected() {
    isConnected = true
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    
}
function onConnectedGroups(){
    isConnected = true
    stompClientGroups.subscribe(`/user/${nickname}/queue/group-messages`, onGrupalMessageReceived);
}

function onError() {
}



function renderMessage(message, received) {
    var chatWrapper = document.querySelector('.chat-wrapper');
    var p = document.createElement('p');
    p.textContent = message;
    p.className = received ? 'received-message' : 'sent-message';
    chatWrapper.appendChild(p);
}
function renderMessageGrupal(sender,body){
    var chatWrapper = document.querySelector('.chat-wrapper');
    var div = document.createElement('div');
    var msg = document.createElement('p');
    var from = document.createElement('p');
    msg.textContent=body;
    msg.style.margin = 0;
    from.textContent=sender;
    msg.style.margin = 0
    div.style.marginBottom = "10px";
    document.getElementById('profile-name').textContent
    if (document.getElementById('profile-name').textContent == sender){
        div.className = 'sent-message';
    }else{
        div.className = 'received-message';
    }
    div.appendChild(msg);
    div.appendChild(from);
    div.style.display = 'flex';
    div.style.flexDirection = 'column';
    div.style.alignItems = 'flex-start';
    div.style.justifyContent = 'center';
    chatWrapper.appendChild(div);
}
function createUsernameListElement(chat){
    const li = document.createElement('li');
    li.classList.add('chat-item-container');
    li.setAttribute('data-user',chat);
    li.setAttribute('onclick', `showChat('${chat}')`);

    // Determine the active class based on the chat username
    if (chat.substring(0, chat.indexOf('@')) === document.querySelector('#current-chat-username').textContent) {
        li.classList.add('active');
    }

    // Create the chat initial span
    const chatInitialSpan = document.createElement('span');
    chatInitialSpan.classList.add('chat-initial');
    const chatInitialP = document.createElement('p');
    chatInitialP.textContent = chat.charAt(0).toUpperCase();
    chatInitialSpan.appendChild(chatInitialP);
    li.appendChild(chatInitialSpan);

    // Create the chat username anchor
    const chatUsernameAnchor = document.createElement('a');
    chatUsernameAnchor.classList.add('chat-username-ref');
    chatUsernameAnchor.textContent = chat.substring(0, chat.indexOf('@'));
    li.appendChild(chatUsernameAnchor);
    return li;
}
function onGrupalMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    var chatHeader = document.querySelector('#current-chat-username');
    if (chatHeader.textContent === message.roomJid){
        renderMessageGrupal(message.body, message.from);
    }
    currentHistoricalGroups[message.roomJid] = currentHistoricalGroups[message.roomJid] || []; //
    currentHistoricalGroups[message.roomJid].push({body :message.body, from : message.from});
}
function onMessageReceived(payload){
    
    const message = JSON.parse(payload.body);
    console.log("Received message")
    console.log(message)
    var chatHeader = document.querySelector('#current-chat-username');
    // Check if the user is already in the sidebar; if not, add them
    var chatList = document.getElementById('current-chat-list');
    var existingUserLink = document.querySelector(`#current-chat-list li[data-user="${message.from}"]`);
    
    console.log(existingUserLink);
    if (!existingUserLink) {
        var li = createUsernameListElement(message.from);
        chatList.appendChild(li);
    }

    if (chatHeader.textContent === message.from) {
        // Append the message to the chat wrapper
        renderMessage(message.body, true);
    }    // Save the message otherwise it will be lost
    currentHistorical[message.from] = currentHistorical[message.from] || [];
    currentHistorical[message.from].push({ message: message.body, received: true });
   
}

// Rendering the messages from history
function showChat(username) {
    let messagesDisplay = document.getElementById('chat-page')
    if (messagesDisplay.classList.contains('inactive')){
        let userSettings = document.getElementById('user-settings')
        userSettings.classList.remove('active')
        userSettings.classList.add('inactive')
        messagesDisplay.classList.remove('inactive')
        messagesDisplay.classList.add('active')
        document.getElementById('rooms').classList.remove("active");
        document.getElementById('rooms').classList.add("inactive");
        Settingsredered = false;
    }

    var chatHeader = document.querySelector('.chat-header p');
    chatHeader.textContent = username;

    var chatWrapper = document.querySelector('.chat-wrapper');
    chatWrapper.innerHTML = '';
    if (currentHistorical[username]) {
        currentHistorical[username].forEach((message) => {
            renderMessage(message.message, message.received); 
        });
            // Handle the "active" class
        var chatItems = document.querySelectorAll('.chat-item-container');
        chatItems.forEach(function(item) {
            item.classList.remove('active');
            // Get the username from the chat item
            var chatUsername = item.querySelector('.chat-username-ref').textContent;
            if (chatUsername + "@alumchat.lol" === username) {
                item.classList.add('active');
            }
        });
    } 
    if (currentHistoricalGroups[username]) {
        currentHistoricalGroups[username].forEach((message) => {
            renderMessageGrupal(message.body, message.from); 
        });
    }

}

