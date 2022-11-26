package br.ufmg.engsoft.reprova.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
   * Builder for Question.
   */
  public class QuestionBuilder {
    protected String id;
    protected String theme;
    protected String description;
    protected String statement;
    protected Map<Semester, Map<String, Float>> record;
    protected boolean pvt = true;
    protected String type;
    protected String optCount;
    protected List<String> options;

    public QuestionBuilder id(String id) {
      this.id = id;
      return this;
    }

    public QuestionBuilder theme(String theme) {
      this.theme = theme;
      return this;
    }

    public QuestionBuilder description(String description) {
      this.description = description;
      return this;
    }

    public QuestionBuilder statement(String statement) {
      this.statement = statement;
      return this;
    }

    public QuestionBuilder record(Map<Semester, Map<String, Float>> record) {
      this.record = record;
      return this;
    }

    public QuestionBuilder pvt(boolean pvt) {
      this.pvt = pvt;
      return this;
    }

    public QuestionBuilder type(String type) {
      this.type = type;
      return this;
    }

    public QuestionBuilder optCount(String optCount) {
      this.optCount = optCount;
      return this;
    }

    public QuestionBuilder options(List<String> options) {
      this.options = options;
      return this;
    }

    /**
     * Build the question.
     * @throws IllegalArgumentException  if any parameter is invalid
     */
    public Question build() {
      if (theme == null)
        throw new IllegalArgumentException("theme mustn't be null");

      if (theme.isEmpty())
        throw new IllegalArgumentException("theme mustn't be empty");

      if (description == null)
        throw new IllegalArgumentException("description mustn't be null");

      if (description.isEmpty())
        throw new IllegalArgumentException("description mustn't be empty");


      if (this.record == null)
        this.record = new HashMap<Semester, Map<String, Float>>();
      else
        // All inner maps mustn't be null:
        for (var entry : this.record.entrySet())
          if (entry.getValue() == null)
            throw new IllegalArgumentException("inner record mustn't be null");


        if("multiple_choice".equals(type)) {
          return new MultipleChoiceQuestion(
            this.id,
            this.theme,
            this.description,
            this.statement,
            this.record,
            this.pvt,
            this.optCount,
            this.options
          );
        } 

        return new OpenQuestion(
          this.id,
          this.theme,
          this.description,
          this.statement,
          this.record,
          this.pvt
        );
    }
  }