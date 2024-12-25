package org.b0102.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonAutoDetect(
    getterVisibility = Visibility.ANY
)
public class SensitiveString {

  //TODO: configurable
  private static final int DEFAULT_MAX_UNMASKED_STRING_LENGTH = 3;

  @EqualsAndHashCode.Include
  private final String data;
  private final int maximumUnmaskedStringLength;

  private SensitiveString() {
    this("", DEFAULT_MAX_UNMASKED_STRING_LENGTH);
  }

  @JsonCreator
  public SensitiveString(@JsonProperty("data") final String data) {
    this(data, DEFAULT_MAX_UNMASKED_STRING_LENGTH);
  }

  public SensitiveString(final String data, final int maximumUnmaskedStringLength) {
    if (data == null) {
      throw new NullPointerException("data cannot be null");
    }
    this.data = data;
    this.maximumUnmaskedStringLength = maximumUnmaskedStringLength;
  }

  public static SensitiveString fromString(final String data) {
    return new SensitiveString(data);
  }

  public String mask() {
    final int dataLength = data.length();
    if (dataLength > maximumUnmaskedStringLength) {
      return data.substring(0, maximumUnmaskedStringLength) + "*".repeat(
          dataLength - maximumUnmaskedStringLength);
    }
    return "*".repeat(maximumUnmaskedStringLength);
  }

  private String getData() {
    return this.data;
  }

  private int getMaximumUnmaskedStringLength() {
    return this.maximumUnmaskedStringLength;
  }

  @JsonIgnore
  public String getOriginal() {
    return this.data;
  }

}
