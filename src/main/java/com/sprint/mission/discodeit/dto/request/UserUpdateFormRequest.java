package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.validator.NoBlankIfPresent;
import com.sprint.mission.discodeit.validator.RequiredMultipartFileIfPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Getter
public class UserUpdateFormRequest {
    @NoBlankIfPresent
    private String newUsername;
    @NoBlankIfPresent
    private String newEmail;
    @NoBlankIfPresent
    private String newPassword;

    @RequiredMultipartFileIfPresent
    private MultipartFile newProfile;
}
