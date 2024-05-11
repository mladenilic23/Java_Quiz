/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author ilicm
 */
public class ServerConnectedClient implements Runnable{
    
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    
    private ArrayList<ServerConnectedClient> allClients;
    private ArrayList<Contestant> contestants;
    private ArrayList<SetOfQuestions> sets;
    private ArrayList<String> admins;
    
    private String username;
    private String password;
    private String role;
    
    private QuizServer server;
    private int clientState;
    
    private SecretKey symmetricKey;
    private byte[] initializationVector;
    //putanja
    private String usersTxt = "D:\\JavaProjects\\users.txt";
    //Block cipher(CBC mode) - tzv Blok sifra kod koje se originalna poruka sifruje po grupama (blokovima)    
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";
    
    public String getUserName() 
    {
        return username;
    }

    public void setUserName(String username) 
    {
        this.username = username;
    }

    public String getPassword() 
    {
        return password;
    }

    public void setPassword(String password) 
    {
        this.password = password;
    }
    
    public String getRole() 
    {
        return role;
    }

    public void setRole(String role) 
    {
        this.role = role;
    }
    
    public void setClientState(int state)
    {
        this.clientState = state;
    }
    
    public int getClientState()
    {
        return clientState;
    }
    
    public ServerConnectedClient(Socket socket, ArrayList<ServerConnectedClient> allClients, ArrayList<Contestant> contestants, ArrayList<SetOfQuestions> sets, ArrayList<String> admins, QuizServer server, SecretKey symmetricKey, byte[] initializationVector) throws Exception
    {
        this.socket = socket;
        this.allClients = allClients;
        this.contestants = contestants;
        this.sets = sets;
        this.admins = admins;
        
        this.server = server;
        this.symmetricKey = symmetricKey;
        this.initializationVector = initializationVector;

        try 
        {
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);

            this.username = "";
            this.password = "";
            this.role = "";
            this.clientState = 0;
        } catch (IOException ex) {
            Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() 
    {
        while (true)
        {
            switch (clientState)
            {
                case 0:
                    try
                    {
                        String userInfo = this.br.readLine();
                        String[] userPassRole = userInfo.split(":");
                        
                        if (userPassRole.length != 3)
                        {
                            System.out.println("User data is not in correct form!");
                            System.exit(0);
                        }
                        else
                        {
                            ByteArrayOutputStream bytesArray = new ByteArrayOutputStream();
                            //citanje users.txt
                            try (FileInputStream readFile = new FileInputStream(usersTxt)) 
                            {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = readFile.read(buffer)) != -1) {  
                                    bytesArray.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException e) {
                                System.out.println("Error while reading from users.txt!");
                            }
                            byte[] encryptedUsers = bytesArray.toByteArray();

                            String decryptedUsers = do_AESDecryption(encryptedUsers, symmetricKey, initializationVector);
                            
                            String[] splitedUsers = decryptedUsers.split("\n");
                            
                            for (String str: splitedUsers)
                            {
                                String[] txtUserData = str.split(":");
                                if (txtUserData.length != 3)
                                {
                                    System.out.println("User data is not in correct form!!");
                                    System.exit(0);
                                }
                                //proverava da li je korisnik admin ili contestant
                                if (userInfo.equals(str))
                                {
                                    if (userPassRole[2].equals("admin"))
                                    {
                                        clientState = 1;
                                        System.out.println("[admin]: " + userPassRole[0] + " has logged in!");
                                    }
                                    else if (userPassRole[2].equals("contestant"))
                                    {
                                        clientState = 2;
                                        System.out.println("[contestant]: " + userPassRole[0] + " has logged in!");
                                    }
                                    else
                                    {
                                        System.out.println("Unknown user!");
                                        System.exit(0);
                                    }
                                    
                                    this.pw.println("Correct!");
                                    
                                    this.username = userPassRole[0];
                                    this.password = userPassRole[1];
                                    this.role = userPassRole[2];
                                    
                                    //proverava da li je takmicar dostupan
                                    if (this.role.equals("contestant"))
                                    {
                                        for(Contestant cnt: this.contestants)
                                        {
                                            if (cnt.getUserName().equals(this.username))
                                            {
                                                cnt.setAvailable(true);
                                                break;
                                            }
                                        }
                                    }
                                    
                                    String existingContestants = "Contestants";
                                    for (Contestant cont: this.contestants) {
                                        existingContestants += ":" + cont.getUserName();
                                    }
                                    this.pw.println(existingContestants);
                                    
                                    String existingAdmins = "Admins";
                                    for (String adm: this.admins) {
                                        existingAdmins += ":" + adm;
                                    }
                                    this.pw.println(existingAdmins);
                                    
                                    String existingSets = "Sets";
                                    for (SetOfQuestions set: this.sets) {
                                        existingSets += ":" + set.getSetName();
                                    }
                                    this.pw.println(existingSets);
                                    
                                    this.pw.println(server.getActiveSet().getSetName());
                                    
                                    break;
                                }
                            }
                            
                            if (clientState == 0)
                            {
                                this.pw.println("Not correct!");
                            }
                        }
                    } catch (IOException ex) {
                        System.out.println("User is disconnected!");
                        return;
                    } catch (Exception ex) {
                        Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;

                case 1:
                    try
                    {
                        String clientMess = this.br.readLine();
                        //NEW CONTESTANT
                        if (clientMess.startsWith("New contestant"))
                        {
                            boolean exist = false;
                            String[] newContestantInfo = clientMess.split(":");
                            
                            ByteArrayOutputStream bytesArray = new ByteArrayOutputStream();
                            //citanje users.txt
                            try (FileInputStream readFile = new FileInputStream(usersTxt)) 
                            {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = readFile.read(buffer)) != -1) { 
                                    bytesArray.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException e) {
                                System.out.println("Error while reading from users.txt!");
                            }
                            byte[] encryptedUsers = bytesArray.toByteArray();

                            String decryptedUsers = do_AESDecryption(encryptedUsers, symmetricKey, initializationVector);
                            
                            String[] splitedUsers = decryptedUsers.split("\n");
                            //proveravanje da li vec postoji korisnik
                            for (String str: splitedUsers)
                            {
                                String[] cntInfo = str.split(":");
                                if (newContestantInfo[1].equals(cntInfo[0]))
                                {
                                    exist = true;
                                    break;
                                }
                            }
                            
                            if (exist)
                            {
                                this.pw.println("Username already exists!");
                            }
                            else
                            {
                                Contestant cont = new Contestant(newContestantInfo[1]);
                                contestants.add(cont);
                                String contestantList = "New contestant";
                                
                                for (Contestant cnt: this.contestants) {
                                    contestantList += ":" + cnt.getUserName();
                                }
                                
                                for (ServerConnectedClient client: allClients) {
                                    client.pw.println(contestantList);
                                }
                            
                                System.out.println(contestantList);
                                
                                String newContestant = newContestantInfo[1] + ":" + newContestantInfo[2] + ":" + "contestant\n";
                                decryptedUsers += newContestant;
                                encryptedUsers = do_AESEncryption(decryptedUsers, symmetricKey, initializationVector);
                                
                                try (FileOutputStream writer = new FileOutputStream(usersTxt)) 
                                {
                                    writer.write(encryptedUsers);
                                } catch (IOException e) {
                                    System.out.println("Error while writing in users.txt!");
                                }
                            }
                        }
                        //NEW ADMIN
                        else if (clientMess.startsWith("New admin"))
                        {
                            boolean exist = false;
                            String[] newAdminInfo = clientMess.split(":");
                            
                            ByteArrayOutputStream bytesArray = new ByteArrayOutputStream();
                            try (FileInputStream readFile = new FileInputStream(usersTxt)) 
                            {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = readFile.read(buffer)) != -1) {
                                    bytesArray.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException e) {
                                System.out.println("Error while reading from users.txt!");
                            }
                            byte[] encryptedUsers = bytesArray.toByteArray();

                            String decryptedUsers = do_AESDecryption(encryptedUsers, symmetricKey, initializationVector);
                            
                            String[] splitedUsers = decryptedUsers.split("\n");
                            for (String str: splitedUsers)
                            {
                                String[] admInfo = str.split(":");
                                if (newAdminInfo[1].equals(admInfo[0]))
                                {
                                    exist = true;
                                    break;
                                }
                            }
                            
                            if (exist)
                            {
                                this.pw.println("Username already exists!");
                            }
                            else
                            {
                                admins.add(newAdminInfo[1]);
                                String adminList = "New admin";
                                
                                for (String str: this.admins) {
                                    adminList += ":" + str;
                                }
                                
                                for (ServerConnectedClient client: allClients) {
                                    client.pw.println(adminList);
                                }
                                
                                String newAdmin = newAdminInfo[1] + ":" + newAdminInfo[2] + ":" + "admin\n";
                                decryptedUsers += newAdmin;
                                encryptedUsers = do_AESEncryption(decryptedUsers, symmetricKey, initializationVector);
                                
                                try (FileOutputStream writer = new FileOutputStream(usersTxt)) 
                                {
                                    writer.write(encryptedUsers);
                                } catch (IOException e) {
                                    System.out.println("Error while writing in users.txt!");
                                }
                            }
                        }
                        //CONTESTANT KICKED OUT
                        else if (clientMess.startsWith("Contestant kicked out"))
                        {
                            System.out.println(clientMess);
                            String[] ejectCnt = clientMess.split(":");
                            for (Contestant cnt: this.contestants)
                            {
                                if (cnt.getUserName().equals(ejectCnt[1]))
                                {
                                    this.contestants.remove(cnt);
                                    break;
                                }
                            }
                            
                            for (ServerConnectedClient client: this.allClients)
                            {
                                System.out.println(client.getUserName());
                                if (client.getUserName().equals(ejectCnt[1]))
                                {
                                    client.setUserName("");
                                    client.setPassword("");
                                    client.setRole("");
                                    client.setClientState(3);
                                    client.pw.println("ByebyeContestant");
                                    System.out.println("Bye bye " + ejectCnt[1]);
                                    break;
                                }
                            }
                            
                            String contestantList = "Contestant kicked out";
                                
                            for (Contestant cnt: this.contestants) {
                                contestantList += ":" + cnt.getUserName();
                            }
                            
                            for (ServerConnectedClient client: allClients) {
                                client.pw.println(contestantList);
                            }

                            ByteArrayOutputStream bytesArray = new ByteArrayOutputStream();
                            try (FileInputStream readFile = new FileInputStream(usersTxt)) 
                            {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = readFile.read(buffer)) != -1) 
                                    bytesArray.write(buffer, 0, bytesRead);
                            } catch (IOException e) {
                                System.out.println("Error while reading from users.txt!");
                            }
                            byte[] encryptedUsers = bytesArray.toByteArray();
                            String decryptedUsers = do_AESDecryption(encryptedUsers, symmetricKey, initializationVector);
                            
                            String[] splitedUsers = decryptedUsers.split("\n");
                            String resultUsers = "";
                            
                            for (String userString: splitedUsers)
                            {
                                String[] userInfo = userString.split(":");
                                if (!userInfo[0].equals(ejectCnt[1]))
                                    resultUsers += userString + "\n";
                            }
                            
                            encryptedUsers = do_AESEncryption(resultUsers, symmetricKey, initializationVector);
                            
                            try (FileOutputStream writer = new FileOutputStream(usersTxt)) 
                            {
                                writer.write(encryptedUsers);
                            } catch (IOException e) {
                                System.out.println("Error while writing in users.txt!");
                            }
                        }
                        else if (clientMess.startsWith("Admin kicked out"))
                        {
                            String[] ejectAdm = clientMess.split(":");
                            if (!ejectAdm[1].equals(this.username))
                            {
                                for (String adm: this.admins)
                                {
                                    if (adm.equals(ejectAdm[1]))
                                    {
                                        this.admins.remove(adm);
                                        break;
                                    }
                                }

                                for (ServerConnectedClient client: this.allClients)
                                {
                                    if (client.getUserName().equals(ejectAdm[1]))
                                    {
                                        client.setUserName("");
                                        client.setPassword("");
                                        client.setRole("");
                                        client.setClientState(3);
                                        client.pw.println("ByebyeAdmin");
                                        break;
                                    }
                                }

                                String adminList = "Admin kicked out";

                                for (String adm: this.admins) {
                                    adminList += ":" + adm;
                                }

                                for (ServerConnectedClient client: allClients) {
                                    client.pw.println(adminList);
                                }
                                
                                ByteArrayOutputStream bytesArray = new ByteArrayOutputStream();
                                try (FileInputStream readFile = new FileInputStream(usersTxt)) 
                                {
                                    byte[] buffer = new byte[1024];
                                    int bytesRead;
                                    while ((bytesRead = readFile.read(buffer)) != -1) { 
                                        bytesArray.write(buffer, 0, bytesRead);
                                    }
                                } catch (IOException e) {
                                    System.out.println("Error while reading from users.txt!");
                                }
                                byte[] encryptedUsers = bytesArray.toByteArray();
                                String decryptedUsers = do_AESDecryption(encryptedUsers, symmetricKey, initializationVector);

                                String[] splitedUsers = decryptedUsers.split("\n");
                                String resultUsers = "";

                                for (String userString: splitedUsers)
                                {
                                    String[] userInfo = userString.split(":");
                                    if (!userInfo[0].equals(ejectAdm[1])) {
                                        resultUsers += userString + "\n";
                                    }
                                }

                                encryptedUsers = do_AESEncryption(resultUsers, symmetricKey, initializationVector);

                                try (FileOutputStream writer = new FileOutputStream(usersTxt)) 
                                {
                                    writer.write(encryptedUsers);
                                } catch (IOException e) {
                                    System.out.println("Error while writing in users.txt!");
                                }
                            }
                            else {
                                this.pw.println("You cannot kick out yourself!");
                            }
                        }
                        else if (clientMess.equals("Logout")) 
                        {
                            this.username = "";
                            this.password = "";
                            this.role = "";
                            this.clientState = 3;
                        }
                    }
                    catch (IOException ex) 
                    {
                        System.out.println("User disconnected");
                        this.username = "";
                        this.password = "";
                        this.role = "";
                        this.clientState = 3;
                        return;
                    } 
                    catch (Exception ex) 
                    {
                    Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    
    //Enkriptuje dati tekst koristeci AES enkripciju.
    public static byte[] do_AESEncryption(String plainText, SecretKey secretKey, byte[] initializationVector) throws Exception
    {
        //klasa Cipher se koristi za enkripciju/dekripciju, prilikom kreiranja navodi se koji algoritam se koristi
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        //IvParameterSpec se kreira koristeci inicijalizacioni vektor a potreban je za inicijalizaciju cipher objekta  
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        //metoda doFinal nakon sto se inicijalizuje metodom init, vrsi enkripciju otvorenog teksta
        return cipher.doFinal(plainText.getBytes());
    }

    //Funkcija koja prima sifrat (kriptovan tekst), kljuc i inicijalizacioni vektor i vraca dekriptovani tekst
    //generise sifrat (cipher text)
    public static String do_AESDecryption(byte[] cipherText, SecretKey secretKey, byte[] initializationVector) throws Exception
    {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        //ista metoda doFinal se koriti i za dekripciju
        byte[] result = cipher.doFinal(cipherText);
        return new String(result);
    }
}
