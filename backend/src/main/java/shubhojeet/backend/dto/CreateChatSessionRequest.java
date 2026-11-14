package shubhojeet.backend.dto;

import java.util.UUID;

public record CreateChatSessionRequest(
        UUID repositoryId,
        String title) {
}
