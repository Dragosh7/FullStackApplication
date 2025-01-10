import React from 'react';

function ChatList({ chats, onChatClick }) {
    if (!Array.isArray(chats) || chats.length === 0) {
        return <p>No chats available.</p>;
    }

    return (
        <ul>
            {chats.map(chat => (
                <li 
                    key={chat.id} 
                    onClick={() => onChatClick(chat.id)} 
                    style={{ cursor: 'pointer' }}
                >
                    {chat.sender?.name} ↔ {chat.receiver?.name}
                </li>
            ))}
        </ul>
    );
}

export default ChatList;
