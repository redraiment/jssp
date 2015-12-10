# JavaScript Server

纯Java实现的一个轻量的Web服务器，可在服务器端运行JavaScript代码。功能类似`php -S localhost:8000`，即以当前目录为根目录开启`Web`服务，只不过服务器端运行的是`JavaScript`而不是`PHP`。

脚本文件采用两个扩展名命名，例如`index.html.js`表示这是一个在服务器端运行的脚本，采用`js`引擎，生成`html`代码；同理，`index.css.bsh`表示这个脚本采用`BeanShell`引擎，生成`css`代码。

`JSS`的定位是一个模板引擎，提供类似`PHP`、`JSP`的功能，但语法有点不同：

1. `[! !]`：插入脚本。例如`[! response.print('<h1>Hello world</h1>'); !]`。
2. `[= =]`：插入表达式。例如`[= 1 + 2 =]`。
