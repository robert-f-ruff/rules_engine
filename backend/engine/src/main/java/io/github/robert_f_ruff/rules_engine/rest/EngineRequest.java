package io.github.robert_f_ruff.rules_engine.rest;

import java.util.Objects;

import jakarta.validation.constraints.NotEmpty;

/**
 * Defines the request structure for the EngineResource REST endpoint.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class EngineRequest {
  @NotEmpty
  private String accessCode;

  /**
   * Returns the authorization key.
   * @return Authorization key
   * @since 1.0
   */
  public String getAccessCode() {
    return accessCode;
  }

  /**
   * Populates the authorization key.
   * @param accessCode Authorization key
   * @since 1.0
   */
  public void setAccessCode(String accessCode) {
    this.accessCode = accessCode;
  }

  /**
   * New instance of EngineRequest.
   * @param accessCode The authorization key for this request
   * @since 1.0
   */
  public EngineRequest(String accessCode) {
    this.accessCode = accessCode;
  }

  /**
   * New intance of EngineRequest.
   * @since 1.0
   */
  public EngineRequest() {
    accessCode = "";
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * @param o The object instance to compare to this instance
   * @return Whether the comparison object instance is equal to this instance
   * @since 1.0
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EngineRequest engineRequest = (EngineRequest)o;
    return Objects.equals(accessCode, engineRequest.accessCode);
  }

  /**
   * Returns a hash code value for the object.
   * @return Hash code value for this object instance
   * @since 1.0
   */
  @Override
  public int hashCode() {
    return Objects.hash(accessCode);
  }
}
