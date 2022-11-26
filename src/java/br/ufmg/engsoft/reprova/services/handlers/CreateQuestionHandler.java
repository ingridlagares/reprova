package br.ufmg.engsoft.reprova.services.handlers;

import br.ufmg.engsoft.reprova.database.QuestionDAO;
import br.ufmg.engsoft.reprova.mime.json.Json;
import br.ufmg.engsoft.reprova.model.QuestionBuilder;
import br.ufmg.engsoft.reprova.model.MultipleChoiceQuestion;
import br.ufmg.engsoft.reprova.model.OpenQuestion;
import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.services.input.CreateQuestionInput;
import br.ufmg.engsoft.reprova.services.interfaces.ICreateQuestionHandler;
import br.ufmg.engsoft.reprova.services.output.CreateQuestionOutput;

public class CreateQuestionHandler implements ICreateQuestionHandler {

  private static QuestionDAO dataAccess =  QuestionDAO.getInstance();

	@Override
	public CreateQuestionOutput handle(CreateQuestionInput input) {
		Question question;
    try {
      question = new Json()
      .parse(input.getBody(), QuestionBuilder.class)
      .build();
      
      if(System.getenv("MULTIPLE_CHOICE").equals("false") 
      && question instanceof MultipleChoiceQuestion
      ) {
        throw new Error("Suas configurações não te dão acesso a esta funcionalidade.");
      }
      if(System.getenv("OPEN").equals("false")
        && question  instanceof OpenQuestion
      ) {
        throw new Error("Suas configurações não te dão acesso a esta funcionalidade.");
      }
    }
    catch (Exception e) {
      throw new Error(e);
    }
  
    boolean created = dataAccess.add(question);
		
		return new CreateQuestionOutput(created);
	}
	
}
