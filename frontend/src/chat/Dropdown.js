import React, { useState } from 'react';

function Dropdown({ users, onUserSelect }) {
    const [isOpen, setIsOpen] = useState(false);

    const handleUserClick = (user) => {
        onUserSelect(user);
        setIsOpen(false); // Close dropdown after selection
    };

    return (
        <div>
            <button onClick={() => setIsOpen(!isOpen)}>+ New Chat</button>
            {isOpen && (
                <ul style={{ border: '1px solid #ccc', padding: '10px', marginTop: '5px', listStyleType: 'none' }}>
                    {users.map((user, index) => (
                        <li
                            key={index}
                            style={{ cursor: 'pointer', padding: '5px 0' }}
                            onClick={() => handleUserClick(user)}
                        >
                            {user}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default Dropdown;
