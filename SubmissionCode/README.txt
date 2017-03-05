[ST2504 Applied Cryptography Assignment]
[Encrypted Chat Program]

Aeron Teo (P1500725)
Aiman Abdul Rashid (P1529335)
Gerald Peh (P1445972)
Lim Zhao Xiang (P1529559)

NOTE: All programs do not require any extra arguments to run (E.g. To run Server, type "java Server").

In order to use this chat program, a user has to register a account using RegisterClient program. A Registration Server has to be running and the RegisterClient will specify the IP Address of the Registration Server. You may run the Registration Server locally using the RegisterServer program.

All information related to the account is stored inside a file called passwd, which contains the username, hashed password and salt separated by colons (':') as delimiters, of all registered users.

After an account has been registered, the user may connect to the chat server using his credentials and the address/ port of the chat server.

To start a chat server locally, use the ServerGUI program. Alternately you may use the Server program if you wish to run it in console mode (command-line).

To connect to the server as a client user, use the ClientGUI program. Alternately you may use the Client program if you wish to run it in console mode (command-line).

Usernames are case-sensitive. When using actions like whispers that require username, do take note of the case of the name.

All cryptographic-related functions are located in Crypto.java.
