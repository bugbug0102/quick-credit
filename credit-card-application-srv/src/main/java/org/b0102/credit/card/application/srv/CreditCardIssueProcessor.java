package org.b0102.credit.card.application.srv;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardIssueEvent;
import org.b0102.credit.card.application.srv.dao.CreditCardApplicationRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;


@ApplicationScoped
public class CreditCardIssueProcessor {

  @Inject
  CreditCardApplicationRepository creditCardApplicationRepository;

  @ConfigProperty(name = "webhook.uri")
  String webhookUri;

  @Incoming(Topic.CREDIT_CARD_ISSUE_EVENT)
  public Uni<Void> onCreditCardIssueEvent(final CreditCardIssueEvent creditCardIssueEvent) {
    return Uni.createFrom().item(creditCardIssueEvent.getRequestId())
        .emitOn(Infrastructure.getDefaultExecutor()).map((requestId) -> {
          QuarkusTransaction.joiningExisting().run(() -> {
            final Instant now = Instant.now();
            creditCardApplicationRepository.getByRequestId(requestId).ifPresent((ccae) -> {
              ccae.setIssued(true);
              ccae.setIssuedAt(now);
            });
          });
          Log.infof("Card Issued (RequestId :%s)", requestId);
          return requestId;
        }).map((requestId) -> {
          //FIXME: should be replaced with silent push or similar mechanism
          try (final HttpClient hc = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUri))
                .POST(HttpRequest.BodyPublishers.ofString(
                    String.format("Card Issued (RequestId) :%s", requestId)))
                .build();
            hc.send(request, HttpResponse.BodyHandlers.ofString());
          } catch (final Throwable e) {
            Log.fatal(e);
            /** Ignore for demo. purposes **/
          }
          return null;
        }).replaceWithVoid();
  }
}
