# Create your views here.
from RemoteStorage.models import Item, Tag, Category, User, Referral, Backup
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse
import sys
import json
from django.core import serializers

def backup(request):
	try:
		user = User.objects.get_or_create(email=request.GET.get("user_email", ""))[0]
		backup = Backup.objects.filter(user__email = request.GET.get("user_email", ""))
		if len(backup):
			backup = backup[0]
		else:
			backup = Backup()
			backup.user = user
		print >> sys.stderr, backup
		#backup.user = user
		if request.GET.get("append", "true") == "false":
			backup.data = request.GET.get("data", "")
		else:
			backup.data = backup.data+request.GET.get("data", "")
		backup.save()
	except Exception as e:
		print >> sys.stderr, e
	return HttpResponse()

def restore(request):
	info = {}
	print >> sys.stderr, "restore view"
	try:
		user = User.objects.get_or_create(email=request.GET.get("user_email", ""))[0]
		index = int(request.GET.get("index", 0))
		backupData = Backup.objects.get_or_create(user = user)[0].data
		strToSend = backupData[index*4096:index*4096+4096] if backupData is not None else ""
		print >> sys.stderr, "restore string"+strToSend+"foo"
		info['data'] = strToSend
		info['done'] = "true" if len(strToSend)<4096 else "false"
	except Exception as e:
		info['done'] = "true"
		print >> sys.stderr, e.message
	return HttpResponse(content = json.dumps(info))


def save_item(request):
	try:

		print >> sys.stderr, request.GET
#		print request.user
		if request.GET.get("type", '') == "item":
			i = Item()
			i.user = User.objects.get_or_create(email=request.GET['user_email'])[0]
			i.tags = request.GET.get('tags') #translate this to a list
			i.category = request.GET.get('category')
			i.data = request.GET.get('data')
			i.save()
		elif request.GET.get("type", '') == "category":
			c = Category()
			c.name = ""
			c.data = ""
			c.save()
		elif request.GET.get("type", '') == "tag":
			t = Tag()
			t.name = ""
			t.color = ""
			t.data = ""
			t.save()
#		print i
	except Exception as e:
		print >> sys.stderr, e.message

	return HttpResponse()

def check_referrals(request):
	try:
		print >> sys.stderr, request.GET
		user = User.objects.get_or_create(email = request.GET.get("user_email", ""))[0]
		referrals = Referral.objects.filter(referred_to = user)
		print >> sys.stderr, referrals
		info = {'referrals':[]}
		for referral in referrals:
			ref = {}
			ref['data'] = referral.data.encode('utf8')
			ref['referred_by'] = referral.referred_by.email.encode('utf8')
			ref['item_name'] = referral.name.encode('utf8')
			info['referrals'].append(ref)
		print >> sys.stderr, "referral info"
#		print >> sys.stderr, json.dumps(info)
		print >> sys.stderr, str(info)
		return HttpResponse(content = str(info))
#		return HttpResponse()
	except Exception as e:
		print >> sys.stderr, e.message

def refer(request):
	try:
		print >> sys.stderr, request.GET
		user = User.objects.get_or_create(email = request.GET.get("user_email", ""))[0]
		refer_to = request.GET.get("email_list", "")
		refer_to_list = refer_to.split(",")
	
		for rt in refer_to_list:
			rt = rt.strip()
			r = Referral()	
			r.data = request.GET.get("item_data", "")
			r.referred_by = user
			r.name = request.GET.get("item_name", "")
			r.referred_to = User.objects.get_or_create(email = rt)[0]
			r.save()
	except Exception as e:
		print >> sys.stderr, e.message
	return HttpResponse()


def delete_referrals(request):
	try:
		delete_list = json.loads(request.GET.get("delete_list", {}))
		user_email = request.GET.get("user_email", "")
		print >> sys.stderr, delete_list
		for ref in delete_list:
			print >> sys.stderr, ref
			r = Referral.objects.filter(referred_to__email = user_email, name = ref['name'], referred_by__email=ref['referred_by']).delete()
			#if len(r):
				#r = r[0]
			#	r.delete()
	except Exception as e:
		return HttpResponse(content = e.message)
	return HttpResponse()
