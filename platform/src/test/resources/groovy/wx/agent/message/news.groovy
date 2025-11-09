package wx.script.agent.message

def user = 'borball'

def title1 = '[发自Groovy] iTerm:让你的命令行也能丰富多彩'
def desc1 = 'iTerm:让你的命令行也能丰富多彩'
def url1 = 'http://swiftcafe.io/2015/07/25/iterm'
def picUrl1 = 'http://swiftcafe.io/images/iterm/1.png'

def map1 = ['title': title1, 'desc': desc1, 'url': url1, 'picUrl': picUrl1];

def title2 = '[发自Groovy] GitHub 漫游指南'
def desc2 = 'GitHub 漫游指南'
def url2 = 'https://github.com/phodal/github-roam'
def picUrl2 = 'http://7rf34y.com1.z0.glb.clouddn.com/user/7ec9b7dc0f494919b68d6f6be9504790/thumb'

def map2 = ['title': title2, 'desc': desc2, 'url': url2, 'picUrl': picUrl2];

def title3 = '[发自Groovy] "疯狂 HTML + CSS + JS 中 CSS 总结'
def desc3 = '疯狂 HTML + CSS + JS 中 CSS 总结'
def url3 = 'http://mzkmzk.github.io/blog/2015/10/18/amazeing-css.markdwon'
def picUrl3 = 'http://extjs.org.cn/screen_capture/extjswebbook/crazy-ajax-03.jpg'

def map3 = ['title': title3, 'desc': desc3, 'url': url3, 'picUrl': picUrl3];

def title4 = 'Facebook CEO 扎克伯格用中文讲了三个故事'
def desc4 = 'Facebook CEO 扎克伯格用中文讲了三个故事'
def url4 = 'http://www.cyzone.cn/a/20151024/282339.html'
def picUrl4 = 'http://img0.pconline.com.cn/pconline/1410/23/5615376_03_thumb.jpg'

def map4 = ['title': title4, 'desc': desc4, 'url': url4, 'picUrl': picUrl4];

def list = [map1, map2, map3, map4]
def news = ['user': user, 'news': list]

wx.agent.news(news)
