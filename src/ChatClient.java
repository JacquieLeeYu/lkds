/**
 * CS18000 Project 4 - Simple Server
 *
 *
 *
 * @author Jacquie Yu, Siddarth Pillai
 *
 *
 */


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }
    private ChatClient(int port, String username) {
        this("localhost",port,username);
    }
    private ChatClient(String username) {
        this(1500,username);
    }

    /*
     * This starts the Chat Client
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        // Get proper arguments and override defaults
        args = new String[3];
        String username;
        String portNumber;
        String serverAddress;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine(); //command taken from terminal
            ArrayList<String> spaceIndex = new ArrayList<>(); //Array of indexes with spaces

            for (int i = 0 ; i < command.length() ; i++) { //check for number of spaces
                if (command.charAt(i) == ' ') {
                    spaceIndex.add(String.valueOf(i));
                }
            }


            //THIS DOES NOT HANDLE IF THE USER INPUTS SOMETHING OTHER THAN FOR JAVA CHATCLIENT

            if (spaceIndex.size() == 4) { //if contains all parameters
                username = command.substring(Integer.parseInt(spaceIndex.get(1) + 1),
                        Integer.parseInt(spaceIndex.get(2)));
                portNumber = command.substring(Integer.parseInt(spaceIndex.get(2) + 1),
                        Integer.parseInt(spaceIndex.get(3)));
                serverAddress = command.substring(Integer.parseInt(spaceIndex.get(3) + 1),
                        Integer.parseInt(spaceIndex.get(4)));
                break;
            } else if (spaceIndex.size() == 3) { //if no serverAddress
                username = command.substring(Integer.parseInt(spaceIndex.get(1) + 1),
                        Integer.parseInt(spaceIndex.get(2)));
                portNumber = command.substring(Integer.parseInt(spaceIndex.get(2) + 1),
                        Integer.parseInt(spaceIndex.get(3)));
                serverAddress = "localhost";
                break;
            } else { //if only contains username
                username = command.substring(Integer.parseInt(spaceIndex.get(1) + 1),
                        Integer.parseInt(spaceIndex.get(2)));
                portNumber = "1500";
                serverAddress = "localhost";
                break;
            }
        }
        args[0] = username;
        args[1] = portNumber;
        args[2] = serverAddress;

        // Create your client and start it
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]), args[2]);
        client.start();

        // Send an empty message to the server
        client.sendMessage(new ChatMessage(0,""));

        client.sendMessage(new ChatMessage(0,scanner.nextLine()));
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            try {
                while(true){
                String msg = (String) sInput.readObject();
                System.out.print(msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
