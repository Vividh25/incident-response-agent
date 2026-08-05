package com.vividh.incident.agent.llm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

/**
 * A single block in an assistant message's content array. Doubles as both the
 * parsed response shape and the shape to echo back in the next request's
 * message history — only the fields matching {@code type} are populated.
 * {@code @JsonInclude(NON_NULL)} is required here: Anthropic validates each
 * block strictly against its type's schema, so a "text" block carrying
 * {@code id/name/input: null} (or a "tool_use" block carrying {@code text:
 * null}) is rejected with "Extra inputs are not permitted" — the unset
 * fields must be omitted from the JSON entirely, not just set to null.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Content {
    String type;

    // present when type == "text"
    String text;

    // present when type == "tool_use" — id is what a follow-up tool_result must reference
    String id;
    String name;
    Map<String, Object> input;
}
