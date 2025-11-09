package wx.script.agent.message

def user = 'borball'
def text = '这是一段由groovy发出的消息'

def map=['user':user, 'text': text, 'safe': true]

wx.agent.text(map)