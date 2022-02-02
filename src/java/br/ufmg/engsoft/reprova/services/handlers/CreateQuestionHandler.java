package br.ufmg.engsoft.reprova.services.handlers;

import br.ufmg.engsoft.reprova.database.QuestionDAO;
import br.ufmg.engsoft.reprova.mime.json.Json;
import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.services.input.CreateQuestionInput;
import br.ufmg.engsoft.reprova.services.interfaces.ICreateQuestionHandler;
import br.ufmg.engsoft.reprova.services.output.CreateQuestionOutput;

public class CreateQuestionHandler implements ICreateQuestionHandler {

  private static QuestionDAO DAO =  QuestionDAO.getInstance();

	@Override
	public CreateQuestionOutput handle(CreateQuestionInput input) {
		Question question;
    try {
      question = new Json()
      .parse(input.getBody(), Question.Builder.class)
      .build();
    }
    catch (Exception e) {
      throw new Error(e);
    }
    boolean created = DAO.add(question);
		
		return new CreateQuestionOutput(created);
	}
	
}
