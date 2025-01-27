package br.ufmg.engsoft.reprova.model;

import java.util.Map;
import java.util.Objects;


/**
 * The question type.
 */
public abstract class Question {
  /**
   * The id of the question.
   * When null, the id will be automatically generated by the database.
   */
  public final String id;
  /**
   * The theme of the question. Mustn't be null nor empty.
   */
  public final String theme;
  /**
   * The description of the question. Mustn't be null nor empty.
   */
  public final String description;
  /**
   * The statement of the question. May be null or empty.
   */
  public final String statement;
  /**
   * The record of the question per semester per class. Mustn't be null, may be empty.
   */
  public final Map<Semester, Map<String, Float>> record;
  /**
   * Whether the question is private.
   */
  public final boolean pvt;

  /**
   * Protected constructor, should only be used by the builder.
   */
  protected Question(
    String id,
    String theme,
    String description,
    String statement,
    Map<Semester, Map<String, Float>> record,
    boolean pvt
  ) {
    this.id = id;
    this.theme = theme;
    this.description = description;
    this.statement = statement;
    this.record = record;
    this.pvt = pvt;
  }

  /**
   * Equality comparison.
   * Although this object has an id, equality is checked on all fields.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Question))
      return false;

    var question = (Question) obj;

    return this.id.equals(question.id)
        && this.theme.equals(question.theme)
        && this.description.equals(question.description)
        && this.statement.equals(question.statement)
        && this.record.equals(question.record)
        && this.pvt == question.pvt;
  }


  @Override
  public int hashCode() {
    return Objects.hash(
      this.id,
      this.theme,
      this.description,
      this.statement,
      this.record,
      this.pvt
    );
  }


  /**
   * Convert a Question to String for visualization purposes.
   */
  @Override
  public String toString() {
    var builder = new StringBuilder();

    builder.append("Question:\n");
    builder.append("  id: " + this.id + "\n");
    builder.append("  theme: " + this.theme + "\n");
    builder.append("  desc: " + this.description + "\n");
    builder.append("  record: " + this.record + "\n");
    builder.append("  pvt: " + this.pvt + "\n");
    if (this.statement != null)
      builder.append(
        "  head: " +
        this.statement.substring(
          0,
          Math.min(this.statement.length(), 50)
        ) +
        "\n"
      );

    return builder.toString();
  }
}
