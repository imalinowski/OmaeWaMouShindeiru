import logging
import socket
import time
import json
from _thread import *

list_of_clients = []
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server.bind(('', 9090))
server.listen(2)


def remove(connection):
    if connection in list_of_clients:
        list_of_clients.remove(connection)


def clientthread(conn: socket, addr):
    question = json.dumps({'hour': time.localtime().tm_hour, 'minute': time.localtime().tm_min, 'nick': 'server',
                           'message': 'Who the fuck are you identify yourself nigga'})
    conn.send(bytes(question, encoding='utf8'))
    username = conn.recv(1024).decode("utf-8")
    greeting_message = "Welcome to the club " + username
    greeting = json.dumps({'hour': time.localtime().tm_hour, 'minute': time.localtime().tm_min, 'nick': 'server',
                           'message': greeting_message})
    conn.send(bytes(greeting, encoding='utf8'))
    while True:
        try:
            message = conn.recv(1024).decode("utf-8")

            if len(message) > 0:

                message_to_send = json.dumps(
                    {'hour': time.localtime().tm_hour, 'minute': time.localtime().tm_min, 'nick': username,
                     'message': message})
                print("cool")
                time.sleep(0.1)
                broadcast(message_to_send, conn)

            else:
                # print("except")
                conn.close()
                remove(conn)

        except (KeyboardInterrupt, SystemExit):
            print("ex")
            conn.close()


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
    client, addr = server.accept()
    list_of_clients.append(client)
    print(addr[0] + " connected")
    start_new_thread(clientthread, (client, addr))
    while len(list_of_clients) > 0:
        client, addr = server.accept()
        list_of_clients.append(client)
        print(addr[0] + " connected")
        start_new_thread(clientthread, (client, addr))
except(KeyboardInterrupt, SystemExit):
    for connection in list_of_clients:
        print("sfaf")
        connection.close()

print("closed")
client.close()
server.close()

