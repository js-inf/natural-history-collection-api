package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.InstitutionDto;
import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.collection.api.service.InstitutionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAdminCUDAuthorizationService;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InstitutionRepository extends DinaRepository<InstitutionDto, Institution> {

  private final DinaAuthenticatedUser dinaAuthenticatedUser;

  public InstitutionRepository(
    @NonNull InstitutionService dinaService,
    @NonNull DinaAdminCUDAuthorizationService adminOnlyAuthorizationService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      dinaService,
      adminOnlyAuthorizationService,
      Optional.empty(),
      new DinaMapper<>(InstitutionDto.class),
      InstitutionDto.class,
      Institution.class,
      null,
      externalResourceProvider,
      buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser.orElse(null);
  }

  @Override
  public <S extends InstitutionDto> S create(S resource) {
    if (dinaAuthenticatedUser != null) {
      resource.setCreatedBy(dinaAuthenticatedUser.getUsername());
    }
    return super.create(resource);
  }

}
