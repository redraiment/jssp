<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta name="renderer" content="webkit" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>Hello JSSP</title>
    <link rel="stylesheet" href="styles/index.css.js" />
  </head>
  <body>
    <h1>Hello JVM Scripting Server Pages</h1>
    <dl>
      [! request.each { key, value -> !]
      <dt>[= escape(key) =]</dt>
      <dd>[= escape(value) =]</dd>
      [! } !]
    </dl>
  </body>
</html>
