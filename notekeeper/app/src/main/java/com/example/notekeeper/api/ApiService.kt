package com.example.notekeeper.api

import com.example.notekeeper.models.GenericResponse
import com.example.notekeeper.models.LoginRequest
import com.example.notekeeper.models.LoginResponse
import com.example.notekeeper.models.NoteRequest
import com.example.notekeeper.models.NoteResponse
import com.example.notekeeper.models.RegisterRequest
import com.example.notekeeper.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/register/")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest
    ): Response<UserResponse>

    @POST("api/auth/login/")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/logout/")
    suspend fun logoutUser(
        @Header("Authorization") token: String
    ): Response<GenericResponse>

    @GET("api/notes/")
    suspend fun getNotes(
        @Header("Authorization") token: String
    ): Response<List<NoteResponse>>

    @POST("api/notes/")
    suspend fun createNote(
        @Header("Authorization") token: String,
        @Body noteRequest: NoteRequest
    ): Response<NoteResponse>

    // @GET("api/notes/{id}/")

    @PUT("api/notes/{id}/")
    suspend fun updateNote(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body noteRequest: NoteRequest
    ): Response<NoteResponse>

    @DELETE("api/notes/{id}/")
    suspend fun deleteNote(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<GenericResponse>
}
