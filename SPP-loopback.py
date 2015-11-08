#!/usr/bin/python

from __future__ import absolute_import, print_function, unicode_literals

from optparse import OptionParser, make_option
import os
import sys
import socket
import uuid
import dbus
import dbus.service
import dbus.mainloop.glib
import mraa
import time

try:
	from gi.repository import GObject
except ImportError:
	import gobject as GObject


class Profile(dbus.service.Object):
	fd = -1

	@dbus.service.method("org.bluez.Profile1",
						 in_signature="", out_signature="")
	def Release(self):
		print("Release")
		mainloop.quit()

	@dbus.service.method("org.bluez.Profile1",
						 in_signature="", out_signature="")
	def Cancel(self):
		print("Cancel")

	@dbus.service.method("org.bluez.Profile1",
						 in_signature="oha{sv}", out_signature="")
	def NewConnection(self, path, fd, properties):
		self.fd = fd.take()
		print("NewConnection(%s, %d)" % (path, self.fd))

		server_sock = socket.fromfd(self.fd, socket.AF_UNIX, socket.SOCK_STREAM)
		server_sock.setblocking(1)
		#server_sock.send("This is Edison SPP loopback test\nAll data will be loopback\nPlease start:\n")

		try:
			while True:
				data = server_sock.recv(1024)
				print("received: %s" % data)
				saved_data = data
				while "start" == saved_data:
					server_sock.send(getSensorData())
					time.sleep(5)
		except IOError:
			pass

		server_sock.close()
		print("all done")


	@dbus.service.method("org.bluez.Profile1",
						 in_signature="o", out_signature="")
	def RequestDisconnection(self, path):
		print("RequestDisconnection(%s)" % (path))

		if (self.fd > 0):
			os.close(self.fd)
			self.fd = -1


def getSensorData():
	# temp,humi,light,uv,pir,ms

	#temp/humi is on I2C
	temp = mraa.I2c(0)
	temp.address(0x40)
	#getting temperature
	temp.writeReg(0x03, 0x11)
	temp_value = temp.readWordReg(3)
	temp_value = temp_value >> 2
	temp_value = (temp_value / 32.0) - 50.0
	#should be around temp=	20.0

	humi = mraa.I2c(0)
	humi.address(0x40)
	humi.writeReg(0x03, 0x01)
	humi_value = humi.readWordReg(3)
	humi_value = humi_value >> 4
	humi_value = (humi_value / 16.0) - 24.0
	#should be around humi=60.0

	light = mraa.Aio(2)  #light is A2
	ligh_value = light.read()
	uv = mraa.Aio(3)  #UV is A3
	uv_value = uv.read()
	pir = mraa.Gpio(7)  #PIR motion sensor is D7
	pir_value = pir.read()
	ms = mraa.Aio(1)  #moisture is A1
	ms_value = ms.read()

	return "%f,%f,%f,%f,%f,%f" % (temp_value, humi_value, ligh_value, uv_value, pir_value, ms_value)


if __name__ == '__main__':
	dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

	bus = dbus.SystemBus()

	manager = dbus.Interface(bus.get_object("org.bluez",
											"/org/bluez"), "org.bluez.ProfileManager1")

	option_list = [
		make_option("-C", "--channel", action="store",
					type="int", dest="channel",
					default=None),
	]

	parser = OptionParser(option_list=option_list)

	(options, args) = parser.parse_args()

	options.uuid = "1101"
	options.psm = "3"
	options.role = "server"
	options.name = "Edison SPP Loopback"
	options.service = "spp char loopback"
	options.path = "/foo/bar/profile"
	options.auto_connect = False
	options.record = ""

	profile = Profile(bus, options.path)

	mainloop = GObject.MainLoop()

	opts = {
		"AutoConnect": options.auto_connect,
	}

	if (options.name):
		opts["Name"] = options.name

	if (options.role):
		opts["Role"] = options.role

	if (options.psm is not None):
		opts["PSM"] = dbus.UInt16(options.psm)

	if (options.channel is not None):
		opts["Channel"] = dbus.UInt16(options.channel)

	if (options.record):
		opts["ServiceRecord"] = options.record

	if (options.service):
		opts["Service"] = options.service

	if not options.uuid:
		options.uuid = str(uuid.uuid4())

	manager.RegisterProfile(options.path, options.uuid, opts)

	mainloop.run()


