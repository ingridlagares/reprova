package br.ufmg.engsoft.reprova.services.input;

public class DeleteQuestionInput {
	
	private String id;

	public DeleteQuestionInput(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
