from django.db import models
from django.contrib.auth.models import User

class Note(models.Model):
    PRIORITY_CHOICES = (
        ('High', 'High'),
        ('Medium', 'Medium'),
        ('Low', 'Low'),
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
    due_date = models.DateTimeField(default="2000-01-01 00:00", blank=True)
    priority = models.CharField(max_length=10, choices=PRIORITY_CHOICES, default='Low')
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