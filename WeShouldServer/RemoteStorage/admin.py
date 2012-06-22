from django.contrib import admin
from models import Item, User, Referral, Category, Tag, Backup

class BackupAdmin(admin.ModelAdmin):
	pass

class ItemAdmin(admin.ModelAdmin):
	pass

class UserAdmin(admin.ModelAdmin):
	list_display = ('email',)

class ReferralAdmin(admin.ModelAdmin):
	pass

admin.site.register(Referral, ReferralAdmin)
admin.site.register(Item, ItemAdmin)
admin.site.register(User, UserAdmin)
admin.site.register(Backup, BackupAdmin)
