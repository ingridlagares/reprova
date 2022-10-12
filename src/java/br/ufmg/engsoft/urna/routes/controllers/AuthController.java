package br.ufmg.engsoft.urna.routes.controllers;

import spark.Spark;
import spark.Request;
import spark.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.engsoft.urna.services.handlers.CreateQuestionHandler;
import br.ufmg.engsoft.urna.services.handlers.DeleteQuestionHandler;
import br.ufmg.engsoft.urna.services.handlers.GetQuestionByIdHandler;
import br.ufmg.engsoft.urna.services.handlers.GetQuestionsHandler;
import br.ufmg.engsoft.urna.services.handlers.UpdateQuestionHandler;
import br.ufmg.engsoft.urna.services.inputs.CreateQuestionInput;
import br.ufmg.engsoft.urna.services.inputs.DeleteQuestionInput;
import br.ufmg.engsoft.urna.services.inputs.GetQuestionByIdInput;
import br.ufmg.engsoft.urna.services.inputs.GetQuestionsInput;
import br.ufmg.engsoft.urna.services.inputs.LoginInput;
import br.ufmg.engsoft.urna.services.inputs.UpdateQuestionInput;
import br.ufmg.engsoft.urna.services.interfaces.ICreateQuestionHandler;
import br.ufmg.engsoft.urna.services.interfaces.IDeleteQuestionHandler;
import br.ufmg.engsoft.urna.services.interfaces.IGetQuestionByIdHandler;
import br.ufmg.engsoft.urna.services.interfaces.IGetQuestionsHandler;
import br.ufmg.engsoft.urna.services.interfaces.IUpdateQuestionHandler;
import br.ufmg.engsoft.urna.services.outputs.CreateQuestionOutput;
import br.ufmg.engsoft.urna.services.outputs.DeleteQuestionOutput;
import br.ufmg.engsoft.urna.services.outputs.UpdateQuestionOutput;
import br.ufmg.engsoft.urna.mime.json.Json;

/**
 * Questions route.
 */
public class AuthController {
  /**
   * Logger instance.
   */
  protected static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

  /**
   * Messages.
   */
  protected static final String invalid = "\"Invalid request\"";
  protected static final String ok = "\"Ok\"";

  /**
   * Json formatter.
   */
  protected final Json json;

  public AuthController() {
    json = new Json();
  }

  /**
   * Install the endpoint in Spark.
   * Methods:
   * - POST
   */
  public void setup() {
    Spark.post("/api/auth/TSE", this::loginTSE);
    Spark.post("/api/auth/Clerk", this::loginClerk);

    logger.info("Setup /api/questions.");
  }

  /**
   * Check if the given token is authorized.
   */
  protected static boolean authorizedTSE(String token) {
    String TSE_token = System.getenv("TSE_TOKEN");
    return TSE_token.equals(token);
  }

  protected static boolean authorizedClerk(String token) {
    String Clerk_token = System.getenv("CLERK_TOKEN");
    return Clerk_token.equals(token);
  }

  /**
   * Get endpoint: lists all questions, or a single question if a 'id' query
   * parameter is
   * provided.
   */
  protected Object loginTSE(Request request, Response response) {
    String rightPassword = System.getenv("TSE_PWD");
    if(request.body().indexOf(rightPassword) == -1) {
      return invalid; 
    }

    response.status(200);
    String token = System.getenv(name: "TSE_TOKEN");
    return token;

  }

  /**
   * Get id endpoint: fetch the specified question from the database.
   * If not authorized, and the given question is private, returns an error
   * message.
   */
  protected Object loginClerk(Request request, Response response, String id, boolean auth) {
    if (id == null)
      throw new IllegalArgumentException("id mustn't be null");

    response.type("application/json");

    logger.info("Fetching question " + id);

    GetQuestionByIdInput input = new GetQuestionByIdInput(id);
    IGetQuestionByIdHandler handler = new GetQuestionByIdHandler();
    var output = handler.handle(input);

    var question = output.getQuestion();

    if (question == null) {
      logger.error("Invalid request!");
      response.status(400);
      return invalid;
    }

    if (question.pvt && !auth) {
      logger.info("Unauthorized token: " + token);
      response.status(403);
      return unauthorized;
    }

    logger.info("Done. Responding...");

    response.status(200);

    return json.render(question);
  }
}
