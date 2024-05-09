package io.github.robert_f_ruff.rules_engine.rest;

import java.util.Objects;

import io.github.robert_f_ruff.rules_engine.Engine;

/**
 * Defines the response structure for the DataResource and EngineResource REST endpoints.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class EngineResponse {
  private String status;

  /**
   * Returns the status.
   * @return Current status
   * @since 1.0
   */
  public String getStatus() {
    return status;
  }

  /**
   * New instance of EngineStatus
   * @param status The status to store
   * @since 1.0
   */
  public EngineResponse(Engine.Status status) {
    this.status = status.name();
  }

  /**
   * New instance of EngineStatus
   * @param status The status to store
   * @since 1.0
   */
  public EngineResponse(EngineResource.Status status) {
    this.status = status.name();
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
    EngineResponse engineStatus = (EngineResponse)o;
    return Objects.equals(status, engineStatus.status);
  }

  /**
   * Returns a hash code value for the object.
   * @return Hash code value for this object instance
   * @since 1.0
   */
  @Override
  public int hashCode() {
    return Objects.hash(status);
  }
}
