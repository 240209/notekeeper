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

// Interface defining the REST API endpoints for the application using Retrofit
interface ApiService {

    // Registers a new user with the given registration data
    @POST("api/auth/register/")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest // Request body containing registration details
    ): Response<UserResponse> // Returns a response containing user information

    // Logs in a user with the given credentials
    @POST("api/auth/login/")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest // Request body containing login credentials
    ): Response<LoginResponse> // Returns a response with login data (e.g., token)

    // Logs out the currently authenticated user
    @POST("api/auth/logout/")
    suspend fun logoutUser(
        @Header("Authorization") token: String // Authorization token in the request header
    ): Response<GenericResponse> // Returns a generic success/failure response

    // Retrieves a list of notes for the authenticated user
    @GET("api/notes/")
    suspend fun getNotes(
        @Header("Authorization") token: String // Authorization token in the request header
    ): Response<List<NoteResponse>> // Returns a list of note objects

    // Creates a new note for the authenticated user
    @POST("api/notes/")
    suspend fun createNote(
        @Header("Authorization") token: String, // Authorization token in the request header
        @Body noteRequest: NoteRequest // Request body containing new note data
    ): Response<NoteResponse> // Returns the created note object

    // Retrieves a specific note by its ID
    @GET("api/notes/{id}/")
    suspend fun getNoteById(
        @Header("Authorization") token: String, // Authorization token in the request header
        @Path("id") id: Int // ID of the note to retrieve
    ): Response<NoteResponse> // Returns the requested note object

    // Updates an existing note by its ID
    @PUT("api/notes/{id}/")
    suspend fun updateNote(
        @Header("Authorization") token: String, // Authorization token in the request header
        @Path("id") id: Int, // ID of the note to update
        @Body noteRequest: NoteResponse // Request body containing updated note data
    ): Response<NoteResponse> // Returns the updated note object

    // Deletes a note by its ID
    @DELETE("api/notes/{id}/")
    suspend fun deleteNote(
        @Header("Authorization") token: String, // Authorization token in the request header
        @Path("id") id: Int // ID of the note to delete
    ): Response<GenericResponse> // Returns a generic success/failure response
}
