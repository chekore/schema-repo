package org.schemarepo.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.NotFoundException;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.schemarepo.Message;
import org.schemarepo.Repository;
import org.schemarepo.SchemaEntry;
import org.schemarepo.Subject;
import org.schemarepo.SubjectConfig;
import org.schemarepo.server.BaseRESTRepository;
import org.schemarepo.server.CustomMediaType;
import org.schemarepo.server.Renderer;
import org.schemarepo.server.RepositoryServer;
import org.schemarepo.utils.MessageAcknowledgement;
import org.schemarepo.utils.StatusCodes;


/**
 * {@link RESTRepository} Is a JSR-311 REST Interface to a {@link Repository}.
 *
 * Combine with {@link RepositoryServer} to run an embedded REST server.
 *
 * This is an abstract base class. Concrete implementations (such as
 * {@link org.schemarepo.server.MachineOrientedRESTRepository} and
 * {@link org.schemarepo.server.HumanOrientedRESTRepository}) handle media types differently and are
 * accessible via different paths, though the actual functionality of accessing the underlying
 * repository server is contained in this class.
 */
public class RESTRepository extends BaseRESTRepository {

  /**
   * Create a {@link RESTRepository} that wraps a given {@link Repository} Typically the wrapped
   * repository is a {@link org.schemarepo.CacheRepository} that wraps a non-caching underlying
   * repository.
   *
   * @param repo      The {@link Repository} to wrap.
   * @param renderers determine which content types (based on the <b>Accept</b> header) will be
   *                  supported; the first renderer will act as default (handling missing or wildcard
   *                  media type)
   */
  public RESTRepository(Repository repo, List<? extends Renderer> renderers) {
    super(repo, renderers);
  }

  /**
   * No @Path annotation means this services the "/" endpoint.
   *
   * @return All subjects in the repository, serialized with
   *         {@link org.schemarepo.RepositoryUtil#subjectsToString(Iterable)}
   */
  @GET
  @Produces(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response allSubjects(@HeaderParam("Accept") String accept) {
    if (!CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON.equalsIgnoreCase(accept)) {
      logger.error("Accept is not set correctly, Method: allSubjects");
      return Response.status(StatusCodes.INVALID_REQUEST).entity(Message.ACCEPT_ERROR).build();
    } else {
      List<String> sl = new ArrayList<>();
      repo.subjects().forEach(subject -> sl.add(subject.getName()));
      logger.info("Query all subjects in the repository is successful.");
      return Response.ok(sl).build();
    }
  }

  /**
   * Returns all schemas in the given subject, serialized with
   * {@link org.schemarepo.RepositoryUtil#schemasToString(Iterable)}
   *
   * @param subject The name of the subject
   * @return all schemas in the subject. Return a 404 Not Found if there is no such subject
   */
  @GET
  @Path("{subject}/all")
  @Produces(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response allSchemaEntries(@HeaderParam("Accept") String accept, @PathParam("subject") String subject) {
    MessageAcknowledgement<String> acknowledgement;
    if (!CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON.equalsIgnoreCase(accept)) {
      logger.error("Accept is not set correctly, Method: allSchemaEntries, subject: {}", subject);
      acknowledgement =
          new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(), Message.ACCEPT_ERROR, null);
    } else if (StringUtils.isAnyBlank(subject)) {
      logger.error("Invalid Parameter Passed to function, Method: allSchemaEntries, subject: {}", subject);
      acknowledgement = new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(),
          StatusCodes.INVALID_REQUEST.getReasonPhrase(), null);
    } else {
      Subject s = repo.lookup(subject);
      if (null == s) {
        logger.error("This subject does not exist, suject: {}", subject);
        acknowledgement = new MessageAcknowledgement<>(StatusCodes.NOT_FOUND.getStatusCode(),
            Message.SUBJECT_DOES_NOT_EXIST_ERROR, null);
      } else {
        List<SchemaEntry> sl = new ArrayList<>();
        s.allEntries().forEach(sl::add);
        logger.info("Query all schema in the subject is successful. subject: {}", subject);
        return Response.ok(sl).build();
      }
    }
    return Response.ok(acknowledgement).build();
  }

  @GET
  @Path("{subject}/config")
  public String subjectConfig(@HeaderParam("Accept") String mediaType, @PathParam("subject") String subject) {
    Subject s = repo.lookup(subject);
    if (null == s) {
      throw new NotFoundException(Message.SUBJECT_DOES_NOT_EXIST_ERROR);
    }
    Properties props = new Properties();
    props.putAll(s.getConfig().asMap());
    return getRenderer(mediaType).renderProperties(props, "Configuration of subject " + subject);
  }

  /**
   * Create a subject if it does not already exist.
   *
   * @param subject      the name of the subject
   * @param configParams the configuration values for the Subject, as form parameters
   * @return the subject name in a 200 response if successful. HTTP 412 if the subject does not exist,
   *         or if there was a conflict creating the subject
   */
  @POST
  @Path("{subject}")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response createSubject(@HeaderParam("Accept") String accept, @PathParam("subject") String subject,
      MultivaluedMap<String, String> configParams) {
    MessageAcknowledgement<String> acknowledgement;
    if (!CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON.equalsIgnoreCase(accept)) {
      logger.error("Accept is not set correctly, Method: createSubject, subject: {}", subject);
      acknowledgement =
          new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(), Message.ACCEPT_ERROR, null);
    } else if (StringUtils.isAnyBlank(subject)) {
      logger.error("Invalid Parameter Passed to function, Method: createSubject, subject: {}, configParams: {}",
          subject, configParams);
      acknowledgement = new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(),
          StatusCodes.INVALID_REQUEST.getReasonPhrase(), null);
    } else {
      SubjectConfig.Builder builder = new SubjectConfig.Builder();
      for (Map.Entry<String, List<String>> entry : configParams.entrySet()) {
        List<String> val = entry.getValue();
        if (val.size() > 0) {
          builder.set(entry.getKey(), val.get(0));
        }
      }
      try {
        Subject created = repo.register(subject, builder.build());
        acknowledgement = new MessageAcknowledgement<>(StatusCodes.CREATED.getStatusCode(),
            StatusCodes.CREATED.getReasonPhrase(), created.getName());
        logger.info("Create the subject is successful. subject: {}", subject);
      } catch (Exception e) {
        logger.error("Create the subject is failed. subject: {}, err: ", subject, e.getMessage());
        acknowledgement =
            new MessageAcknowledgement<>(StatusCodes.UNPROCESSABLE_ENTITY.getStatusCode(), e.getMessage(), null);
      }
    }
    return Response.ok(acknowledgement).build();
  }

  @DELETE
  @Path("{subject}")
  @Consumes(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  @Produces(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response deleteSubject(@HeaderParam("Accept") String accept, @PathParam("subject") String subject) {
    MessageAcknowledgement<String> acknowledgement;
    if (!CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON.equalsIgnoreCase(accept)) {
      logger.error("Accept is not set correctly, Method: deleteSubject, subject: {}", subject);
      acknowledgement =
          new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(), Message.ACCEPT_ERROR, null);
    } else if (StringUtils.isAnyBlank(subject)) {
      logger.error("Invalid Parameter Passed to function, Method: deleteSubject, subject: {}", subject);
      acknowledgement = new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(),
          StatusCodes.INVALID_REQUEST.getReasonPhrase(), null);
    } else {
      try {
        if (repo.delete(subject)) {
          acknowledgement = new MessageAcknowledgement<>(StatusCodes.NO_CONTENT.getStatusCode(),
              "Delete subject is successful.", subject);
        } else {
          acknowledgement =
              new MessageAcknowledgement<>(StatusCodes.NOT_FOUND.getStatusCode(), "The subject is not exist.", subject);
        }
      } catch (Exception e) {
        acknowledgement =
            new MessageAcknowledgement<>(StatusCodes.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage(), subject);
      }
    }
    return Response.ok(acknowledgement).build();
  }

  /**
   * Get the latest schema for a subject
   *
   * @param subject the name of the subject
   * @return A 200 response with {@link SchemaEntry#toString()} as the body, or a 404 response if
   *         either the subject or latest schema is not found.
   */
  @GET
  @Path("{subject}/latest")
  @Produces(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response latest(@HeaderParam("Accept") String accept, @PathParam("subject") String subject) {
    if (!CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON.equalsIgnoreCase(accept)) {
      logger.error("Accept is not set correctly, Method: latest, subject: {}", subject);
      return Response.status(StatusCodes.INVALID_REQUEST).entity(Message.ACCEPT_ERROR).build();
    } else {
      logger.info("Get the latest schema for the subject is successful. subject: {}", subject);
      return Response.ok(exists(getSubject(subject).latest())).build();
    }
  }

  /**
   * Look up a schema by subject + id pair.
   *
   * @param subject the name of the subject
   * @param id      the id of the schema
   * @return A 200 response with the schema as the body, or a 404 response if the subject or schema is
   *         not found
   */
  @GET
  @Path("{subject}/id/{id}")
  public String schemaFromId(@HeaderParam("Accept") String mediaType, @PathParam("subject") String subject,
      @PathParam("id") String id) {
    return getRenderer(mediaType).renderSchemaEntry(exists(getSubject(subject).lookupById(id)), false);
  }

  /**
   * Look up an id by a subject + schema pair.
   *
   * @param subject the name of the subject
   * @param schema  the schema to search for
   * @return A 200 response with the id in the body, or a 404 response if the subject or schema is not
   *         found
   */
  @POST
  @Path("{subject}/schema")
  @Consumes(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response idFromSchema(@PathParam("subject") String subject, String schema) {
    try {
      return Response.ok(exists(getSubject(subject).lookupBySchema(schema)).getId()).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  /**
   * Register a schema with a subject
   *
   * @param subject The subject name to register the schema in
   * @param schema  The schema to register
   * @return A 200 response with the corresponding id if successful, a 400 invalid response with
   *         exception message if the schema fails validation, or a 404 not found response if the
   *         subject does not exist
   */
  @POST
  @Path("{subject}/register")
  @Consumes(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  @Produces(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response addSchema(@HeaderParam("Accept") String accept, @PathParam("subject") String subject, String schema) {
    MessageAcknowledgement<String> acknowledgement;
    if (!CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON.equalsIgnoreCase(accept)) {
      logger.error("Accept is not set correctly, Method: addSchema, subject: {}", subject);
      acknowledgement =
          new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(), Message.ACCEPT_ERROR, null);
    } else if (StringUtils.isAnyBlank(subject, schema)) {
      logger.error("Invalid Parameter Passed to function, Method: addSchema, subject: {}, schema: {}", subject, schema);
      acknowledgement = new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(),
          StatusCodes.INVALID_REQUEST.getReasonPhrase(), null);
    } else {
      try {
        String tmp;
        if (schema.endsWith("\n")) {
          tmp = schema.substring(0, schema.lastIndexOf("\n"));
        } else {
          tmp = schema;
        }
        // Verifying schema
        new Schema.Parser().parse(tmp);
        acknowledgement = new MessageAcknowledgement<>(StatusCodes.CREATED.getStatusCode(),
            StatusCodes.CREATED.getReasonPhrase(), getSubject(subject).register(tmp).getId());
        logger.info("Register a schema with {} is successful.", subject);
      } catch (Exception e) {
        logger.error("Register a schema with {} is failed, err: {}", subject, e.getMessage());
        acknowledgement =
            new MessageAcknowledgement<>(StatusCodes.UNPROCESSABLE_ENTITY.getStatusCode(), e.getMessage(), null);
      }
    }
    return Response.ok(acknowledgement).build();
  }

  /**
   * Register a schema with a subject, only if the latest schema equals the expected value. This is
   * for resolving race conditions between multiple registrations and schema invalidation events in
   * underlying repositories.
   *
   * @param subject  the name of the subject
   * @param latestId the latest schema id, possibly null
   * @param schema   the schema to attempt to register
   * @return a 200 response with the id of the newly registered schema, or a 404 response if the
   *         subject or id does not exist or a 409 conflict if the id does not match the latest id or
   *         a 422 Unprocessable Entity response with exception message if the schema fails validation
   */
  @PUT
  @Path("{subject}/register_if_latest/{latestId: .*}")
  @Consumes(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  @Produces(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response addSchema(@HeaderParam("Accept") String accept, @PathParam("subject") String subject,
      @PathParam("latestId") String latestId, String schema) {
    MessageAcknowledgement<String> acknowledgement;
    if (!CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON.equalsIgnoreCase(accept)) {
      logger.error("Accept is not set correctly, Method: addSchema/register_if_latest, subject: {}", subject);
      acknowledgement =
          new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(), Message.ACCEPT_ERROR, null);
    } else if (StringUtils.isAnyBlank(subject, schema)) {
      logger.error(
          "Invalid Parameter Passed to function, Method: addSchema/register_if_latest, subject: {}, schema: {}",
          subject, schema);
      acknowledgement = new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(),
          StatusCodes.INVALID_REQUEST.getReasonPhrase(), null);
    } else {
      try {
        Subject s = getSubject(subject);
        SchemaEntry latest;
        if (StringUtils.isAnyBlank(latestId)) {
          latest = null;
        } else {
          latest = exists(s.lookupById(latestId));
        }
        SchemaEntry created = s.registerIfLatest(schema, latest);
        if (null == created) {
          logger.warn(
              "Register a schema with a subject is conflicting, only if the latest schema equals the expected value.");
          acknowledgement =
              new MessageAcknowledgement<>(Status.CONFLICT.getStatusCode(), Status.CONFLICT.getReasonPhrase(), null);
        } else {
          logger.info(
              "Register a schema with a subject is successful, only if the latest schema equals the expected value.");
          acknowledgement =
              new MessageAcknowledgement<>(Status.OK.getStatusCode(), Status.OK.getReasonPhrase(), created.getId());
        }
      } catch (Exception e) {
        logger
            .error("Register a schema with a subject is failed, only if the latest schema equals the expected value.");
        acknowledgement =
            new MessageAcknowledgement<>(StatusCodes.UNPROCESSABLE_ENTITY.getStatusCode(), e.getMessage(), null);
      }
    }
    return Response.ok(acknowledgement).build();
  }

  /**
   * Get a subject
   *
   * @param subject the name of the subject
   * @return a 200 response if the subject exists, or a 410 response if the subject does not.
   */
  @GET
  @Path("{subject}")
  @Produces(CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON)
  public Response checkSubject(@HeaderParam("Accept") String accept, @PathParam("subject") String subject) {
    MessageAcknowledgement<String> acknowledgement;
    if (!CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON.equalsIgnoreCase(accept)) {
      logger.error("Accept is not set correctly, Method: checkSubject, subject: {}", subject);
      acknowledgement =
          new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(), Message.ACCEPT_ERROR, null);
    } else if (StringUtils.isAnyBlank(subject)) {
      logger.error("Invalid Parameter Passed to function, Method: checkSubject, subject: {}", subject);
      acknowledgement = new MessageAcknowledgement<>(StatusCodes.INVALID_REQUEST.getStatusCode(),
          StatusCodes.INVALID_REQUEST.getReasonPhrase(), null);
    } else {
      try {
        Subject s = getSubject(subject);
        acknowledgement =
            new MessageAcknowledgement<>(StatusCodes.OK.getStatusCode(), StatusCodes.OK.getReasonPhrase(), s.getName());
        logger.info("Get the subject is successful. subject: {}", subject);
      } catch (NotFoundException e) {
        logger.warn("Get the subject is failed, {}, subject: {}", Message.SUBJECT_DOES_NOT_EXIST_ERROR, subject);
        acknowledgement =
            new MessageAcknowledgement<>(StatusCodes.GONE.getStatusCode(), Message.SUBJECT_DOES_NOT_EXIST_ERROR, null);
      }
    }
    return Response.ok(acknowledgement).build();
  }

  @GET
  @Path("{subject}/integral")
  public String getSubjectIntegralKeys(@PathParam("subject") String subject) {
    return Boolean.toString(getSubject(subject).integralKeys());
  }

  private Subject getSubject(String subjectName) {
    Subject subject = repo.lookup(subjectName);
    if (null == subject) {
      throw new NotFoundException(Message.SUBJECT_DOES_NOT_EXIST_ERROR);
    }
    return subject;
  }

  private SchemaEntry exists(SchemaEntry entry) {
    if (null == entry) {
      throw new NotFoundException(Message.SCHEMA_DOES_NOT_EXIST_ERROR);
    }
    return entry;
  }
}
