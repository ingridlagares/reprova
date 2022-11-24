package br.ufmg.engsoft.reprova.services;

import java.util.Collection;

import br.ufmg.engsoft.reprova.database.QuestionDAO;
import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.mime.json.Json;

/**
 * Servuce for Question entities.
 */
public final class QuestionService {
  private QuestionService() {}

  /**
   * DAO for Question.
   */
  private static QuestionDAO dataAccess =  QuestionDAO.getInstance();
  
    /**
   * Create a new question.
   * @param body the request body.
   * @return Whether the question was successfully added or not.
   */
  public static boolean create(String body) {
    Question question;
    try {
      question = new Json()
      .parse(body, Question.Builder.class)
      .build();
    }
    catch (Exception e) {
      throw new Error(e);
    }
    
    return dataAccess.add(question);
  }
  
  /**
 * Update a new question.
 * @param id id of the question.
 * @param body the request body.
 * @return Whether the question was successfully updated or not.
 */
  public static boolean update(String id, String body) {
    Question question;
    try {
      question = new Json()
        .parse(body, Question.Builder.class)
        .build();
    }
    catch (Exception e) {
      throw new Error(e);
    }
  
    return dataAccess.update(id, question);
  }

  /**
   * Get the question with the given id.
   * @param id  the question's id in the database.
   * @return The question, or null if no such question.
   */
  public static Question getByID(String id) {
    return dataAccess.get(id);
  }

    /**
   * Get all questions according to the current level of permission.
   * @param auth  the authentication token to identify access scope.
   * @return all the questions, or null if no questions on database.
   */
  public static Collection<Question> getAll(boolean auth) {
    return dataAccess.list(null, auth ? null : false);
  }

  /**
   * Delete the question with the given id.
   * @param id  the question id
   * @return Whether the given question was deleted or not.
   * @throws IllegalArgumentException  if any parameter is null
   */
  public static boolean deleteByID(String id) {
    return dataAccess.remove(id);
  }
}
