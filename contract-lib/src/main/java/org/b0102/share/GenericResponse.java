package org.b0102.share;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GenericResponse {

  private String errorMessage;
  private ErrorCode errorCode;
}
