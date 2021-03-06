package org.schemarepo.server;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.schemarepo.BaseRepository;
import org.schemarepo.InMemoryRepository;
import org.schemarepo.ValidatorFactory;
import org.schemarepo.json.GsonJsonUtil;
import org.schemarepo.rest.RESTRepository;


public class TestRESTRepository {

  BaseRepository backendRepo;
  RESTRepository repo;
  AuxiliaryRESTRepository auxRepo;

  @Before
  public void setUp() {
    Properties properties = new Properties();
    properties.setProperty("key", "value");
    backendRepo = new InMemoryRepository(new ValidatorFactory.Builder().build()) {
      @Override
      public void close() throws IOException {
        closed = true;
        super.close();
      }
    };
    repo = new MachineOrientedRESTRepository(backendRepo, new GsonJsonUtil());
    auxRepo = new AuxiliaryRESTRepository(backendRepo, properties);
  }

  @After
  public void tearDown() {
    repo = null;
  }

  @Test(expected = NotFoundException.class)
  public void testNonExistenSubject() throws Exception {
    repo.checkSubject(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON, "nothing");
  }

  @Test(expected = NotFoundException.class)
  public void testNonExistentSubjectList() throws Exception {
    repo.allSchemaEntries(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON, "nothing");
  }

  @Test
  public void testSchemaLatest() {
    repo.latest("application/vnd.schemaregistry.v1+json", "DS.Input.All.Cysxzb");
  }

  @Test(expected = NotFoundException.class)
  public void testNonExistentSubjectGetConfig() throws Exception {
    repo.subjectConfig(null, "nothing");
  }

  @Test
  public void testCreateNullSubject() {
    assertEquals(400, repo.createSubject("application/vnd.schemaregistry.v1+json", null, null).getStatus());
  }

  @Test
  public void testGetConfig() throws IOException {
    Properties properties = new Properties();
    properties.load(new StringReader(auxRepo.getConfiguration(null, false).getEntity().toString()));
    assertEquals("value", properties.getProperty("key"));
  }

  @Test
  public void testGetStatus() throws Exception {
    Response response = auxRepo.getStatus();
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertTrue(response.getEntity().toString().startsWith("OK"));
    backendRepo.close();
    response = auxRepo.getStatus();
    assertEquals(Status.SERVICE_UNAVAILABLE.getStatusCode(), response.getStatus());
    assertFalse(response.getEntity().toString().startsWith("OK"));
  }

  @Test
  public void testInfluenceOfMediaTypeSuccess() {
    final String contentType = "Content-Type";
    repo.createSubject("application/vnd.schemaregistry.v1+json", "dummy", new MultivaluedMapImpl());
    // null and all-inclusive (* or */*) mediaTypes result in the default configured
    // renderer being used
    for (String mediaType : new String[] {null, "", "*/*", "text/plain",
        "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"}) {
      Response response;
      try {
        response = repo.allSubjects(mediaType);
      } catch (WebApplicationException e) {
        response = e.getResponse();
      }
      assertEquals(Status.OK.getStatusCode(), response.getStatus());
      assertEquals(repo.getDefaultMediaType(), response.getMetadata().getFirst(contentType).toString());
    }

    Response response = repo.allSubjects("application/json");
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals("application/json", response.getMetadata().getFirst(contentType).toString());
  }

  @Test
  public void testInfluenceOfMediaTypeFailure() {
    final String contentType = "Content-Type";
    Response response = null;
    try {
      repo.allSubjects("image/jpeg");
    } catch (WebApplicationException e) {
      response = e.getResponse();
    }
    assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus());
  }

  @Test
  public void testSchemaGetsCreated() {
    repo.createSubject("application/vnd.schemaregistry.v1+json", "dummy", new MultivaluedMapImpl());
    Response response = repo.addSchema("application/vnd.schemaregistry.v1+json", "dummy", "schema");
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
  }

  @Test(expected = NotFoundException.class)
  public void testSchemaFailsCreationOnMissingSubject() {
    repo.addSchema("application/vnd.schemaregistry.v1+json", "missing", "schema");
  }

  @Test
  public void testFailingValidationReportsErrors() {
    MultivaluedMapImpl configParams = new MultivaluedMapImpl();
    configParams.putSingle("repo.validators", "repo.reject");
    repo.createSubject("application/vnd.schemaregistry.v1+json", "dummy", configParams);

    Response response = repo.addSchema("application/vnd.schemaregistry.v1+json", "dummy", "schema");

    assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    assertThat((String) response.getEntity(),
        containsString("repo.validator.reject validator always rejects validation"));
  }
}
