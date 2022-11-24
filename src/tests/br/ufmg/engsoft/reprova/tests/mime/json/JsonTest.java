package br.ufmg.engsoft.reprova.tests.mime.json;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.ufmg.engsoft.reprova.mime.json.Json;
import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.model.Semester;


class JsonTest {
  /**
   * Rendering then parsing should produce an equivalent object.
   */
  @Test
  void question() {
    var question = new Question.Builder()
      .id("id")
      .theme("theme")
      .description("description")
      .statement("statement")
      .record(
        Map.of(
          new Semester(2019, Semester.Reference.one), Map.of(
            "tw", 50.0f,
            "tz", 49.5f,
            "tx", 51.2f
          ),
          new Semester(2020, Semester.Reference.one), Collections.emptyMap()
        )
      )
      .pvt(false)
      .build();

    var formatter = new Json();

    var json = formatter.render(question);

    var questionCopy = formatter
      .parse(json, Question.Builder.class)
      .build();

    assertEquals(question,questionCopy);
  }
}
