/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author ilicm
 */

public class ServerMessage implements Runnable {
    private final Client client;
    private final BufferedReader br;
    private int clientStatus;

    public ServerMessage(Client parent) {
        this.client = parent;
        this.br = parent.getBr();
        this.clientStatus = 0;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String serverMessage = this.br.readLine();
                if (serverMessage == null) {
                    System.out.println("Server is not available!");
                    client.printMess("Disconnected from server!");
                    client.logout();
                    return;
                }
                processServerMessage(serverMessage);
            } catch (IOException ex) {
                System.out.println("Server is not available!");
                client.printMess("Disconnected from server!");
                client.logout();
                return;
            }
        }
    }

    private void processServerMessage(String serverMessage) {
        switch (clientStatus) {
            case 0:
                processInitialMessages(serverMessage);
                break;
            case 1:
                processAdminMessages(serverMessage);
                break;
            default:
                break;
        }
    }

    private void processInitialMessages(String serverMessage) {
        try {
            if (serverMessage.equals("Correct!")) {
                client.loginSuccessful(true);
                String[] serverInfo = readServerInfo();
                if (client.getRole().equals("admin"))
                    client.updateContestants(serverInfo);

                serverInfo = readServerInfo();
                if (client.getRole().equals("admin"))
                    client.updateAdmins(serverInfo);

                serverInfo = readServerInfo();
                if (client.getRole().equals("admin"))
                    client.updateSets(serverInfo);

                if (client.getRole().equals("admin"))
                    clientStatus = 1;
                else
                    clientStatus = 2;
            } else {
                client.loginSuccessful(false);
            }
        } catch (IOException ex) {
            System.out.println("Server is not available!");
            client.printMess("Disconnected from server!");
            client.logout();
        }
    }

    private String[] readServerInfo() throws IOException {
        String serverMessage = "";
        while (serverMessage.equals(""))
            serverMessage = this.br.readLine();
        String[] serverInfo = serverMessage.split(":");
        String[] comboInfo = new String[serverInfo.length - 1];
        for (int i = 0; i < comboInfo.length; i++)
            comboInfo[i] = serverInfo[i + 1];
        return comboInfo;
    }

    private void processAdminMessages(String serverMessage) {
        try {
            if (serverMessage.equals("Username already exists"))
                client.printMess(serverMessage);
            else if (serverMessage.startsWith("New contestant") || serverMessage.startsWith("Contestant kicked out"))
                client.updateContestants(readServerInfo());
            else if (serverMessage.startsWith("New admin") || serverMessage.startsWith("Admin kicked out"))
                client.updateAdmins(readServerInfo());
            else if (serverMessage.equals("ByebyeAdmin")) {
                System.out.println(client.getUsername() + " is kicked out!");
                client.logout();
            } else if (serverMessage.equals("You cannot kick out yourself!"))
                client.printMess(serverMessage);
        } catch (IOException ex) {
            System.out.println("Server is not available!");
            client.printMess("Disconnected from server!");
            client.logout();
        }
    }
}

/**
 *
 * @author ilicm
 *
public class ServerMessage implements Runnable {
     
    Client client;
    BufferedReader br;
    
    private int clientStatus;
    
    public ServerMessage(Client client) 
    {
        this.client = client;
        this.br = client.getBr();
        this.clientStatus = 0;
    }
    
    @Override
    public void run() {
        while (true) {
            String serverMessage;
            switch (clientStatus) {
                case 0:
                    handleInitialStatus();
                    break;
                case 1:
                    handleCaseOne();
                    break;
                default:
                    break;
            }
        }
    }
    //Obrada pocetnog statusa klijenta
    private void handleInitialStatus() {
        try {
            String serverMessage = this.br.readLine();
            if (serverMessage.equals("Correct!")) {
                handleCorrectLogin();
            } else {
                client.loginSuccessful(false);
            }
        } catch (IOException ex) {
            handleServerUnavailable();
        }
    }
    
    //Obrada ispravne prijave na server
    private void handleCorrectLogin() throws IOException {
        //Uspesna prijava
        client.loginSuccessful(true);
        
        String serverMessage = readNonEmptyLine();
        String[] serverInfo = extractInfo(serverMessage);
        //Azuriraj listu takmicara ako je korisnik admin
        if (client.getRole().equals("admin")) {
            client.updateContestants(serverInfo);
        }

        serverMessage = readNonEmptyLine();
        serverInfo = extractInfo(serverMessage);
        //Azuriraj listu admina ako je korisnik admin
        if (client.getRole().equals("admin")) {
            client.updateAdmins(serverInfo);
        }

        serverMessage = readNonEmptyLine();
        serverInfo = extractInfo(serverMessage);
        //Azuriraj listu setova ako je korisnik admin
        if (client.getRole().equals("admin")) {
            client.updateSets(serverInfo);
        }

        serverMessage = readNonEmptyLine();
        if (client.getRole().equals("admin")) {
            clientStatus = 1;
        } else {
           // clientStatus = 2;
        }
    }
    //Citanje linije koja nije prazna iz BufferedReader-a
    private String readNonEmptyLine() throws IOException {
        String serverMessage = "";
        while (serverMessage.equals("")) {
            serverMessage = this.br.readLine();
        }
        return serverMessage;
    }
    //Izvlacenje informacija iz serverMessage linije
    private String[] extractInfo(String serverMessage) {
        String[] serverInfo = serverMessage.split(":");
        String[] comboInfo = new String[serverInfo.length - 1];
        for (int i = 0; i < comboInfo.length; i++) {
            comboInfo[i] = serverInfo[i + 1];
        }
        return comboInfo;
    }
    
    private void handleCaseOne() {
        try {
            String serverMessage = this.br.readLine();
            if (serverMessage.equals("Username already exists")) {
                client.printMess(serverMessage);
            } else if (serverMessage.startsWith("New contestant") || serverMessage.startsWith("Contestant kicked out")) {
                String[] serverInfo = extractInfo(serverMessage);
                client.updateContestants(serverInfo);
            } else if (serverMessage.startsWith("New admin") || serverMessage.startsWith("Admin kicked out")) {
                String[] serverInfo = extractInfo(serverMessage);
                client.updateAdmins(serverInfo);
            } else if (serverMessage.equals("ByebyeAdmin")) {
                System.out.println(client.getUsername() + " is kicked out!");
                client.logout();
            } else if (serverMessage.equals("You cannot kick out yourself!")) {
                client.printMess(serverMessage);
            }
        } catch (IOException ex) {
            handleServerUnavailable();
        }
    }
    
    //U slucaju da server nije dostupan
    private void handleServerUnavailable() {
        System.out.println("Server is not available!");
        client.printMess("Disconnected from server!");
        client.logout();
    }
*/

