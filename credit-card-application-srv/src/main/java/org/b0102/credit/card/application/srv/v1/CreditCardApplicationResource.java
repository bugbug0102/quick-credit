package org.b0102.credit.card.application.srv.v1;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.b0102.credit.card.application.srv.service.CreditCardApplicationService;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationAddModel;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationViewModel;
import org.b0102.credit.card.application.srv.v1.form.CreditCardApplicationAddForm;
import org.b0102.util.FileReference;
import org.b0102.util.FileReference.Source;
import org.b0102.util.SensitiveString;


@Path("/v1/credit-card-application-resource")
@Produces(MediaType.APPLICATION_JSON)
public class CreditCardApplicationResource {

  @Inject
  CreditCardApplicationService creditApplicationService;

  @GET
  @Path("/{applicationNumber}")
  public Uni<Optional<CreditCardApplicationViewModel>> get(
      @PathParam("applicationNumber") final String applicationNumber) {
    return creditApplicationService.getCreditApplicationByApplicationNumber(applicationNumber);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<String> add(@HeaderParam("request-id") final UUID requestId,
      final CreditCardApplicationAddForm creditApplicationAddForm) {

    final CreditCardApplicationAddModel caa = new CreditCardApplicationAddModel(
        requestId
        , SensitiveString.fromString(creditApplicationAddForm.getEmirateIdNumber())
        , SensitiveString.fromString(creditApplicationAddForm.getName())
        , SensitiveString.fromString(creditApplicationAddForm.getMobileNumber())
        , creditApplicationAddForm.getNationality()
        , SensitiveString.fromString(creditApplicationAddForm.getAddress())
        , creditApplicationAddForm.getIncome()
        , creditApplicationAddForm.getCurrentEmployer()
        , creditApplicationAddForm.getEmploymentStatus()
        , creditApplicationAddForm.getRequestedCreditLimit()
        , new FileReference(creditApplicationAddForm.getBankStatement(), Source.INSECURE_BUCKET)
    );
    return creditApplicationService.addCreditCardApplication(caa);
  }


  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Uni<String> upload(final Map<String, InputStream> parts) {
    return Uni.createFrom().item("hello");
  }

}
