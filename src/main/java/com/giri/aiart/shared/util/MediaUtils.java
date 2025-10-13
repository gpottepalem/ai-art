package com.giri.aiart.shared.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/// Utility class
@UtilityClass
public class MediaUtils {
    ///
    public Media toMedia(@NonNull String mediaFilename) throws IllegalArgumentException {
        Resource imageResource= new ClassPathResource("images/" + mediaFilename);
        return toMedia(imageResource);
    }

    ///
    public Media toMedia(@NonNull Resource resource) throws IllegalArgumentException {
        var filename = resource.getFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Resource filename is null");
        }

        String extension = FilenameUtils.getExtension(filename);

        if (extension.isEmpty()) {
            throw new IllegalArgumentException("Resource filename must contain extension: " + filename);
        }
        MimeType mimeType = switch (extension) {
            case "jpeg", "jpg" -> MimeTypeUtils.IMAGE_JPEG;
            case "gif" -> MimeTypeUtils.IMAGE_GIF;
            case "png" -> MimeTypeUtils.IMAGE_PNG;
            default -> throw new IllegalArgumentException("Unknown extension: " + extension);
        };
        return new Media(mimeType, resource);
    }
}
