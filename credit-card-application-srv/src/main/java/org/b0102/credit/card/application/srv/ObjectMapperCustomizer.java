package org.b0102.credit.card.application.srv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import jakarta.inject.Singleton;

@Singleton
class ObjectMapperCustomizer implements io.quarkus.jackson.ObjectMapperCustomizer {

  public void customize(final ObjectMapper mapper) {
    mapper.registerModule(new Jdk8Module());
  }
}