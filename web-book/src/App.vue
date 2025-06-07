<template>
  <div id="app">
    <router-view/>
  </div>
</template>
<script>



export default {
  name: 'App',
  data(){
    return{
      user: JSON.parse(sessionStorage.getItem('CurUser')),
    }
  },
}
</script>

<style>
</style>
