(function () {
  var windowLoaded = false;
  $(window).bind('load',function(){
    windowLoaded = true;
  });
  require.ready(function() {
    if ($.readyWait !== 0 && windowLoaded) {
      setTimeout(function() {
        $.ready();
      });
    }
  });
})();