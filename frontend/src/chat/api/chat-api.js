import { HOST } from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const endpoint = {
    chats: '/api/chats',
    users: '/api/users',
    messages: '/api/messages',
};

const getHeaders = () => {
    const token = localStorage.getItem("token"); 
    return {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` }),
    };
};


function getUsers(callback) {
    let request = new Request(HOST.chat_api + endpoint.users, {
        method: 'GET',
        headers: getHeaders(),

    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}


function getChatsForUser(userId, callback) {
    let request = new Request(HOST.chat_api + endpoint.chats + `/${userId}`, {
        method: 'GET',
        headers: getHeaders(),

    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

// Get messages for a specific chat. response example : []
function getMessagesForChat(chatId, userId, callback) {
    let request = new Request(HOST.chat_api + endpoint.chats + `/${chatId}/messages/${userId}`, {
        method: 'GET',
        headers: getHeaders(),

    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

// Create a new chat between two users
function createChat(senderName, receiverName, callback) {
    let request = new Request(HOST.chat_api + endpoint.chats + `/?senderName=${senderName}&receiverName=${receiverName}`, {
        method: 'POST',
        headers: getHeaders(),

        //body: JSON.stringify({ senderId, receiverId })
    });
    console.log("Create chat URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

// Send a message in a chat
function sendMessage(chatId, message, callback) {
    let request = new Request(HOST.chat_api + endpoint.chats + `/sendMessage`, {
        method: 'POST',
        headers: getHeaders(),

        body: JSON.stringify({ chatId, message })
    });
    console.log("Send message URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    getUsers,
    getChatsForUser,
    getMessagesForChat,
    createChat,
    sendMessage,
};
