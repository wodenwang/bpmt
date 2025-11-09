package groovy.wx.agent.pay

def user = 'borball'
def activity = '恭喜发财[from groovy]'
def amount = 100
def remark = '发给borball'
def wishing = '恭喜发财[from agent]'
def sender = '创河软件企业号'

def map=['user':user, 'activity': activity, 'amount': amount, 'remark': remark, 'wishing': wishing, 'sender': sender]

wx.agent.pay.red(map)