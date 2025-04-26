from rest_framework import generics
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from rest_framework.response import Response
from django.contrib.auth.models import User
from rest_framework.views import APIView
from django.core.mail import send_mail
from django.contrib.auth.tokens import default_token_generator
from django.urls import reverse

from .serializers import RegisterSerializer

class RegisterView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = RegisterSerializer

class CustomAuthToken(ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        response = super().post(request, *args, **kwargs)
        token = Token.objects.get(key=response.data['token'])
        return Response({'token': token.key, 'user_id': token.user_id})

class PasswordResetRequest(APIView):
    def post(self, request):
        email = request.data.get('email')
        user = User.objects.filter(email=email).first()
        if user:
            token = default_token_generator.make_token(user)
            reset_link = request.build_absolute_uri(reverse('password-reset-confirm', args=[user.pk, token]))
            send_mail('Password Reset', f'Click here to reset your password: {reset_link}', 'noreply@example.com', [email])
        return Response({'message': 'If the email is registered, a reset link has been sent.'})

class PasswordResetConfirm(APIView):
    def post(self, request, uid, token):
        password = request.data.get('password')
        user = User.objects.get(pk=uid)
        if default_token_generator.check_token(user, token):
            user.set_password(password)
            user.save()
            return Response({'message': 'Password reset successful.'})
        else:
            return Response({'message': 'Invalid reset link.'}, status=400)