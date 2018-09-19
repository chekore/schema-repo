package org.schemarepo.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * @author zhizhou.ren
 * @param <E>
 */
public class MessageAcknowledgement<E> {
  private int code;
  private String message;
  private E payload;

  public MessageAcknowledgement() {
  }

  public MessageAcknowledgement(int code, String message, E payload) {
    this.code = code;
    this.message = message;
    this.setPayload(payload);
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public E getPayload() {
    return payload;
  }

  public void setPayload(E payload) {
    this.payload = payload;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
