Settingsredered = false;
document.getElementById("profile-details-option").addEventListener('click',function(){
    if(!Settingsredered){
    //hide the chats from the navbar
    let chatsSidebar = document.getElementById('sidebar-chat-container');
    chatsSidebar.style.display = 'none';
    //hide the chat options menu
    let grupalOptions = document.getElementById('grupal-chat-container');
    grupalOptions.style.display = 'none';
    let newChatOptions = document.getElementById('new-chat');
    newChatOptions.style.display = 'none';
    //hide the messages and chatting display
    let messagesDisplay = document.getElementById('chat-page')
    messagesDisplay.classList.remove('active')
    messagesDisplay.classList.add('inactive')

    //show the user settings
    let userSettings = document.getElementById('user-settings')
    userSettings.classList.remove('inactive')
    userSettings.classList.add('active')

    //show the go back button
    document.getElementById("back-to-chats").style.display = 'inherit';
    document.getElementById("add-contact").style.display = 'inherit';

    //show the contacts list container
    document.getElementById("sidebar-contacts-container").style.display = 'inherit';

    fetch('/get-contacts', {
        method: 'GET'
        })
        .then(response => response.text())
        .then(data => {
            var contactList = document.getElementById('contact-list');
            contactList.innerHTML = "";
            contacts = JSON.parse(data);
            contacts.forEach(contact => {
                var userLI = createUsernameListElement(contact['jid'])
                contactList.appendChild(userLI);
            });
        })
        .catch(error => {
            console.error("Error getting contacts ", error);
        });
        Settingsredered = true;
    }
})
//handle the change back into chats
document.getElementById("back-to-chats").addEventListener('click',function(){
    Settingsredered = false;
    //show the chats from the navbar
    let chatsSidebar = document.getElementById('sidebar-chat-container');
    chatsSidebar.style.display = 'inherit';
    //show the chat options menu
    let grupalOptions = document.getElementById('grupal-chat-container');
    grupalOptions.style.display = 'inherit';
    let newChatOptions = document.getElementById('new-chat');
    newChatOptions.style.display = 'inherit';
    //show the messages and chatting display
    let messagesDisplay = document.getElementById('chat-page')
    messagesDisplay.classList.remove('inactive')
    messagesDisplay.classList.add('active')

    //show the user settings
    let userSettings = document.getElementById('user-settings')
    userSettings.classList.remove('active')
    userSettings.classList.add('inactive')
    
    document.getElementById("back-to-chats").style.display = 'none';
    document.getElementById("add-contact").style.display = 'none';
    // hide the contact list:
    document.getElementById("sidebar-contacts-container").style.display = 'none';
})

document.getElementById("add-contact").addEventListener('click', async function() { 
    const { value: contact } = await Swal.fire({
        title: "Agregar Contacto",
        input: "text",
        inputLabel: "Username",
        showCancelButton: true,
        inputValidator: (value) => {
          if (!value) {
            return "You need to write something!";
          }
        }
      });
      //pass the request to the server
      fetch('/add-contact', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ contactJid: contact+"@alumchat.lol" })
        })
        .then(response => response.text())
        .then(data => {
            console.log(data)
        })
        .catch(error => {
            console.error("Error sending message: ", error);
        });
} );

