Secure-messenger
================

Dependencies:

-cling

-c3p0

-mysql connector

================

Quick how to use:

1. 
You need a MySQL schema named "messenger_users" running with three tables:

user:
int(10) ID            - primary key, not null, unique, auto increment
varchar(45) username  - not null, unique
varchar(128) password - not null
varchar(45) nickname  - non null, default ''

new_friends:
int(10) ID_from   - primary key, not null
int(10) ID_to     - primary key, not null

friends 
int(10) ID1     - primary key, not null
int(10) ID2     - primary key, not null

Default username is "messenger_admin" and default password is "password1234"

2. 
Run the server.

3. 
Run the client. It searches for the server on 127.0.0.1:45293 by default. P2P (doesn't work at the moment if the target is behind a NAT) runs on port 45292.
