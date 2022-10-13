@= includeOnce('examples/nested-include/color.js') =@
[!
   var tags = ['h1', 'h2', 'p'];
   for (var index = 0; index < tags.length; index++) {
     var tag = tags[index];
!]
     @= includeOnce('examples/nested-include/common.css.js') =@
[! } !]
