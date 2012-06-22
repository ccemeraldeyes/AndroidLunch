from django.db import models

# Create your models here.

class Category(models.Model):
	name = models.CharField(max_length = 256)
	color = models.CharField(max_length = 256, null=True, blank=True)

class Tag(models.Model):
	name = models.CharField(max_length = 256)

class User(models.Model):
	email = models.EmailField()

class Item(models.Model):
	category = models.ForeignKey(Category, null=True, blank=True)
	tags = models.ManyToManyField(Tag, null=True, blank=True)
#	address = models.CharField(max_length = 512, null=True, blank=True)
	user = models.ForeignKey(User)
	data = models.CharField(max_length = 1024, null=True, blank=True)

class Referral(models.Model):
#	item = models.ForeignKey(Item)
	referred_to = models.ForeignKey(User, related_name="referred_to")
	data = models.CharField(max_length = 2048)
	referred_by = models.ForeignKey(User, related_name="referred_by")
	name = models.CharField(max_length = 512)

class Backup(models.Model):
	user = models.ForeignKey(User)
	data = models.TextField(null=True, blank=True)
