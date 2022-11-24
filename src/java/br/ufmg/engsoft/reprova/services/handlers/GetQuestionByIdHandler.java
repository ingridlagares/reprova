package br.ufmg.engsoft.reprova.services.handlers;

import br.ufmg.engsoft.reprova.services.interfaces.IGetQuestionByIdHandler;
import br.ufmg.engsoft.reprova.services.output.GetQuestionByIdOutput;
import br.ufmg.engsoft.reprova.database.QuestionDAO;
import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.services.input.GetQuestionByIdInput;

public class GetQuestionByIdHandler implements IGetQuestionByIdHandler {

	private static QuestionDAO dataAccess =  QuestionDAO.getInstance();

	@Override
	public GetQuestionByIdOutput handle(GetQuestionByIdInput input) {
		Question question = dataAccess.get(input.getId());

    return new GetQuestionByIdOutput(question);
	}

}
