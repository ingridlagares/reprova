package br.ufmg.engsoft.reprova.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;


import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.engsoft.reprova.mime.json.Json;
import br.ufmg.engsoft.reprova.model.QuestionBuilder;
import br.ufmg.engsoft.reprova.model.MultipleChoiceQuestion;
import br.ufmg.engsoft.reprova.model.Question;


/**
 * DAO for Question class on mongodb.
 */
public final class QuestionDAO {
  /**
   * Singleton instance.
   */
  private static QuestionDAO instance;
  
  /**
   * Logger instance.
   */
  protected static final Logger logger = LoggerFactory.getLogger(QuestionDAO.class);

  /**
   * Json formatter.
   */
  protected final Json json;

  /**
   * Questions collection.
   */
  protected final MongoCollection<Document> collection;
  
  /**
   * Basic constructor.
   * @throws IllegalArgumentException  if any parameter is null
   */
  private QuestionDAO() {
    Mongo db = Mongo.getInstance();

    this.collection = db.getCollection("questions");

    this.json = new Json();
  }

  /**
   * Returns the application's QuestionsDAO instance.
   */
  public static QuestionDAO getInstance() {
    if(instance == null) 
      instance = new QuestionDAO();
    return instance;
  }

  /**
   * Parse the given document.
   * @param document  the question document, mustn't be null
   * @throws IllegalArgumentException  if any parameter is null
   * @throws IllegalArgumentException  if the given document is an invalid Question
   */
  protected Question parseDoc(Document document) {
    if (document == null)
      throw new IllegalArgumentException("document mustn't be null");

    var doc = document.toJson();

    logger.info("Fetched question: " + doc);

    try {
      var question = json
        .parse(doc, QuestionBuilder.class)
        .build();

      logger.info("Parsed question: " + question);

      return question;
    }
    catch (Exception e) {
      logger.error("Invalid document in database!", e);
      throw new IllegalArgumentException(e);
    }
  }


  /**
   * Get the question with the given id.
   * @param id  the question's id in the database.
   * @return The question, or null if no such question.
   * @throws IllegalArgumentException  if any parameter is null
   */
  public Question get(String id) {
    if (id == null)
      throw new IllegalArgumentException("id mustn't be null");

    var question = this.collection
      .find(eq(new ObjectId(id)))
      .map(this::parseDoc)
      .first();

    if (question == null)
      logger.info("No such question " + id);

    return question;
  }


  /**
   * List all the questions that match the given non-null parameters.
   * The question's statement is ommited.
   * @param theme      the expected theme, or null
   * @param pvt        the expected privacy, or null
   * @return The questions in the collection that match the given parameters, possibly
   *         empty.
   * @throws IllegalArgumentException  if there is an invalid Question
   */
  public Collection<Question> list(String theme, Boolean pvt) {
    var filters =
      Arrays.asList(
        theme == null ? null : eq("theme", theme),
        pvt == null ? null : eq("pvt", pvt)
      )
      .stream()
      .filter(Objects::nonNull) // mongo won't allow null filters.
      .collect(Collectors.toList());

    var doc = filters.isEmpty() // mongo won't take null as a filter.
      ? this.collection.find()
      : this.collection.find(and(filters));

    var result = new ArrayList<Question>();

    doc.projection(fields(exclude("statement")))
      .map(this::parseDoc)
      .into(result);

    return result;
  }


  /**
   * Adds the given question in the database.
   * @param question  the question to be stored
   * @return Whether the question was successfully added.
   * @throws IllegalArgumentException  if any parameter is null
   */
  public boolean add(Question question) {
    if (question == null)
      throw new IllegalArgumentException("question mustn't be null");

    Map<String, Object> record = question.record // Convert the keys to string,
      .entrySet()                                // and values to object.
      .stream()
      .collect(
        Collectors.toMap(
          e -> e.getKey().toString(),
          Map.Entry::getValue
        )
      );
      
    Document doc = new Document()
      .append("theme", question.theme)
      .append("description", question.description)
      .append("statement", question.statement)
      .append("record", new Document(record))
      .append("pvt", question.pvt);

    if(question instanceof MultipleChoiceQuestion) {
      MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
      doc.append("optCount", multipleChoiceQuestion.optCount)
      .append("options", multipleChoiceQuestion.options);
      
      if(Integer.valueOf(System.getenv("OPTIONS")) != multipleChoiceQuestion.options.size()) {
        throw new Error("Suas configurações apenas permitem questões de tamanho "+System.getenv("OPTIONS")+ ".");
      }
      
      if(multipleChoiceQuestion.options.size() != Integer.valueOf(multipleChoiceQuestion.optCount)) {
        return false;
      }
      
      if(Integer.valueOf(multipleChoiceQuestion.optCount) < 2) {
        return false;
      }
    }

    var id = question.id;
    if (id != null) {
      throw new IllegalArgumentException("to update use the put method");
    }
    
    this.collection.insertOne(doc);

    logger.info("Stored question " + doc.get("_id"));

    return true;
  }

  /**
   * Updates the question with the given id to the question received.
   * @param id  the question id
   * @param question  the new question body
   * @return Whether the question was successfully updated.
   * @throws IllegalArgumentException  if any parameter is null
   */
  public boolean update(String id,Question question) {
    if (id == null)
      throw new IllegalArgumentException("id mustn't be null");
    if (question == null)
      throw new IllegalArgumentException("question mustn't be null");

    Map<String, Object> record = question.record // Convert the keys to string,
      .entrySet()                                // and values to object.
      .stream()
      .collect(
        Collectors.toMap(
          e -> e.getKey().toString(),
          Map.Entry::getValue
        )
      );

    Document doc = new Document()
      .append("theme", question.theme)
      .append("description", question.description)
      .append("statement", question.statement)
      .append("record", new Document(record))
      .append("pvt", question.pvt);


      if(question instanceof MultipleChoiceQuestion) {
        MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
      doc.append("optCount", multipleChoiceQuestion.optCount)
        .append("options", multipleChoiceQuestion.options);

      if(Integer.valueOf(System.getenv("OPTIONS")) != multipleChoiceQuestion.options.size()) {
        throw new Error("Suas configurações apenas permitem questões de tamanho "+System.getenv("OPTIONS")+ ".");
      }
      
      if(multipleChoiceQuestion.options.size() != Integer.valueOf(multipleChoiceQuestion.optCount)) {
        return false;
      }

      if(Integer.valueOf(multipleChoiceQuestion.optCount) < 2) {
        return false;
      }
    }

    var result = this.collection.replaceOne(
      eq(new ObjectId(id)),
      doc
    );

    if (!result.wasAcknowledged()) {
      logger.warn("Failed to replace question " + id);
      return false;
    }
    
    logger.info("Updated question " + doc.get("_id"));

    return true;
  }


  /**
   * Remove the question with the given id from the collection.
   * @param id  the question id
   * @return Whether the given question was removed.
   * @throws IllegalArgumentException  if any parameter is null
   */
  public boolean remove(String id) {
    if (id == null)
      throw new IllegalArgumentException("id mustn't be null");

    Question question = this.get(id);
    if(question != null) {
       this.collection.deleteOne(
        eq(new ObjectId(id))
        );
        logger.info("Deleted question " + id);
        return true;
    }
    logger.warn("Failed to delete question " + id);
    return false;
  }
}
