package groovy.wx.mp.pay

def user = 'oELhlt95_6--RMo3GdZGcbHezFkw'
def activity = '恭喜发财[from groovy]'
def amount = 600
def number = 5
def remark = '发给Charm'
def wishing = '恭喜发财[from 创河服务号]'
def sender = '创河软件服务号'

def map=['user':user, 'activity': activity, 'number': number, 'amount': amount, 'remark': remark, 'wishing': wishing, 'sender': sender]

wx.mp('k1YOwdUYL9X').pay.red(map)