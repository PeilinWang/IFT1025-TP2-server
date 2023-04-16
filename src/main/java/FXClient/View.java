package FXClient;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import server.models.Course;

import java.util.ArrayList;

public class View extends Application {
  private final Controller controller = new Controller(new Model(), this);
  private final TableView<Course> tableView = new TableView<>();
  private final TextField prenom = new TextField();
  private final TextField nom = new TextField();
  private final TextField email = new TextField();
  private final TextField matricule = new TextField();
  private final Border errorBorder = new Border(new BorderStroke(Color.DARKORANGE, BorderStrokeStyle.SOLID,
      CornerRadii.EMPTY, BorderWidths.DEFAULT));
  private final Border normalBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
      CornerRadii.EMPTY, BorderWidths.DEFAULT));
  private final int width = 600;
  private final int height = 600;

  public static void main(String[] args) {
    View.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      HBox root = new HBox();
      Scene scene = new Scene(root, width, height);
      root.setBackground(new Background(
          new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));

      // Left Panel and Table
      VBox leftPanel = new VBox();
      leftPanel.setSpacing(5);
      leftPanel.setAlignment(Pos.TOP_CENTER);
      leftPanel.setPadding(new Insets(8, 8, 8, 8));

      Label labelCours = new Label("Liste des cours");
      labelCours.setFont(new Font("Arial", 22));
      labelCours.setAlignment(Pos.CENTER);
      labelCours.setPadding(new Insets(5, 5, 5, 5));

      TableColumn<Course, String> code = new TableColumn("Code");
      code.setMinWidth(50);
      code.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCode()));
      TableColumn<Course, String> cours = new TableColumn("Cours");
      cours.setMinWidth(250);
      cours.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

      tableView.setEditable(true);
      tableView.getColumns().addAll(code, cours);

      HBox sessionSelect = new HBox();
      sessionSelect.setSpacing(50);
      sessionSelect.setAlignment(Pos.CENTER);

      ChoiceBox<String> choiceBox = new ChoiceBox();
      ObservableList<String> session = choiceBox.getItems();
      session.add("Hiver");
      session.add("Ete");
      session.add("Automne");
      choiceBox.setMinSize(100, 0);

      Button charger = new Button("Charger");
      charger.setMinSize(100, 0);

      sessionSelect.getChildren().addAll(choiceBox, charger);

      Separator separatorH = new Separator();
      separatorH.setMinHeight(10);
      leftPanel.getChildren().addAll(labelCours, tableView, separatorH, sessionSelect);

      VBox rightPanel = new VBox();
      rightPanel.setSpacing(10);
      rightPanel.setAlignment(Pos.TOP_CENTER);
      rightPanel.setPadding(new Insets(10, 10, 10, 10));

      Label labelForm = new Label("inscription");
      labelForm.setFont(new Font("Arial", 20));
      labelForm.setAlignment(Pos.CENTER);
      labelForm.setPadding(new Insets(5, 5, 5, 5));

      Insets fieldInsets = new Insets(10, 10, 10, 10);
      prenom.setPromptText("Prénom");
      nom.setPromptText("Nom");
      email.setPromptText("Courriel");
      matricule.setPromptText("Matricule");
      prenom.setPadding(fieldInsets);
      nom.setPadding(fieldInsets);
      email.setPadding(fieldInsets);
      matricule.setPadding(fieldInsets);

      Button envoyer = new Button("Envoyer");
      envoyer.setMinSize(100, 0);

      rightPanel.getChildren().addAll(labelForm, prenom, nom, email, matricule, envoyer);

      charger.setOnAction((action) -> {
        controller.chargerCours(choiceBox.getValue());
      });
      envoyer.setOnAction((action) -> {
        String fPrenom = prenom.getText();
        String fNom = nom.getText();
        String fEmail = email.getText();
        String fMatricule = matricule.getText();
        String[] formulaire = new String[] { fPrenom, fNom, fEmail, fMatricule };
        Course fCours = tableView.getSelectionModel().getSelectedItem();

        controller.inscriptionCours(formulaire, fCours);
      });

      Separator separatorV = new Separator();
      separatorV.setOrientation(Orientation.VERTICAL);
      root.getChildren().addAll(leftPanel, separatorV, rightPanel);
      primaryStage.setTitle("Inscription UdeM");
      primaryStage.setResizable(false);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public TableView getTableView() {
    return tableView;
}

public void popupInscriptionSuccess(String[] formulaire, Course course) {
    Stage popup = new Stage();
    popup.setResizable(false);
    popup.setTitle("Inscription Reussite!");
    VBox root = new VBox();

    // Reset
    prenom.setBorder(normalBorder);
    nom.setBorder(normalBorder);
    email.setBorder(normalBorder);
    matricule.setBorder(normalBorder);
    tableView.setBorder(normalBorder);
    prenom.setText(null);
    nom.setText(null);
    email.setText(null);
    matricule.setText(null);

    HBox top = new HBox();
    Label topLabel = new Label("Message");
    topLabel.setFont(new Font("Arial", 32));
    topLabel.setPadding(new Insets(20,20,20,20));
    topLabel.setAlignment(Pos.CENTER);

    top.getChildren().addAll(topLabel);

    Separator separator = new Separator();

    Label bottomText = new Label("Félicitation! " + formulaire[0] + " " + formulaire[1] +
            " est inscrit(e) avec succès au cours " + course.getCode());
    bottomText.setFont(new Font("Arial", 15));
    bottomText.setWrapText(true);
    bottomText.setPadding(new Insets(10,10,10,10));

    HBox bnt = new HBox();
    bnt.setAlignment(Pos.BOTTOM_RIGHT);
    Button bouton = new Button("OK");
    bouton.setMinSize(50,0);
    bouton.setOnAction( (action) -> {
        popup.close();
    });
    bnt.setPadding(new Insets(10,10,10,10));
    bnt.getChildren().addAll(bouton);


    root.getChildren().addAll(top, separator, bottomText, bnt);

    Scene stageScene = new Scene(root, 300,200);
    popup.setScene(stageScene);
    popup.show();
}

public void popupInscriptionErreur(ArrayList<String> erreurs) {
    Stage popup = new Stage();
    popup.setResizable(false);
    popup.setTitle("Erreur d'inscription...");
    VBox root = new VBox();

    // Reset
    prenom.setBorder(normalBorder);
    nom.setBorder(normalBorder);
    email.setBorder(normalBorder);
    matricule.setBorder(normalBorder);
    tableView.setBorder(normalBorder);

    for (String erreur : erreurs) {
        switch (erreur) {
            case "Veillez écrire votre prénom":
                prenom.setBorder(errorBorder);
                break;
            case "Veillez écrire votre nom":
                nom.setBorder(errorBorder);
                break;
            case "Votre courriel est invalide (format prenom.nom@umontreal.ca)":
                email.setBorder(errorBorder);
                break;
            case "Votre matricule est invalide (format 12345678)":
                matricule.setBorder(errorBorder);
                break;
            case "Veillez sélectionner un cours":
                tableView.setBorder(errorBorder);
                break;
        }
    }

    HBox top = new HBox();
    Label topLabel = new Label("Erreur");
    topLabel.setFont(new Font("Arial", 32));
    topLabel.setPadding(new Insets(20,20,20,20));
    topLabel.setAlignment(Pos.CENTER);
    top.getChildren().addAll(topLabel);

    Separator separator = new Separator();

    VBox messages = new VBox();
    ArrayList<Label> textes = new ArrayList<>();
    for (String erreur : erreurs) {
        Label text = new Label(erreur);
        text.setFont(new Font("Arial", 12));
        text.setWrapText(true);
        text.setPadding(new Insets(2,2,2,2));
        textes.add(text);
    }

    messages.getChildren().addAll(textes);

    HBox bnt = new HBox();
    bnt.setAlignment(Pos.BOTTOM_RIGHT);
    Button bouton = new Button("OK");
    bouton.setMinSize(50,0);
    bouton.setOnAction( (action) -> {
        popup.close();
    });
    bnt.setPadding(new Insets(10,10,10,10));
    bnt.getChildren().addAll(bouton);

    root.getChildren().addAll(top, separator, messages, bnt);


    Scene stageScene = new Scene(root, 400,225);
    popup.setScene(stageScene);
    popup.show();
  }
}
