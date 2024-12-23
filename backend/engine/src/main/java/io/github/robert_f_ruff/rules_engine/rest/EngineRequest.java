package io.github.robert_f_ruff.rules_engine.rest;

import jakarta.validation.constraints.NotEmpty;

/**
 * Defines the request structure for the EngineResource REST endpoint.
 * @author Robert F. Ruff
 * @version 1.1
 * @param accessCode The authorization key for this request
 */
public record EngineRequest(@NotEmpty String accessCode) { }
