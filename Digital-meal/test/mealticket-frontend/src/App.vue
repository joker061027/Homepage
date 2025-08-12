<template>
  <div id="app">
    <header>
      <h1>餐票系统</h1>
    </header>
    <main>
      <router-view />
    </main>
    <footer>
      <p>© 2023 餐票系统 Demo</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

header {
  margin-bottom: 30px;
}

header h1 {
  color: #42b983;
}

main {
  min-height: 60vh;
}

footer {
  margin-top: 50px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  color: #7f8c8d;
  font-size: 0.9em;
}

/* 全局按钮样式 */
button {
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #3aa876;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* 全局链接样式 */
a {
  color: #42b983;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}
</style>