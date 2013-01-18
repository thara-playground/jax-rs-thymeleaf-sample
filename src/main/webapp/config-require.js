var require = {};

var scripts = document.getElementsByTagName('script');
var currentScript = scripts[scripts.length - 1];
require['baseUrl'] = currentScript.src.replace(/\/[^/]*$/, '/');

var scripts = document.getElementsByTagName('script');
var currentScript = scripts[scripts.length - 1];
var urlArgs = (currentScript.src.match(/(?:\?)(.+)$/) || [])[1];
if (urlArgs) {
  require['urlArgs'] = urlArgs;
}

//require['paths'] = {
//  jqueryui : 'require-js-modules/jquery-ui-1.8.6'
//};

(function() {
	  var global = this;
	  require['deps'] = [];
	  require['deps'].push('common/javaScript-1.6');
	  require['deps'].push('common/require-ie-patch');
})();