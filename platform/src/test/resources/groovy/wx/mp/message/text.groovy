package groovy.wx.mp.message

def text = '这是一段由groovy发出的消息'
def map=['text': text]

wx.mp('k1YOwdUYL9X').text(map);