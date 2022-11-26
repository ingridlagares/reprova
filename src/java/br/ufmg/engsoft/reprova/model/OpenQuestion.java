package br.ufmg.engsoft.reprova.model;

import java.util.Map;

public class OpenQuestion extends Question {
    protected OpenQuestion(
        String id,
        String theme,
        String description,
        String statement,
        Map<Semester, Map<String, Float>> record,
        boolean pvt
      ) {
        super(id, theme, description, statement, record, pvt);
      }
}
