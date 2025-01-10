import React, { useState, useEffect, useRef } from "react";
import {
  Box,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  TextField,
  IconButton,
  Typography,
  Menu,
  MenuItem,
  Badge,
  Paper,
  Divider,
} from "@mui/material";
import { styled } from "@mui/system";
import { BsSend, BsPlus, BsCheckAll } from "react-icons/bs";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import {
  getUsers,
  getChatsForUser,
  getMessagesForChat,
  createChat,
} from "./api/chat-api";
import  "../commons/styles/typing.css"

// Styled components
const StyledBox = styled(Box)(({ theme }) => ({
  display: "flex",
  height: "95vh",
  backgroundColor: "#f5f5f5",
  width: "70%", 

}));

const Sidebar = styled(Box)(({ theme }) => ({
  width: "300px",
  backgroundColor: "#fff",
  borderRight: "1px solid #e0e0e0",
  height: "100vh", 
  overflow: "hidden",
}));

const ChatWindow = styled(Box)(({ theme }) => ({
  flex: 1,
  display: "flex",
  flexDirection: "column",
}));

const MessageContainer = styled(Box)(({ theme }) => ({
  flex: 1,
  padding: theme.spacing(3),
  overflow: "auto",
  display: "flex",
  flexDirection: "column",
  gap: theme.spacing(1.5), // Add spacing between messages
}));

const Message = styled(Box)(({ isOwn, theme }) => ({
  padding: "12px 16px",
  marginBottom: theme.spacing(1),
  maxWidth: "70%",
  borderRadius: "20px", // Rounded corners
  alignSelf: isOwn ? "flex-end" : "flex-start",
  backgroundColor: isOwn ? "#d1f7c4" : "#ffffff",
  boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.1)", // Subtle shadow
  color: "#333",
  display: "flex",
  flexDirection: "column",
  gap: theme.spacing(0.5),
}));

const ChatWindowHeader = styled(Box)(({ theme }) => ({
  p: 2,
  backgroundColor: "#fff",
  borderBottom: "1px solid #e0e0e0",
  display: "flex",
  justifyContent: "space-between",
  alignItems: "center",
  
}));

const InputContainer = styled(Box)(({ theme }) => ({
  padding: theme.spacing(2),
  backgroundColor: "#fff",
  borderTop: "1px solid #e0e0e0",
  display: "flex",
  gap: theme.spacing(1),
  alignItems: "center",
}));

const RoundedTextField = styled(TextField)(({ theme }) => ({
  "& .MuiOutlinedInput-root": {
    borderRadius: "20px",
  },
}));

const ChatPage = () => {
  const [users, setUsers] = useState([]);
  const [chats, setChats] = useState([]);
  const [messages, setMessages] = useState([]);
  const [selectedChat, setSelectedChat] = useState(null);
  const [message, setMessage] = useState("");
  const [anchorEl, setAnchorEl] = useState(null);
  const [connectionStatus, setConnectionStatus] = useState("Disconnected");
  const [typingStatus, setTypingStatus] = useState(null);
  const typingTimeoutRef = useRef(null); 
  const subscriptionsRef = useRef([]); 

  const stompClient = useRef(null);
  const storedUserId = localStorage.getItem("id");
  const storedUserName = localStorage.getItem("name");
  const storedUserRole = localStorage.getItem("role"); // Role-based logic for announcements
  const messageEndRef = useRef(null); // Ref for scrolling to last message

  useEffect(() => {
    fetchUsers();
    fetchChats();

    // Initialize WebSocket connection
    const socket = new SockJS("http://localhost:8088/chat");
    stompClient.current = Stomp.over(socket);

    stompClient.current.connect(
      {},
      () => {
        setConnectionStatus("Connected");
      },
      (error) => {
        console.error("WebSocket error:", error);
        setConnectionStatus("Disconnected");
      }
    );

    return () => {
      if (stompClient.current) stompClient.current.disconnect();
    };
  }, []);

  const fetchUsers = () => {
    getUsers((data) => {
      const allUsers = Array.isArray(data) ? data : JSON.parse(data);
  
      // Filter out the user whose name matches the stored name
      const filteredUsers = allUsers.filter((user) => user !== storedUserName);
  
      setUsers(filteredUsers);
    });
  };

  const fetchChats = () => {
    if (!storedUserId) return;

    getChatsForUser(storedUserId, (data) => {
      try {
        const parsedData = Array.isArray(data) ? data : JSON.parse(data);
        const transformedChats = parsedData.map((chat) => {
          const isSender = chat.sender.id === storedUserId;
          return {
            id: chat.id,
            name: isSender ? chat.receiver.name : chat.sender.name,
            isAnnouncement: chat.id === "announcements",
          };
        });
        setChats(transformedChats);
      } catch (error) {
        console.error("Error Parsing Chats Data:", error.message);
        setChats([]);
      }
    });
  };

  const fetchMessages = (chatId) => {
    
    getMessagesForChat(chatId, storedUserId, (data) => {
      if (!data) {
        console.error(`No messages found for chatId: ${chatId}`);
        setMessages([]);
        return;
      }
      try {
        const parsedData = typeof data === "string" ? JSON.parse(data) : data;

        const transformedMessages = (Array.isArray(parsedData) ? parsedData : []).map((msg) => ({
          isOwn: msg.senderId === storedUserId,
          text: msg.messageContent || "No content",
          readStatus: msg.readStatus || false,
          timestamp: msg.timestamp
            ? new Date(msg.timestamp).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
            : new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
        }));

        setMessages(transformedMessages);
        
        // Scroll to last message on fetch
        scrollToBottom();
       // stompClient.current.send("/app/readMessage", {}, JSON.stringify(selectedChat.id));

      } catch (error) {
        console.error("Error transforming messages data:", error.message);
        setMessages([]);
      }
    });
  };

  const handleNewChat = (user) => {
    if (!storedUserName || !user) {
      console.error("Missing user details to create a chat.");
      return;
    }

    if (storedUserName === user) {
      console.error("Can't create a chat with yourself.");
      return;
    }
  
    createChat(storedUserName, user, (response) => {
      if (response) {
        console.log("New chat created:", response);
        fetchChats(); // Refresh the list of chats after creating a new one
        setAnchorEl(null); // Close the dropdown menu
      } else {
        console.error("Failed to create chat.");
      }
    });
  };
  
  const handleChatSelect = (chat) => {
    if (subscriptionsRef.current.length > 0) {
      subscriptionsRef.current.forEach((subscription) => subscription.unsubscribe());
      subscriptionsRef.current = []; 
    }

    setSelectedChat(chat);
    fetchMessages(chat.id);

    // Subscribe to chat topic
    if (stompClient.current) {
      const subscription = stompClient.current.subscribe(`/topic/chat/${chat.id}`, (message) => {
         
        const newMessage = JSON.parse(message.body);

        setMessages((prev) => [
          ...prev,
          {
            text: newMessage.messageContent,
            isOwn: newMessage.sender === storedUserId,
            readStatus: false,
            timestamp: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
          },
        ]);
        stompClient.current.send("/app/readMessage", {}, JSON.stringify(selectedChat.id));
        // Scroll to bottom on receiving a new message
        scrollToBottom();
      
      });
      subscriptionsRef.current.push(subscription);

    }

    if (stompClient.current) {
      const seenSubscription = stompClient.current.subscribe(`/topic/seen/${chat.id}`, (message) => {
         if(message.body === "seen"){
            setMessages((prevMessages) => 
              prevMessages.map((msg) => ({
                ...msg,
                readStatus: true, // Mark all messages as seen
              }))
            );

            scrollToBottom();
         }
         
      });

      const typingSubscription = stompClient.current.subscribe(`/topic/typing/${chat.id}`, (typingMessage) => {
        const senderName = typingMessage.body;
        if (senderName !== storedUserName) {
          setTypingStatus(`${senderName} is typing `);
          scrollToBottom();
          // Clear typing status after 2 seconds
          clearTimeout(typingTimeoutRef.current);
          typingTimeoutRef.current = setTimeout(() => {
            setTypingStatus(null);
          }, 2000);
        }
      });
    
      subscriptionsRef.current.push(seenSubscription, typingSubscription);

    }


  };

  const handleTyping = () => {
    if (stompClient.current && selectedChat) {
      stompClient.current.send("/app/typing", {}, JSON.stringify({ chatId: selectedChat.id, sender: storedUserName }));
    }
  };

  const handleSendMessage = () => {
    if (message.trim() && selectedChat) {
      if (selectedChat.isAnnouncement && storedUserRole !== "admin") return;

      const messagePayload = {
        chatId: selectedChat.id,
        sender: storedUserId,
        messageContent: message,
      };

      if (stompClient.current) {
        stompClient.current.send("/app/sendMessage", {}, JSON.stringify(messagePayload));
        setMessage("");
      }
    }
  };

  const scrollToBottom = () => {
    messageEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  return (
    <StyledBox>
      <Sidebar>
        <Box sx={{ p: 2, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <Typography variant="h6">Chats</Typography>
          <IconButton onClick={(e) => setAnchorEl(e.currentTarget)}>
            <BsPlus size={24} />
          </IconButton>
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={() => setAnchorEl(null)}
          >
            {users.map((user) => (
              <MenuItem key={user} onClick={() => handleNewChat(user)}>
                {user}
              </MenuItem>
            ))}
          </Menu>
        </Box>
        <Divider />
        <List>
          {chats.map((chat) => (
            <ListItem
              key={chat.id}
              button
              selected={selectedChat?.id === chat.id}
              onClick={() => handleChatSelect(chat)}
            >
              <ListItemAvatar>
                <Badge
                  overlap="circular"
                  anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
                  variant="dot"
                  color="success"
                >
                  <Avatar>{chat.name.charAt(0)}</Avatar>
                </Badge>
              </ListItemAvatar>
              <ListItemText primary={chat.name} />
            </ListItem>
          ))}
        </List>
      </Sidebar>

      <ChatWindow>
        {selectedChat ? (
          <>
            <ChatWindowHeader>
              <Typography variant="h6">{selectedChat.name}</Typography>
            </ChatWindowHeader>

            <MessageContainer>
              {messages.map((msg, index) => (
                <Message key={index} isOwn={msg.isOwn}>
                  <Typography variant="body1">{msg.text}</Typography>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
                    <Typography variant="caption" sx={{ opacity: 0.6 }}>
                      {msg.timestamp}
                    </Typography>
                    {msg.isOwn && <BsCheckAll size={16} color={msg.readStatus ? "#2196f3" : "#9e9e9e"} />}
                  </Box>
                </Message>
              ))}
              {typingStatus && (
                  <Box className="typing-indicator" sx={{ textAlign: "left", padding: "8px 16px" }}>
                    {typingStatus} <span></span><span></span><span></span>
                  </Box>
                )}

              <div ref={messageEndRef} />
            </MessageContainer>

            {!selectedChat.isAnnouncement || storedUserRole === "admin" ? (
              <InputContainer>
                <RoundedTextField
                  fullWidth
                  variant="outlined"
                  placeholder="Type a message"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  onKeyPress={(e) => e.key === "Enter" && handleSendMessage()}
                  onKeyUp={handleTyping} // Notify typing on keyup
                />
                <IconButton onClick={handleSendMessage} color="primary">
                  <BsSend />
                </IconButton>
              </InputContainer>
            ) : (
              <Box sx={{ p: 2 }}>
                <Typography variant="caption" color="textSecondary">
                  Only admins can send messages in this group.
                </Typography>
              </Box>
            )}
          </>
        ) : (
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              height: "100%",
            }}
          >
            <Typography variant="h6" color="textSecondary">
              Select a chat to start messaging
            </Typography>
          </Box>
        )}
      </ChatWindow>
    </StyledBox>
  );
};

export default ChatPage;
