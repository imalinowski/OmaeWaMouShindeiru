#!/usr/bin/env python

import socket
import time
import json
from _thread import *

list_of_clients = []
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server.bind(('', 8000))
server.listen(214748364)
token = "3ce965ac7f9328"


def auth(message):
    if message[0:14] == token:
        return True
    else:
        return False


def remove(curr_connection):
    if curr_connection in list_of_clients:
        list_of_clients.remove(curr_connection)


def client_thread(conn: socket, addr):
    question = json.dumps({'hour': time.localtime().tm_hour + 3, 'minute': time.localtime().tm_min, 'nick': 'server',
                           'message': 'What\'s your name?'})
    conn.send(bytes(question, encoding='utf8'))
    username = conn.recv(1024).decode("utf-8")

    if len(username) == 0 or username[0:14] != token:
        remove(curr_connection=conn)
        conn.close()

    greeting_message = "Welcome to the club " + username[14:]
    greeting = json.dumps({'hour': time.localtime().tm_hour + 3, 'minute': time.localtime().tm_min, 'nick': 'server',
                           'message': greeting_message})
    try:
        conn.send(bytes(greeting, encoding='utf8'))
    except Exception as e:
        print("User not auth")
        conn.close()
        remove(conn)

    broadcast_meeting(username=username[14:], connection=conn)
    try:
        while True:
            message = conn.recv(1024).decode("utf-8")
            auth(message=message)
            if len(message) > 0:
                message_to_send = json.dumps(
                    {'hour': time.localtime().tm_hour - 3, 'minute': time.localtime().tm_min, 'nick': username[14:],
                     'message': message[14:]})
                print("cool")
                broadcast(message_to_send, conn)

            else:
                conn.close()
                remove(conn)
    except ConnectionResetError:
        print("Connection lost")
        conn.close()
        remove(conn)

    except Exception as e:
        print(str(e))
        conn.close()
        remove(conn)


def broadcast_meeting(username, connection):
    notification = json.dumps(
        {'hour': time.localtime().tm_hour + 3, 'minute': time.localtime().tm_min, 'nick': 'server',
         'message': username + ' join us'})
    for clients in list_of_clients:
        if clients != connection:
            clients.send(bytes(notification, encoding='utf8'))


def broadcast(message, connection):
    for clients in list_of_clients:
        if clients != connection:
            try:
                clients.send(bytes(message, encoding='utf8'))
            except:
                print(connection + " remove")
                clients.close()
                remove(clients)


try:
    print("Server launch")
    client, addr = server.accept()
    list_of_clients.append(client)
    print(addr[0] + " connected")
    start_new_thread(client_thread, (client, addr))
    while len(list_of_clients) > 0:
        client, addr = server.accept()
        list_of_clients.append(client)
        print(addr[0] + " connected")
        start_new_thread(client_thread, (client, addr))
except KeyboardInterrupt as e:
    print(str(e))
    for connection in list_of_clients:
        print("Keyboard interrupt")
        connection.close()

print("closed")
server.close()
