package br.ufmg.engsoft.reprova;

import br.ufmg.engsoft.reprova.database.QuestionsDAO;
import br.ufmg.engsoft.reprova.routes.Setup;


public class Reprova {
  public static void main(String[] args) {
    var questionsDAO = QuestionsDAO.getInstance();
    
    Setup.routes(questionsDAO);
  }
}
