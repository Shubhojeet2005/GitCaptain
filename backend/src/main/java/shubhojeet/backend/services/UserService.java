
package shubhojeet.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shubhojeet.backend.entity.User;
import shubhojeet.backend.repository.UserRepository;

import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TextEncryptor textEncryptor;

    @Transactional(readOnly = true)
    public User getUserByGithubId(Long githubId) {
        return userRepository.findByGithubId(githubId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with GitHub ID: " + githubId
                        )
                );
    }

    @Transactional(readOnly = true)
    public User requiredById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with ID: " + id
                        )
                );
    }

    public String decryptAccessToken(User user) {
        if (user == null || user.getAccessToken() == null) {
            throw new IllegalArgumentException(
                    "User or access token cannot be null"
            );
        }

        return textEncryptor.decrypt(user.getAccessToken());
    }

    @Transactional
    public User upsertFromGitHub(Map<String, Object> attributes, String accessToken, String scopes) {
        Long githubId = Long.valueOf(String.valueOf(attributes.get("id")));
        String login = String.valueOf(attributes.get("login"));
        String name = attributes.get("name") != null
                ? String.valueOf(attributes.get("name"))
                : login;
        String avatarUrl = attributes.get("avatar_url") != null
                ? String.valueOf(attributes.get("avatar_url"))
                : null;

        String encryptedToken = textEncryptor.encrypt(accessToken);

        User user = userRepository.findByGithubId(githubId).orElseGet(User::new);
        user.setGithubId(githubId);
        user.setGithubUsername(login);
        user.setDisplayName(name);
        user.setAvatarUrl(avatarUrl);
        user.setAccessToken(encryptedToken);
        user.setTokenScopes(scopes);
        return userRepository.save(user);
    }
}
