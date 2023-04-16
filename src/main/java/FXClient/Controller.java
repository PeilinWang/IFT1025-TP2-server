package FXClient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.models.Course;

import java.util.ArrayList;

public class Controller {
    private Model model;
    private View view;

    /**
     Constructor.
     @param m Model
     @param v View
     */
    public Controller(Model m,  View v) {
        this.model = m;
        this.view = v;
    }

    /**
     The method chargerCours loads the list of courses for a given session.
     @param session the session for which we want to load the courses.
     */
    public void chargerCours(String session) {
        ArrayList<Course> listeDeCours = this.model.chargerCoursSession(session);
        updateTable(listeDeCours);
    }

    /**
      The method updateTable updates the table with the list of courses.
     @param listeDeCours list of courses.
     */
    private void updateTable(ArrayList<Course> listeDeCours) {
        ObservableList<Course> data = FXCollections.observableArrayList();
        data.addAll(listeDeCours);
        view.getTableView().setItems(data);
    }

    /**
     The method inscriptionCours registers the user with a form and a course.
      It also displays a window (popup) by calling doPopup.
     @param formulaire an array of strings containing the information from the form.
     @param course choice of course
     */
    public void inscriptionCours(String[] formulaire, Course course) {
        ArrayList<String> resultat = model.inscription(formulaire, course);
        doPopup(resultat, formulaire, course);
        model.getErreursMessage().clear();
    }

    /**
    The method doPopup displays a different window (popup) depending on whether the registration was successful or if
    there were errors in the fields requiring a certain format of response from the user.

     @param resultat list with the result of the registration.
     @param formulaire list with the information
     @param course the chosen course.
     */
    private void doPopup(ArrayList<String> resultat, String[] formulaire, Course course) {
        if (resultat.isEmpty()) {
            view.popupInscriptionSuccess(formulaire, course);
        } else {
            view.popupInscriptionErreur(resultat);
        }
    }
}
