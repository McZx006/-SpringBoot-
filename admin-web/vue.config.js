module.exports = {
  publicPath: process.env.VUE_APP_PUBLIC_PATH || '/',
  devServer: {
    historyApiFallback: {
      rewrites: [
        { from: /^\/api\/.*$/, to: (context) => context.parsedUrl.pathname },
        { from: /./, to: '/index.html' }
      ]
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/upload': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
