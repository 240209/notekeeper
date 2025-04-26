from django.urls import path
from .views import RegisterView, CustomAuthToken, PasswordResetRequest, PasswordResetConfirm

urlpatterns = [
    path('register/', RegisterView.as_view(), name='register'),
    path('login/', CustomAuthToken.as_view(), name='login'),
    path('password-reset/', PasswordResetRequest.as_view(), name='password-reset-request'),
    path('password-reset-confirm/<int:uid>/<str:token>/', PasswordResetConfirm.as_view(), name='password-reset-confirm'),
]