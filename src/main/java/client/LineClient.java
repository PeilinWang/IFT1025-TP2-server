package client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import server.models.Course;
import server.models.RegistrationForm;
import java.util.Scanner;

public class LineClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1337;

    public static void main(String[] args) {
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'Udem ***");
        // Création du client
        LineClient client = new LineClient();
        client.sessionsMessage();

        Scanner scanner = new Scanner(System.in);
        int etat = 0;
        String session = null;
        boolean run = true;
        while (run) {
            // Choix session
            if (etat == 0) {
                int choix1 = Integer.parseInt(scanner.nextLine());
                if (choix1 == 1 || choix1 == 2 || choix1 == 3) {
                    switch (choix1) {
                        case 1:
                            session = "Automne";
                            break;
                        case 2:
                            session = "Hiver";
                            break;
                        case 3:
                            session = "Ete";
                    }
                    client.choiceSession(choix1);
                    etat = 1;
                } else {
                    throw new IllegalArgumentException("Entrée invalide");
                }
            }
            // Choix inscription ou sélection d'une autre session
            if (etat == 1) {
                int choix2 = Integer.parseInt(scanner.nextLine());
                if (choix2 == 1) {
                    client.sessionsMessage();
                    etat = 0;

                } else if (choix2 == 2) {
                    etat = 2;
                } else {
                    throw new IllegalArgumentException("Entrée invalide");
                }
            }
            // Envoi de l'inscription
            if (etat == 2) {

                System.out.print("\n" + "Veuiller saisir votre prénom: ");
                String prenom = scanner.nextLine();
                System.out.print("Veuiller saisir votre nom: ");
                String nom = scanner.nextLine();
                System.out.print("Veuiller saisir votre email: ");
                String email = scanner.nextLine();
                System.out.print("Veuiller saisir votre matricule: ");
                String matricule = scanner.nextLine();
                System.out.print("Veuiller saisir le code du cours: ");
                String code = scanner.nextLine();

                if (!client.verifyEmail(email)) {
                    throw new IllegalArgumentException("L'addrese courriel rentré est invalide");
                }
                if (!client.verifyCodeCourse(code, session)) {
                    throw new IllegalArgumentException("Le code du cours rentré est invalide");
                }
                if (!client.verifyMatricule(matricule)) {
                    throw new IllegalArgumentException("Le matricule rentré est invalide");
                } 
                else {
                    client.inscription(nom, prenom, email, matricule, session, code);
                }
                scanner.close();
                run = false;
            }
        }
    }

    // Fonction qui affiche le message d'accueil
    public void sessionsMessage() {

        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste de cours");
        System.out.println("1.Automne" + "\n" + "2.Hiver" + "\n" + "3.Été");
        System.out.print("> Choix: ");

    }

    // Fonction en charge d'afficher les cours des différentes sessions
    public void choiceSession(int choix) {
        try {
            String saison = null; // Variable qui contient la saison
            String session = null; // Variable qui contient la session
            if (choix == 1) {
                saison = "automne";
                session = "CHARGER Automne";
            } else if (choix == 2) {
                saison = "hiver";
                session = "CHARGER Hiver";
            } else if (choix == 3) {
                saison = "été";
                session = "CHARGER Ete";
            }

            Socket clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream receiver = new ObjectInputStream(clientSocket.getInputStream());

            writer.writeObject(session);
            writer.flush();
            System.out.println("Les cours offerts pendant la session d'" + saison + " sont:");
            ArrayList<Course> courses = new ArrayList<>();
            courses = (ArrayList<Course>) receiver.readObject();

            // courses is a arrayList of Course objects with the form of course Name, course Code and session. Print list of courses in a ordered list tabular format

            for(int i = 0; i<courses.size(); i++){
                Course course = courses.get(i);
                System.out.println((i+1) + ". " + course.getCode() + "\t" + course.getName()  + "\t" + course.getSession());
            }

            
            writer.close();
            receiver.close();

            System.out.println("> Choix: ");
            System.out.println("1. Consulter les cours offerts pour une autre session");
            System.out.println("2. Inscription à un cours");
            System.out.print("> Choix: ");
            
            clientSocket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.out.println("Erreur: Classe Introuvable");
        }
    }
    // function to verify whether the number has 6 digits and are all numbers
    public boolean verifyMatricule(String matricule) {
        return matricule.length() == 6 && matricule.matches("[0-9]+");
    }

    // Fonction en charge de vérifier que le email fourni est valide
    public boolean verifyEmail(String email) {
        return email.contains("@") && email.split("@")[1].contains(".") && email.split("@")[0].length() > 0 && email.split("@")[1].split("\\.")[0].length() > 0 && email.split("@")[1].split("\\.")[1].length() > 0;
    }

    // Fonction en charge de vérifier que le cours choisi correspond à la bonne
    // session
    public boolean verifyCodeCourse(String code, String session) {
        boolean check = false;
        try {
            session = "CHARGER " + session;
            Socket clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream receiver = new ObjectInputStream(clientSocket.getInputStream());

            writer.writeObject(session);
            writer.flush();

            List<String> courses = new ArrayList<>();
            courses = (ArrayList) receiver.readObject();

            for (int i = 0; i < courses.size(); i++) {
                String course = courses.get(i);
                String[] splitcourse = course.split("\t");
                if (splitcourse[0].equals(code)) {
                    check = true;
                }
            }
            writer.close();
            receiver.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.out.println("Erreur: Classe Introuvable");
        }
        return check;
    }

    // Fonction en charge d'effectuer l'inscription
    public void inscription(String nom, String prenom, String email, String matricule, String session, String code) {
        try {
            Socket clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            Course coursChoisi = new Course(nom, code, session);

            RegistrationForm fileInscription = new RegistrationForm(prenom, nom, email, matricule, coursChoisi);
            ObjectOutputStream input = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream output = new ObjectInputStream(clientSocket.getInputStream());

            input.writeObject("INSCRIRE");
            input.flush();
            input.writeObject(fileInscription);
            input.flush();

            String success = output.readObject().toString();

            System.out.println("\n" + success);

            input.close();
            output.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.out.println("Erreur: Classe Introuvable");
        }
    }
}