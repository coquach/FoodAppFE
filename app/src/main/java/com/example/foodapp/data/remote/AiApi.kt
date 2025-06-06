package com.example.foodapp.data.remote

import com.example.foodapp.data.dto.request.ChatKnowledgeEntryRequest
import com.example.foodapp.data.dto.request.ChatMessageRequest
import com.example.foodapp.data.dto.request.IntentTypeRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.ChatKnowledgeEntry
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.IntentType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AiApi {
    //ChatKnowledgeEntryController
    @GET("chat-knowledge-entrys")
    suspend fun getChatKnowledgeEntry(): Response<List<ChatKnowledgeEntry>>

    @POST("chat-knowledge-entrys")
    suspend fun createChatKnowledgeEntry(@Body request: ChatKnowledgeEntryRequest): Response<ChatKnowledgeEntry>

    @PUT("chat-knowledge-entrys/{id}")
    suspend fun updateChatKnowledgeEntry(
        @Path("id") id: Long,
        @Body request: ChatKnowledgeEntryRequest
    ): Response<ChatKnowledgeEntry>

    @DELETE("chat-knowledge-entrys/{id}")
    suspend fun deleteChatKnowledgeEntry(@Path("id") id: Long): Response<Unit>


    //ChatMessagesController
    @GET("chat-messages")
    suspend fun getChatMessage(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("order") order: String = "desc",
    ): Response<PageResponse<ChatMessage>>


    //IntentTypeController
    @GET("intent-types")
    suspend fun getIntentTypes(): Response<List<IntentType>>

    @POST("intent-types")
    suspend fun createIntentType(@Body request: IntentTypeRequest): Response<IntentType>

    @PUT("intent-types/{id}")
    suspend fun updateIntentType(
        @Path("id") id: Long,
        @Body request: IntentTypeRequest
    ): Response<IntentType>

    @DELETE("intent-types/{id}")
    suspend fun deleteIntentType(@Path("id") id: Long): Response<Unit>


    //MistralAIController
    @GET("mistral-ai/suggest-foods")
    suspend fun suggestFoodsForCurrentUser(): Response<List<Food>>

    @POST("mistral-ai/chat")
    suspend fun chatWithMistralAi(@Body request: ChatMessageRequest): Response<ChatMessage>





}