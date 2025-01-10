import React from 'react';

function ChatMessages({ messages }) {
    return (
        <div>
            <h3>Messages</h3>
            <ul style={{ listStyleType: 'none', padding: 0 }}>
                {messages.map((message, index) => (
                    <li key={index} style={{ padding: '10px', borderBottom: '1px solid #ccc' }}>
                        <p><strong>{message.senderName}</strong>: {message.content}</p>
                        <p style={{ fontSize: '0.8rem', color: '#555' }}>{new Date(message.timestamp).toLocaleString()}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default ChatMessages;
