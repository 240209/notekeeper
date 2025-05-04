package com.example.notekeeper.models

// Represents the login request body containing username and password
data class LoginRequest(
    val username: String, // The user's username
    val password: String  // The user's password
)

// Represents the response from a successful login, containing an authentication token
data class LoginResponse(
    val token: String // JWT or session token for authenticated requests
)

// Represents the registration request body containing user details
data class RegisterRequest(
    val username: String, // Desired username
    val email: String,    // User's email address
    val password: String  // Desired password
)

// A generic response model typically used for simple success or error messages
data class GenericResponse(
    val message: String // A human-readable response message
)

// Represents the request body to create or update a note
data class NoteRequest(
    val title: String,     // Title of the note
    val body: String,      // Main content of the note
    val due_date: String,  // Due date in ISO 8601 format (e.g., "2025-05-04T23:59:00")
    val priority: Int,     // Priority level (e.g., 1 = high, 3 = low)
    val category: String   // Category of the note (e.g., work, personal)
)

// Represents a note received from the API
data class NoteResponse(
    val id: Int,             // Unique identifier of the note
    val title: String,       // Title of the note
    val body: String,        // Main content of the note
    val due_date: String,    // Due date in ISO 8601 format
    val priority: Int,       // Priority level
    val category: String,    // Category of the note
    val created_at: String?, // Timestamp of creation (nullable)
    val modified_at: String? // Timestamp of last modification (nullable)
)

// Represents the user data returned from the API after registration or fetch
data class UserResponse(
    val id: Int,         // Unique identifier of the user
    val username: String,// Username of the user
    val email: String    // Email of the user
)
