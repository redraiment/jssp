[!
 var backgroundColor = 'lavender';
 var foregroundColor = 'blue';
 var fontSize = 12;

 var em = function(size) {
     if (arguments.length === 0) {
         size = 1.0;
     }
     return Math.floor(fontSize * size) + 'px';
 };
!]

body {
    background-color: [= backgroundColor =];
    color: [= foregroundColor =];
    font-size: [= em() =];
}

dt {
    font-size: [= em(1.5) =];
}
