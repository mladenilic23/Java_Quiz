/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author ilicm
 */

public class QuizServer {

    private ServerSocket ssocket;
    private int port;

    private ArrayList<ServerConnectedClient> clients;
    private ArrayList<Contestant> contestants;
    private ArrayList<SetOfQuestions> sets;
    private ArrayList<String> admins;
    private SetOfQuestions activeSet;

    private SecretKey symmetricKey;
    private byte[] initializationVector;

    ObjectInputStream inContestants;

    private final String contestantsTxt = "D:\\JavaProjects\\contestants.txt";
    private final String adminsTxt = "D:\\JavaProjects\\admins.txt";
    private final String usersTxt = "D:\\JavaProjects\\users.txt";
    private final String setTxt = "D:\\JavaProjects\\set";

    private static final String AES = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";

    public ServerSocket getSsocket() {
        return ssocket;
    }

    public void setSsocket(ServerSocket ssocket) {
        this.ssocket = ssocket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setActiveSet(SetOfQuestions set) {
        this.activeSet = set;
    }

    public SetOfQuestions getActiveSet() {
        return activeSet;
    }

    public QuizServer(int port) throws Exception {
        this.clients = new ArrayList<>();
        this.contestants = new ArrayList<>();
        this.sets = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.symmetricKey = createAESKey();
        this.initializationVector = createInitializationVector();

        loadAdminsFromFile(adminsTxt);
        initializeServerSocket(port);
        loadContestantsFromFile(contestantsTxt);
        loadQuestionSets();
        initializeFirstAdmin();
    }

    //Ucitava admine iz txt fajla u 'admins' ArrayList-u.
    private void loadAdminsFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String adminLine;
            while ((adminLine = br.readLine()) != null) {
                if (!adminLine.isEmpty())
                    admins.add(adminLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Inicijalizuje server socket na odredjenom portu
    private void initializeServerSocket(int port) {
        try {
            this.port = port;
            this.ssocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(QuizServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Ucitava takmicare iz fajla u 'contestants' ArrayList.
    private void loadContestantsFromFile(String filename) {
        try {
            inContestants = new ObjectInputStream(new FileInputStream(filename));
            ArrayList<Contestant> contestants = new ArrayList<>();
            contestants = (ArrayList<Contestant>) inContestants.readObject();
            this.contestants = contestants;
            inContestants.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Ucitava setove pitanja iz fajlova u 'sets' ArrayList.
    private void loadQuestionSets() {
        for (int i = 1; i <= 4; i++) {
            SetOfQuestions set = new SetOfQuestions();
            set.setSetName("Set" + i);
            String finalSetTxt = setTxt + i + ".txt";
            loadQuestionsFromFile(finalSetTxt, set);
            sets.add(set);
        }
        this.activeSet = sets.get(0);
    }
    /*
    Ucitava pitanja iz datoteke i dodaje ih u zadati set.
    Format pitanja u datoteci treba da bude: redniBrojPitanja. TekstPitanja
    Format odgovora treba da bude: odgovorTekst. Primer: a) Odgovor na pitanje.
    Tacan odgovor oznacava se sa 'd)' ispred odgovora.
    */
    private void loadQuestionsFromFile(String filename, SetOfQuestions set) {
        Pattern questionPattern = Pattern.compile("^(?:1[0-1]|[1-9])\\..*$");
        Pattern answerPattern = Pattern.compile("^[a-d]\\)\\s*(.*)$");

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line, trimLine;
            Matcher questionMatcher, answerMatcher;
            QuizQuestions questions = new QuizQuestions();

            while ((line = br.readLine()) != null) {
                trimLine = line.trim();
                if (!trimLine.isEmpty()) {
                    questionMatcher = questionPattern.matcher(trimLine);
                    answerMatcher = answerPattern.matcher(trimLine);

                    if (questionMatcher.find()) 
                    {
                        questions = new QuizQuestions();
                        int keyIndex = trimLine.indexOf('.');
                        questions.setQuestionText(trimLine.substring(keyIndex + 2).trim());
                    } 
                    else if (answerMatcher.find()) 
                    {
                        Answer answer = new Answer();
                        int keyIndex = trimLine.indexOf(')');
                        answer.setAnswerText(trimLine.substring(keyIndex + 2).trim());
                        if (trimLine.charAt(0) == 'd') 
                        {
                            answer.setCorrect(true);
                        }
                        else
                        {
                            answer.setCorrect(false);
                        }
                        
                        questions.addAnswer(answer);

                        if (trimLine.charAt(0) == 'd')
                            set.addQuestion(questions);
                    }
                }
            }
            sets.add(set);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Inicijalizuje prvog admina ako nema takmicara i samo jedan admin u sistemu.
    private void initializeFirstAdmin() throws Exception {
        if (contestants.isEmpty() && admins.size() == 1) {
            String initialAdmin = "admin:admin:admin\n";
            byte[] encryptedInitialAdmin = do_AESEncryption(initialAdmin, symmetricKey, initializationVector);
            try (FileOutputStream writer = new FileOutputStream(usersTxt)) {
                writer.write(encryptedInitialAdmin);
            } catch (IOException e) {
                System.out.println("Error!");
            }
        }
    }
    
    //Prihvata klijente u petlji i kreira novu nit za svakog novog klijenta.
    //Izlazi iz petlje kucanjem 'Exit' na tastaturi.
    public void acceptClients() throws Exception {
        Socket client = null;
        Thread thr;

        Thread thrTerm;
        CloseApp termServer = new CloseApp(contestants, admins);
        thrTerm = new Thread(termServer);
        thrTerm.start();

        while (true) {
            try {
                System.out.println("Waiting for new clients..");
                client = this.ssocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(QuizServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (client != null) {
                //Povezao se novi klijent, kreiraj objekat klase ServerConnectedClient
                //koji ce biti zaduzen za komunikaciju sa njim
                ServerConnectedClient clnt = new ServerConnectedClient(client, clients, contestants, sets, admins, this, symmetricKey, initializationVector);
                //i dodaj ga na listu povezanih klijenata jer ce ti trebati kasnije
                clients.add(clnt);
                //kreiraj novu nit (konstruktoru prosledi klasu koja implementira Runnable interfejs)
                thr = new Thread(clnt);
                //..i startuj ga
                thr.start();
            } else {
                System.out.println("Client connection failed.");
                break;
            }
        }
    }
    
    //Generise AES kljuc za enkripciju.
    public static SecretKey createAESKey() throws Exception 
    {
        try
        {
            String seed = "RSZEOS2024";
            SecureRandom secureRandom = new SecureRandom();
            
            secureRandom.setSeed(seed.getBytes());
            KeyGenerator keygenerator = KeyGenerator.getInstance(AES);
            keygenerator.init(128, secureRandom);
            SecretKey key = keygenerator.generateKey();
            
            return key;  
        }
        catch(NoSuchAlgorithmException ex)
        {
            Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    //Generise inicijalizacioni vektor za enkripciju
    public static byte[] createInitializationVector()
    {
        //velicina inicijalizacionog vektora
        byte[] initializationVector = new byte[16];
        String seed = "RSZEOS2024";
        SecureRandom secureRandom = new SecureRandom();
        
        secureRandom.setSeed(seed.getBytes());
        secureRandom.nextBytes(initializationVector);
        return initializationVector;
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

    public static void main(String[] args) throws Exception {
        QuizServer server = new QuizServer(6001);

        System.out.println("Server started, listening on port 6001");
        System.out.println("Enter 'Exit' to shut down the application.");

        server.acceptClients();
    }
}
