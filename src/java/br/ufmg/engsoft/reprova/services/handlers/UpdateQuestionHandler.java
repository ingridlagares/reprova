package br.ufmg.engsoft.reprova.services.handlers;

import br.ufmg.engsoft.reprova.database.QuestionDAO;
import br.ufmg.engsoft.reprova.mime.json.Json;
import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.services.input.UpdateQuestionInput;
import br.ufmg.engsoft.reprova.services.interfaces.IUpdateQuestionHandler;
import br.ufmg.engsoft.reprova.services.output.UpdateQuestionOutput;

public class UpdateQuestionHandler implements IUpdateQuestionHandler {

	private static QuestionDAO DAO =  QuestionDAO.getInstance();
	
	@Override
	public UpdateQuestionOutput handle(UpdateQuestionInput input) {
		Question question;
    try {
      question = new Json()
        .parse(input.getBody(), Question.Builder.class)
        .build();
    }
    catch (Exception e) {
      throw new Error(e);
    }
  
    boolean updated = DAO.update(input.getId(), question);
    return new UpdateQuestionOutput(updated);
	}
	
}
