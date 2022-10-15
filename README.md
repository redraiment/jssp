# JVM Scripting Server Pages

[![Clojure CI](https://github.com/redraiment/jssp/actions/workflows/clojure.yml/badge.svg)](https://github.com/redraiment/jssp/actions/workflows/clojure.yml)

JVM Scripting Server page (also shortened as JSSP) is a polyglot templating system that embeds JVM scripting language into a text document, similar to JSP, PHP, ASP, and other server-side scripting languages.

# Getting Started with Docker

```sh
docker run --rm redraiment/jssp
```

# Tutorial

## Basic Example

Here is a basic example of JSSP.

```sh
docker run --rm -v $PWD:/jssp redraiment/jssp \
  examples/local-mode/languages.md.groovy
```

*Template*: examples/local-mode/languages.md.groovy

```groovy
# Languages

[! ["JavaScript", "Groovy", "JRuby", "BeanShell"].each { !]
* [= it =]
[! } !]
```

*Output*

```markdown
# Languages

* JavaScript
* Groovy
* JRuby
* BeanShell
```

## Multi-Language Support

JSSP supports the following languages:

* JavaScript
* Groovy
* BeanShell (Java)
* JRuby (Ruby)

*JavaScript Template*: examples/local-mode/languages.md.js

```js
[! var languages = ['JavaScript', 'Groovy', 'JRuby', 'BeanShell'] !]
# Languages

[! for (var index = 0; index < languages.length; index++) { !]
* [= languages[index] =]
[! } !]
```

*Groovy Template*: examples/local-mode/languages.md.groovy

```groovy
# Languages

[! ["JavaScript", "Groovy", "JRuby", "BeanShell"].each { !]
* [= it =]
[! } !]
```

*BeanShell Template*: examples/local-mode/languages.md.bsh

```java
[! String[] languages = new String[] { "JavaScript", "Groovy", "JRuby", "BeanShell" } !]
# Languages

[! for (String language : languages) { !]
* [= language =]
[! } !]
```

*JRuby Template*: examples/local-mode/languages.md.rb

```ruby
# Languages

[! ["JavaScript", "Groovy", "JRuby", "BeanShell"].each do |language| !]
* [= language =]
[! end !]
```

## Trimming Spaces

JSSP deletes spaces around statement patterns (`[! !]` and `@! !@`) automatically, while it leaves spaces around expression patterns (`[= =]` and `@= =@`). If you want leave spaces, add command-line option `--trim=false`.

```sh
docker run --rm -v $PWD:/jssp redraiment/jssp \
  --trim=false \
  examples/local-mode/languages.md.groovy
```

*Output*

```markdown
# Languages


* JavaScript

* Groovy

* JRuby

* BeanShell

```

## Context Data File

Command-line option `-f/--context-file` specifies context data file. JSSP load context data file and use it as context data. Context data file can be JSON file (`*.json`) only.

```sh
docker run --rm -v $PWD:/jssp redraiment/jssp \
  -f examples/context-data/data.json \
  examples/context-data/languages.md.rb
```

*Template*: examples/context-data/languages.md.rb

```ruby
# Languages

[! languages.each do |language| !]
* [= language =]
[! end !]
```

*Context Data File*: examples/context-data/data.json

```json
{
    "languages": [
        "JavaScript",
        "Groovy",
        "JRuby",
        "BeanShell"
    ]
}
```

*Output*:

```markdown
# Languages

* JavaScript
* Groovy
* JRuby
* BeanShell
```

## Context Data String

Command-line option `-c/--context-string JSON` enables you to specify JSON format context data in command-line. 

```sh
docker run --rm -v $PWD:/jssp redraiment/jssp \
  -c '{"languages": ["JavaScript", "Groovy", "JRuby", "BeanShell"]}' \
  examples/context-data/languages.md.rb
```

*Output*:

```markdown
# Languages

* JavaScript
* Groovy
* JRuby
* BeanShell
```

## Embedded Pattern

You can change embedded pattern to another.

```sh
docker run --rm -v $PWD:/jssp redraiment/jssp \
  --executing-statement='<!--% %-->' \
  --executing-expression='<!--= =-->' \
  examples/embedded-patterns/languages.html.groovy
```

*Template*: examples/embedded-patterns/languages.html.groovy

```html
<h1>Languages</h1>
<ul>
  <!--% ["JavaScript", "Groovy", "JRuby", "BeanShell"].each { %-->
  <li><!--= it =--></li>
  <!--% } %-->
<ul>
```

*Output*:

```html
<h1>Languages</h1>
<ul>
  <li>JavaScript</li>
  <li>Groovy</li>
  <li>JRuby</li>
  <li>BeanShell</li>
<ul>
```

## Nested Include

There are two phases during render a template:

1. Expanding phase: render repeatedly with expanding patterns, to compute another template which will in turn render, until no more patterns can be found.
2. Executing phase: render with executing patterns once.

The `include` and `includeOnce` functions are read and return the specified file. The argument is a relative path of current working directory.

```sh
docker run --rm -v $PWD:/jssp redraiment/jssp \
  examples/nested-include/index.css.groovy
```

*Template*: examples/nested-include/index.css.groovy

```groovy
@= includeOnce('examples/nested-include/colors.groovy') =@
[! ['h1', 'h2', 'h3', 'h4', 'h5', 'h6'].each { tag -> !]
@= includeOnce('examples/nested-include/highlight.css.groovy') =@
[! } !]
```

*Template*: examples/nested-include/highlight.css.groovy

```groovy
@= includeOnce('examples/nested-include/colors.groovy') =@
[! colors.each { level, color -> !]
[= tag =].[= level =] {
  color: [= color =];
}
[! } !]
```

*Template*: examples/nested-include/colors.groovy

```groovy
[!
 colors = [
   normal: '#777777',
   primary: 'blue',
   success: 'green',
   warning: 'yellow',
   danger: 'red'
 ]
!]
```

*Output*:

```css


h1.normal {
  color: #777777;
}

h1.primary {
  color: blue;
}

...

h6.warning {
  color: yellow;
}

h6.danger {
  color: red;
}

```

## HTTP Server

**TODO**

## Escape

**TODO**

# Command Reference

## Usage

Outside mode: to produce a static document offline:

```sh
jssp [options] TEMPLATE-FILE
```

Server-side mode: to run the inner web server:

```sh
jssp [-s | --server] [options] WORK-DIRECTORY
```

## Options

* `-c, --context-string JSON`: context data string in JSON format, default `{}`.
* `-f, --context-file JSON-FILE`: context data JSON file name, it will override context data string above.
* `-t, --trim BOOLEAN`: switch to delete spaces around statement, default `true`.
* `--expanding-statement PATTERN`: expanding statement pattern, default `@! !@`.
* `--expanding-expression PATTERN`: expanding expression pattern, default `@= =@`.
* `--executing-statement PATTERN`: executing statement pattern, default `[! !]`.
* `--executing-expression PATTERN`: executing expression pattern, default `[= =]`.
* `-m, --expand-limit TIMES`: set the limit times for expanding phase, it's infinite if not provides.
* `-x, --emit-code`: emit expanded code.
* `-h, --help`: show help and exit.

## Built-in Functions

### include

The `include` function reads and returns the content of specified file.

It usually used with expanding expression pattern to include a shared fragment context into current template.

### includeOnce

The `includeOnce` function is identical to `include` except JSSP will check if the file has already been included, and if so, not include it again.

# Contributing

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement". Don't forget to give the project a star! Thanks again!

1. Fork the Project
1. Create your Feature Branch (git checkout -b feature/AmazingFeature)
1. Commit your Changes (git commit -m 'Add some AmazingFeature')
1. Push to the Branch (git push origin feature/AmazingFeature)
1. Open a Pull Request

# License

Distrubuted under the Apache v2 License. See `LICENSE` for more information.

# Contact

* Zhang, Zepeng - [@redraiment](https://twitter.com/redraiment) - [redraiment@gmail.com](mailto:redraiment@gmail.com)
