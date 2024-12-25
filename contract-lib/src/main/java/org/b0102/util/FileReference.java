package org.b0102.util;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileReference {

  private UUID reference;
  private Source source;

  public enum Source {
    INSECURE_BUCKET,
    SECURED_BUCKET
  }
}
