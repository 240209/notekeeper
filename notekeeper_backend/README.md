# Multi-User Note-Taking Application

This is a full-stack note-taking application consisting of a **Django REST API** backend and an **Android** frontend. The goal of this project is to provide a user-friendly mobile experience for managing personal notes with authentication, categorization, sorting, and reminder features.

## 📱 Features

### ✅ Implemented

- **User Authentication**
  - Register and log in using email and password
  - Token-based authentication via Django REST Framework

- **Notes Management**
  - Create, read, update, and delete (CRUD) operations
  - Each note includes:
    - Title
    - Body
    - Due date
    - Priority
    - Category

- **Sorting & Filtering**
  - Sorting by:
    - **Modification date**
    - **Category**
  - Sorting logic implemented in the backend

- **Frontend**
  - Android app with:
    - Login and registration screens
    - Notes display and management
    - Seamless integration with Django backend

### 🔧 In Progress / Not Yet Implemented

- **Push Notifications**
  - To be triggered based on due dates

- **Password Reset**
  - Email-based password reset functionality

- **Admin Panel**
  - Backend interface for managing users

- **Sorting Options in UI**
  - Sorting/filtering UI in Android app not yet fully implemented

---

## ⏱ Time Estimates & Progress

| Task | Status |
|------|--------|
| Backend API & Auth | ✅ Completed |
| Android UI & Integration | ✅ Completed |
| Sorting, Priority, Notifications | ⚠️ Partially Done (backend only) |
| Password Reset & Admin Panel | ❌ Not Started |

---