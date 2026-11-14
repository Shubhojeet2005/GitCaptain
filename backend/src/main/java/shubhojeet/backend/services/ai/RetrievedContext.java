package shubhojeet.backend.services.ai;

import shubhojeet.backend.dto.CitationDto;

import java.util.List;

public record RetrievedContext(
        List<CitationDto> citations,
        String contextText) {
}
