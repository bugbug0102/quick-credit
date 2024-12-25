package org.b0102.util;

import jakarta.persistence.AttributeConverter;

public class SensitiveStringConverter implements AttributeConverter<SensitiveString, String> {

  @Override
  public String convertToDatabaseColumn(final SensitiveString sensitiveString) {
    return sensitiveString.getOriginal();
  }

  @Override
  public SensitiveString convertToEntityAttribute(final String s) {
    return new SensitiveString(s);
  }
}
