const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(
        '/user-backend',
        createProxyMiddleware({
            target: 'http://user-backend:8080',
            changeOrigin: true,
            pathRewrite: { '^/user-backend': '' },
        })
    );

    app.use(
        '/device-backend',
        createProxyMiddleware({
            target: 'http://device-backend:8081',
            changeOrigin: true,
            pathRewrite: { '^/device-backend': '' },
        })
    );
};
