package com.example.final_project.data.repository

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.example.final_project.BuildConfig
import kotlin.time.Duration.Companion.seconds
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor() {

    private val openAI = OpenAI(
        OpenAIConfig(
            token = BuildConfig.DEEPSEEK_API_KEY,
            timeout = Timeout(socket = 60.seconds),
            host = OpenAIHost(baseUrl = "https://api.deepseek.com/v1")
        )
    )

    suspend fun getChatCompletion(
        userMessage: String,
        taskContext: String
    ): Result<String> {
        return try {
            val systemPrompt = """
                You are a helpful to-do list assistant.
                The user's current tasks are provided below in a simplified format.
                Use this information to answer the user's questions.
                Be friendly and concise.

                Here are the user's tasks:
                $taskContext
            """.trimIndent()

            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("deepseek-chat"), // Or "gpt-3.5-turbo"
                messages = listOf(
                    ChatMessage.System(systemPrompt),
                    ChatMessage.User(userMessage)
                )
            )

            val completion = openAI.chatCompletion(chatCompletionRequest)
            val response = completion.choices.first().message.content
                ?: "Sorry, I couldn't generate a response."

            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
