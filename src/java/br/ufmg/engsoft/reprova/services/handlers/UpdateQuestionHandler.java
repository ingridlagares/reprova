package br.ufmg.engsoft.reprova.services.handlers;

import br.ufmg.engsoft.reprova.database.QuestionDAO;
import br.ufmg.engsoft.reprova.mime.json.Json;
import br.ufmg.engsoft.reprova.model.QuestionBuilder;
import br.ufmg.engsoft.reprova.model.MultipleChoiceQuestion;
import br.ufmg.engsoft.reprova.model.OpenQuestion;
import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.services.input.UpdateQuestionInput;
import br.ufmg.engsoft.reprova.services.interfaces.IUpdateQuestionHandler;
import br.ufmg.engsoft.reprova.services.output.UpdateQuestionOutput;

public class UpdateQuestionHandler implements IUpdateQuestionHandler {

	private static QuestionDAO dataAccess =  QuestionDAO.getInstance();
	
	@Override
	public UpdateQuestionOutput handle(UpdateQuestionInput input) {
		Question question;
    try {
      question = new Json()
        .parse(input.getBody(), QuestionBuilder.class)
        .build();
    
      if(System.getenv("MULTIPLE_CHOICE") == "false" 
        && question  instanceof MultipleChoiceQuestion
      ) {
        throw new Error("Suas configurações não te dão acesso a esta funcionalidade.");
      }
      if(System.getenv("OPEN") == "false"
        && question  instanceof OpenQuestion
      ) {
        throw new Error("Suas configurações não te dão acesso a esta funcionalidade.");
      }
    }
    catch (Exception e) {
      throw new Error(e);
    }

    boolean updated = dataAccess.update(input.getId(), question);
    return new UpdateQuestionOutput(updated);
	}
	
}
