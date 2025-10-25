package com.giri.aiart.shared.util;

import org.springframework.ai.chat.client.ChatClient;

/// Wrapper that associates a ChatClient instance with its model name.
/// Used to make model metadata available for logging and diagnostics, which is otherwise not possible to find from
/// {@link ChatClient}.
///
/// @see com.giri.aiart.config.ChatClientConfiguration
///
/// @author Giri Pottepalem
public record ChatClientWithMeta(ChatClient chatClient, String modelName) { }
