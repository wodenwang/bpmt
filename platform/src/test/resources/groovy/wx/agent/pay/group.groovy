package groovy.wx.agent.pay

def user = 'borball'
def activity = '恭喜发财[from groovy]'
def amount = 600
def number = 5
def remark = '发给borball'
def wishing = '恭喜发财[from agent]'
def sender = '创河软件企业号'

def map=['user':user, 'activity': activity, 'number': number, 'amount': amount, 'remark': remark, 'wishing': wishing, 'sender': sender]

wx.agent.pay.red(map)