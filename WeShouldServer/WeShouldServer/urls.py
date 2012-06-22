from django.conf.urls import patterns, include, url
from RemoteStorage.views import refer, restore
from RemoteStorage.views import check_referrals
from RemoteStorage.views import backup, delete_referrals


# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'WeShouldServer.views.home', name='home'),
    # url(r'^WeShouldServer/', include('WeShouldServer.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
    url(r'^backup/', backup),
    url(r'^restore/', restore),
    url(r'^check-referrals/', check_referrals),
    url(r'^refer/', refer),
    url(r'^delete-referrals/', delete_referrals),
)
