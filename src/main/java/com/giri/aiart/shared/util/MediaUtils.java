package com.giri.aiart.shared.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/// Utility class, provides utility methods for {@link Media}
///
/// @author Giri Pottepalem
@UtilityClass
public class MediaUtils {

    /// Loads the given file based on its filename from the application's classpath and constructs a
    ///  {@link ClassPathResource} object, which points to a file within the `src/main/resources/images/` directory.
    ///
    /// @param mediaFilename The filename of the resource, including its extension.
    /// @return a {@link Resource} representing the file on the classpath.
    /// @see ClassPathResource
    public Resource toResource(String mediaFilename) {
        return new ClassPathResource("images/" + mediaFilename);
    }

    /// Given a media filename found in the classpath, it loads, constructs and returns {@link Media} object.
    ///
    /// @param mediaFilename the media filename
    /// @return a {@link Media} representing the file on the classpath
    /// @see Media
    public Media toMedia(@NonNull String mediaFilename) throws IllegalArgumentException {
        return toMedia(toResource(mediaFilename));
    }

    /// Given a {@link Resource} found in the classpath, it loads, constructs and returns {@link Media} object.
    ///
    /// @param resource the {@link Resource} object
    /// @return a {@link Media} representing the {@link Resource} object
    /// @see Media
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
