package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.PreparationProcessDefinition;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class PreparationProcessDefinitionFactory implements TestableEntityFactory<PreparationProcessDefinition> {

  @Override
  public PreparationProcessDefinition getEntityInstance() {
    return newPreparationProcessDefinition().build();
  }
  
  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static PreparationProcessDefinition.PreparationProcessDefinitionBuilder newPreparationProcessDefinition() {
      return PreparationProcessDefinition
          .builder()
          .uuid(UUID.randomUUID())
          .createdBy("test user");
  }
  
}
