package br.ufmg.engsoft.reprova.routes.controllers;

import spark.Spark;
import spark.Request;
import spark.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.engsoft.reprova.services.handlers.CreateQuestionHandler;
import br.ufmg.engsoft.reprova.services.handlers.DeleteQuestionHandler;
import br.ufmg.engsoft.reprova.services.handlers.GetQuestionByIdHandler;
import br.ufmg.engsoft.reprova.services.handlers.GetQuestionsHandler;
import br.ufmg.engsoft.reprova.services.handlers.UpdateQuestionHandler;
import br.ufmg.engsoft.reprova.services.input.CreateQuestionInput;
import br.ufmg.engsoft.reprova.services.input.DeleteQuestionInput;
import br.ufmg.engsoft.reprova.services.input.GetQuestionByIdInput;
import br.ufmg.engsoft.reprova.services.input.GetQuestionsInput;
import br.ufmg.engsoft.reprova.services.input.UpdateQuestionInput;
import br.ufmg.engsoft.reprova.services.interfaces.ICreateQuestionHandler;
import br.ufmg.engsoft.reprova.services.interfaces.IDeleteQuestionHandler;
import br.ufmg.engsoft.reprova.services.interfaces.IGetQuestionByIdHandler;
import br.ufmg.engsoft.reprova.services.interfaces.IGetQuestionsHandler;
import br.ufmg.engsoft.reprova.services.interfaces.IUpdateQuestionHandler;
import br.ufmg.engsoft.reprova.services.output.CreateQuestionOutput;
import br.ufmg.engsoft.reprova.services.output.DeleteQuestionOutput;
import br.ufmg.engsoft.reprova.services.output.UpdateQuestionOutput;
import br.ufmg.engsoft.reprova.mime.json.Json;


/**
 * Questions route.
 */
public class QuestionController {
  /**
   * Logger instance.
   */
  protected static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

  /**
   * Access token.
   */
  protected static final String Token = System.getenv("REPROVA_TOKEN");

  /**
   * Messages.
   */
  protected static final String Unauthorized = "\"Unauthorized\"";
  protected static final String Invalid = "\"Invalid request\"";
  protected static final String Ok = "\"Ok\"";

  /**
   * Json formatter.
   */
  protected final Json json;

  public QuestionController() {
    json = new Json();
  }

  /**
   * Install the endpoint in Spark.
   * Methods:
   * - GET
   * - POST
   * - PUT
   * - DELETE
   */
  public void setup() {
    Spark.get("/api/questions", this::get);
    Spark.post("/api/questions", this::post);
    Spark.put("/api/questions", this::put);
    Spark.delete("/api/questions", this::delete);

    logger.info("Setup /api/questions.");
  }


  /**
   * Check if the given token is authorized.
   */
  protected static boolean authorized(String token) {
    return QuestionController.Token.equals(token);
  }


  /**
   * Get endpoint: lists all questions, or a single question if a 'id' query parameter is
   * provided.
   */
  protected Object get(Request request, Response response) {
    logger.info("Received questions get:");

    var id = request.queryParams("id");
    var auth = authorized(request.queryParams("token"));

    return id == null
      ? this.get(response, auth)
      : this.get(response, id, auth);
  }

  /**
   * Get id endpoint: fetch the specified question from the database.
   * If not authorized, and the given question is private, returns an error message.
   */
protected Object get(Response response, String id, boolean auth) {
  if (id == null) {

      throw new IllegalArgumentException("id mustn't be null");
    }

    response.type("application/json");

    if (logger.isInfoEnabled()) {
      logger.info("Fetching question " + id);
    }

		GetQuestionByIdInput input = new GetQuestionByIdInput(id);
		IGetQuestionByIdHandler handler = new GetQuestionByIdHandler();
		var output = handler.handle(input);

    var question = output.getQuestion();

    if (question == null) {
      logger.error("Invalid request!");
      response.status(400);
      return Invalid;
    }

    if (question.pvt && !auth) {
      if (logger.isInfoEnabled()) {
        logger.info("Unauthorized token: " + Token);
      }
      response.status(403);
      return Unauthorized;
    }

    logger.info("Done. Responding...");

    response.status(200);

    return json.render(question);
  }

  /**
   * Get all endpoint: fetch all questions from the database.
   * If not authorized, fetches only public questions.
   */
  protected Object get(Response response, boolean auth) {
    response.type("application/json");

    logger.info("Fetching questions.");

		GetQuestionsInput input = new GetQuestionsInput(auth);
		IGetQuestionsHandler handler = new GetQuestionsHandler();
		var output = handler.handle(input);

    var questions = output.getQuestions();

    logger.info("Done. Responding...");

    response.status(200);

    return json.render(questions);
  }


  /**
   * Post endpoint: add a question in the database.
   * The question must be supplied in the request's body.
   * The given question is added as a new question in the database.
   * This endpoint is for authorized access only.
   */
  protected Object post(Request request, Response response) {
    String body = request.body();
    if (logger.isInfoEnabled()) {
      logger.info("Received questions post:" + body);
    }

    response.type("application/json");

    var newToken = request.queryParams("token");

    if (!authorized(newToken)) {          
    if (logger.isInfoEnabled()) {
      logger.info("Unauthorized token: " + newToken);
    }
      response.status(403);
      return Unauthorized;
    }

    if("false".equals(System.getenv("MULTIPLE_CHOICE")) && "false".equals(System.getenv("OPEN"))) {
      response.status(403);
      return Invalid;
    }
  
		CreateQuestionInput input = new CreateQuestionInput(body);
		ICreateQuestionHandler handler = new CreateQuestionHandler();
		CreateQuestionOutput output;
    try {
      output = handler.handle(input);
      logger.info("Parsed question");
      logger.info("Adding question.");
    } catch(Exception e) {
      logger.error("Invalid request payload!", e);
      response.status(400);
      return Invalid;
    }

    response.status(
			output.isCreated() ? 200
               : 400
    );

    logger.info("Done. Responding...");

    return output.isCreated() ? Ok : Invalid;
  }

  /**
   * Put endpoint: update a question in the database.
   * The question must be supplied in the request's body.
   * The given question is put as a the question with the given id.
   * This endpoint is for authorized access only.
   */
  protected Object put(Request request, Response response) {
    String body = request.body();

    var id = request.queryParams("id");
    var newToken = request.queryParams("token");

    if(logger.isInfoEnabled()) {
      logger.info("Received questions put:" + body);
    }

    response.type("application/json");


    if (!authorized(newToken)) {
      if(logger.isInfoEnabled()) {
        logger.info("Unauthorized token: " + newToken);
      }
      response.status(403);
      return Unauthorized;
    }

    UpdateQuestionInput input = new UpdateQuestionInput(id, body);
		IUpdateQuestionHandler handler = new UpdateQuestionHandler();
		UpdateQuestionOutput output;
    try {
      output = handler.handle(input);
      logger.info("Parsed question");
      logger.info("Adding question.");
    } catch(Exception e) {
      logger.error("Invalid request payload!", e);
      response.status(400);
      return Invalid;
    }

    response.status(
			output.isUpdated() ? 200
               : 400
    );

    logger.info("Done. Responding...");

    return output.isUpdated() ? Ok : Invalid;
  }


  /**
   * Delete endpoint: remove a question from the database.
   * The question's id must be supplied through the 'id' query parameter.
   * This endpoint is for authorized access only.
   */
  protected Object delete(Request request, Response response) {
    logger.info("Received questions delete:");

    response.type("application/json");

    var id = request.queryParams("id");
    var newToken = request.queryParams("token");

    if (!authorized(newToken)) {
      if(logger.isInfoEnabled()) {
        logger.info("Unauthorized token: " + newToken);
      }
      response.status(403);
      return Unauthorized;
    }

    if (id == null) {
      logger.error("Invalid request!");
      response.status(400);
      return Invalid;
    }

    if(logger.isInfoEnabled()) {
      logger.info("Deleting question " + id);
    }

		DeleteQuestionInput input = new DeleteQuestionInput(id);
		IDeleteQuestionHandler handler = new DeleteQuestionHandler();
		DeleteQuestionOutput output = handler.handle(input);


    logger.info("Done. Responding...");

    response.status(
      output.isDeleted() ? 200
              : 400
    );

    return output.isDeleted() ? Ok : Invalid;
  }
}
