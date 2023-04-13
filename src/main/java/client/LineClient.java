public class LineClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1337;

    public static void main(String[] args) {
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'Udem ***");
        LineClient client = new LineClient();
        client.sessionsMessage();
    }

    public void sessionsMessage() {
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste de cours");
        System.out.println("1.Automne" + "\n" + "2.Hiver" + "\n" + "3.Été");
        System.out.print("> Choix: ");
    }
}
