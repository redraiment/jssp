@= includeOnce('examples/nested-include/colors.groovy') =@
[! colors.each { level, color -> !]
[= tag =].[= level =] {
  color: [= color =];
}
[! } !]