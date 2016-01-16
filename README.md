# Puncher

TCP hole puncher for [Transhift](https://github.com/transhift/transhift).

The Puncher receives connections from a target or source Transhift client. The Puncher facilitates TCP NAT traversal to connect two peers. As security is one of Transhift's main goals, the Puncher is not able to authenticate clients. If the server is compromised, the worst that can happen is that a client is connected to the wrong peer, where further authentication methods employed by the client will prevent exchanging files with an attacker.

Decentralization is important to security; therefore, files will never pass through the Puncher. In addition, the Puncher is completely open source and is able to run on your own machines if so desired.
