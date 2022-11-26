package br.ufmg.engsoft.reprova.model;

import java.util.List;
import java.util.Map;

public class MultipleChoiceQuestion extends Question {
      /**
   * How many options the question have if the question is multiple choice.
   */
  public final String optCount;
  /**
   * The options in the multiple choice question.
   */
  public final List<String> options;

    /**
   * Protected constructor, should only be used by the builder.
   */
  protected MultipleChoiceQuestion(
    String id,
    String theme,
    String description,
    String statement,
    Map<Semester, Map<String, Float>> record,
    boolean pvt,
    String optCount,
    List<String> options
  ) {
    super(id, theme, description, statement, record, pvt);
    this.optCount = optCount;
    this.options = options;
  }
  
}
