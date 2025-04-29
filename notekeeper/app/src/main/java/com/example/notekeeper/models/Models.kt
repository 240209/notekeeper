package com.example.notekeeper.models

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
data class RegisterRequest(val username: String, val email: String, val password: String)
data class GenericResponse(val message: String)
data class NoteRequest(val title: String, val body: String, val due_date: String, val priority: Int, val category: String)
data class NoteResponse(val id: Int, val title: String, val body: String, val due_date: String, val priority: Int, val category: String, val created_at: String?, val modified_at: String?)
data class UserResponse(val id: Int, val username: String, val email: String)
