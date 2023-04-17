const proxy = require('http-proxy-middleware');

module.exports = function(app) {
  app.use('/webvowl', proxy({
    target: 'http://service.tib.eu',
    changeOrigin: true,
    pathRewrite: {
      '^/webvowl': '/webvowl/'
    }
  }));
};
