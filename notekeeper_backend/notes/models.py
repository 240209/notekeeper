from django.db import models
from django.contrib.auth.models import User
from django.utils.timezone import make_aware
from datetime import datetime

class Note(models.Model):
    PRIORITY_CHOICES = (
        (1, 'High'),
        (2, 'Medium'),
        (3, 'Low'),
    )
    CATEGORY_CHOICES = [
        ('Personal', 'Personal'),
        ('Work', 'Work'),
        ('School', 'School'),
        ('Travel', 'Travel'),
        ('Other', 'Other'),
    ]

    user = models.ForeignKey(User, on_delete=models.CASCADE)
    title = models.CharField(max_length=255)
    body = models.TextField()
    due_date = models.DateTimeField(
        default=make_aware(datetime(2000, 1, 1, 0, 0)),
        blank=True
    )
    priority = models.IntegerField(choices=PRIORITY_CHOICES, default=3)
    category= models.CharField(
        max_length=20,
        choices=CATEGORY_CHOICES,
        default='Other',
    )
    created_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now=True)

    class Meta:
        ordering = ['priority', '-modified_at', 'category']

    def __str__(self):
        return self.title