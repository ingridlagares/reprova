package br.ufmg.engsoft.urna.services.inputs;

public class GetQuestionsInput {

	private boolean auth;

	public GetQuestionsInput(boolean auth) {
		this.auth = auth;
	}

	public boolean getAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

}
