package org.b0102.share;

import lombok.Getter;

@Getter
public enum ErrorCode {
  NO_ERROR("no.error");
  private final String systemDescription;

  private ErrorCode() {
    this("");
  }

  ErrorCode(final String systemDescription) {
    this.systemDescription = systemDescription;
  }
}
