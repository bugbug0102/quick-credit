package org.b0102.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class SensitiveStringTest {

  @Test
  void test_sensitive_string_masking_for_short_string() {
    final String input = "fun";
    final SensitiveString ss = new SensitiveString(input, 5);
    final String masked = ss.mask();
    assertEquals(masked, "*".repeat(5));
  }

  @Test
  void test_sensitive_string_masking_for_long_string() {
    final String input = "this is a secret";
    final SensitiveString ss = new SensitiveString(input, 5);
    final String masked = ss.mask();
    assertEquals(masked, "this ***********");
  }

  @Test
  void test_sensitive_string_null() {
    final String input = null;
    assertThrows(NullPointerException.class, () -> {
      new SensitiveString(input, 5);
    });
  }

  @Test
  void test_sensitive_string_serde() throws JsonProcessingException {
    final ObjectMapper om = new ObjectMapper();
    final SensitiveString ss1 = new SensitiveString("Hello", 5);

    final String json = om.writeValueAsString(ss1);
    final SensitiveString ss2 = om.readValue(json, SensitiveString.class);

    assertEquals(ss1.getOriginal(), ss2.getOriginal());
    assertEquals(ss1.mask(), ss2.mask());
  }
}


