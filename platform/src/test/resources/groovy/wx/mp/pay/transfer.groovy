package groovy.wx.mp.pay

def user = 'oELhlt95_6--RMo3GdZGcbHezFkw'
def userCheck = 'FORCE_CHECK'
def userName = '刘畅'
def amount = 100
def desc = '发给Charm'

def map=['user':user, 'userCheck': userCheck, 'amount': amount, 'userName': userName, 'desc': desc]

wx.mp('8J38wz1oM9X').pay.transfer(map)