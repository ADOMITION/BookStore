<template>
  <div>
    <top-bar/>
    <search-bar v-if="this.$route.path !== '/center'"/>
    <el-main style="background-color: #edede9">
      <router-view/>
    </el-main>
  </div>
</template>

<script>
import topBar from "./TopBar"
import searchBar from "./SearchBar"
export default {
  name: "ContainerPage",
  components:{ topBar,searchBar},
}
</script>

<style scoped>

</style>