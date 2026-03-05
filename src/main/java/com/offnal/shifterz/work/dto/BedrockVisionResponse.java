package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.JsonNode;

public record BedrockVisionResponse(
	@Schema(
		description = "Bedrock(Claude) 응답 JSON",
		example = """
        {
          "calendars": [
            {
              "team": "1조",
              "shifts": { "1": "D", "2": "-", "3": "D" }
            }
          ],
          "unreadableCells": []
        }
        """
	)
	JsonNode bedrockResponse
) {}
