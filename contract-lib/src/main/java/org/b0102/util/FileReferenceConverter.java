package org.b0102.util;

import jakarta.persistence.AttributeConverter;
import java.util.UUID;

public class FileReferenceConverter implements AttributeConverter<FileReference, String> {

  @Override
  public String convertToDatabaseColumn(final FileReference fileReference) {
    return fileReference.getSource().toString() + "," + fileReference.getReference();
  }

  @Override
  public FileReference convertToEntityAttribute(final String s) {
    final String[] parts = s.split(",");
    return new FileReference(UUID.fromString(parts[1]), FileReference.Source.valueOf(parts[0]));

  }
}
