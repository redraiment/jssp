<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta name="renderer" content="webkit" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>Hello JSS</title>
    <link rel="stylesheet" href="styles/index.css.js" />
  </head>
  <body>
    <h1>[= java.lang.String.format('%s %s%s', request.getMethod(), request.getPath(), request.getQuery()) =]</h1>
    <dl>
      <dt>JSS</dt>
      <dd>JavaScript Server</dd>
      <dt>JSSP</dt>
      <dd>JavaScript Server Page</dd>
    </dl>
    [! for (var i = 0; i < 10; i++) { !]
    <p>line [= i =]</p>
    [! } !]
  </body>
</html>
