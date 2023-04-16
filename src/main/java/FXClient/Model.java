package FXClient;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Model {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1337;
    private ArrayList<Course> listeDeCours = new ArrayList<>();
    private  ArrayList<String> erreursMessage = new ArrayList<>();

    /**
     The chargerCoursSession method communicates with the server to load the list of courses available for the chosen session.
     @param session the session for which we want to load the courses.
     @return the list of courses for the session
     */
    public ArrayList<Course> chargerCoursSession(String session) {
        try {
            Socket clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

            // Envoie au serveur la requête CHARGER
            objectOutputStream.writeObject("CHARGER " + session);

            listeDeCours = (ArrayList<Course>) objectInputStream.readObject();

            objectInputStream.close();
            objectOutputStream.close();

            return listeDeCours;
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     La méthode inscription sert à inscrire l'utilisateur au cours valide qu'il choisira. Elle communique avec le
     serveur pour modifier le fichier inscription.txt.
     @param formulaire un tableau de chaînes de caractères contenant les informations de l'étudiant (prénom, nom, courriel, matricule)
     @param course le cours choisi
     @return une liste de messages d'erreurs. Elle sera vide si l'inscription est réussie
     */
   public ArrayList<String> inscription(String[] formulaire, Course course) {
        // Checker si le formulaire a des erreurs
        if (formulaire[0].isEmpty()) {
            erreursMessage.add("Veillez écrire votre prénom");
        }
        if (formulaire[1].isEmpty()) {
            erreursMessage.add("Veillez écrire votre nom");
        }
        if (formulaire[2].isEmpty() | !formulaire[2].matches("[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[A-Za-z0-9](?:[-A-Za-z0-9]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[-A-Za-z0-9]*[A-Za-z0-9])?")) {
            erreursMessage.add("Votre courriel est invalide (format accepté: prenom.nom@umontreal.ca)");
        }
        if (formulaire[3].isEmpty() | !formulaire[3].matches("^[0-9]{6}$")) {
            erreursMessage.add("Votre matricule est invalide (format accepté: 123456)");
        }
        if (course == null) {
            erreursMessage.add("Veillez sélectionner un cours");
        }

        // Envoyer le formulaire s'il n'y a pas d'erreurs
        if (erreursMessage.isEmpty()) {
            try {
                // Connection au serveur
                Socket clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

                RegistrationForm inscription = new RegistrationForm(
                        formulaire[0], formulaire[1], formulaire[2], formulaire[3], course
                );

                objectOutputStream.writeObject("INSCRIRE");
                objectOutputStream.writeObject(inscription);

                objectInputStream.close();
                objectOutputStream.close();

                return erreursMessage;
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return erreursMessage;
        }
        return erreursMessage;
    }

    /**
     La méthode getErreursMessage renvoie la liste des messages d'erreur associés à l'inscription.
     @return une ArrayList de String contenant les messages d'erreur
     */
    public ArrayList<String> getErreursMessage() {
        return erreursMessage;
    }
}
