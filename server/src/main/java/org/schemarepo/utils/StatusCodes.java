package org.schemarepo.utils;

import javax.ws.rs.core.Response;


/**
 * @author zhizhou.ren
 */
public enum StatusCodes implements Response.StatusType {
  /**
   * <li>200 OK - [GET]：服务器成功返回用户请求的数据，该操作是幂等的（Idempotent）</li>
   * <li>201 CREATED - [POST/PUT/PATCH]：用户新建或修改数据成功</li>
   * <li>202 Accepted - [*]：表示一个请求已经进入后台排队（异步任务）</li>
   * <li>204 NO CONTENT - [DELETE]：用户删除数据成功</li>
   * <li>400 INVALID REQUEST - [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的</li>
   * <li>401 Unauthorized - [*]：表示用户没有权限（令牌、用户名、密码错误）</li>
   * <li>403 Forbidden - [*]：表示用户得到授权（与401错误相对），但是访问是被禁止的</li>
   * <li>404 NOT FOUND - [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的</li>
   * <li>406 Not Acceptable - [GET]：用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）</li>
   * <li>409 Conflict - [*]: 在响应中包含有冲突的信息</li>
   * <li>410 Gone - [GET]：用户请求的资源被永久删除，且不会再得到的</li>
   * <li>422 Unprocessable entity - [POST/PUT/PATCH]：当创建一个对象时，发生一个验证错误</li>
   * <li>500 INTERNAL SERVER ERROR - [*]：服务器发生错误，用户将无法判断发出的请求是否成功</li>
   */
  OK(200, "OK"),
  CREATED(201, "Created"),
  ACCEPTED(202, "Accepted"),
  NO_CONTENT(204, "No Content"),
  MOVED_PERMANENTLY(301, "Moved Permanently"),
  FOUND(302, "Found"),
  INVALID_REQUEST(400, "Invalid Request"),
  UNAUTHORIZED(401, "Unauthorized"),
  FORBIDDEN(403, "Forbidden"),
  NOT_FOUND(404, "Not Found"),
  NOT_ACCEPTABLE(406, "Not Acceptable"),
  CONFLICT(409, "Conflict"),
  GONE(410, "Gone"),
  UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
  INTERNAL_SERVER_ERROR(500, "Internal Server Error");

  private final int code;
  private String reason;
  private Response.Status.Family family;

  StatusCodes(int code) {
    this.code = code;
  }

  StatusCodes(final int statusCode, final String reasonPhrase) {
    this.code = statusCode;
    this.reason = reasonPhrase;
    switch (code / 100) {
      case 1:
        this.family = Response.Status.Family.INFORMATIONAL;
        break;
      case 2:
        this.family = Response.Status.Family.SUCCESSFUL;
        break;
      case 3:
        this.family = Response.Status.Family.REDIRECTION;
        break;
      case 4:
        this.family = Response.Status.Family.CLIENT_ERROR;
        break;
      case 5:
        this.family = Response.Status.Family.SERVER_ERROR;
        break;
      default:
        this.family = Response.Status.Family.OTHER;
        break;
    }
  }

  /**
   * Convert a numerical status code into the corresponding Status
   * @param statusCode the numerical status code
   * @return the matching Status or null is no matching Status is defined
   */
  public static StatusCodes fromStatusCode(final int statusCode) {
    for (StatusCodes s : StatusCodes.values()) {
      if (s.code == statusCode) {
        return s;
      }
    }
    return null;
  }

  @Override
  public int getStatusCode() {
    return code;
  }

  @Override
  public Response.Status.Family getFamily() {
    return family;
  }

  @Override
  public String getReasonPhrase() {
    return toString();
  }

  /**
   * Get the reason phrase
   * @return the reason phrase
   */
  @Override
  public String toString() {
    return reason;
  }
}
