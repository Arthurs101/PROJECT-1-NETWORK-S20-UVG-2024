Settingsredered = false;
FoundGroups = [];
Mygroups = [];
MyContacts = {};
listMode = 1;
function search(JID, myArray){
    for (let i=0; i < myArray.length; i++) {
        if (myArray[i]['JID'] == JID) {
            return myArray[i]['Name'];
        }
    }
    return ""
}
async function  fetchGroups(){
    
    await fetch('/rooms', {
        method: 'GET'
        }).then(response => response.json())
        .then(body => {
            FoundGroups = body || [];
        }
        );

    await fetch('/rooms/mine', {
        method: 'GET'
        }).then(response => response.json())
        .then(body => {
            Mygroups = body || [];
        }
        );
        Mygroups.forEach(group => {
           group['Name'] = search(group['roomJid'],FoundGroups);;
        })
}   

function createGroupElement(groupName,join,JID){
    const li = document.createElement('li');
    li.classList.add('chat-item-container');
    li.id = groupName;
    li.style.color = 'black';
   

    // Determine the active class based on the chat username
    if (groupName === document.querySelector('#current-chat-username').textContent) {
        li.classList.add('active');
    }

    // Create the chat initial span
    const chatInitialSpan = document.createElement('span');
    chatInitialSpan.classList.add('chat-initial');
    const chatInitialP = document.createElement('p');
    chatInitialP.textContent = groupName.charAt(0).toUpperCase();
    chatInitialSpan.appendChild(chatInitialP);
    li.appendChild(chatInitialSpan);

    // Create the chat username anchor
    const chatUsernameAnchor = document.createElement('a');
    chatUsernameAnchor.classList.add('chat-username-ref');
    chatUsernameAnchor.textContent = groupName;
    chatUsernameAnchor.style.color = 'black';
    li.appendChild(chatUsernameAnchor);
    if(join){
        const joinBtn = document.createElement('i');
        joinBtn.className = "bx bx-log-in-circle"
        li.setAttribute('onclick', `joinGroup('${JID}')`);
        li.appendChild(joinBtn);
    }else{
        li.setAttribute('onclick', `showChat('${JID}')`);
    }

    return li;
}

function createGroup(){
    Swal.fire({
        title: "Cual será el nombre del grupo?",
        input: "text",
        inputAttributes: {
          autocapitalize: "off"
        },
        showCancelButton: true,
        confirmButtonText: "Crear",
        showLoaderOnConfirm: true,
        preConfirm: async (name) => {
            await fetch('/rooms', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body : JSON.stringify({roomName : name , nickname:document.getElementById('profile-name').textContent})
                }).then(response => response.json())
                .then(body => {
                   if (body.succes == 'false') {
                    Swal.showValidationMessage(`
                        Request failed: ${body.message}
                      `);
                   }
            })
            const response = await fetch(
                {}
            );
        },
        allowOutsideClick: () => !Swal.isLoading()
      }).then((result) => {
        if (result.isConfirmed) {
          Swal.fire('succes','room created','success');
        }
      });
}

function joinGroup(groupJid){
    fetch('/rooms/join',{
     method: 'POST',
     headers: {
        'Content-Type': 'application/json'
    },
     body: JSON.stringify({JID:groupJid,Name: document.getElementById('profile-name').textContent})
    }).then(response => response.json())
    .then(body => {
        if (body.succes == "true"){
         Swal.fire('success','joined into the group','success')
        }else{
         Swal.fire('error',body.message,'error')    
     }
    }
    );
 }

// document.getElementById('create-group').addEventListener('click', function() {
    
// });

document.getElementById("join-room").addEventListener("click", function() {
    fetchGroups();
    var groupList = document.getElementById("rooms-list");
    groupList.innerHTML = "";
    switch (listMode) {
        case 0:
            Mygroups.forEach( (k) => {
                groupList.appendChild(createGroupElement(k['Name'],false,k['roomJid']));
            } );
            listMode = 1;
            this.innerHTML = "Join a group";
        break;
        case 1:
            FoundGroups.forEach( (k) => {
                groupList.appendChild(createGroupElement(k['Name'],true,k['JID']));
            } );
            listMode = 0;
            this.innerHTML = "Bact to my groups";
            break;
    }   
});

document.getElementById("grupal-chat-container").addEventListener("click", function() {
    Settingsredered = false;
    document.getElementById('user-settings').classList.remove("active");
    document.getElementById('user-settings').classList.add("inactive");
    document.getElementById('rooms').classList.remove("inactive");
    document.getElementById('rooms').classList.add("active");
    let messagesDisplay = document.getElementById('chat-page')
    messagesDisplay.classList.remove('active')
    messagesDisplay.classList.add('inactive')
    document.getElementById("rooms-list").innerHTML = "";
    fetchGroups();
    Mygroups.forEach( (k) => {
    document.getElementById("rooms-list").appendChild(createGroupElement(k['Name']));
    });
    
});

document.getElementById("view-users").addEventListener("click",function(){
    fetch('/users', {
        method: 'GET'
        }).then(response => response.json())
        .then(body => console.log(body));
})

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
    document.getElementById('rooms').classList.remove("active");
    document.getElementById('rooms').classList.add("inactive");
   
    //show the user settings
    let userSettings = document.getElementById('user-settings')
    userSettings.classList.remove('inactive')
    userSettings.classList.add('active')

    //show the go back button
    document.getElementById("back-to-chats").style.display = 'inherit';
    document.getElementById("add-contact").style.display = 'inherit';

    //show the contacts list container
    document.getElementById("sidebar-contacts-container").style.display = 'inherit';
    getContacts();
    Settingsredered = true;
    }
})

function getContacts() {
    fetch('/get-contacts', {
        method: 'GET'
        })
        .then(response => response.text())
        .then(data => {
            console.log(data)
            var contactList = document.getElementById('contact-list');
            contactList.innerHTML = "";
            contacts = JSON.parse(data);
            contacts.forEach(contact => {

                var wrapperLi = document.createElement('li');
                wrapperLi.style.display ="flex";
                wrapperLi.style.flexDirection = "row";
                wrapperLi.style.justifyContent= "flex-start";
                wrapperLi.style.alignContent = "center"
                
                const info = document.createElement('i')
                const remove = document.createElement('i');
                const contactActionsDiv = document.createElement('div');

                remove.className = 'bx bxs-trash';
                remove.style.fontSize = "20px";
                remove.style.color = "red";
                remove.setAttribute('onclick', `removeContact('${contact['jid']}')`)
                
                info.className = 'bx bxs-info-circle';
                info.style.fontSize = "20px";
                info.style.color = "white";
                info.setAttribute('onclick', `showUsrDetails('${contact['jid']}')`)
                
                contactActionsDiv.style.display ="flex";
                contactActionsDiv.style.flexDirection = "column";
                contactActionsDiv.style.justifyContent= "flex-start";
                contactActionsDiv.style.alignContent = "center"
                
                const userLI = createUsernameListElement(contact['jid'])
                
                MyContacts[contact['jid']] = contact;

                contactActionsDiv.appendChild(remove);
                contactActionsDiv.appendChild(info);
                wrapperLi.appendChild(contactActionsDiv);
                wrapperLi.appendChild(userLI)
                contactList.appendChild(wrapperLi);
            });
        })
        .catch(error => {
            console.error("Error getting contacts ", error);
        });
}

function showUsrDetails(JID){
    Swal.fire({
        title: `User Info: ${MyContacts[JID].name}`,
        html: `
        <p> Estado : ${MyContacts[JID].status} </p>
        <p> Mensaje de presencia : ${MyContacts[JID].presence} </p>
    `,
    })
}

function removeContact(JID){
    fetch('/remove-contact', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ contactJid: JID })
        })
        .then(response => response.text())
        .then(data => { 
            console.log(data);
            if(data != "succes") {
                Swal.fire('error',data,'error');
            }else{
                Swal.fire('success','contact removed','success');
                getContacts();
            }
        })
        .catch(error => {
            console.error("Error sending message: ", error);
        });
}

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
    document.getElementById('rooms').classList.remove("active");
    document.getElementById('rooms').classList.add("inactive");
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
            if(data != "succes") {
                Swal.fire('error',data,'error');
            }else{
                Swal.fire('succes','contact added','succes');
                getContacts();
            }
        })
        .catch(error => {
            console.error("Error sending message: ", error);
        });
} );

document.getElementById("new-chat").addEventListener('click', async function() { 
    const { value: username } = await Swal.fire({
        title: "Iniciar chat con:",
        input: "text",
        inputLabel: "Username",
        showCancelButton: true,
        inputValidator: (value) => {
          if (!value) {
            return "You need to write something!";
          }
        }
      });
    //show the chat :D
    if(username) {
    var chatList = document.getElementById('current-chat-list');
    var existingUserLink = document.getElementById(`${username}@alumchat.lol`);
    if (!existingUserLink){    
        var li = createUsernameListElement(username+'@alumchat.lol');
        chatList.appendChild(li);
    }
    showChat(username+'@alumchat.lol');
    }else{
        Swal.fire('error','type a username ._.','error')
    }
} );
