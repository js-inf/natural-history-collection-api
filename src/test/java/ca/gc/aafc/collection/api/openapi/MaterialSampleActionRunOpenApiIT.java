package ca.gc.aafc.collection.api.openapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionRunDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.FormTemplate;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.MaterialSampleFormComponent;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.TemplateField;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import lombok.SneakyThrows;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})

public class MaterialSampleActionRunOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = "material-sample-action-run";

  private static final UUID agentId = UUID.randomUUID();

  protected MaterialSampleActionRunOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void materialSampleActionRun_SpecValid() {
    MaterialSampleActionRunDto materialSampleActionRunDto = new MaterialSampleActionRunDto();
    materialSampleActionRunDto.setCreatedBy("test user");
    materialSampleActionRunDto.setGroup("group");
    materialSampleActionRunDto.setMaterialSampleActionDefinition(null);
    materialSampleActionRunDto.setSourceMaterialSample(null);
    materialSampleActionRunDto.setAgentId(agentId);

    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = new MaterialSampleActionDefinitionDto();
    materialSampleActionDefinitionDto.setCreatedBy("materialSample test user");
    materialSampleActionDefinitionDto.setGroup("materialSample aafc");
    materialSampleActionDefinitionDto.setName("materialSample definition name");
    materialSampleActionDefinitionDto.setActionType(MaterialSampleActionDefinition.ActionType.ADD);
    materialSampleActionDefinitionDto.setFormTemplates(new HashMap<>(Map.of(MaterialSampleFormComponent.MATERIAL_SAMPLE, FormTemplate.builder()
      .allowNew(true)
      .allowExisting(true)
      .templateFields(new HashMap<>(Map.of("materialSampleName", TemplateField.builder()
        .enabled(true)  
        .defaultValue("test-default-value")
        .build())))
      .build())));

    MaterialSampleDto materialSampleDto = new MaterialSampleDto();
    materialSampleDto.setCreatedBy("test user");  
    materialSampleDto.setGroup("test group");  
    materialSampleDto.setDwcCatalogNumber("55342");
    materialSampleDto.setMaterialSampleName( "S-412");
    materialSampleDto.setAttachment(null);
    materialSampleDto.setCollectingEvent(null);
    materialSampleDto.setParentMaterialSample(null);
    materialSampleDto.setMaterialSampleChildren(null);
    materialSampleDto.setPreparationProtocol(null);

    sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toAttributeMap(materialSampleDto)));
    sendPost(MaterialSampleActionDefinitionDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(MaterialSampleActionDefinitionDto.TYPENAME, JsonAPITestHelper.toAttributeMap(materialSampleActionDefinitionDto)));

    String materialSampleUUID = sendGet(MaterialSampleDto.TYPENAME, "").extract().response().body().path("data[0].id");
    String materialSampleActionDefinitionUUID = sendGet(MaterialSampleActionDefinitionDto.TYPENAME, "").extract().response().body().path("data[0].id");

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "MaterialSampleActionRun",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(materialSampleActionRunDto),
        JsonAPITestHelper.toRelationshipMap(List.of(
          JsonAPIRelationship.of("sourceMaterialSample", MaterialSampleDto.TYPENAME, materialSampleUUID),
          JsonAPIRelationship.of("materialSampleActionDefinition", MaterialSampleActionDefinitionDto.TYPENAME, materialSampleActionDefinitionUUID))
        ),
        null)
      ).extract().asString()); // Allow group field
  }

}
