public class LineClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1337;

    public static void main(String[] args) {
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'Udem ***");
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

                run = false;
            }
        }
    }

    public boolean verifyMatricule(String matricule) {
        return matricule.length() == 6 && matricule.matches("[0-9]+");
    }

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
            List<String> courses = new ArrayList<>();
            courses = (ArrayList) receiver.readObject();

            for (int i = 0; i < courses.size(); i++) {
                String course = courses.get(i);
                String[] splitcourse = course.split("\t");
                if (splitcourse.length != 3) {
                    System.out.println("Error in line " + i);
                    continue;
                }
                System.out.println("1. " + splitcourse[0] + "\t" + splitcourse[1]);
            }
            writer.close();
            receiver.close();

            System.out.println("> Choix: ");
            System.out.println("1. Consulter les cours offerts pour une autre session");
            System.out.println("2. Inscription à un cours");
            System.out.print("> Choix: ");

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.out.println("Erreur: Classe Introuvable");
        }
    }

    public void sessionsMessage() {
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste de cours");
        System.out.println("1.Automne" + "\n" + "2.Hiver" + "\n" + "3.Été");
        System.out.print("> Choix: ");
    }
}
